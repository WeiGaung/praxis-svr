package com.noriental.praxissvr.answer.request;


import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by bluesky on 2016/5/19.
 */
public class UpdateSubmitAnswer implements Serializable{
    @NotNull
    private Long questionId;
    @NotBlank
    private String result;
    private String submitAnswer;
    private String audioResult;
    /**
     * 分值批改传入的每个小题的总分值
     */
    private Double totalScore;
    /**
     * 以下为郭维江迁题库要求新增字段
     */
    private Long parentQuestionId;
    private Integer structId;
    private Integer subjectId;
    private Integer questionTypeId;

    private String matrix;
    private String intelligent;

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getSubmitAnswer() {
        return submitAnswer;
    }

    public void setSubmitAnswer(String submitAnswer) {
        this.submitAnswer = submitAnswer;
    }

    public String getAudioResult() {
        return audioResult;
    }

    public void setAudioResult(String audioResult) {
        if (StringUtils.isBlank(audioResult)){
            this.audioResult = null;
        }
        this.audioResult = audioResult;
    }

    public Double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Double totalScore) {
        this.totalScore = totalScore;
    }

    public Integer getStructId() {
        return structId;
    }

    public void setStructId(Integer structId) {
        this.structId = structId;
    }

    public Integer getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
    }

    public Integer getQuestionTypeId() {
        return questionTypeId;
    }

    public void setQuestionTypeId(Integer questionTypeId) {
        this.questionTypeId = questionTypeId;
    }

    public String getMatrix() {
        return matrix;
    }

    public void setMatrix(String matrix) {
        this.matrix = matrix;
    }

    public String getIntelligent() {
        return intelligent;
    }

    public void setIntelligent(String intelligent) {
        this.intelligent = intelligent;
    }

    public Long getParentQuestionId() {
        return parentQuestionId;
    }

    public void setParentQuestionId(Long parentQuestionId) {
        this.parentQuestionId = parentQuestionId;
    }
}
