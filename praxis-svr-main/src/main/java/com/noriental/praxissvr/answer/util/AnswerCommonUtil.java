package com.noriental.praxissvr.answer.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.noriental.adminsvr.bean.knowledge.Module;
import com.noriental.adminsvr.bean.knowledge.Topic;
import com.noriental.adminsvr.bean.knowledge.Unit;
import com.noriental.adminsvr.bean.teaching.Chapter;
import com.noriental.adminsvr.request.RequestEntity;
import com.noriental.adminsvr.response.ResponseEntity;
import com.noriental.adminsvr.service.knowledge.ModuleService;
import com.noriental.adminsvr.service.knowledge.TopicService;
import com.noriental.adminsvr.service.knowledge.UnitService;
import com.noriental.adminsvr.service.teaching.ChapterService;
import com.noriental.lessonsvr.entity.request.LinkResourceWithPublishInfo;
import com.noriental.lessonsvr.entity.request.LongRequest;
import com.noriental.lessonsvr.entity.request.ResourceParam;
import com.noriental.lessonsvr.entity.vo.LinkPublishResource;
import com.noriental.lessonsvr.entity.vo.LinkResourceStudentVo;
import com.noriental.lessonsvr.rservice.LessonService;
import com.noriental.log.TraceKeyHolder;
import com.noriental.praxissvr.answer.bean.QuestionStructAndParentId;
import com.noriental.praxissvr.answer.bean.StudentExercise;
import com.noriental.praxissvr.answer.bean.UpdateSubmitAnswerVo;
import com.noriental.praxissvr.answer.request.UpdateSubmitAnswer;
import com.noriental.praxissvr.answer.service.StudentExerciseService;
import com.noriental.praxissvr.brush.bean.StudentWorkAnswer;
import com.noriental.praxissvr.common.QuestionState;
import com.noriental.praxissvr.common.QuestionTypeEnum;
import com.noriental.praxissvr.common.StuAnswerConstant;
import com.noriental.praxissvr.question.bean.Question;
import com.noriental.praxissvr.question.request.FindQuestionsRequest;
import com.noriental.praxissvr.question.response.FindQuestionsResponse;
import com.noriental.praxissvr.questionSearch.service.QuestionSearchService;
import com.noriental.praxissvr.utils.HttpUtil;
import com.noriental.praxissvr.wrong.bean.SimpleKnowVo;
import com.noriental.utils.json.JsonUtil;
import com.noriental.validate.bean.CommonResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.BeanUtils;

import java.lang.invoke.MethodHandles;
import java.util.*;

import static com.noriental.praxissvr.answer.util.StuAnswerUtil.*;

/**
 * @author kate
 * @create 2017-12-20 15:06
 * @desc 学生做答、老师批改后置业务处理工具类
 **/
public class AnswerCommonUtil {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * 根据题目ID查询题的详情
     * @param questionIdList
     * @param questionSearchService
     * @return
     */
    public static List<Question> getQuesListByIds(List<Long> questionIdList, QuestionSearchService
            questionSearchService) {
        long s = System.currentTimeMillis();
        FindQuestionsRequest req = new FindQuestionsRequest();
        req.setIds(questionIdList);
        req.setBasic(true);
        req.setPageable(false);
        req.setQuestionType(QuestionTypeEnum.ALL);
        req.setStates(Collections.singletonList(QuestionState.ALL));
        FindQuestionsResponse questionsResponse = questionSearchService.findSolrQuestions(req);
        logger.info("findQuestions cost time:" + (System.currentTimeMillis() - s));
        if (questionsResponse.success()) {
            return questionsResponse.getQuestionList();
        } else {
            logger.error("not found!" + questionIdList);
            return new ArrayList<>();
        }
    }


    public static Map<Long, Question> getQuesMapByQuesList(List<Question> quesList) {
        Map<Long, Question> questionMap = new HashMap<>(10);
        for (Question ques : quesList) {
            questionMap.put(ques.getId(), ques);
        }
        return questionMap;
    }


