package com.noriental.praxissvr.wrongQuestion.bean;

import java.io.Serializable;

/**
 * Created by kate on 2017/5/31.
 */
public class WrongQuesListEntity implements Serializable {

    private Long questionId;//题目ID

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

}
