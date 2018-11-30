package com.noriental.praxissvr.wrong.service.impl;

import com.noriental.BaseTest;
import com.noriental.log.TraceKeyHolder;
import com.noriental.praxissvr.common.StuAnswerConstant;
import com.noriental.praxissvr.common.TrailBaseErrorRequestRequest;
import com.noriental.praxissvr.wrong.bean.StuQuesKnowledge;
import com.noriental.praxissvr.wrong.request.DeleteWrongQuesRequest;
import com.noriental.praxissvr.wrong.request.FindWrongQuesAnswersRequest;
import com.noriental.praxissvr.wrong.response.FindWrongQuesAnswersResponse;
import com.noriental.praxissvr.wrong.service.StuQuesKnowledgeService;
import com.noriental.solr.common.msg.SolrIndexReqMsg;
import com.noriental.utils.json.JsonUtil;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by shengxian on 2017/1/5.
 */
public class StuQuesKnowledgeServiceImplTest extends BaseTest {
    @Autowired
    StuQuesKnowledgeService stuQuesKnowledgeService;

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Test
    public void findWrongQuesAnswers() {
        FindWrongQuesAnswersRequest req ;
        /*req = JsonUtil.readValue("{\"reqId\":null,\"level\":\"LEVEL_1\",\"levelId\":7909,\"pageSize\":10," +
                "\"fromIndex\":0,\"studentId\":81951087986,\"dataType\":\"WRONG_MATERIAL\"}", FindWrongQuesAnswersRequest.class);*/


        req = JsonUtil.readValue("{\"reqId\":null,\"level\":\"LEVEL_3\",\"levelId\":3318,\"pageSize\":10," +
                "\"fromIndex\":0,\"studentId\":81951085082,\"dataType\":\"WRONG_KONWLEDGE\"}", FindWrongQuesAnswersRequest.class);
        FindWrongQuesAnswersResponse resp = stuQuesKnowledgeService.findWrongQuesAnswers(req);
        logger.info(JsonUtil.obj2Json(resp));
    }

    @Test
    public void findTrailBaseError() throws Exception {
        TrailBaseErrorRequestRequest req = new TrailBaseErrorRequestRequest();
        req.setStudentId(82951059896L);
        req.setSubjectId(5L);
        req.setDirectoryId(284l);
        logger.info(JsonUtil.obj2Json(stuQuesKnowledgeService.findTrailBaseError(req)));
    }

    @Test
    public void deleteWrongQues() {
        //        FindWrongQuesAnswersRequest req = new FindWrongQuesAnswersRequest();
        DeleteWrongQuesRequest req = new DeleteWrongQuesRequest();
        req.setQuestionId(219578L);
        req.setStudentId(8133899L);
        logger.info(JsonUtil.obj2Json(stuQuesKnowledgeService.deleteWrongQues(req)));
    }


    //    @Autowired
    //    private RabbitTemplate asyncNoReplyRabbitTemplateKnowledge;
    //
    //    @Test
    //    public void testSendMQ() {
    //        List<StuQuizKnowledgeDocument> docs = new ArrayList<>();
    //        StuQuizKnowledgeDocument d = new StuQuizKnowledgeDocument();
    //        d.setId(1L);
    //        d.setSeq(123);
    //        d.setCreateTime(new Date());
    //        d.setDataType(5);
    //        d.setModuleId(6L);
    //        d.setQuestionId(123456L);
    //        d.setStudentId(123456L);
    //        d.setSubjectId(1L);
    //        d.setTopicId(5L);
    //        d.setUnitId(7L);
    //        d.setQuestionCount(8);
    //        docs.add(d);
    //        if (docs.size() > 0) {
    //
    //            TraceKeyHolder.setTraceKey("caojiaqing123456");
    //            SolrIndexReqMsg object = new SolrIndexReqMsg(docs);
    //            asyncNoReplyRabbitTemplateKnowledge.convertAndSend(object);
    //            logger.info("send to solr mq!" + JsonUtil.obj2Json(docs));
    //        }
    //    }

}