package com.noriental;

import com.noriental.log.TraceKeyHolder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.invoke.MethodHandles;
import java.util.Random;

/**
 * Created by chendengyu on 2016/7/27.
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:application-client.xml"})
public class BaseTestClient {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private long start;
    public   void setRequestid(){
        String key = "BaseTestClient_"+Math.abs(new Random().nextInt());
        TraceKeyHolder.setTraceKey(key);
        MDC.put("id", key);
        logger.info("reqid:"+key);
    }
    @Before
    public void befor() {
        logger.info("============befor============");
        setRequestid();
        start = System.currentTimeMillis();
    }

    @After
    public void after() {
        logger.info("============after============");
        logger.info("============cost[{}]============", System.currentTimeMillis() - start);
        MDC.remove("id");
    }

    public void valid(Object object) {

    }

    @Test
    public void testName() throws Exception {

    }
}
