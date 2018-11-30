package com.noriental.praxissvr.answer.bean;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by bluesky on 2016/5/19.
 */
public class UpdateSubmitAnswerVo implements Serializable{
    private Long questionId;
    private String result;
    private String submitAnswer;
    private String correctorRole;
    private Date correctorTime;
    private Long correctorId;
    private Integer structId;
    private Long parentQuestionId;
    private String redoStatus;
    private String audioResult;
    private Integer flag;  //是否已批改的标识  3表示已批 对应的都是复合体下的小题和单题
    private Double totalScore;
    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

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

    public String getCorrectorRole() {
        return correctorRole;
    }

    public void setCorrectorRole(String correctorRole) {
        this.correctorRole = correctorRole;
    }

    public Date getCorrectorTime() {
        return correctorTime;
    }

    public void setCorrectorTime(Date correctorTime) {
        this.correctorTime = correctorTime;
    }

    public Long getCorrectorId() {
        return correctorId;
    }

    public void setCorrectorId(Long correctorId) {
        this.correctorId = correctorId;
    }

    public Integer getStructId() {
        return structId;
    }

    public void setStructId(Integer structId) {
        this.structId = structId;
    }

    public String getRedoStatus() {
        return redoStatus;
    }

    public void setRedoStatus(String redoStatus) {
        this.redoStatus = redoStatus;
    }

    public Long getParentQuestionId() {
        return parentQuestionId;
    }

    public void setParentQuestionId(Long parentQuestionId) {
        this.parentQuestionId = parentQuestionId;
    }

    public String getAudioResult() {
        if (StringUtils.isBlank(audioResult)){
            return null;
        }
        return audioResult;
    }

    public void setAudioResult(String audioResult) {
        this.audioResult = audioResult;
    }

    public Double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Double totalScore) {
        this.totalScore = totalScore;
    }
}
