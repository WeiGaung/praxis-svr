package com.noriental.praxissvr.answer.service.impl;

import com.noriental.adminsvr.service.knowledge.ModuleService;
import com.noriental.adminsvr.service.knowledge.TopicService;
import com.noriental.adminsvr.service.knowledge.UnitService;
import com.noriental.adminsvr.service.teaching.ChapterService;
import com.noriental.praxissvr.answer.bean.*;
import com.noriental.praxissvr.answer.mappers.AnswerCorrectMapper;
import com.noriental.praxissvr.answer.service.StuAnswerDataToMqService;
import com.noriental.praxissvr.answer.service.StuAnswerService;
import com.noriental.praxissvr.answer.util.AnswerCommonUtil;
import com.noriental.praxissvr.answer.util.PraxisSsdbUtil;
import com.noriental.praxissvr.answer.util.StuAnswerUtil;
import com.noriental.praxissvr.brush.bean.StudentWorkAnswer;
import com.noriental.praxissvr.common.StuAnswerConstant;
import com.noriental.praxissvr.question.bean.Question;
import com.noriental.praxissvr.questionSearch.service.QuestionSearchService;
import com.noriental.praxissvr.wrong.bean.SimpleKnowVo;
import com.noriental.praxissvr.wrong.service.StuQuesKnowledgeService;
import com.noriental.utils.json.JsonUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Map;

/**
 * @author kate
 * @create 2017-12-20 14:46
 * @desc 学生做答完成监听后置业务处理
 **/
@Component
class SubmitAnswerListener implements ApplicationListener<AnswerSubmitEvent> {
    @Resource
    private StuAnswerService stuAnswerService;
    @Resource
    private QuestionSearchService questionSearchService;
    @Resource
    private TopicService topicService;
    @Resource
    private UnitService unitService;
    @Resource
    private ModuleService moduleService;
    @Resource
    private ChapterService chapterService;
    @Resource
    private StuQuesKnowledgeService stuQuesKnowledgeService;
    @Resource
    private StuAnswerDataToMqService stuAnswerDataToMqService;
    @Resource
    private AnswerCorrectMapper answerCorrectMapper;

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Async
    @Override
    public void onApplicationEvent(AnswerSubmitEvent event) {
        Long startTime = System.currentTimeMillis();
        AnswerSubmitEntity entity = (AnswerSubmitEntity) event.getSource();
        OperateType operateType=entity.getOperateType();
        List<StudentExercise> dataList = entity.getList();
        String traceKey = entity.getTraceKey();
        AnswerCommonUtil.initLogRequestId(traceKey);
        if (CollectionUtils.isEmpty(dataList)) {
            logger.error("UpdateSubmitAnswerListener get data is null");
            return;
        }

        logger.info("UpdateSubmitAnswerListener get data:" + JsonUtil.obj2Json(dataList));
        if (CollectionUtils.isNotEmpty(dataList)) {
            //2、知识点、章节错题录入
            excuteCommonKnowledge(dataList);
            //3、学生提交的刷题数据流转到cms后台
            if (OperateType.ANSWER.equals(operateType)){
                answerDataToCms(dataList);
            }
            //1、向统计服务发送MQ消息
            excuteNotify(dataList);

        }
        Long endTime = System.currentTimeMillis();
        logger.info("SubmitAnswerListener cost time:{} ms", endTime - startTime);

    }

    private void answerDataToCms(List<StudentExercise> dataList) {
        StudentExercise exercise = dataList.get(0);
        String exerciseType = exercise.getExerciseSource();
        //先判断学生做答场景
        if (exerciseType.equals(StuAnswerConstant.ExerciseSource.WORK) || exerciseType.equals(StuAnswerConstant
                .ExerciseSource.GOOD_QUES) || exerciseType.equals(StuAnswerConstant.ExerciseSource.WRONG_QUES)) {
            boolean flag = false;
            for (StudentExercise entity : dataList) {
                if (entity.getStructId().equals(StuAnswerConstant.StructType.STRUCT_JDT) || entity.getStructId()
                        .equals(StuAnswerConstant.StructType.STRUCT_TKT) || entity.getStructId().equals
                        (StuAnswerConstant.StructType.STRUCT_ZTT) || entity.getStructId().equals(StuAnswerConstant
                        .StructType.STRUCT_PZT)) {//判断题型是否为主观题
                    flag = true;
                    break;
                }
            }
            if (flag) {//向cms插入一条数据
                BrushDataEntity brushDataEntity = new BrushDataEntity();
                brushDataEntity.setResourceId(exercise.getResourceId());
                brushDataEntity.setExerciseSource(exercise.getExerciseSource());
                brushDataEntity.setStudentId(exercise.getStudentId());
                brushDataEntity.setBusFlag(brushDataEntity.getBusFlag());
                answerCorrectMapper.insertDataToCms(brushDataEntity);
            }
        }
    }

