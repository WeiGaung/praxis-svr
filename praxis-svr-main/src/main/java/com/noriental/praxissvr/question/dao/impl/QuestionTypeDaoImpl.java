package com.noriental.praxissvr.question.dao.impl;

import com.noriental.dao.BaseDaoImpl;
import com.noriental.praxissvr.question.bean.QuestionType;
import com.noriental.praxissvr.question.dao.QuestionTypeDao;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Repository
public class QuestionTypeDaoImpl extends BaseDaoImpl<QuestionType, Long> implements QuestionTypeDao {

    private final String namespace = QuestionType.class.getName();


    @Override
    public List<QuestionType> findQuesTypeBySubjectId(Map<String, Object> param) {
        return this.findList(namespace + ".findQuesTypeBySubjectId", param);
    }

    @Override
    public List<QuestionType> findAllEnable() {
        Map<String, Object> param = new HashMap<>();
        param.put("enable", 1);
        return findList(namespace + ".findAllEnable", param);
    }

    @Override
    public List<QuestionType> findAll() {
        Map<String, Object> param = new HashMap<>();
        return findList(namespace + ".findAll", param);
    }

    @Override
    public QuestionType findById(long id) {
        return this.findById(QuestionType.class, id);
    }
}
