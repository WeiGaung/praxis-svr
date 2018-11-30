package com.noriental.praxissvr.wrongQuestion.serviceImpl;

import com.noriental.BaseTest;
import com.noriental.praxissvr.answer.service.StuAnswerDataToMqService;
import com.noriental.praxissvr.brush.bean.StudentWorkAnswer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kate on 2016/12/27.
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class StuAnswerDataToMqServiceTest  extends BaseTest {
    private static final Logger logger = LoggerFactory.getLogger(StuAnswerDataToMqServiceTest.class);
    @Autowired
    private StuAnswerDataToMqService stuAnswerDataToMqService;


    @Test //ok
    public void test() throws Exception{
        List<StudentWorkAnswer> dataList=new ArrayList<>();
        for(int i=0;i<=5;i++){
            StudentWorkAnswer dataEntity=new StudentWorkAnswer();
            dataEntity.setQuestionId(10000L);
            dataEntity.setStudentId(2000L);
            StudentWorkAnswer dataEntity2=new StudentWorkAnswer();
            dataEntity2.setQuestionId(30000L);
            dataEntity2.setStudentId(2000L);
            dataList.add(dataEntity);
            dataList.add(dataEntity2);
            stuAnswerDataToMqService.sendExerciseData2Mq(dataList);
        }



      //  logger.info(JsonUtil.obj2Json(wrongQuesSubjectStatisResp));
    }



}
