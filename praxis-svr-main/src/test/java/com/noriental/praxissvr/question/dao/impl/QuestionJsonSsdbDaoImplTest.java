package com.noriental.praxissvr.question.dao.impl;

import com.noriental.BaseTest;
import com.noriental.praxissvr.question.dao.QuestionJsonDao;
import com.noriental.utils.json.JsonUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Created by shengxian on 2016/12/22.
 */
public class QuestionJsonSsdbDaoImplTest extends BaseTest{
    Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @Autowired
    QuestionJsonSsdbDao questionJsonSsdbDao;
    @Test
    public void findByIds() throws Exception {
        logger.info(JsonUtil.obj2Json(questionJsonSsdbDao.findByIds(Arrays.asList(4000677L))));
    }

}