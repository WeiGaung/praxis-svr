package com.noriental.praxissvr.question.dao;


import com.noriental.praxissvr.question.bean.SuperQuestionMongoHtml;

import java.util.List;
import java.util.Map;

public interface QuestionHtmlDao {

    Map<Long, SuperQuestionMongoHtml> findByIds(List<Long> ids);
}
