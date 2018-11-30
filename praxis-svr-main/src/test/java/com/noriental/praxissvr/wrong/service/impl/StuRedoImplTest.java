package com.noriental.praxissvr.wrong.service.impl;

import com.noriental.BaseTest;
import com.noriental.praxissvr.wrong.request.FindChalQuessRequest;
import com.noriental.praxissvr.wrong.service.StuRedoService;
import com.noriental.utils.json.JsonUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.invoke.MethodHandles;

/**
 * @author chenlihua
 * @date 2016/1/6
 * @time 11:44
 */
public class StuRedoImplTest extends BaseTest{
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private StuRedoService stuRedoService;
    @Test
    public void findClassQuesReport() {
        FindChalQuessRequest req = new FindChalQuessRequest();
        req.setStudentId(1L);
        req.setExerciseSource("1");
        req.setClassId(1L);
        req.setCourseId(1L);
        req.setResouceId(1L);
//        logger.info(JsonUtil.obj2Json(stuRedoService.findChalQuess(req)));
    }
}