package com.noriental.praxissvr.question.utils;

import com.noriental.adminsvr.bean.knowledge.Topic;
import com.noriental.adminsvr.service.knowledge.TopicService;
import com.noriental.praxissvr.common.*;
import com.noriental.praxissvr.question.QuestionSsdbHtml;
import com.noriental.praxissvr.question.bean.CustomQuestionResource;
import com.noriental.praxissvr.question.bean.Question;
import com.noriental.praxissvr.question.bean.QuestionSsdbContentHtml;
import com.noriental.praxissvr.question.dao.QuestionTopicDao;
import com.noriental.praxissvr.question.mapper.LinkCustomQuestionResourceMapper;
import com.noriental.praxissvr.question.request.FindAllQuestionTypeRequest;
import com.noriental.praxissvr.question.request.FindMyQuestionRequest;
import com.noriental.praxissvr.question.response.FindAllQuestionTypeResponse;
import com.noriental.praxissvr.question.service.QuestionTypeService;
import com.noriental.praxissvr.questionSearch.util.SolrSearchUtil;
import com.noriental.resourcesvr.common.request.IdRequest;
import com.noriental.resourcesvr.customlist.service.CustomListService;
import com.noriental.resourcesvr.customlist.vo.CustomListVo;
import com.noriental.solr.bean.doc.LqResourceDocument;
import com.noriental.solr.bean.doc.QuestionDocument;
import com.noriental.solr.common.msg.SolrIndexReqMsg;
import com.noriental.solr.common.search.QueryMap;
import com.noriental.solr.common.search.SolrPage;
import com.noriental.solr.common.search.SolrQueryPageReq;
import com.noriental.solr.common.search.SolrQueryPageRsp;
import com.noriental.solr.common.utils.UUIDUtil;
import com.noriental.solr.service.search.LqResourceSolrSearchService;
import com.noriental.solr.service.search.QuestionSolrSearchService;
import com.noriental.utils.entity.EntityUtils;
import com.noriental.utils.json.JsonUtil;
import com.noriental.validate.bean.CommonResponse;
import com.noriental.validate.exception.BizLayerException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.*;

import static com.noriental.praxissvr.exception.PraxisErrorCode.PRAXIS_INVOKE_SOLR;

/**
 * Created by hushuang on 2017/2/6.
 * 1.批量删除solr索引
 */
public class SolrUtils {

    private static final Logger logger = LoggerFactory.getLogger(SolrUtils.class);

    /**
     * 批量删除solr索引
     */
    public static void fBatchDeleteIndex(List<Long> idList, RabbitTemplate asyncNoReplyRabbitTemplate) {

        logger.info("批量删除solr索引:"+idList.toString());

        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put(QueryMap._PRIMARY_KEY_VALUE_DEFAULT, idList);
        bodyMap.put(QueryMap.KEY_DELETE_FLG, null);
        //当主键不是id时，要提供主键属性
        bodyMap.put(QueryMap._PRIMARY_KEY, QueryMap._PRIMARY_KEY_VALUE_DEFAULT);
        bodyMap.put(QueryMap._DOC_CLASS_NAME, QuestionDocument.class.getName());
        asyncNoReplyRabbitTemplate.convertAndSend(bodyMap);
    }


    /*
        删除LqResourceDocument 索引
     */
    public static void fBatchDeleteLqResourceIndex(List<Long> idList, RabbitTemplate asyncNoReplyRabbitTemplate) {
        logger.info("批量删除关联solr索引:"+idList.toString());
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put(QueryMap._PRIMARY_KEY_VALUE_DEFAULT, idList);
        bodyMap.put(QueryMap.KEY_DELETE_FLG, null);
        //当主键不是id时，要提供主键属性
        bodyMap.put(QueryMap._PRIMARY_KEY, QueryMap._PRIMARY_KEY_VALUE_DEFAULT);
        bodyMap.put(QueryMap._DOC_CLASS_NAME, LqResourceDocument.class.getName());
        logger.info("fBatchDeleteLqResourceIndex 向MQ发送数据:"+JsonUtils.getObjectToJson(bodyMap));
        asyncNoReplyRabbitTemplate.convertAndSend(bodyMap);
    }



