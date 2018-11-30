package com.noriental.praxissvr.question.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by hushuang on 2017/3/14.
 */
public class BlankAndAnswerResult implements Serializable {

    /**
     * 题目ID
     */
    private Long questionId;
    /**
     * 子题的父类目ID
     */
    private long parentQuestionId;


    List<AnswerResult> answerResults;

    public BlankAndAnswerResult() {
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

    public List<AnswerResult> getAnswerResults() {
        return answerResults;
    }

    public void setAnswerResults(List<AnswerResult> answerResults) {
        this.answerResults = answerResults;
    }

    @Override
    public String toString() {
        return "BlankAndAnswerResult{" +
                "questionId=" + questionId +
                ", parentQuestionId=" + parentQuestionId +
                ", answerResults=" + answerResults +
                '}';
    }
}
