package com.noriental.praxissvr.wrong.service.impl;

import com.noriental.BaseTest;
import com.noriental.praxissvr.common.StuAnswerConstant.DataTypeEnum;
import com.noriental.praxissvr.common.StuAnswerConstant.LevelEnum;
import com.noriental.praxissvr.common.TrailBaseErrorRequestRequest;
import com.noriental.praxissvr.common.TrailBaseErrorRequestResponse;
import com.noriental.praxissvr.wrong.request.FindWrongQuesAnswersRequest;
import com.noriental.praxissvr.wrong.response.FindWrongQuesAnswersResponse;
import com.noriental.praxissvr.wrong.service.StuQuesKnowledgeService;
import com.noriental.utils.json.JsonUtil;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import javax.annotation.Resource;

/**
 * Created by chendengyu on 2016/8/13.
 *
*/
public class StuQuesKnowledgeServiceTest extends  BaseTest{

    @Resource
    StuQuesKnowledgeService stuQuesKnowledgeService;

    @Test
    public void testFindWrongQuesAnswers() throws Exception {
        //{"reqId":null,"level":"LEVEL_1","levelId":8072,"pageSize":10,"fromIndex":0,"studentId":81951087626,"dataType":"WRONG_MATERIAL"}]
        //{"reqId":null,"level":"LEVEL_3","levelId":15282,"pageSize":10,"fromIndex":0,"studentId":81951085082,"dataType":"WRONG_KONWLEDGE"}]
        FindWrongQuesAnswersRequest findWrongQuesAnswersRequest = new FindWrongQuesAnswersRequest();
        String s="{\"reqId\":null,\"level\":\"LEVEL_3\",\"levelId\":963,\"pageSize\":10,\"fromIndex\":0," +
                "\"studentId\":81951113985,\"dataType\":\"WRONG_KONWLEDGE\"}";
        findWrongQuesAnswersRequest=JsonUtil.readValue(s,FindWrongQuesAnswersRequest.class);
        FindWrongQuesAnswersResponse wrongQuesAnswers = stuQuesKnowledgeService.findWrongQuesAnswers(findWrongQuesAnswersRequest);
        System.out.println(JsonUtil.obj2Json(wrongQuesAnswers));

    }

    @Test
    public void testfindTrailBaseError() throws Exception {
        //{"reqId":null,"level":"LEVEL_1","levelId":8072,"pageSize":10,"fromIndex":0,"studentId":81951087626,"dataType":"WRONG_MATERIAL"}]
        //{"reqId":null,"level":"LEVEL_3","levelId":15282,"pageSize":10,"fromIndex":0,"studentId":81951085082,"dataType":"WRONG_KONWLEDGE"}]
        TrailBaseErrorRequestRequest trailBaseErrorRequestRequest = new TrailBaseErrorRequestRequest();
        String s="{\"reqId\":null,\"studentId\":8157101,\"subjectId\":3,\"directoryId\":null,\"queryIds\":null," +
                "\"topic\":true}";
        trailBaseErrorRequestRequest=JsonUtil.readValue(s,TrailBaseErrorRequestRequest.class);
        trailBaseErrorRequestRequest.setIsTopic(true);
        TrailBaseErrorRequestResponse wrongQuesAnswers = stuQuesKnowledgeService.findTrailBaseError(trailBaseErrorRequestRequest);
        System.out.println(JsonUtil.obj2Json(wrongQuesAnswers));

    }
}