    /**
     * 增量更新solr索引
     * @param mapIndex
     * @param asyncNoReplyRabbitTemplate
     */
    public static void updateSolrIndex(Map<String,Object> mapIndex,RabbitTemplate asyncNoReplyRabbitTemplate){
        logger.info("增量更新solr索引:"+mapIndex);
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.putAll(mapIndex);
        bodyMap.put(QueryMap._DOC_CLASS_NAME, QuestionDocument.class.getName());
        SolrIndexReqMsg msg = new SolrIndexReqMsg(bodyMap);
        msg.setRequestId(UUIDUtil.getUUID());
        asyncNoReplyRabbitTemplate.convertAndSend(msg);
    }

//    /***
//     *通过自定义目录查询习题IDs
//     * @param request
//     * @param lqResourceSolrSearchService
//     * @return
//     */
//
//    public static SolrQueryPageRsp<LqResourceDocument> getLqResourceDocument(FindMyQuestionRequest request,
//                                                                             LqResourceSolrSearchService
//                                                                                     lqResourceSolrSearchService,
//                                                                             List<String> states) {
//        /*
//            1.构建索引条件
//         */
//        Map<String, Object> queryConf = new HashMap<>();
//        //分页
//        if (request.isPageable()) {
//            queryConf.put(QueryMap.KEY_START, (request.getPageNo() - 1) * request.getPageSize());
//            queryConf.put(QueryMap.KEY_ROWS, request.getPageSize());
//        }
//        //按照更新时间倒序
//        queryConf.put("sort", QuestionSort.UPDATE_TIME_DESC.getSort());
//        Map<String, Object> qMap = new HashMap<>();
//        //单选题和多选题的搜索条件
//        if (request.getQuestionType() != null) {
//            qMap.put("questionType", request.getQuestionType().getSolrType());
//        }
//        //单选题获取多选题索引
//        if (request.getQuestionType() == QuestionTypeEnum.DAN_XUAN) {
//            qMap.put("answerNum", 1);
//        } else if (request.getQuestionType() == QuestionTypeEnum.DUO_XUAN) {
//            qMap.put("answerNum", "[2 TO *]");
//        }
//        // 题目是否可见
//        qMap.put("resourceStatus", 1);
//        //上传者
//        Long systemId = request.getSystemId();
//        if (systemId != null) {
//            qMap.put("systemId", systemId);
//        }
//        /*
//             自定义目录查询条件
//         */
//        if (request.getCusDirId1() != null) {
//            qMap.put("customListId1", request.getCusDirId1());
//        }
//        if (request.getCusDirId2() != null) {
//            qMap.put("customListId2", request.getCusDirId2());
//        }
//        if (request.getCusDirId3() != null) {
//            qMap.put("customListId3", request.getCusDirId3());
//        }
//        //难易程度
//        Difficulty difficulty = request.getDifficulty();
//        if (difficulty != null) {
//            qMap.put("difficulty", difficulty.getCode());
//        }
//        qMap.put("state", states);
//        queryConf.put("q", qMap);
//        logger.info("solr queryConf:" + queryConf);
//        SolrQueryPageRsp<LqResourceDocument> linkQuestDir = lqResourceSolrSearchService.search(new SolrQueryPageReq
//                (queryConf));
//        return linkQuestDir;
//    }


    /***
     * 根据习题IDS查询习题信息
     * @param request
     * @param questionIds
     * @param states
     * @param questionSolrSearchService
     * @return
     * @throws BizLayerException
     */
    public static List<QuestionDocument> getQuestionDocument(FindMyQuestionRequest request, List<Long> questionIds,
                                                             List<String> states, QuestionSolrSearchService
                                                                     questionSolrSearchService) throws BizLayerException {
        List<QuestionDocument> allQuestionDocuments=new ArrayList<>();
        Map<String, Object> idQueryConf = new HashMap<>();
        Map<String, Object> idqMap = new HashMap<>();
        //第二次查询不允许分页
        if (request.isPageable()) {
            idQueryConf.put(QueryMap.KEY_ROWS, request.getPageSize());
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
            //logger.info("SolrQueryPageReq:" + JsonUtil.obj2Json(new SolrQueryPageReq(idQueryConf)));
            SolrQueryPageRsp<QuestionDocument> search = questionSolrSearchService.search(new SolrQueryPageReq
                    (idQueryConf));

            if (!search.success()) {
                throw new BizLayerException(String.format("查询主题数据异常|%s|%s", search.getMessage(), search.getCode()),
                        PRAXIS_INVOKE_SOLR);
            }
            SolrPage<QuestionDocument> solrPages = search.getPage();
            // 所有主题
             allQuestionDocuments = solrPages.getList();

            return allQuestionDocuments;


        }
        return allQuestionDocuments;
    }


