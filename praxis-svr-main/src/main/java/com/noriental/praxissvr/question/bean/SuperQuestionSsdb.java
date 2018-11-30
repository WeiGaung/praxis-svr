package com.noriental.praxissvr.question.bean;

import java.io.Serializable;
public class SuperQuestionSsdb implements Serializable {
    private static final long serialVersionUID = -7175663580976632251L;

    private SuperQuestionMongoContent content;
    private String questionId;

    public SuperQuestionSsdb(String questionId, SuperQuestionMongoContent content) {
        this.content = content;
        this.questionId = questionId;
    }

    public SuperQuestionSsdb() {
    }

    public SuperQuestionMongoContent getContent() {
        return content;
    }

    public void setContent(SuperQuestionMongoContent content) {
        this.content = content;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    @Override
    public String toString() {
        return "SuperQuestionMongo [content=" + content + "]";
    }

}
