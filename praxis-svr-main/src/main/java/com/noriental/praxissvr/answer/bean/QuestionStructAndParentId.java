package com.noriental.praxissvr.answer.bean;

import java.io.Serializable;

/**
 * @author kate
 * @create 2018-04-08 17:10
 * @desc 结构缓存类
 **/
public class QuestionStructAndParentId implements Serializable{
    private Long questionId;
    private Integer structId;
    private Integer questionTypeId;
    private Long parentQuestionId;

    public QuestionStructAndParentId() {

    }

    public QuestionStructAndParentId(Long questionId, Integer structId, Long parentQuestionId, Integer questionTypeId) {
        this.parentQuestionId = parentQuestionId;
        this.questionId = questionId;
        this.structId = structId;
        this.questionTypeId = questionTypeId;
    }

    public Long getParentQuestionId() {
        return parentQuestionId;
    }

    public void setParentQuestionId(Long parentQuestionId) {
        this.parentQuestionId = parentQuestionId;
    }

    public Integer getStructId() {
        return structId;
    }

    public void setStructId(Integer structId) {
        this.structId = structId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Integer getQuestionTypeId() {
        return questionTypeId;
    }

    public void setQuestionTypeId(Integer questionTypeId) {
        this.questionTypeId = questionTypeId;
    }
}
