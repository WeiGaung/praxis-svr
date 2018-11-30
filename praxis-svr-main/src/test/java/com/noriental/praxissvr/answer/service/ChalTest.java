package com.noriental.praxissvr.answer.service;

import com.noriental.BaseTest;
import com.noriental.praxissvr.answer.request.*;
import com.noriental.praxissvr.common.StuAnswerConstant;
import com.noriental.praxissvr.answer.service.AnswerCommonService;
import com.noriental.utils.json.JsonUtil;
import com.noriental.validate.exception.BizLayerException;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

/**
 * 错题消灭测试用例
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = {"classpath:testApplicationContext.xml"})
public class ChalTest extends BaseTest{

    public static  final String result6 = "6";
    public static  final String result2 = "2";
    public static  final String corT = StuAnswerConstant.CorrectorRole.TEACHER;
    public static  final String corS = StuAnswerConstant.CorrectorRole.STUDENT;

    public static  final Long resouceId = 2260L;
    public static  final String exerciseSouce = StuAnswerConstant.ExerciseSource.HO_WORK;
    public static  final Long studentId = 4285662L;

    public static  final String redoSouce = StuAnswerConstant.ExerciseSource.HO_WORK;
//    public static  final Long resouceId = 2260L;
//    public static  final String exerciseSouce = StuAnswerConstant.ExerciseSource.WRONGBOOK;
//    public static  final Long studentId = 4285662L;


    public static  final Long questionId1 = 810184L;



    @Autowired
    AnswerCommonService answerCommonService;
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @Test
    public void updateSubmitAnswer() {
        UpdateSubmitAnswerRequest request = new UpdateSubmitAnswerRequest();
        request.setExerciseSource(exerciseSouce);
        request.setCorrectorId(1L);
        request.setCorrectorRole(StuAnswerConstant.CorrectorRole.STUDENT);
        request.setResourceId(resouceId);
        request.setStudentId(studentId);
        List<UpdateSubmitAnswer> updateSubmitAnswerList  = new ArrayList<>();
        request.setUpdateSubmitAnswerList(updateSubmitAnswerList);
        UpdateSubmitAnswer u1 = new UpdateSubmitAnswer();
        u1.setQuestionId(questionId1);
        u1.setResult(result6);
        u1.setSubmitAnswer("A");
        updateSubmitAnswerList.add(u1);
        try{
        logger.info(JsonUtil.obj2Json(answerCommonService.updateSubmitAnswer(request)));
    }catch (Exception e){
        if(e instanceof BizLayerException){
            logger.error(JsonUtil.obj2Json(e));
        }else {
            logger.error("eee",e);
        }
    }
    }
    @Test
    public void updateCorrect() {
        UpdateCorrectRequest studentExercise = new UpdateCorrectRequest();
        studentExercise.setCorrectorId(1L);
        studentExercise.setCorrectorRole(corT);

        studentExercise.setExerciseSource(exerciseSouce);
        studentExercise.setResourceId(resouceId);
        studentExercise.setStudentId(studentId);
        studentExercise.setQuestionId(questionId1);
        studentExercise.setResult(result2);
        try{
        logger.info(JsonUtil.obj2Json(answerCommonService.updateCorrect(studentExercise)));
    }catch (Exception e){
        if(e instanceof BizLayerException){
            logger.error(JsonUtil.obj2Json(e));
        }else {
            logger.error("eee",e);
        }
    }
    }

    @Test
    public void updateSubmitAnswerChal() {
        UpdateSubmitAnswerRequest request = new UpdateSubmitAnswerRequest();
        request.setExerciseSource(exerciseSouce);
        request.setCorrectorId(1L);
        request.setCorrectorRole(StuAnswerConstant.CorrectorRole.STUDENT);
        request.setResourceId(resouceId);
        request.setStudentId(studentId);
        request.setRedoSource(redoSouce);

        List<UpdateSubmitAnswer> updateSubmitAnswerList  = new ArrayList<>();
        request.setUpdateSubmitAnswerList(updateSubmitAnswerList);

        UpdateSubmitAnswer u1 = new UpdateSubmitAnswer();
        u1.setQuestionId(questionId1);
        u1.setResult(result2);
        u1.setSubmitAnswer("A");
        updateSubmitAnswerList.add(u1);
        try{
        logger.info(JsonUtil.obj2Json(answerCommonService.updateSubmitAnswer(request)));
    }catch (Exception e){
        if(e instanceof BizLayerException){
            logger.error(JsonUtil.obj2Json(e));
        }else {
            logger.error("eee",e);
        }
    }
    }

    @Test
    public void updateCorrectChal() {
        UpdateCorrectRequest studentExercise = new UpdateCorrectRequest();
        studentExercise.setCorrectorId(1L);
        studentExercise.setCorrectorRole(corT);

        studentExercise.setExerciseSource(exerciseSouce);
        studentExercise.setRedoSource(redoSouce);
        studentExercise.setResourceId(resouceId);
        studentExercise.setStudentId(studentId);
        studentExercise.setQuestionId(questionId1);
        studentExercise.setResult("2");
        try{
        logger.info(JsonUtil.obj2Json(answerCommonService.updateCorrect(studentExercise)));
        }catch (Exception e){
            if(e instanceof BizLayerException){
                logger.error(JsonUtil.obj2Json(e));
            }else {
                logger.error("eee",e);
            }
        }
    }

        @Test
    public void findStuAnswsOnBatch() {
        FindStuAnswsOnBatchRequest in = new FindStuAnswsOnBatchRequest();
        in.setExerciseSource(exerciseSouce);
        in.setResourceId(resouceId);
        in.setStudentId(studentId);
        logger.info(JsonUtil.obj2Json(answerCommonService.findStuAnswsOnBatch(in)));
    }



}
