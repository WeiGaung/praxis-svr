package com.noriental.praxissvr.wrongQuestion.bean;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

/**
 * Created by kate on 2016/12/16.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WrongQuesChapterEntity implements Serializable {
    /**
     *单题错题次数
     */
    private int questionErrorNum;

    /***
     * 习题ID
     */
    private Long questionId;

    private Long id;


    public int getQuestionErrorNum() {
        return questionErrorNum;
    }

    public void setQuestionErrorNum(int questionErrorNum) {
        this.questionErrorNum = questionErrorNum;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