    public static List<QuestionDocument> getSubQuestions(List<QuestionDocument> allQuestionDocuments,
                                                         QuestionSolrSearchService questionSolrSearchService) {

        List<QuestionDocument> allSubQuestList = new ArrayList<>();
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
       /* List<QuestionDocument> allSubQuestList = new ArrayList<>();*/
        if (parIds.size() > 0) {
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
                throw new BizLayerException(String.format("查询子题数据异常|%s|%s", searchSub.getMessage(), searchSub.getCode
                        ()), PRAXIS_INVOKE_SOLR);
            }
            SolrPage<QuestionDocument> solrPagesSubQuestion = searchSub.getPage();
            allSubQuestList = solrPagesSubQuestion.getList();
            return allSubQuestList;
        }
        return allSubQuestList;
    }


    /***
     * 给查询出来的单题、复合体组装自定义目录信息
     * @param allQuestionDocuments
     * @param allSubQuestList
     * @param questionTypeService
     * @param linkCustomQuestionResourceMapper
     * @param customListService
     * @return
     */
    public static List<Question> componentQuestionInfo(List<QuestionDocument> allQuestionDocuments,
                                                       List<QuestionDocument> allSubQuestList, QuestionTypeService
                                                               questionTypeService, LinkCustomQuestionResourceMapper
                                                               linkCustomQuestionResourceMapper, CustomListService
                                                               customListService, TopicService topicService,
                                                       QuestionTopicDao questionTopicDao) {
        List<Question> returnList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(allQuestionDocuments)) {
            Set<Long> topicIds = new HashSet<>();
            // 遍历试题
            List<Long> resultQuestionIds = new ArrayList<>();
            FindAllQuestionTypeResponse allQuestionTypes = questionTypeService.findAllQuestionType(new
                    FindAllQuestionTypeRequest());
            for (QuestionDocument questDoc : allQuestionDocuments) {
                // solr中试题信息
                try {
                    com.noriental.praxissvr.question.bean.Question question = EntityUtils.copyValueDeep2Object
                            (questDoc, 1, com.noriental.praxissvr.question.bean.Question.class, 1);
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
                            question.setCustomerDirectoryIds(customerDirectorys);
                        }
                    }
                    // 子题
                    SolrSearchUtil.analyzeQuestionSub(question, allSubQuestList, allQuestionTypes);
                    returnList.add(question);
                    resultQuestionIds.add(question.getId());
                } catch (InstantiationException | IllegalAccessException | IllegalArgumentException e) {
                    logger.error(e.getMessage(), e);
                }
            }
            Map<Long, Topic> mapTopic = new HashMap<>();
            Map<String, Integer> mapTopicMastery = new HashMap<>();
            SolrSearchUtil.batchQueryMapTopicAndMastery(topicService, questionTopicDao, topicIds, resultQuestionIds,
                    mapTopic, mapTopicMastery);
            Map<String, String> questionContentMap = new HashMap<>();
              /*
                    构建html数据为了兼容原有的ssdb数据格式
                */
            for (QuestionDocument document : allQuestionDocuments) {
                questionContentMap.put(document.getId() + "", document.getHtmlData());
            }

            for (com.noriental.praxissvr.question.bean.Question question : returnList) {
                //复合体下设置子题挂接的知识点
                SolrSearchUtil.analyzeQuestionTopic(question, mapTopic, mapTopicMastery);
                analyzeQuestionSsdbHtml(question, questionContentMap, mapTopicMastery, topicService, questionTopicDao);
            }
        }

        return returnList;

    }


    @SuppressWarnings("unchecked")
    public static void analyzeQuestionSsdbHtml(com.noriental.praxissvr.question.bean.Question question, Map<String,
            String> questionContentMap, Map<String, Integer> mapTopicMastery, TopicService topicService,
                                               QuestionTopicDao questionTopicDao) {
        if (org.apache.commons.collections4.MapUtils.isEmpty(questionContentMap)) {
            return;
        }
        String html = questionContentMap.get(question.getId() + "");
        if (StringUtils.isNoneBlank(html)) {
            QuestionSsdbHtml questionSsdb;
            questionSsdb = JsonUtil.readValue(html, QuestionSsdbHtml.class);

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
}
