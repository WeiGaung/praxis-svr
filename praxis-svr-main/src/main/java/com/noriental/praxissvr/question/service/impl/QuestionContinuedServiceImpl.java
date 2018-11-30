package com.noriental.praxissvr.question.service.impl;

import com.alibaba.dubbo.common.json.JSON;
import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.hyd.ssdb.util.Str;
import com.noriental.adminsvr.bean.knowledge.Module;
import com.noriental.adminsvr.bean.knowledge.TopicWithParent;
import com.noriental.adminsvr.bean.knowledge.Unit;
import com.noriental.adminsvr.request.RequestEntity;
import com.noriental.adminsvr.response.ResponseEntity;
import com.noriental.adminsvr.service.knowledge.TopicService;
import com.noriental.extresources.request.ConvertMp3Request;
import com.noriental.extresources.service.QiniuVoiceService;
import com.noriental.log.TraceKeyHolder;
import com.noriental.praxissvr.answer.util.RedisKeyUtil;
import com.noriental.praxissvr.common.QuestionState;
import com.noriental.praxissvr.common.QuestionTypeEnum;
import com.noriental.praxissvr.question.bean.BaseWordTranslation;
import com.noriental.praxissvr.question.bean.*;
import com.noriental.praxissvr.question.bean.html.*;
import com.noriental.praxissvr.question.bean.html.Question;
import com.noriental.praxissvr.question.bean.queueBean.QuestionQuality;
import com.noriental.praxissvr.question.dao.QuestionTypeDao;
import com.noriental.praxissvr.question.mapper.*;
import com.noriental.praxissvr.question.request.*;
import com.noriental.praxissvr.question.response.*;
import com.noriental.praxissvr.question.service.QuestionContinuedService;
import com.noriental.praxissvr.question.service.QuestionTypeService;
import com.noriental.praxissvr.question.utils.*;
import com.noriental.publicsvr.bean.SeqNextIdListReq;
import com.noriental.publicsvr.service.SequenceService;
import com.noriental.resourcesvr.common.request.IdRequest;
import com.noriental.resourcesvr.customlist.service.CustomListService;
import com.noriental.resourcesvr.customlist.vo.CustomListVo;
import com.noriental.solr.bean.doc.LqResourceDocument;
import com.noriental.solr.bean.doc.QuestionDocument;
import com.noriental.solr.common.msg.SolrIndexReqMsg;
import com.noriental.solr.common.utils.UUIDUtil;
import com.noriental.utils.entity.EntityUtils;
import com.noriental.utils.json.JsonUtil;
import com.noriental.utils.redis.RedisUtil;
import com.noriental.validate.bean.CommonResponse;
import com.noriental.validate.exception.BizLayerException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.beans.IntrospectionException;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.noriental.praxissvr.exception.PraxisErrorCode.*;

/**
 * Created by hushuang on 2017/3/6.
 * 题目操作的服务
 * 1.题目透传
 * 2.根据题目ID，父ID，子题的序号，获取填空题的题目ID，父ID，第几个空格：和对应的答案
 */
@Service("questionContinuedService")
public class QuestionContinuedServiceImpl implements QuestionContinuedService {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    @Resource
    private QuestionTypeDao questionTypeDao;
    @Resource
    private SequenceService sequenceService;
    @Resource
    private EntityQuestionMapper entityQuestionMapper;
    @Resource
    private TopicService topicService;
    /*  @Resource
      private QuestionGroupMapper questionGroupMapper;*/
    @Resource
    private LinkQuestionTopicMapper linkQuestionTopicMapper;
    @Resource
    private LinkQuestionChapterMapper linkQuestionChapterMapper;
    @Resource
    private RabbitTemplate solrUploadQuestionRabbitTemplate;
    @Autowired
    private RedisUtil redisUtil;
    @Resource
    private QiniuVoiceService qiniuVoiceService;
    @Resource
    private TeacherChapterMapper teacherChapterMapper;
    @Resource
    private BaseWordTranslationMapper baseWordTranslationMapper;
    @Resource
    private QuestionTypeService questionTypeService;
    @Resource
    private LinkCustomQuestionResourceMapper linkCustomQuestionResourceMapper;
    @Resource
    private LinkExerciseQuestionMapper linkExerciseQuestionMapper;
    @Resource
    private CustomListService customListService;
    @Resource
    private AuditsedSchoolMapper auditsedSchoolMapper;
    @Resource
    private RabbitTemplate createQuestionRecommendTemplate;

