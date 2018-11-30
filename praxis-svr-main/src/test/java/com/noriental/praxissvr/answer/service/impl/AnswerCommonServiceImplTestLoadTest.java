package com.noriental.praxissvr.answer.service.impl;

import com.noriental.BaseTest;
import com.noriental.BaseTestClient;
import com.noriental.log.TraceKeyHolder;
import com.noriental.praxissvr.LoaderRunnerUnit;
import com.noriental.praxissvr.answer.request.UpdateSubmitAnswerRequest;
import com.noriental.praxissvr.answer.service.AnswerCommonService;
import com.noriental.utils.json.JsonUtil;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.invoke.MethodHandles;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * Created by shengxian on 2016/11/30.
 */
public class AnswerCommonServiceImplTestLoadTest extends BaseTest {
    @Autowired
    AnswerCommonService answerCommonService;
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @Test
    @Ignore
    public void updateSubmitAnswer() throws Exception {
        Runnable run = new Runnable() {
            @Override
            public void run() {
                int abs = Math.abs(new Random().nextInt());
                String reqId = "xiaotest"+Math.abs(abs);
                TraceKeyHolder.setTraceKey(reqId);
                MDC.put("id",reqId);
                UpdateSubmitAnswerRequest request = JsonUtil.readValue("{\"reqId\":null,\"resourceId\":274181," +
                        "\"exerciseSource\":\"8\",\"studentId\":81184199," +
                        "\"updateSubmitAnswerList\":[{\"questionId\":2600697,\"result\":\"6\"," +
                        "\"submitAnswer\":\"[\\\"ap_JAzcQkBS4o.okpng\\\"]\"}],\"correctorRole\":\"student\"," +
                        "\"correctorId\":81184199,\"redoSource\":null,\"subExerciseSource\":1}",
                        UpdateSubmitAnswerRequest.class);
                //request.setStudentId(abs+1L-1L);
                logger.info(JsonUtil.obj2Json(answerCommonService.updateSubmitAnswer(request)));
            }
        };
        LoaderRunnerUnit.run(run,200,1);
    }

}