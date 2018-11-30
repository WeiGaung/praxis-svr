package com.noriental.praxissvr.service.stuanswer.impl;

import com.noriental.BaseTest;
import com.noriental.praxissvr.answer.bean.StudentExercise;
import com.noriental.praxissvr.answer.request.StudentExerciseIn;
import com.noriental.praxissvr.common.StuAnswerConstant;
import com.noriental.praxissvr.answer.service.StuAnswerService;
import com.noriental.trailsvr.bean.SendTrailCountRequest;
import com.noriental.utils.json.JsonUtil;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chenlihua
 * @date 2015/12/31
 * @time 14:25
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = {"classpath:testApplicationContext.xml"})
public class StuAnswerServiceImplTest extends BaseTest{

    @Autowired
    StuAnswerService stuAnswerService;
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @Autowired
    private RabbitTemplate trailRabbitTemplate;
    @Test
    public void x() {
      for (int i=0;i<5;i++){

          SendTrailCountRequest req1 = new SendTrailCountRequest();
          req1.setStudentId(1L);
          req1.setExerciseType(Integer.valueOf(1));
          req1.setHistoryId(1L);
          req1.setRedoType(1);
//        trailCountService.updateTrailData(req1);
          trailRabbitTemplate.convertAndSend(req1);
          ConnectionFactory connectionFactory = trailRabbitTemplate.getConnectionFactory();
          logger.info("-----"+connectionFactory.getHost());
          logger.info("-----"+connectionFactory.getPort());
          logger.info("------------"+i);
      }
    }
}