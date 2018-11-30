package com.noriental.praxissvr.question.service.impl;

import com.alibaba.dubbo.common.json.JSON;
import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.noriental.adminsvr.bean.knowledge.Module;
import com.noriental.adminsvr.bean.knowledge.Topic;
import com.noriental.adminsvr.bean.knowledge.TopicWithParent;
import com.noriental.adminsvr.bean.knowledge.Unit;
import com.noriental.adminsvr.request.RequestEntity;
import com.noriental.adminsvr.response.ResponseEntity;
import com.noriental.adminsvr.service.knowledge.TopicService;
import com.noriental.extresources.request.ConvertMp3Request;
import com.noriental.extresources.service.QiniuVoiceService;
import com.noriental.global.dict.AppType;
import com.noriental.log.TraceKeyHolder;
import com.noriental.praxissvr.answer.request.FlowTurnCorrectRequest;
import com.noriental.praxissvr.answer.util.RedisKeyUtil;
import com.noriental.praxissvr.common.*;
import com.noriental.praxissvr.exception.PraxisErrorCode;
import com.noriental.praxissvr.question.QuestionSsdbHtml;
import com.noriental.praxissvr.question.bean.*;
import com.noriental.praxissvr.question.bean.html.Audio;
import com.noriental.praxissvr.question.bean.html.ComplexQuestion;
import com.noriental.praxissvr.question.bean.html.NewMap;
import com.noriental.praxissvr.question.bean.html.SingleQuestion;
import com.noriental.praxissvr.question.bean.queueBean.QuestionQuality;
import com.noriental.praxissvr.question.common.ResultConfigEnum;
import com.noriental.praxissvr.question.dao.QuestionTopicDao;
import com.noriental.praxissvr.question.dao.QuestionTypeDao;
import com.noriental.praxissvr.question.mapper.*;
import com.noriental.praxissvr.question.request.*;
import com.noriental.praxissvr.question.response.*;
import com.noriental.praxissvr.question.service.QuestionService;
import com.noriental.praxissvr.question.service.QuestionTypeService;
import com.noriental.praxissvr.question.utils.*;
import com.noriental.praxissvr.questionSearch.util.SolrSearchUtil;
import com.noriental.publicsvr.bean.SeqNextIdListReq;
import com.noriental.publicsvr.bean.SeqNextIdReq;
import com.noriental.publicsvr.service.SequenceService;
import com.noriental.resourcesvr.common.request.IdRequest;
import com.noriental.resourcesvr.common.request.IdsRequest;
import com.noriental.resourcesvr.common.service.CounterResService;
import com.noriental.resourcesvr.customlist.request.CanUseCustomRequest;
import com.noriental.resourcesvr.customlist.service.CustomListService;
import com.noriental.resourcesvr.customlist.vo.CustomListVo;
import com.noriental.resourcesvr.enumerate.UpdateType;
import com.noriental.resourcesvr.request.CounterBatchResRequest;
import com.noriental.resourcesvr.request.CounterResRequest;
import com.noriental.resourcesvr.request.CounterResUpdateRequest;
import com.noriental.solr.bean.doc.LqResourceDocument;
import com.noriental.solr.bean.doc.QuestionDocument;
import com.noriental.solr.bean.doc.UserDocument;
import com.noriental.solr.common.exceptions.SolrSearchServiceException;
import com.noriental.solr.common.msg.SolrIndexReqMsg;
import com.noriental.solr.common.search.QueryMap;
import com.noriental.solr.common.search.SolrPage;
import com.noriental.solr.common.search.SolrQueryPageReq;
import com.noriental.solr.common.search.SolrQueryPageRsp;
import com.noriental.solr.common.utils.UUIDUtil;
import com.noriental.solr.service.search.LqResourceSolrSearchService;
import com.noriental.solr.service.search.QuestionSolrSearchService;
import com.noriental.solr.service.search.UserSolrSearchService;
import com.noriental.utils.entity.EntityUtils;
import com.noriental.utils.json.JsonUtil;
import com.noriental.utils.redis.RedisUtil;
import com.noriental.validate.bean.CommonDes;
import com.noriental.validate.bean.CommonResponse;
import com.noriental.validate.bean.LongRequest;
import com.noriental.validate.error.BasicErrorCode;
import com.noriental.validate.exception.BizLayerException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.*;
import java.util.concurrent.*;

import static com.noriental.praxissvr.exception.PraxisErrorCode.*;


/**
 * @author chenlihua
 */