    /**
     * 获得题目的所有知识点
     *
     * @param quesList
     * @return key:topicId,value:topicId&unitId&moduleId&subjectId
     */
    public static Map<Long, SimpleKnowVo> getTopicMapByQuesList(List<Question> quesList, TopicService topicService,
                                                                UnitService unitService, ModuleService moduleService) {
        List<Long> allTopicIds = getAllTopicIds(quesList);
        allTopicIds = new ArrayList<>(new HashSet<>(allTopicIds));
        Map<Long, Long> topicUnitMap = getTopicUnitMap(allTopicIds, topicService);
        List<Long> allUnitIds = new ArrayList<>(new HashSet<>(topicUnitMap.values()));
        Map<Long, Long> unitModuleMap = new HashMap<>(5);
        Map<Long, Long> moduleSubjectMap = new HashMap<>(5);
        setUnitsMap(allUnitIds, unitModuleMap, moduleSubjectMap, unitService, moduleService);
        return getKnowVoMap(allTopicIds, topicUnitMap, unitModuleMap, moduleSubjectMap);
    }

    /**
     * @param allTopicIds
     * @param topicUnitMap
     * @param unitModuleMap
     * @param moduleSubjectMap
     * @return
     */
    public static Map<Long, SimpleKnowVo> getKnowVoMap(List<Long> allTopicIds, Map<Long, Long> topicUnitMap,
                                                       Map<Long, Long> unitModuleMap, Map<Long, Long>
                                                               moduleSubjectMap) {
        Map<Long, SimpleKnowVo> knowVoMap = new HashMap<>(5);
        for (Long topicId : allTopicIds) {
            SimpleKnowVo vo = new SimpleKnowVo();
            Long unitId = topicUnitMap.get(topicId);
            Long moduleId = unitModuleMap.get(unitId);
            Long subjectId = moduleSubjectMap.get(moduleId);
            vo.setTopicId(topicId);
            vo.setUnitId(unitId);
            vo.setModuleId(moduleId);
            vo.setSubjectId(subjectId);
            knowVoMap.put(topicId, vo);
        }
        return knowVoMap;
    }


    /***
     *
     * @param allUnitIds
     * @param unitModuleMap
     * @param moduleSubjectMap
     * @param unitService
     * @param moduleService
     */
    public static void setUnitsMap(List<Long> allUnitIds, Map<Long, Long> unitModuleMap, Map<Long, Long>
            moduleSubjectMap, UnitService unitService, ModuleService moduleService) {
        if (CollectionUtils.isNotEmpty(allUnitIds)) {
            ResponseEntity<List<Unit>> unitResp = unitService.findByIds(new RequestEntity<>(allUnitIds));
            List<Unit> units = unitResp.getEntity();
            List<Long> moduleIds = new ArrayList<>();
            for (Unit u : units) {
                unitModuleMap.put(u.getId(), u.getModule().getId());
                moduleIds.add(u.getModule().getId());
            }
            moduleIds = new ArrayList<>(new HashSet<>(moduleIds));
            if (CollectionUtils.isNotEmpty(moduleIds)) {
                ResponseEntity<List<Module>> resp = moduleService.findByIds(new RequestEntity<>(moduleIds));
                List<Module> modules = resp.getEntity();
                for (Module m : modules) {
                    moduleSubjectMap.put(m.getId(), m.getSubject().getId());
                }
            }
        }
    }

    /**
     * @param allTopicIds
     * @param topicService
     * @return
     */
    public static Map<Long, Long> getTopicUnitMap(List<Long> allTopicIds, TopicService topicService) {
        Map<Long, Long> topicUnitMap = new HashMap<>(5);
        if (CollectionUtils.isNotEmpty(allTopicIds)) {
            ResponseEntity<List<Topic>> resp = topicService.findByIds(new RequestEntity<>(allTopicIds));
            List<Topic> topics = resp.getEntity();
            for (Topic t : topics) {
                topicUnitMap.put(t.getId(), t.getUnit().getId());
            }
        }
        return topicUnitMap;
    }

