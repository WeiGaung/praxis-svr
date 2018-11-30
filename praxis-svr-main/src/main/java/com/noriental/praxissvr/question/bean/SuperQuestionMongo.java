package com.noriental.praxissvr.question.bean;

import java.io.Serializable;

/**
 * 接受所有类型试题的类
 *
 * @author xiangfei
 * @date 2015年9月15日 上午9:22:54
 */
public class SuperQuestionMongo implements Serializable {
    private static final long serialVersionUID = -7175663580976632251L;

    private SuperQuestionMongoContent content;

    private String question_id;

    public SuperQuestionMongo( String question_id,SuperQuestionMongoContent content) {
        this.content = content;
        this.question_id = question_id;
    }

    public SuperQuestionMongo() {
    }

    public SuperQuestionMongoContent getContent() {
        return content;
    }

    public void setContent(SuperQuestionMongoContent content) {
        this.content = content;
    }

    public String getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(String question_id) {
        this.question_id = question_id;
    }

    @Override
    public String toString() {
        return "SuperQuestionMongo [content=" + content + "]";
    }

}
