package com.noriental.praxissvr.brush.service.impl;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.Paginator;
import com.noriental.global.dict.AppType;
import com.noriental.global.dict.QiniuConstant;
import com.noriental.praxissvr.answer.bean.OperateType;
import com.noriental.praxissvr.answer.bean.StudentExercise;
import com.noriental.praxissvr.answer.dao.StudentExerciseDao;
import com.noriental.praxissvr.answer.request.CreateRecordsRequest;
import com.noriental.praxissvr.answer.service.StudentExerciseService;
import com.noriental.praxissvr.answer.util.StuAnswerUtil;
import com.noriental.praxissvr.brush.bean.StudentWork;
import com.noriental.praxissvr.brush.dao.StudentWorkDao;
import com.noriental.praxissvr.brush.request.CreateEnchanceRequest;
import com.noriental.praxissvr.brush.request.CreateStudentWorkRequest;
import com.noriental.praxissvr.brush.request.StudentWorkIn;
import com.noriental.praxissvr.brush.request.StudentWorkPageInput;
import com.noriental.praxissvr.brush.response.CreateEnchanceResponse;
import com.noriental.praxissvr.brush.response.CreateStudentWorkResponse;
import com.noriental.praxissvr.brush.response.StudentWorkOut;
import com.noriental.praxissvr.brush.response.StudentWorkPageOutput;
import com.noriental.praxissvr.brush.service.StuBrushService;
import com.noriental.praxissvr.brush.util.FindSubset;
import com.noriental.praxissvr.common.StuAnswerConstant;
import com.noriental.praxissvr.exception.PraxisErrorCode;
import com.noriental.praxissvr.question.bean.QuesitonRecomdPriorityCompare;
import com.noriental.praxissvr.question.bean.QuesitonRecommend;
import com.noriental.praxissvr.question.bean.Question;
import com.noriental.praxissvr.question.request.FindQuestionByIdRequest;
import com.noriental.praxissvr.question.request.FindQuestionsByIdsRequest;
import com.noriental.praxissvr.question.response.FindQuestionByIdResponse;
import com.noriental.praxissvr.question.response.FindQuestionsByIdsResponse;
import com.noriental.praxissvr.question.service.QuestionService;
import com.noriental.praxissvr.questionSearch.service.QuestionSearchService;
import com.noriental.praxissvr.utils.LoggerHolder;
import com.noriental.solr.bean.doc.QuestionDocument;
import com.noriental.solr.common.exceptions.SolrSearchServiceException;
import com.noriental.solr.common.search.QueryMap;
import com.noriental.solr.common.search.SolrPage;
import com.noriental.solr.common.search.SolrQueryPageReq;
import com.noriental.solr.service.search.QuestionSolrSearchService;
import com.noriental.utils.json.JsonUtil;
import com.noriental.validate.bean.CommonDes;
import com.noriental.validate.exception.BizLayerException;
import com.sumory.mybatis.pagination.result.PageResult;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.*;
import java.util.concurrent.*;

import static com.noriental.praxissvr.exception.PraxisErrorCode.*;


