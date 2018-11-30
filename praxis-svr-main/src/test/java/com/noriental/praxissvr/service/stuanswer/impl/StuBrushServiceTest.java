package com.noriental.praxissvr.service.stuanswer.impl;

import com.noriental.BaseTest;
import com.noriental.BaseTestClient;
import com.noriental.log.TraceKeyHolder;
import com.noriental.praxissvr.LoaderRunnerUnit;
import com.noriental.praxissvr.brush.request.CreateEnchanceRequest;
import com.noriental.praxissvr.brush.request.CreateSelfExerciseRequest;
import com.noriental.praxissvr.brush.request.StudentWorkIn;
import com.noriental.praxissvr.brush.response.StudentWorkOut;
import com.noriental.praxissvr.common.StuAnswerConstant;
import com.noriental.praxissvr.brush.service.StuBrushService;
import com.noriental.utils.json.JsonUtil;
import com.noriental.validate.exception.BizLayerException;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.invoke.MethodHandles;
import java.util.Random;


//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = { "classpath:testApplicationContext.xml" })
//public class StuBrushServiceTest extends BaseTest{
public class StuBrushServiceTest extends BaseTest {

    Logger loger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private Long studentId=82951058013L;
    private Long resourceId = 650L;
    private Long questionIdjdt1 = 1392068L;
    private Long questionIdjdt2 = 1392067L;
    private Long questionIdjdt3 = 1304645L;
    private String postil="[\"a.jpg\"]";
    private String answer="[\"a.jpg\"]";
    private String exerciseSource =StuAnswerConstant.ExerciseSource.LESSON;

    @Autowired
    StuBrushService stuBrushService;
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    @Test
    public  void getWork() {
        StudentWorkIn req = new StudentWorkIn();
        req.setId(1615162L);
        req.setStudentId(81951071748L);
        StudentWorkOut work = stuBrushService.getWork(req);
        logger.info(JsonUtil.obj2Json(work));

//        Runnable runnable = new Runnable() {
//        @Override
//        public void run() {
//            TraceKeyHolder.setTraceKey("xiaotest"+ new Random().nextLong());
//            StudentWorkIn req = JsonUtil.readValue("{\"studentWork\":{\"id\":1519903,\"workName\":null,\"resourceType\":null,\"workLevel\":null,\"studentId\":1,\"subjectId\":null,\"moduleId\":null,\"unitId\":null,\"topicId\":null,\"workStatus\":null,\"createTime\":null,\"lastUpdateTime\":null,\"year\":null,\"type\":null,\"studentExercises\":null,\"parentQuesNumber\":null,\"quesNumber\":null,\"rightNumber\":null,\"baseurl\":null,\"moduleName\":null,\"levelId\":0}}",StudentWorkIn.class);
//            StudentWorkOut work = stuBrushService.getWork(req);
//        }
//    };
//        LoaderRunnerUnit.run(runnable,200);

    }

    @Test
    @Ignore
    public  void createEnchance() {
//        CreateEnchanceRequest in = new CreateEnchanceRequest();
//        in.setQuestionId(3530L);
//        in.setStudentId(1L);
//        logger.info(JsonUtil.obj2Json(stuBrushService.createEnchance(in)));

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                TraceKeyHolder.setTraceKey("xiaotest"+ new Random().nextLong());
                CreateEnchanceRequest in = new CreateEnchanceRequest();
                in.setQuestionId(3530L);
                in.setStudentId(1L);
                logger.info(JsonUtil.obj2Json(stuBrushService.createEnchance(in)));
            }
        };
        LoaderRunnerUnit.run(runnable,20,1);
    }


}