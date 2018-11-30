package com.noriental.praxissvr.answer.service.impl;

import com.noriental.adminsvr.service.knowledge.ModuleService;
import com.noriental.adminsvr.service.knowledge.TopicService;
import com.noriental.adminsvr.service.knowledge.UnitService;
import com.noriental.adminsvr.service.teaching.ChapterService;
import com.noriental.praxissvr.answer.bean.CorrectAnswerEntity;
import com.noriental.praxissvr.answer.bean.CorrectAnswerEvent;
import com.noriental.praxissvr.answer.bean.OperateType;
import com.noriental.praxissvr.answer.bean.StudentExercise;
import com.noriental.praxissvr.answer.response.CorrectCollectionInfoRes;
import com.noriental.praxissvr.answer.service.AnswerCommonService;
import com.noriental.praxissvr.answer.service.StuAnswerDataToMqService;
import com.noriental.praxissvr.answer.service.StuAnswerService;
import com.noriental.praxissvr.answer.service.StudentExerciseService;
import com.noriental.praxissvr.answer.util.AnswerCommonUtil;
import com.noriental.praxissvr.answer.util.PraxisSsdbUtil;
import com.noriental.praxissvr.answer.util.RedisKeyUtil;
import com.noriental.praxissvr.answer.util.StuAnswerUtil;
import com.noriental.praxissvr.brush.bean.StudentWorkAnswer;
import com.noriental.praxissvr.common.StuAnswerConstant;
import com.noriental.praxissvr.question.bean.Question;
import com.noriental.praxissvr.questionSearch.service.QuestionSearchService;
import com.noriental.praxissvr.wrong.bean.SimpleKnowVo;
import com.noriental.praxissvr.wrong.service.StuQuesKnowledgeService;
import com.noriental.utils.json.JsonUtil;
import com.noriental.utils.redis.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author kate
 * @create 2017-12-20 15:44
 * @desc 学生批改、老师批改监听
 **/
@Component
public class CorrectAnswerListener implements ApplicationListener<CorrectAnswerEvent> {
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
    private StudentExerciseService studentExerciseService;
    @Resource
    private StuAnswerDataToMqService stuAnswerDataToMqService;
    @Resource
    private AnswerCommonService answerCommonService;
    @Resource
    private RedisUtil redisUtil;
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Async
    @Override
    public void onApplicationEvent(CorrectAnswerEvent event) {
        Long startTime = System.currentTimeMillis();
        CorrectAnswerEntity entity = (CorrectAnswerEntity) event.getSource();
        String traceKey = entity.getTraceKey();
        AnswerCommonUtil.initLogRequestId(traceKey);
        logger.info("CorrectAnswerListener get data:" + JsonUtil.obj2Json(entity));
        if (entity == null) {
            logger.error("CorrectAnswerListener get data is null");
            return;
        }
        int busType = 0;
        if (OperateType.INTELL.equals(entity.getOperateType())) {
            busType = 3;
        } else if (OperateType.CORRECT.equals(entity.getOperateType())) {
            busType = 2;
        }else if(OperateType.INTELLCORRECT.equals(entity.getOperateType())){
            busType = 8;
        }
        logger.info("-------busType---------"+busType);
        excuteCorrectNotify(entity.getNewRecord(), entity.getExitRecord(), busType);
        excuteCorrectKnowledge(entity.getNewRecord(), entity.getExitRecord(), busType);
        Long endTime = System.currentTimeMillis();
        logger.info("CorrectAnswerListener cost time:{} ms", endTime - startTime);
    }

    /***
     * 通知统计服务、错题重做错题的录入
     * @param se
     * @param existedRecord
     * @param busType
     */

    private void excuteCorrectNotify(StudentExercise se, StudentExercise existedRecord, int busType) {
        StudentExercise exercise = new StudentExercise();
        BeanUtils.copyProperties(se, exercise);
        notifyTrailForAnswer(se, busType, existedRecord);
    }


    private void notifyTrailForAnswer(StudentExercise se, int operaType, StudentExercise exitedRecord) {
        //课单独通知，强化不用上通知
        if (operaType == PraxisSsdbUtil.CORRECTTYPE || operaType == PraxisSsdbUtil.INTELIGENTYPE || operaType == PraxisSsdbUtil.INTELIGENCORRECTTYPE) {
            try {
                //批改队列数据偶尔出现先于提交作答发出，强制延迟
                //Thread.sleep(500L);
                stuAnswerService.notifyTrail(se, operaType, exitedRecord);
            } catch (Exception e) {
                logger.error("操作类型operaType={}向统计服务发送消息失败，失败的原因是:", operaType, e);
            }
        }

    }

