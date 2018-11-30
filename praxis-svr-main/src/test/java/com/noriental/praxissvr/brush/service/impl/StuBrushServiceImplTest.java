package com.noriental.praxissvr.brush.service.impl;

import com.noriental.BaseTest;
import com.noriental.praxissvr.brush.request.CreateEnchanceRequest;
import com.noriental.praxissvr.brush.request.CreateStudentWorkRequest;
import com.noriental.praxissvr.brush.request.StudentWorkIn;
import com.noriental.praxissvr.brush.request.StudentWorkPageInput;
import com.noriental.praxissvr.brush.service.StuBrushService;
import com.noriental.praxissvr.utils.LoggerHolder;
import com.noriental.utils.json.JsonUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.invoke.MethodHandles;

/**
 * Created by shengxian on 2016/12/19.
 */
public class StuBrushServiceImplTest extends BaseTest{
    @Test
    public void findWorksPage() throws Exception {
        StudentWorkPageInput req  = new StudentWorkPageInput();
        req.setSubjectId(1L);
        req.setCurrentpage(1);
        req.setPagesize(10);
        req.setStudentId(8130014L);
        req.setLevelId(242L);
        req.setWorkLevel(1);
        req.setWorkStatusEnum(StudentWorkPageInput.WorkStatusEnum.COMPLETE);
        logger.info(JsonUtil.obj2Json(stuBrushService.findWorksPage(req)));
    }

    @Autowired
    StuBrushService stuBrushService;
    Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @Test
    public void setLogger() throws Exception {
        stuBrushService.setLogger("4",1600176L);

        logger.info(LoggerHolder.getBrushLogger("se46666"));
    }



    @Test
    public void createStudentWork() throws Exception {
       /* CreateStudentWorkRequest req = new CreateStudentWorkRequest();
        req.setStudentId(1L);
        req.setType(9);
        stuBrushService.createStudentWork(req);*/
        String s="{\"reqId\":null,\"studentId\":81951115740,\"type\":10}";
      /*  CreateRecordsRequest createRecordsRequest;
        createRecordsRequest=JsonUtil.readValue(s,CreateRecordsRequest.class);
        stuBrushService.createBrushRecords(createRecordsRequest);*/

        //CreateStudentWorkRequest
        CreateStudentWorkRequest req = new CreateStudentWorkRequest();
        req=JsonUtil.readValue(s,CreateStudentWorkRequest.class);
        stuBrushService.createStudentWork(req);
    }


    @Test
    public void getWork() throws Exception {
        StudentWorkIn workIn;
        String s="{\"reqId\":null,\"studentWork\":{\"id\":19728359,\"workName\":null,\"resourceType\":null," +
                "\"workLevel\":null,\"studentId\":81951085080,\"subjectId\":null,\"moduleId\":null,\"unitId\":null," +
                "\"topicId\":null,\"workStatus\":null,\"createTime\":null,\"lastUpdateTime\":null,\"year\":null," +
                "\"type\":null,\"studentExercises\":null,\"parentQuesNumber\":null,\"quesNumber\":null," +
                "\"rightNumber\":null,\"baseurl\":null,\"moduleName\":null,\"levelId\":0}}";
        String s1="{\"reqId\":null,\"studentWork\":{\"id\":60894363,\"workName\":null,\"resourceType\":null,\"workLevel\":null,\"studentId\":81104313,\"subjectId\":null,\"moduleId\":null,\"unitId\":null,\"topicId\":null,\"workStatus\":null,\"createTime\":null,\"lastUpdateTime\":null,\"year\":null,\"type\":null,\"studentExercises\":null,\"parentQuesNumber\":null,\"quesNumber\":null,\"rightNumber\":null,\"baseurl\":null,\"moduleName\":null,\"levelId\":0}}";
        workIn=JsonUtil.readValue(s1,StudentWorkIn.class);
        System.out.println(stuBrushService.getWork(workIn));

    }


    @Test
    public void createEnchance() throws Exception {

        CreateEnchanceRequest createEnchanceRequest;
        String s="{\"reqId\":null,\"questionId\":11345424,\"studentId\":81951187385}";
        createEnchanceRequest=JsonUtil.readValue(s,CreateEnchanceRequest.class);
        stuBrushService.createEnchance(createEnchanceRequest);

    }


}