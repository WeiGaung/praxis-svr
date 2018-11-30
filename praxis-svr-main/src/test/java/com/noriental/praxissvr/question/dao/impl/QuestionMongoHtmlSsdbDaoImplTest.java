package com.noriental.praxissvr.question.dao.impl;

import com.noriental.BaseTest;
import com.noriental.praxissvr.question.dao.QuestionHtmlDao;
import com.noriental.utils.json.JsonUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;

/**
 * Created by shengxian on 2016/12/21.
 */
public class QuestionMongoHtmlSsdbDaoImplTest extends BaseTest{
    @Resource
    private QuestionHtmlSsdbDao questionMongoHtmlSsdbDao;
    Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @Test
    public void getQuestionMongoHtmlByQid() throws Exception {

    }

    @Test
    public void findByIds() throws Exception {
        logger.info(JsonUtil.obj2Json(questionMongoHtmlSsdbDao.findByIds(Arrays.asList(4000677L))));
    }

}