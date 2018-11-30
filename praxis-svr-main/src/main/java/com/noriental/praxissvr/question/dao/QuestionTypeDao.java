package com.noriental.praxissvr.question.dao;

import com.noriental.dao.BaseDao;
import com.noriental.praxissvr.question.bean.QuestionType;

import java.util.List;
import java.util.Map;


public interface QuestionTypeDao extends BaseDao<QuestionType, Long> {

    List<QuestionType> findQuesTypeBySubjectId(Map<String, Object> param);

    List<QuestionType> findAllEnable();

    List<QuestionType> findAll();

    QuestionType findById(long id);
}
