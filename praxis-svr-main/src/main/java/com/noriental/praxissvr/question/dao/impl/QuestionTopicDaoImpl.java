package com.noriental.praxissvr.question.dao.impl;

import com.noriental.dao.BaseDaoImpl;
import com.noriental.praxissvr.question.bean.QuestionTopic;
import com.noriental.praxissvr.question.dao.QuestionTopicDao;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository("questionTopicDao")
public class QuestionTopicDaoImpl extends BaseDaoImpl<QuestionTopic, Long> implements QuestionTopicDao {
    private String namespace = QuestionTopic.class.getName();

    @Override
    public List<QuestionTopic> findByQuestionIds(List<Long> questionIds) {
        if (CollectionUtils.isEmpty(questionIds)) {
            return new ArrayList<>();
        }
        return this.findList(this.namespace + ".findByQuestionIds", questionIds);
    }

    @Override
    public List<QuestionTopic> findByQuestionId(long id) {
        return this.findList(this.namespace + ".findByQuestionId", id);
    }
}