    /**
     * 透传题目接口
     *
     * @param req
     * @return
     * @throws BizLayerException
     */
    @Override
    public CommonResult createContinuedQuestion(ContinuedRequest req) throws BizLayerException {

        /**打印日志信息**/
        logger.info("【题目透传服务开始】入参为-->" + req);
        Continued continued;
        try {
            continued = JsonUtils.fromJson(req.toString(), Continued.class);
        } catch (IOException e) {
            logger.error("json转换失败{}", e.getMessage());
            throw new BizLayerException(e, JSON_CONVERT_FAIL);
        }

        QuestionContinuedRequest request;
        try {
            request = JsonUtils.fromJson(continued.getObject().toString(), QuestionContinuedRequest.class);
        } catch (IOException e) {
            logger.error("json转换失败{}", e.getMessage());
            throw new BizLayerException(e, JSON_CONVERT_FAIL);
        }
        if(StringUtils.isBlank(request.getHtml())){
            throw new BizLayerException("",QUESTION_CONTENT_NULL_EXCEPTION);
        }
        /**
         * 题目类型匹配验证,验证题型ID与题型枚举所对应的题型名称一致
         */
        int typeId = request.getTypeId();
        QuestionTypeEnum typeEnum = QuestionTypeEnum.getQuestionTypeByTypeId(typeId);
        if (!typeEnum.getSolrType().equals(request.getTypeName())) {
            throw new BizLayerException("", ANSWER_RECORD_QUES_TYPE_WRONG);
        }
        if (StringUtils.isEmpty(request.getSource())) {
            throw new BizLayerException("", QUESTION_SOURCE);
        }
        //新增透传来源判断
        if(!request.getSource().equals("content_group")&&!request.getSource().equals("CMS套卷上传")&&!request.getSource().equals("教师空间上传题集")){
            throw new BizLayerException("", QUESTION_SOURCE_ERROR);
        }
        /**主题章节关联处理**/
        List<Long> topicIds = request.getTopicIds();
        long chapterId = request.getChapterId();
        if (CollectionUtils.isEmpty(topicIds)) {
            throw new BizLayerException("知识点不能为空", ANSWER_PARAMETER_ILL);
        }
        if (CollectionUtils.isEmpty(topicIds) && chapterId == 0) {
            throw new BizLayerException("知识点和章节不能同时为空", ANSWER_PARAMETER_ILL);
        }
        //知识点数量不能超过10个
        if (topicIds != null && topicIds.size() > 10) {
            throw new BizLayerException("", TOPIC_COUNT);
        }

        if (continued.getReqId() != null) {
            request.setReqId(continued.getReqId() + "");
        } else {
            request.setReqId(UUIDUtil.getUUID());
        }

        /**参数构建**/
        int is_single = 1;//是否是单题
        String html = request.getHtml();//题目信息

        //验证乱码问题
        if(!StringUtils.isEmpty(QuestionServiceUtil.getSubUtilSimple(html,QuestionServiceUtil.htmlUnicodeRegex))){
            logger.error("透传题目存在乱码问题");
            throw new BizLayerException("", QUESTION_FORMAT_HTML_ERROR);
        }

        //验证style白名单
        if(QuestionServiceUtil.checkWhiteRuleKey(QuestionServiceUtil.getSubUtil(html,QuestionServiceUtil.htmlStyleRegex),QuestionServiceUtil.whiteRuleKeyCompare,QuestionServiceUtil.whiteRuleAllCompare )){
            throw new BizLayerException("", QUESTION_FORMAT_HTML_ERROR);
        }

        int length = 0;
        if (!JsonUtils.is_key(html, "questions")) { //单题
            is_single = 1;
        } else {
            length = JsonUtils.getQuestionsLength(html, "questions");
            if (length > 0) {
                is_single = 0;
            } else {
                is_single = 1;
            }
        }


        //批量获取题目主键ID
        List<Long> questionIds = getQuestionIds(1 + length);
        logger.info("\n 主键个数为:{},获取主键为:{}", questionIds.size(), questionIds);
        //==============================================================================
        EntityQuestion question = new EntityQuestion();
        question.setId(questionIds.get(0));//数据库主键题目ID
        question.setSubjectId(request.getSubjectId());//学科ID
        question.setUploadId(request.getUploadId());//上传人ID
        question.setDifficulty(request.getLevel());//难易程度
        String state = request.getState();
        if (53 == request.getTypeId()) { //如果题型为拍照题
            question.setState(QuestionState.BANNED);
        } else {
            if (StringUtils.isNotEmpty(state)) {
                question.setState(QuestionState.valueOf(state));
            } else {
                question.setState(QuestionState.PREVIEWED);
            }
        }

        question.setNewFormat(1);//新题标志
        question.setUploadSrc(request.getUploadSrc());//区分上传人来源
        question.setQuestionTypeId(request.getTypeId()); //题目类型ID
        question.setUploadTime(new Date()); //上传时间
        question.setUpdateTime(new Date());//更新时间
        question.setVisible(1); //是否可见
        question.setIsFinishedProduct(0);

        question.setScore(0F);
        question.setQuestionType(request.getTypeName());//题目类型
        question.setSource(request.getSource());//习题来源
        question.setParentQuestionId(0L);//子题的大题题目ID
        question.setOrgId(request.getOrgId());//题目创建机构id
        question.setOrgType(request.getOrgType());


        try {
            String json = ParseHtmlUtil.html2json(html, request.getTypeId() + "", question.getId(),questionTypeDao);
            question.setJsonData(json);
        } catch (IOException e) {
            logger.error("HTML转JSON异常:{}", e);
            e.printStackTrace();
        }

        Object o = JsonUtil.readValue(html, Object.class);
        Map<String, Object> wrapper = new HashMap<>();
        wrapper.put("question_id", question.getId());
        wrapper.put("content", o);
        question.setHtmlData(JsonUtil.obj2Json(wrapper));
        question.setIsSingle(is_single); //单题

        //==============================================================================
        List<QuestionQuality> questionQualityList=new ArrayList<>();
        /**
         * 单题处理
         */
        if (is_single == 1) { //单题
            try {
                logger.info("入参html数据为：" + html);
                SingleQuestion singleQuestion = JsonUtils.fromJson(html, SingleQuestion.class);
                if (0 == request.getTypeId() || 1 == request.getTypeId()) {
                    question.setRightOption(singleQuestion.getAnswer().toString());//选择题正确答案
                    question.setCountOptions(singleQuestion.getOptions().size());//选项数量
                    question.setQuestionType("选择题");
                }
                if (singleQuestion.getAnswer() != null) {
                    question.setAnswerNum(singleQuestion.getAnswer().size());//答案个数
                } else {
                    question.setAnswerNum(0);//答案个数
                }
                //是否支持智能批改
                if(singleQuestion.getAnswer()!=null && question.getQuestionTypeId()!=null){
                    question.setIntelligent(QuestionServiceUtil.getIntelligent(singleQuestion.getAnswer(),
                            question.getQuestionTypeId(), question.getSubjectId()));
                }else {
                    question.setIntelligent(0); //是否支持智能批改
                }

                if (singleQuestion != null && singleQuestion.getMap() != null) {
                    if (question.getQuestionTypeId() == 51) { //作图题添加底图数据
                    /*
                        获取底图数据并将底图数据进行序列化存储到数据库中
                     */
                        NewMap jsonMapImgae = singleQuestion.getMap();
                        try {
                            String json = JSON.json(jsonMapImgae);
                            question.setJsonMap(json);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                //给recommend_svr发送消息
                QuestionQuality questionQuality=new QuestionQuality();
                questionQuality.setUploadId(request.getUploadId());
                questionQuality.setDifficulty(question.getDifficulty());
                questionQuality.setQuestionId(question.getId());
                questionQualityList.add(questionQuality);
            } catch (IOException e) {
                logger.error("html单题转换异常{}", e.getMessage());
                throw new BizLayerException(e, JSON_CONVERT_FAIL);
            }
        } else {
            question.setAnswerNum(0);
            question.setIntelligent(0);
        }
        if(QuestionServiceUtil.isWhiteSchool(auditsedSchoolMapper,request.getOrgId(),redisUtil)){
            question.setState(QuestionState.ENABLED);
        }
        //CMS后台套卷上传按照旧
        if(request.getSource().equals("CMS套卷上传")){
            question.setState(QuestionState.PREVIEWED);
        }

        logger.info("\n 创建题目mysql入参为:{}", question);
        int i = entityQuestionMapper.createQuestion(question);
        if (i <= 0) {
            throw new BizLayerException("", QUESTION_CREATE_FAIL);
        }

        /**
         * 复合题处理
         */
        if (is_single == 0) { //复合题
            logger.info("入参html数据为：" + html);
            ComplexQuestion complexQuestion;
            try {
                complexQuestion = JsonUtils.fromJson(html, ComplexQuestion.class);
            } catch (IOException e) {
                logger.error("html转复合题异常{}", e.getMessage());
                throw new BizLayerException(e, JSON_CONVERT_FAIL);
            }
            List<Question> questions = complexQuestion.getQuestions();
            List<EntityQuestion> subQuestionList = new ArrayList<>();
            //批量插入子题题目信息
            for (int a = 1; a < questionIds.size(); a++) {
                EntityQuestion subEntity = new EntityQuestion();//子题模板
                Question subQuestion = questions.get(a - 1);//子题
                subEntity.setId(questionIds.get(a));
                if (subQuestion.getType().getId() == 0 || subQuestion.getType().getId() == 1) {
                    subEntity.setRightOption(subQuestion.getAnswer().toString());//正确答案
                    subEntity.setCountOptions(subQuestion.getOptions().size());//选项数量
                }
                if (subQuestion.getAnswer() != null) {
                    subEntity.setAnswerNum(subQuestion.getAnswer().size());//正确答案数
                } else {
                    subEntity.setAnswerNum(0);//正确答案数
                }


                subEntity.setQuestionTypeId(subQuestion.getType().getId());//题型ID
                subEntity.setQuestionType(subQuestion.getType().getName());//题型

                if(StringUtils.isEmpty(subQuestion.getDifficulty())){
                    subEntity.setDifficulty(1);
                }else{
                    subEntity.setDifficulty(Integer.valueOf(subQuestion.getDifficulty()));
                }
                //subEntity.setDifficulty(Integer.parseInt(subQuestionList.get(j).getDifficulty()));//难度
                subEntity.setIsSingle(1);//单题
                subEntity.setParentQuestionId(questionIds.get(0));//大题ID
                if (StringUtils.isNotEmpty(state)) {
                    //当是不需要审核的听口题时，直接让子题状态和父题相同
                    if(request.getTypeId()>=54&&request.getTypeId()<=60){
                        subEntity.setState(QuestionState.valueOf(state));
                    }else{
                        //待审核
                        subEntity.setState(QuestionState.PREVIEWED);
                    }
                } else {
                    //待审核
                    subEntity.setState(QuestionState.PREVIEWED);
                }
                if(QuestionServiceUtil.isWhiteSchool(auditsedSchoolMapper,request.getOrgId(),redisUtil)){
                    subEntity.setState(QuestionState.ENABLED);
                }
                //CMS后台套卷上传按照旧
                if(request.getSource().equals("CMS套卷上传")){
                    question.setState(QuestionState.PREVIEWED);
                }

                subEntity.setSubjectId(request.getSubjectId());//学科ID
                subEntity.setUploadId(request.getUploadId());//上传人ID
                subEntity.setNewFormat(1);//是否是新题
                subEntity.setOrgId(request.getOrgId());
                subEntity.setOrgType(request.getOrgType());
                subEntity.setUploadTime(new Date());
                subEntity.setUpdateTime(new Date());
                subEntity.setIsFinishedProduct(0);
                subEntity.setVisible(1);
                subEntity.setUploadSrc(request.getUploadSrc());


                // 是否支持智能批改
                if(subQuestion.getAnswer()!=null){
                    subEntity.setIntelligent(QuestionServiceUtil.getIntelligent(subQuestion.getAnswer(),
                            subQuestion.getType().getId(),subEntity.getSubjectId()));
                }else{
                    subEntity.setIntelligent(0);
                }
                logger.info("获取的子题智批标签为：{}",subQuestion.getIntelligent());
                subEntity.setScore(0F);
                /**
                 * 如果为作图题
                 * 将底图信息取出来json化插入进数据库
                 */
                if (subQuestion.getType().getId() == 51) {
                    NewMap jsonMapImgae = subQuestion.getMap();
                    try {
                        String json = JSON.json(jsonMapImgae);
                        subEntity.setJsonMap(json);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                //给recommend_svr发送消息
                QuestionQuality questionQuality=new QuestionQuality();
                questionQuality.setUploadId(request.getUploadId());
                questionQuality.setDifficulty(subEntity.getDifficulty());
                questionQuality.setQuestionId(subEntity.getId());
                questionQualityList.add(questionQuality);

                subQuestionList.add(subEntity);
            }
            int j = entityQuestionMapper.batchInsertQuestion(subQuestionList);
            if (j <= 0) {//批量插入失败
                throw new BizLayerException("", BATCH_INSERT_FAIL);
            }
        }

        if (CollectionUtils.isNotEmpty(topicIds)) {
            //用户主题关联批量
            List<Map<String, Object>> listTopics = new ArrayList<>();
            for (Long topicId : topicIds) {
                Map<String, Object> mapTopic = new HashMap<>();
                mapTopic.put("questionId", question.getId());
                mapTopic.put("topicId", topicId);
                listTopics.add(mapTopic);
            }
            //知识点关联批量插入
            int n = linkQuestionTopicMapper.batchQuestionTopicLink(listTopics);
            if (n <= 0) {
                logger.error("主题关联批量插入失败");
                throw new BizLayerException("知识点", BATCH_INSERT_FAIL);
            }
        }
        //章节插入
        if (request.getChapterId() != 0) {
            linkQuestionChapterMapper.createLinkQuestionChapter(question.getId(), request.getChapterId());
        }

        /**发送Solr**/
        SolrIndexReqMsg reqMsg;

        /**发送Solr**/
        if (is_single == 1) { //单题
            logger.info("====================MQ发送单题开始================");
            reqMsg = sendSolrSingleDoc2Mq(question, request.getChapterId(), request.getTopicIds(), request.getHtml(),
                    request.getReqId());

        } else { //复合题
            logger.info("====================MQ发送复合题开始================");
            reqMsg = solrComplexReqMsg(question, request.getHtml(), request.getChapterId(), request.getTopicIds(),
                    questionIds, request.getReqId());
        }
        logger.info("====================MQ发送结束================");
        logger.info("MQ发送数据为" + reqMsg.getBody().toString());
        solrUploadQuestionRabbitTemplate.convertAndSend(reqMsg);





        if (is_single == 1) { //单题
            logger.info("七牛回调单题开始");
            try {
                SingleQuestion singleQuestion = JsonUtils.fromJson(request.getHtml(), SingleQuestion.class);
                if (singleQuestion != null && singleQuestion.getAudio() != null && singleQuestion.getAudio()
                        .getUrl() != null && !"".equals(singleQuestion.getAudio().getUrl())) {

                        ConvertMp3Request convertMp3Request = new ConvertMp3Request();
                        convertMp3Request.setEntityId(question.getId() + "");
                        convertMp3Request.setAudioUrl(singleQuestion.getAudio().getUrl());
                        CommonResponse<String> response = qiniuVoiceService.convertMp3(convertMp3Request);
                        logger.info("七牛回调单题数据返回" + response.getData());

                }
            } catch (IOException e) {
                logger.debug("html内容为：" + request.getHtml());
                throw new BizLayerException(e, JSON_CONVERT_FAIL);
            }
        } else {  //复合题
            logger.info("七牛回调复合题开始");
            try {
                ComplexQuestion complexQuestion = JsonUtils.fromJson(request.getHtml(), ComplexQuestion.class);
                if (complexQuestion != null && complexQuestion.getAudio() != null && complexQuestion.getAudio()
                        .getUrl() != null && !"".equals(complexQuestion.getAudio().getUrl())) {

                        ConvertMp3Request convertMp3Request = new ConvertMp3Request();
                        convertMp3Request.setEntityId(question.getId() + "");
                        convertMp3Request.setAudioUrl(complexQuestion.getAudio().getUrl());
                        CommonResponse<String> convertMp3 = qiniuVoiceService.convertMp3(convertMp3Request);
                        logger.info("七牛回调复合题数据返回：" + convertMp3.getData());

                }
            } catch (IOException e) {
                logger.info("html内容为：" + request.getHtml());
                throw new BizLayerException(e, JSON_CONVERT_FAIL);
            }
        }
        //创建习题给统计服务发送相关数据
        logger.info("创建题目给统计服务发送的消息：{}",questionQualityList.toString());
        createQuestionRecommendTemplate.convertAndSend(JsonUtil.obj2Json(questionQualityList));

        CommonResult commonResult = new CommonResult();
        commonResult.setId(question.getId());
        return commonResult;
    }

    /**
     * 更新
     *
     * @param req
     * @return
     * @throws BizLayerException
     */
    @Override
    public CommonResult updateContinuedQuestion(ContinuedRequest req) throws BizLayerException {

        logger.info("透传更新题目开始入参为：" + req);

        UpdateContinue updateContinue;
        try {
            updateContinue = JsonUtils.fromJson(req.toString(), UpdateContinue.class);
        } catch (IOException e) {
            logger.error("json转换失败{}", e.getMessage());
            throw new BizLayerException(e, JSON_CONVERT_FAIL);
        }


        UpdateQuestionContinuedRequest request;

        try {
            request = JsonUtils.fromJson(updateContinue.getObject().toString(), UpdateQuestionContinuedRequest.class);
        } catch (IOException e) {
            logger.error("json转换失败{}", e.getMessage());
            throw new BizLayerException(e, JSON_CONVERT_FAIL);
        }
        if(StringUtils.isBlank(request.getHtml())){
            throw new BizLayerException("",QUESTION_CONTENT_NULL_EXCEPTION);
        }


        /**
         * 业务实现流程
         * 1.查询当前题目是否存在
         * 2.判断是否已经通过平台审核
         * 3.封装题目更新数据
         * 4.判断单题和复合题
         * if 单题
         *      直接更新数据库
         *      覆盖ssdb
         *      更新solr
         *  else 复合题
         *      删除复合题子题
         *      更新数据库复合题大题
         *      重新添加数据库复合题子题
         *      覆盖ssdb
         *      删除复合题子题solr
         *      更新solr复合题大题
         *      重新添加solr复合题子题
         */

        /*
            1.查询当前题目是否存在
         */
        EntityQuestion question = entityQuestionMapper.findQuestionById(request.getId());

        //题集查询
        List<LinkExerciseQuestion> exerciseQuestions = linkExerciseQuestionMapper.findLinkExerciseQuestionById
                (request.getId());
        //修改题目时，如果该题目属于一个题集，而且该题集被缓存了，需要在此处过期该缓存
        QuestionServiceUtil.delExerciseQuestionCache(exerciseQuestions,redisUtil);

        if (null == question) {
            throw new BizLayerException("", QUESTION_DOWN_FAIL);
        }

        /*
            2.封装题目更新的数据
         */
        EntityQuestion entityQuestion = new EntityQuestion();
        entityQuestion.setId(request.getId());
        entityQuestion.setSubjectId(request.getSubjectId());//学科ID
        entityQuestion.setUploadId(request.getUploadId());//上传人ID
        entityQuestion.setQuestionGroup(request.getGroup());//组ID
        entityQuestion.setDifficulty(request.getLevel());//难易程度

        if (request.getType() != null) {

            if ("53".equals(request.getType())) {   //如果为拍照提那么状态为BANNED
                if (request.getState().equals(QuestionState.DISABLED.name())) {
                    entityQuestion.setState(QuestionState.DISABLED);
                } else {
                    entityQuestion.setState(QuestionState.BANNED);
                }
            } else {
                if (StringUtils.isEmpty(request.getState())) {
                    entityQuestion.setState(QuestionState.PREVIEWED);
                } else {
                    entityQuestion.setState(QuestionState.valueOf(request.getState()));
                }

            }

            entityQuestion.setQuestionTypeId(Integer.parseInt(request.getType())); //题目类型ID
            if ("1".equals(request.getType())) {
                entityQuestion.setQuestionType("选择题");//题型
            } else {
                entityQuestion.setQuestionType(QuestionTypeEnum.getQuestionTypeByTypeId(Integer.parseInt(request
                        .getType())).getType());//题型
            }
        }


        entityQuestion.setNewFormat(1);//新题标志
        entityQuestion.setUploadSrc(request.getUploadSrc());//区分上传人来源

        //entityQuestion.setSource(request.getSource());//习题来源   强制为空，不让改来源
        entityQuestion.setUpdateTime(new Date());
        if (question.getUploadTime() != null) {
            entityQuestion.setUploadTime(question.getUploadTime());
        }
        entityQuestion.setVisible(1);

        //创建机构ID
        entityQuestion.setOrgId(request.getOrgId());
        entityQuestion.setOrgType(request.getOrgType());
        entityQuestion.setParentQuestionId(0L);


        try {
            String json = ParseHtmlUtil.html2json(request.getHtml(), request.getType(), entityQuestion.getId(),questionTypeDao);
            entityQuestion.setJsonData(json);
        } catch (IOException e) {
            logger.error("HTML转JSON异常:{}", e);
            e.printStackTrace();
        }

        Object o = JsonUtil.readValue(request.getHtml(), Object.class);
        Map<String, Object> wrapper = new HashMap<>();
        wrapper.put("question_id", question.getId());
        wrapper.put("content", o);
        entityQuestion.setHtmlData(JsonUtil.obj2Json(wrapper));


        /*
            3.判断是否为单题
         */
        int is_single = 1;
        String html = request.getHtml();
        try {
            int questionsLength = JsonUtils.getQuestionsLength(html, "questions");
            if (questionsLength > 0) {//复合题
                is_single = 0;
            }
        } catch (BizLayerException e) {
            throw new BizLayerException("", JSON_CONVERT_FAIL);
        }
        entityQuestion.setIsSingle(is_single);

        /**
         * 如果是单题
         * 更新数据库
         */
        if (1 == is_single) {
            SingleQuestion singleQuestion;
            try {
                singleQuestion = JsonUtils.fromJson(html, SingleQuestion.class);

                if (request.getType() != null) {
                    if ("1".equals(request.getType())) {//选择题
                        entityQuestion.setRightOption(singleQuestion.getAnswer().toString());//选择题正确选项
                        entityQuestion.setCountOptions(singleQuestion.getOptions().size());//选项数量
                    }
                }

                entityQuestion.setAnswerNum(singleQuestion.getAnswer().size());//答案个数

                //是否支持智能批改
                if(singleQuestion.getAnswer()!=null && entityQuestion.getQuestionTypeId()!=null){
                    entityQuestion.setIntelligent(QuestionServiceUtil.getIntelligent(singleQuestion.getAnswer(),
                            entityQuestion.getQuestionTypeId(),entityQuestion.getSubjectId()));
                }else {
                    entityQuestion.setIntelligent(0);
                }


                if (entityQuestion.getQuestionTypeId() == 51) {
                    if (singleQuestion != null && singleQuestion.getMap() != null) {

                        if (entityQuestion.getQuestionTypeId() == 51) { //作图题添加底图数据
                        /*
                            获取底图数据并将底图数据进行序列化存储到数据库中
                         */
                            NewMap jsonMapImgae = singleQuestion.getMap();
                            try {
                                String json = JSON.json(jsonMapImgae);
                                entityQuestion.setJsonMap(json);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                logger.error("html转换单题实体异常={}", e.getMessage());
            }
        }else{
            entityQuestion.setIntelligent(0);
        }
        //白名单特殊处理
        if(QuestionServiceUtil.isWhiteSchool(auditsedSchoolMapper,request.getOrgId(),redisUtil)){
            entityQuestion.setState(QuestionState.ENABLED);
        }
        int entityQuestionUpdate = entityQuestionMapper.updateQuestion(entityQuestion);
        if (entityQuestionUpdate <= 0) {  //更新失败
            logger.error("更新习题失败");
            throw new BizLayerException("", QUESTION_UPDATE_FAIL);
        }

        /**
         * 如果是复合题
         * 更新数据库操作
         */
        if (0 == is_single) {

            /**
             * 如果是复合题更新那么需要删除redis缓存
             * 智能批改题目进行从新获取
             */
            redisUtil.del(RedisKeyUtil.LEAF_QUESTION_PREFIX + request.getId());

            /*
                1.查询复合题下子题的Id
                2.批量删除solr下所有子题
                3.删除数据库复合题下面的所有子题
                4.更新复合题大题
                5.重新添加复合题下的子题
             */

            List<Long> subIdList = entityQuestionMapper.findQuestionSubjIdByParentId(request.getId());

            SolrUtils.fBatchDeleteIndex(subIdList, solrUploadQuestionRabbitTemplate);

            entityQuestionMapper.deleteQuestionByParentId(request.getId());

            entityQuestionMapper.updateQuestion(entityQuestion);


            ComplexQuestion complexQuestion;
            try {
                complexQuestion = JsonUtils.fromJson(html, ComplexQuestion.class);
            } catch (IOException e) {
                logger.error("html转复合题实体异常", e.getMessage());
                throw new BizLayerException(e, JSON_CONVERT_FAIL);
            }
            List<Question> subQuestionList = complexQuestion.getQuestions();

            List<EntityQuestion> entityQuestionList = new ArrayList<>();

            List<Long> longList = getQuestionIds(subQuestionList.size());

            for (int j = 0; j < subQuestionList.size(); j++) {
                EntityQuestion entityf = new EntityQuestion();
                entityf.setId(longList.get(j));
                if (subQuestionList.get(j).getType().getId() == 0 || subQuestionList.get(j).getType().getId() == 1) {
                    entityf.setRightOption(subQuestionList.get(j).getAnswer().toString());//正确答案
                    entityf.setCountOptions(subQuestionList.get(j).getOptions().size());//选项数量
                }
                entityf.setAnswerNum(subQuestionList.get(j).getAnswer().size());//正确答案数
                entityf.setQuestionTypeId(subQuestionList.get(j).getType().getId());//题型ID
                entityf.setQuestionType(subQuestionList.get(j).getType().getName());//题型
                if(StringUtils.isEmpty(subQuestionList.get(j).getDifficulty())){
                    entityf.setDifficulty(1);
                }else{
                    entityf.setDifficulty(Integer.valueOf(subQuestionList.get(j).getDifficulty()));
                }
                //entityf.setDifficulty(Integer.parseInt(subQuestionList.get(j).getDifficulty()));//难度
                entityf.setIsSingle(1);//单题
                entityf.setParentQuestionId(request.getId());//大题ID
                entityf.setState(QuestionState.PREVIEWED);//待审核
                entityf.setSubjectId(request.getSubjectId());//学科ID
                entityf.setUploadId(request.getUploadId());//上传人ID
                entityf.setNewFormat(1);//是否是新题
                entityf.setUploadTime(new Date());
                entityf.setUpdateTime(new Date());
                entityf.setVisible(1);


                if(subQuestionList.get(j).getAnswer()!=null){
                    entityf.setIntelligent(QuestionServiceUtil.getIntelligent(subQuestionList.get(j).getAnswer(),
                            subQuestionList.get(j).getType().getId(),entityf.getSubjectId()));
                }else{
                    entityf.setIntelligent(0);
                }

                entityf.setOrgId(request.getOrgId());
                entityf.setOrgType(request.getOrgType());
                entityf.setScore(0f);
                entityf.setUploadSrc(request.getUploadSrc());
                //白名单特殊处理
                if(QuestionServiceUtil.isWhiteSchool(auditsedSchoolMapper,request.getOrgId(),redisUtil)){
                    entityf.setState(QuestionState.ENABLED);
                }

                /**
                * 如果为作图题
                * 将底图信息取出来json化插入进数据库
                */
                if (subQuestionList.get(j).getType().getId() == 51) {
                    NewMap jsonMapImgae = subQuestionList.get(j).getMap();
                    try {
                        String json = JSON.json(jsonMapImgae);
                        entityf.setJsonMap(json);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                entityQuestionList.add(entityf);



            }
            int i = entityQuestionMapper.batchInsertQuestion(entityQuestionList);
            if (i <= 0) {//批量插入失败
                logger.error("batchInsertQuestion={}", "批量插入失败");
                throw new BizLayerException("", BATCH_INSERT_FAIL);
            }
        }


        /**
         * 更新主题和章节信息
         * 1.删除原有主题和章节
         * 2.添加新的主题章节关系依赖
         *
         * if 章节不为null
         *      1.删除原有章节关联
         *      2.插入新的关联
         * if 主题不为null
         *      1.删除原有主题关联
         *      2.插入新的主题关联
         *
         */


        if (request.getChapterId() != null && request.getChapterId() != 0) {
            linkQuestionChapterMapper.deleteLinkQuestionChapterById(request.getId());

            int questionChapter = linkQuestionChapterMapper.createLinkQuestionChapter(request.getId(), request
                    .getChapterId());
            if (questionChapter <= 0) {
                logger.error("createLinkQuestionChapter={}", "章节关联插入失败");
                throw new BizLayerException("章节关联插入失败", BATCH_INSERT_FAIL);
            }
        }

        if (request.getTopicIds() != null && request.getTopicIds().size() > 0) {

            linkQuestionTopicMapper.deleteQuestionTopicLinkById(request.getId());

            List<Long> topicIds = request.getTopicIds();
            /**
             * 更新习题知识点数最多为10个
             */
            if (topicIds != null && topicIds.size() > 10) {
                throw new BizLayerException("", TOPIC_COUNT);
            }


            List<Map<String, Object>> listTopics = new ArrayList<>();
            if (null != topicIds && topicIds.size() > 0) {
                for (Long topicId : topicIds) {
                    Map<String, Object> mapTopic = new HashMap<>();
                    mapTopic.put("questionId", entityQuestion.getId());
                    mapTopic.put("topicId", topicId);
                    listTopics.add(mapTopic);
                }
                /**
                 * 主题关联批量插入
                 */
                int iTopic = linkQuestionTopicMapper.batchQuestionTopicLink(listTopics);
                if (iTopic <= 0) {
                    logger.error("linkQuestionTopic={}", "主题关联批量插入失败");
                    throw new BizLayerException("主题关联批量插入失败", BATCH_INSERT_FAIL);
                }
            }
        }

        /**
         * 更新solr
         */
        updateQuestionSolrIndex(entityQuestion, html, request.getChapterId(), request.getTopicIds(), null);

        //更新自定义目录 根据system_id和question_id查询link_custom_question_resource表数据
        CustomQuestionResource questionResource = linkCustomQuestionResourceMapper
                .queryCustomQuestionResourceByQuesIdAndSysId(question.getId(), question.getUploadId());
        if (null != questionResource) {
           QuestionServiceUtil.sendLinkQuestionIndex(entityQuestion,questionResource.getCustomListId(),questionResource.getId(),0,customListService,solrUploadQuestionRabbitTemplate);

        }
        /**
         * 更新音频七牛回调
         */
        if (is_single == 1) {
            SingleQuestion singleQuestion;
            try {
                singleQuestion = JsonUtils.fromJson(request.getHtml(), SingleQuestion.class);
            } catch (IOException e) {
                logger.info("html内容为：" + request.getHtml());
                throw new BizLayerException("", JSON_CONVERT_FAIL);
            }
            if (singleQuestion != null && singleQuestion.getAudio() != null && singleQuestion.getAudio().getUrl() !=
                    null && !"".equals(singleQuestion.getAudio().getUrl())) {
                    logger.info("----------------------------更新习题，七牛回调开始------------------------------------");
                    ConvertMp3Request convertMp3Request = new ConvertMp3Request();
                    convertMp3Request.setEntityId(entityQuestion.getId() + "");
                    convertMp3Request.setAudioUrl(singleQuestion.getAudio().getUrl());

                    try {
                        CommonResponse<String> convertMp3 = qiniuVoiceService.convertMp3(convertMp3Request);
                        logger.info("----------------------更新习题，七牛回调结束返回convertMp3+" + convertMp3.getData() +
                                "+------------------");
                    } catch (BizLayerException e) {
                        logger.info("音频回调异常参数", "questionId:" + convertMp3Request.getEntityId() + ",Url=" +
                                convertMp3Request.getAudioUrl());
                        throw new BizLayerException("", QIQIU_FAIL);
                    }
            }

        } else {  //复合题
            ComplexQuestion complexQuestion;
            try {
                complexQuestion = JsonUtils.fromJson(request.getHtml(), ComplexQuestion.class);
            } catch (IOException e) {
                logger.info("html内容为：" + request.getHtml());
                throw new BizLayerException(e, JSON_CONVERT_FAIL);
            }
            if (complexQuestion != null && complexQuestion.getAudio()!=null && complexQuestion.getAudio().getUrl() != null && !"".equals(complexQuestion
                    .getAudio().getUrl())) {

                    logger.info("----------------------------更新习题，七牛回调开始------------------------------------");
                    ConvertMp3Request convertMp3Request = new ConvertMp3Request();
                    convertMp3Request.setEntityId(entityQuestion.getId() + "");
                    convertMp3Request.setAudioUrl(complexQuestion.getAudio().getUrl());

                    try {
                        CommonResponse<String> convertMp3 = qiniuVoiceService.convertMp3(convertMp3Request);
                        logger.info("--------------更新习题，七牛回调结束返回convertMp3+" + convertMp3.getData() + "------------------");
                    } catch (BizLayerException e) {
                        logger.info("音频回调异常参数：{}", "questionId:" + convertMp3Request.getEntityId() + ",Url=" +
                                convertMp3Request.getAudioUrl());
                        throw new BizLayerException("", QIQIU_FAIL);
                    }

            }
        }
        CommonResult commonResult = new CommonResult();
        commonResult.setId(request.getId());
        return commonResult;
    }

    @Override
    public void batchCloseQuestion(StateQuestionRequest request) throws BizLayerException {

        logger.info("批量关闭题目start 入参：" + request.getRequestIds().toString());
        /**
         * 1.批量更新数据库state
         * 2.批量更新solr state
         */
        //1.更新数据库
        int i = entityQuestionMapper.batchUpdateQuestionStatusDISABLE(request.getRequestIds());
        if (i <= 0) {
            throw new BizLayerException("", BATCH_UPDATE_FAIL);
        }
        //2.更新solr
        List<Long> requestIds = request.getRequestIds();
        for (Long requestId : requestIds) {
            Map<String, Object> mapBody = new HashMap<>();
            mapBody.put("id", requestId);
            mapBody.put("state", "DISABLE");
            try {
                mapBody.put(Constants._DOC_CLASS_NAME, QuestionDocument.class.getName());
                SolrIndexReqMsg msg = new SolrIndexReqMsg(mapBody);
                msg.setRequestId(UUIDUtil.getUUID());
                solrUploadQuestionRabbitTemplate.convertAndSend(msg);
            } catch (Exception e) {
                logger.error("关闭习题更新solr失败：{}", e.getMessage());
                throw new BizLayerException("", UPDATE_SOLR_FAIL);
            }
        }
    }

    @Override
    public void batchStartQuestion(StateQuestionRequest request) throws BizLayerException {
        logger.info("批量开启题目start 入参：" + request.getRequestIds().toString());
        /**
         * 1.批量更新数据库state
         * 2.批量更新solr state
         */
        //1.更新数据库
        int i = entityQuestionMapper.batchUpdateQuestionStatusABLE(request.getRequestIds());
        if (i <= 0) {
            throw new BizLayerException("", BATCH_UPDATE_FAIL);
        }
        //2.更新solr
        List<Long> requestIds = request.getRequestIds();
        for (Long requestId : requestIds) {
            Map<String, Object> mapBody = new HashMap<>();
            //mapBody.put("requestId", TraceKeyHolder.getTraceKey());
            //logger.info("requestId=========="+TraceKeyHolder.getTraceKey());
            mapBody.put("id", requestId);
            mapBody.put("state", "ENABLED");
            try {
                mapBody.put(Constants._DOC_CLASS_NAME, QuestionDocument.class.getName());
                SolrIndexReqMsg msg = new SolrIndexReqMsg(mapBody);
                msg.setRequestId(UUIDUtil.getUUID());
                solrUploadQuestionRabbitTemplate.convertAndSend(msg);
            } catch (Exception e) {
                logger.error("关闭习题更新solr失败：{}", e.getMessage());
                throw new BizLayerException("", UPDATE_SOLR_FAIL);
            }
        }
    }

    @Override
    public FindBlankAndAnswerResponse findBlankAndAnswer(FindBlankAndAnswerRequst requst) throws BizLayerException {
        logger.info("通过题目ID查询题目接口sart…… 入参为：" + requst.getBlankAndAnswers().toString());
        /**
         * 1.查询SSDB中的题目信息
         * 2.通过入参判断单题/复合题
         * 3.解析JSON转换实体
         * 4.通过入参确定子题的题目顺序
         * 5.封装对象并返回
         */
        FindBlankAndAnswerResponse response = new FindBlankAndAnswerResponse();
        /**
         * 批量返回数据
         */
        List<BlankAndAnswerResult> blankAndAnswerResults = new ArrayList<>();
        /**
         * 批量请求数据
         */
        List<BlankAndAnswer> andAnswers = requst.getBlankAndAnswers();


        List<Long> ids=new ArrayList();
        for (BlankAndAnswer andAnswer : andAnswers){
            if(andAnswer.getParentQuestionId()==0){
                ids.add(andAnswer.getQuestionId());
            }else{
                ids.add(andAnswer.getParentQuestionId());
            }
        }
        List<EntityQuestion> entityQuestions = entityQuestionMapper.batchQueryQuestionsByIds(ids);
        for (BlankAndAnswer andAnswer : andAnswers){
            for(EntityQuestion entityQuestion:entityQuestions){
                //单题情况
                if(andAnswer.getParentQuestionId()==0){
                    if(andAnswer.getQuestionId().equals(entityQuestion.getId())){
                        String content = JsonUtils.getSsdbContent(entityQuestion.getHtmlData());
                        BlankAndAnswerResult blankAndAnswerResult = new BlankAndAnswerResult();
                        SingleQuestion question = null;
                        try {
                            question = JsonUtils.fromJson(content, SingleQuestion.class);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        blankAndAnswerResult.setQuestionId(andAnswer.getQuestionId());
                        blankAndAnswerResult.setParentQuestionId(andAnswer.getParentQuestionId());
                        /**
                         * 获取的答案
                         */
                        List<Object> answer = question.getAnswer();
                        /**
                         * 返回的答案
                         */
                        List<AnswerResult> answerResults = new ArrayList<>();
                        for (int i = 0; i < answer.size(); i++) {
                            AnswerResult answerResult = new AnswerResult();
                            answerResult.setIndex(i);
                            //定义正则去除标签
                            String regexstr = "<([^>]*)>";
                            Pattern p_space = Pattern.compile(regexstr, Pattern.CASE_INSENSITIVE);
                            Matcher m_space = p_space.matcher(answer.get(i) + "");
                            String s1 = m_space.replaceAll("");
                            answerResult.setAnswer(StringEscapeUtils.unescapeHtml4(s1.trim()));
                            answerResults.add(answerResult);
                        }

                        blankAndAnswerResult.setAnswerResults(answerResults);
                        blankAndAnswerResults.add(blankAndAnswerResult);
                    }
                }else{
                    if(entityQuestion.getId().equals(andAnswer.getParentQuestionId())){
                        String content = JsonUtils.getSsdbContent(entityQuestion.getHtmlData());

                        BlankAndAnswerResult blankAndAnswerResult = new BlankAndAnswerResult();
//                        ComplexQuestion question = null;
//                        try {
//                            question = JsonUtils.fromJson(content, ComplexQuestion.class);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                        /**
//                         * 复合题下的所有子题
//                         */
//                        List<Question> questions = question.getQuestions();
//                        logger.info("复合题子题数据：{}", questions.toString());
//                        //子题顺序
//                        Integer index = andAnswer.getIndex();
//                        /**
//                         * 需要查询的子题
//                         */
//                        Question subjQuestion = questions.get(index);
//                        /**
//                         * 子题答案
//                         */
//                        List<Object> answer = subjQuestion.getAnswer();
//                        /**
//                         * 返回的答案
//                         */
//                        List<AnswerResult> answerResults = new ArrayList<>();
//                        for (int i = 0; i < answer.size(); i++) {
//                            AnswerResult answerResult = new AnswerResult();
//                            answerResult.setIndex(i);
//
//                            //定义正则去除标签
//                            String regexstr = "<([^>]*)>";
//                            Pattern p_space = Pattern.compile(regexstr, Pattern.CASE_INSENSITIVE);
//                            Matcher m_space = p_space.matcher(answer.get(i) + "");
//                            String s1 = m_space.replaceAll("");
//
//                            answerResult.setAnswer(StringEscapeUtils.unescapeHtml4(s1));
//                            answerResults.add(answerResult);
//                        }

                        ComplexQuestionV1 question = null;
                        try {
                            question = JsonUtils.fromJson(content, ComplexQuestionV1.class);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        /**
                         * 复合题下的所有子题
                         */
                        List<QuestionV1> questions = question.getQuestions();
                        logger.info("复合题子题数据：{}", questions.toString());
                        //子题顺序
                        Integer index = andAnswer.getIndex();
                        /**
                         * 需要查询的子题
                         */
                        QuestionV1 subjQuestion = questions.get(index);
                        /**
                         * 返回的答案
                         */
                        List<AnswerResult> answerResults = new ArrayList<>();

                        if(subjQuestion.getAnswer() instanceof ArrayList){
                            /**
                             * 子题答案
                             */
                            List<Object> answer = (List<Object>) subjQuestion.getAnswer();

                            for (int i = 0; i < answer.size(); i++) {
                                AnswerResult answerResult = new AnswerResult();
                                answerResult.setIndex(i);

                                //定义正则去除标签
                                String regexstr = "<([^>]*)>";
                                Pattern p_space = Pattern.compile(regexstr, Pattern.CASE_INSENSITIVE);
                                Matcher m_space = p_space.matcher(answer.get(i) + "");
                                String s1 = m_space.replaceAll("");

                                answerResult.setAnswer(StringEscapeUtils.unescapeHtml4(s1));
                                answerResults.add(answerResult);
                            }
                        }else if(subjQuestion.getAnswer() instanceof String){
                            String answer = (String) subjQuestion.getAnswer();
                            AnswerResult answerResult = new AnswerResult();
                            answerResult.setIndex(index);

                            //定义正则去除标签
                            String regexstr = "<([^>]*)>";
                            Pattern p_space = Pattern.compile(regexstr, Pattern.CASE_INSENSITIVE);
                            Matcher m_space = p_space.matcher(answer);
                            String s1 = m_space.replaceAll("");

                            answerResult.setAnswer(StringEscapeUtils.unescapeHtml4(s1));
                            answerResults.add(answerResult);
                        }

                        blankAndAnswerResult.setQuestionId(andAnswer.getQuestionId());
                        blankAndAnswerResult.setParentQuestionId(andAnswer.getParentQuestionId());
                        blankAndAnswerResult.setAnswerResults(answerResults);
                        blankAndAnswerResults.add(blankAndAnswerResult);
                    }
                }
            }
        }
        response.setResult(blankAndAnswerResults);
        logger.info("通过题目ID查询题目答案结果是：{}",response.toString());
        return response;
    }


    @Override
    public FindWordByChapterResponse findWordsByChapterId(FindWordByChapterRequest request) {
        PageList<BaseWordTranslation> translationPageList = baseWordTranslationMapper.findWordTranslationsByChpterId
                (request.getChapterId(), new PageBounds(request.getPageNumber(), 50));
        List<Word> wordList = new ArrayList<>();
        for (BaseWordTranslation baseWordTranslation : translationPageList) {
            Word word = new Word();
            word.setWordId(baseWordTranslation.getWordId());
            word.setSpell(baseWordTranslation.getSpell());
            word.setTranslation(baseWordTranslation.getTranslation());
            word.setTranslationId(baseWordTranslation.getId());
            wordList.add(word);
        }
        return new FindWordByChapterResponse(wordList);
    }

    @Override
    public FindQuestionsByTypesAndWordsResponse findQuestionsByTypesAndWords(FindQuestionsByTypesAndWordsRequest
                                                                                         request) {
        if(request.getQuestionTypeIds()==null||request.getWordIds()==null||request.getQuestionTypeIds().size()*request.getWordIds().size()<=0){
            throw new BizLayerException("", ANSWER_PARAMETER_ILL);
        }
        PageList<LinkWordType> linkWordTypes = baseWordTranslationMapper.findWordTypeByTypesAndWords(request
                        .getDirectoryId(), request.getChapterId(), request.getVersionId(), request.getQuestionTypeIds(),
                request.getWordIds(), new PageBounds(request.getPageNumber(), 100));
        FindAllQuestionTypeResponse responseType = questionTypeService.findAllQuestionType(new
                FindAllQuestionTypeRequest());

        List<Map<String, Object>> questionDataList = new ArrayList<>();
        for (LinkWordType linkWordType : linkWordTypes) {
            long structId = responseType.findStructIdByTypeId(linkWordType.getQuestionTypeId());
            Map<String, Object> questionMap = new HashMap<>();
            questionMap.put("questionId", linkWordType.getQuestionId());
            questionMap.put("question_type_id", linkWordType.getQuestionTypeId());
            questionMap.put("question_type_name", QuestionTypeEnum.getQuestionTypeByTypeId(linkWordType
                    .getQuestionTypeId()).getSolrType());
            questionMap.put("struct_id", structId);
            questionDataList.add(questionMap);
        }
        return new FindQuestionsByTypesAndWordsResponse(questionDataList);

    }

    @Override
    public FindWordsByQuestionIdsAndChapterIdResponse findWordsByQuestionIdsAndChapterId(FindWordsByQuestionIdsAndChapterIdRequest request) {
        if(request.getQuestionIds()==null||request.getQuestionIds().size()==0){
            throw new BizLayerException("", ANSWER_PARAMETER_ILL);
        }
        PageList<WordAndChapter> wordAndChapters= baseWordTranslationMapper.findWordsByQuestionIdsAndChapterId(request.getQuestionIds(),request.getChapterId(),new PageBounds(1, 100));

        return new FindWordsByQuestionIdsAndChapterIdResponse(wordAndChapters);
    }

    /**
     * 发送单题MQ2Solr
     *
     * @param entityQuestion
     * @param chapterId
     * @param topicIds
     */
    private SolrIndexReqMsg sendSolrSingleDoc2Mq(EntityQuestion entityQuestion, Long chapterId, List<Long> topicIds,
                                                 String html, String reqId) {
        QuestionDocument questionTmpDocument = new QuestionDocument();
        questionTmpDocument.setId(entityQuestion.getId());
        questionTmpDocument.setCountOptions(entityQuestion.getCountOptions());
        questionTmpDocument.setDifficulty(entityQuestion.getDifficulty());
        questionTmpDocument.setHighQual(entityQuestion.getHighQual());
        questionTmpDocument.setMultiScoreAnswer(entityQuestion.getMultiScoreAnswer() != null ? entityQuestion
                .getMultiScoreAnswer() : "");
        questionTmpDocument.setParentQuestionId(entityQuestion.getParentQuestionId());
        questionTmpDocument.setIsSingle(entityQuestion.getIsSingle());
        questionTmpDocument.setState(entityQuestion.getState().name());
        questionTmpDocument.setQrId(entityQuestion.getQrId());
        questionTmpDocument.setCountTopic(entityQuestion.getCountTopic());
        questionTmpDocument.setSubjectId(entityQuestion.getSubjectId());
        questionTmpDocument.setOrgId(entityQuestion.getOrgId());//机构ID
        questionTmpDocument.setOrgType(entityQuestion.getOrgType());
        if (entityQuestion.getUploadTime() == null) {
            questionTmpDocument.setUploadTime(new Date());
        } else {
            questionTmpDocument.setUploadTime(entityQuestion.getUploadTime());
        }
        questionTmpDocument.setUpdateTime(new Date());//更新时间
        questionTmpDocument.setUploadId(entityQuestion.getUploadId());
        questionTmpDocument.setUploadSrc(entityQuestion.getUploadSrc());
        questionTmpDocument.setHtmlData(entityQuestion.getHtmlData());
        questionTmpDocument.setJsonData(entityQuestion.getJsonData());
        //新增对单题发送智批标签
        questionTmpDocument.setIntelligent(entityQuestion.getIntelligent());
        SingleQuestion singleQuestion;
        try {
            singleQuestion = JsonUtils.fromJson(html, SingleQuestion.class);
            if (1 == entityQuestion.getQuestionTypeId()) {  //选择题
                questionTmpDocument.setQuestionType("选择题");
            } else {
                questionTmpDocument.setQuestionType(entityQuestion.getQuestionType());

            }
            questionTmpDocument.setAnswerNum(singleQuestion.getAnswer().size());
        } catch (IOException e) {
            logger.error("html转换单题实体异常={}", e.getMessage());
            throw new BizLayerException("", JSON_CONVERT_FAIL);
        }
        questionTmpDocument.setQuestionTypeId(entityQuestion.getQuestionTypeId());
        questionTmpDocument.setVisible(1);
        questionTmpDocument.setSource(entityQuestion.getSource());
        questionTmpDocument.setNewFormat(1);

        if (entityQuestion.getQuestionTypeId() == 51) {
            if (StringUtils.isNotEmpty(entityQuestion.getJsonMap())) {
                questionTmpDocument.setJsonMap(entityQuestion.getJsonMap());
            }
        }
        //        questionTmpDocument.setQuestionGroup(entityQuestion.getQuestionGroup());
        //        Group group = questionGroupMapper.findById(entityQuestion.getQuestionGroup());//群组名称获取
        //        if (group != null) {
        //            questionTmpDocument.setQuestionGroupName(group.getName());
        //        }

        /**
         * 主题
         */
        if (topicIds != null && topicIds.size() > 0) {
            setTopicIdsRelation(topicIds, questionTmpDocument);
        }

        /**
         * 章节
         */
        if (chapterId != null && chapterId != 0) {

            List<EntityTeachingChapter> teachingChapters = teacherChapterMapper.findChaptersById(chapterId);
            if (CollectionUtils.isEmpty(teachingChapters)) {
                throw new BizLayerException("查询章节不存在={}", FIND_CHAPTER_FAIL);
            }


            List<Long> chapterIds = new ArrayList<>();
            StringBuilder chaptersBuffer = new StringBuilder();

            for (EntityTeachingChapter entityTeachingChapter : teachingChapters) {
                //组装chapterIds
                chapterIds.add(entityTeachingChapter.getId());
                //组装chapterStrs
                chaptersBuffer.append(entityTeachingChapter.getId() + ",");
            }
            String chapters = chaptersBuffer.toString();
            questionTmpDocument.setChapterId(chapterIds);
            questionTmpDocument.setChapterStr(chapters.substring(0, chapters.length() - 1));

            if (teachingChapters.size() == 1) {
                //chapter1
                List<Long> chapter1Id = new ArrayList<>();
                chapter1Id.add(teachingChapters.get(0).getId());
                questionTmpDocument.setChapterId1(chapter1Id);
                List<String> prefixName1 = new ArrayList<>();
                prefixName1.add(teachingChapters.get(0).getPrefixName());
                questionTmpDocument.setPrefixName1(prefixName1);
                List<String> name1 = new ArrayList<>();
                name1.add(teachingChapters.get(0).getName());
                questionTmpDocument.setName1(name1);
                questionTmpDocument.setChapterStr1(teachingChapters.get(0).getId() + "");
            }

            if (teachingChapters.size() == 2) {
                //chapter2
                List<Long> chapter1Id = new ArrayList<>();
                chapter1Id.add(teachingChapters.get(0).getId());
                questionTmpDocument.setChapterId1(chapter1Id);
                List<String> prefixName1 = new ArrayList<>();
                prefixName1.add(teachingChapters.get(0).getPrefixName());
                questionTmpDocument.setPrefixName1(prefixName1);
                List<String> name1 = new ArrayList<>();
                name1.add(teachingChapters.get(0).getName());
                questionTmpDocument.setName1(name1);
                questionTmpDocument.setChapterStr1(teachingChapters.get(0).getId() + "");

                List<Long> chapter2Id = new ArrayList<>();
                chapter2Id.add(teachingChapters.get(1).getId());
                questionTmpDocument.setChapterId2(chapter2Id);
                List<String> prefixName2 = new ArrayList<>();
                prefixName2.add(teachingChapters.get(1).getPrefixName());
                questionTmpDocument.setPrefixName2(prefixName2);
                List<String> name2 = new ArrayList<>();
                name2.add(teachingChapters.get(1).getName());
                questionTmpDocument.setName2(name2);
                questionTmpDocument.setChapterStr2(teachingChapters.get(1).getId() + "");
            }
            if (teachingChapters.size() == 3) {
                //chapter3
                List<Long> chapter1Id = new ArrayList<>();
                chapter1Id.add(teachingChapters.get(0).getId());
                questionTmpDocument.setChapterId1(chapter1Id);
                List<String> prefixName1 = new ArrayList<>();
                prefixName1.add(teachingChapters.get(0).getPrefixName());
                questionTmpDocument.setPrefixName1(prefixName1);
                List<String> name1 = new ArrayList<>();
                name1.add(teachingChapters.get(0).getName());
                questionTmpDocument.setName1(name1);
                questionTmpDocument.setChapterStr1(teachingChapters.get(0).getId() + "");

                List<Long> chapter2Id = new ArrayList<>();
                chapter2Id.add(teachingChapters.get(1).getId());
                questionTmpDocument.setChapterId2(chapter2Id);
                List<String> prefixName2 = new ArrayList<>();
                prefixName2.add(teachingChapters.get(1).getPrefixName());
                questionTmpDocument.setPrefixName2(prefixName2);
                List<String> name2 = new ArrayList<>();
                name2.add(teachingChapters.get(1).getName());
                questionTmpDocument.setName2(name2);
                questionTmpDocument.setChapterStr2(teachingChapters.get(1).getId() + "");

                List<Long> chapter3Id = new ArrayList<>();
                chapter3Id.add(teachingChapters.get(2).getId());
                questionTmpDocument.setChapterId3(chapter3Id);
                List<String> prefixName3 = new ArrayList<>();
                prefixName3.add(teachingChapters.get(2).getPrefixName());
                questionTmpDocument.setPrefixName3(prefixName3);
                List<String> name3 = new ArrayList<>();
                name3.add(teachingChapters.get(2).getName());
                questionTmpDocument.setName3(name3);
                questionTmpDocument.setChapterStr3(teachingChapters.get(2).getId() + "");
            }
        }
        SolrIndexReqMsg solrIndexReqMsg = new SolrIndexReqMsg(questionTmpDocument);

        solrIndexReqMsg.setRequestId(reqId);
        logger.info("========单题========" + solrIndexReqMsg.getRequestId());
        return solrIndexReqMsg;
    }


    /**
     * 复合题MQ2Solr
     *
     * @param entityQuestion
     * @param html
     * @param chapterId
     * @param topicIds
     * @param ids
     * @return
     */
    private SolrIndexReqMsg solrComplexReqMsg(EntityQuestion entityQuestion, String html, Long chapterId, List<Long>
            topicIds, List<Long> ids, String reqId) {
        ComplexQuestion complexQuestion;
        try {
            complexQuestion = JsonUtils.fromJson(html, ComplexQuestion.class);
        } catch (IOException e) {
            logger.error("", e);
            throw new BizLayerException(e, INNER_ERROR);
        }
        List<com.noriental.praxissvr.question.bean.html.Question> questions = complexQuestion.getQuestions();
        List<QuestionDocument> questionTmpDocumentList = new ArrayList<>();

        List<Long> subIds = new ArrayList<>();
        for (int i = 0; i < questions.size(); i++) {

            QuestionDocument questionTmpDocument = new QuestionDocument();
            questionTmpDocument.setId(ids.get(i + 1));
            questionTmpDocument.setCountOptions(questions.get(i).getOptions().size());
            String difficulty = questions.get(i).getDifficulty();
            if (null != difficulty && !"".equals(difficulty)) {
                questionTmpDocument.setDifficulty(Integer.parseInt(difficulty));
            }
            questionTmpDocument.setParentQuestionId(entityQuestion.getId());
            questionTmpDocument.setQuestionType(questions.get(i).getType().getName());//题型
            if (1 == questions.get(i).getType().getId()) { //选择题
                questionTmpDocument.setRightOption(questions.get(i).getAnswer() != null && questions.get(i).getAnswer
                        ().size() > 0 ? questions.get(i).getAnswer().toString() : "");
            }
            /**
             * answerNum个数
             */
            if (questions.get(i).getAnswer() != null) {
                questionTmpDocument.setAnswerNum(questions.get(i).getAnswer().size());
            } else {
                questionTmpDocument.setAnswerNum(0);
            }
            questionTmpDocument.setIsSingle(1);
            questionTmpDocument.setQuestionTypeId(questions.get(i).getType().getId());
            questionTmpDocument.setVisible(1);
            questionTmpDocument.setNewFormat(1);
            questionTmpDocument.setUploadId(entityQuestion.getUploadId());//上传人ID
            questionTmpDocument.setSubjectId(entityQuestion.getSubjectId());//学科ID
            questionTmpDocument.setUpdateTime(new Date());
            questionTmpDocument.setUploadTime(entityQuestion.getUploadTime() != null ? entityQuestion.getUploadTime()
                    : new Date());
            //智能批改
            if(questions.get(i).getAnswer()!=null){
                questionTmpDocument.setIntelligent(QuestionServiceUtil.getIntelligent(questions.get(i).getAnswer(),
                        questions.get(i).getType().getId(),entityQuestion.getSubjectId()));
            }else{
                questionTmpDocument.setIntelligent(0);
            }
            //复合题子题白名单特殊处理
            if(QuestionServiceUtil.isWhiteSchool(auditsedSchoolMapper,entityQuestion.getOrgId(),redisUtil)){
                questionTmpDocument.setState(QuestionState.ENABLED.name());
            }

             /*
                子题作图题处理
             */
            if (questions.get(i).getType().getId()==51) {
                NewMap map = questions.get(i).getMap();
                try {
                    String json = JSON.json(map);
                    questionTmpDocument.setJsonMap(json);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            subIds.add(questionTmpDocument.getId());
            questionTmpDocumentList.add(questionTmpDocument);
        }
        //大题信息
        QuestionDocument questionTmp = new QuestionDocument();
        questionTmp.setId(entityQuestion.getId());
        questionTmp.setCountOptions(entityQuestion.getCountOptions());
        questionTmp.setDifficulty(entityQuestion.getDifficulty());
        questionTmp.setHighQual(entityQuestion.getHighQual());
        questionTmp.setMultiScoreAnswer(entityQuestion.getMultiScoreAnswer() != null ? entityQuestion
                .getMultiScoreAnswer() : "");
        questionTmp.setQuestionType(entityQuestion.getQuestionType());
        questionTmp.setRightOption(entityQuestion.getRightOption() != null && !"".equals(entityQuestion
                .getRightOption()) ? entityQuestion.getRightOption() : "");
        questionTmp.setIsSingle(0);
        questionTmp.setState(entityQuestion.getState().name());
        questionTmp.setQrId(entityQuestion.getQrId());
        questionTmp.setCountTopic(entityQuestion.getCountTopic());
        questionTmp.setSubjectId(entityQuestion.getSubjectId());
        questionTmp.setOrgId(entityQuestion.getOrgId());
        questionTmp.setOrgType(entityQuestion.getOrgType());
        questionTmp.setAllLeafQuesIds(subIds);
        questionTmp.setUploadTime((entityQuestion.getUploadTime() != null && !"".equals(entityQuestion.getUpdateTime
                ())) ? entityQuestion.getUploadTime() : null);
        questionTmp.setUpdateTime(new Date());
        questionTmp.setUploadId(entityQuestion.getUploadId());
        questionTmp.setUploadSrc(entityQuestion.getUploadSrc());
        questionTmp.setQuestionGroup(entityQuestion.getQuestionGroup());
        questionTmp.setAnswerNum(entityQuestion.getAnswerNum());
        questionTmp.setQuestionTypeId(entityQuestion.getQuestionTypeId());
        questionTmp.setSource(entityQuestion.getSource());
        questionTmp.setParentQuestionId(entityQuestion.getParentQuestionId());
        questionTmp.setVisible(1);
        questionTmp.setNewFormat(1);
        questionTmp.setParentQuestionId(0L);
        questionTmp.setHtmlData(entityQuestion.getHtmlData());
        questionTmp.setJsonData(entityQuestion.getJsonData());



        //================章节信息=================
        if (chapterId != null && chapterId != 0) {

            List<EntityTeachingChapter> teachingChapters = teacherChapterMapper.findChaptersById(chapterId);

            if (CollectionUtils.isEmpty(teachingChapters)) {
                throw new BizLayerException("查询章节不存在={}", FIND_CHAPTER_FAIL);
            }

            List<Long> chapterIds = new ArrayList<>();
            StringBuilder chaptersBuf = new StringBuilder();
            for (EntityTeachingChapter entityTeachingChapter : teachingChapters) {
                //组装chapterIds
                chapterIds.add(entityTeachingChapter.getId());
                //组装chapterStrs
                chaptersBuf.append(entityTeachingChapter.getId() + ",");
            }
            String chapters = chaptersBuf.toString();
            questionTmp.setChapterId(chapterIds);
            questionTmp.setChapterStr(chapters.substring(0, chapters.length() - 1));

            if (teachingChapters.size() == 1) {
                //chapter1
                List<Long> chapter1Id = new ArrayList<>();
                chapter1Id.add(teachingChapters.get(0).getId());
                questionTmp.setChapterId1(chapter1Id);
                List<String> prefixName1 = new ArrayList<>();
                prefixName1.add(teachingChapters.get(0).getPrefixName());
                questionTmp.setPrefixName1(prefixName1);
                List<String> name1 = new ArrayList<>();
                name1.add(teachingChapters.get(0).getName());
                questionTmp.setName1(name1);
                questionTmp.setChapterStr1(teachingChapters.get(0).getId() + "");
            }

            if (teachingChapters.size() == 2) {
                //chapter2
                List<Long> chapter1Id = new ArrayList<>();
                chapter1Id.add(teachingChapters.get(0).getId());
                questionTmp.setChapterId1(chapter1Id);
                List<String> prefixName1 = new ArrayList<>();
                prefixName1.add(teachingChapters.get(0).getPrefixName());
                questionTmp.setPrefixName1(prefixName1);
                List<String> name1 = new ArrayList<>();
                name1.add(teachingChapters.get(0).getName());
                questionTmp.setName1(name1);
                questionTmp.setChapterStr1(teachingChapters.get(0).getId() + "");

                List<Long> chapter2Id = new ArrayList<>();
                chapter2Id.add(teachingChapters.get(1).getId());
                questionTmp.setChapterId2(chapter2Id);
                List<String> prefixName2 = new ArrayList<>();
                prefixName2.add(teachingChapters.get(1).getPrefixName());
                questionTmp.setPrefixName2(prefixName2);
                List<String> name2 = new ArrayList<>();
                name2.add(teachingChapters.get(1).getName());
                questionTmp.setName2(name2);
                questionTmp.setChapterStr2(teachingChapters.get(1).getId() + "");
            }
            if (teachingChapters.size() == 3) {
                //chapter3
                List<Long> chapter1Id = new ArrayList<>();
                chapter1Id.add(teachingChapters.get(0).getId());
                questionTmp.setChapterId1(chapter1Id);
                List<String> prefixName1 = new ArrayList<>();
                prefixName1.add(teachingChapters.get(0).getPrefixName());
                questionTmp.setPrefixName1(prefixName1);
                List<String> name1 = new ArrayList<>();
                name1.add(teachingChapters.get(0).getName());
                questionTmp.setName1(name1);
                questionTmp.setChapterStr1(teachingChapters.get(0).getId() + "");

                List<Long> chapter2Id = new ArrayList<>();
                chapter2Id.add(teachingChapters.get(1).getId());
                questionTmp.setChapterId2(chapter2Id);
                List<String> prefixName2 = new ArrayList<>();
                prefixName2.add(teachingChapters.get(1).getPrefixName());
                questionTmp.setPrefixName2(prefixName2);
                List<String> name2 = new ArrayList<>();
                name2.add(teachingChapters.get(1).getName());
                questionTmp.setName2(name2);
                questionTmp.setChapterStr2(teachingChapters.get(1).getId() + "");

                List<Long> chapter3Id = new ArrayList<>();
                chapter3Id.add(teachingChapters.get(2).getId());
                questionTmp.setChapterId3(chapter3Id);
                List<String> prefixName3 = new ArrayList<>();
                prefixName3.add(teachingChapters.get(2).getPrefixName());
                questionTmp.setPrefixName3(prefixName3);
                List<String> name3 = new ArrayList<>();
                name3.add(teachingChapters.get(2).getName());
                questionTmp.setName3(name3);
                questionTmp.setChapterStr3(teachingChapters.get(2).getId() + "");
            }
        }
        //==================主题信息==================
        if (topicIds != null && topicIds.size() > 0) {
            //通过topicIds获取unit 和 module
            setTopicIdsRelation(topicIds, questionTmp);

        }
        questionTmpDocumentList.add(questionTmp);
        SolrIndexReqMsg solrIndexReqMsg = new SolrIndexReqMsg(questionTmpDocumentList);
        solrIndexReqMsg.setRequestId(reqId);
        logger.info("========复合题========" + solrIndexReqMsg.getRequestId());
        return solrIndexReqMsg;

    }

    private void setTopicIdsRelation(List<Long> topicIds, QuestionDocument questionTmp) {
        ResponseEntity<List<TopicWithParent>> topicsWithParent = topicService.findTopicsWithParent(new
                RequestEntity<>(topicIds));
        List<TopicWithParent> entitys = topicsWithParent.getEntity();
        List<Long> unitIds = new ArrayList<>();
        List<Long> moduleIds = new ArrayList<>();
        List<String> topicNames = new ArrayList<>();
        Set<String> allMUTIds = new HashSet<>();
        for (TopicWithParent entity : entitys) {
            if (entity != null) {
                topicNames.add(entity.getName());//知识点名称
                allMUTIds.add("T" + entity.getId());
                Unit unit = entity.getUnit();
                if (unit != null) {
                    unitIds.add(entity.getUnit().getId());//单元IDS
                    allMUTIds.add("U" + unit.getId());
                }
                Module module = entity.getModule();
                if (module != null) {
                    moduleIds.add(entity.getModule().getId());//模块IDS
                    allMUTIds.add("M" + module.getId());
                }

            }
        }
        questionTmp.setAllMUTIds(new ArrayList<String>(allMUTIds));
        questionTmp.setAllTopicIds(topicIds);
        questionTmp.setAllTopicIdStr(topicIds.toString().substring(1, topicIds.toString().length() - 1));
        questionTmp.setAllUnitIds(unitIds);
        questionTmp.setAllTopicIdStr(unitIds.toString().substring(1, unitIds.toString().length() - 1));
        questionTmp.setAllModuleIds(moduleIds);
        questionTmp.setAllModuleIdStr(moduleIds.toString().substring(1, moduleIds.toString().length() - 1));
        questionTmp.setTopicName(topicNames);
        questionTmp.setTopicId(topicIds);
    }


    private void updateQuestionSolrIndex(EntityQuestion entityQuestion, String html, Long chapterId, List<Long>
            topicIds, List<Long> ids) throws BizLayerException {

        SolrIndexReqMsg solrIndexReqMsg;
        String common_id=TraceKeyHolder.getTraceKey();

        int length = JsonUtils.getQuestionsLength(html, "questions");
        if (length > 0) { //复合题


            /**
             * 复合题solr更新步骤
             * 1.查询数据库插入子题的题目ID,此题目ID为更新之后的子题ID
             * 2.构建大题参数进行solr更新
             * 3.重新构建子题参数进行solr创建
             */


            /**
             * 查询数据库子题ID，用于发送solr子题信息
             */
            List<Long> subjId = entityQuestionMapper.findQuestionSubjIdByParentId(entityQuestion.getId());
            if (CollectionUtils.isEmpty(subjId)) {
                throw new BizLayerException("", QUERY_QUESTIONID_FAIL);
            }

            /**
             * 发送solr更新复合题大题索引
             */
            QuestionDocument questionDocument = parentIndexQuestion(entityQuestion, chapterId, topicIds, subjId);
            /**
             * 大题更新索引
             */
            Map<String, Object> bodyMap = new HashMap<>();
            String question_id=common_id + "-question";
            try {
                EntityUtils.copyValueByGetter(questionDocument, bodyMap);
                //过滤bodyMap数据，将bodyMap中为null的值过滤掉
                Map<String, Object> map = MapUtils.mapRemoveWithNullByRecursion(bodyMap);
                map.put(Constants._DOC_CLASS_NAME, QuestionDocument.class.getName());
                SolrIndexReqMsg msg = new SolrIndexReqMsg(map);
                msg.setRequestId(question_id);
                solrUploadQuestionRabbitTemplate.convertAndSend(msg);
                logger.info("updateContinuedQuestion cms后台复合体大题更新solr发送的信息:{}",JsonUtil.obj2Json(msg));
            } catch (IntrospectionException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }


            /**
             * 发送solr创建复合题子题索引
             */

            //构建复合题子题参数
            List<QuestionDocument> questionDocuments = solrIndexReqMsgSubj(entityQuestion, html, subjId);
            /**
             * 取单题创建索引
             */
            String reqid=common_id + "-sub-question";
            for (QuestionDocument document : questionDocuments) {
                SolrIndexReqMsg solrIndexReqMsg1 = new SolrIndexReqMsg(document);
                solrIndexReqMsg1.setRequestId(reqid);
                solrUploadQuestionRabbitTemplate.convertAndSend(solrIndexReqMsg1);
                logger.info("updateContinuedQuestion cms后台复合体子题更新solr发送的信息:{}",JsonUtil.obj2Json(solrIndexReqMsg1));
            }

        } else { //单题
            try {
                solrIndexReqMsg = componentSolrDoc(entityQuestion, html, chapterId, topicIds);
                QuestionDocument questionTmpDocument = (QuestionDocument) solrIndexReqMsg.getBody();

                Map<String, Object> bodyMap = new HashMap<>();
                EntityUtils.copyValueByGetter(questionTmpDocument, bodyMap);
                /**
                 * 数据过滤
                 */
                Map<String, Object> map = MapUtils.mapRemoveWithNullByRecursion(bodyMap);
                map.put(Constants._DOC_CLASS_NAME, QuestionDocument.class.getName());
                SolrIndexReqMsg msg = new SolrIndexReqMsg(map);
                String single_id=common_id + "-single-question";
                msg.setRequestId(single_id);
               // logger.info("更新solr数据：" + msg.getBody());
                solrUploadQuestionRabbitTemplate.convertAndSend(msg);
                logger.info("updateContinuedQuestion cms后台复合体单题题更新solr发送的信息:{}",JsonUtil.obj2Json(msg));
            } catch (Exception e) {
                logger.error("更新单题异常消息={}", e);
                throw new BizLayerException("", UPDATE_SOLR_FAIL);
            }
        }
    }


    /**
     * 单题转换MQ
     *
     * @param entityQuestion
     * @param html
     * @param chapterId
     * @param topicIds
     * @return
     */
    private SolrIndexReqMsg componentSolrDoc(EntityQuestion entityQuestion, String html, Long chapterId, List<Long>
            topicIds) {

        QuestionDocument questionTmpDocument = new QuestionDocument();
        if (entityQuestion.getId() != 0) {
            questionTmpDocument.setId(entityQuestion.getId());
        }
        if (entityQuestion.getCountOptions() != 0) {
            questionTmpDocument.setCountOptions(entityQuestion.getCountOptions());
        }

        if (entityQuestion.getDifficulty() != 0) {
            questionTmpDocument.setDifficulty(entityQuestion.getDifficulty());
        }
        if (entityQuestion.getHighQual() != null) {
            questionTmpDocument.setHighQual(entityQuestion.getHighQual());
        }

        questionTmpDocument.setMultiScoreAnswer(entityQuestion.getMultiScoreAnswer() != null ? entityQuestion
                .getMultiScoreAnswer() : "");
        questionTmpDocument.setParentQuestionId(entityQuestion.getParentQuestionId());

        questionTmpDocument.setIsSingle(entityQuestion.getIsSingle());

        if (entityQuestion.getState() != null) {
            questionTmpDocument.setState(entityQuestion.getState().name());
        }

        if (entityQuestion.getQrId() != null) {
            questionTmpDocument.setQrId(entityQuestion.getQrId());
        }

        if (entityQuestion.getCountTopic() != null) {
            questionTmpDocument.setCountTopic(entityQuestion.getCountTopic());
        }

        if (entityQuestion.getSubjectId() != null) {
            questionTmpDocument.setSubjectId(entityQuestion.getSubjectId());
        }

        //更新时间
        questionTmpDocument.setUpdateTime(new Date());
        //机构ID
        if (entityQuestion.getOrgId() != null) {
            questionTmpDocument.setOrgId(entityQuestion.getOrgId());
        }
        if (entityQuestion.getOrgType() != null) {
            questionTmpDocument.setOrgType(entityQuestion.getOrgType());
        }

        //智能批改
        questionTmpDocument.setIntelligent(entityQuestion.getIntelligent());
        //上传时间
        if (null != entityQuestion.getUploadTime() && !"".equals(entityQuestion.getUploadTime())) {
            questionTmpDocument.setUploadTime(entityQuestion.getUploadTime());
        }

        if (entityQuestion.getUploadId() != null) {
            questionTmpDocument.setUploadId(entityQuestion.getUploadId());
        }
        if (entityQuestion.getUploadSrc() != null) {
            questionTmpDocument.setUploadSrc(entityQuestion.getUploadSrc());
        }
        if (entityQuestion.getHtmlData() != null) {
            questionTmpDocument.setHtmlData(entityQuestion.getHtmlData());
        }
        if (entityQuestion.getJsonData() != null) {
            questionTmpDocument.setJsonData(entityQuestion.getJsonData());
        }
        /*
            作图题处理
         */
        if (entityQuestion.getQuestionTypeId() == 51) {
            if (StringUtils.isNotEmpty(entityQuestion.getJsonMap())) {
                questionTmpDocument.setJsonMap(entityQuestion.getJsonMap());
            }
        }

        SingleQuestion singleQuestion;
        try {
            singleQuestion = JsonUtils.fromJson(html, SingleQuestion.class);
            if (1 == entityQuestion.getQuestionTypeId()) {  //选择题
                questionTmpDocument.setQuestionType("选择题");
            } else {
                if (StringUtils.isNotEmpty(entityQuestion.getQuestionType())) {
                    questionTmpDocument.setQuestionType(entityQuestion.getQuestionType());
                }
            }
            questionTmpDocument.setAnswerNum(singleQuestion.getAnswer().size());
        } catch (IOException e) {
            logger.error("html转换单题实体异常={}", e.getMessage());
            throw new BizLayerException("", JSON_CONVERT_FAIL);
        }
        if (entityQuestion.getQuestionTypeId() != null) {
            questionTmpDocument.setQuestionTypeId(entityQuestion.getQuestionTypeId());
        }

        questionTmpDocument.setVisible(1);
        questionTmpDocument.setSource(entityQuestion.getSource());
        questionTmpDocument.setNewFormat(1);
        questionTmpDocument.setQuestionGroup(entityQuestion.getQuestionGroup());
        //群组名称获取
        //        Group group = questionGroupMapper.findById(entityQuestion.getQuestionGroup());
        //        if (group != null) {
        //            //添加群组索引
        //            questionTmpDocument.setQuestionGroupName(group.getName());
        //        }

        //============主题================

        if (topicIds != null && topicIds.size() > 0) {

            questionTmpDocument.setTopicId(topicIds);

            Set<String> allMUTIds = new HashSet<>();
            List<String> topicNames = new ArrayList<>();

            ResponseEntity<List<TopicWithParent>> topicListResp = topicService.findTopicsWithParent(new
                    RequestEntity<>(topicIds));

            List<TopicWithParent> topicList = topicListResp.getEntity();
            for (TopicWithParent topic : topicList) {
                topicNames.add(topic.getName());
                allMUTIds.add("T" + topic.getId());
                Unit unit = topic.getUnit();
                if (unit != null) {
                    allMUTIds.add("U" + unit.getId());
                }
                Module module = topic.getModule();
                if (module != null) {
                    allMUTIds.add("M" + module.getId());
                }
            }
            questionTmpDocument.setTopicStr(topicIds.toString().substring(1, topicIds.toString().length() - 1));
            questionTmpDocument.setAllMUTIds(new ArrayList<>(allMUTIds));
            questionTmpDocument.setTopicName(topicNames);
        }


        //============章节================
        logger.info("=============章节：{}", chapterId);
        //通过章节ID获取章节列表
        if (chapterId != null && chapterId != 0) {

            //查询chapter是全部级联信息
            List<EntityTeachingChapter> teachingChapters = teacherChapterMapper.findChaptersById(chapterId);

            if (CollectionUtils.isEmpty(teachingChapters)) {
                throw new BizLayerException("查询章节不存在={}", FIND_CHAPTER_FAIL);
            }


            List<Long> chapterIds = new ArrayList<>();
            StringBuilder chaptersBuffer = new StringBuilder();

            for (EntityTeachingChapter entityTeachingChapter : teachingChapters) {
                //组装chapterIds
                chapterIds.add(entityTeachingChapter.getId());
                //组装chapterStrs
                chaptersBuffer.append(entityTeachingChapter.getId() + ",");
            }
            String chapters = chaptersBuffer.toString();
            questionTmpDocument.setChapterId(chapterIds);
            questionTmpDocument.setChapterStr(chapters.substring(0, chapters.length() - 1));

            if (teachingChapters.size() == 1) {
                //chapter1
                List<Long> chapter1Id = new ArrayList<>();
                chapter1Id.add(teachingChapters.get(0).getId());
                questionTmpDocument.setChapterId1(chapter1Id);
                List<String> prefixName1 = new ArrayList<>();
                prefixName1.add(teachingChapters.get(0).getPrefixName());
                questionTmpDocument.setPrefixName1(prefixName1);
                List<String> name1 = new ArrayList<>();
                name1.add(teachingChapters.get(0).getName());
                questionTmpDocument.setName1(name1);
                questionTmpDocument.setChapterStr1(teachingChapters.get(0).getId() + "");
            }

            if (teachingChapters.size() == 2) {
                //chapter2
                List<Long> chapter1Id = new ArrayList<>();
                chapter1Id.add(teachingChapters.get(0).getId());
                questionTmpDocument.setChapterId1(chapter1Id);
                List<String> prefixName1 = new ArrayList<>();
                prefixName1.add(teachingChapters.get(0).getPrefixName());
                questionTmpDocument.setPrefixName1(prefixName1);
                List<String> name1 = new ArrayList<>();
                name1.add(teachingChapters.get(0).getName());
                questionTmpDocument.setName1(name1);
                questionTmpDocument.setChapterStr1(teachingChapters.get(0).getId() + "");

                List<Long> chapter2Id = new ArrayList<>();
                chapter2Id.add(teachingChapters.get(1).getId());
                questionTmpDocument.setChapterId2(chapter2Id);
                List<String> prefixName2 = new ArrayList<>();
                prefixName2.add(teachingChapters.get(1).getPrefixName());
                questionTmpDocument.setPrefixName2(prefixName2);
                List<String> name2 = new ArrayList<>();
                name2.add(teachingChapters.get(1).getName());
                questionTmpDocument.setName2(name2);
                questionTmpDocument.setChapterStr2(teachingChapters.get(1).getId() + "");
            }
            if (teachingChapters.size() == 3) {
                //chapter3
                List<Long> chapter1Id = new ArrayList<>();
                chapter1Id.add(teachingChapters.get(0).getId());
                questionTmpDocument.setChapterId1(chapter1Id);
                List<String> prefixName1 = new ArrayList<>();
                prefixName1.add(teachingChapters.get(0).getPrefixName());
                questionTmpDocument.setPrefixName1(prefixName1);
                List<String> name1 = new ArrayList<>();
                name1.add(teachingChapters.get(0).getName());
                questionTmpDocument.setName1(name1);
                questionTmpDocument.setChapterStr1(teachingChapters.get(0).getId() + "");

                List<Long> chapter2Id = new ArrayList<>();
                chapter2Id.add(teachingChapters.get(1).getId());
                questionTmpDocument.setChapterId2(chapter2Id);
                List<String> prefixName2 = new ArrayList<>();
                prefixName2.add(teachingChapters.get(1).getPrefixName());
                questionTmpDocument.setPrefixName2(prefixName2);
                List<String> name2 = new ArrayList<>();
                name2.add(teachingChapters.get(1).getName());
                questionTmpDocument.setName2(name2);
                questionTmpDocument.setChapterStr2(teachingChapters.get(1).getId() + "");

                List<Long> chapter3Id = new ArrayList<>();
                chapter3Id.add(teachingChapters.get(2).getId());
                questionTmpDocument.setChapterId3(chapter3Id);
                List<String> prefixName3 = new ArrayList<>();
                prefixName3.add(teachingChapters.get(2).getPrefixName());
                questionTmpDocument.setPrefixName3(prefixName3);
                List<String> name3 = new ArrayList<>();
                name3.add(teachingChapters.get(2).getName());
                questionTmpDocument.setName3(name3);
                questionTmpDocument.setChapterStr3(teachingChapters.get(2).getId() + "");
            }
        }

        SolrIndexReqMsg solrIndexReqMsg = new SolrIndexReqMsg(questionTmpDocument);
        logger.info("单题solr更新数据" + solrIndexReqMsg.getBody());
        solrIndexReqMsg.setRequestId(TraceKeyHolder.getTraceKey());
        logger.info("单题发送MQ的请求ID=", solrIndexReqMsg.getRequestId());
        return solrIndexReqMsg;
    }


    /**
     * 封装复合题大题的信息用于进行solr更新
     *
     * @param entityQuestion
     * @param chapterId
     * @param topicIds
     * @param subIds
     * @return
     */
    private QuestionDocument parentIndexQuestion(EntityQuestion entityQuestion, Long chapterId, List<Long> topicIds,
                                                 List<Long> subIds) {
        QuestionDocument questionTmp = new QuestionDocument();

        /**
         * 重新封装复合题大题参数用于更新solr
         */
        questionTmp.setId(entityQuestion.getId());
        questionTmp.setCountOptions(entityQuestion.getCountOptions());
        questionTmp.setDifficulty(entityQuestion.getDifficulty());
        questionTmp.setHighQual(entityQuestion.getHighQual());
        questionTmp.setMultiScoreAnswer(entityQuestion.getMultiScoreAnswer() != null ? entityQuestion
                .getMultiScoreAnswer() : "");
        questionTmp.setQuestionType(entityQuestion.getQuestionType());
        questionTmp.setRightOption(entityQuestion.getRightOption() != null && !"".equals(entityQuestion
                .getRightOption()) ? entityQuestion.getRightOption() : "");
        questionTmp.setIsSingle(0);
        questionTmp.setState(entityQuestion.getState().name());
        questionTmp.setQrId(entityQuestion.getQrId());
        questionTmp.setCountTopic(entityQuestion.getCountTopic());
        questionTmp.setSubjectId(entityQuestion.getSubjectId());
        questionTmp.setOrgId(entityQuestion.getOrgId());
        questionTmp.setOrgType(entityQuestion.getOrgType());
        //智能批改--复合题答题肯定不支持智批
        questionTmp.setIntelligent(0);
        questionTmp.setAllLeafQuesIds(subIds);

        if (entityQuestion.getUploadTime() != null) {
            questionTmp.setUploadTime((entityQuestion.getUploadTime() != null && !"".equals(entityQuestion
                    .getUpdateTime())) ? entityQuestion.getUploadTime() : null);
        }
        questionTmp.setUpdateTime(new Date());
        questionTmp.setUploadId(entityQuestion.getUploadId());
        questionTmp.setUploadSrc(entityQuestion.getUploadSrc());
        questionTmp.setQuestionGroup(entityQuestion.getQuestionGroup());
        questionTmp.setAnswerNum(entityQuestion.getAnswerNum());
        questionTmp.setQuestionTypeId(entityQuestion.getQuestionTypeId());
        questionTmp.setSource(entityQuestion.getSource());
        questionTmp.setParentQuestionId(0L);
        questionTmp.setVisible(1);
        questionTmp.setNewFormat(1);
        questionTmp.setHtmlData(entityQuestion.getHtmlData());
        questionTmp.setJsonData(entityQuestion.getJsonData());


        //群组名称获取
      /*  Group group = questionGroupMapper.findById(entityQuestion.getQuestionGroup());
        if (group != null) {
            //添加群组索引
            questionTmp.setQuestionGroupName(group.getName());
        }*/

        //================章节信息=================

        if (chapterId != null && chapterId != 0) {

            List<EntityTeachingChapter> teachingChapters = teacherChapterMapper.findChaptersById(chapterId);

            if (CollectionUtils.isEmpty(teachingChapters)) {
                throw new BizLayerException("查询章节不存在={}", FIND_CHAPTER_FAIL);
            }


            List<Long> chapterIds = new ArrayList<>();
            StringBuilder chaptersBuf = new StringBuilder();
            for (EntityTeachingChapter entityTeachingChapter : teachingChapters) {
                //组装chapterIds
                chapterIds.add(entityTeachingChapter.getId());
                //组装chapterStrs
                chaptersBuf.append(entityTeachingChapter.getId() + ",");
            }
            String chapters = chaptersBuf.toString();
            questionTmp.setChapterId(chapterIds);
            questionTmp.setChapterStr(chapters.substring(0, chapters.length() - 1));

            if (teachingChapters.size() == 1) {
                //chapter1
                List<Long> chapter1Id = new ArrayList<>();
                chapter1Id.add(teachingChapters.get(0).getId());
                questionTmp.setChapterId1(chapter1Id);
                List<String> prefixName1 = new ArrayList<>();
                prefixName1.add(teachingChapters.get(0).getPrefixName());
                questionTmp.setPrefixName1(prefixName1);
                List<String> name1 = new ArrayList<>();
                name1.add(teachingChapters.get(0).getName());
                questionTmp.setName1(name1);
                questionTmp.setChapterStr1(teachingChapters.get(0).getId() + "");
            }

            if (teachingChapters.size() == 2) {
                //chapter2
                List<Long> chapter1Id = new ArrayList<>();
                chapter1Id.add(teachingChapters.get(0).getId());
                questionTmp.setChapterId1(chapter1Id);
                List<String> prefixName1 = new ArrayList<>();
                prefixName1.add(teachingChapters.get(0).getPrefixName());
                questionTmp.setPrefixName1(prefixName1);
                List<String> name1 = new ArrayList<>();
                name1.add(teachingChapters.get(0).getName());
                questionTmp.setName1(name1);
                questionTmp.setChapterStr1(teachingChapters.get(0).getId() + "");

                List<Long> chapter2Id = new ArrayList<>();
                chapter2Id.add(teachingChapters.get(1).getId());
                questionTmp.setChapterId2(chapter2Id);
                List<String> prefixName2 = new ArrayList<>();
                prefixName2.add(teachingChapters.get(1).getPrefixName());
                questionTmp.setPrefixName2(prefixName2);
                List<String> name2 = new ArrayList<>();
                name2.add(teachingChapters.get(1).getName());
                questionTmp.setName2(name2);
                questionTmp.setChapterStr2(teachingChapters.get(1).getId() + "");
            }
            if (teachingChapters.size() == 3) {
                //chapter3
                List<Long> chapter1Id = new ArrayList<>();
                chapter1Id.add(teachingChapters.get(0).getId());
                questionTmp.setChapterId1(chapter1Id);
                List<String> prefixName1 = new ArrayList<>();
                prefixName1.add(teachingChapters.get(0).getPrefixName());
                questionTmp.setPrefixName1(prefixName1);
                List<String> name1 = new ArrayList<>();
                name1.add(teachingChapters.get(0).getName());
                questionTmp.setName1(name1);
                questionTmp.setChapterStr1(teachingChapters.get(0).getId() + "");

                List<Long> chapter2Id = new ArrayList<>();
                chapter2Id.add(teachingChapters.get(1).getId());
                questionTmp.setChapterId2(chapter2Id);
                List<String> prefixName2 = new ArrayList<>();
                prefixName2.add(teachingChapters.get(1).getPrefixName());
                questionTmp.setPrefixName2(prefixName2);
                List<String> name2 = new ArrayList<>();
                name2.add(teachingChapters.get(1).getName());
                questionTmp.setName2(name2);
                questionTmp.setChapterStr2(teachingChapters.get(1).getId() + "");

                List<Long> chapter3Id = new ArrayList<>();
                chapter3Id.add(teachingChapters.get(2).getId());
                questionTmp.setChapterId3(chapter3Id);
                List<String> prefixName3 = new ArrayList<>();
                prefixName3.add(teachingChapters.get(2).getPrefixName());
                questionTmp.setPrefixName3(prefixName3);
                List<String> name3 = new ArrayList<>();
                name3.add(teachingChapters.get(2).getName());
                questionTmp.setName3(name3);
                questionTmp.setChapterStr3(teachingChapters.get(2).getId() + "");
            }
        }
        //==================主题信息==================

        if (topicIds != null && topicIds.size() > 0) {

            //通过topicIds获取unit 和 module
            setTopicIdsRelation(topicIds, questionTmp);

        }
        return questionTmp;
    }


    /**
     * 复合题中子题转换
     *
     * @param entityQuestion
     * @param html
     * @param ids
     * @return
     */
    private List<QuestionDocument> solrIndexReqMsgSubj(EntityQuestion entityQuestion, String html, List<Long> ids) {
        ComplexQuestion complexQuestion;
        try {
            complexQuestion = JsonUtils.fromJson(html, ComplexQuestion.class);
        } catch (IOException e) {
            logger.error("", e);
            throw new BizLayerException(e, INNER_ERROR);
        }
        List<com.noriental.praxissvr.question.bean.html.Question> questions = complexQuestion.getQuestions();
        List<QuestionDocument> questionTmpDocumentList = new ArrayList<>();

        for (int i = 0; i < questions.size(); i++) {
            QuestionDocument questionTmpDocument = new QuestionDocument();

            questionTmpDocument.setId(ids.get(i));
            if(questions.get(i).getOptions()!=null){
                questionTmpDocument.setCountOptions(questions.get(i).getOptions().size());
            }
            String difficulty = questions.get(i).getDifficulty();
            if (null != difficulty && !"".equals(difficulty)) {
                questionTmpDocument.setDifficulty(Integer.parseInt(difficulty));
            }
            questionTmpDocument.setParentQuestionId(entityQuestion.getId());
            questionTmpDocument.setQuestionType(questions.get(i).getType().getName());//题型
            if (1 == questions.get(i).getType().getId()) { //选择题
                questionTmpDocument.setRightOption(questions.get(i).getAnswer() != null && questions.get(i).getAnswer
                        ().size() > 0 ? questions.get(i).getAnswer().toString() : "");

            }
            /**
             * answerNum个数
             */
        if(questions.get(i).getAnswer()!=null){
            questionTmpDocument.setAnswerNum(questions.get(i).getAnswer().size());
        }
            questionTmpDocument.setIsSingle(1);
            questionTmpDocument.setQuestionTypeId(questions.get(i).getType().getId());
            questionTmpDocument.setVisible(1);
            questionTmpDocument.setNewFormat(1);
            //上传人ID
            questionTmpDocument.setUploadId(entityQuestion.getUploadId());
            //学科ID
            questionTmpDocument.setSubjectId(entityQuestion.getSubjectId());
            //智能批改
            if(questions.get(i).getAnswer()!=null){
                questionTmpDocument.setIntelligent(QuestionServiceUtil.getIntelligent(questions.get(i).getAnswer(),
                        questions.get(i).getType().getId(),entityQuestion.getSubjectId()));
            }else{
                questionTmpDocument.setIntelligent(0);
            }

            if (entityQuestion.getUploadTime() != null) {
                questionTmpDocument.setUploadTime(entityQuestion.getUploadTime() != null ? entityQuestion
                        .getUploadTime() : null);
            }
            questionTmpDocument.setUpdateTime(new Date());
            //复合题子题白名单特殊处理
            if(QuestionServiceUtil.isWhiteSchool(auditsedSchoolMapper,entityQuestion.getOrgId(),redisUtil)){
                questionTmpDocument.setState(QuestionState.ENABLED.name());
            }

            if (questions.get(i).getType().getId()==51) {
                NewMap map = questions.get(i).getMap();
                try {
                    String json = JSON.json(map);
                    questionTmpDocument.setJsonMap(json);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            questionTmpDocumentList.add(questionTmpDocument);
        }
        return questionTmpDocumentList;

    }


    /**
     * 批量获取数据库ID
     *
     * @param count
     * @return
     */
    private List<Long> getQuestionIds(int count) {
        SeqNextIdListReq seqNextIdListReq = new SeqNextIdListReq();
        seqNextIdListReq.setSequenceType("OKAY_QUESTION_ID");
        seqNextIdListReq.setIdSize(count);
        CommonResponse<List<Long>> idList = sequenceService.getNextIdList(seqNextIdListReq);
        return idList.getData();
    }


}
