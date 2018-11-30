package com.noriental.praxissvr.question.service;

import com.noriental.BaseTest;
import com.noriental.praxissvr.question.request.FindQuestionTypesBySubjectIdRequest;
import com.noriental.praxissvr.question.response.FindQuestionTypesBySubjectIdResponse;
import com.noriental.utils.json.JsonUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by hushuang on 2017/7/31.
 */
public class QuestionTypeServiceTest extends BaseTest {


    private static final Logger logger = LoggerFactory.getLogger(QuestionTypeServiceTest.class);

    @Autowired
    private QuestionTypeService questionTypeService;



    @Test
    public void findQuestionTypeBySubjectIdTest(){


        FindQuestionTypesBySubjectIdRequest request = new FindQuestionTypesBySubjectIdRequest();
        request.setSubjectId(3);
        FindQuestionTypesBySubjectIdResponse response = questionTypeService.findQuestionTypeBySubjectId(request);
        logger.info("\n==========={}", JsonUtil.obj2Json(response.getList()));

    }


}