    public static List<Long> getAllTopicIds(List<Question> quesList) {
        List<Long> allTopicIds = new ArrayList<>();
        for (Question ques : quesList) {
            if (CollectionUtils.isNotEmpty(ques.getTopicId())) {
                allTopicIds.addAll(ques.getTopicId());
            }
        }
        return allTopicIds;
    }

    /**
     * 设置某个题目的体系，级别ids，填空题空个数
     *
     * @param studentWorkAnswer
     * @param resourceType
     * @param quesMaps
     * @param topicKnowMap
     */
    public static void setModuleUnitTopicOfQues(StudentWorkAnswer studentWorkAnswer, Integer resourceType, Map<Long,
            Question> quesMaps, Map<Long, SimpleKnowVo> topicKnowMap) {
        Long questionId = studentWorkAnswer.getQuestionId();
        //该题目模块、单元、主题
        Question ques = quesMaps.get(questionId);
        if (ques == null) {
            ques = new Question();
        }
        studentWorkAnswer.setAnswerNum(ques.getAnswerNum());

        List<Long> questionTopics;
        List<Long> questionModules = new ArrayList<>();
        List<Long> questionUnits = new ArrayList<>();

        if (StuAnswerConstant.ResourceType.RESOURCE_TYPE_KNOWLEDGE.equals(resourceType)) {
            if (ques.getTopicId() != null) {
                questionTopics = ques.getTopicId();
            } else {
                questionTopics = new ArrayList<>();
            }
            setOtherKnow(questionTopics, questionModules, questionUnits, ques, topicKnowMap);
        } else {
            //获得题目的章节，根据级别设置
            List<Long> c1 = ques.getChapterId1();
            List<Long> c2 = ques.getChapterId2();
            List<Long> c3 = ques.getChapterId3();
            if (c3 != null) {
                questionTopics = c3;
            } else {
                questionTopics = new ArrayList<>();
            }

            if (c2 != null) {
                questionUnits = c2;
            } else {
                questionUnits = new ArrayList<>();
            }

            if (c1 != null) {
                questionModules = c1;
            } else {
                questionModules = new ArrayList<>();
            }
        }

        studentWorkAnswer.setResourceType(resourceType);
        studentWorkAnswer.setTopicIds(questionTopics);
        studentWorkAnswer.setUnitIds(questionUnits);
        studentWorkAnswer.setModuleIds(questionModules);

        logger.debug("questionId,moduleIds,unitIds,topicIds:" + questionId + "," + questionModules + "," +
                questionUnits + "," + questionTopics);
    }


    public static void setOtherKnow(List<Long> questionTopics, List<Long> questionModules, List<Long> questionUnits,
                                    Question quiz, Map<Long, SimpleKnowVo> topicKnowMap) {
        setOtherKnow(questionTopics, questionUnits, questionModules, topicKnowMap);
    }

    public static void setOtherKnow(List<Long> questionTopics, List<Long> questionUnits, List<Long> questionModules,
                                    Map<Long, SimpleKnowVo> topicKnowMap) {
        if (CollectionUtils.isNotEmpty(questionTopics)) {
            for (Long topicId : questionTopics) {
                SimpleKnowVo simpleKnowVo = topicKnowMap.get(topicId);
                if (!questionUnits.contains(simpleKnowVo.getUnitId())) {
                    questionUnits.add(simpleKnowVo.getUnitId());
                }
                if (!questionModules.contains(simpleKnowVo.getModuleId())) {
                    questionModules.add(simpleKnowVo.getModuleId());
                }
            }
        }
    }


    /**
     * 获得题目的所有章节
     *
     * @param quesList
     * @return key:chapterId,value:chapterId1（一级）&chapterId1（二级）&chapterId1（三级）
     */
    public static Map<Long, SimpleKnowVo> getChapterMapByQuesList(List<Question> quesList, ChapterService
            chapterService) {
        List<Long> chapterIds = getChapterIdsByQuesList(quesList);
        chapterIds = new ArrayList<>(new HashSet<>(chapterIds));
        Map<Long, Chapter> chapterMap = getChaptersByIds(chapterIds, chapterService);
        return getChapterMap(chapterIds, chapterMap);
    }

