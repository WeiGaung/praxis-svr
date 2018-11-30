package com.noriental.praxissvr.question.bean;

import java.io.Serializable;

/**
 * Created by chenlihua on 2016/9/23.
 * praxis-svr
 */
public class QuestionTopicVo implements Serializable {
    private long questionId;
    private long topicId;
    private String topicName;

    public long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(long questionId) {
        this.questionId = questionId;
    }

    public long getTopicId() {
        return topicId;
    }

    public void setTopicId(long topicId) {
        this.topicId = topicId;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }
}
