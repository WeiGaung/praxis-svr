//package com.noriental.praxissvr.service.stuanswer.impl;
//
//import com.noriental.praxissvr.answer.bean.StudentExercise;
//import com.noriental.praxissvr.answer.request.StudentExerciseListIn;
//import com.noriental.praxissvr.common.StuAnswerConstant;
//import com.noriental.praxissvr.service.stuanswer.StuHoWorkService;
//import com.noriental.utils.html.JsonUtil;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.slf4j.MDC;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import java.lang.invoke.MethodHandles;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * @author chenlihua
// * @date 2015/12/31
// * @time 14:25
// */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = {"classpath:testApplicationContext.xml", "classpath:spring/applicationContext-consumer*.xml"})
//public class StuHoWorkServiceImplTest {
//
//    @SuppressWarnings("SpringJavaAutowiringInspection")
//    @Autowired
//    private StuHoWorkService stuHoWorkService;
//    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
//    @Test
//    public void createAnswer_xzt() {
//        StudentExerciseListIn seListIn = new StudentExerciseListIn();
//        List<StudentExercise> studentExerciseList = new ArrayList<>();
//        StudentExercise se1 = new StudentExercise();
//        se1.setExerciseSource(StuAnswerConstant.ExerciseSource.HO_WORK);
//        se1.setResourceId(5L);
//        se1.setQuestionId(2430753L);
////        se1.setParentQuestionId(1257796L);
//        se1.setStudentId(2111138L);
//        se1.setStuType(2);
//        se1.setSubmitAnswer("xxx");
//        se1.setResult("6");
//        se1.setQuestionType("4");
//        studentExerciseList.add(se1);
////
////        StudentExercise se2 = new StudentExercise();
////        se2.setExerciseSource(StuAnswerConstant.ExerciseSource.HO_WORK);
////        se2.setResourceId(4L);
////        se2.setQuestionId(8574L);
////        se2.setStudentId(2111118L);
////        se2.setStuType(2);
////        se2.setSubmitAnswer("A");
////        se2.setResult("2");
////        se2.setQuestionType("4");
////        studentExerciseList.add(se2);
//=======
//        se1.setExerciseSource(StuAnswerConstant.ExerciseSource.EVALUATION);
//        se1.setResourceId(resourceId);
//        se1.setQuestionId(201483L);
//        se1.setParentQuestionId(201485L);
//        se1.setStudentId(4L);
//        se1.setSubmitAnswer("2222");
//        se1.setResult("6");
//        se1.setClassId(2L);
//        se1.setYear(2016);
//        studentExerciseList.add(se1);
//>>>>>>> feature-sharding
//        String id = "112323247";
//        TraceKeyHolder.setTraceKey(id);
//        MDC.put("id",id);
//        seListIn.setStudentExerciseList(studentExerciseList);
//        logger.info(JsonUtil.obj2Json(stuHoWorkService.createAnswer(seListIn)));
//    }
//
//}