    public static Map<Long, SimpleKnowVo> getChapterMap(List<Long> chapterIds, Map<Long, Chapter> chapterMap) {
        Map<Long, SimpleKnowVo> map = new HashMap<>(5);
        for (Long chapterId : chapterIds) {
            SimpleKnowVo vo = getVO(chapterId, chapterMap);
            if (vo != null) {
                map.put(chapterId, vo);
            }
        }
        return map;
    }

    public static SimpleKnowVo getVO(Long chapterId, Map<Long, Chapter> chapterMap) {
        SimpleKnowVo vo = new SimpleKnowVo();
        Chapter chapter = chapterMap.get(chapterId);
        if (chapter == null) {
            return null;
        }
        vo.setSubjectId(chapter.getSubjectId());
        vo.setDirectoryId(chapter.getDirectoryId());
        int level = chapter.getLevel();
        if (level == 1) {
            vo.setModuleId(chapter.getId());
        }
        if (level == 2) {
            vo.setUnitId(chapter.getId());
            vo.setModuleId(chapter.getParentId());
        }
        if (level == 3) {
            vo.setTopicId(chapter.getId());
            long parentId = chapter.getParentId();
            vo.setUnitId(parentId);
            Chapter chapterP = chapterMap.get(parentId);
            if (chapterP == null) {
                return null;
            }
            vo.setModuleId(chapterP.getParentId());
        }
        return vo;
    }

    public static Map<Long, Chapter> getChaptersByIds(List<Long> chapterIds, ChapterService chapterService) {
        Map<Long, Chapter> map = new HashMap<>(10);
        if (CollectionUtils.isNotEmpty(chapterIds)) {
            ResponseEntity<List<Chapter>> resp = chapterService.findByIds(new RequestEntity<>(chapterIds));
            List<Chapter> chapters = resp.getEntity();
            for (Chapter c : chapters) {
                map.put(c.getId(), c);
            }
        }
        return map;
    }

    public static List<Long> getChapterIdsByQuesList(List<Question> quesList) {
        List<Long> chapterIds = new ArrayList<>();
        for (Question ques : quesList) {
            if (CollectionUtils.isNotEmpty(ques.getChapterId1())) {
                chapterIds.addAll(ques.getChapterId1());
            }
            if (CollectionUtils.isNotEmpty(ques.getChapterId2())) {
                chapterIds.addAll(ques.getChapterId2());
            }
            if (CollectionUtils.isNotEmpty(ques.getChapterId3())) {
                chapterIds.addAll(ques.getChapterId3());
            }
        }
        return chapterIds;
    }