    private void excuteNotify(List<StudentExercise> seList) {
        StudentExercise notifyEntity = new StudentExercise();
        notifyEntity.setStudentExerciseList(seList);
        notifyTrailForAnswer(notifyEntity, PraxisSsdbUtil.SUBMITTYPE, null);

    }


    private void notifyTrailForAnswer(StudentExercise se, int operaType, StudentExercise exitedRecord) {
        //课单独通知，强化不用上通知
        try {
            stuAnswerService.notifyTrail(se, operaType, exitedRecord);
        } catch (Exception e) {
            logger.error("操作类型operaType={}向统计服务发送消息失败，失败的原因是:", operaType, e);
        }
    }

    private void excuteCommonKnowledge(List<StudentExercise> seList) {
        StudentExercise se = seList.get(0);
        final String exerciseSource = se.getExerciseSource();
        final Long studentId = se.getStudentId();
        //所有题目id：包含小题
        List<Long> quesIdList = StuAnswerUtil.getQuesIdList(seList);
        List<Question> quesListByIds = AnswerCommonUtil.getQuesListByIds(quesIdList, questionSearchService);
        logger.info("学生做答根据习题ID通过solr查询所有的小题ID:" + JsonUtil.obj2Json(quesListByIds));
        final Map<Long, Question> quesMap = AnswerCommonUtil.getQuesMapByQuesList(quesListByIds);
            //获得题目的所有知识点
        final Map<Long, SimpleKnowVo> topicKnowMap = AnswerCommonUtil.getTopicMapByQuesList(quesListByIds,
                topicService, unitService, moduleService);
            //获得题目的所有章节
        final Map<Long, SimpleKnowVo> chapterMap = AnswerCommonUtil.getChapterMapByQuesList(quesListByIds,
                chapterService);
        //转换成中间数据格式
        //把分小题提交的数据格式转换成以大题组织的数据格式，大题下面包含小题，大题小题都对应知识点/章节，小题对应正确错误，方便处理
            //教材体系
        final List<StudentWorkAnswer> studentWorkAnswersChapter = AnswerCommonUtil.toMiddle(seList, quesMap,
                StuAnswerConstant.ResourceType.RESOURCE_TYPE_MATERIAL, topicKnowMap);
            //知识体系
        final List<StudentWorkAnswer> studentWorkAnswersKnow = AnswerCommonUtil.toMiddle(seList, quesMap,
                StuAnswerConstant.ResourceType.RESOURCE_TYPE_KNOWLEDGE, topicKnowMap);
        //发送好题状态
        sendGoodQuesStatus(exerciseSource, studentWorkAnswersKnow);
        createAnsweredWrongQues(studentWorkAnswersChapter, studentWorkAnswersKnow, topicKnowMap, chapterMap);
    }


    /**
     * 发送好题状态
     *
     * @param exerciseSource
     * @param studentWorkAnswersKnow
     */
    private void sendGoodQuesStatus(String exerciseSource, List<StudentWorkAnswer> studentWorkAnswersKnow) {
        try {
            if (StuAnswerConstant.ExerciseSource.GOOD_QUES.equals(exerciseSource)) {
                stuAnswerDataToMqService.sendExerciseData2Mq(studentWorkAnswersKnow);
            }
        } catch (Exception e) {
            logger.error("发送好题状态失败，失败的原因是:", e);
        }
    }


    /***
     * 学生做答知识点章节错题录入
     * @param studentWorkAnswersChapter
     * @param studentWorkAnswersKnow
     * @param topicKnowMap
     * @param chapterMap
     */
    private void createAnsweredWrongQues(final List<StudentWorkAnswer> studentWorkAnswersChapter, final
    List<StudentWorkAnswer> studentWorkAnswersKnow, final Map<Long, SimpleKnowVo> topicKnowMap, final Map<Long,
            SimpleKnowVo> chapterMap) {
        logger.info("studentWorkAnswersKnow:{},topicKnowMap:{},studentWorkAnswersChapter:{},chapterMap:{}", JsonUtil
                .obj2Json(studentWorkAnswersKnow), JsonUtil.obj2Json(topicKnowMap), JsonUtil.obj2Json
                (studentWorkAnswersChapter), JsonUtil.obj2Json(chapterMap));
        //新增知识点章节错题
        stuQuesKnowledgeService.recordAnsweredWrongQues(studentWorkAnswersKnow, topicKnowMap);
        stuQuesKnowledgeService.recordAnsweredWrongQues(studentWorkAnswersChapter, chapterMap);
    }


}