@SuppressWarnings({"SpringJavaAutowiringInspection", "SpringAutowiredFieldsWarningInspection"})
@Service(value = "stuBrushService")
public class StuBrushServiceImpl implements StuBrushService, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @Autowired
    private StudentWorkDao studentWorkDao;
    @Autowired
    private StudentExerciseService stuExeService;
    @Autowired
    QuestionSolrSearchService questionSolrSearchService;
    @Autowired
    private QuestionService questionService_v2;
   /* @Autowired
    StuAnswerMoreService stuAnswerMoreService;*/
    @Autowired
    private QuestionSearchService questionSearchService;
    @Autowired
    private StudentExerciseDao studentExerciseDao;

    @Override
    public StudentWorkOut getWork(StudentWorkIn in) {
        StudentWork studentWork = in.getStudentWork();
        Long workId = studentWork.getId();
        Long studentId = studentWork.getStudentId();
        if (workId == null || studentId == null) {
            throw new BizLayerException("", ANSWER_PARAMETER_NULL);
        }
        List<StudentWork> studentWorks = studentWorkDao.findForExist(studentWork);
        int count = studentWorks.size();
        if (count == 0) {
            throw new BizLayerException("", ANSWER_RECORD_NOT_FOUND);
        }
        studentWork = studentWorks.get(0);
        StudentExercise se = new StudentExercise();
        se.setExerciseSource(StuAnswerConstant.ExerciseSource.WORK);
        se.setResourceId(workId);
        se.setStudentId(studentId);
        List<StudentExercise> studentExercises = stuExeService.getDbRecords(se);
        studentWork.setStudentExercises(studentExercises);
        studentWork.setBaseurl(QiniuConstant.URL_PREFIX.ANSWER_PICTRUE);
        StudentWorkOut out = new StudentWorkOut();
        out.setStudentWork(studentWork);
        return out;
    }


    @Override
    public CreateEnchanceResponse createEnchance(CreateEnchanceRequest in) {
        FindQuestionByIdRequest req = new FindQuestionByIdRequest();
        req.setQuestionId(in.getQuestionId());
        req.setRecursive(false);
        FindQuestionByIdResponse rep = questionService_v2.findQuestionById(req);// TODO: 2016/9/30 增加只查询题目基本信息的接口
        // chenlihua
        Question question = rep.getQuestion();
        List<Long> allTopicIds = question.getAllTopicIds();
        if (CollectionUtils.isNotEmpty(allTopicIds)) {
            Set<Object> quesTopicsSet = new HashSet<Object>(allTopicIds);
            FindSubset subSets = new FindSubset();
            List<String> quesTopicsSubSetsStr = subSets.getAllSortedSubSet(quesTopicsSet);
            List<Question> quesList = getQuesesByKnow(in.getQuestionId(), quesTopicsSubSetsStr);
            List<Question> subKnowQuesList = getSubKnowQueses(allTopicIds, quesList);
            List<Long> sortQueses = getSortQueses(question, subKnowQuesList);
            if (sortQueses.size() > 0) {
                List<Long> resultIds = sortQueses.subList(0, 1);
                StudentWork work = new StudentWork();
                work.setType(Integer.valueOf(StuAnswerConstant.ExerciseSource.ENHANCE));
                work.setStudentId(in.getStudentId());
                work.setWorkName(in.getQuestionId() + "");
                Long resourceId = createWork(work, resultIds);
                CreateEnchanceResponse out = new CreateEnchanceResponse();
                out.setQuestionIds(resultIds);
                out.setResourceId(resourceId);
                return out;
            }
        }
        CreateEnchanceResponse out = new CreateEnchanceResponse();
        out.setCode(ANSWER_RECORD_RECOMMEND_QUES_NOT_FOUND.getValue());
        out.setMessage(ANSWER_RECORD_RECOMMEND_QUES_NOT_FOUND.getComment());
        return out;
    }

    private List<Question> getSubKnowQueses(List<Long> allTopicIds, List<Question> quesList) {
        List<Question> subKnowQuesList = new ArrayList<>();
        for (Question q : quesList) {
            if (allTopicIds.containsAll(q.getAllTopicIds())) {
                subKnowQuesList.add(q);
            }
        }
        return subKnowQuesList;
    }

    public static final int PRIOR1 = 1;// 1) 知识点完全匹配、难度完全匹配、题型完全匹配
    public static final int PRIOR4 = 4; // 2) 知识点完全匹配、难度或题型不匹配
    public static final int PRIOR7 = 7;// 3) 知识点完全匹配、难度及题型都不匹配
    public static final int PRIOR10 = 10;// 4) 知识点部分匹配、难度完全匹配、题型完全匹配
    public static final int PRIOR13 = 13;// 5) 知识点部分匹配、难度或题型不匹配
    public static final int PRIOR16 = 16;// 6) 知识点部分匹配、难度及题型都不匹配

    private List<Long> getSortQueses(Question question, List<Question> quesList) {
        List<QuesitonRecommend> qrList = new ArrayList<>();
        for (Question ques : quesList) {
            QuesitonRecommend qr = new QuesitonRecommend();
            qr.setDifficulty(ques.getDifficulty());
            qr.setQuestionId(ques.getId());
            qr.setQuestionType(ques.getQuestionType());
            qr.setTopicIds(ques.getAllTopicIds());
            this.setPrior(question.getDifficulty(), question.getQuestionType(), question.getAllTopicIds(), qr);
            qrList.add(qr);
        }
        // 按照优先级进行排序
        Collections.sort(qrList, new QuesitonRecomdPriorityCompare());
        List<Long> sortedIds = new ArrayList<>();
        for (QuesitonRecommend qr : qrList) {
            sortedIds.add(qr.getQuestionId());
        }
        logger.debug("sortedIds:" + JsonUtil.obj2Json(sortedIds));
        return sortedIds;
    }

    private void setPrior(Integer sourceDiff, String sourceQuesType, List<Long> sourceTopicIds, QuesitonRecommend
            targetQue) {
        String targetQuesType = targetQue.getQuestionType();
        Integer targetDiff = targetQue.getDifficulty();
        List<Long> targetTopicIds = targetQue.getTopicIds();
        // 目标题目是单知识点
        if (targetTopicIds != null && targetTopicIds.size() == 1) {
            // 优先级1
            if (sourceDiff.equals(targetDiff) && sourceQuesType.equals(targetQuesType)) {
                targetQue.setRecommedPrior(PRIOR1);
            }
            // 优先级4
            else if (sourceDiff.equals(targetDiff) || sourceQuesType.equals(targetQuesType)) {
                targetQue.setRecommedPrior(PRIOR4);
            }
            // 优先级7
            else {//if (!sourceDiff.equals(targetDiff) && !sourceQuesType.equals(targetQuesType))
                targetQue.setRecommedPrior(PRIOR7);
            }
            // 目标题目的知识点与原题目知识点至少有一个是相同的，主要区分是全部匹配，还是部分匹配
        } else if (targetTopicIds != null) {
            // 比较两个知识点是否完全匹配
            boolean isAllMatching = (sourceTopicIds.size() == targetTopicIds.size());
            if (isAllMatching) {
                // 优先级1
                if (sourceDiff.equals(targetDiff) && sourceQuesType.equals(targetQuesType)) {
                    targetQue.setRecommedPrior(PRIOR1);
                }
                // 优先级4
                else if (sourceDiff.equals(targetDiff) || sourceQuesType.equals(targetQuesType)) {
                    targetQue.setRecommedPrior(PRIOR4);
                }
                // 优先级7
                else {//if (!sourceDiff.equals(targetDiff) && !sourceQuesType.equals(targetQuesType))
                    targetQue.setRecommedPrior(PRIOR7);
                }
            } else {

                // 优先级10
                if (sourceDiff.equals(targetDiff) && sourceQuesType.equals(targetQuesType)) {
                    targetQue.setRecommedPrior(PRIOR10);
                }

                // 优先级13
                else if (sourceDiff.equals(targetDiff) || sourceQuesType.equals(targetQuesType)) {
                    targetQue.setRecommedPrior(PRIOR13);
                }
                // 优先级16
                else {//if (!sourceDiff.equals(targetDiff) && !sourceQuesType.equals(targetQuesType))
                    targetQue.setRecommedPrior(PRIOR16);
                }
            }


        }
    }


    private List<Question> getQuesesByKnow(Long quesId, List<String> quesTopicsSubSetsStr) {
        Map<String, Object> map = new HashMap<>(1);
        Map<String, Object> fqMap = new HashMap<>(1);
        map.put("fq", fqMap);
        map.put("rows", 300);
        fqMap.put("state", AppType.PaperStatus.ENABLED);
        fqMap.put("parentQuestionId", 0);
        fqMap.put("allTopicIdStr", quesTopicsSubSetsStr);
        fqMap.put("newFormat", 1);
        fqMap.put(QueryMap.KeyPrefix.LOGIC_NOT.getValue() + "id", quesId);
        SolrPage<QuestionDocument> queses = null;
        try {
            long st = System.currentTimeMillis();
            queses = questionSolrSearchService.search(new SolrQueryPageReq(map)).getPage();
            logger.debug("getQuesesByKnow solr cost" + (System.currentTimeMillis() - st));
        } catch (SolrSearchServiceException e) {
            logger.error(e.getMessage(), e);
        }
        List<Question> datalist;
        if (queses != null && CollectionUtils.isNotEmpty(queses.getList())) {
            logger.debug("solr output :" + queses.getTotalCount());
            datalist = StuAnswerUtil.convertToQuestion(queses.getList());
        } else {
            datalist = new ArrayList<>();
        }
        return datalist;
    }


    private Long createWork(StudentWork brush, List<Long> quesIds) {
        Long stuId = brush.getStudentId();
        //insert work
        StudentWork studentWork = new StudentWork();
        BeanUtils.copyProperties(brush, studentWork);
        studentWork.setYear(Calendar.getInstance().get(Calendar.YEAR));
        studentWork.setWorkStatus(StuAnswerConstant.WorkStatus.NO_COMPLETE);
        studentWork.setCreateTime(new Date());
        StudentWork studentWorkResult = studentWorkDao.insertGetId(studentWork);
        Long resourceId = studentWorkResult.getId();
        //insert answerrecord
        List<StudentExercise> params = new ArrayList<>();
        List<Question> questionList = StuAnswerUtil.getQuesListByIds(questionSearchService, quesIds, false);
        for (Question ques : questionList) {
            if (!ques.isSingle()) {
                logger.debug(JsonUtil.obj2Json(ques));
                for (Question subQues : ques.getSubQuestions()) {
                    params.add(initStuExeByQues(subQues, resourceId, stuId, brush.getType()));
                }
            } else {
                params.add(initStuExeByQues(ques, resourceId, stuId, brush.getType()));
            }
        }
        stuExeService.createRecords(sort(quesIds, params), OperateType.NONE,null,true);
        return resourceId;
    }

    private List<StudentExercise> sort(List<Long> quesIds, List<StudentExercise> params) {
        List<StudentExercise> params1 = new ArrayList<>();
        Map<Long, List<StudentExercise>> map = new HashedMap<>();
        for (StudentExercise se : params) {
            se.setYear(Calendar.getInstance().get(Calendar.YEAR));
            Long quesId = se.getParentQuestionId() != null ? se.getParentQuestionId() : se.getQuestionId();
            if (map.get(quesId) == null) {
                List<StudentExercise> subList = new ArrayList<>();
                subList.add(se);
                map.put(quesId, subList);
            } else {
                map.get(quesId).add(se);
            }
        }
        for (Long quesId : quesIds) {
            if (null != map.get(quesId)) {
                params1.addAll(map.get(quesId));
            }

        }
        logger.debug("sorted params1:" + JsonUtil.obj2Json(params1));
        return params1;
    }


    private StudentExercise initStuExeByQues(Question ques, Long resourceId, Long studentId, Integer exerciseSource) {
        StudentExercise se = new StudentExercise();
        se.setExerciseSource(String.valueOf(exerciseSource));
        se.setResourceId(resourceId);
        se.setStudentId(studentId);
        se.setQuestionId(ques.getId());
        se.setStructId(Integer.valueOf(String.valueOf(ques.getStructId())));
        se.setParentQuestionId(ques.getParentQuestionId() > 0 ? ques.getParentQuestionId() : null);
        se.setYear(Calendar.getInstance().get(Calendar.YEAR));
        se.setCreateTime(new Date());
        se.setLastUpdateTime(new Date());
        return se;
    }


    private List<Question> getQuestions(List<Long> questionIds) {

        if (CollectionUtils.isEmpty(questionIds)) {
            return new ArrayList<Question>();
        }
        int mark = 500;//每次取少量，防止过长拼接导致solr报错
        List<Question> alls = new ArrayList<>();
        for (int i = 0; i < questionIds.size(); i = i + mark) {
            List<Long> questionIds1 = questionIds.subList(i, (i + mark) <= questionIds.size() ? (i + mark) :
                    questionIds.size());
            logger.debug("the " + (i / mark + 1) + " batch find :" + questionIds1);
            FindQuestionsByIdsRequest request = new FindQuestionsByIdsRequest();
            request.setQuestionIds(questionIds1);
            request.setRecursive(false);
            FindQuestionsByIdsResponse rep = questionService_v2.findQuestionsByIds(request);
            alls.addAll(rep.getQuestionList());
        }

        return alls;
    }


    private List<Long> getEnabledQuesReco(List<Long> quesIds) {
        List<Question> enabledQues = removeDisableQues(quesIds);
        List<Long> enabledQuesReco = StuAnswerUtil.getIdsByQues(enabledQues);
        return enabledQuesReco;
    }


    private List<Question> removeDisableQues(List<Long> quesIds) {
        List<Question> enableQues = new ArrayList<>();
        if (CollectionUtils.isEmpty(quesIds)) {
            return enableQues;
        }

        List<Question> quesSimpleInfos = getQuestions(quesIds);
        for (Question ques : quesSimpleInfos) {
            String state = ques.getState();
            Integer newFormat = ques.getNewFormat();
            //有效且转换成功的
            if ((StringUtils.equals(state, AppType.PaperServiceImpl.STATE_ENABLED)) && (newFormat != null &&
                    newFormat.equals(1))) {
                enableQues.add(ques);
            }
        }
        return enableQues;
    }


    private static final int targetRecommendCount = 5;//推题数量
    //局部变量，不能使用方法内变量
    private final ExecutorService threadPool = Executors.newCachedThreadPool();

    /**
     * 获得某个知识点下指定数量的题目
     *
     * @param workLevel           知识点id
     * @param levelId             知识点级别
     * @param solrGetQuestionType 用知识点字符串全匹配，还是用知识点多值匹配  @see 常量
     */
    List<Long> getQues(int getQuesCount, Integer resourceType, Integer workLevel, Long levelId, String
            solrGetQuestionType) {
        //易中难，从solr并发获得

        List<String> quesDiffsEasy = new ArrayList<>();
        quesDiffsEasy.add(String.valueOf(AppType.QuestionDifficuty.DIFFICUTY1));
        List<String> quesDiffsMid = new ArrayList<>();
        quesDiffsMid.add(String.valueOf(AppType.QuestionDifficuty.DIFFICUTY2));
        List<String> quesDiffsHard = new ArrayList<>();
        quesDiffsHard.add(String.valueOf(AppType.QuestionDifficuty.DIFFICUTY3));
        quesDiffsHard.add(String.valueOf(AppType.QuestionDifficuty.DIFFICUTY4));

        Callable<List<Question>> callableEasy = new SolrSearchCallable(questionSolrSearchService, resourceType,
                workLevel, levelId, getQuesCount, quesDiffsEasy, solrGetQuestionType);
        Callable<List<Question>> callableMid = new SolrSearchCallable(questionSolrSearchService, resourceType,
                workLevel, levelId, getQuesCount, quesDiffsMid, solrGetQuestionType);
        Callable<List<Question>> callableHard = new SolrSearchCallable(questionSolrSearchService, resourceType,
                workLevel, levelId, getQuesCount, quesDiffsHard, solrGetQuestionType);

        FutureTask<List<Question>> futureEasy = new FutureTask(callableEasy);
        FutureTask<List<Question>> futureMid = new FutureTask(callableMid);
        FutureTask<List<Question>> futureHard = new FutureTask(callableHard);

        threadPool.execute(futureEasy);
        threadPool.execute(futureMid);
        threadPool.execute(futureHard);

        List<Question> easyQuestionList = new ArrayList<>();
        List<Question> midQuestionList = new ArrayList<>();
        List<Question> hardQuestionList = new ArrayList<>();
        try {
            long st = System.currentTimeMillis();
            easyQuestionList = futureEasy.get();
            midQuestionList = futureMid.get();
            hardQuestionList = futureHard.get();
            long et = System.currentTimeMillis();
            logger.debug("solr three times  cost :" + (et - st) + " milli ");

            if (easyQuestionList == null) {
                easyQuestionList = new ArrayList<>();
            }
            if (midQuestionList == null) {
                midQuestionList = new ArrayList<>();
            }
            if (hardQuestionList == null) {
                hardQuestionList = new ArrayList<>();
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.error(e.getMessage(), e);
        }
        List<Question> quesList = new ArrayList<>();
        quesList.addAll(easyQuestionList);
        quesList.addAll(midQuestionList);
        quesList.addAll(hardQuestionList);

        List<Long> ids = new ArrayList<>();
        for (Question q : quesList) {
            ids.add(q.getId());
        }
        return ids;
    }


    @Override
    public StudentWorkPageOutput findWorksPage(StudentWorkPageInput studentWorkPageInput) {
        logger.info("studentWorkPageInput:" + JsonUtil.obj2Json(studentWorkPageInput));
        Long subjectId = studentWorkPageInput.getSubjectId();
        Long studentId = studentWorkPageInput.getStudentId();
        Integer currentPage = studentWorkPageInput.getCurrentpage();
        Integer pageSize = studentWorkPageInput.getPagesize();
        if (subjectId == null || studentId == null || currentPage == null || pageSize == null) {
            logger.warn("input param illegal!");
            StudentWorkPageOutput output = new StudentWorkPageOutput();
            output.setCurrentPage(0);
            output.setTotalPageCount(0);
            output.setStudentWorks(new ArrayList<StudentWork>());
            return output;
        }

        Map<String, Object> params = new HashMap<>();
        params.put("studentId", studentId);
        params.put("subjectId", subjectId);

        Integer workLevel = studentWorkPageInput.getWorkLevel();
        Long levelId = studentWorkPageInput.getLevelId();
        if (workLevel != null && levelId != null) {
            int workLevelInt = workLevel.intValue();
            if (workLevelInt == 1) {
                params.put("moduleId", levelId);
            } else if (workLevelInt == 2) {
                params.put("unitId", levelId);
            } else if (workLevelInt == 3) {
                params.put("topicId", levelId);
            }
        }

        StudentWorkPageInput.WorkStatusEnum workStatusEnum = studentWorkPageInput.getWorkStatusEnum();
        if (workStatusEnum != null) {
            params.put("status", workStatusEnum.getStatus());
        }

        PageBounds pager = new PageBounds(currentPage, pageSize);
        params.put("pager", pager);
        long st1 = System.currentTimeMillis();
        PageResult<StudentWork> pages = studentWorkDao.findWorksPage(params);
        long et1 = System.currentTimeMillis();
        logger.debug(" studentWorkDao cost " + (et1 - st1) + " millis " + pages.getPageList().size());
        List<StudentWork> studentWorks = pages.getPageList();
        Paginator paginator = pages.getPager();
        StudentWorkPageOutput output = new StudentWorkPageOutput();
        output.setCurrentPage(paginator.getPage());
        output.setTotalPageCount(paginator.getTotalPages());
        output.setStudentWorks(studentWorks);

        long st2 = System.currentTimeMillis();

        long et2 = System.currentTimeMillis();
        logger.debug(" detail cost " + (et2 - st2) + " millis ");
        logger.info("studentWorkPageoutput:" + JsonUtil.obj2Json(output));
        return output;
    }

    @Override
    public void destroy() throws Exception {
        threadPool.shutdown();
    }

    /**
     * solr查询题目
     *
     * @author shengxian.xiao
     */
    static class SolrSearchCallable implements Callable<List<Question>> {
        private QuestionSolrSearchService questionSolrSearchService;
        private Integer quesNumber;
        private List<String> quesDiffs;
        private Integer level;
        private Long levelId;
        private String solrGetQuestionType;
        private Integer resourceType;
        private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

        /**
         * @param quesNumber          题目数量
         * @param level               知识点级别
         * @param levelId             知识点id
         * @param solrGetQuestionType 字符串全部匹配，多值匹配
         */
        public SolrSearchCallable(QuestionSolrSearchService questionSolrSearchService, Integer resourceType, Integer
                level, Long levelId, Integer quesNumber, List<String> quesDiffs, String solrGetQuestionType) {
            this.questionSolrSearchService = questionSolrSearchService;
            this.quesDiffs = quesDiffs;
            this.level = level;
            this.levelId = levelId;
            this.quesNumber = quesNumber;
            this.solrGetQuestionType = solrGetQuestionType;
            this.resourceType = resourceType;
        }

        @Override
        public List<Question> call() {


            Map<String, Object> map = new HashMap<>();
            Map<String, Object> fqMap = new HashMap<>();
            map.put("fq", fqMap);
            map.put("rows", quesNumber);
            fqMap.put("state", AppType.PaperStatus.ENABLED);
            fqMap.put("parentQuestionId", 0);
            fqMap.put("difficulty", quesDiffs);
            fqMap.put("newFormat", 1);

            String level1LName = "allModuleIds";
            String level2LName = "allUnitIds";
            String level3LName = "allTopicIds";

            String level1SName = "allModuleIdStr";
            String level2SName = "allUnitIdStr";
            String level3SName = "allTopicIdStr";

            if (StuAnswerConstant.ResourceType.RESOURCE_TYPE_MATERIAL.equals(resourceType)) {
                level1LName = "chapterId1";
                level2LName = "chapterId2";
                level3LName = "chapterId3";

                level1SName = "chapterStr1";
                level2SName = "chapterStr2";
                level3SName = "chapterStr3";
            }

            if (solrGetQuestionType.equals(StuAnswerUtil.SOLR_GET_QUESTION_TYPE_EQUAL)) {
                if (level.equals(StudentWork.WorkLevel.TOPIC)) {
                    fqMap.put(level3SName, levelId.toString());
                } else if (level.equals(StudentWork.WorkLevel.UNIT)) {
                    fqMap.put(level2SName, levelId.toString());
                } else if (level.equals(StudentWork.WorkLevel.MODULE)) {
                    fqMap.put(level1SName, levelId.toString());
                }
            }
            if (solrGetQuestionType.equals(StuAnswerUtil.SOLR_GET_QUESTION_TYPE_MULTI)) {
                List<String> list = new ArrayList<>();
                list.add(levelId.toString());
                if (level.equals(StudentWork.WorkLevel.TOPIC)) {
                    fqMap.put(level3LName, list);
                } else if (level.equals(StudentWork.WorkLevel.UNIT)) {
                    fqMap.put(level2LName, list);
                } else if (level.equals(StudentWork.WorkLevel.MODULE)) {
                    fqMap.put(level1LName, list);
                }
            }

            logger.debug("run:" + Thread.currentThread().getName() + "," + JsonUtil.obj2Json(map));
            SolrPage<QuestionDocument> queses = null;
            try {
                queses = questionSolrSearchService.search(new SolrQueryPageReq(map)).getPage();
            } catch (SolrSearchServiceException e) {
                logger.error(e.getMessage(), e);
            }
            List<Question> datalist = new ArrayList<>();
            if (queses != null) {
                datalist = StuAnswerUtil.convertToQuestion(queses.getList());
            }

            logger.debug("run:" + Thread.currentThread().getName() + "," + JsonUtil.obj2Json(datalist));
            logger.debug("run:" + Thread.currentThread().getName() + "," + JsonUtil.obj2Json(datalist.size()));
            return datalist;

        }
    }


    /**
     * 获取单个 复习（学生作业记录表对象）
     */
    public StudentWork findById(long id) {
        return studentWorkDao.findById(id);
    }

    @Override
    public void updateWorkStatus(String exerciseSource, Long resourceId) {
        try{
            if (StuAnswerConstant.ExerciseSource.WORK.equals(exerciseSource) || StuAnswerConstant.ExerciseSource.ENHANCE
                    .equals(exerciseSource)) {
                StudentWork studentWork = new StudentWork();
                studentWork.setId(resourceId);
                studentWork.setWorkStatus(StuAnswerConstant.WorkStatus.COMPLETE);
                studentWorkDao.update(studentWork);
            }
        }catch (Exception e){
            logger.error("学生刷题状态更新失败,失败的原因是:",e);
            //不影响主业务不做BizException处理
        }

    }

    @Override
    public CreateStudentWorkResponse createStudentWork(CreateStudentWorkRequest request) throws BizLayerException {
        StudentWork studentWork = new StudentWork();
        BeanUtils.copyProperties(request, studentWork);
        studentWork.setYear(Calendar.getInstance().get(Calendar.YEAR));
        studentWork.setWorkStatus(StuAnswerConstant.WorkStatus.NO_COMPLETE);
        studentWork.setCreateTime(new Date());
        StudentWork studentWorkResult = studentWorkDao.insertGetId(studentWork);
        Long id = studentWorkResult.getId();
        CreateStudentWorkResponse response = new CreateStudentWorkResponse();
        response.setWorkId(id);
        return response;
    }

    /**
     * 刷题由异步改为同步，屏蔽掉因为超时、习题错误引起的答题记录不存在的异常
     * @param createRecordsRequest
     * @return
     * @throws BizLayerException
     */
    @Override
    public CommonDes createBrushRecords(final CreateRecordsRequest createRecordsRequest) throws BizLayerException {
        long start_time=System.currentTimeMillis();
       // final String traceKey = TraceKeyHolder.getTraceKey();
        List<Long> ids = createRecordsRequest.getQuesIds();
        // 通过去重之后的HashSet长度来判断原list是否包含重复元素
        boolean isRepeat = ids.size() != new HashSet<Long>(ids).size();
        if (isRepeat){
            throw  new BizLayerException("", PraxisErrorCode.DATA_IS_REPEAT);
        }
       /* Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

            }
        });
        threadPool.execute(t);*/
       // TraceKeyHolder.setTraceKey(traceKey);
        List<Long> quesIds = createRecordsRequest.getQuesIds();
        Long resourceId = createRecordsRequest.getResourceId();
        Long stuId = createRecordsRequest.getStudentId();
        Integer exerciseSource = Integer.valueOf(StuAnswerConstant.ExerciseSource.WORK);
        List<Question> questionList = StuAnswerUtil.getQuesListByIdsFast(questionService_v2, quesIds);
        if (questionList == null || questionList.size() == 0) {
            logger.error("get question list is null from praxis svr,,quesIds list ={}", quesIds);
            throw  new BizLayerException("", PraxisErrorCode.BRUSH_QUESTION_EXCEPTION);
        }
        List<StudentExercise> params = new ArrayList<>();
        for (Question ques : questionList) {
            if (!ques.isSingle()) {
                for (Question subQues : ques.getSubQuestions()) {
                    params.add(initStuExeByQues(subQues, resourceId, stuId, exerciseSource));
                }
            } else {
                params.add(initStuExeByQues(ques, resourceId, stuId, exerciseSource));
            }
        }
        List<StudentExercise> studentExerciseList = sort(quesIds, params);
        studentExerciseDao.creates(studentExerciseList);
        long end_time=System.currentTimeMillis();
        logger.info("createBrushRecords createRecords success! cost Time:{}ms",end_time-start_time);
        return new CommonDes();
    }

    @Override
    public void setLogger(String exerciseSource, Long resourceId) {
        if (StuAnswerConstant.ExerciseSource.WORK.equals(exerciseSource)) {
            StudentWork byId = studentWorkDao.findById(resourceId);
            LoggerHolder.set(byId);
        }
    }

}