    /**
     * 原始格式转换成中间格式
     *
     * @param resourceType 体系类型，分别查询题目的知识点或者章节
     * @param topicKnowMap
     * @return
     */
    public static List<StudentWorkAnswer> toMiddle(List<StudentExercise> seList, Map<Long, Question> quesMaps,
                                                   Integer resourceType, Map<Long, SimpleKnowVo> topicKnowMap) {
        //中间格式
        List<StudentWorkAnswer> studentWorkAnswers = new ArrayList<>();
        for (StudentExercise answerQues : seList) {
            Long studentId = answerQues.getStudentId();
            Long questionId = answerQues.getQuestionId();
            String result = answerQues.getResult();
            String submitAnswer = answerQues.getSubmitAnswer();
            Long parentQuestionId = answerQues.getParentQuestionId();
            Integer structId = answerQues.getStructId();
            //提交的单题,设置单题的题目，答案，答案结果，知识点，增加到中间格式
            if (parentQuestionId == null) {
                StudentWorkAnswer studentWorkAnswer = new StudentWorkAnswer();
                studentWorkAnswer.setQuestionId(questionId);
                studentWorkAnswer.setStudentId(studentId);
                studentWorkAnswer.setResult(result);
                studentWorkAnswer.setSubmitAnswer(submitAnswer);
                studentWorkAnswer.setStructId(structId);
                studentWorkAnswer.setResourceType(resourceType);
                //设置某个题目的知识点:主题、单元、模块
                setModuleUnitTopicOfQues(studentWorkAnswer, resourceType, quesMaps, topicKnowMap);
                studentWorkAnswers.add(studentWorkAnswer);
                //提交的是一个大题下的小题
            } else {
                StudentWorkAnswer studentWorkAnswer = new StudentWorkAnswer();
                studentWorkAnswer.setQuestionId(parentQuestionId);
                studentWorkAnswer.setStudentId(studentId);
                //中间格式中没有该大题，把大题小题增加到中间格式，增加前设置大题的知识点，小题的知识点、答案、答案结果
                if (!studentWorkAnswers.contains(studentWorkAnswer)) {
                    //设置大题的知识点:主题、单元、模块
                    setModuleUnitTopicOfQues(studentWorkAnswer, resourceType, quesMaps, topicKnowMap);
                    //增加小题
                    StudentWorkAnswer subStudentWorkAnswer = new StudentWorkAnswer();
                    subStudentWorkAnswer.setQuestionId(questionId);
                    subStudentWorkAnswer.setStudentId(studentId);
                    subStudentWorkAnswer.setResult(result);
                    subStudentWorkAnswer.setSubmitAnswer(submitAnswer);
                    subStudentWorkAnswer.setStructId(structId);
                    subStudentWorkAnswer.setResourceType(resourceType);
                    //设置小题的知识点:主题、单元、模块
                    setModuleUnitTopicOfQues(subStudentWorkAnswer, resourceType, quesMaps, topicKnowMap);
                    List<StudentWorkAnswer> subQuesAnswers = new ArrayList<>();
                    subQuesAnswers.add(subStudentWorkAnswer);
                    studentWorkAnswer.setSubQuesAnswers(subQuesAnswers);
                    studentWorkAnswers.add(studentWorkAnswer);
                } else {
                    //获得该大题
                    studentWorkAnswer = studentWorkAnswers.get(studentWorkAnswers.indexOf(studentWorkAnswer));

                    //增加小题
                    StudentWorkAnswer subStudentWorkAnswer = new StudentWorkAnswer();
                    subStudentWorkAnswer.setQuestionId(questionId);
                    subStudentWorkAnswer.setStudentId(studentId);
                    subStudentWorkAnswer.setResult(result);
                    subStudentWorkAnswer.setSubmitAnswer(submitAnswer);
                    subStudentWorkAnswer.setStructId(structId);
                    subStudentWorkAnswer.setResourceType(resourceType);
                    //设置小题的知识点:主题、单元、模块
                    setModuleUnitTopicOfQues(subStudentWorkAnswer, resourceType, quesMaps, topicKnowMap);
                    List<StudentWorkAnswer> subQuesAnswers = studentWorkAnswer.getSubQuesAnswers();
                    subQuesAnswers.add(subStudentWorkAnswer);
                    studentWorkAnswer.setSubQuesAnswers(subQuesAnswers);
                }
            }
        }
        return studentWorkAnswers;
    }

