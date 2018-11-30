package com.noriental.praxissvr.question.service.impl;

import com.noriental.BaseTest;
import com.noriental.praxissvr.question.request.FindQuestionByIdRequest;
import com.noriental.praxissvr.question.request.FindQuestionsByIdsRequest;
import com.noriental.praxissvr.question.service.QuestionPartQueryService;
import com.noriental.praxissvr.question.service.QuestionService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liujiang on 2018/5/17.
 */
public class QuestionPartQueryServiceImplTest extends BaseTest {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Resource
    private QuestionPartQueryService questionPartQueryService;

    @Test
    public void testFindQuestionsState(){
        FindQuestionsByIdsRequest request=new FindQuestionsByIdsRequest();
        List<Long> ids=new ArrayList<>();
        ids.add(11330819L);
//        ids.add(11330820L);
        request.setQuestionIds(ids);
        System.out.println(questionPartQueryService.findQuestionsState(request).getQuestionStateResponseList());
    }

}
