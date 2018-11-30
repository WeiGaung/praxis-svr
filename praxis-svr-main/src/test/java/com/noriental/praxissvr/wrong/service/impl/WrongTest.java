//package com.noriental.praxissvr.wrong.service.impl;
//
//import com.alibaba.fastjson.JSONObject;
//import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
//import com.noriental.global.dict.AppType;
//import com.noriental.log.TraceKeyHolder;
//import com.noriental.praxissvr.answer.request.StudentExerciseIn;
//import com.noriental.praxissvr.answer.service.AnswerCommonService;
//import com.noriental.praxissvr.answer.service.StuAnswerService;
//import com.noriental.praxissvr.common.StuAnswerConstant;
//import com.noriental.praxissvr.wrong.service.StuRedoService;
//import com.noriental.usersvr.bean.user.domain.User;
//import com.noriental.utils.html.JsonUtil;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.slf4j.MDC;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.test.context.transaction.TransactionConfiguration;
//
//import java.lang.invoke.MethodHandles;
//import java.util.*;
//
//import static org.junit.Assert.assertNotNull;
//
///**
// * @author chenlihua
// * @date 2016/1/6
// * @time 11:44
// */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = {"classpath:testApplicationContext.xml"})
//@TransactionConfiguration
//public class WrongTest {
//
//    @SuppressWarnings("SpringJavaAutowiringInspection")
//    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
//    @Autowired
//    StuAnswerService stuAnswerService;
//    @Autowired
//    AnswerCommonService answerCommonService;
//    @Autowired
//    StuRedoService stuRedoService;
////    public static  final Long resouceId = 475124L;
////    public static  final Long courseId = 475124L;
////    public static  final Long classId = 475124L;
////    public static  final String exerciseSouce = StuAnswerConstant.ExerciseSource.EVALUATION;
////    public static  final Long studentId = 123456+Long.valueOf(String.valueOf(Math.random()*1000));
//
//    //上课
////    public static  final Long resouceId = 13L;
////    public static  final Long courseId = 7347112L;
////    public static  final Long classId = 1061L;
////    public static  final String result6 = "6";
////    public static  final Long questionId1 = 2430319L;
////    public static  final String type1 = "3";
////    public static  final Long questionId2 = 2430320L;
////    public static  final String result2 = "2";
////    public static  final String type2 = "3";
////    public static  final String result1 = "1";
////    public static  final Long parentQuestionId = 2430318L;
////    public static  final String exerciseSouce = StuAnswerConstant.ExerciseSource.LESSON;
////    public static  final Long studentId = 17L;
////    public static  final String correctRoleTeacher = StuAnswerConstant.CorrectorRole.TEACHER;
////    public static  final String correctRoleStu = StuAnswerConstant.CorrectorRole.STUDENT;
//
//
//    //填空�?
////    public static  final Long resouceId = 13L;
////    public static  final Long courseId = null;
////    public static  final Long classId = null;
////    public static  final String result6 = "[{\"index\":1,\"result\":6},{\"index\":2,\"result\":6}]";
////    public static  final Long questionId1 = 1257792L;
////    public static  final String type1 = "2";
////    public static  final Long questionId2 = 1257793L;
////    public static  final String result26 ="[{\"index\":1,\"result\":2},{\"index\":2,\"result\":6}]";
////    public static  final String result22 = "[{\"index\":1,\"result\":2},{\"index\":2,\"result\":2}]";
////    public static  final String type2 = "2";
////    public static  final String result1 = "[{\"index\":1,\"result\":1},{\"index\":2,\"result\":1}]";
////    public static  final Long parentQuestionId = 1257790L;
////    public static  final String exerciseSouce = StuAnswerConstant.ExerciseSource.HO_WORK;
////    public static  final Long studentId = 18L;
////    public static  final String correctRoleTeacher = StuAnswerConstant.CorrectorRole.TEACHER;
////    public static  final String correctRoleStu = StuAnswerConstant.CorrectorRole.STUDENT;
////    public static  final String index1result2 = "[{\"index\":1,\"result\":2}]";
////    public static  final String index2result2 = "[{\"index\":2,\"result\":2}]";
//
////    //单题
////    public static  final Long resouceId = 24L;
////    public static  final Long courseId = null;
////    public static  final Long classId = null;
////    public static  final String result6 = "6";
////    public static  final Long questionId1 = 158197L;
////    public static  final Long questionId2 = 578L;
////    public static  final Long parentQuestionId = null;
////    public static  final String type1 = "1";
////    public static  final String type2 = "1";
////    public static  final String result1 = "1";
////    public static  final String result2 = "2";
////    public static  final String exerciseSouce = StuAnswerConstant.ExerciseSource.HO_WORK;
////    public static  final Long studentId = 25L;
////    public static  final String correctRoleTeacher = StuAnswerConstant.CorrectorRole.TEACHER;
////    public static  final String correctRoleStu = StuAnswerConstant.CorrectorRole.STUDENT;
//
//
//    //已会�?
//    public static  final Long resouceId = 5L;
//    public static  final Long courseId = 7347112L;
//    public static  final Long classIdull = null;
//    public static  final Long courseIdull = null;
//    public static  final Long classId = 1061L;
//    public static  final String result6 = "6";
//    public static  final Long questionId1 = 2430319L;
//    public static  final Long questionId2 = 1419890L;
//    public static  final Long parentQuestionId = null;
//    public static  final Long parentQuestionId2 = null;
//    public static  final Long parentQuestionId1 = 24303181L;
//    public static  final String type1 = "1";
//    public static  final String type2 = "1";
//    public static  final String result1 = "1";
//    public static  final String result2 = "2";
//    public static  final String exerciseSouce = StuAnswerConstant.ExerciseSource.LESSON;
//    public static  final String exerciseSouce1 = StuAnswerConstant.ExerciseSource.LESSON;
//    public static  final String exerciseSouce6 = StuAnswerConstant.ExerciseSource.HO_WORK;
//    public static  final Long studentId = 81148745L;
//    public static  final String correctRoleTeacher = StuAnswerConstant.CorrectorRole.TEACHER;
//    public static  final String correctRoleStu = StuAnswerConstant.CorrectorRole.STUDENT;
//
//    @Test
//    public void trailfindChal() throws Exception {
//        StudentExerciseIn seIn = new StudentExerciseIn();
//        StudentExercise studentExercise =  new StudentExercise();
//        studentExercise.setResourceId(resouceId);
//        studentExercise.setStudentId(studentId);
//        studentExercise.setExerciseSource(3+exerciseSouce);
//        studentExercise.setCourseId(courseId);
//        studentExercise.setClassId(classId);
//        seIn.setStudentExercise(studentExercise);
//        logger.info(JsonUtil.obj2Json(stuAnswerService.findStuAnswers(seIn)));
//    }
//
//    @Test
//    public void classAnswer() throws Exception {
//        List<StudentExercise> answers = new ArrayList<>();
//        StudentExercise se = new StudentExercise();
//        se.setClassId(classId);
//        se.setCourseId(courseId);
//        se.setQuestionId(questionId1);
//		se.setParentQuestionId(parentQuestionId);
//        se.setStudentId(studentId);
//        se.setStuType(2);
//        se.setResult(result6);
//		se.setSubmitAnswer("C");
//        se.setResourceId(resouceId);
//        se.setQuestionType(type1);
//        answers.add(se);
////        StudentExercise se1 = new StudentExercise();
////        se1.setClassId(classId);
////        se1.setCourseId(courseId);
////        se1.setQuestionId(questionId2);
////        se1.setParentQuestionId(parentQuestionId);
////        se1.setStudentId(studentId);
////        se1.setQuestionType(type2);
////        se1.setSubmitAnswer("1");
////        se1.setStuType(2);
////        se1.setResult(result6);
////        se1.setResourceId(resouceId);
////        answers.add(se1);
//        StudentExerciseListIn in = new StudentExerciseListIn();
//        in.setStudentExerciseList(answers);
//        System.out.println("-----------" + classroomService.createSubmitAnswer_V2(in));
//    }
//
//    @Test
//    public void  howorkAnswer(){
//        StudentExerciseListIn seListIn = new StudentExerciseListIn();
//        List<StudentExercise> studentExerciseList = new ArrayList<>();
//        StudentExercise se1 = new StudentExercise();
//        se1.setExerciseSource(exerciseSouce);
//        se1.setResourceId(resouceId);
//        se1.setQuestionId(questionId1);
//        se1.setParentQuestionId(parentQuestionId);
//        se1.setStudentId(studentId);
//        se1.setStuType(2);
//        se1.setSubmitAnswer("xxx");
//        se1.setResult(result2);
//        se1.setQuestionType(type1);
//        studentExerciseList.add(se1);
////        StudentExercise se2 = new StudentExercise();
////        se2.setExerciseSource(exerciseSouce);
////        se2.setResourceId(resouceId);
////        se2.setQuestionId(questionId2);
////        se2.setParentQuestionId(parentQuestionId);
////        se2.setStudentId(studentId);
////        se2.setStuType(2);
////        se2.setSubmitAnswer("A");
////        se2.setResult(result2);
////        se2.setQuestionType(type2);
////        studentExerciseList.add(se2);
//        seListIn.setStudentExerciseList(studentExerciseList);
//        logger.info(JsonUtil.obj2Json(stuHoWorkService.createAnswer(seListIn)));
//    }
//
//    @Test
//    public  void correct() {
//        StudentExerciseIn seIn = new StudentExerciseIn();
//        StudentExercise studentExercise =  new StudentExercise();
//        studentExercise.setExerciseSource(exerciseSouce);
//        studentExercise.setStudentId(studentId);
//        studentExercise.setQuestionId(questionId1);
//        studentExercise.setParentQuestionId(parentQuestionId);
//        studentExercise.setResult(result2);
//        studentExercise.setResourceId(resouceId);
//        studentExercise.setCourseId(courseId);
//        studentExercise.setClassId(classId);
//        studentExercise.setCorrectorId(1L);
//        studentExercise.setCorrectorRole(correctRoleTeacher);
//
//        seIn.setStudentExercise(studentExercise);
//        logger.info(JsonUtil.obj2Json(answerCommonService.updateCorrect(seIn)));
//    }
//
//    @Test
//    public void chalAnswer() {
//        StudentExerciseListIn seListIn = new StudentExerciseListIn();
//        List<StudentExercise> studentExerciseList = new ArrayList<>();
//        StudentExercise se = new StudentExercise();
//        se.setRedoSource(exerciseSouce);
//        se.setResourceId(resouceId);
//        se.setQuestionId(questionId1);
//        se.setClassId(classId);
//        se.setCourseId(courseId);
//        se.setParentQuestionId(parentQuestionId);
//        se.setStudentId(studentId);
//        se.setStuType(2);
//        se.setSubmitAnswer("sss");
//        se.setResult(result2);
//        se.setQuestionType(type1);
//        studentExerciseList.add(se);
////        StudentExercise se1 = new StudentExercise();
////        se1.setRedoSource(exerciseSouce);
////        se1.setResourceId(resouceId);
////        se1.setClassId(classId);
////        se1.setCourseId(courseId);
////        se1.setQuestionId(questionId1);
////        se1.setParentQuestionId(parentQuestionId);
////        se1.setStudentId(studentId);
////        se1.setSubmitAnswer("dsfs");
////        se1.setResult(result6);
////        se1.setQuestionType(type1);
////        studentExerciseList.add(se1);
//        seListIn.setStudentExerciseList(studentExerciseList);
//        stuRedoService.updateSubmitAnswer(seListIn);
//    }
//    @Test
//    public void chalCorrect() {
//        StudentExerciseIn seIn = new StudentExerciseIn();
//        StudentExercise studentExercise =  new StudentExercise();
//        studentExercise.setRedoSource(exerciseSouce);
//        studentExercise.setResourceId(resouceId);
//        studentExercise.setClassId(classId);
//        studentExercise.setCourseId(courseId);
//        studentExercise.setQuestionId(questionId2);
//        studentExercise.setParentQuestionId(parentQuestionId);
//        studentExercise.setStudentId(studentId);
//        studentExercise.setStuType(2);
//        studentExercise.setCorrectorRole(correctRoleTeacher);
//        studentExercise.setResult(result2);
//        seIn.setStudentExercise(studentExercise);
//        logger.info(JsonUtil.obj2Json(stuRedoService.updateCorrect(seIn)));
//    }
//    @Test
//    public void findChal() {
//        StudentExerciseIn in = new StudentExerciseIn();
//	    	StudentExercise studentExercise = new StudentExercise();
//	    	studentExercise.setRedoSource(exerciseSouce);
//	    	studentExercise.setResourceId(resouceId);
//	    	studentExercise.setStudentId(studentId);
//            studentExercise.setClassId(classId);
//            studentExercise.setCourseId(courseId);
//			in.setStudentExercise(studentExercise);
//        logger.info(JsonUtil.obj2Json(stuRedoService.findStuChalAnswers(in)));
//    }
//    @Test
//    public void updateRedoStatus() {
//        StudentExerciseIn seIn = new StudentExerciseIn();
//        StudentExercise studentExercise =  new StudentExercise();
//        studentExercise.setRedoStatus("9");
//        studentExercise.setRedoSource(exerciseSouce6);
//        studentExercise.setStudentId(studentId);
//        studentExercise.setQuestionId(questionId2);
//        studentExercise.setParentQuestionId(parentQuestionId2);
//        studentExercise.setResourceId(resouceId);
//        studentExercise.setCourseId(courseIdull);
//        studentExercise.setClassId(classIdull);
//        seIn.setStudentExercise(studentExercise);
//        logger.info(JsonUtil.obj2Json(stuRedoService.updateRedoStatus(seIn)));
//
//
//    }
//
//}