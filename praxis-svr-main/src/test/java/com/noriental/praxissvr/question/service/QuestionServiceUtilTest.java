package com.noriental.praxissvr.question.service;

import com.noriental.BaseTest;
import com.noriental.praxissvr.common.QuestionState;
import com.noriental.praxissvr.question.mapper.AuditsedSchoolMapper;
import com.noriental.praxissvr.question.utils.QuestionServiceUtil;
import com.noriental.utils.redis.RedisUtil;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * Created by liujiang on 2018/4/9.
 */
public class QuestionServiceUtilTest extends BaseTest {
    @Resource
    private AuditsedSchoolMapper auditsedSchoolMapper;
    @Resource
    private RedisUtil redisUtil;

    @Test
    public void testQuestionUtil(){

        long i=1314L;
        System.out.println("机构是否处于白名单"+ QuestionServiceUtil.isWhiteSchool(auditsedSchoolMapper,i,redisUtil));
    }

}