    /***
     * 批改习题的知识点、章节单题组装和复合体组装
     * @param se
     * @param quesMap
     * @param resourceType
     * @param topicKnowMap
     * @param studentExerciseService
     * @return
     */
    public static StudentWorkAnswer toMiddleC(StudentExercise se, Map<Long, Question> quesMap, Integer resourceType,
                                              Map<Long, SimpleKnowVo> topicKnowMap, StudentExerciseService
                                                      studentExerciseService) {
        String exerciseSource = se.getExerciseSource();
        String redoSource = se.getRedoSource();
        Integer structId = se.getStructId();
        Long parentQuestionId = se.getParentQuestionId();
        String result = se.getResult();
        Long questionId = se.getQuestionId();
        Long resourceId = se.getResourceId();
        Long studentId = se.getStudentId();
        Long classId = se.getClassId();
        //该题目模块、单元、主题
        StudentWorkAnswer thisAnswer = new StudentWorkAnswer();
        thisAnswer.setStudentId(studentId);
        thisAnswer.setQuestionId(questionId);
        thisAnswer.setStructId(structId);
        thisAnswer.setResult(result);
        thisAnswer.setParentQuestionId(parentQuestionId);
        AnswerCommonUtil.setModuleUnitTopicOfQues(thisAnswer, resourceType, quesMap, topicKnowMap);
        //大题
        if (parentQuestionId != null) {
            StudentWorkAnswer studentWorkAnswer = new StudentWorkAnswer();
            studentWorkAnswer.setQuestionId(parentQuestionId);
            studentWorkAnswer.setStudentId(studentId);
            //设置大题的模块单元主题
            AnswerCommonUtil.setModuleUnitTopicOfQues(studentWorkAnswer, resourceType, quesMap, topicKnowMap);
            //大题下所有小题
            List<StudentWorkAnswer> subQuesAnswers = new ArrayList<>();
            studentWorkAnswer.setSubQuesAnswers(subQuesAnswers);
            subQuesAnswers.add(thisAnswer);
            StudentExercise seDin = new StudentExercise();
            seDin.setStudentId(studentId);
            seDin.setResourceId(resourceId);
            seDin.setClassId(classId);
            seDin.setParentQuestionId(parentQuestionId);
            seDin.setExerciseSource(exerciseSource);
            seDin.setRedoSource(redoSource);
            List<StudentExercise> subQueses = studentExerciseService.getDbRecords(seDin);
            for (StudentExercise subQues : subQueses) {
                Long subQuesId = subQues.getQuestionId();
                if (!subQuesId.equals(questionId)) {
                    StudentWorkAnswer studentWorkAnswerSub = new StudentWorkAnswer();
                    studentWorkAnswerSub.setQuestionId(subQuesId);
                    studentWorkAnswerSub.setStudentId(studentId);
                    //设置小题的模块单元主题
                    AnswerCommonUtil.setModuleUnitTopicOfQues(studentWorkAnswerSub, resourceType, quesMap,
                            topicKnowMap);
                    studentWorkAnswerSub.setResult(subQues.getResult());
                    studentWorkAnswerSub.setStructId(subQues.getStructId());
                    subQuesAnswers.add(studentWorkAnswerSub);
                }
            }
            return studentWorkAnswer;
        } else {
            return thisAnswer;
        }
    }

    /**
     * 获得指定小题题目的答题数据
     *
     * @param studentWorkAnswer 复合题答题数据
     * @param quesId            小题id
     * @return 小题的答题数据
     */
    public static StudentWorkAnswer getSubIfParent(StudentWorkAnswer studentWorkAnswer, Long quesId) {
        StudentWorkAnswer newStudentWorkAnswer = new StudentWorkAnswer();
        BeanUtils.copyProperties(studentWorkAnswer, newStudentWorkAnswer);

        if (CollectionUtils.isNotEmpty(studentWorkAnswer.getSubQuesAnswers())) {
            for (StudentWorkAnswer sub : studentWorkAnswer.getSubQuesAnswers()) {
                if (sub.getQuestionId().equals(quesId)) {
                    newStudentWorkAnswer.setSubQuesAnswers(Collections.singletonList(sub));
                    break;
                }
            }
        }
        return newStudentWorkAnswer;
    }

    public static void initLogRequestId(String requestId) {
        if (StringUtils.isNotBlank(requestId)) {
            MDC.put("id", requestId);
            TraceKeyHolder.setTraceKey(requestId);
            logger.debug("set requestId:" + requestId);
        } else {
            logger.warn("requestId is null!");
        }
    }
    /***
     * 根据题型结构判断是否全部批改完成
     * @param result
     * @param structId
     * @return
     */
    public static boolean isAllCorrect(String result, Integer structId) {
        //result 答题状态 7/无答案，6/有答案未批改，1/对，2/错，5/半对
        if (StuAnswerConstant.StructType.STRUCT_TKT.equals(structId)) {
            List<Map<String, Object>> correctResultList = JsonUtil.readValue(result, new
                    TypeReference<List<Map<String, Object>>>() {
            });
            for (Map<String, Object> correctResultMap : correctResultList) {
                String correctValue = correctResultMap.get(BLANK_RESULT) + "";
                if (correctValue.equals(StuAnswerConstant.ExerciseResult.SUBMITED)) { //有答案未批改
                    return false;
                }
            }
            return true;
        } else {
            if (!result.contains(StuAnswerConstant.ExerciseResult.SUBMITED)) { //有答案未批改
                return true;
            }
        }
        return false;
    }

