package com.noriental.praxissvr.question.bean;

/**
 * Created by hushuang on 2016/11/22.
 * 题目-知识点主题关联
 */
public class LinkQuestionTopic {

    private Long id;
    private long questionId;
    private long topicId;
    private int mastery;

    public LinkQuestionTopic() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public int getMastery() {
        return mastery;
    }

    public void setMastery(int mastery) {
        this.mastery = mastery;
    }

    @Override
    public String toString() {
        return "LinkQuestionTopic{" +
                "id=" + id +
                ", questionId=" + questionId +
                ", topicId=" + topicId +
                ", mastery=" + mastery +
                '}';
    }
}
