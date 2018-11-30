package com.noriental.praxissvr.question.bean;

public class SuperQuestionSsdbHtml {
    private String questionId;
    private Object content;

    public SuperQuestionSsdbHtml(String questionId, Object content) {
        this.questionId = questionId;
        this.content = content;
    }

    public SuperQuestionSsdbHtml() {
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }


    @Override
    public String toString() {
        return "SuperQuestionMongoHtml{" +
                "question_id='" + questionId + '\'' +
                ", content=" + content +
                '}';
    }
}