    public static List<Long> getQuestionIdList(List<UpdateSubmitAnswer> updateSubmitAnswerList) {
        List<Long> ids = new ArrayList<>();
        for (UpdateSubmitAnswer updateSubmitAnswer : updateSubmitAnswerList) {
            ids.add(updateSubmitAnswer.getQuestionId());
        }
        return ids;
    }

    public static List<StudentExercise> covertSeList(StudentExercise se, List<UpdateSubmitAnswerVo>
            updateSubmitAnswerVoList) {
        List<StudentExercise> seList = new ArrayList<>();
        for (UpdateSubmitAnswerVo vo : updateSubmitAnswerVoList) {
            StudentExercise se1 = new StudentExercise();
            BeanUtils.copyProperties(se, se1);
            se1.setQuestionId(vo.getQuestionId());
            se1.setResult(vo.getResult());
            se1.setStructId(vo.getStructId());
            se1.setSubmitAnswer(vo.getSubmitAnswer());
            se1.setParentQuestionId(vo.getParentQuestionId());
            seList.add(se1);
        }
        return seList;
    }
    /***
     * 查询设置 题结构和parentId 方法
     * @param updateSubmitAnswerVoList
     */
    public static void setQuesProperty(List<UpdateSubmitAnswerVo> updateSubmitAnswerVoList, QuestionSearchService questionSearchService) {
        long setQuesProperty = System.currentTimeMillis();
        List<Long> quesIdList = StuAnswerUtil.getQuestionIdList(updateSubmitAnswerVoList);
        Map<Long, QuestionStructAndParentId> map = new HashMap<>(updateSubmitAnswerVoList.size() + 1);
        //获取习题结构和习题的父ID
        getQuestionStruct(quesIdList, map,questionSearchService);
        //将查找到的 所有的结构信息 设置到 对象里去
        for (UpdateSubmitAnswerVo vo : updateSubmitAnswerVoList) {
            QuestionStructAndParentId getStruct = map.get(vo.getQuestionId());
            if (getStruct != null) {
                vo.setStructId(getStruct.getStructId());
                vo.setParentQuestionId(getStruct.getParentQuestionId());
            }
        }
        logger.debug("setQuesProperty cost[{}]", System.currentTimeMillis() - setQuesProperty);
    }

