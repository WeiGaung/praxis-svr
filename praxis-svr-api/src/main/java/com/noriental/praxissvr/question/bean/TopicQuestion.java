package com.noriental.praxissvr.question.bean;


import com.noriental.adminsvr.bean.knowledge.Topic;
import com.noriental.praxissvr.question.bean.Question;

import java.io.Serializable;

/**
 * Author : Lance lance7in_gmail_com
 * Date   : 23/02/2014 13:19
 * Since  :
 */
public class TopicQuestion implements Serializable {

    private static final long serialVersionUID = 4884927532480053911L;

    private long id;

    private Question question;

    private int mastery = 0;

    private Topic topic;

    private long topicId;

    private long questionId;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public int getMastery() {
        return mastery;
    }

    public void setMastery(int mastery) {
        this.mastery = mastery;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public long getTopicId() {
        return topicId;
    }

    public void setTopicId(long topicId) {
        this.topicId = topicId;
    }

    public long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(long questionId) {
        this.questionId = questionId;
    }
}