    /***
     *
     * @param se
     * @param existedRecord
     */
    private void excuteCorrectKnowledge(StudentExercise se, StudentExercise existedRecord, int busType) {
        String key = lockCorrectRecord(se.getExerciseSource(), se.getResourceId(), se.getStudentId(), se
                .getParentQuestionId() != null ? se.getParentQuestionId() : se.getQuestionId());
        try {
            List<Long> quesIdList = StuAnswerUtil.getQuesIdList(Collections.singletonList(se));//所有题目id：包含小题
            List<Question> quesListByIds = AnswerCommonUtil.getQuesListByIds(quesIdList, questionSearchService);
            final Map<Long, Question> quesMap = AnswerCommonUtil.getQuesMapByQuesList(quesListByIds);
            //根据习题列表获取习题知识点map集合
            final Map<Long, SimpleKnowVo> topicKnowMap = AnswerCommonUtil.getTopicMapByQuesList(quesListByIds,
                    topicService, unitService, moduleService);
            //根据习题列表获取习题章节map集合
            final Map<Long, SimpleKnowVo> chapterMap = AnswerCommonUtil.getChapterMapByQuesList(quesListByIds,
                    chapterService);
            //章节中间结果
            final StudentWorkAnswer studentWorkAnswerChapter = AnswerCommonUtil.toMiddleC(se, quesMap,
                    StuAnswerConstant.ResourceType.RESOURCE_TYPE_MATERIAL, chapterMap, studentExerciseService);
            //知识点中间结果
            final StudentWorkAnswer studentWorkAnswerKnow = AnswerCommonUtil.toMiddleC(se, quesMap, StuAnswerConstant
                    .ResourceType.RESOURCE_TYPE_KNOWLEDGE, topicKnowMap, studentExerciseService);
            //发送好题状态
            sendGoodQuesStatus(se.getExerciseSource(), Arrays.asList(studentWorkAnswerKnow));
            //correctTeacherDone(se);
            updateAnsweredWrongQues(se, existedRecord, studentWorkAnswerChapter, studentWorkAnswerKnow, topicKnowMap,
                    chapterMap, busType);
        } finally {
            unLockCorrectRecord(key);
        }
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
     * 老师批改更新错题
     * @param se
     * @param existedRecord
     * @param studentWorkAnswerChapter
     * @param studentWorkAnswerKnow
     * @param topicKnowMap
     * @param chapterMap
     */
    private void updateAnsweredWrongQues(final StudentExercise se, final StudentExercise existedRecord, final
    StudentWorkAnswer studentWorkAnswerChapter, final StudentWorkAnswer studentWorkAnswerKnow, final Map<Long,
            SimpleKnowVo> topicKnowMap, final Map<Long, SimpleKnowVo> chapterMap, int busType) {
        //重复批改的场景变为全场景  一键批改不存在重复批改的问题 StuAnswerUtil.isExerciseSource(se)&&
        if (null != existedRecord && null != existedRecord.getResult()) {
            CorrectCollectionInfoRes correctCollectionInfoRes = answerCommonService.correctCollectionInfo(se,
                    existedRecord);
            //判断是否为老师重复批改，并且由错批改为对
            if (correctCollectionInfoRes.isRepeatCorrect() && correctCollectionInfoRes.getCoorectStatus() == 1) {
                stuQuesKnowledgeService.updataStuQueKnowledgeStatu(se, 0);
            }
        }
        stuQuesKnowledgeService.recordWrongQues(studentWorkAnswerKnow, topicKnowMap);
        stuQuesKnowledgeService.recordWrongQues(studentWorkAnswerChapter, chapterMap);
    }

    /**
     * 加锁原因：批改多空填空题为错，并发操作时：导致生成多条错题记录和错题消灭
     *
     * @param exerciseSource
     * @param resourceId
     * @param studentId
     * @param questionId
     * @return
     */
    private String lockCorrectRecord(String exerciseSource, Long resourceId, Long studentId, Long questionId) {
        final String key = RedisKeyUtil.makeKey(RedisKeyUtil.CORRECT_MORE_LOCK_PREFIX, exerciseSource, resourceId +
                "", studentId + "", questionId + "");
        redisUtil.addLock(key, RedisKeyUtil.CORRECT_MORE_LOCK_EXPIRE, 20);
        return key;
    }

    private void unLockCorrectRecord(String key) {
        redisUtil.unLock(key);
    }


}
