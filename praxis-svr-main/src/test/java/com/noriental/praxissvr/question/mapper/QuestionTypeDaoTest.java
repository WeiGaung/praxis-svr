package com.noriental.praxissvr.question.mapper;

import com.noriental.BaseTest;
import com.noriental.praxissvr.question.bean.QuestionType;
import com.noriental.praxissvr.question.bean.html.Question;
import com.noriental.praxissvr.question.dao.QuestionTypeDao;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hushuang on 2017/7/31.
 */
public class QuestionTypeDaoTest extends BaseTest {


    private static final Logger LOGGER = LoggerFactory.getLogger(QuestionTypeDao.class);

    @Autowired
    private QuestionTypeDao questionTypeDao;


    @Test
    public void findQuesTypeBySubjectIdTest(){


        Map<String, Object> params = new HashMap<>();
        params.put("subjectId", 7);
        List<QuestionType> types = questionTypeDao.findQuesTypeBySubjectId(params);

        LOGGER.info("\n========{}",types);


    }
    @Test
    public void testFindById(){

        QuestionType questionType= questionTypeDao.findById(4);
        LOGGER.info("\n========{}",questionType.getStructId());
    }

}
