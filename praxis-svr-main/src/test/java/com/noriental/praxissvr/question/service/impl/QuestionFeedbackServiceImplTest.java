package com.noriental.praxissvr.question.service.impl;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.noriental.BaseTest;
import com.noriental.praxissvr.question.request.CreateQuestionFeedbackRequest;
import com.noriental.praxissvr.question.request.FindEntrustExercisesRequest;
import com.noriental.praxissvr.question.request.UpdateEntrustExerciseRequest;
import com.noriental.praxissvr.question.service.EntrustExerciseService;
import com.noriental.praxissvr.question.service.QuestionFeedbackService;
import com.noriental.utils.json.JsonUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;

/**
 * @author chenlihua
 * @date 2015/12/23
 * @time 11:21
 */
@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = {"classpath:testApplicationContext.xml", "classpath:spring/applicationContext-consumer*.xml"})
//@ContextConfiguration(locations = {"classpath:spring/applicationContext*.xml"})
public class QuestionFeedbackServiceImplTest extends BaseTest{

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private QuestionFeedbackService questionFeedbackService;
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

//    @Test
//    public void findEntrustExercises() throws Exception {
//        PageBounds p = new PageBounds();
//        p.setLimit(10);
//        p.setPage(1);
//        FindEntrustExercisesRequest req = new FindEntrustExercisesRequest(6161745,p);
//
//        logger.info("result-"+JsonUtil.obj2Json(questionFeedbackService.findEntrustExercises(req).getPager()));
//    }

    @Test
    public void createQuestionFeedback() throws Exception {
//        CreateQuestionFeedbackRequest req = new CreateQuestionFeedbackRequest(3009576L,null,6205448L,1,"admin",new ArrayList<String>(){{add("1");}},"error2");
//        logger.info(JsonUtil.obj2Json(questionFeedbackService.createQuestionFeedback(req)));

        //submit_by = 62159681
        logger.info("====== before =====");
        CreateQuestionFeedbackRequest req1 = new CreateQuestionFeedbackRequest(832355L,null,
                81951175826L,2,"app",new ArrayList<String>(){{add("1");}},
                "junit_error",1);
        logger.info(JsonUtil.obj2Json(questionFeedbackService.createQuestionFeedback(req1)));
        logger.info("====== after =====");
    }
}