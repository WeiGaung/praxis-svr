package com.noriental.praxissvr.answer.request;

import com.noriental.validate.bean.BaseRequest;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;


public class UpdateCorrectRequest extends BaseRequest {
    @NotNull
    private Long resourceId;
    @NotNull
    private String exerciseSource;
    @NotNull
    private Long studentId;
    @NotNull
    private Long questionId;
    @NotBlank
    private String correctorRole;
    @NotNull
    private Long correctorId;
    @NotBlank
    private String result;
    private String redoSource;
    private Double totalScore;
    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public String getExerciseSource() {
        return exerciseSource;
    }

    public void setExerciseSource(String exerciseSource) {
        this.exerciseSource = exerciseSource;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getCorrectorRole() {
        return correctorRole;
    }

    public void setCorrectorRole(String correctorRole) {
        this.correctorRole = correctorRole;
    }

    public Long getCorrectorId() {
        return correctorId;
    }

    public void setCorrectorId(Long correctorId) {
        this.correctorId = correctorId;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getRedoSource() {
        return redoSource;
    }

    public void setRedoSource(String redoSource) {
        this.redoSource = redoSource;
    }

    public Double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Double totalScore) {
        this.totalScore = totalScore;
    }
}
