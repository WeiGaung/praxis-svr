package com.noriental.praxissvr.question.dao;


import com.noriental.praxissvr.question.bean.SuperQuestionMongo;

import java.util.List;
import java.util.Map;

public interface QuestionJsonDao  {

    Map<Long, SuperQuestionMongo> findByIds(List<Long> ids);

}
