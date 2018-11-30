package com.noriental.praxissvr.wrong.service.impl;

import com.noriental.praxissvr.answer.bean.OperateType;
import com.noriental.praxissvr.answer.bean.StudentExercise;
import com.noriental.praxissvr.answer.service.StudentExerciseService;
import com.noriental.praxissvr.answer.util.RedisKeyUtil;
import com.noriental.praxissvr.answer.util.StuAnswerUtil;
import com.noriental.praxissvr.common.StuAnswerConstant;
import com.noriental.praxissvr.question.request.FindLeafQuesIdsByParentIdsRequest;
import com.noriental.praxissvr.question.response.FindLeafQuesIdsByParentIdsResponse;
import com.noriental.praxissvr.question.service.QuestionService;
import com.noriental.praxissvr.utils.PraxisUtilForOther;
import com.noriental.praxissvr.wrong.service.AnswerChalService;
import com.noriental.praxissvr.wrong.service.StuRedoTempService;
import com.noriental.praxissvr.wrong.util.WrongUtil;
import com.noriental.utils.redis.RedisUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by bluesky on 2016/7/6.
 */
@Service
@Deprecated
public class StuRedoTempServiceImpl implements StuRedoTempService {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @Autowired
    private QuestionService questionService_v2;
    @Autowired
    private StudentExerciseService stuExeService;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public void createRedoStuExes(List<StudentExercise> allSeList) {
        long startMill = System.currentTimeMillis();
        //获取做答为错的习题数据
        List<StudentExercise> wrongSeList = StuAnswerUtil.getWrongList(allSeList);
        if (CollectionUtils.isEmpty(wrongSeList)) {
            return;
        }
        List<StudentExercise> otherBatchSubList = getBatchOtherSubs(wrongSeList, allSeList);
        List<StudentExercise> chalSelist = new ArrayList<>();
        chalSelist.addAll(wrongSeList);
        chalSelist.addAll(otherBatchSubList);
        List<StudentExercise> chalSelistInit = initChalSes(chalSelist);
        stuExeService.createRecords(chalSelistInit, OperateType.NONE, null,true);

        long endMill = System.currentTimeMillis();
        if ((endMill - startMill) > 1000) {
            logger.warn("createRedoStuExes cost:" + (endMill - startMill));
        } else {
            logger.debug("createRedoStuExes cost:" + (endMill - startMill));
        }
    }

    @Override
    public void createRedoStuExesCorrect(StudentExercise se, AnswerChalService answerChalService, Long parentBatchId) {
        String key = lockCorrectRecord(StuAnswerConstant.ExerciseSource.WRONGBOOK, se.getResourceId(), se.getStudentId(), se
                .getParentQuestionId() != null ? se.getParentQuestionId() : se.getQuestionId());
        try{
            long startMill = System.currentTimeMillis();
            List<Long> subQuesIds = new ArrayList<>();
            if (StuAnswerUtil.isComplexQues(se.getParentQuestionId())) {
                FindLeafQuesIdsByParentIdsRequest req = new FindLeafQuesIdsByParentIdsRequest();
                req.setParentQuesIds(Collections.singletonList(se.getParentQuestionId()));
                FindLeafQuesIdsByParentIdsResponse response = questionService_v2.findLeafQuesIdsByParentIds(req);
                subQuesIds.addAll(response.getLeafQuesIds());
            } else {
                subQuesIds.add(se.getQuestionId());
            }
            StudentExercise se1 = new StudentExercise();
            se1.setQuestionIdList(subQuesIds);
            se1.setResourceId(se.getResourceId());
            se1.setStudentId(se.getStudentId());
            se1.setExerciseSource(StuAnswerConstant.ExerciseSource.WRONGBOOK);
            WrongUtil.setRedoSouce(se1, se);
            se1.setClassId(se.getClassId());
            //查询的是错题挑战答题记录
            List<StudentExercise> chalSes = stuExeService.getDbRecords(se1);

            StudentExercise se2 = new StudentExercise();
            se2.setQuestionIdList(subQuesIds);
            se2.setResourceId(parentBatchId);
            se2.setStudentId(se.getStudentId());
            WrongUtil.setExerciseSource(se2, se);
            se2.setClassId(se.getClassId());
            //获取正常的做答场景学生做答记录
            List<StudentExercise> answerSes = stuExeService.getDbRecords(se2);
            List<StudentExercise> notExistChalSes = getNotExistChalSes(chalSes, answerSes);
            if (CollectionUtils.isNotEmpty(notExistChalSes)) {
                List<StudentExercise> notExistChalSesInit = initChalSes(notExistChalSes);
                WrongUtil.setOhterSeSonBatchId(se, notExistChalSesInit);
                stuExeService.createRecords(notExistChalSesInit, OperateType.NONE, null,true);
            }
            long endMill = System.currentTimeMillis();
            if ((endMill - startMill) > 1000) {
                logger.warn("createCorrectRedoStuExes cost:" + (endMill - startMill));
            } else {
                logger.debug("createCorrectRedoStuExes cost:" + (endMill - startMill));
            }
        }finally {
            redisUtil.unLock(key);
        }

    }
    private String lockCorrectRecord(String exerciseSource, Long resourceId, Long studentId, Long questionId) {
        final String key = RedisKeyUtil.makeKey(RedisKeyUtil.CORRECT_MORE_LOCK_PREFIX, exerciseSource, resourceId +
                "", studentId + "", questionId + "");
        redisUtil.addLock(key, RedisKeyUtil.CORRECT_MORE_LOCK_EXPIRE, 20);
        return key;
    }

