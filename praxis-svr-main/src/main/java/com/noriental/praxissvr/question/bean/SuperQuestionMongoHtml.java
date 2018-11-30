package com.noriental.praxissvr.question.bean;

public class SuperQuestionMongoHtml {
    private String question_id;
    private Object content;

    public SuperQuestionMongoHtml(String question_id, Object content) {
        this.question_id = question_id;
        this.content = content;
    }

    public SuperQuestionMongoHtml() {
    }

    public String getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(String question_id) {
        this.question_id = question_id;
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
                "question_id='" + question_id + '\'' +
                ", content=" + content +
                '}';
    }
}
