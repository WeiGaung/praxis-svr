package com.noriental;

import com.noriental.log.TraceKeyHolder;
import com.noriental.validate.util.MsgTemplateResolverUtils;
import org.junit.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.lang.invoke.MethodHandles;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.assertNull;

/**
 * Discribe:
 * Project:praxis-svr
 * Package: com.noriental
 * User: Chengwenbo
 * Date:  2016/05/28
 * Time: .16:24
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:application-root.xml"})
public class BaseTest {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private long start;
    @Before
    public void before() {
        String key = "BaseTest_"+Math.abs(new Random().nextInt());
        TraceKeyHolder.setTraceKey(key);
        MDC.put("id", key);
        logger.info("============before============");
        start = System.currentTimeMillis();
    }

    @After
    public void after() {

        try {
            Thread.sleep(4000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("============after cost[{}]============",System.currentTimeMillis() - start);
        MDC.remove("id");
    }

    protected <T> void valid(T t) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<T>> validate = validator.validate(t);
        ConstraintViolation constraintViolation = null;
        if (validate != null && !validate.isEmpty()) {
            constraintViolation = validate.iterator().next();
        }
        if (constraintViolation != null) {
            String msg = MsgTemplateResolverUtils.getMsgFromViolation(constraintViolation);
            assertNull(msg, msg);
        }
    }

    @Test
    @Ignore
    public void testBase() throws Exception {
        Assert.assertTrue(true);
    }
}