    public static List<UpdateSubmitAnswerVo> convertUpdateSubmitAnswerVo(List<UpdateSubmitAnswer> updateSubmitAnswerList) {
        List<UpdateSubmitAnswerVo> updateSubmitAnswerVoList = new ArrayList<>();
        for (UpdateSubmitAnswer updateSubmitAnswer : updateSubmitAnswerList) {
            UpdateSubmitAnswerVo vo = new UpdateSubmitAnswerVo();
            BeanUtils.copyProperties(updateSubmitAnswer, vo);
            updateSubmitAnswerVoList.add(vo);
        }
        return updateSubmitAnswerVoList;
    }
    /**
     * 获取 题 对应的 QuestionStructAndParentId 对象
     *
     * @param questionIdList 题 ID集合
     * @param map            key=questionId  value 查询结果
     */
    public static void getQuestionStruct(List<Long> questionIdList, Map<Long, QuestionStructAndParentId> map,
                                         QuestionSearchService questionSearchService) {
        long start = System.currentTimeMillis();
        List<Question> quesListByIds = StuAnswerUtil.getQuesListByIds(questionSearchService, questionIdList);
        for (Question q : quesListByIds) {
            int structId = new Long(q.getStructId()).intValue();
            Long parentQuestionId = q.getParentQuestionId() > 0 ? q.getParentQuestionId() : null;
            Integer questionTypeId = q.getQuestionTypeId() > 0 ? q.getQuestionTypeId() : null;
            QuestionStructAndParentId qStruct = new QuestionStructAndParentId(q.getId(), structId, parentQuestionId,
                    questionTypeId);
            map.put(q.getId(), qStruct);
        }
        logger.info("getQuestionStruct cost:{}ms", System.currentTimeMillis() - start);
    }
    public static boolean getQuestionPublicTime (Long resourceId,LessonService lessonService,RedisUtilExtend redisUtilExtend,long pythonServerTime) {
        //true : 调别人  false : 调自己
        String key = ANSWER_PUBLIC_TIME_REDIS_KEY_PREFIXX + resourceId;
        Object o = redisUtilExtend.get(key);
        CommonResponse<LinkResourceWithPublishInfo> linkResourceWithPublishInfoCommonResponse;
        CommonResponse<LinkResourceStudentVo> resourceResponse;
        if(o!=null) {
            long publicTime = Long.parseLong(String.valueOf(o));
            if (publicTime > pythonServerTime) {
                return true;
            }else{
                return false;
            }
        }else {
            //根据 发布关联表ID 查找 题集信息
            linkResourceWithPublishInfoCommonResponse = lessonService.fetchResourcePublicByLinkId(new ResourceParam(resourceId));
            if(linkResourceWithPublishInfoCommonResponse != null && linkResourceWithPublishInfoCommonResponse.success()) {
                LinkResourceWithPublishInfo data = linkResourceWithPublishInfoCommonResponse.getData();
                    LinkPublishResource linkPublishResource = data.getLinkPublishResource();
                    Long publishId = linkPublishResource.getPublishId();
                    //获取发布的资源包里的资源
                    resourceResponse = lessonService.fetchLessonDetail(new LongRequest(publishId));
                    if(resourceResponse != null &&resourceResponse.success()) {
                        LinkResourceStudentVo data1 = resourceResponse.getData();
                        String s = String.valueOf(data1.getPublishTime());
                        String sTime = s.substring(0, s.length() - 3);
                        long publishTime = Long.parseLong(sTime);
                        redisUtilExtend.set(key,sTime,ANSWER_PUBLIC_EXPIRE_TIME);
                        if(publishTime > pythonServerTime) {
                            return true;
                        }else{
                            return false;
                        }
                    }
                 }
        }
        logger.error("getQuestionPublicTime error----lessonService.fetchResourcePublicByLinkId->"+JsonUtil.obj2Json(linkResourceWithPublishInfoCommonResponse));
        //当查询lesson-svr失败，无法判断新老数据，由于上线后新数据全部查询python组，默认查询新数据
        return true;
    }



    public static String doPost(String param, final String url){
        //向洪清组发送数据
        final Map<String,String> map = new HashMap<>();
        map.put("param",param);
        long startTime = System.currentTimeMillis();
        String result = HttpUtil.doPostStr(url, map, 5000, 5000, "UTF-8");
        logger.info("------------>调洪清组接口消耗时间 doPost cost time:{} ms", System.currentTimeMillis() - startTime);
        logger.info("------------------------------------->调洪清组接口返回结果 doPost result----->"+JsonUtil.obj2Json(result));
        return result;
    }

    public static String doGet(Map<String,Object> param, final String url){
        //向洪清组发送数据
        long startTime = System.currentTimeMillis();
        String httpUrl = url + "?" + CommonUtil.paramToHttpGetParams(param);
        String result = HttpUtil.doGetStr(httpUrl);
        logger.info("------------->调洪清组接口消耗时间 doGet cost time:{} ms", System.currentTimeMillis() - startTime);
        logger.info("------------------------------------->调洪清组接口返回结果 doGet result------>"+JsonUtil.obj2Json(result));
        return result;
    }
}