@Service("quiz2.questionService")
public class QuestionServiceImpl implements QuestionService, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    static Map<String, Map<Integer, QuestionType>> questionMemTypeMap = new ConcurrentHashMap<>();

    @Resource
    private QuestionTypeDao questionTypeDao;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private EntityQuestionMapper entityQuestionMapper;
    @Resource
    private LinkQuestionTopicMapper linkQuestionTopicMapper;
    @Resource
    private LinkQuestionChapterMapper linkQuestionChapterMapper;
    @Resource
    private LinkExerciseQuestionMapper linkExerciseQuestionMapper;
    @Resource
    private RabbitTemplate solrUploadQuestionRabbitTemplate;
    @Resource
    private SequenceService sequenceService;
    @Resource
    private TeacherSpaceQuestionMapper teacherSpaceQuestionMapper;
    @Resource
    private QuestionTypeService questionTypeService;
    @Resource
    private TopicService topicService;
    @Resource
    private QuestionTopicDao questionTopicDao;
    @Resource
    private QuestionSolrSearchService questionSolrSearchService;
    @Resource
    private QiniuVoiceService qiniuVoiceService;
    @Resource
    private TeacherChapterMapper teacherChapterMapper;
    @Resource
    private LinkCustomQuestionResourceMapper linkCustomQuestionResourceMapper;
    @Resource
    private CustomListService customListService;
    @Resource
    private LqResourceSolrSearchService lqResourceSolrSearchService;
    @Resource
    private CounterResService counterResService;
    @Resource
    private UserSolrSearchService userSolrSearchService;
    @Resource
    private AuditsedSchoolMapper auditsedSchoolMapper;
    @Resource
    private QuestionSpecialMapper questionSpecialMapper;
    @Resource
    private RabbitTemplate createQuestionRecommendTemplate;
    @Resource
    private EntityCounterResMapper entityCounterResMapper;

    private ExecutorService threadPool = Executors.newFixedThreadPool(4);


    /***
     *业务：
     * 题目的知识点、章节移动更新自定义目录
     * @param request
     * @return
     */
    @Override
    public CommonResult updateCustomerDiretory(UpCusDirRequest request) {
        logger.info("开始自定义目录业务入参为==>>{}", request.toString());

        Long systemId = request.getSystemId();
        Long questionId = request.getQuestionId();
        Long cusDirId = request.getCusDirId();
        Long groupId = request.getGroupId();

        if (systemId == null || questionId == null || cusDirId == null) {
            throw new BizLayerException("", ANSWER_PARAMETER_ILL);
        }
        CanUseCustomRequest canUseCustomRequest = new CanUseCustomRequest();

        canUseCustomRequest.setGroupId(groupId);
        canUseCustomRequest.setSystemId(systemId);
        canUseCustomRequest.setCatalogId(cusDirId);
        CommonResponse<Boolean> commonResponse = customListService.canUseCustom(canUseCustomRequest);
        if (!commonResponse.getData()) {
            throw new BizLayerException("", CUS_DIR_INVALID);
        }
        /*
            判断自定义目录是否存在如果存在更新，否则更新自定义目录失败
         */
        CustomQuestionResource resource = linkCustomQuestionResourceMapper
                .queryCustomQuestionResourceByQuesIdAndSysId(questionId, systemId);
        if (resource == null) {
            throw new BizLayerException("", CUS_DIR_UPDATE_FAIL);
        }
        if (resource.getCustomListId().equals(cusDirId)){
            throw new BizLayerException("", CUS_DIR_MOVE_FAIL);
        }
        /*
         * 更新solr
         */
        EntityQuestion question = entityQuestionMapper.findQuestionById(questionId);
        CustomQuestionResource customQuestionResource = new CustomQuestionResource();
        customQuestionResource.setQuestionId(questionId);
        customQuestionResource.setCustomListId(cusDirId);
        customQuestionResource.setSystemId(systemId);
        customQuestionResource.setGroupId(groupId);
        customQuestionResource.setUpdateTime(new Date());
        customQuestionResource.setIsFav(resource.getIsFav());
      /*  customQuestionResource.setVisible(question.getVisible());
        customQuestionResource.setSubjectId(question.getSubjectId());
        customQuestionResource.setNewFormat(question.getNewFormat());*/
        int i = linkCustomQuestionResourceMapper.updateLinkCustomQuestionResource(customQuestionResource);
        if (i <= 0) {
            throw new BizLayerException("", CUS_DIR_UPDATE_FAIL);
        }
        //题集查询
        List<LinkExerciseQuestion> exerciseQuestions = linkExerciseQuestionMapper.findLinkExerciseQuestionById
                (questionId);
        //修改题目时，如果该题目属于一个题集，而且该题集被缓存了，需要在此处过期该缓存
        QuestionServiceUtil.delExerciseQuestionCache(exerciseQuestions,redisUtil);

        question.setUploadId(resource.getSystemId());
        sendLinkQuestionIndex(question, cusDirId, resource.getId(), resource.getIsFav());


        return new CommonResult();
    }

    /*
        收藏题目
     */
    @Override
    public CommonResult collectionQuestion(CollectionQuestionRequest request) {
        logger.info("开始收藏题目业务==>>入参为：{}", request);
        if (request.getCustomerDirectoryId() == null || request.getQuestionId() == null || request.getUploadId() ==
                null) {
            throw new BizLayerException("", ANSWER_PARAMETER_ILL);
        }

        CommonResponse<List<CustomListVo>> commonResponse = customListService.findParentsByLeafId(new IdRequest
                (request.getCustomerDirectoryId()));
        if (CollectionUtils.isEmpty(commonResponse.getData())) {
            throw new BizLayerException("", CUS_DIR_INVALID);
        }
        /*
            判断自定义目录和题目和上传人的关系和是否已经收藏
         */
        CustomQuestionResource resources = linkCustomQuestionResourceMapper
                .queryCustomQuestionResourceByQuesIdAndSysId(request.getQuestionId(), request.getUploadId());
        if (resources != null) {  //说明是我的题目
            throw new BizLayerException("", EXIST_COLLECTION);
        }
        EntityQuestion entityQuestion = entityQuestionMapper.findQuestionById(request.getQuestionId());
        if (Objects.equals(entityQuestion.getUploadId(), request.getUploadId())) { //如果是同一个人
            throw new BizLayerException("", EXIST_COLLECTION);
        }
        //如果是听口题也不收藏
        if (collectionsQuesTypeCondition(entityQuestion)){
            throw new BizLayerException("", EXIST_COLLECTION);
        }

        CustomQuestionResource custom = new CustomQuestionResource();
        custom.setQuestionId(request.getQuestionId());
        custom.setCustomListId(request.getCustomerDirectoryId());
        custom.setSystemId(request.getUploadId());
        custom.setResourceStatus(1);
        custom.setCreateTime(new Date());
        custom.setIsFav(1);
        custom.setGroupId(request.getGroupId());
        int i = linkCustomQuestionResourceMapper.insertLinkCustomQuestionResource(custom);
        if (i <= 0) {
            throw new BizLayerException("", QUESTION_COLLECTION_FAIL);
        }


        /*
            创建solr
         */

        entityQuestion.setUploadId(custom.getSystemId());
        logger.info("发送solr数据为:{},cusTomDirId:{}", entityQuestion.toString());
        sendLinkQuestionIndex(entityQuestion, request.getCustomerDirectoryId(), custom.getId(), 1);

        /*
            调用lesson-svr服务，统计题目收藏量
         */
        CounterResRequest counterResRequest = new CounterResRequest();
        counterResRequest.setObjectId(request.getQuestionId());
        counterResRequest.setObjectType(AppType.ResourceType.QUESTION);
        counterResRequest.setUpdateType(UpdateType.fav_count.getCode());
        counterResRequest.setUserId(request.getUploadId());
        counterResService.updateCounter(counterResRequest);

        return new CommonResult();
    }

    @Override
    public CommonResult collectionQuestions(CollectionQuestionsRequest request) throws BizLayerException {
        logger.info("开始批量收藏题目业务==>>入参为：{}", request);
        if(CollectionUtils.isEmpty(request.getCollectionQuestions())){
            throw new BizLayerException("", ANSWER_PARAMETER_ILL);
        }

        List<CounterResUpdateRequest> counterList=new ArrayList<>();
        //优化查询自定义目录接口次数
        Set<Long> customerDirectorySet=new HashSet<>();
        for(CollectionQuestion collectionQuestion:request.getCollectionQuestions()){
            customerDirectorySet.add(collectionQuestion.getCustomerDirectoryId());
        }
        if(customerDirectorySet.size()==1){
            CommonResponse<List<CustomListVo>> commonResponse = customListService.findParentsByLeafId(new IdRequest
                    (request.getCollectionQuestions().get(0).getCustomerDirectoryId()));
            if (CollectionUtils.isEmpty(commonResponse.getData())) {
                throw new BizLayerException("", CUS_DIR_INVALID);
            }
        }
        for(CollectionQuestion collectionQuestion:request.getCollectionQuestions()){
            if (collectionQuestion.getCustomerDirectoryId() == null || collectionQuestion.getQuestionId() == null || collectionQuestion.getUploadId() ==
                    null) {
                continue;
            }
            if(customerDirectorySet.size()>1){
                CommonResponse<List<CustomListVo>> commonResponse = customListService.findParentsByLeafId(new IdRequest
                        (collectionQuestion.getCustomerDirectoryId()));
                if (CollectionUtils.isEmpty(commonResponse.getData())) {
                    continue;
                }
            }
        /*
            判断自定义目录和题目和上传人的关系和是否已经收藏
         */
            CustomQuestionResource resources = linkCustomQuestionResourceMapper
                    .queryCustomQuestionResourceByQuesIdAndSysId(collectionQuestion.getQuestionId(), collectionQuestion.getUploadId());

            CounterResUpdateRequest counterResUpdateRequest=new CounterResUpdateRequest();
            EntityQuestion entityQuestion = entityQuestionMapper.findQuestionById(collectionQuestion.getQuestionId());
            //我的题目;是同一个人;听口题;这三种情况只增加引用量，不增加收藏量。
            if (collectionsQuesTypeCondition(entityQuestion)|| resources != null ||Objects.equals(entityQuestion.getUploadId(), collectionQuestion.getUploadId())) {
                counterResUpdateRequest.setObjectId(collectionQuestion.getQuestionId());
                counterResUpdateRequest.setObjectType(AppType.ResourceType.QUESTION);
                counterResUpdateRequest.setUpdateTypes(Arrays.asList(UpdateType.ref_count));
                counterResUpdateRequest.setUserId(collectionQuestion.getUploadId());
                counterList.add(counterResUpdateRequest);
                continue;
            }


            CustomQuestionResource custom = new CustomQuestionResource();
            custom.setQuestionId(collectionQuestion.getQuestionId());
            custom.setCustomListId(collectionQuestion.getCustomerDirectoryId());
            custom.setSystemId(collectionQuestion.getUploadId());
            custom.setResourceStatus(1);
            custom.setCreateTime(new Date());
            custom.setIsFav(1);
            custom.setGroupId(collectionQuestion.getGroupId());
            int i = linkCustomQuestionResourceMapper.insertLinkCustomQuestionResource(custom);
            if (i <= 0) {
                throw new BizLayerException("", QUESTION_COLLECTION_FAIL);
            }


        /*
            创建solr
         */

            entityQuestion.setUploadId(custom.getSystemId());
            logger.info("发送solr数据为:{},cusTomDirId:{}", entityQuestion.toString());
            sendLinkQuestionIndex(entityQuestion, collectionQuestion.getCustomerDirectoryId(), custom.getId(), 1);


            counterResUpdateRequest.setObjectId(collectionQuestion.getQuestionId());
            counterResUpdateRequest.setObjectType(AppType.ResourceType.QUESTION);
            counterResUpdateRequest.setUpdateTypes(Arrays.asList(UpdateType.ref_count,UpdateType.fav_count));
            counterResUpdateRequest.setUserId(collectionQuestion.getUploadId());
            counterList.add(counterResUpdateRequest);
        }
        /*
            调用lesson-svr服务，统计题目收藏量
         */
        if(CollectionUtils.isNotEmpty(counterList)){
            CounterBatchResRequest counterBatchResRequest=new CounterBatchResRequest();
            for (int i = 0; i < counterList.size()-1; i++) {
                for (int j = counterList.size()-1; j > i; j--) {
                    if (counterList.get(j).getObjectId() == counterList.get(i).getObjectId()) {
                        counterList.remove(j);
                    }
                }
            }
            counterBatchResRequest.setResRequests(counterList);
            counterResService.updateBatchResCounter(counterBatchResRequest);
        }


        return new CommonResult();
    }
    //如果是听口题也不收藏
    private boolean collectionsQuesTypeCondition(EntityQuestion entityQuestion) {
        if(entityQuestion.getQuestionTypeId()!=null){
            if(entityQuestion.getQuestionTypeId()>=55&&entityQuestion.getQuestionTypeId()<=60){
                return true;
            }
        }
        return false;
    }


    @Override
    public FindQuestionsResponse findQuestions(FindQuestionsRequest request) {
        FindQuestionsResponse resp = new FindQuestionsResponse();
        if(StringUtils.isEmpty(request.getReqId())){
            request.setReqId(TraceKeyHolder.getTraceKey());
        }
        long l = System.currentTimeMillis();
        Map<String, Object> queryConf = SolrSearchUtil.buildSolrQueryMap(request);
        long l1 = System.currentTimeMillis();
        logger.info("findQuestions:buildSolrQueryMap:cost:{}ms", l1 - l);
        logger.info("findQuestions:queryMap:{}", JsonUtil.obj2Json(queryConf));
        long querySolrStart = System.currentTimeMillis();
        SolrQueryPageRsp<QuestionDocument> search = questionSolrSearchService.search(new SolrQueryPageReq(queryConf));
        if (!search.success()) {
            throw new BizLayerException("数据查询异常|", PRAXIS_INVOKE_SOLR);
        }
        SolrPage<QuestionDocument> solrPages = search.getPage();
        long querySolrEnd = System.currentTimeMillis();
        logger.info("findQuestions:Query solr cost:{}ms", (querySolrEnd - querySolrStart));

        // 所有试题

        List<QuestionDocument> allQuestionDocuments = solrPages.getList();

        FutureTask<Map<Long, TeacherSpaceQuestion>> future = null;
        List<Long> ids = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(allQuestionDocuments)) {
            for (QuestionDocument document : allQuestionDocuments) {
                    ids.add(document.getId());
            }
            //查询题目的统计信息
            Callable<Map<Long, TeacherSpaceQuestion>> callable = new QueryQuestionReportThread(ids);
            future = new FutureTask<>(callable);
            new Thread(future).start();
        }

        long totalPage = solrPages.getTotalPage();
        long totalCount = solrPages.getTotalCount();
        long currentPage = solrPages.getCurrentPage();
        // 复合题
        List<Long> parIds = new ArrayList<>();
        if (!request.isBasic()) {
            for (QuestionDocument questionDocument : allQuestionDocuments) {
                if (questionDocument != null && BooleanUtils.isNotTrue(questionDocument.getIsSingle() != null &&
                        questionDocument.getIsSingle() == 1)) {
                    parIds.add(questionDocument.getId());
                }
            }
        }
        List<QuestionDocument> allSubQuestList = new ArrayList<>();
        if (parIds.size() > 0) {
            long l2 = System.currentTimeMillis();
            Map<String, Object> queryMap = new HashMap<>();
            Map<String, Object> temp_query_conf = new HashMap<>();
            queryMap.put(QueryMap.KEY_START, 0);
            queryMap.put(QueryMap.KEY_ROWS, parIds.size() * 20);
            temp_query_conf.put("parentQuestionId", parIds);

            if (CollectionUtils.isNotEmpty(request.getSubStates())) {
                List<String> states = new ArrayList<>();
                for (QuestionState questionState : request.getSubStates()) {
                    states.add(questionState.toString());
                }
                if (!states.contains(QuestionState.ALL.toString())) {
                    temp_query_conf.put("state", states);
                }
            } else {
                temp_query_conf.put(QueryMap.KeyPrefix.LOGIC_NOT.getValue() + "state", QuestionState.DISABLED
                        .toString());
            }

            queryMap.put("q", temp_query_conf);
            queryMap.put("sort", "id");
            // 获取复合题下的子题
            SolrQueryPageRsp<QuestionDocument> searchSub = questionSolrSearchService.search(new SolrQueryPageReq
                    (queryMap));
            if (!searchSub.success()) {
                throw new BizLayerException("查询数据异常|", PRAXIS_INVOKE_SOLR);
            }
            SolrPage<QuestionDocument> solrPagesSubQuestion = searchSub.getPage();
            allSubQuestList = solrPagesSubQuestion.getList();
            long l3 = System.currentTimeMillis();
            logger.info("findQuestions:Query sub questions cost:{}ms", l3 - l2);
        }

        List<Question> returnList = new ArrayList<>();
        try{
            if (CollectionUtils.isNotEmpty(allQuestionDocuments)) {
                Set<Long> topicIds = new HashSet<>();
                // 遍历试题
                List<Long> resultQuestionIds = new ArrayList<>();
                FindAllQuestionTypeResponse allQuestionTypes = questionTypeService.findAllQuestionType(new
                        FindAllQuestionTypeRequest());


                Map<Long, SuperQuestionSsdbHtml> mongoQuestionMap = new HashMap<>();


                for (QuestionDocument questDoc : allQuestionDocuments) {
                    String htmlData = questDoc.getHtmlData();
                    boolean illQuestionContent=questDoc.getParentQuestionId()!=null &&questDoc.getParentQuestionId().equals(0L)&& (StringUtils.isBlank(htmlData)||StringUtils.isBlank(questDoc.getJsonData()));
                    if(illQuestionContent){
                        continue;
                    }

                    // solr中试题信息
                    try {
                        Question question = EntityUtils.copyValueDeep2Object(questDoc, 1, Question.class, 1);
                        //0929新增智能批改标记
                        if(questDoc.getIntelligent()!=null){
                            question.setIntelligent(questDoc.getIntelligent());
                        }else{
                            question.setIntelligent(0);
                        }
                        //solr中存的single solrDocument存的是isSingle 数据库中存的是isSingle但在java bean中使用的single,
                        //所以从solr中取到值重新覆盖保持统一
                        question.setSingle(questDoc.getIsSingle() != null && questDoc.getIsSingle() == 1);

                        //因为QuestionDocument 和 Question 字段类型HighQual不一致
                        Integer highQual = questDoc.getHighQual();
                        question.setHighQual((highQual != null && highQual == 1) ? 1 : 0);
                        //处理历史数据，并更新类型详情字段
                        SolrSearchUtil.analyzeQuestionType(question);
                        // 题型结构
                        SolrSearchUtil.analyzeQuestionStruct(question, allQuestionTypes);

                        Map<Integer, QuestionType> supportedQuestionTypeMap=getCacheQuestionType(request.getReqId());

                        //是否是新题
                        if (null != question.getNewFormat() && question.getNewFormat() == 1 && null != question
                                .getQuestionTypeId() && !supportedQuestionTypeMap.containsKey(question.getQuestionTypeId
                                ())) {
                            question.setNewFormat(0);
                        }

                        if (!request.isBasic()) {

                            // 主题
                            if (CollectionUtils.isNotEmpty(question.getTopicId())) {
                                topicIds.addAll(question.getTopicId());
                            }

                            // 子题
                            SolrSearchUtil.analyzeQuestionSub(question, allSubQuestList, allQuestionTypes);
                        }

                    /*
                        查询自定义目录通过solr表
                        题目ID，用户ID 确定自定义目录TODO
                     */
                        Map<String, Object> qMap = new HashMap<>();
                        Map<String, Object> idsMap = new HashMap<>();
                        idsMap.put("systemId", questDoc.getUploadId());//用户ID
                        idsMap.put("questionId", questDoc.getId());//题目ID
                        idsMap.put("resourceStatus", 1);
                        qMap.put("q", idsMap);
                        qMap.put(QueryMap.KEY_ROWS, 1);
                        SolrQueryPageRsp<LqResourceDocument> solrQueryPageRsp = lqResourceSolrSearchService.search(new
                                SolrQueryPageReq(qMap));
                        if (solrQueryPageRsp.success()) {
                            SolrPage<LqResourceDocument> page = solrQueryPageRsp.getPage();
                            if (CollectionUtils.isNotEmpty(page.getList())) {
                                LqResourceDocument resourceDocument = page.getList().get(0);

                                List<Map<String, Object>> customerDirectoryList = new ArrayList<>();

                                if (resourceDocument.getCustomListId1() != null) {
                                    Map<String, Object> cusMap1 = new HashMap<>();
                                    cusMap1.put("level", 1);
                                    cusMap1.put("cusomerDeritoryId", resourceDocument.getCustomListId1());
                                    cusMap1.put("cusomerDeritoryName", resourceDocument.getCustomListName1());
                                    cusMap1.put("groupId", resourceDocument.getGroupId());
                                    customerDirectoryList.add(cusMap1);
                                }

                                if (resourceDocument.getCustomListId2() != null) {
                                    Map<String, Object> cusMap2 = new HashMap<>();
                                    cusMap2.put("level", 2);
                                    cusMap2.put("cusomerDeritoryId", resourceDocument.getCustomListId2());
                                    cusMap2.put("cusomerDeritoryName", resourceDocument.getCustomListName2());
                                    cusMap2.put("groupId", resourceDocument.getGroupId());
                                    customerDirectoryList.add(cusMap2);
                                }

                                if (resourceDocument.getCustomListId3() != null) {
                                    Map<String, Object> cusMap3 = new HashMap<>();
                                    cusMap3.put("level", 3);
                                    cusMap3.put("cusomerDeritoryId", resourceDocument.getCustomListId3());
                                    cusMap3.put("cusomerDeritoryName", resourceDocument.getCustomListName3());
                                    cusMap3.put("groupId", resourceDocument.getGroupId());
                                    customerDirectoryList.add(cusMap3);
                                }
                                question.setGroupId(resourceDocument.getGroupId());
                                question.setCustomerDirectoryIds(customerDirectoryList);
                            }
                        }

                        returnList.add(question);
                        resultQuestionIds.add(question.getId());
                    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException e) {
                        logger.error(e.getMessage(), e);
                    }

                    if(!StringUtils.isBlank(htmlData)){
                        SuperQuestionSsdbHtml superQuestionHtml = JsonUtil.readValue(htmlData, SuperQuestionSsdbHtml.class);
                        if (null==superQuestionHtml){
                            htmlData= htmlData.replaceAll("[\u0000-\u001f]", "");
                            superQuestionHtml = JsonUtil.readValue(htmlData, SuperQuestionSsdbHtml.class);
                        }
                        mongoQuestionMap.put(questDoc.getId(), superQuestionHtml);
                    }
                }
                if (!request.isBasic()) {
                    long l4 = System.currentTimeMillis();
                    Map<Long, Topic> mapTopic = new HashMap<>();
                    Map<String, Integer> mapTopicMastery = new HashMap<>();
                    SolrSearchUtil.batchQueryMapTopicAndMastery(topicService, questionTopicDao, topicIds,
                            resultQuestionIds, mapTopic, mapTopicMastery);
                    long l5 = System.currentTimeMillis();
                    logger.info("findQuestions:find topics:cost:{}ms", l5 - l4);

                    long l7 = System.currentTimeMillis();
                    logger.info("findQuestions:find all mongo HTML:cost:{}ms", l7 - l5);
                    for (Question question : returnList) {
                        SolrSearchUtil.analyzeQuestionTopic(question, mapTopic, mapTopicMastery);
                        SolrSearchUtil.analyzeQuestionMongoHtml(topicService, questionTopicDao, question,
                                mongoQuestionMap, mapTopicMastery,0);
                    }
                    long l8 = System.currentTimeMillis();
                    logger.info("findQuestions:copy groups, topics to question list:{}ms", l8 - l7);
                }
            }
        }finally {
            //移除本次的缓存
            questionMemTypeMap.remove(request.getReqId());
        }
        Map<Long, TeacherSpaceQuestion> questionReportMap = null;
        try {
            questionReportMap = future == null ? new HashMap() : future.get(1, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            logger.info("", e);
        } finally {
            if (questionReportMap == null) {
                questionReportMap = new HashMap<>();
            }
        }
        //收藏量
        List<EntityCounterResources> entityCounterResources = entityCounterResMapper.find(ids);
        for (Question question : returnList) {
            TeacherSpaceQuestion teacherSpaceQuestion = questionReportMap.get(question.getId());
            if (teacherSpaceQuestion != null) {
                //question.setQuoteNum(teacherSpaceQuestion.getQuoteNum());
                question.setSubmitNum(teacherSpaceQuestion.getSubmitNum());
                question.setAccuracy(teacherSpaceQuestion.getAccuracy());
            }

            getQuestionShareNum(entityCounterResources, question);
        }

        resp.setCurrentPage(currentPage);
        resp.setTotalPageCount(totalPage);
        resp.setTotalCount(totalCount);
        resp.setQuestionList(returnList);
        return resp;
    }

    private void getQuestionShareNum(List<EntityCounterResources> entityCounterResources, Question question) {
        if(CollectionUtils.isNotEmpty(entityCounterResources)){
            for(EntityCounterResources entityCounterResource:entityCounterResources){
                if(question.getId()==entityCounterResource.getObjectId()){
                    question.setCollectionNum(entityCounterResource.getFavCount());
                    if(entityCounterResource.getRefCount()!=null){
                        question.setQuoteNum(Integer.parseInt(entityCounterResource.getRefCount().toString()));
                    }
                }
            }
        }
    }


    /***
     * 通过solr查询习题列表信息详情，如果习题为复合题则查询对应的子题详情
     * @param request request
     * @return 结果TODO
     */
    @Override
    public FindQuestionsByIdsResponse findQuestionsByIds(FindQuestionsByIdsRequest request) {
        FindQuestionsByIdsResponse resp = new FindQuestionsByIdsResponse();
        try {
            Map<String, Object> query_conf = new HashMap<>();
            Map<String, Object> qMap = new HashMap<>();
            List<Long> questionIds = request.getQuestionIds();
            List<Long> quizIds = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(questionIds)) {
                for (Long questionId : questionIds) {
                    if (questionId != null) {
                        quizIds.add(questionId);
                    }
                }
            }

            if (CollectionUtils.isEmpty(quizIds)) {
                throw new BizLayerException("quizIds is empty.", BasicErrorCode.BASIC_INPUT_PARAM_ERROR);
            }
            qMap.put("id", quizIds);
            query_conf.put("q", qMap);
            query_conf.put(QueryMap.KEY_ROWS, quizIds.size());
            SolrQueryPageRsp<QuestionDocument> search = questionSolrSearchService.search(new SolrQueryPageReq
                    (query_conf));
            if (!search.success()) {
                throw new BizLayerException("查询数据异常|", PRAXIS_INVOKE_SOLR);
            }
            SolrPage<QuestionDocument> solrPages = search.getPage();
            if (null != solrPages && CollectionUtils.isNotEmpty(solrPages.getList())) {
                List<Question> questionList = new ArrayList<>();
                Map<Long, Question> questionMap = new HashMap<>();
                FindAllQuestionTypeResponse response = questionTypeService.findAllQuestionType(new
                        FindAllQuestionTypeRequest());
                for (QuestionDocument doc : solrPages.getList()) {
                    Question question = EntityUtils.copyValueDeep2Object(doc, 1, Question.class, 1);
                    SolrSearchUtil.analyzeQuestionStruct(question, response);
                    if (CollectionUtils.isNotEmpty(doc.getAllLeafQuesIds()) && request.isRecursive()) {
                        List<Question> sonList = new ArrayList<>();
                        List<Long> leafQuestIds = question.getAllLeafQuesIds();
                        if (null != leafQuestIds) {
                            for (Long leafId : leafQuestIds) {
                                sonList.add(getQuestionInfoById(leafId));
                            }
                        }
                        question.setSubQuestions(sonList);
                    }
                    if (doc.getIsSingle() != null && doc.getIsSingle() == 1) {
                        question.setSingle(true);
                    }
                    questionMap.put(doc.getId(), question);
                }
                for (Long questionId : quizIds) {
                    Question q = questionMap.get(questionId);
                    if (q == null) {
                        logger.warn("题目[{}]solr index 不存在，忽略", questionId);
                        continue;
                    }
                    questionList.add(q);
                }
                resp.setQuestionList(questionList);
            }
        } catch (Exception e) {
            logger.error("", e);
            throw new BizLayerException("", e, BasicErrorCode.UNKNOWN);
        }
        return resp;
    }

    /***
     * 根据习题ID列表通过solr查询习题详情，单题直接返回solr查询结果，复合题通过组装子题返回习题列表
     * @param request 参数
     * @return 结果
     */
    @Override
    public FindQuestionsByIdsResponse findQuestionsByIdsFast(FindQuestionsByIdsRequest request) {
        FindQuestionsByIdsResponse resp = new FindQuestionsByIdsResponse();
        logger.info("findQuestionsByIdsFast入参是：{}",request);
        try {
            Map<String, Object> query_conf = new HashMap<>();
            Map<String, Object> qMap = new HashMap<>();
            List<Long> questionIds = request.getQuestionIds();
            List<Long> quizIds = new ArrayList<>(new HashSet<>(questionIds));

            if (request.isRecursive()) {//大题id和小题id一起查询
                Map<String, Object> allidsMap = new HashMap<>();
                allidsMap.put("parentQuestionId", quizIds);
                Map<String, Object> allidsMap1 = new HashMap<>();
                allidsMap1.put("id", quizIds);
                qMap.put("allidsMapList", Arrays.asList(allidsMap, allidsMap1));
                query_conf.put(QueryMap.KEY_ROWS, quizIds.size() * 21);//一个大题最多有20个小题
            } else {
                qMap.put("id", quizIds);
                query_conf.put(QueryMap.KEY_ROWS, quizIds.size()+1);
            }
            query_conf.put("q", qMap);
            SolrQueryPageRsp<QuestionDocument> search = questionSolrSearchService.search(new SolrQueryPageReq
                    (query_conf));
            if (!search.success()) {
                throw new BizLayerException("查询数据异常|", PRAXIS_INVOKE_SOLR);
            }
            SolrPage<QuestionDocument> solrPages = search.getPage();
            if (null != solrPages && CollectionUtils.isNotEmpty(solrPages.getList())) {
                FindAllQuestionTypeResponse response = questionTypeService.findAllQuestionType(new
                        FindAllQuestionTypeRequest());
                List<Question> singleOrParentQuestionList = new ArrayList<>();
                Map<Long, Question> singleOrParentQuestionMap = new HashMap<>();//判断题目是否存在
                Map<Long, List<Question>> parentSubQuestionMap = new HashMap<>();
                for (QuestionDocument doc : solrPages.getList()) {
                    Question question = EntityUtils.copyValueDeep2Object(doc, 1, Question.class, 1);
                    SolrSearchUtil.analyzeQuestionStruct(question, response);
                    if (doc.getParentQuestionId() != null && doc.getParentQuestionId().equals(0L)) {//单题或者复合题
                        singleOrParentQuestionList.add(question);
                        singleOrParentQuestionMap.put(question.getId(), question);
                    } else if (doc.getParentQuestionId() != null && !doc.getParentQuestionId().equals(0L)) {//子题
                        List<Question> subQues = parentSubQuestionMap.get(doc.getParentQuestionId());
                        if (subQues == null) {
                            subQues = new ArrayList<>();
                            parentSubQuestionMap.put(doc.getParentQuestionId(), subQues);
                        }
                        subQues.add(question);
                    } else {
                        logger.warn("parentQuestionId is null,{}", doc.getId());
                    }
                    if (doc.getIsSingle() != null && doc.getIsSingle() == 1) {
                        question.setSingle(true);
                    }
                }
                //设置复合题的子题
                for (Question q : singleOrParentQuestionList) {
                    q.setSubQuestions(parentSubQuestionMap.get(q.getId()));
                }
                for (Long questionId : quizIds) {
                    Question q = singleOrParentQuestionMap.get(questionId);
                    if (q == null) {
                        logger.warn("题目[{}]solr index 不存在，忽略", questionId);
                    }
                }
                resp.setQuestionList(singleOrParentQuestionList);
            }
        } catch (Exception e) {
            logger.error("", e);
            throw new BizLayerException("", e, BasicErrorCode.UNKNOWN);
        }
        return resp;
    }

    @Override
    public FindQuestionByIdResponse findQuestionById(FindQuestionByIdRequest request) {
        FindQuestionByIdResponse resp = new FindQuestionByIdResponse();

        FindQuestionsRequest req = new FindQuestionsRequest();
        req.setPageNo(1);
        req.setPageSize(1);
        long questionId = request.getQuestionId();
        req.setIds(Collections.singletonList(questionId));
        req.setQuestionType(QuestionTypeEnum.ALL);
        req.setStates(Collections.singletonList(QuestionState.ALL));
        req.setBasic(request.isBasic());
        req.setSubStates(request.getSubStates());
        FindQuestionsResponse response = findQuestions(req);
        if (!resp.success()) {
            throw new BizLayerException(response.getMessage(), PRAXIS_QUESTION_NOT_FOUND);
        }
        if (CollectionUtils.isEmpty(response.getQuestionList())) {
            throw new BizLayerException(String.format("[questionId:%s] not exist.", questionId),
                    PRAXIS_QUESTION_NOT_FOUND);
        }
        resp.setQuestion(response.getQuestionList().get(0));

        return resp;
    }

    /***
     * 通过solr分页查询单题和复合体大题信息列表    17/12/13标注  接口是不是存在问题paperId
     * @param request req
     * @return 结果
     */
    @Override
    public FindPaperByPaperIdResponse findPaperByPaperId(FindPaperByPaperIdRequest request) {
        FindPaperByPaperIdResponse resp = new FindPaperByPaperIdResponse();
        try {
            Map<String, Object> query_conf = new HashMap<>();
            Map<String, Object> qMap = new HashMap<>();
            long paperId = request.getPaperId();
            qMap.put("paperId", paperId);
            query_conf.put("q", qMap);
            query_conf.put("parentQuestionId", 0);
            query_conf.put(QueryMap.KEY_ROWS, 100);
            SolrPage<QuestionDocument> solrPages = questionSolrSearchService.search(new SolrQueryPageReq(query_conf))
                    .getPage();
            if (null != solrPages && CollectionUtils.isNotEmpty(solrPages.getList())) {
                List<Question> questionList = new ArrayList<>();
                FindAllQuestionTypeResponse response = questionTypeService.findAllQuestionType(new
                        FindAllQuestionTypeRequest());
                for (QuestionDocument doc : solrPages.getList()) {
                    Question question = EntityUtils.copyValueDeep2Object(doc, 1, Question.class, 1);
                    SolrSearchUtil.analyzeQuestionStruct(question, response);
                    questionList.add(question);
                }
                resp.setPaperId(paperId);
                resp.setQuestionList(questionList);
            }
            resp.setCode(0);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            resp.setCode(BasicErrorCode.UNKNOWN.getValue());
        }
        return resp;
    }

    /**
     * 获取试题信息，包含复合题的子题信息
     *
     * @param id id
     * @return question
     */
    private Question getQuestionInfoById(Long id) {
        Question newQuestion = null;
        QuestionDocument questDoc = getSolrQuestionByQuestionId(id);
        if (null != questDoc) {
            try {
                newQuestion = EntityUtils.copyValueDeep2Object(questDoc, 1, Question.class, 1);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
                logger.error("", e);
                throw new BizLayerException("", PraxisErrorCode.INNER_ERROR);
            }
            FindAllQuestionTypeResponse response = questionTypeService.findAllQuestionType(new
                    FindAllQuestionTypeRequest());
            SolrSearchUtil.analyzeQuestionStruct(newQuestion, response);
            if (!newQuestion.isSingle()) {
                List<Long> subQuestIds = newQuestion.getAllLeafQuesIds();
                if (null != subQuestIds) {
                    List<Question> subQuestList = new ArrayList<>();
                    for (Long subQuestId : subQuestIds) {
                        subQuestList.add(getQuestionInfoById(subQuestId));
                    }
                    newQuestion.setSubQuestions(subQuestList);
                }
            }
        }
        return newQuestion;
    }

    /**
     * 根据试题获取solr中试题的基本信息
     *
     * @param questionId questionId
     * @return questionDocument
     */
    private QuestionDocument getSolrQuestionByQuestionId(Long questionId) {
        Map<String, Object> query_conf = new HashMap<>();
        Map<String, Object> qMap = new HashMap<>();
        qMap.put("id", questionId);
        query_conf.put("q", qMap);
        query_conf.put(QueryMap.KEY_ROWS, 1);
        SolrPage<QuestionDocument> solrPages = questionSolrSearchService.search(new SolrQueryPageReq(query_conf))
                .getPage();
        QuestionDocument tempDoc = null;
        if (null != solrPages && CollectionUtils.isNotEmpty(solrPages.getList())) {
            tempDoc = solrPages.getList().get(0);
        }
        return tempDoc;
    }

    @Override
    public FindFacetQuestionCountResponse findFacetQuestionCount1(FindFacetQuestionCount1Request request) throws
            BizLayerException {
        QueryMap queryMap = QueryMap.build().buildQMap().putRows(0).putQ("subjectId", request.getSubjectId()).putQ
                ("state", "ENABLED").putQ("newFormat", "1").putQ("parentQuestionId", 0).putQ("visible", request
                .getVisible().getSolrValue()).putFacetLimit(0).putFacet(true);
        if (request.getQuesSearchPerm() != null) {
            List<Map<String, Object>> searchPermList = SolrSearchUtil.getSearchPerm(request.getQuesSearchPerm());
            try {
                queryMap.getQMap().put("xxxxOrSearchKey", searchPermList);
            } catch (Exception e) {
                logger.error("", e);
            }
        }
        if (request.isSameFilter()) {
            queryMap.putQ("sameNum", "[* TO -1]");
        }
        List<Long> chapterIds = request.getChapterIds();

        List<Long> moduleIds = request.getModuleIds();
        List<Long> unitIds = request.getUnitIds();
        List<Long> topicIds = request.getTopicIds();


        List<String> allMUTIds = new ArrayList<>();
        SolrSearchUtil.setAllMUTIds(allMUTIds, moduleIds, "M");
        SolrSearchUtil.setAllMUTIds(allMUTIds, unitIds, "U");
        SolrSearchUtil.setAllMUTIds(allMUTIds, topicIds, "T");

        if (CollectionUtils.isEmpty(allMUTIds) && CollectionUtils.isEmpty(chapterIds)) {
            throw new BizLayerException("不合法的统计字段 ", BasicErrorCode.BASIC_INPUT_PARAM_ERROR);
        }
        queryMap.buildFacetQueryMap();
        if (CollectionUtils.isNotEmpty(allMUTIds)) {
            queryMap.addFacetQuery("allMUTIds", allMUTIds);
        }
        if (CollectionUtils.isNotEmpty(chapterIds)) {
            queryMap.addFacetQuery("chapterId", chapterIds);
        }

        FindFacetQuestionCountResponse resp = new FindFacetQuestionCountResponse();
        logger.debug("query map:{}", queryMap);
        SolrQueryPageRsp<QuestionDocument> search = questionSolrSearchService.search(new SolrQueryPageReq(queryMap));
        if (!search.success()) {
            throw new BizLayerException("查询数据异常|", PRAXIS_INVOKE_SOLR);
        }
        SolrPage<QuestionDocument> solrPage = search.getPage();
        Map<String, Integer> facetQuery = solrPage.getFacetQuery();
        if (facetQuery != null && !facetQuery.isEmpty()) {
            Map<Long, Long> chapterFacet = resp.getChapterFacet();
            Map<Long, Long> moduleFacet = resp.getModuleFacet();
            Map<Long, Long> unitFacet = resp.getUnitFacet();
            Map<Long, Long> topicFacet = resp.getTopicFacet();
            for (Map.Entry<String, Integer> entry : facetQuery.entrySet()) {
                String queryKey = entry.getKey();
                Integer count = facetQuery.get(queryKey);
                long contLong = (long) count;
                if (StringUtils.startsWith(queryKey, "allMUTIds:M")) {
                    moduleFacet.put(Long.valueOf(StringUtils.substringAfter(queryKey, "allMUTIds:M")), contLong);
                } else if (StringUtils.startsWith(queryKey, "allMUTIds:U")) {
                    unitFacet.put(Long.valueOf(StringUtils.substringAfter(queryKey, "allMUTIds:U")), contLong);
                } else if (StringUtils.startsWith(queryKey, "allMUTIds:T")) {
                    topicFacet.put(Long.valueOf(StringUtils.substringAfter(queryKey, "allMUTIds:T")), contLong);
                } else if (StringUtils.startsWith(queryKey, "chapterId:")) {
                    chapterFacet.put(Long.valueOf(StringUtils.substringAfter(queryKey, "chapterId:")), contLong);
                }
            }
        }

        return resp;
    }

    /***
     * 通过solr查询复合题列表下所有的子题ID
     * @param request req
     * @return 结果
     * @throws BizLayerException 异常
     */
    @Override
    public FindLeafQuesIdsByParentIdsResponse findLeafQuesIdsByParentIds(FindLeafQuesIdsByParentIdsRequest request)
            throws BizLayerException {
        FindLeafQuesIdsByParentIdsResponse response = new FindLeafQuesIdsByParentIdsResponse();

        List<Long> questionIds = request.getParentQuesIds();

        Map<String, Object> query_conf = new HashMap<>();
        Map<String, Object> qMap = new HashMap<>();
        qMap.put("id", questionIds);
        query_conf.put("q", qMap);
        query_conf.put(QueryMap.KEY_ROWS, questionIds.size());
        try {
            SolrQueryPageRsp<QuestionDocument> search = questionSolrSearchService.search(new SolrQueryPageReq
                    (query_conf));
            if (!search.success()) {
                throw new BizLayerException("查询数据异常|", PRAXIS_INVOKE_SOLR);
            }
            SolrPage<QuestionDocument> solrPages = search.getPage();
            if (null != solrPages && CollectionUtils.isNotEmpty(solrPages.getList())) {
                List<Long> sonList = new ArrayList<>();
                for (QuestionDocument doc : solrPages.getList()) {
                    Question question = EntityUtils.copyValueDeep2Object(doc, 1, Question.class, 1);
                    List<Long> leafQuestIds = question.getAllLeafQuesIds();
                    if (null != leafQuestIds) {
                        sonList.addAll(leafQuestIds);
                    }
                }

                response.setLeafQuesIds(sonList);
                return response;
            }
        } catch (Exception e) {
            throw new BizLayerException("", e, BasicErrorCode.UNKNOWN);
        }
        response.setCode(PraxisErrorCode.PRAXIS_QUESTION_NOT_FOUND.getValue());
        response.setMessage(PraxisErrorCode.PRAXIS_QUESTION_NOT_FOUND.getComment());
        return response;
    }

    /***
     * 通过solr查询复合题列表下所有的子题ID 以key和valueList的形式返回
     * @param request 参数
     * @return 结果
     * @throws BizLayerException 异常
     */
    @Override
    public FindParentIdLeafQuesIdsMapResponse findParentIdLeafQuesIdsMap(FindLeafQuesIdsByParentIdsRequest request)
            throws BizLayerException {
        FindParentIdLeafQuesIdsMapResponse response = new FindParentIdLeafQuesIdsMapResponse();
        Map<Long, List<Long>> parentIdLeafQuesIdsMap = new HashMap<>();
        List<Long> questionIds = request.getParentQuesIds();

        Map<String, Object> query_conf = new HashMap<>();
        Map<String, Object> qMap = new HashMap<>();
        qMap.put("id", questionIds);
        query_conf.put("q", qMap);
        query_conf.put(QueryMap.KEY_ROWS, questionIds.size());
        try {
            SolrQueryPageRsp<QuestionDocument> search = questionSolrSearchService.search(new SolrQueryPageReq
                    (query_conf));
            if (!search.success()) {
                throw new BizLayerException("查询数据异常|", PRAXIS_INVOKE_SOLR);
            }
            SolrPage<QuestionDocument> solrPages = search.getPage();
            if (null != solrPages && CollectionUtils.isNotEmpty(solrPages.getList())) {
                for (QuestionDocument doc : solrPages.getList()) {
                    Question question = EntityUtils.copyValueDeep2Object(doc, 1, Question.class, 1);
                    List<Long> leafQuestIds = question.getAllLeafQuesIds();
                    if (null != leafQuestIds) {
                        for (Long leafId : leafQuestIds) {
                            List<Long> sonList = parentIdLeafQuesIdsMap.get(question.getId());
                            if (sonList == null) {
                                sonList = new ArrayList<>();
                                parentIdLeafQuesIdsMap.put(question.getId(), sonList);
                            }
                            sonList.add(leafId);
                        }
                    }
                }

                response.setParentIdLeafQuesIdsMap(parentIdLeafQuesIdsMap);
                return response;
            }
        } catch (Exception e) {
            throw new BizLayerException("", e, BasicErrorCode.UNKNOWN);
        }
        response.setCode(PraxisErrorCode.PRAXIS_QUESTION_NOT_FOUND.getValue());
        response.setMessage(PraxisErrorCode.PRAXIS_QUESTION_NOT_FOUND.getComment());
        return response;
    }

    @Override
    public CheckoutNewFormatAndTrunkResponse checkoutNewFormatAndTrunk(CheckoutNewFormatAndTrunkRequest request) {
        List<Long> returnList = new ArrayList<>();
        Map<String, Object> qMap = new HashMap<>();
        qMap.put("id", request.getIds());
        qMap.put("newFormat", 1);
        qMap.put("parentQuestionId", 0);
        SolrSearchUtil.setQuestionStates(request.getStates(), qMap);

        Map<String, Object> queryConf = new HashMap<>();
        queryConf.put("q", qMap);
        queryConf.put("fl", "id");
        queryConf.put(QueryMap.KEY_ROWS, request.getIds().size());
        SolrPage<QuestionDocument> solrPages;
        try {
            long querySolrStart = System.currentTimeMillis();
            SolrQueryPageRsp<QuestionDocument> search = questionSolrSearchService.search(new SolrQueryPageReq
                    (queryConf));
            if (!search.success()) {
                throw new BizLayerException(search.getMessage(), PRAXIS_INVOKE_SOLR);
            }
            solrPages = search.getPage();
            long querySolrEnd = System.currentTimeMillis();
            logger.debug("checkoutNewFormatAndTrunk: Query solr cost:{}ms", (querySolrEnd - querySolrStart));
            if (solrPages != null) {
                List<QuestionDocument> list = solrPages.getList();
                if (CollectionUtils.isNotEmpty(list)) {
                    Set<Long> ids = new HashSet<>();
                    for (QuestionDocument document : list) {
                        ids.add(document.getId());
                    }

                    for (Long aLong : request.getIds()) {
                        if (ids.contains(aLong)) {
                            returnList.add(aLong);
                        }
                    }

                }
            }
        } catch (SolrSearchServiceException e) {
            throw new BizLayerException(e, PRAXIS_INVOKE_SOLR);
        }
        CheckoutNewFormatAndTrunkResponse resp = new CheckoutNewFormatAndTrunkResponse();
        resp.setIds(returnList);
        return resp;
    }


    @Override
    public FindPadQuestionListByQuestionIdsResponse findPadQuestionListByQuestionIds
            (FindPadQuestionListByQuestionIdsRequest request) {
        long s = System.currentTimeMillis();
        FindPadQuestionListByQuestionIdsResponse resp = new FindPadQuestionListByQuestionIdsResponse();
        String key = "PAD_QUIZ_IDS3_" + request.getQuestionIds().toString();
        if(StringUtils.isEmpty(request.getReqId())){
            request.setReqId(TraceKeyHolder.getTraceKey());
        }
        try {
            // long l = System.currentTimeMillis();
            // Object o = redisUtil.get(key);
            // long l1 = System.currentTimeMillis();
            // logger.info("get {} from redis cost:{}ms", key, l1 - l);
            // if (o != null) {
            //     logger.info("find Pad quiz list from redis:{}", key);
            //   resp.setQuestionList((List<SuperQuestion>) o);
            // } else {
            logger.info("find Pad quiz list from solr:{}", key);
            //根据题集ID 查缓存
            List<SuperQuestion> questions;
            final String cacheKey = RedisKeyUtil.makeKey(RedisKeyUtil.GET_PAD_QUESTIONS, request.getExerciseId()+"");
            if(request.getExerciseId()!=null){
                if(redisUtil.exists(cacheKey)){
                    //如果已缓存，则直接根据key获取
                    questions=(List<SuperQuestion>)redisUtil.get(cacheKey);
                    if(CollectionUtils.isEmpty(questions)){
                        questions = getQuestions(request.getQuestionIds(),request.getReqId(),request.getExerciseId(),cacheKey);
                    }
                }else{
                    questions = getQuestions(request.getQuestionIds(),request.getReqId(),request.getExerciseId(),cacheKey);
                }
            }else{
                questions = getQuestions(request.getQuestionIds(),request.getReqId(),request.getExerciseId(),cacheKey);
            }
            resp.setQuestionList(questions);
            // redisUtil.set(key, questions, 45 * 60);
            //}
        } catch (Exception e) {
            logger.error("数据异常：", e);
            throw new BizLayerException("", PRAXIS_QUESTION_NOT_FOUND);
        }finally {
            //移除本次的缓存
            questionMemTypeMap.remove(request.getReqId());
        }
        logger.info("findPadQuestionListByQuestionIds cost time" + (System.currentTimeMillis() - s));
        return resp;
    }

    /**
     * 试题处理
     *
     * @param questionIds questionIds
     * @return superQuestion
     */

    @SuppressWarnings("unchecked")
    private List<SuperQuestion> getQuestions(List<Long> questionIds,String reqId,Long exerciseId,String cacheKey) {
        List<SuperQuestion> questionList = new ArrayList<>();
        Map<Long, SuperQuestion> map = new HashMap<>();

        // try {
        Map<String, Object> qMap = new HashMap<>();
        Map<String, Object> idsMap = new HashMap<>();
        idsMap.put("id", questionIds);
        qMap.put("q", idsMap);
        qMap.put(QueryMap.KEY_ROWS, questionIds.size());
        long l = System.currentTimeMillis();
        SolrPage<QuestionDocument> questionSolrPage = questionSolrSearchService.search(new SolrQueryPageReq(qMap))
                .getPage();
        long l1 = System.currentTimeMillis();
        logger.info("pad query quiz from solr cost:{}ms", l1 - l);


        if (questionSolrPage != null && CollectionUtils.isNotEmpty(questionSolrPage.getList())) {
            List<Long> parentQuestionIds = new ArrayList<>();
            for (QuestionDocument questionDocument : questionSolrPage.getList()) {
                parentQuestionIds.add(questionDocument.getId());
            }
            // 获取solr中的试题信息
            Map<String, Object> queryMap = new HashMap<>();
            Map<String, Object> idMap = new HashMap<>();
            idMap.put("parentQuestionId", parentQuestionIds);
            queryMap.put("q", idMap);
            queryMap.put("sort", "id");
            queryMap.put(QueryMap.KEY_ROWS, 20 * parentQuestionIds.size());
            // 获取所有子题信息
            long l2 = System.currentTimeMillis();
            SolrQueryPageRsp<QuestionDocument> search = questionSolrSearchService.search(new SolrQueryPageReq
                    (queryMap));
            long l3 = System.currentTimeMillis();
            logger.info("pad query sub quiz from solr cost:{}ms", l3 - l2);
            if (!search.success()) {
                throw new BizLayerException("数据查询异常|", PRAXIS_INVOKE_SOLR);
            }
            SolrPage<QuestionDocument> subQuestionSolrPage = search.getPage();
            Map<Long, List<QuestionDocument>> subQuizMap = new HashMap<>();
            if (subQuestionSolrPage != null) {
                for (QuestionDocument questionDocument : subQuestionSolrPage.getList()) {
                    Long parentQuestionId = questionDocument.getParentQuestionId();
                    List<QuestionDocument> subList = subQuizMap.get(parentQuestionId);
                    if (subList == null) {
                        subList = new ArrayList<>();
                        subQuizMap.put(parentQuestionId, subList);
                    }
                    subList.add(questionDocument);
                }
            }
            Map<Long, QuestionDocument> tempDocMap = new HashMap<>();

            Map<Long, SuperQuestionSsdb> mongoMap = new HashMap<>();



            for (QuestionDocument questionDoc : questionSolrPage.getList()) {
                if(StringUtils.isBlank(questionDoc.getHtmlData())||StringUtils.isBlank(questionDoc.getJsonData())){
                    continue;
                }

                tempDocMap.put(questionDoc.getId(), questionDoc);
                // 获取solr中的试题信息
                SuperQuestion question = new SuperQuestion();
                Long questionId = questionDoc.getId();
                question.setId(questionId);
                Integer questionTypeId = questionDoc.getQuestionTypeId();
                try {
                    long structIdByTypeId = findStructIdByTypeId(questionTypeId,reqId);
                    question.setStructId(structIdByTypeId);
                } catch (Exception e) {
                    String msg = String.format("题目[%d], 题目类型[%s], 不合法 ", questionId, questionTypeId);
                    logger.error(msg, e);
                    throw new BizLayerException(msg, ANSWER_RECORD_QUES_TYPE_WRONG);
                }

                analyzeNewFormat(questionDoc, question);
                // 试题难度：1易，2中，3难，4极难
                question.setLevel(getDifficulty(questionDoc.getDifficulty()));
                // 主题
                List<Long> topicIds = questionDoc.getTopicId();
                List<String> topicNames = questionDoc.getTopicName();
                question.setTopicIds(topicIds);
                question.setTopicNames(topicNames);
                question.setChapterIds(getLeafChapterIds(questionDoc.getChapterId1(), questionDoc.getChapterId2(),
                        questionDoc.getChapterId3()));
                if(CollectionUtils.isNotEmpty(question.getChapterIds())){
                    Long charpterId= question.getChapterIds().get(0);
                    List<Long> chapterInnerIds=new ArrayList<>();
                    chapterInnerIds.add(charpterId);
                    question.setChapterIds(chapterInnerIds);
                    //question.setChapterIds(question.getChapterIds().subList(0,1));
                }
                Long subjectId = questionDoc.getSubjectId();
                question.setSubjectId(subjectId != null ? subjectId : 0);
                if (CollectionUtils.isNotEmpty(topicIds) && CollectionUtils.isNotEmpty(topicNames)) {
                    //
                    List<Map<String, Object>> themeList = new ArrayList<>();
                    int topicNameSize = topicNames.size();
                    for (int i = 0; i < topicIds.size(); i++) {
                        Map<String, Object> themeMap = new HashMap<>();
                        themeMap.put("id", topicIds.get(i));
                        if (i < topicNameSize) {
                            themeMap.put("name", topicNames.get(i));
                        } else {
                            themeMap.put("name", "");
                            logger.warn("questionId topic count not same:" + questionId);
                        }
                        themeList.add(themeMap);
                    }
                    question.setTheme(themeList);
                }


                Map<String, Object> typeMap = getQuestionType(questionTypeId,reqId);
                question.setTopic_type(typeMap);
                // 选择题答案个数
                Integer answerNum = questionDoc.getAnswerNum();
                question.setAnswer_num(answerNum);


                setChoiceAbout(question, questionTypeId, answerNum, typeMap);

                // 是否为单题
                boolean isSingle = (questionDoc.getIsSingle() != null && questionDoc.getIsSingle() == 1);
                question.setIsSingle(isSingle);
                //智能批改
                if(questionDoc.getIntelligent()!=null){
                    question.setIntelligent(questionDoc.getIntelligent());
                }else{
                    question.setIntelligent(0);
                }
                question.setHtmlData(questionDoc.getHtmlData());
                if(!StringUtils.isEmpty(questionDoc.getJsonData())){
                    question.setJsonData(questionDoc.getJsonData().replace("\"image\", \"value\": \"//","\"image\", \"value\": \"https://").replace("\"image\", \"value\":\"//","\"image\", \"value\": \"https://").replace("\"url\": \"//","\"url\": \"https://").replace("\"url\":\"//","\"url\": \"https://"));
                }



                map.put(question.getId(), question);
                //                questionList.add(question);

                String jsonData = questionDoc.getJsonData();
                if(!StringUtils.isEmpty(jsonData)){
                    jsonData=jsonData.replace("\"image\", \"value\": \"//","\"image\", \"value\": \"https://").replace("\"image\", \"value\":\"//","\"image\", \"value\": \"https://").replace("\"url\": \"//","\"url\": \"https://").replace("\"url\":\"//","\"url\": \"https://");
                }


                /*
                    json解析错误
                 */
                SuperQuestionSsdb content = JsonUtil.readValue(jsonData, SuperQuestionSsdb.class);
                //对线上存在内容中包含unicode码的数据进行替换
                if (null==content){
                    jsonData= jsonData.replaceAll("[\u0000-\u001f]", "");
                    content = JsonUtil.readValue(jsonData, SuperQuestionSsdb.class);
                }
                mongoMap.put(question.getId(), content);

            }

            if (!mongoMap.isEmpty()) {
                Set<Map.Entry<Long, SuperQuestionSsdb>> entries = mongoMap.entrySet();

                for (Map.Entry<Long, SuperQuestionSsdb> entry : entries) {
                    Long questionId = entry.getKey();
                    SuperQuestionSsdb questionMongoObj = mongoMap.get(questionId);
                    if (null == questionMongoObj) {
                        logger.error("不存在此试题。。。。。。。。。。。。。。。questionId:{}", questionId);
                        continue;
                    }
                    SuperQuestionMongoContent questionMongoObjContent = questionMongoObj.getContent();
                    if (null == questionMongoObjContent) {
                        logger.error("试题content字段为null。。。。。。。。。。。。questionId:{}", questionId);
                        continue;
                    }
                    SuperQuestion question = map.get(questionId);
                    QuestionDocument questionDoc = tempDocMap.get(questionId);
                    if (question.getIsSingle()) {
                        SingleQuestionContent singleContent = new SingleQuestionContent();
                        singleContent.setBody(questionMongoObjContent.getBody());
                        singleContent.setInterpret(questionMongoObjContent.getTranslation());
                        singleContent.setImage(questionMongoObjContent.getImage());
                        singleContent.setModel_essay(questionMongoObjContent.getModel_essay());
                        singleContent.setAnswerNum(questionDoc.getAnswerNum());
                        singleContent.setOptions(questionMongoObjContent.getOptions());
                        singleContent.setAnswer(questionMongoObjContent.getAnswer());
                        singleContent.setAnalysis(questionMongoObjContent.getAnalysis());
                        singleContent.setPrompt(questionMongoObjContent.getPrompt());
                        singleContent.setBase_image(questionMongoObjContent.getMap());
                        singleContent.setThird_party_use(questionMongoObjContent.getThird_party_use());
                        singleContent.setAudio(questionMongoObjContent.getAudio());
                        question.setContent(singleContent);
                    } else {
                        // 复合题
                        MultiQuestionContent multiContent = new MultiQuestionContent();
                        multiContent.setSource(questionMongoObjContent.getSource());
                        multiContent.setInterpret(questionMongoObjContent.getInterpret());
                        multiContent.setMaterial(questionMongoObjContent.getMaterial());
                        multiContent.setTranslation(questionMongoObjContent.getTranslation());
                        multiContent.setAudio(questionMongoObjContent.getAudio());

                        List<SuperQuestion> subList = new ArrayList<>();

                        if (CollectionUtils.isNotEmpty(subQuizMap.get(questionId))) {
                            // mongo中子题信息
                            List<SuperQuestionMongoContent> quests = questionMongoObjContent.getQuestions();
                            // logger.debug("solr子题信息===>123:"+JsonUtil.obj2Json(quests));
                            // solr中子题信息
                            List<QuestionDocument> docs = subQuizMap.get(questionId);
                            int count = docs.size() > quests.size() ? quests.size() : docs.size();
                            for (int i = 0; i < count; i++) {
                                // mongo中试题信息
                                SuperQuestionMongoContent questMongoContent = quests.get(i);
                                if (null == questMongoContent) {
                                    continue;
                                }
                                // solr中试题信息
                                QuestionDocument doc = docs.get(i);
                                boolean flag = BooleanUtils.isTrue(doc.getIsSingle() != null && doc.getIsSingle() == 1);
                                SuperQuestion subQuestion = new SuperQuestion();
                                subQuestion.setStructId(findStructIdByTypeId(doc.getQuestionTypeId(),reqId));

                                if (flag) {
                                    SingleQuestionContent singleContent = new SingleQuestionContent();
                                    // 新题｜旧题
                                    analyzeNewFormat(questionDoc, subQuestion);
                                    subQuestion.setId(doc.getId());
                                    // 试题难度：1易，2中，3难，4极难
                                    subQuestion.setLevel(getDifficulty(doc.getDifficulty()));
                                    // 主题
                                    List<Long> tempTopicIds = doc.getTopicId();
                                    List<String> tempTopicNames = doc.getTopicName();
                                    if (null != tempTopicIds && null != tempTopicNames && tempTopicIds.size() > 0 &&
                                            tempTopicNames.size() > 0 && tempTopicIds.size() == tempTopicNames.size()) {
                                        List<Map<String, Object>> themeList = new ArrayList<>();
                                        for (int j = 0; j < tempTopicIds.size(); j++) {
                                            Map<String, Object> themeMap = new HashMap<>();
                                            themeMap.put("id", tempTopicIds.get(j));
                                            themeMap.put("name", tempTopicNames.get(j));
                                            themeList.add(themeMap);
                                        }
                                        subQuestion.setTheme(themeList);
                                    }

                                    // 题型: 1-选择题 2-填空题 3-判断题 4-简答题 5-实验题
                                    //                                    String tempType = doc.getQuestionType();
                                    //                                    if ("单项选择".equals(tempType)) {
                                    //                                        tempType = "选择题";
                                    //                                        subQuestion.setChoiceType(1);
                                    //                                    }
                                    //                                    if ("解答题".equals(tempType)) {
                                    //                                        tempType = "简答题";
                                    //                                    }
                                    Map<String, Object> tempTypeMap = getQuestionType(doc.getQuestionTypeId(),reqId);
                                    subQuestion.setTopic_type(tempTypeMap);
                                    // 答案数量
                                    if (null != questMongoContent.getAnswer()) {
                                        singleContent.setAnswer(questMongoContent.getAnswer());
                                        if (questMongoContent.getAnswer() instanceof List) {
                                            List<Object> tempList = (List<Object>) questMongoContent.getAnswer();
                                            subQuestion.setAnswer_num(tempList.size());
                                        } else if (questMongoContent.getAnswer() instanceof String) {
                                            String tempStr = (String) questMongoContent.getAnswer();
                                            subQuestion.setAnswer_num(tempStr.split(",").length);
                                        }
                                    }
                                    Integer questionTypeId = doc.getQuestionTypeId();
                                    Integer answerNum = subQuestion.getAnswer_num();

                                    setChoiceAbout(subQuestion, questionTypeId, answerNum, tempTypeMap);

                                    singleContent.setBody(questMongoContent.getBody());
                                    singleContent.setInterpret(questMongoContent.getTranslation());
                                    singleContent.setImage(questMongoContent.getImage());
                                    singleContent.setBase_image(questMongoContent.getMap());
                                    singleContent.setModel_essay(questMongoContent.getModel_essay());
                                    singleContent.setOptions(questMongoContent.getOptions());
                                    singleContent.setAnalysis(questMongoContent.getAnalysis());
                                    subQuestion.setContent(singleContent);
                                    subQuestion.setIsSingle(true);
                                    //智能批改
                                    if(doc.getIntelligent()!=null){
                                        subQuestion.setIntelligent(doc.getIntelligent());
                                    }else{
                                        subQuestion.setIntelligent(0);
                                    }
                                } else {
                                    logger.error("习题[{}]为子题，但是isSingle = 0, 数据异常", doc.getId());
                                    throw new BizLayerException("题型不合法，数据异常习题Id：" + doc.getId(), BasicErrorCode
                                            .UNKNOWN);
                                }
                                subList.add(subQuestion);
                            }
                        }
                        multiContent.setQuestions(subList);
                        question.setContent(multiContent);
                    }

                }
            }
        }
        for (Long questionId : questionIds) {
            SuperQuestion superQuestion = map.get(questionId);
            if (superQuestion != null) {
                questionList.add(superQuestion);
            }
        }
        //如果题集ID不为空，就缓存该题集5分钟-set就是秒
        if(exerciseId!=null){
            redisUtil.set(cacheKey,questionList,300);
        }
        logger.info("getQuestions cost {}ms", System.currentTimeMillis() - l);
        return questionList;

    }

    /**
     * // 选择题题型:单选题、多选题
     * // 7选5也算单选题ChoiceType方圆按钮结构
     *
     * @param subQuestion 参数
     * @param questionTypeId 参数
     * @param answerNum 参数
     * @param tempTypeMap 参数
     */
    private void setChoiceAbout(SuperQuestion subQuestion, Integer questionTypeId, Integer answerNum, Map<String,
            Object> tempTypeMap) {

        if (Integer.compare(questionTypeId, QuestionTypeEnum.DAN_XUAN.getTypeId()) == 0) {
            if (null != answerNum) {
                if (subQuestion.getAnswer_num() == 1) {
                    subQuestion.setChoiceType(1);
                    tempTypeMap.put("name", "单选题");
                } else if (subQuestion.getAnswer_num() > 1) {
                    subQuestion.setChoiceType(2);
                    tempTypeMap.put("name", "多选题");
                }
            }
        }
        if (questionTypeId.equals(QuestionTypeEnum.QI_XUAN_WU.getTypeId())) {
            subQuestion.setChoiceType(1);
        }
        if(questionTypeId.equals(56)||questionTypeId.equals(58)||questionTypeId.equals(59)){
            if (null != answerNum) {
                if (subQuestion.getAnswer_num() == 1) {
                    subQuestion.setChoiceType(1);
                }
            }
        }
    }

    private List<Long> getLeafChapterIds(List<Long> chapterId1, List<Long> chapterId2, List<Long> chapterId3) {
        return CollectionUtils.isNotEmpty(chapterId3) ? chapterId3 : (CollectionUtils.isNotEmpty(chapterId2) ?
                chapterId2 : chapterId1);
    }

    private void analyzeNewFormat(QuestionDocument questionDoc, SuperQuestion question) {
        if (null != questionDoc.getNewFormat() && questionDoc.getNewFormat() == 1) {
            question.setqCode(ResultConfigEnum.QUESTION_NEW.getCode());
            question.setqInfo(ResultConfigEnum.QUESTION_NEW.getInfo());
        } else {
            question.setqCode(ResultConfigEnum.QUESTION_OLD.getCode());
            question.setqInfo(ResultConfigEnum.QUESTION_OLD.getInfo());
        }
    }

    /**
     * 根据选题类型名称，获取包含id和name的map
     *
     * @return map
     */
    private Map<String, Object> getQuestionType(int questionTypeId,String reqId) {
        Map<Integer, QuestionType> questionTypeMap = questionMemTypeMap.get(reqId);
        if(questionTypeMap==null||questionTypeMap.size()==0){
            questionTypeMap = getRealQuestionTypeMap(reqId);
        }
        QuestionType type = questionTypeMap.get(questionTypeId);
        Map<String, Object> typeMap = new HashMap<>();
        typeMap.put("id", type.getTypeId());
        typeMap.put("name", type.getTypeName());
        return typeMap;
    }

    private Map<Integer, QuestionType> getRealQuestionTypeMap(String reqId) {
        Map<Integer, QuestionType> questionTypeMap = new HashMap<>();
        List<QuestionType> allEnable = questionTypeDao.findAllEnable();
        if (CollectionUtils.isNotEmpty(allEnable)) {
            for (QuestionType questionType : allEnable) {
                questionTypeMap.put(questionType.getTypeId().intValue(), questionType);
            }
        }
        if(!StringUtils.isEmpty(reqId)){
            questionMemTypeMap.put(reqId,questionTypeMap);
        }
        return questionTypeMap;
    }

    private long findStructIdByTypeId(long questionTypeId,String  reqId) {
        Map<Integer, QuestionType> questionTypeMap = questionMemTypeMap.get(reqId);
        if(questionTypeMap==null||questionTypeMap.size()==0){
            questionTypeMap = getRealQuestionTypeMap(reqId);
        }

        QuestionType questionType = questionTypeMap.get((int) questionTypeId);
        if (questionType == null) {//垃圾数据questionTypeId=0
            return 0;
        }
        return questionType.getStructId();
    }
    private Map<Integer, QuestionType> getCacheQuestionType(String  reqId){
        Map<Integer, QuestionType> questionTypeMap = questionMemTypeMap.get(reqId);
        if(questionTypeMap==null||questionTypeMap.size()==0){
            questionTypeMap = getRealQuestionTypeMap(reqId);
        }
        return questionTypeMap;
    }


    private Map<Integer, String> getRealSupportedQuestionTypeMap() {
        Map<Integer, String> supportedQuestionTypeMap = new HashMap<>();
        List<QuestionType> allEnable = questionTypeDao.findAllEnable();
        if (CollectionUtils.isNotEmpty(allEnable)) {
            for (QuestionType questionType : allEnable) {
                supportedQuestionTypeMap.put(questionType.getTypeId().intValue(), questionType.getTypeName());
            }
        }
        return supportedQuestionTypeMap;
    }

    /**
     * 根据难易度的id返回包含 id 和 name的map
     *
     * @param difficulty difficulty
     * @return map
     */
    private Map<String, Object> getDifficulty(Integer difficulty) {
        Map<String, Object> levelMap = new HashMap<>();
        if (difficulty != null) {
            Difficulty difficultyEnum = Difficulty.getDifficultyByCode(difficulty);
            if (difficultyEnum != null) {
                levelMap.put("id", difficultyEnum.getCode());
                levelMap.put("name", difficultyEnum.getDesc());
            }
        }
        return levelMap;
    }

    @Override
    public void destroy() {
        threadPool.shutdown();
    }

    private class QueryQuestionReportThread implements Callable<Map<Long, TeacherSpaceQuestion>> {
        private List<Long> questionIds;

        QueryQuestionReportThread(List<Long> questionIds) {
            this.questionIds = questionIds;
        }

        public Map<Long, TeacherSpaceQuestion> call() {
            Map<Long, TeacherSpaceQuestion> map = new HashMap<>();
            List<TeacherSpaceQuestion> list = teacherSpaceQuestionMapper.findByQuestionIds(questionIds);
            if (CollectionUtils.isNotEmpty(list)) {
                for (TeacherSpaceQuestion teacherSpaceQuestion : list) {
                    map.put(teacherSpaceQuestion.getQuestionId(), teacherSpaceQuestion);
                }
            }
            return map;
        }
    }


    @Override
    public FindQuestionKnowledgeByIdsResponse findQuestionKnowledgeByIds(FindQuestionKnowledgeByIdsRequest request)
            throws BizLayerException {

        Map<String, Object> queryConf = new HashMap<>();
        List<Long> ids = request.getQuestionIds();
        int size = ids.size();
        queryConf.put(QueryMap.KEY_ROWS, size);
        if (size > 50) {
            logger.warn("统计数据过大size:{}", size);
        }
        queryConf.put(QueryMap.KEY_FL, "id, topicId, topicName");

        Map<String, Object> qMap = new HashMap<>();
        qMap.put("id", ids);
        queryConf.put(QueryMap.KEY_Q, qMap);
        SolrQueryPageRsp<QuestionDocument> search = questionSolrSearchService.search(new SolrQueryPageReq(queryConf));
        if (!search.success()) {
            throw new BizLayerException("数据查询失败", PraxisErrorCode.PRAXIS_INVOKE_SOLR);
        }
        FindQuestionKnowledgeByIdsResponse resp = new FindQuestionKnowledgeByIdsResponse();
        Map<Long, List<QuestionTopicVo>> map = new HashMap<>();
        resp.setQuestionTopicMap(map);
        SolrPage<QuestionDocument> page = search.getPage();
        if (page != null && CollectionUtils.isNotEmpty(page.getList())) {
            for (QuestionDocument doc : page.getList()) {
                Long questionId = doc.getId();
                List<QuestionTopicVo> list = map.get(questionId);
                if (list == null) {
                    list = new ArrayList<>();
                    map.put(questionId, list);
                }
                List<Long> topicIds = doc.getTopicId();
                if (CollectionUtils.isEmpty(topicIds)) {
                    logger.warn("当前题目[{}]没有知识点", questionId);
                    continue;
                }
                List<String> topicNames = doc.getTopicName();
                for (int i = 0; i < topicIds.size(); i++) {
                    QuestionTopicVo qt = new QuestionTopicVo();
                    qt.setQuestionId(questionId);
                    qt.setTopicId(topicIds.get(i));
                    try {
                        String topicName = topicNames.get(i);
                        qt.setTopicName(StringUtils.replace(topicName, "\n", ""));
                    } catch (Exception e) {
                        logger.error("题目[{}]知识点异常, topicId:{}, topicName:{}", topicIds, topicNames);
                        continue;
                    }
                    list.add(qt);
                }
            }
        }
        return resp;
    }


    /**
     * 创建题目
     *
     * @param request req
     * @return 返回
     * @throws BizLayerException 创建题目的基本流程
     *                           首先就是数据库插入
     *                           -->单题
     *                           -->复合题
     *                           其次是MQ的发送创建Solr索引
     *                           -->单题
     *                           -->复合题
     *                           最后是音频处理，因为音频转换为异步转换，所以在题目上传成功之后需要触发七牛进行转换
     */
    @Override
    public CommonResult createQuestion(UploadQuestionRequest request) throws BizLayerException {
        logger.info("创建题目开始==》入参为:{}", request.toString());

        String html = "";
        if(StringUtils.isBlank(request.getHtml())){
            throw new BizLayerException("",QUESTION_CONTENT_NULL_EXCEPTION);
        }
        /*
            口语文章朗读 题型54
            需要对html进行转换
            添加answer字段
            添加third_party_use字段
         */
        html=getQuestionHtmlString(request.getHtml(),request.getType());

        //编辑的情况不走验证
        if(request.getId()==null){
            //验证乱码问题
            if(!StringUtils.isEmpty(QuestionServiceUtil.getSubUtilSimple(html,QuestionServiceUtil.htmlUnicodeRegex))){
                logger.error("题目创建存在乱码问题");
                throw new BizLayerException("", QUESTION_FORMAT_HTML_ERROR);
            }

            //验证style白名单
            if(QuestionServiceUtil.checkWhiteRuleKey(QuestionServiceUtil.getSubUtil(html,QuestionServiceUtil.htmlStyleRegex),QuestionServiceUtil.whiteRuleKeyCompare,QuestionServiceUtil.whiteRuleAllCompare )){
                throw new BizLayerException("", QUESTION_FORMAT_HTML_ERROR);
            }
        }


        //logger.debug("创建题目数据html为:{}", html);

        /*
            1.判断单题还是符合题
            判断此题为单题或者复合题根据html的questions属性来判断
            if questions.length>0
                复合题
            else
                单题
         */
        //定义题目是否为单题,状态默认值为1，为单题
        int is_single = 1;
        int length = 0;
        if (!JsonUtils.is_key(html, "questions")) {
            is_single = 1;
        } else {
            length = JsonUtils.getQuestionsLength(html, "questions");
            //如果length>0 复合题 否则题单题
            if (length > 0) {
                is_single = 0;
            } else {
                is_single = 1;
            }
        }

        /*
            2.获取数据库主键
            因为数据库主键生成方式非自增形式，
            采用外部服务生成主键的方式，
            所以新增题目的时候首先就是生成数据库主键ID
            获取数据库主键的方式采用批量获取方式，如果为单题那么length=0，获取一个，
            如果为复合题那么根据复合题数量而定，length为复合题子题数量
         */
        List<Long> questionIds = getBatchQuestionId(1 + length);
        logger.debug("\n 获取数据库主键列表：{},复合题个数为：{}", questionIds, questionIds.size() - 1);
        /*
            3.构建参数

                mysql：
                if 单题:
                    -->构建大题参数
                else
                    -->构建大题参数
                    -->构建子题参数
                solr：
                if 单题:
                    -->构建大题参数
                else
                    -->构建大题参数
                    -->构建子题参数
         */

        /*
            参数列表
               -->章节和知识点不能同时为空
               -->知识点不能超过十个
               验证提前
         */
        List<Long> topicIds = request.getTopicIds();
        Long chapterId = request.getChapterId();
        if ((topicIds == null && chapterId == null) || ((topicIds != null && topicIds.size() == 0) && chapterId == null)) {
            throw new BizLayerException("知识点和章节不能同时为空", ANSWER_PARAMETER_ILL);
        }
        if (topicIds != null && topicIds.size() > 10) {
            throw new BizLayerException("", TOPIC_COUNT);
        }

        Integer level = request.getLevel();
        int type = request.getType();
        Long groupId = request.getGroupId(); //自定义目录体系ID
        Long customerDirectory = request.getCustomerDirectory();
        Long subjectId = request.getSubjectId();
        Long uploadId = request.getUploadId();
        Integer uploadSrc = request.getUploadSrc();
        String source = request.getSource();
        Long orgId = request.getOrgId();
        Integer orgType = request.getOrgType();
        String statue = request.getStatue();

        /*
            3.1构建大题参数
         */
        EntityQuestion question = new EntityQuestion();
        question.setId(questionIds.get(0)); //题目ID
        question.setDifficulty(level); //题目的难易程度
        question.setIsSingle(is_single); //是否是单题
        question.setSubjectId(subjectId); //科目ID
        question.setUploadId(uploadId); //上传人ID
        question.setNewFormat(1); //新题标志
        question.setUploadSrc(uploadSrc); //区分山传人来源
        question.setParentQuestionId(0L); //题目的父类目ID，如果为单题此值为0如果为复合题，那么此值大题为0，子题为大题的题目ID
        question.setUploadTime(new Date()); //上传时间
        question.setUpdateTime(new Date()); //更新时间
        if (CollectionUtils.isNotEmpty(topicIds)) {
            question.setCountTopic(topicIds.size()); //知识点个数
        } else {
            question.setCountTopic(0); //知识点个数
        }
        question.setScore(0F); //题目分值
        if (StringUtils.isNotEmpty(source)) {
            question.setSource(source); //习题来源
        }
        question.setOrgId(orgId); //创建题目的平台ID
        question.setOrgType(orgType); //创建题目的平台类型
        question.setVisible(1); //教师空间是否可见

        /*
            题目状态
         */
        if (53 == type) { //如果为拍照题
            if (StringUtils.isNotEmpty(statue)) {
                question.setState(QuestionState.valueOf(statue));
            } else {
                question.setState(QuestionState.BANNED);
            }
        } else {
            if (StringUtils.isNotEmpty(statue)) {
                question.setState(QuestionState.valueOf(statue));
            } else {
                question.setState(QuestionState.PREVIEWED);
            }
        }
        //如果创建者是百名单学校，那么题目直接是可流转状态
        if(QuestionServiceUtil.isWhiteSchool(auditsedSchoolMapper,orgId,redisUtil)){
            question.setState(QuestionState.ENABLED);
        }


        question.setQuestionTypeId(type); //题目类型ID, 比如选择题为 1
        QuestionType questionType= questionTypeDao.findById(type);
        if(questionType==null){
            throw new BizLayerException("", NULL_QUESTION_TYPE_EXCEPTION);
        }
        question.setQuestionType(questionType.getTypeName());

        List<QuestionQuality> questionQualityList=new ArrayList<>();
        if (1 == is_single) { //如果是单题题目类型名称为
            SingleQuestion singleQuestion = null;
            try {
                singleQuestion = JsonUtils.fromJson(html, SingleQuestion.class);
                if (0 == type || 1 == type) {  //选择题
                    question.setRightOption(singleQuestion.getAnswer().toString()); //正确选项
                    question.setCountOptions(singleQuestion.getOptions().size()); //选项数量
                    if(singleQuestion.getAnswer().size()>=2){
                        question.setQuestionType("多选题");
                    }
                }
                if (singleQuestion.getAnswer() != null) {
                    question.setAnswerNum(singleQuestion.getAnswer().size()); //答案个数
                } else {
                    question.setAnswerNum(0);
                }
                //是否支持智能批改
                if(singleQuestion.getAnswer()!=null && question.getQuestionTypeId()!=null){
                    question.setIntelligent(QuestionServiceUtil.getIntelligent(singleQuestion.getAnswer(),
                            question.getQuestionTypeId(), question.getSubjectId()));
                }else {
                    question.setIntelligent(0); //是否支持智能批改
                }

                //给recommend_svr发送消息
                QuestionQuality questionQuality=new QuestionQuality();
                questionQuality.setUploadId(request.getUploadId());
                questionQuality.setDifficulty(question.getDifficulty());
                questionQuality.setQuestionId(question.getId());
                questionQualityList.add(questionQuality);

            } catch (IOException e) {
                logger.error("html转换单题SingleQuestion异常:{}", e);
                e.printStackTrace();
            }
            //作图题添加底图数据
            handleQuestionBasemap(question,singleQuestion);
        } else {
            question.setAnswerNum(0);
            question.setIntelligent(0);
        }


        /*
            3.1.1  HTML JSON转换构建html和json参数
         */
        try {
            String json = ParseHtmlUtil.html2json(html, type + "", question.getId(),questionTypeDao);
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



        /*
            3.1.2 插入自定义目录q
         */
        CustomQuestionResource customQuestionResource = new CustomQuestionResource();
        customQuestionResource.setSystemId(uploadId);
        customQuestionResource.setCreateTime(new Date());
        customQuestionResource.setCustomListId(customerDirectory);
        customQuestionResource.setQuestionId(question.getId());
        customQuestionResource.setGroupId(groupId);
        customQuestionResource.setIsFav(0);
        customQuestionResource.setResourceStatus(1);
        int i = entityQuestionMapper.createQuestion(question);
        if (i <= 0) {
            throw new BizLayerException("", QUESTION_CREATE_FAIL);
        }
        int resource = linkCustomQuestionResourceMapper.insertLinkCustomQuestionResource(customQuestionResource);
        if (resource <= 0) {
            logger.info("插入自定义目录失败，入参：{}", customerDirectory);
            throw new BizLayerException("", INSERT_DIRECTORY_FAIL);
        }
        logger.info("\n <><>question{},\n cirectory{},\n QuestionResource{}", question, customerDirectory,
                customQuestionResource);
        sendLinkQuestionIndex(question, customerDirectory, customQuestionResource.getId(), 0);

        /*
            3.2 构建复合题参数
         */
        if (0 == is_single) { //如果是复合题
            ComplexQuestion complexQuestion = null;
            try {
                complexQuestion = JsonUtils.fromJson(html, ComplexQuestion.class);
            } catch (IOException e) {
                logger.debug("html转换复合题ComplexQuestion异常:{}", html);
                throw new BizLayerException("", JSON_CONVERT_FAIL);
            }
            if (complexQuestion == null) {
                throw new BizLayerException("复合题子题不能为空:{}", QUESTION_CREATE_FAIL);
            }
            List<com.noriental.praxissvr.question.bean.html.Question> questions = complexQuestion.getQuestions();
            //封装子题列表，用于批量插入
            List<EntityQuestion> subQuestionList = new ArrayList<>();
            for (int x = 1; x < questionIds.size(); x++) {
                EntityQuestion subQuestion = new EntityQuestion();
                subQuestion.setId(questionIds.get(x));
                //获取单个子题
                com.noriental.praxissvr.question.bean.html.Question sub_question_index = questions.get(x - 1);
                subQuestion.setQuestionTypeId(sub_question_index.getType().getId()); //题型ID
                if (sub_question_index.getType().getId() == 0 || sub_question_index.getType().getId() == 1) {
                    subQuestion.setRightOption(sub_question_index.getAnswer().toString()); //正确选项
                    subQuestion.setCountOptions(sub_question_index.getOptions().size());   //选项数量
                }
                if (sub_question_index.getAnswer() != null) {
                    subQuestion.setAnswerNum(sub_question_index.getAnswer().size()); //正确答案数量
                } else {
                    subQuestion.setAnswerNum(0); //正确答案数量
                }

                subQuestion.setQuestionType(sub_question_index.getType().getName()); //题目类型
                subQuestion.setQuestionTypeId(sub_question_index.getType().getId()); //题目类型ID
                if(StringUtils.isEmpty(sub_question_index.getDifficulty())){
                    subQuestion.setDifficulty(1);
                }else{
                    subQuestion.setDifficulty(Integer.valueOf(sub_question_index.getDifficulty()));
                }
                subQuestion.setIsSingle(1); //子题是单题
                subQuestion.setScore(0F); //题目分值
                subQuestion.setParentQuestionId(question.getId()); //子题的父类ID是大题的题目ID
                subQuestion.setState(QuestionState.PREVIEWED); //待审核
                subQuestion.setSubjectId(subjectId); //学科ID
                subQuestion.setUploadId(uploadId); //上传人ID
                subQuestion.setNewFormat(1); //是否是新题
                subQuestion.setOrgId(orgId); //平台ID
                subQuestion.setOrgType(orgType); //平台类型
                subQuestion.setVisible(1); //是否可见默认值是1
                subQuestion.setUploadSrc(request.getUploadSrc());
                //白名单学校的题目状态
                if(QuestionServiceUtil.isWhiteSchool(auditsedSchoolMapper,orgId,redisUtil)){
                    subQuestion.setState(QuestionState.ENABLED);
                }

                // 是否支持智能批改
                if(sub_question_index.getAnswer()!=null){
                    subQuestion.setIntelligent(QuestionServiceUtil.getIntelligent(sub_question_index.getAnswer(),
                            sub_question_index.getType().getId(),subQuestion.getSubjectId()));
                }else{
                    subQuestion.setIntelligent(0);
                }
                /**
                 * 如果为作图题
                 * 将底图信息取出来json化插入进数据库
                 */
                if (subQuestion.getQuestionTypeId() == 51) {
                    NewMap jsonMapImgae = sub_question_index.getMap();
                    try {
                        String json = JSON.json(jsonMapImgae);
                        subQuestion.setJsonMap(json);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                //给recommend_svr发送消息
                QuestionQuality questionQuality=new QuestionQuality();
                questionQuality.setUploadId(request.getUploadId());
                questionQuality.setDifficulty(subQuestion.getDifficulty());
                questionQuality.setQuestionId(subQuestion.getId());
                questionQualityList.add(questionQuality);

                subQuestionList.add(subQuestion);
            }
            logger.info("\n 复合题批量插入数据为：{}", subQuestionList);
            int subI = entityQuestionMapper.batchInsertQuestion(subQuestionList);
            if (subI <= 0) {
                logger.error("创建题目复合题时，批量插入子题失败");
                throw new BizLayerException("", BATCH_INSERT_FAIL);
            }
        }

        /*
            3.3 主题操作
               -->章节和知识点不能同时为空
               -->知识点不能超过十个
        */


        if (CollectionUtils.isNotEmpty(topicIds)) {
            List<Map<String, Object>> topicList = new ArrayList<>();
            for (Long topicId : topicIds) {
                Map<String, Object> mapTopic = new HashMap<>();
                mapTopic.put("questionId", question.getId());
                mapTopic.put("topicId", topicId);
                topicList.add(mapTopic);
            }
            //批量插入知识点
            int topicI = linkQuestionTopicMapper.batchQuestionTopicLink(topicList);
            if (topicI <= 0) {
                logger.error("知识点关联批量插入失败");
                throw new BizLayerException("知识点关联批量插入失败", BATCH_INSERT_FAIL);
            }
        }

        /*
            3.4 章节操作
         */
        if (chapterId != null && chapterId != 0) {
            int chapterI = linkQuestionChapterMapper.createLinkQuestionChapter(question.getId(), chapterId);
            if (chapterI <= 0) {
                throw new BizLayerException("章节插入失败", QUESTION_CREATE_FAIL);
            }
        }
        /**
         * 题目挂接的专题数据
         */
        List<Long> reqSpecials=request.getSpecials();
        Long questionId=questionIds.get(0);
        if(!CollectionUtils.isEmpty(reqSpecials)){
            handleSpecials(reqSpecials, questionId);
        }
        /*
            4. 发送MQ 创建Solr索引
         */
        MDC.put("id", TraceKeyHolder.getTraceKey());

        sendSolrDoc2Mq(question, html, chapterId, topicIds, questionIds, customerDirectory);



        /*
            5.七牛回调
            听力题/
         */
        logger.info("===音频调用==={},是否是单题=={}", type, is_single);
        if (is_single == 1) { //如果是单题
            logger.info("单题七牛回调音频触发成功:{}", is_single);
            SingleQuestion sinQuestion = null;
            try {
                sinQuestion = JsonUtils.fromJson(html, SingleQuestion.class);
            } catch (IOException e) {
                logger.error("七牛回调单题JSON转换失败{}", e);
                e.printStackTrace();
            }
            if (sinQuestion != null && sinQuestion.getAudio() != null) {
                if(sinQuestion.getAudio().getUrl().indexOf(".mp3")==-1){
                    ConvertMp3Request convertMp3Request = new ConvertMp3Request();
                    convertMp3Request.setEntityId(question.getId() + "");
                    convertMp3Request.setAudioUrl(sinQuestion.getAudio().getUrl());
                    CommonResponse<String> convertMp3 = qiniuVoiceService.convertMp3(convertMp3Request);
                    logger.info("单题七牛回调音频触发成功:{}", convertMp3.getData());
                }
            }
        } else {
            logger.info("复合题七牛回调音频触发成功:{}", is_single);
            ComplexQuestion complexQuestion = null;
            try {
                complexQuestion = JsonUtils.fromJson(html, ComplexQuestion.class);
            } catch (IOException e) {
                logger.info("七牛回调复合题JSON转换失败{}", html);
                e.printStackTrace();
            }
            logger.info("触发转换{}", complexQuestion);
            if (complexQuestion != null && complexQuestion.getAudio() != null) {
                if(complexQuestion.getAudio().getUrl().indexOf(".mp3")==-1){
                    ConvertMp3Request convertMp3Request = new ConvertMp3Request();
                    convertMp3Request.setEntityId(question.getId() + "");
                    convertMp3Request.setAudioUrl(complexQuestion.getAudio().getUrl());
                    CommonResponse<String> convertMp3 = qiniuVoiceService.convertMp3(convertMp3Request);
                    logger.info("复合题七牛回调音频触发成功:{}", convertMp3.getData());
                }
            }
        }

        List<Long> idList = new ArrayList<>();
        idList.add(customerDirectory);
        /*  记录自定义目录下是否有资源.*/
        customListService.updateCustomHasRes(new IdsRequest(idList));
        //创建习题给统计服务发送相关数据
        logger.info("创建题目给统计服务发送的消息：{}",questionQualityList.toString());
        createQuestionRecommendTemplate.convertAndSend(JsonUtil.obj2Json(questionQualityList));
        //获取创建题目成功的题目ID返回
        CommonResult result = new CommonResult();
        result.setId(question.getId());
        return result;
    }

    /**
     * 插入题目专题信息数据
     * @param reqSpecials
     * @param questionId
     */
    private void handleSpecials(List<Long> reqSpecials, Long questionId) {
        List<EntityQuestionSpecial> specialList=new ArrayList<>();
        for (Long specialId:reqSpecials){
            EntityQuestionSpecial special=new EntityQuestionSpecial();
            special.setExamSitesId(specialId);
            special.setQuestionId(questionId);
            specialList.add(special);
        }
        int specialListBatchInsert=questionSpecialMapper.batchInsertQuestionSpecial(specialList);
        if(specialListBatchInsert<=0){
            throw new BizLayerException("专题数据插入失败",QUESTION_SPECIAL_ERROR);
        }
    }

    private String getQuestionHtmlString(String requestHtml,int questionTypeId) {
        String html="";
        if (questionTypeId == 54) {
            JSONObject json = null;
            try {
                //创建题目时不再替换题干中的非常规http路径
                //requestHtml=requestHtml.replace("\"url\": \"//","\"url\":\"http://").replace("\"url\":\"//","\"url\":\"http://");
                json = new JSONObject(requestHtml);
            } catch (JSONException e) {
                logger.error("读入html异常数据为:{}", requestHtml);
                e.printStackTrace();
            }
            if (json != null) {
                String prompt = null;
                try {
                    prompt = json.getString("prompt");
                } catch (JSONException e) {
                    logger.error("获取prompt数据异常html数据为 :{}", requestHtml);
                    e.printStackTrace();
                }

                if (StringUtils.isNotEmpty(prompt)) {
                    /*
                        在这根据prompt字段拼装answer数据和third_party_use 数据
                        answer数据的html格式为 [{answer_audio:{"url":"","name":"","size":""}},
                        "answer_content":"<p>aaabbbccc</p>"]
                        third_party_use的数据格式为  "aabbcddcdd"

                     */
                    try {
                        json.put("third_party_use", prompt);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    List<Object> answer = new ArrayList<>();
                    Map<String, Object> answer_audio_content = new HashMap<>();
                    if (JsonUtils.is_key(requestHtml, "audio")) {
                        Audio audio = null;
                        try {
                            try {
                                audio = JsonUtils.fromJson(json.get("audio") + "", Audio.class);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        answer_audio_content.put("answer_audio", audio);
                    }
                    String content = "<p>" + prompt + "</p>";
                    answer_audio_content.put("answer_content", content);
                    answer.add(answer_audio_content);
                    try {
                        json.put("answer", answer);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                html = json.toString();
            }
            //听力题过滤url中的空格
        }else if(questionTypeId == 49){
            JSONObject json = null;
            try {
                json = new JSONObject(requestHtml);
                JSONObject audio = json.getJSONObject("audio");
                audio.put("url",audio.getString("url").trim());
                json.put("audio",audio);
            } catch (JSONException e) {
                logger.error("读入html异常数据为:{}", requestHtml);
                e.printStackTrace();
            }
            html = json == null ? "":json.toString();
        }else {
            html = requestHtml;
        }
        return html;
    }
    /**
     * 将转换后的solr实体发送给RabbitMQ
     *
     * @param entityQuestion entity
     */
    private void sendSolrDoc2Mq(EntityQuestion entityQuestion, String html, Long chapterId, List<Long> topicIds,
                                List<Long> ids, Long cusDirId) {
        SolrIndexReqMsg solrIndexReqMsg = null;
        if (1 == entityQuestion.getIsSingle()) {//单题
            solrIndexReqMsg = componentSolrDoc(entityQuestion, html, chapterId, topicIds, cusDirId);
        } else {//复合题
            solrIndexReqMsg = SolrIndexReqMsgSubj(entityQuestion, html, chapterId, topicIds, ids, cusDirId);
            logger.info("\n MQ发送复合题requestID:{} \n MQ发送复合题开始:{}", solrIndexReqMsg.getRequestId(), solrIndexReqMsg
                    .getBody());
        }
        solrUploadQuestionRabbitTemplate.convertAndSend(solrIndexReqMsg);
    }

    /**
     * @param entityQuestion 参数
     * @param cusDirId        :自定义目录ID
     * @param link_cus_dir_id ：自定义目录关联表Id
     * @param stat            ：是否需要收藏 1：收藏  0：不需要收藏
     */
    private void sendLinkQuestionIndex(EntityQuestion entityQuestion, Long cusDirId, Long link_cus_dir_id, Integer
            stat) {

        LqResourceDocument lqResourceDocument = new LqResourceDocument();
        //自定义关联目录ID
        lqResourceDocument.setId(link_cus_dir_id);
        //当前用户ID
        lqResourceDocument.setSystemId(entityQuestion.getUploadId());
        //创建时间
        lqResourceDocument.setCreateTime(entityQuestion.getUploadTime());
        //更新时间
        lqResourceDocument.setUpdateTime(new Date());
        //题目ID
        lqResourceDocument.setQuestionId(entityQuestion.getId());
        //题目类型ID
        lqResourceDocument.setQuestionTypeId(Long.valueOf(entityQuestion.getQuestionTypeId()));
        //QuestionTypeEnum typeEnum = QuestionTypeEnum.getQuestionTypeByTypeId(entityQuestion.getQuestionTypeId());
        //题型
        //lqResourceDocument.setQuestionType(typeEnum.getSolrType());
        if(entityQuestion.getQuestionTypeId().equals(1)){
            lqResourceDocument.setQuestionType("选择题");
        }else{
            lqResourceDocument.setQuestionType(entityQuestion.getQuestionType());
        }

        //难易程度
        lqResourceDocument.setDifficulty(entityQuestion.getDifficulty());
        //是否是单题
        lqResourceDocument.setIsSingle(entityQuestion.getIsSingle());
        //自定义目录题目状态
        lqResourceDocument.setResourceStatus(1);
        //是否收藏
        lqResourceDocument.setIsFav(stat);
        //题目状态
        lqResourceDocument.setState(QuestionState.PREVIEWED.name());
        //答案个数
        lqResourceDocument.setAnswerNum(entityQuestion.getAnswerNum());
        lqResourceDocument.setVisible(entityQuestion.getVisible());
        lqResourceDocument.setSubjectId(entityQuestion.getSubjectId());
        lqResourceDocument.setNewFormat(entityQuestion.getNewFormat());

        /*
            1.自定义目录索引
         */
        if (cusDirId != null) {
            CommonResponse<List<CustomListVo>> response = customListService.findParentsByLeafId(new IdRequest
                    (cusDirId));
            List<CustomListVo> data = response.getData();
            if (CollectionUtils.isNotEmpty(data)) {
                //自定义目录体系ID
                lqResourceDocument.setGroupId(data.get(0).getGroupId());
                Long cusDirIds1 = null;
                Long cusDirIds2 = null;
                Long cusDirIds3 = null;
                String customListName1 = "";
                String customListName2 = "";
                String customListName3 = "";
                for (CustomListVo customListVo : data) {
                    if (customListVo.getLevel() == 1) {
                        cusDirIds1 = customListVo.getId();
                        customListName1 = customListVo.getName();
                    } else if (customListVo.getLevel() == 2) {
                        cusDirIds2 = customListVo.getId();
                        customListName2 = customListVo.getName();
                    } else if (customListVo.getLevel() == 3) {
                        cusDirIds3 = customListVo.getId();
                        customListName3 = customListVo.getName();
                    }
                }
                lqResourceDocument.setCustomListId1(cusDirIds1);
                lqResourceDocument.setCustomListName1(customListName1);

                lqResourceDocument.setCustomListId2(cusDirIds2);
                lqResourceDocument.setCustomListName2(customListName2);

                lqResourceDocument.setCustomListId3(cusDirIds3);
                lqResourceDocument.setCustomListName3(customListName3);

                lqResourceDocument.setCustomListId(cusDirId);
            }
        }

        SolrIndexReqMsg solrIndexReqMsg = new SolrIndexReqMsg(lqResourceDocument);
        solrIndexReqMsg.setRequestId(TraceKeyHolder.getTraceKey());
        logger.info("发送solr关联关系文档请求ID{}", solrIndexReqMsg.getRequestId());
        solrUploadQuestionRabbitTemplate.convertAndSend(solrIndexReqMsg);
    }


    /*
        复合题转换mq
     */
    private SolrIndexReqMsg SolrIndexReqMsgSubj(EntityQuestion entityQuestion, String html, Long chapterId,
                                                List<Long> topicIds, List<Long> ids, Long cusDirId) {

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
            if (questions.get(i).getOptions() != null) {
                questionTmpDocument.setCountOptions(questions.get(i).getOptions().size());
            } else {
                questionTmpDocument.setCountOptions(0);
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

            /*
             * answerNum个数
             */
            questionTmpDocument.setAnswerNum(questions.get(i).getAnswer().size());
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
                        questions.get(i).getType().getId(),questionTmpDocument.getSubjectId()));
            }else{
                questionTmpDocument.setIntelligent(0);
            }

            if (entityQuestion.getUploadTime() != null) {
                questionTmpDocument.setUploadTime(entityQuestion.getUploadTime());
            }
            /*
                子题作图题处理
             */
            if (questions.get(i).getType().getId() == 51) {
                NewMap map = questions.get(i).getMap();
                try {
                    String json = JSON.json(map);
                    questionTmpDocument.setJsonMap(json);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            questionTmpDocument.setUpdateTime(new Date());
            questionTmpDocument.setState(QuestionState.PREVIEWED.name());
            //复合题子题白名单特殊处理
            if(QuestionServiceUtil.isWhiteSchool(auditsedSchoolMapper,entityQuestion.getOrgId(),redisUtil)){
                questionTmpDocument.setState(QuestionState.ENABLED.name());
            }
            questionTmpDocument.setQrId(entityQuestion.getQrId());
            questionTmpDocument.setCountTopic(entityQuestion.getCountTopic());
            questionTmpDocument.setSrc(entityQuestion.getSrc());
            questionTmpDocument.setQuestionGroup(entityQuestion.getQuestionGroup());
            questionTmpDocument.setOrgId(entityQuestion.getOrgId());
            questionTmpDocument.setOrgType(entityQuestion.getOrgType());
            questionTmpDocument.setSource(entityQuestion.getSource());
            questionTmpDocument.setUploadSrc(entityQuestion.getUploadSrc());
            questionTmpDocument.setIsFinishedProduct(0);


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
        //智能批改
        questionTmp.setIntelligent(0);
        questionTmp.setHtmlData(entityQuestion.getHtmlData());
        questionTmp.setJsonData(entityQuestion.getJsonData());

        questionTmp.setAllLeafQuesIds(subIds);

        if (entityQuestion.getUploadTime() != null) {
            questionTmp.setUploadTime(entityQuestion.getUploadTime());
        }
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
        questionTmp.setParentQuestionId(entityQuestion.getParentQuestionId());

        /*
            自定义目录
         */
        if (cusDirId != null) {
            CommonResponse<List<CustomListVo>> response = customListService.findParentsByLeafId(new IdRequest
                    (cusDirId));
            List<CustomListVo> data = response.getData();
            if (CollectionUtils.isNotEmpty(data)) {

                List<Long> cusDirIds1 = new ArrayList<>();
                List<Long> cusDirIds2 = new ArrayList<>();
                List<Long> cusDirIds3 = new ArrayList<>();
                List<Long> cusDirIds = new ArrayList<>();
                for (CustomListVo customListVo : data) {
                    cusDirIds.add(customListVo.getId());
                    if (customListVo.getLevel() == 1) {
                        cusDirIds1.add(customListVo.getId());
                    } else if (customListVo.getLevel() == 2) {
                        cusDirIds2.add(customListVo.getId());
                    } else if (customListVo.getLevel() == 3) {
                        cusDirIds3.add(customListVo.getId());
                    }

                }
                questionTmp.setCustomListId1(cusDirIds1);
                questionTmp.setCustomListStr1((cusDirIds1 != null && cusDirIds1.size() > 0) ? cusDirIds1.toString()
                        .substring(1, cusDirIds1.toString().length() - 1) : "");
                questionTmp.setCustomListId2(cusDirIds2);
                questionTmp.setCustomListStr2((cusDirIds2 != null && cusDirIds2.size() > 0) ? cusDirIds2.toString()
                        .substring(1, cusDirIds2.toString().length() - 1) : "");
                questionTmp.setCustomListId3(cusDirIds3);
                questionTmp.setCustomListStr3((cusDirIds3 != null && cusDirIds3.size() > 0) ? cusDirIds3.toString()
                        .substring(1, cusDirIds3.toString().length() - 1) : "");
                questionTmp.setCustomListId(cusDirIds);
                questionTmp.setCustomListStr((cusDirIds != null && cusDirIds.size() > 0) ? cusDirIds.toString()
                        .substring(1, cusDirIds.toString().length() - 1) : "");
            }
        }


        //================章节信息=================

        if (chapterId != null && chapterId != 0) {
            //通过章节ID获取章节列表
            //组装chapter参数

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
        //************************************************************************************
        SolrIndexReqMsg solrIndexReqMsg = new SolrIndexReqMsg(questionTmpDocumentList);
        solrIndexReqMsg.setRequestId(TraceKeyHolder.getTraceKey());
        logger.info("复合题发送MQ的请求ID={}", solrIndexReqMsg.getRequestId());
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

                topicNames.add(entity.getName());        //知识点名称
                allMUTIds.add("T" + entity.getId());
                Unit unit = entity.getUnit();
                if (unit != null) {
                    unitIds.add(entity.getUnit().getId());   //单元IDS
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


    /***
     * 单题转换mq
     * @param entityQuestion question
     * @return solrMsg
     */
    private SolrIndexReqMsg componentSolrDoc(EntityQuestion entityQuestion, String html, Long chapterId, List<Long>
            topicIds, Long cusDirId) {

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
        //更新时间
        if (entityQuestion.getUpdateTime() != null) {
            questionTmpDocument.setUpdateTime(entityQuestion.getUpdateTime());
        }
        //机构ID
        questionTmpDocument.setOrgId(entityQuestion.getOrgId());
        questionTmpDocument.setOrgType(entityQuestion.getOrgType());
        //智能批改
        questionTmpDocument.setIntelligent(entityQuestion.getIntelligent());
        //上传时间
        if (null != entityQuestion.getUploadTime()) {
            questionTmpDocument.setUploadTime(entityQuestion.getUploadTime());
        }
        questionTmpDocument.setUploadId(entityQuestion.getUploadId());
        questionTmpDocument.setUploadSrc(entityQuestion.getUploadSrc());
        questionTmpDocument.setHtmlData(entityQuestion.getHtmlData());
        questionTmpDocument.setJsonData(entityQuestion.getJsonData());

        SingleQuestion singleQuestion;
        try {
            singleQuestion = JsonUtils.fromJson(html, SingleQuestion.class);
            if (1 == entityQuestion.getQuestionTypeId()) {  //选择题
                questionTmpDocument.setQuestionType("选择题");
            } else {
                questionTmpDocument.setQuestionType(entityQuestion.getQuestionType());

            }
            if (singleQuestion.getAnswer() != null) {
                questionTmpDocument.setAnswerNum(singleQuestion.getAnswer().size());
            } else {
                questionTmpDocument.setAnswerNum(0);
            }

        } catch (IOException e) {
            logger.error("", e);
            throw new BizLayerException("", JSON_CONVERT_FAIL);
        }


        questionTmpDocument.setQuestionTypeId(entityQuestion.getQuestionTypeId());
        questionTmpDocument.setVisible(1);
        questionTmpDocument.setSource(entityQuestion.getSource());
        questionTmpDocument.setNewFormat(1);

        /*
            作图题处理
         */
        if (entityQuestion.getQuestionTypeId() == 51) {
            if (StringUtils.isNotEmpty(entityQuestion.getJsonMap())) {
                questionTmpDocument.setJsonMap(entityQuestion.getJsonMap());
            }
        }

        /*
            自定义目录
         */
        if (cusDirId != null) {
            CommonResponse<List<CustomListVo>> response = customListService.findParentsByLeafId(new IdRequest
                    (cusDirId));
            List<CustomListVo> data = response.getData();
            if (CollectionUtils.isNotEmpty(data)) {

                List<Long> cusDirIds1 = new ArrayList<>();
                List<Long> cusDirIds2 = new ArrayList<>();
                List<Long> cusDirIds3 = new ArrayList<>();
                List<Long> cusDirIds = new ArrayList<>();
                for (CustomListVo customListVo : data) {
                    cusDirIds.add(customListVo.getId());
                    if (customListVo.getLevel() == 1) {
                        cusDirIds1.add(customListVo.getId());
                    } else if (customListVo.getLevel() == 2) {
                        cusDirIds2.add(customListVo.getId());
                    } else if (customListVo.getLevel() == 3) {
                        cusDirIds3.add(customListVo.getId());
                    }

                }
                questionTmpDocument.setCustomListId1(cusDirIds1);
                questionTmpDocument.setCustomListStr1((cusDirIds1 != null && cusDirIds1.size() > 0) ?
                        cusDirIds1.toString().substring(1, cusDirIds1.toString().length() - 1) : "");
                questionTmpDocument.setCustomListId2(cusDirIds2);
                questionTmpDocument.setCustomListStr2((cusDirIds2 != null && cusDirIds2.size() > 0) ?
                        cusDirIds2.toString().substring(1, cusDirIds2.toString().length() - 1) : "");
                questionTmpDocument.setCustomListId3(cusDirIds3);
                questionTmpDocument.setCustomListStr3((cusDirIds3 != null && cusDirIds3.size() > 0) ?
                        cusDirIds3.toString().substring(1, cusDirIds3.toString().length() - 1) : "");
                questionTmpDocument.setCustomListId(cusDirIds);
                questionTmpDocument.setCustomListStr((cusDirIds != null && cusDirIds.size() > 0) ? cusDirIds.toString
                        ().substring(1, cusDirIds.toString().length() - 1) : "");
            }
        }


        //============主题================

        if (topicIds != null && topicIds.size() > 0) {
            setTopicIdsRelation(topicIds, questionTmpDocument);
        }


        //============章节================
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
        solrIndexReqMsg.setRequestId(TraceKeyHolder.getTraceKey());
        logger.info("\n requestID为:{} \n ,MQ 发送单题数据为:{}", solrIndexReqMsg.getRequestId(), solrIndexReqMsg.getBody());
        return solrIndexReqMsg;
    }


    /**
     * 查询习题
     *
     * @param request req
     * @return resp
     * @throws BizLayerException e
     */
    @Override
    public GetQuestionByIdResponse findQuestionById(LongRequest request) throws BizLayerException {

        GetQuestionByIdResponse response = new GetQuestionByIdResponse();

        QuestionData questionData = new QuestionData();

        //查询习题
        EntityQuestion questionBean = entityQuestionMapper.findQuestionById(request.getId());
        if (questionBean == null) {
            logger.error("findQuestionById={}", request.getId());
            throw new BizLayerException("", QUERY_CHAPTER_FAIL);
        }
        List<LinkQuestionChapter> linkQuestionChapters = linkQuestionChapterMapper.findLinkQuestionChapterById
                (request.getId());
        //        List<Chapter> entityTeachingChapters = null;
        //        if (linkQuestionChapters != null && linkQuestionChapters.size() > 0) {
        //
        //            List<Long> chapterIds = new ArrayList<>();
        //            for (LinkQuestionChapter chapter : linkQuestionChapters) {
        //                chapterIds.add(chapter.getChapterId());
        //            }
        //
        ////            ResponseEntity<List<Chapter>> chaptersResp = chapterService.findByIds(new RequestEntity<>
        // (chapterIds));
        ////            entityTeachingChapters = chaptersResp.getEntity();
        //        }
        /*
            获取章节列表
         */
        List<Map<String, Object>> mapList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(linkQuestionChapters) && linkQuestionChapters.get(0) != null) {
            List<EntityTeachingChapter> chapterList = teacherChapterMapper.findChaptersById(linkQuestionChapters.get
                    (0).getChapterId());
            if (CollectionUtils.isNotEmpty(chapterList)) {
                for (EntityTeachingChapter entityTeachingChapter : chapterList) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", entityTeachingChapter.getId());
                    map.put("directoryId", entityTeachingChapter.getDirectoryId());
                    map.put("chapterNumber", entityTeachingChapter.getChapterNumber());
                    map.put("parentId", entityTeachingChapter.getParentId());
                    map.put("prefixName", entityTeachingChapter.getPrefixName());
                    map.put("name", entityTeachingChapter.getName());
                    map.put("level", entityTeachingChapter.getLevel());
                    map.put("stageId", entityTeachingChapter.getStageId());
                    map.put("subjectId", entityTeachingChapter.getSubjectId());
                    mapList.add(map);
                }
                Map<String, Object> version = teacherChapterMapper.findVersionByDirectoryId(chapterList.get(0)
                        .getDirectoryId());
                questionData.setVersionId((Long) version.get("version_id"));
            }
        }


        //List<Map<String, Object>> mapList = new ArrayList<>();
        //if (null != entityTeachingChapters && entityTeachingChapters.size() > 0) {
        //            for (Chapter entityTeachingChapter : entityTeachingChapters) {
        //                Map<String, Object> map = new HashMap<>();
        //                map.put("id", entityTeachingChapter.getId());
        //                map.put("directoryId", entityTeachingChapter.getDirectoryId());
        //                map.put("chapterNumber", entityTeachingChapter.getChapterNumber());
        //                map.put("parentId", entityTeachingChapter.getParentId());
        //                map.put("prefixName", entityTeachingChapter.getPrefixName());
        //                map.put("name", entityTeachingChapter.getName());
        //                map.put("level", entityTeachingChapter.getLevel());
        //                map.put("stageId", entityTeachingChapter.getStageId());
        //                map.put("subjectId", entityTeachingChapter.getSubjectId());
        //                mapList.add(map);
        //            }

            /*
            教材版本查询
            */
        //Map<String, Object> version = teacherChapterMapper.findVersionByDirectoryId(entityTeachingChapters.get(0)
        // .getDirectoryId());
        // questionData.setVersionId((Long) version.get("version_id"));
        // }

        /*封装结果集**/
        TypeHtml typeHtml = new TypeHtml();
        typeHtml.setId(questionBean.getQuestionTypeId());
        typeHtml.setName(questionBean.getQuestionType());
        questionData.setType(typeHtml);
        questionData.setChapterInfo(mapList);
        questionData.setLevel(questionBean.getDifficulty());
        questionData.setSource(questionBean.getSource());
        questionData.setUpdateTime(questionBean.getUpdateTime());
        questionData.setCreateTime(questionBean.getUploadTime());
        if(questionBean.getIntelligent()!=null){
            questionData.setIntelligent(questionBean.getIntelligent());
        }else{
            questionData.setIntelligent(0);
        }
        questionData.setUploadId(questionBean.getUploadId());
        /*
            获取自定义目录体系ID
         */
        CustomQuestionResource customQuestionResource = linkCustomQuestionResourceMapper
                .queryCustomQuestionResourceByQuesIdAndSysId(request.getId(), questionBean.getUploadId());
        if (customQuestionResource != null && customQuestionResource.getGroupId() != null) {
            questionData.setGroupId(customQuestionResource.getGroupId());
        }
        /*
            获取自定义目录ID列表
            级联查询
         */
        if (customQuestionResource != null && customQuestionResource.getCustomListId() != null) {
            IdRequest idRequest = new IdRequest(customQuestionResource.getCustomListId());
            CommonResponse<List<CustomListVo>> byLeafId = customListService.findParentsByLeafId(idRequest);
            List<CustomListVo> customListVos = byLeafId.getData();
            if (CollectionUtils.isNotEmpty(customListVos)) {
                List<Map<String, Object>> customerDirectorys = new ArrayList<>();
                for (CustomListVo customListVo : customListVos) {
                    Map<String, Object> customerDirectory = new HashMap<>();
                    customerDirectory.put("level", customListVo.getLevel());
                    customerDirectory.put("groupId", customListVo.getGroupId());
                    customerDirectory.put("cusomerDeritoryId", customListVo.getId());
                    customerDirectory.put("cusomerDeritoryName", customListVo.getName());
                    customerDirectorys.add(customerDirectory);
                }
                questionData.setCustomerDirectory(customerDirectorys);
            }
        }
        /*
            题目类型结构ID structId
         */
        FindAllQuestionTypeResponse allQuestionTypes = questionTypeService.findAllQuestionType(new
                FindAllQuestionTypeRequest());
        long structIdByTypeId = allQuestionTypes.findStructIdByTypeId(questionBean.getQuestionTypeId());
        questionData.setStructId(structIdByTypeId);

        /*
         * 获取主题列表
         */
        //获取当前习题的主题IDS
        List<LinkQuestionTopic> topicsList = linkQuestionTopicMapper.findLinkQuestionTopicByQuestionId(request.getId());
        if (null != topicsList && topicsList.size() > 0) {

            List<Long> topicIds = new ArrayList<>();
            for (LinkQuestionTopic questionTopic : topicsList) {
                topicIds.add(questionTopic.getTopicId());
            }

            ResponseEntity<List<Topic>> topicResp = topicService.findByIds(new RequestEntity<>(topicIds));
            if (!topicResp.success()) {
                throw new BizLayerException(String.format("%s|%s", topicResp.getCode(), topicResp.getMessage()),
                        BasicErrorCode.UNKNOWN);
            }
            List<Topic> entityTopics = topicResp.getEntity();
            if (CollectionUtils.isNotEmpty(entityTopics)) {
                List<Map<String, Object>> topics = new ArrayList<>();
                for (Topic entityTopic : entityTopics) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", entityTopic.getId());
                    map.put("description", entityTopic.getDescription());
                    map.put("mastery", entityTopic.getMastery());
                    map.put("name", entityTopic.getName());
                    map.put("seq", entityTopic.getSeq());
                    map.put("stageId", entityTopic.getStage().getId());
                    map.put("subjectId", entityTopic.getSubject().getId());
                    map.put("unitId", entityTopic.getUnit().getId());
                    topics.add(map);
                }
                questionData.setTopics(topics);
            }
        }

        /**
         *获取题目的专题信息
         */
        List<EntityQuestionSpecial> specialList=questionSpecialMapper.queryQuestionSpecialList(request.getId(),new PageBounds(1,100));
        if(!CollectionUtils.isEmpty(specialList)){
            List<Map<String, Object>> specials = new ArrayList<>();
            for (EntityQuestionSpecial special:specialList){
                Map<String, Object> map = new HashMap<>();
                map.put("id",special.getExamSitesId());
                map.put("name",special.getName());
                specials.add(map);
            }
            questionData.setSpecialInfo(specials);
        }

        /*
            获取题目的html数据信息
         */
        String htmlData = questionBean.getHtmlData();
        //htmlData = StringEscapeUtils.unescapeHtml3(htmlData);
        JSONObject object;
        Object content;
        try {
            object = new JSONObject(htmlData);
            content = object.get("content");
        } catch (JSONException e) {
            htmlData = StringEscapeUtils.unescapeHtml3(htmlData);
            try{
                object = new JSONObject(htmlData);
                content = object.get("content");
                if (null==content){
                    htmlData= htmlData.replaceAll("[\u0000-\u001f]", "");
                    object = new JSONObject(htmlData);
                    content = object.get("content");
                }

            }catch (Exception es){
                logger.error("\n =====>>JSONObject 读取数据库htmlData数据异常:{}", htmlData);
                throw new BizLayerException(e.getMessage(), JSON_CONVERT_FAIL);
            }
        }

        if (content == null) {
            throw new BizLayerException("", QUESTION_DOWN_FAIL);
        }
        Map htmlMap;
        try {
            htmlMap = JsonUtils.fromJson(content.toString(), Map.class);
        } catch (IOException e) {
            logger.error("\n =====>>JsonUtils 转 Map异常:{}", e);
            throw new BizLayerException(e.getMessage(), JSON_CONVERT_FAIL);
        }
        if (htmlData == null) {
            throw new BizLayerException("", QUESTION_DOWN_FAIL);
        }

        List<Map> quesList = (List) htmlMap.get("questions");
        if (CollectionUtils.isNotEmpty(quesList)) {
            for (Map map : quesList) {
                Map typeMap = (Map) map.get("type");
                int typeId = (int) typeMap.get("id");
                long structId = allQuestionTypes.findStructIdByTypeId(typeId);
                map.put("struct_id", structId);
            }
        }
        questionData.setHtml(htmlMap);
        response.setQuestionData(questionData);
        return response;
    }


    /**
     * 删除习题
     *
     * @param request req
     * @return resp
     * @throws BizLayerException e
     */
    @Override
    public CommonDes deleteQuestion(DeleteQuestionRequest request) throws BizLayerException {

        /*
            1.删除题库题目信息
         */
        //entityQuestionMapper.deleteQuestion(request.getId());
        /*
            2.删除自定义目录
         */
        CustomQuestionResource resource = linkCustomQuestionResourceMapper
                .queryCustomQuestionResourceByQuesIdAndSysId(request.getId(), request.getSystemId());
        if (resource == null) {
            logger.warn("题目ID:{},上传者ID:{}自定义目录数据不存在!!!",request.getId(), request.getSystemId());
            //throw new BizLayerException("", PRAXIS_QUESTION_NOT_FOUND);
        }else{
            //删除solr
            List<Long> ids = new ArrayList<>();
            ids.add(resource.getId());
            SolrUtils.fBatchDeleteLqResourceIndex(ids, solrUploadQuestionRabbitTemplate);
            int i = linkCustomQuestionResourceMapper.deleteCustomDiritory(request.getId(), request.getSystemId());
            if (i <= 0) {
                throw new BizLayerException("", QUESTION_DELETE_FAIL);
            }
            //处理历史收藏量
            List<EntityCounterResources> entityCounterResources = entityCounterResMapper.find(Arrays.asList(request.getId()));
            if(CollectionUtils.isNotEmpty(entityCounterResources)&&entityCounterResources.get(0).getFavCount()!=null ){
                if(entityCounterResources.get(0).getFavCount()>0){
                    int i1 = entityCounterResMapper.updateCounter(entityCounterResources.get(0).getFavCount() - 1, request.getId());
                }
            }

            //题集查询
            List<LinkExerciseQuestion> exerciseQuestions = linkExerciseQuestionMapper.findLinkExerciseQuestionById
                    (request.getId());
            //修改题目时，如果该题目属于一个题集，而且该题集被缓存了，需要在此处过期该缓存
            QuestionServiceUtil.delExerciseQuestionCache(exerciseQuestions,redisUtil);
        }
        return new CommonDes();
    }

    /**
     * 更新题目用于草稿，不做校验
     */
    @Override
    public CommonResult updateQuestion_sketch(UpdateQuestionRequest request) throws BizLayerException {

        if(StringUtils.isBlank(request.getHtml())){
            throw new BizLayerException("",QUESTION_CONTENT_NULL_EXCEPTION);
        }

        //题集查询
        List<LinkExerciseQuestion> exerciseQuestions = linkExerciseQuestionMapper.findLinkExerciseQuestionById
                (request.getId());
        //修改题目时，如果该题目属于一个题集，而且该题集被缓存了，需要在此处过期该缓存
        QuestionServiceUtil.delExerciseQuestionCache(exerciseQuestions,redisUtil);

        //封装更新参数
        EntityQuestion entityQuestion = new EntityQuestion();
        entityQuestion.setId(request.getId());
        entityQuestion.setSubjectId(request.getSubjectId());//学科ID
        entityQuestion.setUploadId(request.getUploadId());//上传人ID
        entityQuestion.setDifficulty(request.getLevel());//难易程度

        entityQuestion.setState(QuestionState.INCOMPLETE);
        entityQuestion.setNewFormat(1);//新题标志
        entityQuestion.setUploadSrc(request.getUploadSrc());//区分上传人来源
        entityQuestion.setQuestionTypeId(request.getType()); //题目类型ID

        if (1 == request.getType()) {
            entityQuestion.setQuestionType("选择题");//题型
        } else {
            QuestionType questionType= questionTypeDao.findById(request.getType());
            if(questionType==null){
                throw new BizLayerException("", NULL_QUESTION_TYPE_EXCEPTION);
            }
            entityQuestion.setQuestionType(questionType.getTypeName());//题型
        }

        entityQuestion.setSource(request.getSource());//习题来源

        entityQuestion.setUpdateTime(new Date());

        entityQuestion.setUploadTime(new Date());

        entityQuestion.setVisible(1);

        //创建机构ID
        entityQuestion.setOrgId(request.getOrgId());
        entityQuestion.setOrgType(request.getOrgType());
        entityQuestion.setParentQuestionId(0L);
        entityQuestion.setScore(0F);


        try {
            String json = ParseHtmlUtil.html2json(request.getHtml(), request.getType() + "",
                    entityQuestion.getId(),questionTypeDao);
            entityQuestion.setJsonData(json);
        } catch (IOException e) {
            logger.error("HTML转JSON异常:{}", e);
            throw new BizLayerException("HTML 转 JSON 异常", JSON_CONVERT_FAIL);
        }
        Object o = JsonUtil.readValue(request.getHtml(), Object.class);
        Map<String, Object> wrapper = new HashMap<>();
        wrapper.put("question_id", entityQuestion.getId());
        wrapper.put("content", o);
        entityQuestion.setHtmlData(JsonUtil.obj2Json(wrapper));

        /*
            -- 根据用户ID和题目ID更新自定义目录
         */
        Long link_cus_id;

        CustomQuestionResource questionResource = linkCustomQuestionResourceMapper
                .queryCustomQuestionResourceByQuesIdAndSysId(request.getId(), request.getUploadId());
        if (questionResource != null) {  //存在走更新逻辑
            link_cus_id = questionResource.getId();
            CustomQuestionResource customQuestionResource = new CustomQuestionResource();
            customQuestionResource.setQuestionId(request.getId());
            customQuestionResource.setSystemId(request.getUploadId());
            customQuestionResource.setUpdateTime(new Date());
            customQuestionResource.setGroupId(request.getGroupId());
            customQuestionResource.setCustomListId(request.getCustomerDirectory());
            int i = linkCustomQuestionResourceMapper.updateLinkCustomQuestionResource(customQuestionResource);
            if (i <= 0) {
                logger.info("更新自定义目录失败，入参：{}", customQuestionResource);
                throw new BizLayerException("", CUS_DIR_UPDATE_FAIL);
            }
        } else {   //不存在走创建逻辑
            CustomQuestionResource customQuestionResource = new CustomQuestionResource();
            customQuestionResource.setQuestionId(request.getId());
            customQuestionResource.setSystemId(request.getUploadId());
            customQuestionResource.setCreateTime(new Date());
            customQuestionResource.setUpdateTime(new Date());
            customQuestionResource.setGroupId(request.getGroupId());
            customQuestionResource.setCustomListId(request.getCustomerDirectory());
            customQuestionResource.setIsFav(0);
            customQuestionResource.setResourceStatus(1);
            int i = linkCustomQuestionResourceMapper.insertLinkCustomQuestionResource(customQuestionResource);
            if (i <= 0) {
                logger.info("插入自定义目录失败，入参：{}", customQuestionResource);
                throw new BizLayerException("", INSERT_DIRECTORY_FAIL);
            }
            link_cus_id = customQuestionResource.getId();
        }


        //是否是单题
        int is_single = 1;
        String html = request.getHtml();
        try {
            int questionsLength = JsonUtils.getQuestionsLength(html, "questions");
            if (questionsLength > 0) {//复合题
                is_single = 0;
            }
        } catch (BizLayerException e) {
            throw new BizLayerException("更新习题失败", JSON_CONVERT_FAIL);
        }

        entityQuestion.setIsSingle(is_single);

        if (1 == is_single) { //单题
            SingleQuestion singleQuestion;
            try {
                singleQuestion = JsonUtils.fromJson(html, SingleQuestion.class);
                if (1 == request.getType()) {//选择题
                    entityQuestion.setRightOption(singleQuestion.getAnswer().toString());//选择题正确选项
                    entityQuestion.setCountOptions(singleQuestion.getOptions().size());//选项数量

                }
                if (CollectionUtils.isNotEmpty(singleQuestion.getAnswer())) {
                    entityQuestion.setAnswerNum(singleQuestion.getAnswer().size());//答案个数
                } else {
                    entityQuestion.setAnswerNum(0);//答案个数
                }
                //是否支持智能批改
                if(singleQuestion.getAnswer()!=null && entityQuestion.getQuestionTypeId()!=null){
                    entityQuestion.setIntelligent(QuestionServiceUtil.getIntelligent(singleQuestion.getAnswer(),
                            entityQuestion.getQuestionTypeId(),entityQuestion.getSubjectId()));
                }else {
                    entityQuestion.setIntelligent(0);
                }
                /*
                    作图题底图数据操作
                 */
                handleQuestionBasemap(entityQuestion,singleQuestion);


            } catch (IOException e) {
                logger.error("html转换单题实体异常={}", e.getMessage());
            }
        }else{
            entityQuestion.setIntelligent(0);
        }
        //特殊处理白名单学校的资源状态
        if(QuestionServiceUtil.isWhiteSchool(auditsedSchoolMapper,request.getOrgId(),redisUtil)){
            entityQuestion.setState(QuestionState.ENABLED);
        }
        int question = entityQuestionMapper.updateQuestion(entityQuestion);
        if (question <= 0) {  //更新失败
            logger.error("更新习题失败");
            throw new BizLayerException("", QUESTION_UPDATE_FAIL);
        }

        if (0 == is_single) {

            /*
             * 清除智能批改数据缓存
             */
            redisUtil.del(RedisKeyUtil.LEAF_QUESTION_PREFIX + request.getId());

            //复合题 批量更新
            //1.删除solr复合题下所有的子题
            //①查询此复合题下的所有子题题目ID
            List<Long> subIds = entityQuestionMapper.findQuestionSubjIdByParentId(request.getId());
            //②批量删除solr子题(物理删除)
            SolrUtils.fBatchDeleteIndex(subIds, solrUploadQuestionRabbitTemplate);

            //2.删除数据库复合题下所有子题
            //①通过大题题目ID删除当前大题下的所有子题
            entityQuestionMapper.deleteQuestionByParentId(request.getId());

            //更新复合题大题
            entityQuestionMapper.updateQuestion(entityQuestion);

            //2.重新添加复合题下的子题
            ComplexQuestion complexQuestion;
            try {
                complexQuestion = JsonUtils.fromJson(html, ComplexQuestion.class);
            } catch (IOException e) {
                logger.error("html转复合题实体异常", e.getMessage());
                throw new BizLayerException(e, JSON_CONVERT_FAIL);
            }
            List<com.noriental.praxissvr.question.bean.html.Question> questions = complexQuestion.getQuestions();
            /*
             * 用于批量插入复合题
             */
            List<EntityQuestion> entityQuestionList = new ArrayList<>();
            /*
             * 批量获取数据库ID
             */
            List<Long> longList = getBatchQuestionId(questions.size());
            //批量添加复合题
            for (int j = 0; j < questions.size(); j++) {

                EntityQuestion entityf = new EntityQuestion();
                entityf.setId(longList.get(j));
                if (questions.get(j).getType().getId() == 0 || questions.get(j).getType().getId() == 1) {
                    entityf.setRightOption(questions.get(j).getAnswer().toString());//正确答案
                    entityf.setCountOptions(questions.get(j).getOptions().size());//选项数量
                }
                if (CollectionUtils.isNotEmpty(questions.get(j).getAnswer())) {
                    entityf.setAnswerNum(questions.get(j).getAnswer().size());//正确答案数
                } else {
                    entityf.setAnswerNum(0);//正确答案数
                }
                entityf.setQuestionTypeId(questions.get(j).getType().getId());//题型ID
                entityf.setQuestionType(questions.get(j).getType().getName());//题型
                //难度
                if(StringUtils.isEmpty(questions.get(j).getDifficulty())){
                    entityf.setDifficulty(1);
                }else{
                    entityf.setDifficulty(Integer.valueOf(questions.get(j).getDifficulty()));
                }
                entityf.setIsSingle(1);//单题
                entityf.setParentQuestionId(request.getId());//大题ID
                entityf.setState(QuestionState.PREVIEWED);//待审核
                entityf.setSubjectId(request.getSubjectId());//学科ID
                entityf.setUploadId(request.getUploadId());//上传人ID
                entityf.setNewFormat(1);//是否是新题
                entityf.setUploadTime(new Date());
                entityf.setUpdateTime(new Date());
                entityf.setVisible(1);
                entityf.setScore(0F);
                entityf.setOrgId(request.getOrgId());
                entityf.setOrgType(request.getOrgType());
                entityf.setUploadSrc(request.getUploadSrc());
                if(questions.get(j).getAnswer()!=null){
                    entityf.setIntelligent(QuestionServiceUtil.getIntelligent(questions.get(j).getAnswer(),
                            questions.get(j).getType().getId(),entityf.getSubjectId()));
                }else{
                    entityf.setIntelligent(0);
                }
                if(QuestionServiceUtil.isWhiteSchool(auditsedSchoolMapper,request.getOrgId(),redisUtil)){
                    entityf.setState(QuestionState.ENABLED);
                }

                if (questions.get(j).getType().getId() == 51) {
                    NewMap jsonMapImgae = questions.get(j).getMap();
                    try {
                        String json = JSON.json(jsonMapImgae);
                        entityf.setJsonMap(json);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                entityQuestionList.add(entityf);
            }
            int iList = entityQuestionMapper.batchInsertQuestion(entityQuestionList);
            if (iList <= 0) {//批量插入失败
                logger.error("batchInsertQuestion={}", "批量插入失败");
                throw new BizLayerException("", BATCH_INSERT_FAIL);
            }
        }

        /*
         * 处理主题和章节信息
         */
        //1.先删除原有的主题和章节
        linkQuestionTopicMapper.deleteQuestionTopicLinkById(request.getId());
        linkQuestionChapterMapper.deleteLinkQuestionChapterById(request.getId());
        questionSpecialMapper.deleteQuestionSpecialById(request.getId());
        //2.添加新的主题章节依赖
        List<Long> topicIds = request.getTopicIds();

        /*
         * 更新习题知识点数最多为10个
         */
        if (topicIds != null && topicIds.size() > 10) {
            throw new BizLayerException("", TOPIC_COUNT);
        }

        //用户主题关联批量
        List<Map<String, Object>> listTopics = new ArrayList<>();

        if (null != topicIds && topicIds.size() > 0) {

            for (Long topicId : topicIds) {
                Map<String, Object> mapTopic = new HashMap<>();
                mapTopic.put("questionId", entityQuestion.getId());
                mapTopic.put("topicId", topicId);
                listTopics.add(mapTopic);
            }

            /*
             * 主题关联批量插入
             */
            int iTopic = linkQuestionTopicMapper.batchQuestionTopicLink(listTopics);
            if (iTopic <= 0) {
                logger.error("linkQuestionTopic={}", "主题关联批量插入失败");
                throw new BizLayerException("主题关联批量插入失败", BATCH_INSERT_FAIL);
            }
        }


        //章节插入
        int questionChapter = linkQuestionChapterMapper.createLinkQuestionChapter(request.getId(), request
                .getChapterId());
        if (questionChapter <= 0) {
            logger.error("createLinkQuestionChapter={}", "章节关联插入失败");
            throw new BizLayerException("章节关联插入失败", BATCH_INSERT_FAIL);
        }

        //专题处理
        List<Long> specials=request.getSpecials();
        if(!CollectionUtils.isEmpty(specials)){
            handleSpecials(specials,request.getId());
        }

         /*
            发送solr关联关系表
         */
        logger.info("updateQuestion_sketch=====entityQuestion===={},==CustomerDirectory====={}," +
                "=====customQuestionResource======{}", entityQuestion, request.getCustomerDirectory(), link_cus_id);
        sendLinkQuestionIndex(entityQuestion, request.getCustomerDirectory(), link_cus_id, 0);

        //更新solr
        try {
            updateQuestionSolrIndex(entityQuestion, html, request.getChapterId(), request.getTopicIds(), null,
                    request.getCustomerDirectory());
        } catch (Exception e) {
            logger.error("更新solr异常{}", e.getMessage());
            throw new BizLayerException("", UPDATE_SOLR_FAIL);
        }

        // 更新音频七牛回调
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
            if (complexQuestion != null && complexQuestion.getAudio() != null && complexQuestion.getAudio().getUrl()
                    != null && !"".equals(complexQuestion.getAudio().getUrl())) {
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


    /**
     * 更新习题
     *
     * @param request req
     * @return resp
     * @throws BizLayerException e
     */
    @Override
    public CommonResult updateQuestion(UpdateQuestionRequest request) throws BizLayerException {
        logger.info("更新题目开始入参为：{}", request);
        /*
            查询题目是否已经
            1.平台审核通过
            2.题集中已经存在
            3.题目是否被收藏
         */
        if(StringUtils.isBlank(request.getHtml())){
            throw new BizLayerException("",QUESTION_CONTENT_NULL_EXCEPTION);
        }
        EntityQuestion entityQuestion11 = entityQuestionMapper.findQuestionById(request.getId());
        //题集查询
        List<LinkExerciseQuestion> exerciseQuestions = linkExerciseQuestionMapper.findLinkExerciseQuestionById
                (request.getId());
        //修改题目时，如果该题目属于一个题集，而且该题集被缓存了，需要在此处过期该缓存
        QuestionServiceUtil.delExerciseQuestionCache(exerciseQuestions,redisUtil);
        //判断题目是否被收藏
        CustomQuestionResource resource1 = linkCustomQuestionResourceMapper
                .queryCustomQuestionResourceByQuesIdAndSysId(request.getId(), request.getUploadId());

        //审核通过或者被收藏，删除此习题,并且新建
        if ((resource1 != null && resource1.getIsFav() == 1) || (exerciseQuestions != null && exerciseQuestions.size
                () > 0) || (entityQuestion11 != null && QuestionState.ENABLED.equals(entityQuestion11.getState()))) {

            //如果当前编辑题集的题目为操作人本人的题集，那么执行删除然后创建，如果为非本人题集，那么直接创建，不做删除
            if ((entityQuestion11.getUploadId() == request.getUploadId())||(resource1 != null&&resource1.getSystemId()
                    ==request.getUploadId())) {

                //删除习题
                //LongRequest delReq = new LongRequest();
                DeleteQuestionRequest delReq = new DeleteQuestionRequest();
                delReq.setId(request.getId());
                delReq.setCusDirId(request.getCustomerDirectory());
                delReq.setSystemId(request.getUploadId());
                deleteQuestion(delReq);

            }

            /*
             * 新建习题
             */
            //封装参数
            UploadQuestionRequest uploadQuestionRequest = new UploadQuestionRequest();
            uploadQuestionRequest.setId(getQuestionId());
            uploadQuestionRequest.setHtml(request.getHtml());
            if (request.getTopicIds() != null && request.getTopicIds().size() > 0) {
                uploadQuestionRequest.setTopicIds(request.getTopicIds());
            }
            uploadQuestionRequest.setChapterId(request.getChapterId());
            uploadQuestionRequest.setLevel(request.getLevel());
            uploadQuestionRequest.setType(request.getType());
            uploadQuestionRequest.setRef(request.getRef());
            uploadQuestionRequest.setSubjectId(request.getSubjectId());
            uploadQuestionRequest.setUploadId(request.getUploadId());
            uploadQuestionRequest.setUploadSrc(request.getUploadSrc());
            uploadQuestionRequest.setSource(request.getSource() != null ? request.getSource() : "");
            uploadQuestionRequest.setHtml(request.getHtml());
            uploadQuestionRequest.setGroupId(request.getGroupId());
            uploadQuestionRequest.setCustomerDirectory(request.getCustomerDirectory());
            uploadQuestionRequest.setSpecials(request.getSpecials());

            //题目创建机构id
            uploadQuestionRequest.setOrgId(request.getOrgId());
            uploadQuestionRequest.setOrgType(request.getOrgType());
            //创建

            return createQuestion(uploadQuestionRequest);
        }

        //============================================================================================================
        /*
         * 题集中不存在或者没有通过审核，直接更新
         */
        //封装更新参数
        EntityQuestion entityQuestion = new EntityQuestion();
        entityQuestion.setId(request.getId());
        entityQuestion.setSubjectId(request.getSubjectId());//学科ID
        entityQuestion.setUploadId(request.getUploadId());//上传人ID
        entityQuestion.setDifficulty(request.getLevel());//难易程度
        if (53 == request.getType()) {       //如果为拍照提那么状态为BANNED
            entityQuestion.setState(QuestionState.BANNED);
        } else {
            entityQuestion.setState(QuestionState.PREVIEWED);
        }
        entityQuestion.setNewFormat(1);//新题标志
        entityQuestion.setUploadSrc(request.getUploadSrc());//区分上传人来源
        entityQuestion.setQuestionTypeId(request.getType()); //题目类型ID

        if (1 == request.getType()) {
            entityQuestion.setQuestionType("选择题");//题型
        } else {
            QuestionType questionType= questionTypeDao.findById(request.getType());
            if(questionType==null){
                throw new BizLayerException("", NULL_QUESTION_TYPE_EXCEPTION);
            }
            entityQuestion.setQuestionType(questionType.getTypeName());//题型
        }

        entityQuestion.setSource(request.getSource());//习题来源

        entityQuestion.setUpdateTime(new Date());
        entityQuestion.setUploadTime(entityQuestion11.getUploadTime());
        entityQuestion.setVisible(1);
        entityQuestion.setIsFinishedProduct(0);
        entityQuestion.setScore(0F);

        //创建机构ID
        entityQuestion.setOrgId(request.getOrgId());
        entityQuestion.setOrgType(request.getOrgType());
        entityQuestion.setParentQuestionId(0L);
        String html="";
        html=getQuestionHtmlString(request.getHtml(),request.getType());


        try {
            String json = ParseHtmlUtil.html2json(html, request.getType() + "",
                    entityQuestion.getId(),questionTypeDao);
            entityQuestion.setJsonData(json);
        } catch (IOException e) {
            logger.error("HTML转JSON异常:{}", e);
            throw new BizLayerException("HTML 转 JSON 异常", JSON_CONVERT_FAIL);
        }
        Object o = JsonUtil.readValue(html, Object.class);
        Map<String, Object> wrapper = new HashMap<>();
        wrapper.put("question_id", entityQuestion.getId());
        wrapper.put("content", o);
        entityQuestion.setHtmlData(JsonUtil.obj2Json(wrapper));


        CustomQuestionResource customQuestionResource = new CustomQuestionResource();

        customQuestionResource.setSystemId(request.getUploadId());
        customQuestionResource.setUpdateTime(new Date());
        customQuestionResource.setCustomListId(request.getCustomerDirectory());
        customQuestionResource.setQuestionId(entityQuestion.getId());
        customQuestionResource.setGroupId(request.getGroupId());
        int resource = linkCustomQuestionResourceMapper.updateLinkCustomQuestionResource(customQuestionResource);
        if (resource <= 0) {
            logger.info("插入自定义目录失败，入参：{}", customQuestionResource);
            throw new BizLayerException("", INSERT_DIRECTORY_FAIL);
        }


        //是否是单题
        int is_single = 1;

        try {
            int questionsLength = JsonUtils.getQuestionsLength(html, "questions");
            if (questionsLength > 0) {//复合题
                is_single = 0;
            }
        } catch (BizLayerException e) {
            throw new BizLayerException("更新习题失败", JSON_CONVERT_FAIL);
        }

        entityQuestion.setIsSingle(is_single);

        if (1 == is_single) { //单题
            SingleQuestion singleQuestion;
            try {
                singleQuestion = JsonUtils.fromJson(html, SingleQuestion.class);
                if (1 == request.getType()) {//选择题
                    entityQuestion.setRightOption(singleQuestion.getAnswer().toString());//选择题正确选项
                    entityQuestion.setCountOptions(singleQuestion.getOptions() != null ? singleQuestion.getOptions()
                            .size() : 0);//选项数量
                    if(singleQuestion.getAnswer().size()>=2){
                        entityQuestion.setQuestionType("多选题");
                    }
                }
                entityQuestion.setAnswerNum(singleQuestion.getAnswer() != null ? singleQuestion.getAnswer().size() :
                        0);//答案个数
                //是否支持智能批改
                if(singleQuestion.getAnswer()!=null && entityQuestion.getQuestionTypeId()!=null){
                    entityQuestion.setIntelligent(QuestionServiceUtil.getIntelligent(singleQuestion.getAnswer(),
                            entityQuestion.getQuestionTypeId(),entityQuestion.getSubjectId()));
                }else {
                    entityQuestion.setIntelligent(0);
                }
                /*
                    更新底图信息
                 */
                handleQuestionBasemap(entityQuestion, singleQuestion);

            } catch (IOException e) {
                logger.error("html转换单题实体异常={}", e.getMessage());
            }

        }else{
            entityQuestion.setIntelligent(0);
        }
        //特殊处理学校白名单
        if(QuestionServiceUtil.isWhiteSchool(auditsedSchoolMapper,request.getOrgId(),redisUtil)){
            entityQuestion.setState(QuestionState.ENABLED);
        }
        logger.debug("更新数据库数据为：{}", entityQuestion.toString());
        int question = entityQuestionMapper.updateQuestion(entityQuestion);
        if (question <= 0) {  //更新失败
            logger.error("更新习题失败");
            throw new BizLayerException("", QUESTION_UPDATE_FAIL);
        }

        if (0 == is_single) {
            /*
             * 清除智能批改的数据缓存
             */
            redisUtil.del(RedisKeyUtil.LEAF_QUESTION_PREFIX + request.getId());

            //复合题 批量更新
            //1.删除solr复合题下所有的子题
            //①查询此复合题下的所有子题题目ID
            List<Long> subIds = entityQuestionMapper.findQuestionSubjIdByParentId(request.getId());
            //②批量删除solr子题(物理删除)
            SolrUtils.fBatchDeleteIndex(subIds, solrUploadQuestionRabbitTemplate);

            //2.删除数据库复合题下所有子题
            //①通过大题题目ID删除当前大题下的所有子题
            entityQuestionMapper.deleteQuestionByParentId(request.getId());

            //更新复合题大题
            entityQuestionMapper.updateQuestion(entityQuestion);

            //2.重新添加复合题下的子题
            ComplexQuestion complexQuestion;
            try {
                complexQuestion = JsonUtils.fromJson(html, ComplexQuestion.class);
            } catch (IOException e) {
                logger.error("html转复合题实体异常", e.getMessage());
                throw new BizLayerException(e, JSON_CONVERT_FAIL);
            }
            List<com.noriental.praxissvr.question.bean.html.Question> questions = complexQuestion.getQuestions();
            /*
             * 用于批量插入复合题
             */
            List<EntityQuestion> entityQuestionList = new ArrayList<>();
            /*
             * 批量获取数据库ID
             */
            List<Long> longList = getBatchQuestionId(questions.size());
            //批量添加复合题
            for (int j = 0; j < questions.size(); j++) {

                EntityQuestion entityf = new EntityQuestion();
                entityf.setId(longList.get(j));
                if (questions.get(j).getType().getId() == 0 || questions.get(j).getType().getId() == 1) {
                    entityf.setRightOption(questions.get(j).getAnswer().toString());//正确答案
                    entityf.setCountOptions(questions.get(j).getOptions().size());//选项数量
                }
                entityf.setAnswerNum(questions.get(j).getAnswer().size());//正确答案数
                entityf.setQuestionTypeId(questions.get(j).getType().getId());//题型ID
                entityf.setQuestionType(questions.get(j).getType().getName());//题型
                //难度
                if(StringUtils.isEmpty(questions.get(j).getDifficulty())){
                    entityf.setDifficulty(1);
                }else{
                    entityf.setDifficulty(Integer.valueOf(questions.get(j).getDifficulty()));
                }

                entityf.setIsSingle(1);//单题
                entityf.setParentQuestionId(request.getId());//大题ID
                entityf.setState(QuestionState.PREVIEWED);//待审核
                entityf.setSubjectId(request.getSubjectId());//学科ID
                entityf.setUploadId(request.getUploadId());//上传人ID
                entityf.setNewFormat(1);//是否是新题
                entityf.setUploadTime(new Date());
                entityf.setUpdateTime(new Date());
                entityf.setVisible(1);
                entityf.setScore(0F);
                entityf.setOrgId(request.getOrgId());
                entityf.setOrgType(request.getOrgType());
                entityf.setUploadSrc(request.getUploadSrc());
                if(questions.get(j).getAnswer()!=null){
                    entityf.setIntelligent(QuestionServiceUtil.getIntelligent(questions.get(j).getAnswer(),
                            questions.get(j).getType().getId(),entityf.getSubjectId()));
                }else{
                    entityf.setIntelligent(0);
                }
                //白名单学校
                if(QuestionServiceUtil.isWhiteSchool(auditsedSchoolMapper,request.getOrgId(),redisUtil)){
                    entityf.setState(QuestionState.ENABLED);
                }

                if (questions.get(j).getType().getId() == 51) {
                    NewMap jsonMapImgae = questions.get(j).getMap();
                    try {
                        String json = JSON.json(jsonMapImgae);
                        entityf.setJsonMap(json);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                entityQuestionList.add(entityf);
            }
            int iList = entityQuestionMapper.batchInsertQuestion(entityQuestionList);
            if (iList <= 0) {//批量插入失败
                logger.error("batchInsertQuestion={}", "批量插入失败");
                throw new BizLayerException("", BATCH_INSERT_FAIL);
            }
        }

        /*
         * 处理主题和章节信息
         */
        //1.先删除原有的主题和章节;180426新增删除原有专题数据
        linkQuestionTopicMapper.deleteQuestionTopicLinkById(request.getId());
        linkQuestionChapterMapper.deleteLinkQuestionChapterById(request.getId());
        questionSpecialMapper.deleteQuestionSpecialById(request.getId());
        //2.添加新的主题章节依赖
        List<Long> topicIds = request.getTopicIds();

        /*
         * 更新习题知识点数最多为10个
         */
        if (topicIds != null && topicIds.size() > 10) {
            throw new BizLayerException("", TOPIC_COUNT);
        }

        //用户主题关联批量
        List<Map<String, Object>> listTopics = new ArrayList<>();

        if (null != topicIds && topicIds.size() > 0) {

            for (Long topicId : topicIds) {
                Map<String, Object> mapTopic = new HashMap<>();
                mapTopic.put("questionId", entityQuestion.getId());
                mapTopic.put("topicId", topicId);
                listTopics.add(mapTopic);
            }

            /*
             * 主题关联批量插入
             */
            int iTopic = linkQuestionTopicMapper.batchQuestionTopicLink(listTopics);
            if (iTopic <= 0) {
                logger.error("linkQuestionTopic={}", "主题关联批量插入失败");
                throw new BizLayerException("主题关联批量插入失败", BATCH_INSERT_FAIL);
            }
        }

        //章节插入
        int questionChapter = linkQuestionChapterMapper.createLinkQuestionChapter(request.getId(), request
                .getChapterId());
        if (questionChapter <= 0) {
            logger.error("createLinkQuestionChapter={}", "章节关联插入失败");
            throw new BizLayerException("章节关联插入失败", BATCH_INSERT_FAIL);
        }
        //更新题目专题信息处理
        List<Long> specials= request.getSpecials();
        if(!CollectionUtils.isEmpty(specials)){
            handleSpecials(specials,request.getId());
        }

        //更新solr
        try {
            updateQuestionSolrIndex(entityQuestion, html, request.getChapterId(), request.getTopicIds(), null,
                    request.getCustomerDirectory());
        } catch (Exception e) {
            logger.error("更新solr异常{}", e.getMessage());
            throw new BizLayerException("", UPDATE_SOLR_FAIL);
        }
        /*
            更新关联solr视图
         */
        logger.info("/n 更新solr关联视图========={}", resource1.getId());
        sendLinkQuestionIndex(entityQuestion, request.getCustomerDirectory(), resource1.getId(), 0);


        /*
            更新音频七牛回调
        */


        if (is_single == 1) {
            SingleQuestion singleQuestion;
            try {
                singleQuestion = JsonUtils.fromJson(request.getHtml(), SingleQuestion.class);
            } catch (IOException e) {
                logger.info("html内容为：" + request.getHtml());
                throw new BizLayerException("", JSON_CONVERT_FAIL);
            }
            if (singleQuestion != null && singleQuestion.getAudio() != null && singleQuestion.getAudio().getUrl()
                    != null && !"".equals(singleQuestion.getAudio().getUrl())) {
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
            if (complexQuestion != null &&complexQuestion.getAudio()!=null && complexQuestion.getAudio().getUrl() != null && !"".equals
                    (complexQuestion.getAudio().getUrl())) {
                logger.info("----------------------------更新习题，七牛回调开始------------------------------------");
                ConvertMp3Request convertMp3Request = new ConvertMp3Request();
                convertMp3Request.setEntityId(entityQuestion.getId() + "");
                convertMp3Request.setAudioUrl(complexQuestion.getAudio().getUrl());

                try {
                    CommonResponse<String> convertMp3 = qiniuVoiceService.convertMp3(convertMp3Request);
                    logger.info("--------------更新习题，七牛回调结束返回convertMp3+" + convertMp3.getData() +
                            "------------------");
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
    //处理单题作图题底图数据
    private void handleQuestionBasemap(EntityQuestion entityQuestion, SingleQuestion singleQuestion) {
        if (entityQuestion.getQuestionTypeId() == 51) {
            if (singleQuestion != null && singleQuestion.getMap() != null) {
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


    @Override
    public FindQuestionsResponse findMine(FindMyQuestionRequest request) throws BizLayerException {
        logger.info("开始获取题目列表的请求入参为={}", request.toString());
        FindQuestionsResponse resp = new FindQuestionsResponse();
        /*
            1.构建索引条件
         */

        Map<String, Object> queryConf = new HashMap<>();
        //分页
        //if (request.isPageable()) {
            if(request.getIsOffset()!=null &&request.getIsOffset().equals(1)){
                queryConf.put(QueryMap.KEY_START, request.getPageNo()-1);
                queryConf.put(QueryMap.KEY_ROWS, request.getPageSize());
            }else{
                queryConf.put(QueryMap.KEY_START, (request.getPageNo() - 1) * request.getPageSize());
                queryConf.put(QueryMap.KEY_ROWS, request.getPageSize());
            }
        //}
        //按照更新时间倒序
        //queryConf.put("sort", );
        queryConf.put("sort", Arrays.asList("_9isFav",QuestionSort.UPDATE_TIME_DESC.getSort()) );



        Map<String, Object> qMap = new HashMap<>();

        //单选题和多选题的搜索条件

        QuestionType solrType= questionTypeDao.findById(request.getQuestionType());
        //还需要处理-1，-2
        if(solrType==null){
            qMap.put("questionType", "*");
        }else{
            qMap.put("questionType", solrType.getTypeName());
        }
        if(request.getQuestionType()==-1||request.getQuestionType()==-2){
            qMap.put("questionType", "选择题");
        }
        //单选题获取多选题索引
        if (request.getQuestionType() == -1) {
            qMap.put("answerNum", 1);
        } else if (request.getQuestionType() == -2) {
            qMap.put("answerNum", "[2 TO *]");
        }
        //学科
        if(null != request.getSubjectId()){
            qMap.put("subjectId", request.getSubjectId());
        }
        //如果查我收藏的
        if(request.getIsFav()!=null&& request.getIsFav().equals(1)){
            qMap.put("isFav",1);
        }
        //如果查我创建的
        if(request.getIsCreate()!=null && request.getIsCreate().equals(1)){
            qMap.put("isFav",0);
        }

        // 题目是否可见
        qMap.put("resourceStatus", 1);

        //上传者
        Long systemId = request.getSystemId();
        if (systemId != null) {
            qMap.put("systemId", systemId);
        }
        /*
             自定义目录查询条件
         */
        if (request.getCusDirId1() != null) {
            qMap.put("customListId1", request.getCusDirId1());
        }
        if (request.getCusDirId2() != null) {
            qMap.put("customListId2", request.getCusDirId2());
        }
        if (request.getCusDirId3() != null) {
            qMap.put("customListId3", request.getCusDirId3());
        }


        //难易程度
        Difficulty difficulty = request.getDifficulty();
        if (difficulty != null) {
            qMap.put("difficulty", difficulty.getCode());
        }
        //题目的审核状态
        List<QuestionState> questionStates = Arrays.asList(QuestionState.ENABLED, QuestionState.BANNED, QuestionState
                .PREVIEWED);
        List<String> states = new ArrayList<>();
        for (QuestionState questionState : questionStates) {
            states.add(questionState.toString());
        }
        qMap.put("state", states);
        qMap.put("visible",1);
        qMap.put("newFormat",1);


        queryConf.put("q", qMap);
        logger.info("solr queryConf:" + queryConf);

        SolrQueryPageRsp<LqResourceDocument> linkQuestDir = lqResourceSolrSearchService.search(new SolrQueryPageReq
                (queryConf));

        long totalPage = 0L;
        long totalCount = 0L;
        long currentPage = 1L;
        //分页信息
        if (linkQuestDir.getPage() != null) {
            totalPage = linkQuestDir.getPage().getTotalPage();
            totalCount = linkQuestDir.getPage().getTotalCount();
            currentPage = linkQuestDir.getPage().getCurrentPage();
            //logger.info("总记录数=============={}",totalCount);
        }

        /*
            通过题目Id列表查询题目信息
         */
        List<Long> questionIds = new ArrayList<>();

        if (!linkQuestDir.success()) {
            throw new BizLayerException("", ANSWER_RECORD_QUES_NOT_FOUND);
        }
        List<LqResourceDocument> documentList=new ArrayList<>();
        if (linkQuestDir.success()) {
            documentList = linkQuestDir.getPage().getList();
            for (LqResourceDocument lqResourceDocument : documentList) {
                questionIds.add(lqResourceDocument.getQuestionId());
            }
        }
        logger.info("=获取题目列表IDs={},=={}", questionIds, request.getReqId());
        //===============查询题库视图==============

        Map<String, Object> idQueryConf = new HashMap<>();
        Map<String, Object> idqMap = new HashMap<>();
        //第二次查询不允许分页
        if (request.isPageable()) {
            idQueryConf.put(QueryMap.KEY_ROWS, request.getPageSize());
        }

        FutureTask<Map<Long, TeacherSpaceQuestion>> future = null;
        if(CollectionUtils.isNotEmpty(questionIds)){
            //查询题目的统计信息
            Callable<Map<Long, TeacherSpaceQuestion>> callable = new QueryQuestionReportThread(questionIds);
            future = new FutureTask<>(callable);
            new Thread(future).start();
        }


        if (CollectionUtils.isNotEmpty(questionIds)) {

            idqMap.put("id", questionIds);
            idqMap.put("visible", QuestionVisibleEnum.VISIBLE.getSolrValue());
            idqMap.put("state", states);
            /*
                排序根据传过来的ID顺序进行排序
             */
            HashMap<String, Object> raped = new HashMap<>();
            raped.put("sortedIds", questionIds);
            idQueryConf.put("raped", raped);
            idQueryConf.put("q", idqMap);
            logger.info("SolrQueryPageReq:" + JsonUtil.obj2Json(new SolrQueryPageReq(idQueryConf)));
            SolrQueryPageRsp<QuestionDocument> search = questionSolrSearchService.search(new SolrQueryPageReq
                    (idQueryConf));

            if (!search.success()) {
                throw new BizLayerException(String.format("查询主题数据异常|%s|%s", search.getMessage(), search.getCode()),
                        PRAXIS_INVOKE_SOLR);
            }
            SolrPage<QuestionDocument> solrPages = search.getPage();
            // 所有主题
            List<QuestionDocument> allQuestionDocuments = solrPages.getList();

            //当前页的所有复合题的id
            List<Long> parIds = new ArrayList<>();
            for (QuestionDocument questionDocument : allQuestionDocuments) {
                if (questionDocument != null && BooleanUtils.isNotTrue(questionDocument.getIsSingle() != null &&
                        questionDocument.getIsSingle() == 1) && null != questionDocument.getHtmlData() && null !=
                        questionDocument.getJsonData()) {
                    parIds.add(questionDocument.getId());
                }
            }

            //查询复合题的子题
            List<QuestionDocument> allSubQuestList = new ArrayList<>();
            if (parIds.size() > 0) {
                long l2 = System.currentTimeMillis();
                Map<String, Object> queryMap = new HashMap<>();
                Map<String, Object> subQMap = new HashMap<>();
                //分页
                queryMap.put(QueryMap.KEY_START, 0);
                //分页 复合题最多包含20个子题
                queryMap.put(QueryMap.KEY_ROWS, parIds.size() * 20);
                //按照ID正序
                queryMap.put("sort", "id");
                subQMap.put("parentQuestionId", parIds);
                //新题
                subQMap.put("newFormat", 1);
                //过滤掉停用的题目
                subQMap.put(QueryMap.KeyPrefix.LOGIC_NOT.getValue() + "state", QuestionState.DISABLED.toString());

                queryMap.put("q", subQMap);

                // 获取复合题下的子题
                SolrQueryPageRsp<QuestionDocument> searchSub = questionSolrSearchService.search(new SolrQueryPageReq
                        (queryMap));
                if (!searchSub.success()) {
                    throw new BizLayerException(String.format("查询子题数据异常|%s|%s", search.getMessage(), search.getCode()
                    ), PRAXIS_INVOKE_SOLR);
                }
                SolrPage<QuestionDocument> solrPagesSubQuestion = searchSub.getPage();
                allSubQuestList = solrPagesSubQuestion.getList();
            }
            List<Question> returnList = new ArrayList<>();

            if (CollectionUtils.isNotEmpty(allQuestionDocuments)) {
                Set<Long> topicIds = new HashSet<>();
                // 遍历试题
                List<Long> resultQuestionIds = new ArrayList<>();
                FindAllQuestionTypeResponse allQuestionTypes = questionTypeService.findAllQuestionType(new
                        FindAllQuestionTypeRequest());
                for (QuestionDocument questDoc : allQuestionDocuments) {
                    if(StringUtils.isBlank(questDoc.getHtmlData())){
                        continue;
                    }

                    // solr中试题信息
                    try {
                        com.noriental.praxissvr.question.bean.Question question = EntityUtils.copyValueDeep2Object
                                (questDoc, 1, com.noriental.praxissvr.question.bean.Question.class, 1);
                        //0929新增智能批改标记

                        if(questDoc.getIntelligent()!=null){
                            question.setIntelligent(questDoc.getIntelligent());
                        }else{
                            question.setIntelligent(0);
                        }
                        //solr中存的single solrDocument存的是isSingle 数据库中存的是isSingle但在java bean中使用的single,
                        //所以从solr中取到值重新覆盖保持统一
                        question.setSingle(questDoc.getIsSingle() != null && questDoc.getIsSingle() == 1);
                        question.setQuoteNum(questDoc.getRef()==null?0:questDoc.getRef().intValue());
                        //因为QuestionDocument 和 Question 字段类型HighQual不一致
                        Integer highQual = questDoc.getHighQual();
                        question.setHighQual((highQual != null && highQual == 1) ? 1 : 0);
                        //处理历史数据，并更新类型详情字段
                        SolrSearchUtil.analyzeQuestionType(question);
                        // 题型结构
                        SolrSearchUtil.analyzeQuestionStruct(question, allQuestionTypes);
                        // 主题
                        if (CollectionUtils.isNotEmpty(question.getTopicId())) {
                            topicIds.addAll(question.getTopicId());
                        }

                    /*
                        自定义目录和自定义目录体系ID
                    */
                        CustomQuestionResource customQuestionResource = linkCustomQuestionResourceMapper
                                .queryCustomQuestionResourceByQuesIdAndSysId(questDoc.getId(), questDoc.getUploadId());
                        if (customQuestionResource != null && customQuestionResource.getGroupId() != null) {
                            question.setGroupId(customQuestionResource.getGroupId());
                        }

                    /*
                        获取自定义目录ID列表
                        级联查询
                    */
                        if (customQuestionResource != null && customQuestionResource.getCustomListId() != null) {
                            IdRequest idRequest = new IdRequest(customQuestionResource.getCustomListId());
                            CommonResponse<List<CustomListVo>> byLeafId = customListService.findParentsByLeafId
                                    (idRequest);
                            List<CustomListVo> customListVos = byLeafId.getData();
                            if (CollectionUtils.isNotEmpty(customListVos)) {
                                List<Map<String, Object>> customerDirectorys = new ArrayList<>();
                                for (CustomListVo customListVo : customListVos) {
                                    Map<String, Object> customerDirectory = new HashMap<>();
                                    customerDirectory.put("level", customListVo.getLevel());
                                    customerDirectory.put("groupId", customListVo.getGroupId());
                                    customerDirectory.put("cusomerDeritoryId", customListVo.getId());
                                    customerDirectory.put("cusomerDeritoryName", customListVo.getName());
                                    customerDirectorys.add(customerDirectory);
                                }
                                question.setCustomerDirectoryIds(customerDirectorys);
                            }

                        }
                        //操作赋值
                        if(question.getUpdateTime()!=null && question.getUploadTime()!=null && question.getUpdateTime().equals(question.getUploadTime())){
                            question.setQuestionSource(1);
                        }else if(customQuestionResource != null && customQuestionResource.getIsFav()!=null && customQuestionResource.getIsFav()==1){
                            question.setQuestionSource(4);
                        }else{
                            question.setQuestionSource(3);
                        }


                        // 子题
                        SolrSearchUtil.analyzeQuestionSub(question, allSubQuestList, allQuestionTypes);
                        returnList.add(question);

                        resultQuestionIds.add(question.getId());
                    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
                //用户名处理
                setQuestionUserName(returnList);

                Map<Long, Topic> mapTopic = new HashMap<>();
                Map<String, Integer> mapTopicMastery = new HashMap<>();
                SolrSearchUtil.batchQueryMapTopicAndMastery(topicService, questionTopicDao, topicIds,
                        resultQuestionIds, mapTopic, mapTopicMastery);

                Map<String, String> questionContentMap = new HashMap<>();

                /*
                    构建html数据为了兼容原有的ssdb数据格式
                */
                for (QuestionDocument document : allQuestionDocuments) {
                    questionContentMap.put(document.getId() + "", document.getHtmlData());
                }
                /**
                 * 当数据转换失败时，移除转换失败的数据
                 */
                List<com.noriental.praxissvr.question.bean.Question> removeList=new ArrayList<>();
                for (com.noriental.praxissvr.question.bean.Question question : returnList) {
                    SolrSearchUtil.analyzeQuestionTopic(question, mapTopic, mapTopicMastery);
                    analyzeQuestionSsdbHtml(question, questionContentMap, mapTopicMastery,removeList);
                }
                totalCount=totalCount-removeList.size();
                /**
                 * 求余集
                 */
                returnList.removeAll(removeList);
            }
            //收藏量
            List<EntityCounterResources> entityCounterResources = entityCounterResMapper.find(questionIds);

            Map<Long, TeacherSpaceQuestion> questionReportMap = null;
            try {
                questionReportMap = future == null ? new HashMap() : future.get(1, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                logger.info("", e);
            } finally {
                if (questionReportMap == null) {
                    questionReportMap = new HashMap<>();
                }
            }

            for (Question question: returnList){
                TeacherSpaceQuestion teacherSpaceQuestion = questionReportMap.get(question.getId());
                if (teacherSpaceQuestion != null) {
                    //question.setQuoteNum(teacherSpaceQuestion.getQuoteNum());
                    question.setSubmitNum(teacherSpaceQuestion.getSubmitNum());
                    question.setAccuracy(teacherSpaceQuestion.getAccuracy());
                }
                //收藏的题目
                for(LqResourceDocument lqResourceDocument:documentList){
                    if(question.getId()==lqResourceDocument.getQuestionId()&&lqResourceDocument.getIsFav().equals(0)){
                        question.setIsTeacherQuestion(1);
                    }
                    if(question.getId()==lqResourceDocument.getQuestionId()){
                        question.setFavTime(lqResourceDocument.getUpdateTime());
                    }
                }
                //题目的收藏量
                getQuestionShareNum(entityCounterResources, question);
            }

            resp.setCurrentPage(currentPage);
            resp.setTotalPageCount(totalPage);
            resp.setTotalCount(totalCount);
            resp.setQuestionList(returnList);

        }
        if (null == resp.getQuestionList()) {
            List<Question> questionArrayList = new ArrayList<>();
            resp.setQuestionList(questionArrayList);
        }
        return resp;
    }

    private void setQuestionUserName(List<Question> returnList) {
        List<Long> uploadIds = new ArrayList<>();
        for(Question questionGetAuther:returnList){
            uploadIds.add(questionGetAuther.getUploadId());
        }
        Map<String, Object> userQueryMap = new HashMap<>();
        Map<String, Object> userqMap = new HashMap<>();

        userqMap.put("systemId", uploadIds);

        userQueryMap.put("q",userqMap);
        userQueryMap.put(QueryMap.KEY_START,1);
        //最多50个题，最多50位资源主（创建人）
        userQueryMap.put(QueryMap.KEY_ROWS,50);
        SolrQueryPageRsp<UserDocument> userDocumentSolrQueryPageRsp= userSolrSearchService.search(new SolrQueryPageReq
                (userQueryMap));
        if(!userDocumentSolrQueryPageRsp.success()){
            throw new BizLayerException("",QUESTION_USER_ERROR);
        }
        List<UserDocument> userList= userDocumentSolrQueryPageRsp.getPage().getList();
        if(CollectionUtils.isNotEmpty(userList)){
            for(Question questionSetAuther:returnList){
                for(UserDocument userDocument:userList){
                    if(questionSetAuther.getUploadId().equals(userDocument.getSystemId())){
                        questionSetAuther.setUploadName(userDocument.getName());
                        break;
                    }
                }
            }
        }
    }


    @SuppressWarnings("unchecked")
    private void analyzeQuestionSsdbHtml(com.noriental.praxissvr.question.bean.Question question, Map<String, String>
            questionContentMap, Map<String, Integer> mapTopicMastery,List<Question> removeList) {
        if (MapUtils.isEmpty(questionContentMap)) {
            return;
        }
        String html = questionContentMap.get(question.getId() + "");
        if (StringUtils.isNoneBlank(html)) {
            QuestionSsdbHtml questionSsdb = JsonUtil.readValue(html, QuestionSsdbHtml.class);

            if(questionSsdb==null){
                removeList.add(question);
                return;
            }
            QuestionSsdbContentHtml contentObj = questionSsdb.getContent();

            if (null != contentObj) {
                question.setQuestionBody(contentObj.getBody());
                question.setMaterial(contentObj.getMaterial());
                question.setModel_essay(contentObj.getModel_essay());
                question.setSource(contentObj.getSource());
                question.setInterpret(contentObj.getInterpret());
                question.setTranslation(contentObj.getTranslation());
                question.setQuestionOptionList(contentObj.getOptions());
                question.setQuestionAnalysis(contentObj.getAnalysis());
                question.setQuestionAnswerList(contentObj.getAnswer());
                question.setAudioJson(contentObj.getAudio());
                question.setJsonMap(contentObj.getMap());
                //听口题prompt
                question.setPrompt(contentObj.getPrompt());
                //题目分析
                question.setQuestionAnalysis(contentObj.getAnalysis());

                List<QuestionSsdbContentHtml> subQuests = contentObj.getQuestions();
                if (CollectionUtils.isNotEmpty(subQuests)) {
                    List<Question> quest1 = question.getSubQuestions();
                    int count = subQuests.size() > quest1.size() ? quest1.size() : subQuests.size();
                    Set<Long> topicIds = new HashSet<>();
                    List<Long> resultQuestionIds = new ArrayList<>();
                    for (int j = 0; j < count; j++) {

                        com.noriental.praxissvr.question.bean.Question tempQuest = quest1.get(j);
                        resultQuestionIds.add(tempQuest.getId());
                        QuestionSsdbContentHtml questionMap = subQuests.get(j);
                        tempQuest.setQuestionBody(questionMap.getBody());
                        // 主题
                        if (CollectionUtils.isNotEmpty(tempQuest.getTopicId())) {
                            topicIds.addAll(tempQuest.getTopicId());
                        }
                        // 范文
                        tempQuest.setModel_essay(questionMap.getModel_essay());
                        // 原文
                        tempQuest.setSource(questionMap.getSource());
                        // 解读
                        tempQuest.setInterpret(questionMap.getInterpret());
                        tempQuest.setMaterial(questionMap.getMaterial());
                        tempQuest.setTranslation(questionMap.getTranslation());
                        tempQuest.setQuestionOptionList(questionMap.getOptions());
                        tempQuest.setQuestionAnswerList(questionMap.getAnswer());
                        //题目分析
                        tempQuest.setQuestionAnalysis(questionMap.getAnalysis());
                    }

                    Map<Long, Topic> mapTopic = new HashMap<>();
                    if (mapTopicMastery == null) {
                        mapTopicMastery = new HashMap<>();
                    }
                    SolrSearchUtil.batchQueryMapTopicAndMastery(topicService, questionTopicDao, topicIds,
                            resultQuestionIds, mapTopic, mapTopicMastery);
                    for (int j = 0; j < count; j++) {
                        com.noriental.praxissvr.question.bean.Question tempQuest = quest1.get(j);
                        SolrSearchUtil.analyzeQuestionTopic(tempQuest, mapTopic, mapTopicMastery);
                    }
                }

            }
        }
    }

    /*
     * 更新音频信息 1、更新mysql html_data json_data 数据
     *            2、更新SSDB里面html数据 业务取消
     * @param request
     * @return
     * @throws BizLayerException
     */
    @Override
    public CommonDes updateAudioInfoQuestion(UpdateAudioInfoQuestionRequest request) throws BizLayerException {
        logger.info("--------音频地址替换UpdateAudioInfoQuestionRequest" + request.getId() + "=====" + request.getUrl() +
                "===" + request.getName() + "----------------");

        /*
            1. 更新数据库
         */
        Map<String, Object> audioMap = new HashMap<>();
        audioMap.put("id", request.getId());
        audioMap.put("url", request.getUrl());
        audioMap.put("name", request.getName());
        audioMap.put("size", request.getSize());
        int i = entityQuestionMapper.updateAudioData(audioMap);
        if (i <= 0) {
            throw new BizLayerException("", UPDATE_AUDIO_FAIL);
        }
        //题集查询
        List<LinkExerciseQuestion> exerciseQuestions = linkExerciseQuestionMapper.findLinkExerciseQuestionById
                (request.getId());
        //修改题目时，如果该题目属于一个题集，而且该题集被缓存了，需要在此处过期该缓存
        QuestionServiceUtil.delExerciseQuestionCache(exerciseQuestions,redisUtil);
        /*
            2.更新solr
            2.1.查询数据库html_data json_data数据
            2.2.更新solr htmlData jsonData数据
         */
        EntityQuestion question = entityQuestionMapper.findQuestionById(request.getId());
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("id", request.getId());
        parameterMap.put("htmlData", question.getHtmlData());
        parameterMap.put("jsonData", question.getJsonData());
        SolrUtils.updateSolrIndex(parameterMap, solrUploadQuestionRabbitTemplate);
        return new CommonDes();
    }

    @Override
    public CommonResponse<List<Long>> findComplement(FindComplementQuestionRequest request) {
        Set<Long> subjectIds = request.getSubjectIds();
        QueryMap queryMap = QueryMap.build().buildQMap().putQ("state", QuestionState.ENABLED.toString()).putQ
                ("newFormat", 1).putQ("sameNum", "[* TO -1]").putQ("xxxxOrSearchKey", SolrSearchUtil.getSearchPerm
                (new QuesSearchPerm(request.getOrgId(), request.getOrgType()))).putFl("id");
        //        queryMap.putSort("score desc");
        if (CollectionUtils.isNotEmpty(subjectIds) && !subjectIds.contains(0L)) {
            queryMap.putQ("subjectId", new ArrayList<>(subjectIds));
        }
        queryMap.putRows(request.getMaxCount());
        SolrQueryPageRsp<QuestionDocument> search = questionSolrSearchService.search(new SolrQueryPageReq(queryMap));
        if (!search.success()) {
            throw new BizLayerException(String.format("查询补全题目数据异常|%s|%s", search.getMessage(), search.getCode()),
                    PRAXIS_INVOKE_SOLR);
        }
        long totalPage = search.getPage().getTotalPage();
        List<Long> ids = new ArrayList<>();
        if (totalPage > 1) {
            long randomPage = RandomUtils.nextLong(1, totalPage);
            queryMap.putFqCurrentPage(randomPage);
            search = questionSolrSearchService.search(new SolrQueryPageReq(queryMap));
            if (!search.success()) {
                throw new BizLayerException(String.format("查询补全题目数据异常|%s|%s", search.getMessage(), search.getCode()),
                        PRAXIS_INVOKE_SOLR);
            }
            SolrPage<QuestionDocument> page = search.getPage();

            List<QuestionDocument> list = page.getList();
            if (CollectionUtils.isNotEmpty(list)) {
                for (QuestionDocument questionDocument : list) {
                    ids.add(questionDocument.getId());
                }
            }
        }
        return CommonResponse.success(ids);
    }

    @Override
    public BatchQueryQuestionsResponse batchQueryQuestionsByIds(BatchQueryQuestionsRequest request) throws
            BizLayerException {

        logger.info("批量查询题目的基本信息batchQueryQuestionsByIds调用开始...." + "入参为 ：{}", request.getIds());

        if (CollectionUtils.isEmpty(request.getIds())) {
            throw new BizLayerException("", ANSWER_PARAMETER_NULL);

        }

        /*
         * 批量查询题目的基本信息
         */
        List<EntityQuestion> entityQuestions = entityQuestionMapper.batchQueryQuestionsByIds(request.getIds());
        BatchQueryQuestionsResponse response = new BatchQueryQuestionsResponse();
        List<QuestionBean> questionBeanList = new ArrayList<>();
        if (entityQuestions == null) {
            throw new BizLayerException("", QUESTION_DOWN_FAIL);
        }
        /*
         * 参数转换，封装返回参数
         */
        for (EntityQuestion entityQuestion : entityQuestions) {
            QuestionBean questionBean = new QuestionBean();

            questionBean.setId(entityQuestion.getId());
            questionBean.setCountOptions(entityQuestion.getCountOptions());
            questionBean.setDifficulty(entityQuestion.getDifficulty());
            questionBean.setHighQual(entityQuestion.getHighQual());
            questionBean.setMastery(entityQuestion.getMastery());
            questionBean.setMultiScoreAnswer(entityQuestion.getMultiScoreAnswer());
            questionBean.setParentQuestionId(entityQuestion.getParentQuestionId());
            questionBean.setQuestionType(entityQuestion.getQuestionType());
            questionBean.setRightOption(entityQuestion.getRightOption());
            questionBean.setIsSingle(entityQuestion.getIsSingle());
            questionBean.setState(entityQuestion.getState());
            questionBean.setQrId(entityQuestion.getQrId());
            questionBean.setCountTopic(entityQuestion.getCountTopic());
            questionBean.setSubjectId(entityQuestion.getSubjectId());
            questionBean.setUpdateTime(entityQuestion.getUpdateTime());
            questionBean.setSrc(entityQuestion.getSrc());
            questionBean.setUploadTime(entityQuestion.getUploadTime());
            questionBean.setUploadSrc(entityQuestion.getUploadSrc());
            questionBean.setUploadId(entityQuestion.getUploadId());
            questionBean.setNewFormat(entityQuestion.getNewFormat());
            questionBean.setQuestionGroup(entityQuestion.getQuestionGroup());
            questionBean.setAnswerNum(entityQuestion.getAnswerNum());
            questionBean.setQuestionTypeId(entityQuestion.getQuestionTypeId());
            questionBean.setVisible(entityQuestion.getVisible());
            questionBean.setSource(entityQuestion.getSource());
            questionBean.setOrgId(entityQuestion.getOrgId());
            questionBean.setOrgType(entityQuestion.getOrgType());

            if(entityQuestion.getIntelligent()!=null){
                questionBean.setIntelligent(entityQuestion.getIntelligent());
            }else{
                questionBean.setIntelligent(0);
            }
            questionBean.setHtmlData(entityQuestion.getHtmlData());
            questionBeanList.add(questionBean);
        }
        response.setQuestionBeanList(questionBeanList);
        return response;
    }


    @Override
    public CommonDes updateQuestionState(UpdateQuestionStateRequest request) throws BizLayerException {

        /*
         * 1.查询题型是否为拍照提
         * if 拍照题
         *  if state=PREVIEWED
         *      state=BANNED
         * else
         *  state=state
         */


        final List<Long> questionIds = request.getQuestionIds();
        final String state = request.getState().name();
        entityQuestionMapper.updateStateByIds(questionIds, state);

        Date now = new Date();
        for (Long questionId : questionIds) {
            //1.查询题目信息判断题型是否为拍照题目
            EntityQuestion question = entityQuestionMapper.findQuestionById(questionId);
            if (question.getQuestionTypeId() == 53) { //拍照题目
                if (state.equals(QuestionState.PREVIEWED.name())) {
                    try {
                        Map<String, Object> mapBody = new HashMap<>();
                        mapBody.put("id", questionId);
                        mapBody.put("state", QuestionState.BANNED);
                        mapBody.put("updateTime", now);
                        mapBody.put(Constants._DOC_CLASS_NAME, QuestionDocument.class.getName());
                        SolrIndexReqMsg msg = new SolrIndexReqMsg(mapBody);
                        solrUploadQuestionRabbitTemplate.convertAndSend(msg);
                    } catch (AmqpException e) {
                        logger.error("更新solr异常", e);
                    }
                } else {
                    try {
                        Map<String, Object> mapBody = new HashMap<>();
                        mapBody.put("id", questionId);
                        mapBody.put("state", state);
                        mapBody.put("updateTime", now);
                        mapBody.put(Constants._DOC_CLASS_NAME, QuestionDocument.class.getName());
                        SolrIndexReqMsg msg = new SolrIndexReqMsg(mapBody);
                        solrUploadQuestionRabbitTemplate.convertAndSend(msg);
                    } catch (AmqpException e) {
                        logger.error("更新solr异常", e);
                    }
                }
            } else {
                logger.debug("{}:state --> {}", questionId, state);
                try {
                    Map<String, Object> mapBody = new HashMap<>();
                    mapBody.put("id", questionId);
                    mapBody.put("state", state);
                    mapBody.put("updateTime", now);
                    mapBody.put(Constants._DOC_CLASS_NAME, QuestionDocument.class.getName());
                    SolrIndexReqMsg msg = new SolrIndexReqMsg(mapBody);
                    solrUploadQuestionRabbitTemplate.convertAndSend(msg);
                } catch (AmqpException e) {
                    logger.error("更新solr异常", e);
                }
            }
        }
        return new CommonDes();
    }

    @Override
    public QueryCusDirResponse queryCustomQuestionResourceByCusDirId(QueryCusDirRequest request) throws
            BizLayerException {
        List<CustomQuestionResource> resourceList = linkCustomQuestionResourceMapper
                .queryCustomQuestionResourceByCusDirId(request.getSystemId(), request.getCusDirId());
        List<CusDirQuestion> data = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(resourceList)) {
            for (CustomQuestionResource resource : resourceList) {
                CusDirQuestion cusDirQuestion = new CusDirQuestion();
                cusDirQuestion.setId(resource.getId());
                cusDirQuestion.setCustomListId(resource.getCustomListId());
                cusDirQuestion.setGroupId(resource.getGroupId());
                cusDirQuestion.setCreateTime(resource.getCreateTime());
                cusDirQuestion.setQuestionId(resource.getQuestionId());
                cusDirQuestion.setIsFav(resource.getIsFav());
                cusDirQuestion.setResourceStatus(resource.getResourceStatus());
                cusDirQuestion.setUpdateTime(resource.getUpdateTime());
                data.add(cusDirQuestion);
            }
            return new QueryCusDirResponse(data);
        }
        return new QueryCusDirResponse();
    }

    @Deprecated
    @Override
    public CommonDes updateLinkCustomDirectoryBySysIdAndCusDirId(UpdateCusDirRequest request) throws BizLayerException {
        /*
            --更新自定义目录的步骤
            1.通过新的自定义目录ID查询体系ID
            2.通过老自定义目录和用户ID更新自定义目录
            3.更新solr
         */
        List<CustomQuestionResource> resources = linkCustomQuestionResourceMapper
                .queryCustomQuestionResourceByCusDirId(request.getSystemId(), request.getOldCusDirId());
        if (CollectionUtils.isNotEmpty(resources)) {
            List<Long> questionIds = new ArrayList<>();
            for (CustomQuestionResource resource : resources) {
                questionIds.add(resource.getQuestionId());
            }
            CommonResponse<List<CustomListVo>> response = customListService.findParentsByLeafId(new IdRequest(request
                    .getCusDirId()));
            if (response.getData() != null && response.getData().get(0) != null) {
                Long groupId = response.getData().get(0).getGroupId();
                int i = linkCustomQuestionResourceMapper.updateLinkCustomDirectoryBySysIdAndCusDirId(request
                        .getOldCusDirId(), request.getCusDirId(), request.getSystemId(), groupId);
                if (i <= 0) {
                    throw new BizLayerException("", CUS_DIR_UPDATE_FAIL);
                }
            } else {
                throw new BizLayerException("", CUS_DIR_UPDATE_FAIL);
            }

            /*
                增量更新solr
             */
            if (response.getData() != null) {

                if (CollectionUtils.isNotEmpty(response.getData())) {


                    List<CustomListVo> data = response.getData();

                    List<Long> customListId = new ArrayList<>();
                    List<Long> customListId1 = new ArrayList<>();
                    List<Long> customListId2 = new ArrayList<>();
                    List<Long> customListId3 = new ArrayList<>();
                    for (CustomListVo customListVo : data) {
                        customListId.add(customListVo.getId());
                        if (customListVo.getLevel() == 1) {
                            customListId1.add(customListVo.getId());
                        } else if (customListVo.getLevel() == 2) {
                            customListId2.add(customListVo.getId());
                        } else if (customListVo.getLevel() == 3) {
                            customListId3.add(customListVo.getId());
                        }
                    }

                    String customListStr = "";
                    if (CollectionUtils.isNotEmpty(customListId)) {
                        customListStr = customListId.toString().substring(1, customListId.toString().length() - 1);
                    }
                    String customListStr1 = "";
                    if (CollectionUtils.isNotEmpty(customListId1)) {
                        customListStr1 = customListId1.toString().substring(1, customListId1.toString().length() - 1);
                    }
                    String customListStr2 = "";
                    if (CollectionUtils.isNotEmpty(customListId2)) {
                        customListStr2 = customListId2.toString().substring(1, customListId2.toString().length() - 1);
                    }
                    String customListStr3 = "";
                    if (CollectionUtils.isNotEmpty(customListId3)) {
                        customListStr3 = customListId3.toString().substring(1, customListId3.toString().length() - 1);
                    }


                    if (CollectionUtils.isNotEmpty(questionIds)) {

                        for (Long questionId : questionIds) {
                            Map<String, Object> mapBody = new HashMap<>();
                            mapBody.put("requestId", UUIDUtil.getUUID());
                            mapBody.put("id", questionId);
                            mapBody.put("customListId", customListId);
                            mapBody.put("customListStr", customListStr);
                            mapBody.put("customListId1", customListId1);
                            mapBody.put("customListId2", customListId2);
                            mapBody.put("customListId3", customListId3);
                            mapBody.put("customListStr1", customListStr1);
                            mapBody.put("customListStr2", customListStr2);
                            mapBody.put("customListStr3", customListStr3);

                            try {
                                mapBody.put(Constants._DOC_CLASS_NAME, QuestionDocument.class.getName());
                                SolrIndexReqMsg msg = new SolrIndexReqMsg(mapBody);
                                msg.setRequestId(UUIDUtil.getUUID());
                                solrUploadQuestionRabbitTemplate.convertAndSend(msg);
                            } catch (Exception e) {
                                logger.error(e.getMessage());
                                throw new BizLayerException("", UPDATE_SOLR_FAIL);
                            }
                        }
                    }
                }
            }

        }
        return new CommonDes();
    }

    @Override
    public CommonDes deleteCustomDiritory(DeleteCusDirRequest request) throws BizLayerException {
        int i = linkCustomQuestionResourceMapper.deleteCustomDiritory(request.getCusDirId(), request.getSystemId());
        if (i <= 0) {
            throw new BizLayerException("", CUS_DIR_DELETE_FAIL);
        }
        return new CommonDes();
    }

    @Override
    public CommonDes insertLinkCusQuesResou(InsertCusDirRequest request) throws BizLayerException {
        //为lesson-svr提供一个插入自定义目录关系,用于为套卷上传的题目添加关联关系
        logger.info("插入自定义目录入参为：{}", request.toString());
        /*
            1.插入数据库
            2.更新数据库
            由于题集问题里面的题目有的带有自定义目录有的不带有自定义目录所以首先应该讲带有自定义目录的和没有自定义目录的区分开
            带有自定义目录的进行批量更新
            没有自定义目录的进行批量插入
         */
        //封装参数
        List<LinkCusQuesRes> linkCusQuesRes = request.getLinkCusQuesRes();
        /*
            批量插入入参
         */
        List<CustomQuestionResource> customQuestionResourcesInserts = new ArrayList<>();

        for (LinkCusQuesRes linkCusQuesRe : linkCusQuesRes) {

            CustomQuestionResource resource = linkCustomQuestionResourceMapper
                    .queryCustomQuestionResourceByQuesIdAndSysId(linkCusQuesRe.getQuestionId(), linkCusQuesRe
                            .getSystemId());
            if (resource == null) {
                CustomQuestionResource customQuestionResourceinsert = new CustomQuestionResource();
                if (linkCusQuesRe.getSystemId() == null) {
                    throw new BizLayerException("", ANSWER_PARAMETER_NULL);
                }
                if (linkCusQuesRe.getGroupId() == null) {
                    throw new BizLayerException("", ANSWER_PARAMETER_NULL);
                }
                if (linkCusQuesRe.getCustomListId() == null) {
                    throw new BizLayerException("", ANSWER_PARAMETER_NULL);
                }
                if (linkCusQuesRe.getQuestionId() == null) {
                    throw new BizLayerException("", ANSWER_PARAMETER_NULL);
                }
                customQuestionResourceinsert.setSystemId(linkCusQuesRe.getSystemId());
                customQuestionResourceinsert.setCustomListId(linkCusQuesRe.getCustomListId());
                customQuestionResourceinsert.setGroupId(linkCusQuesRe.getGroupId());
                customQuestionResourceinsert.setCreateTime(new Date());
                customQuestionResourceinsert.setQuestionId(linkCusQuesRe.getQuestionId());
                customQuestionResourceinsert.setResourceStatus(1);//启用
                customQuestionResourceinsert.setIsFav(0);//没有被收藏
                customQuestionResourcesInserts.add(customQuestionResourceinsert);
            }
        }

        if (CollectionUtils.isNotEmpty(customQuestionResourcesInserts)) {
            int i = linkCustomQuestionResourceMapper.batchInsertLinkCustomQuestionResource
                    (customQuestionResourcesInserts);
            if (i <= 0) {
                throw new BizLayerException("自定义目录", BATCH_INSERT_FAIL);
            }
            /*
                2.发送mq 创建solr索引
            */
            //创建MQ文档

            List<LinkCusQuesRes> quesResList = request.getLinkCusQuesRes();
            for (LinkCusQuesRes cusQuesRes : quesResList) {
                EntityQuestion entityQuestion = entityQuestionMapper.findQuestionById(cusQuesRes.getQuestionId());
                CustomQuestionResource resource = linkCustomQuestionResourceMapper
                        .queryCustomQuestionResourceByQuesIdAndSysId(cusQuesRes.getQuestionId(), cusQuesRes
                                .getSystemId());
                entityQuestion.setUploadId(resource.getSystemId());
                sendLinkQuestionIndex(entityQuestion, cusQuesRes.getCustomListId(), resource.getId(), 0);
            }
        }

        return new CommonDes();
    }

    @Override
    public CommonResult isMyCollection(FindMyCollectionRequest request) throws BizLayerException {
        //判断题目是否被收藏
        CustomQuestionResource resource1 = linkCustomQuestionResourceMapper
                .queryCustomQuestionResourceByQuesIdAndSysId(request.getId(), request.getUploadId());
        CommonResult result=new CommonResult();
        if ((resource1 != null && resource1.getIsFav() == 1)){
            result.setMyCollection(true);
        }else{
            result.setMyCollection(false);
        }
        return result;
    }

    @Override
    public CommonDes updateQuestionHtmlImg(UpdateQuestionHtmlImgRequest request) throws BizLayerException {
        logger.info("更新题目htmldata的img入参为：{}", request.toString());

        EntityQuestion entityQuestion11 = entityQuestionMapper.findQuestionById(request.getId());

        EntityQuestion entityQuestion = new EntityQuestion();
        entityQuestion.setId(request.getId());

        entityQuestion.setCountOptions(entityQuestion11.getCountOptions());
        entityQuestion.setDifficulty(entityQuestion11.getDifficulty());

        String html=getQuestionHtmlString(request.getHtml(),entityQuestion11.getQuestionTypeId());


        try {
            String json = ParseHtmlUtil.html2json(html, entityQuestion11.getQuestionTypeId() + "",
                    entityQuestion11.getId(),questionTypeDao);
            entityQuestion.setJsonData(json);
        } catch (IOException e) {
            logger.error("HTML转JSON异常:{}", e);
            throw new BizLayerException("HTML 转 JSON 异常", JSON_CONVERT_FAIL);
        }
        Object o = JsonUtil.readValue(html, Object.class);
        Map<String, Object> wrapper = new HashMap<>();
        wrapper.put("question_id", entityQuestion.getId());
        wrapper.put("content", o);
        entityQuestion.setHtmlData(JsonUtil.obj2Json(wrapper));

        logger.debug("更新数据库数据为：{}", entityQuestion.toString());
        int question = entityQuestionMapper.updateQuestion(entityQuestion);
        if (question <= 0) {  //更新失败
            logger.error("更新习题失败");
            throw new BizLayerException("", QUESTION_UPDATE_FAIL);
        }
        //题集查询
        List<LinkExerciseQuestion> exerciseQuestions = linkExerciseQuestionMapper.findLinkExerciseQuestionById
                (request.getId());
        //修改题目时，如果该题目属于一个题集，而且该题集被缓存了，需要在此处过期该缓存
        QuestionServiceUtil.delExerciseQuestionCache(exerciseQuestions,redisUtil);


        //更新solr
        Date now = new Date();
        logger.debug("{}:html --> {}", request.getId(), request.getHtml());
        try {
            Map<String, Object> mapBody = new HashMap<>();
            mapBody.put("id", request.getId());
            mapBody.put("htmlData", entityQuestion.getHtmlData());
            mapBody.put("jsonData", entityQuestion.getJsonData());
            mapBody.put("updateTime", now);
            mapBody.put(Constants._DOC_CLASS_NAME, QuestionDocument.class.getName());
            SolrIndexReqMsg msg = new SolrIndexReqMsg(mapBody);
            solrUploadQuestionRabbitTemplate.convertAndSend(msg);
        } catch (AmqpException e) {
            logger.error("更新solr异常", e);
        }

        return new CommonDes();
    }

    @Override
    public FindQuestionsResponse findQuestionsAllFiled(FindQuestionsRequest request) {
        logger.info("通过题目ID查询题目信息入参：{}",request.getIds());
        FindQuestionsResponse resp = new FindQuestionsResponse();
        if(CollectionUtils.isEmpty(request.getIds())||request.getIds().size()>=2){
            return resp;
        }
        List<EntityQuestion> entityQuestionsPre = entityQuestionMapper.batchQueryQuestionsByIds(request.getIds());
        if(CollectionUtils.isEmpty(entityQuestionsPre)){
           return resp;
        }
        List<Long> ids=new ArrayList<>();
        if(entityQuestionsPre.get(0).getParentQuestionId()==0){
            FindQuestionsRequest request1=new FindQuestionsRequest();
            List<Long> ids1=new ArrayList<>();
            ids1.add(request.getIds().get(0));
            request1.setIds(ids1);
            request1.setQuestionType(QuestionTypeEnum.ALL);
            resp=findQuestions(request1);

            logger.info("查询小子题的结果是：{}",resp.getQuestionList());
            return resp;

        }else {
            ids.add(entityQuestionsPre.get(0).getParentQuestionId());
            FindQuestionsRequest request2=new FindQuestionsRequest();
            List<Long> ids1=new ArrayList<>();
            ids1.add(entityQuestionsPre.get(0).getParentQuestionId());
            request2.setIds(ids1);
            request2.setQuestionType(QuestionTypeEnum.ALL);
            request2.setReqId(TraceKeyHolder.getTraceKey());

            List<EntityQuestion> entityQuestions = entityQuestionMapper.batchQueryQuestionsByIdsAndParentIds(ids1);

            int offset= CalculationQuesionOffset(request.getIds().get(0),entityQuestions);

            resp=findSubQuestions(request2,request.getIds().get(0),offset);
            logger.info("查询小子题的结果是：{}",resp.getQuestionList());
            return resp;
        }
    }

    private int CalculationQuesionOffset(Long id, List<EntityQuestion> entityQuestions) {
        //子题顺序
        int offsetIndex=-1;
        if(entityQuestions.size()>0){
            for(EntityQuestion question:entityQuestions){
                if(!question.getId().equals(id)){
                    offsetIndex++;
                }else{
                    break;
                }
            }
        }
        return offsetIndex;
    }

    private FindQuestionsResponse findSubQuestions(FindQuestionsRequest request,Long subQuestionId,int offset) {
        FindQuestionsResponse resp = new FindQuestionsResponse();
        long l = System.currentTimeMillis();
        Map<String, Object> queryConf = SolrSearchUtil.buildSolrQueryMap(request);
        long l1 = System.currentTimeMillis();
        logger.info("findQuestions:buildSolrQueryMap:cost:{}ms", l1 - l);
        logger.info("findQuestions:queryMap:{}", JsonUtil.obj2Json(queryConf));
        long querySolrStart = System.currentTimeMillis();
        SolrQueryPageRsp<QuestionDocument> search = questionSolrSearchService.search(new SolrQueryPageReq(queryConf));
        if (!search.success()) {
            throw new BizLayerException("数据查询异常|", PRAXIS_INVOKE_SOLR);
        }
        SolrPage<QuestionDocument> solrPages = search.getPage();
        long querySolrEnd = System.currentTimeMillis();
        logger.info("findQuestions:Query solr cost:{}ms", (querySolrEnd - querySolrStart));

        // 所有试题

        List<QuestionDocument> allQuestionDocuments = solrPages.getList();

        FutureTask<Map<Long, TeacherSpaceQuestion>> future = null;
        if (CollectionUtils.isNotEmpty(allQuestionDocuments)) {
            List<Long> ids = new ArrayList<>();
            for (QuestionDocument document : allQuestionDocuments) {
                ids.add(document.getId());
            }
            //查询题目的统计信息
            Callable<Map<Long, TeacherSpaceQuestion>> callable = new QueryQuestionReportThread(ids);
            future = new FutureTask<>(callable);
            new Thread(future).start();
        }

        long totalPage = solrPages.getTotalPage();
        long totalCount = solrPages.getTotalCount();
        long currentPage = solrPages.getCurrentPage();
        // 复合题
        List<Long> parIds = new ArrayList<>();
        if (!request.isBasic()) {
            for (QuestionDocument questionDocument : allQuestionDocuments) {
                if (questionDocument != null && BooleanUtils.isNotTrue(questionDocument.getIsSingle() != null &&
                        questionDocument.getIsSingle() == 1)) {
                    parIds.add(questionDocument.getId());
                }
            }
        }
        List<QuestionDocument> allSubQuestList = new ArrayList<>();
        List<QuestionDocument> resultSubQuestList = new ArrayList<>();
        if (parIds.size() > 0) {
            long l2 = System.currentTimeMillis();
            Map<String, Object> queryMap = new HashMap<>();
            Map<String, Object> temp_query_conf = new HashMap<>();
            queryMap.put(QueryMap.KEY_START, 0);
            queryMap.put(QueryMap.KEY_ROWS, parIds.size() * 20);
            temp_query_conf.put("parentQuestionId", parIds);

            if (CollectionUtils.isNotEmpty(request.getSubStates())) {
                List<String> states = new ArrayList<>();
                for (QuestionState questionState : request.getSubStates()) {
                    states.add(questionState.toString());
                }
                if (!states.contains(QuestionState.ALL.toString())) {
                    temp_query_conf.put("state", states);
                }
            } else {
                temp_query_conf.put(QueryMap.KeyPrefix.LOGIC_NOT.getValue() + "state", QuestionState.DISABLED
                        .toString());
            }

            queryMap.put("q", temp_query_conf);
            queryMap.put("sort", "id");
            // 获取复合题下的子题
            SolrQueryPageRsp<QuestionDocument> searchSub = questionSolrSearchService.search(new SolrQueryPageReq
                    (queryMap));
            if (!searchSub.success()) {
                throw new BizLayerException("查询数据异常|", PRAXIS_INVOKE_SOLR);
            }
            SolrPage<QuestionDocument> solrPagesSubQuestion = searchSub.getPage();
            allSubQuestList = solrPagesSubQuestion.getList();
            if(!CollectionUtils.isEmpty(allSubQuestList)){
                for(QuestionDocument subQuestion :allSubQuestList){
                    if(subQuestion.getId().equals(subQuestionId)){
                        resultSubQuestList.add(subQuestion);
                    }
                }
            }
            long l3 = System.currentTimeMillis();
            logger.info("findQuestions:Query sub questions cost:{}ms", l3 - l2);
        }

        List<Question> returnList = new ArrayList<>();
        try{
            if (CollectionUtils.isNotEmpty(allQuestionDocuments)) {
                Set<Long> topicIds = new HashSet<>();
                // 遍历试题
                List<Long> resultQuestionIds = new ArrayList<>();
                FindAllQuestionTypeResponse allQuestionTypes = questionTypeService.findAllQuestionType(new
                        FindAllQuestionTypeRequest());


                Map<Long, SuperQuestionSsdbHtml> mongoQuestionMap = new HashMap<>();


                for (QuestionDocument questDoc : allQuestionDocuments) {


                    // solr中试题信息
                    try {
                        Question question = EntityUtils.copyValueDeep2Object(questDoc, 1, Question.class, 1);
                        //0929新增智能批改标记
                        if(questDoc.getIntelligent()!=null){
                            question.setIntelligent(questDoc.getIntelligent());
                        }else{
                            question.setIntelligent(0);
                        }
                        //solr中存的single solrDocument存的是isSingle 数据库中存的是isSingle但在java bean中使用的single,
                        //所以从solr中取到值重新覆盖保持统一
                        question.setSingle(questDoc.getIsSingle() != null && questDoc.getIsSingle() == 1);

                        //因为QuestionDocument 和 Question 字段类型HighQual不一致
                        Integer highQual = questDoc.getHighQual();
                        question.setHighQual((highQual != null && highQual == 1) ? 1 : 0);
                        //处理历史数据，并更新类型详情字段
                        SolrSearchUtil.analyzeQuestionType(question);
                        // 题型结构
                        SolrSearchUtil.analyzeQuestionStruct(question, allQuestionTypes);
                        Map<Integer, QuestionType> supportedQuestionTypeMap=getCacheQuestionType(request.getReqId());
                        //是否是新题
                        if (null != question.getNewFormat() && question.getNewFormat() == 1 && null != question
                                .getQuestionTypeId() && !supportedQuestionTypeMap.containsKey(question.getQuestionTypeId
                                ())) {
                            question.setNewFormat(0);
                        }

                        if (!request.isBasic()) {

                            // 主题
                            if (CollectionUtils.isNotEmpty(question.getTopicId())) {
                                topicIds.addAll(question.getTopicId());
                            }

                            // 子题
                            SolrSearchUtil.analyzeQuestionSub(question, resultSubQuestList, allQuestionTypes);
                        }

                    /*
                        查询自定义目录通过solr表
                        题目ID，用户ID 确定自定义目录TODO
                     */
                        Map<String, Object> qMap = new HashMap<>();
                        Map<String, Object> idsMap = new HashMap<>();
                        idsMap.put("systemId", questDoc.getUploadId());//用户ID
                        idsMap.put("questionId", questDoc.getId());//题目ID
                        idsMap.put("resourceStatus", 1);
                        qMap.put("q", idsMap);
                        qMap.put(QueryMap.KEY_ROWS, 1);
                        SolrQueryPageRsp<LqResourceDocument> solrQueryPageRsp = lqResourceSolrSearchService.search(new
                                SolrQueryPageReq(qMap));
                        if (solrQueryPageRsp.success()) {
                            SolrPage<LqResourceDocument> page = solrQueryPageRsp.getPage();
                            if (CollectionUtils.isNotEmpty(page.getList())) {
                                LqResourceDocument resourceDocument = page.getList().get(0);

                                List<Map<String, Object>> customerDirectoryList = new ArrayList<>();

                                if (resourceDocument.getCustomListId1() != null) {
                                    Map<String, Object> cusMap1 = new HashMap<>();
                                    cusMap1.put("level", 1);
                                    cusMap1.put("cusomerDeritoryId", resourceDocument.getCustomListId1());
                                    cusMap1.put("cusomerDeritoryName", resourceDocument.getCustomListName1());
                                    cusMap1.put("groupId", resourceDocument.getGroupId());
                                    customerDirectoryList.add(cusMap1);
                                }

                                if (resourceDocument.getCustomListId2() != null) {
                                    Map<String, Object> cusMap2 = new HashMap<>();
                                    cusMap2.put("level", 2);
                                    cusMap2.put("cusomerDeritoryId", resourceDocument.getCustomListId2());
                                    cusMap2.put("cusomerDeritoryName", resourceDocument.getCustomListName2());
                                    cusMap2.put("groupId", resourceDocument.getGroupId());
                                    customerDirectoryList.add(cusMap2);
                                }

                                if (resourceDocument.getCustomListId3() != null) {
                                    Map<String, Object> cusMap3 = new HashMap<>();
                                    cusMap3.put("level", 3);
                                    cusMap3.put("cusomerDeritoryId", resourceDocument.getCustomListId3());
                                    cusMap3.put("cusomerDeritoryName", resourceDocument.getCustomListName3());
                                    cusMap3.put("groupId", resourceDocument.getGroupId());
                                    customerDirectoryList.add(cusMap3);
                                }
                                question.setGroupId(resourceDocument.getGroupId());
                                question.setCustomerDirectoryIds(customerDirectoryList);
                            }
                        }

                        returnList.add(question);
                        resultQuestionIds.add(question.getId());
                    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException e) {
                        logger.error(e.getMessage(), e);
                    }

                    String htmlData = questDoc.getHtmlData();
                    if(StringUtils.isEmpty(htmlData)){
                        continue;
                    }
                    SuperQuestionSsdbHtml superQuestionHtml = JsonUtil.readValue(htmlData, SuperQuestionSsdbHtml.class);
                    if (null==superQuestionHtml){
                        htmlData= htmlData.replaceAll("[\u0000-\u001f]", "");
                        superQuestionHtml = JsonUtil.readValue(htmlData, SuperQuestionSsdbHtml.class);
                    }
                    mongoQuestionMap.put(questDoc.getId(), superQuestionHtml);
                }
                if (!request.isBasic()) {
                    long l4 = System.currentTimeMillis();
                    Map<Long, Topic> mapTopic = new HashMap<>();
                    Map<String, Integer> mapTopicMastery = new HashMap<>();
                    SolrSearchUtil.batchQueryMapTopicAndMastery(topicService, questionTopicDao, topicIds,
                            resultQuestionIds, mapTopic, mapTopicMastery);
                    long l5 = System.currentTimeMillis();
                    logger.info("findQuestions:find topics:cost:{}ms", l5 - l4);

                    long l7 = System.currentTimeMillis();
                    logger.info("findQuestions:find all mongo HTML:cost:{}ms", l7 - l5);
                    for (Question question : returnList) {
                        SolrSearchUtil.analyzeQuestionTopic(question, mapTopic, mapTopicMastery);
                        SolrSearchUtil.analyzeQuestionMongoHtml(topicService, questionTopicDao, question,
                                mongoQuestionMap, mapTopicMastery,offset);
                    }
                    long l8 = System.currentTimeMillis();
                    logger.info("findQuestions:copy groups, topics to question list:{}ms", l8 - l7);
                }
            }
        }finally {
            //移除本次的缓存
            questionMemTypeMap.remove(request.getReqId());
        }


        Map<Long, TeacherSpaceQuestion> questionReportMap = null;
        try {
            questionReportMap = future == null ? new HashMap() : future.get(1, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            logger.info("", e);
        } finally {
            if (questionReportMap == null) {
                questionReportMap = new HashMap<>();
            }
        }
        for (Question question : returnList) {
            TeacherSpaceQuestion teacherSpaceQuestion = questionReportMap.get(question.getId());
            if (teacherSpaceQuestion != null) {
                question.setQuoteNum(teacherSpaceQuestion.getQuoteNum());
                question.setSubmitNum(teacherSpaceQuestion.getSubmitNum());
                question.setAccuracy(teacherSpaceQuestion.getAccuracy());
            }
        }

        resp.setCurrentPage(currentPage);
        resp.setTotalPageCount(totalPage);
        resp.setTotalCount(totalCount);
        resp.setQuestionList(returnList);
        return resp;
    }

    /**
     * 获取单个数据库ID
     *
     * @return questionId
     */
    private Long getQuestionId() {
        SeqNextIdReq req = new SeqNextIdReq();
        req.setSequenceType("OKAY_QUESTION_ID");
        CommonResponse<Long> nextId = sequenceService.getNextId(req);
        return nextId.getData();
    }

    /**
     * 批量获取数据库ID
     *
     * @param count:获取多少个数据库ID
     * @return questionIds
     */
    private List<Long> getBatchQuestionId(int count) {
        SeqNextIdListReq seqNextIdListReq = new SeqNextIdListReq();
        seqNextIdListReq.setSequenceType("OKAY_QUESTION_ID");
        seqNextIdListReq.setIdSize(count);
        CommonResponse<List<Long>> idList = sequenceService.getNextIdList(seqNextIdListReq);
        return idList.getData();
    }


    /***
     * 根据习题内容和html信息组装新的solr实体
     * @param entityQuestion q
     * @param html h
     */
    private void updateQuestionSolrIndex(EntityQuestion entityQuestion, String html, Long chapterId, List<Long>
            topicIds, List<Long> ids, Long cusDirId) throws BizLayerException {

        SolrIndexReqMsg solrIndexReqMsg;
        int length = JsonUtils.getQuestionsLength(html, "questions");
        if (length > 0) { //复合题

            //重新发送solr
            //①构建复合题的新题目列表（包括大题题目Id和新的子题题目Id）
            List<Long> SubjId = entityQuestionMapper.findQuestionSubjIdByParentId(entityQuestion.getId());
            if (CollectionUtils.isEmpty(SubjId)) {
                throw new BizLayerException("", QUERY_QUESTIONID_FAIL);
            }
            List<Long> idlist = new ArrayList<>();
            idlist.add(entityQuestion.getId());
            idlist.addAll(SubjId);

            //②构建复合题参数
            solrIndexReqMsg = SolrIndexReqMsgSubj(entityQuestion, html, chapterId, topicIds, idlist, cusDirId);
            //③重新发送solr
            solrUploadQuestionRabbitTemplate.convertAndSend(solrIndexReqMsg);

        } else { //单题
            try {
                solrIndexReqMsg = componentSolrDoc(entityQuestion, html, chapterId, topicIds, cusDirId);

                QuestionDocument questionTmpDocument = (QuestionDocument) solrIndexReqMsg.getBody();
                Map<String, Object> bodyMap = new HashMap<>();
                EntityUtils.copyValueByGetter(questionTmpDocument, bodyMap);
                bodyMap.put(Constants._DOC_CLASS_NAME, QuestionDocument.class.getName());
                SolrIndexReqMsg msg = new SolrIndexReqMsg(bodyMap);
                msg.setRequestId(TraceKeyHolder.getTraceKey());
                logger.info("更新solr请求ID=" + msg.getRequestId());
                logger.info("更新solr数据：" + msg.getBody());
                solrUploadQuestionRabbitTemplate.convertAndSend(msg);
            } catch (Exception e) {
                logger.error("更新单题异常消息={}", e);
                throw new BizLayerException("", UPDATE_SOLR_FAIL);
            }
        }
    }


    /**
     * 查询自定义目录题目关系表
     * @param request
     * @return
     */
    public List<Long> findLinkCustomQuestionResource(FlowTurnCorrectRequest request) {
        return linkCustomQuestionResourceMapper.findLinkCustomQuestionResource(request.getSystemId(), request.getCatalogGroupId(), request.getIsCustomListid(), request.getLevel(),request.getCatalogId(),request.getCatalogIdFirst(),request.getCatalogIdSecond(), request.getCustomListid());
    }

}
