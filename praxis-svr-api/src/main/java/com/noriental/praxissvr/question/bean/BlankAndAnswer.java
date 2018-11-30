package com.noriental.praxissvr.question.bean;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by hushuang on 2017/3/10.
 */
public class BlankAndAnswer  implements Serializable{

    /**
     * 题目ID
     */
    @NotNull
    private Long questionId;
    /**
     * 子题的父类目ID
     */
    private long parentQuestionId;
    /**
     * 子题序号
     */
    private Integer index;

    public BlankAndAnswer() {
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public long getParentQuestionId() {
        return parentQuestionId;
    }

    public void setParentQuestionId(long parentQuestionId) {
        this.parentQuestionId = parentQuestionId;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return "BlankAndAnswer{" +
                "questionId=" + questionId +
                ", parentQuestionId=" + parentQuestionId +
                ", index=" + index +
                '}';
    }
}
