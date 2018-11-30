package com.noriental.praxissvr.questionSearch.util;

import com.noriental.adminsvr.bean.knowledge.Topic;
import com.noriental.adminsvr.request.RequestEntity;
import com.noriental.adminsvr.response.ResponseEntity;
import com.noriental.adminsvr.service.knowledge.TopicService;
import com.noriental.praxissvr.common.*;
import com.noriental.praxissvr.question.bean.*;
import com.noriental.praxissvr.question.dao.QuestionTopicDao;
import com.noriental.praxissvr.question.request.FindQuestionsRequest;
import com.noriental.praxissvr.question.response.FindAllQuestionTypeResponse;
import com.noriental.solr.bean.doc.QuestionDocument;
import com.noriental.solr.common.search.QueryMap;
import com.noriental.utils.entity.EntityUtils;
import com.noriental.utils.json.JsonUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by kate on 2017/8/9.
 */
public class SolrSearchUtil {
    private static final Logger logger = LoggerFactory.getLogger(SolrSearchUtil.class);

    /**
     * 组织查询条件
     *
     * @param request request
     * @return map
     */
    public static Map<String, Object> buildSolrQueryMap(FindQuestionsRequest request) {
        Map<String, Object> queryConf = new HashMap<>();

        if (request.isPageable()) {
            queryConf.put(QueryMap.KEY_START, (request.getPageNo() - 1) * request.getPageSize());
            queryConf.put(QueryMap.KEY_ROWS, request.getPageSize());
        }

        Map<String, Object> qMap = new HashMap<>();
        if (request.isSameFilter()) {
            qMap.put("sameNum", "[* TO -1]");//非同款题目sameNum:-questionId,同款题目sameNum:sameNum-Long.MAX_VALUE,
            // 同款题目分值小的sameNum:sameNum
        }

        //单选题和多选题的搜索条件
        qMap.put("questionType", request.getQuestionType().getSolrType());
        if (request.getQuestionType() == QuestionTypeEnum.DAN_XUAN) {
            qMap.put("answerNum", 1);
        } else if (request.getQuestionType() == QuestionTypeEnum.DUO_XUAN) {
            qMap.put("answerNum", "[2 TO *]");
        }
        // 是否是新题
        if (request.isNewFormat()) {
            qMap.put("newFormat", 1);
        }

        // 题目是否可见
        qMap.put("visible", request.getVisible().getSolrValue());

        //是否查询上传者所有的习题
        Long uploadId = request.getUploadId();
        if (uploadId != null && uploadId > 0) {
            qMap.put("uploadId", uploadId);
        }

        List<String> allMUTIds = new ArrayList<>();
        //模块
        setAllMUTIds(allMUTIds, request.getModuleIds(), "M");
        //单元
        setAllMUTIds(allMUTIds, request.getUnitIds(), "U");
        //主题
        setAllMUTIds(allMUTIds, request.getTopicIds(), "T");

        if (CollectionUtils.isNotEmpty(allMUTIds)) {
            qMap.put("allMUTIds", allMUTIds);
        }

        List<Long> ids = request.getIds();
        if (CollectionUtils.isNotEmpty(ids)) {
            ids = new ArrayList<>(new HashSet<>(ids));//去重
            qMap.put("id", ids);
            queryConf.put(QueryMap.KEY_ROWS, ids.size());
        }
        //章节
        Long chapterId = request.getChapterId();
        if (chapterId != null && chapterId > 0) {
            qMap.put("chapterId", chapterId);
        }
        //学校
        long orgId = request.getOrgId();
        if (orgId > 0) {
            qMap.put("orgId", orgId);
        } else if (orgId < 0) {
            qMap.put(QueryMap.KeyPrefix.LOGIC_NOT.getValue() + "orgId", Math.abs(orgId));
        }

        queryConf.put("q", qMap);


        Map<String, Object> fqMap = new HashMap<>();

        Integer questionTypeId = request.getQuestionTypeId();
        if (questionTypeId != null) {
            fqMap.put("questionTypeId", questionTypeId);
        }

        Difficulty difficulty = request.getDifficulty();
        if (difficulty != null) {
            fqMap.put("difficulty", difficulty.getCode());
        }

        Integer questionGroup = request.getQuestionGroup();
        if (questionGroup != null) {
            fqMap.put("questionGroup", questionGroup);
        }

        setQuestionStates(request.getStates(), fqMap);

        if (request.isQueryTrunk()) {
            fqMap.put("parentQuestionId", 0);
        }

        Long subjectId = request.getSubjectId();
        if (subjectId != null) {
            fqMap.put("subjectId", subjectId);
        }

        Long gradeId = request.getGradeId();
        if (gradeId != null) {
            fqMap.put("gradeId", gradeId);
        }

        Mastery mastery = request.getMastery();
        if (mastery != null) {
            fqMap.put("mastery", mastery);
        }
        //是否是精品
        Boolean highQual = request.getHighQual();
        if (highQual != null) {
            fqMap.put("highQual", highQual ? 1 : 0);
        }

        //是否是音频习题
        Boolean isAudio = request.getAudio();
        if (isAudio != null) {
            fqMap.put((isAudio ? "" : QueryMap.KeyPrefix.LOGIC_NOT.getValue()) + "questionTypeId", QuestionTypeEnum
                    .TING_LI.getTypeId());
        }

        //上传的开始结束时间
        Date uploadTimeStart = request.getUploadTimeStart();
        Date uploadTimeEnd = request.getUploadTimeEnd();
        if (uploadTimeStart != null || uploadTimeEnd != null) {
            fqMap.put("uploadTime", String.format("[%s TO %s]", uploadTimeStart == null ? "*" : DateFormatUtils
                    .format(uploadTimeStart, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", TimeZone.getTimeZone("UTC")),
                    uploadTimeEnd == null ? "*" : DateFormatUtils.format(uploadTimeEnd, "yyyy-MM-dd'T'HH:mm:ss" + ""
                            + ".SSS'Z'", TimeZone.getTimeZone("UTC"))));
        }

        //试卷相关关键字搜索
        String paperKeyword = request.getPaperKeyword();
        if (StringUtils.isNotEmpty(paperKeyword)) {
            Map<String, Object> keywordsMap = new HashMap<>();
            //            keywordsMap.put(QueryMap.KeyPrefix.MATCHING_QUERY.getValue() + "paperName", paperKeyword);
            if (NumberUtils.isNumber(paperKeyword)) {
                keywordsMap.put("id", paperKeyword);
                keywordsMap.put("uploadId", paperKeyword);
                Map<String, Object> subKeywordsMap = new HashMap<>();
                subKeywordsMap.put(QueryMap.KeyPrefix.LOGIC_REVERSE.getValue() + "subKeywordsMap", keywordsMap);
                qMap.put("subKeywordsMap", subKeywordsMap);
            }
        }

        if (request.getQuesSearchPerm() != null) {
            QuesSearchPerm quesSearchPerm = request.getQuesSearchPerm();
            List<Map<String, Object>> searchPermList = getSearchPerm(quesSearchPerm);
            qMap.put("xxxxOrSearchKey", searchPermList);
        }

        queryConf.put("fq", fqMap);

        List<QuestionSort> sorts = request.getSorts();
        if (CollectionUtils.isNotEmpty(sorts)) {
            StringBuilder sortStr = new StringBuilder("");
            for (QuestionSort sort : sorts) {
                sortStr.append(sort.getSort()).append(",");
            }
            queryConf.put("sort", StringUtils.substringBeforeLast(sortStr.toString(), ","));
        } else {
            queryConf.put("sort", QuestionSort.ID_DESC.getSort());
        }
        return queryConf;
    }


    public static void setAllMUTIds(List<String> allMUTIds, List<Long> moduleIds, String prefix) {
        if (CollectionUtils.isNotEmpty(moduleIds)) {
            for (Long id : moduleIds) {
                allMUTIds.add(prefix + id);
            }
        }
    }


    /**
     * 当前机构，公立，okay学校
     *
     * @param quesSearchPerm
     * @return
     */
    public static List<Map<String, Object>> getSearchPerm(QuesSearchPerm quesSearchPerm) {
        String currentOrgId = String.valueOf(quesSearchPerm.getCurrentOrgId());
        Integer currentOrgType = quesSearchPerm.getCurrentOrgType();
        String okayOrgId = "1";
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();//当前学校
        map.put("orgId", currentOrgId);
        map.put("orgType", currentOrgType);

        Map<String, Object> map2 = new HashMap<>();//okay学校
        map2.put("orgId", okayOrgId);
        map2.put("orgType", "4");

        Map<String, Object> map1 = new HashMap<>();//公立学校
        map1.put("orgType", String.valueOf(2));
        list.add(map);
        list.add(map2);
        list.add(map1);
        return list;
    }


    public static void setQuestionStates(List<QuestionState> statesList, Map<String, Object> qMap) {
        //默认查询已启用状态的习题，在request中设置了默认值
        if (CollectionUtils.isNotEmpty(statesList)) {
            List<String> states = new ArrayList<>();
            for (QuestionState questionState : statesList) {
                states.add(questionState.toString());
            }
            if (!states.contains(QuestionState.ALL.toString())) {
                qMap.put("state", states);
            }
        }
    }


    public static void analyzeQuestionType(Question question) {
        switch (question.getQuestionType()) {
            case "单项选择":
                question.setQuestionType("选择题");
                question.setQuestionTypeDetail("单选题");
                break;
            case "选择题":
                Integer answerNum = question.getAnswerNum();
                if (answerNum != null) {
                    if (answerNum > 1) {
                        question.setQuestionTypeDetail("多选题");
                    } else if (1 == answerNum) {
                        question.setQuestionTypeDetail("单选题");
                    } else if (0 == answerNum) {
                        question.setQuestionTypeDetail(question.getQuestionType());
                    }
                } else {
                    question.setQuestionTypeDetail(question.getQuestionType());
                }
                break;
            default:
                question.setQuestionTypeDetail(question.getQuestionType());
        }
    }


    //设置题型结构
    public static void analyzeQuestionStruct(Question question, FindAllQuestionTypeResponse resp) {
        long l = System.currentTimeMillis();
        try {
            question.setStructId(resp.findStructIdByTypeId(question.getQuestionTypeId()));
        } catch (Exception e) {
            logger.error(String.format("习题[%d], 类型为[%s], 类型不合法", question.getId(), question.getQuestionTypeId()), e);
        }
        long l1 = System.currentTimeMillis();
        logger.debug("analyzeQuestionStruct cost:{}ms", l1 - l);
    }


    public static void analyzeQuestionSub(Question question, List<QuestionDocument> allSubQuestList,
                                          FindAllQuestionTypeResponse response) throws InstantiationException,
            IllegalAccessException {
        List<Question> secList = new ArrayList<>();
        // 所有子题
        if (CollectionUtils.isNotEmpty(allSubQuestList)) {
            for (QuestionDocument doc : allSubQuestList) {
                if (doc.getParentQuestionId() != null && question.getId() == doc.getParentQuestionId()) {
                    com.noriental.praxissvr.question.bean.Question q = EntityUtils.copyValueDeep2Object(doc, 1, com
                            .noriental.praxissvr.question.bean.Question.class, 1);
                    Integer highQual = doc.getHighQual();
                    q.setHighQual((highQual != null && highQual == 1) ? 1 : 0);
                    analyzeQuestionType(q);
                    analyzeQuestionStruct(q, response);
                    secList.add(q);
                }
            }
        }
        question.setSubQuestions(secList);
    }


    public static void batchQueryMapTopicAndMastery(TopicService topicService, QuestionTopicDao questionTopicDao,
                                                    Set<Long> topicIds, List<Long> questionIds, Map<Long, Topic>
                                                            mapTopic, Map<String, Integer> mapTopicMastery) {
        if (CollectionUtils.isNotEmpty(topicIds)) {
            ResponseEntity<List<Topic>> resp = topicService.getTopics(new RequestEntity<List<Long>>(new ArrayList<>
                    (topicIds)));
            List<Topic> topics = resp.getEntity();
            for (Topic topic : topics) {
                mapTopic.put(topic.getId(), topic);
            }

            List<QuestionTopic> qtList = questionTopicDao.findByQuestionIds(questionIds);
            for (QuestionTopic questionTopic : qtList) {
                String key = String.format("%d-%d", questionTopic.getQuestionId(), questionTopic.getTopicId());
                if (!mapTopicMastery.containsKey(key)) {
                    mapTopicMastery.put(key, questionTopic.getMastery());
                }
            }
        }
    }


    public static void analyzeQuestionTopic(Question question, Map<Long, Topic> mapTopic, Map<String, Integer>
            mapMastery) {
        List<Long> topicIds = question.getTopicId();
        if (CollectionUtils.isNotEmpty(topicIds) && MapUtils.isNotEmpty(mapTopic)) {
            List<Topic> topicList = new ArrayList<>();
            for (Long topicId : topicIds) {
                Topic topic = mapTopic.get(topicId);
                if (topic != null) {
                    if (MapUtils.isNotEmpty(mapMastery)) {
                        Integer mastery = mapMastery.get(String.format("%d-%d", question.getId(), topicId));
                        if (mastery != null) {
                            topic.setMastery(mastery);
                        }
                    }
                    topicList.add(topic);
                } else {
                    logger.warn("题目[{}], topicId:{}, 查询Topic无记录", question.getId(), topicId);
                }
            }
            question.setTopicList(topicList);
        }
    }

    /**
     * 给题目（单题和复合题下的子题）的题干，答案，选项等属性赋值；
     * @param topicService
     * @param questionTopicDao
     * @param question
     * @param mongoQuestionMap
     * @param mapTopicMastery
     * @param offset   默认传0，当需要单独获取复合题中的某个小题，传入偏移量。
     */
    public static void analyzeQuestionMongoHtml(TopicService topicService, QuestionTopicDao questionTopicDao,
                                                Question question, Map<Long, SuperQuestionSsdbHtml> mongoQuestionMap,
                                                Map<String, Integer> mapTopicMastery,int offset) {
        if (MapUtils.isEmpty(mongoQuestionMap)) {
            return;
        }
        SuperQuestionSsdbHtml superQuestionMongoHtml = mongoQuestionMap.get(question.getId());
        if (superQuestionMongoHtml != null) {
            Object contentObj = superQuestionMongoHtml.getContent();
            if (contentObj instanceof HashMap) {
                Map content = (HashMap) contentObj;
                Object contentBody = content.get("body");
                if (contentBody != null) {
                    String body = contentBody.toString();
                    question.setQuestionBody(body);
                }

                Object contentMaterial = content.get("material");
                if (contentMaterial != null) {
                    if (contentMaterial instanceof String) {
                        question.setMaterial(contentMaterial.toString());
                    }

                }
                // 范文
                Object contentModelEssay = content.get("model_essay");
                if (null != contentModelEssay) {
                    question.setModel_essay(contentModelEssay.toString());
                }
                // 原文
                Object contentSource = content.get("source");
                if (null != contentSource) {
                    question.setSource(contentSource.toString());
                }
                // 解读
                Object contentInterpret = content.get("interpret");
                if (null != contentInterpret) {
                    question.setInterpret(contentInterpret.toString());
                }
                Object contentTranslation = content.get("translation");
                if (contentTranslation != null) {
                    if (contentTranslation instanceof String) {
                        question.setTranslation(contentTranslation.toString());
                    }

                }
                //选项
                Object contentOptions = content.get("options");
                if (contentOptions != null) {
                    question.setQuestionOptionList(contentOptions);
                }
                // 解析
                Object contentAnalysis = content.get("analysis");
                if (contentAnalysis != null) {
                    question.setQuestionAnalysis(contentAnalysis.toString());
                }
                // 答案
                Object contentAnswer = content.get("answer");
                if (null != contentAnswer) {
                    question.setQuestionAnswerList(contentAnswer);
                }

                //听力
                Object contentAudio = content.get("audio");
                if (contentAudio != null) {
                    QuestionAudio o = JsonUtil.readValue(JsonUtil.obj2Json(contentAudio), QuestionAudio.class);
                    question.setAudioJson(o);
                }

                //听口题的文章
                Object prompt = content.get("prompt");
                if (prompt != null) {
                    question.setPrompt(prompt);
                }

                // 子题
                Object contentQuestions = content.get("questions");
                if (contentQuestions != null && question.getSubQuestions() != null) {
                    try {
                        if (contentQuestions instanceof ArrayList) {
                            // mongo中子题
                            List subQuests = (List) contentQuestions;
                            List<Question> quest1 = question.getSubQuestions();
                            int count = subQuests.size() > quest1.size() ? quest1.size() : subQuests.size();
                            Set<Long> topicIds = new HashSet<>();
                            //                            Set<Long> seriesIds = new HashSet<>();
                            List<Long> resultQuestionIds = new ArrayList<>();
                            for (int j = 0; j < count; j++) {
                                Question tempQuest = quest1.get(j);
                                resultQuestionIds.add(tempQuest.getId());
                                if(offset!=0){
                                    j=offset;
                                    tempQuest.setSubQuestionSeqNum(offset+1);
                                }else{
                                    tempQuest.setSubQuestionSeqNum(j+1);
                                }
                                Map<String, Object> questMap = (Map<String, Object>) subQuests.get(j);
                                if (questMap.get("body") != null) {
                                    tempQuest.setQuestionBody(questMap.get("body").toString());
                                }
                                // 主题
                                if (CollectionUtils.isNotEmpty(tempQuest.getTopicId())) {
                                    topicIds.addAll(tempQuest.getTopicId());
                                }
                                // 范文
                                if (null != questMap.get("model_essay")) {
                                    tempQuest.setModel_essay(questMap.get("model_essay").toString());
                                }
                                // 原文
                                if (null != questMap.get("source")) {
                                    tempQuest.setSource(questMap.get("source").toString());
                                }
                                // 解读
                                if (null != questMap.get("interpret")) {
                                    tempQuest.setInterpret(questMap.get("interpret").toString());
                                }
                                if (questMap.get("material") != null) {
                                    if (questMap.get("material") instanceof String) {
                                        tempQuest.setMaterial(questMap.get("material").toString());
                                    }

                                }
                                if (questMap.get("translation") != null) {
                                    if (questMap.get("translation") instanceof String) {
                                        tempQuest.setTranslation(questMap.get("translation").toString());
                                    }

                                }
                                if (questMap.get("options") != null) {
                                    if (questMap.get("options") instanceof ArrayList) {
                                        List<String> optionsList = new ArrayList<>();
                                        List tempOptions = (ArrayList) questMap.get("options");
                                        for (Object tempOption : tempOptions) {
                                            optionsList.add(tempOption.toString());
                                        }
                                        tempQuest.setQuestionOptionList(optionsList);
                                    }
                                    if (questMap.get("options") instanceof String) {
                                        tempQuest.setQuestionOptionList(questMap.get("options").toString());
                                    }


                                }

                                if (questMap.get("analysis") != null) {
                                    String tempAnalysis = questMap.get("analysis").toString();
                                    tempQuest.setQuestionAnalysis(tempAnalysis);
                                    logger.debug("analysis:" + tempAnalysis);
                                }
                                // 答案
                                if (questMap.get("answer") != null) {
                                    tempQuest.setQuestionAnswerList(questMap.get("answer"));
                                }

                            }

                            Map<Long, Topic> mapTopic = new HashMap<>();
                            if (mapTopicMastery == null) {
                                mapTopicMastery = new HashMap<>();
                            }
                            batchQueryMapTopicAndMastery(topicService, questionTopicDao, topicIds, resultQuestionIds,
                                    mapTopic, mapTopicMastery);
                            for (int j = 0; j < count; j++) {
                                Question tempQuest = quest1.get(j);
                                analyzeQuestionTopic(tempQuest, mapTopic, mapTopicMastery);
                            }
                        }
                    } catch (Exception e) {
                        logger.error("子题的内容为====>异常了" + e.getMessage(), e);
                    }
                }

            }

        }
    }




}