    private List<StudentExercise> initChalSes(List<StudentExercise> answerSeList) {
        List<StudentExercise> chalSeList = new ArrayList<>();
        for (StudentExercise se : answerSeList) {
            StudentExercise se1 = new StudentExercise();
            se1.setQuestionId(se.getQuestionId());
            se1.setParentQuestionId(se.getParentQuestionId());
            se1.setStudentId(se.getStudentId());
            se1.setStructId(se.getStructId());
            se1.setResourceId(se.getResourceId());
            se1.setClassId(se.getClassId());
            se1.setExerciseSource(StuAnswerConstant.ExerciseSource.WRONGBOOK);
            WrongUtil.setRedoSouce(se1, se);
            se1.setQuestionType(se.getQuestionType());
            se1.setCreateTime(new Date());
            se1.setSubmitTime(new Date());
            String redoStatus = PraxisUtilForOther.getQuesResultWrongBook(se.getStructId(), se.getResult());
            se1.setRedoStatus(redoStatus);

            chalSeList.add(se1);
        }
        return chalSeList;
    }

    private List<StudentExercise> getNotExistChalSes(List<StudentExercise> chalSes, List<StudentExercise> answerSes) {

        List<StudentExercise> notExistChalSes = new ArrayList<>();

        List<Long> chalQuesIds = new ArrayList<>();
        for (StudentExercise chal : chalSes) {
            chalQuesIds.add(chal.getQuestionId());
        }
        for (StudentExercise an : answerSes) {
            if (!chalQuesIds.contains(an.getQuestionId())) {
                notExistChalSes.add(an);
            }
        }
        return notExistChalSes;
    }

    private List<StudentExercise> getBatchOtherSubs(List<StudentExercise> wrongSeList, List<StudentExercise>
            allSeList) {
        List<Long> quesIds = new ArrayList<>();
        List<Long> parentQuesIds = new ArrayList<>();
        for (StudentExercise se : wrongSeList) {
            quesIds.add(se.getQuestionId());
            if (se.getParentQuestionId() != null) {
                parentQuesIds.remove(se.getParentQuestionId());
                parentQuesIds.add(se.getParentQuestionId());
            }
        }
        List<StudentExercise> batchOtherSubs = new ArrayList<>();
        for (StudentExercise se : allSeList) {
            if (parentQuesIds.contains(se.getParentQuestionId()) && !quesIds.contains(se.getQuestionId())) {
                batchOtherSubs.add(se);
            }
        }
        return batchOtherSubs;
    }
}
