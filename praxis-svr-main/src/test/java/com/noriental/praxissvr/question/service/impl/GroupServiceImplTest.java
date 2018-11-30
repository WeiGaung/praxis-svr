package com.noriental.praxissvr.question.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.noriental.BaseTestClient;
import com.noriental.log.TraceKeyHolder;
import com.noriental.praxissvr.question.request.FindGroupByIdRequest;
import com.noriental.praxissvr.question.request.FindGroupBySystemIdRequest;
import com.noriental.praxissvr.question.response.FindGroupByIdResponse;
import com.noriental.praxissvr.question.response.FindGroupBySystemIdResponse;
import com.noriental.praxissvr.question.service.GroupService;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.lang.invoke.MethodHandles;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author chenlihua
 * @date 2015/12/23
 * @time 11:21
 */

public class GroupServiceImplTest extends BaseTestClient {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Resource
    private GroupService groupService;

    @Test
    public void testFindGroupBySystemId() throws Exception {
        FindGroupBySystemIdRequest request = new FindGroupBySystemIdRequest();
        request.setSystemId(6161545);
        FindGroupBySystemIdResponse resp = groupService.findGroupBySystemId(request);
//        System.out.println(JSONObject.toJSONString(resp, true));
        assertTrue(CollectionUtils.isNotEmpty(resp.getList()));
    }

    @Test
    public void testFindGroupById() throws Exception {
        FindGroupByIdRequest request = new FindGroupByIdRequest();
        request.setId(56);
        FindGroupByIdResponse resp = groupService.findGroupById(request);
        System.out.println(JSONObject.toJSONString(resp, true));
        assertNotNull(resp.getGroup());
    }

    @Test
    @Ignore //自动单元测试不用跑该用例，用完记得@Ignore
    public void testFindGroupBySystemIdConcurrent() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        final AtomicLong total = new AtomicLong(0);
        final AtomicLong sec1 = new AtomicLong(0);
        final AtomicLong sec2 = new AtomicLong(0);
        final AtomicLong sec3 = new AtomicLong(0);
        final AtomicLong sec4 = new AtomicLong(0);

        for (int i = 0; i < threadCount; i++) {
            final String name = String.format("Thread-%s-%s", i, System.currentTimeMillis());
            Runnable t = new Thread(new Runnable() {
                @Override
                public void run() {
                    total.addAndGet(1);
                    try {
                        while (true) {
                            TraceKeyHolder.setTraceKey(name);
                            long l = System.currentTimeMillis();
                            testFindGroupBySystemId();
                            long o = System.currentTimeMillis() - l;
                            if (o > 1000) {
                                sec1.addAndGet(1);
                                if (o > 2000) {
                                    sec2.addAndGet(1);
                                    if (o > 3000) {
                                        sec3.addAndGet(1);
                                        if (o > 4000) {
                                            sec4.addAndGet(1);
                                        }
                                    }
                                }
                            }
//                            logger.info("cost:{}ms", o);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            executorService.execute(t);
        }

       new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    logger.info("total:{}, se1:{}, se2:{}, se3:{}, se4:{}", total.get(), sec1.get(), sec2.get(), sec3.get(), sec4.get());
                }
            }
        }).start();

        executorService.awaitTermination(5, TimeUnit.MINUTES);
    }
}