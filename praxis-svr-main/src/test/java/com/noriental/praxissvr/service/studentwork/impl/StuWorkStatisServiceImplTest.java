package com.noriental.praxissvr.service.studentwork.impl;

import com.noriental.BaseTest;
import com.noriental.praxissvr.statis.request.FindStuWorkSatisRequest;
import com.noriental.praxissvr.statis.request.StatisLevelVo;
import com.noriental.praxissvr.statis.request.StatisLevelsRequest;
import com.noriental.praxissvr.statis.service.StuWorkStatisService;
import com.noriental.utils.json.JsonUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;

/**
 * Created by bluesky on 2016/6/21.
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = {"classpath:testApplicationContext.xml"})
public class StuWorkStatisServiceImplTest extends BaseTest{
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @Autowired
    StuWorkStatisService stuWorkStatisService;
    @Test
    public void test(){
        FindStuWorkSatisRequest req = new FindStuWorkSatisRequest();
        req.setStudentId(2222L);
        req.setSubjectId(1L);
        logger.info(JsonUtil.obj2Json(stuWorkStatisService.findStuWorkSatis(req)));
    }

    @Test
    public void findStuWorkSatisByIds(){
        StatisLevelsRequest req = new StatisLevelsRequest();
        StatisLevelVo vo = new StatisLevelVo(2,618L);
        StatisLevelVo vo1 = new StatisLevelVo(1,242L);

        req.setStatisLevels(Arrays.asList(vo,vo1));
        req.setStudentId(82951058500L);
        valid(req);
        logger.info(JsonUtil.obj2Json(stuWorkStatisService.findStuWorkSatisByIds(req)));
    }
}
