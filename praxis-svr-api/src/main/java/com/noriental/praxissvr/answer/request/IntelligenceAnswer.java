package com.noriental.praxissvr.answer.request;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by kate on 2017/3/8.
 * 智能批改对应的习题ID、批改结果、第三方提供的结果
 */
public class IntelligenceAnswer implements Serializable {

    /***
     * 习题ID（复合体为小题的ID）
     */
    @NotNull
    private Long questionId;
    /***
     * 智能批改结果
     */
    @NotBlank
    private String result;

    /***
     * 智能批改源数据
     */
    @NotNull
    private String answerSource;

    /***
     * 批改角色
     */
    @NotBlank
    private String correctorRole;


    /***
     * 智能批改点阵数据
     */
    private String matrix;

    /**
     * 总分值
     */
    private Double totalScore;

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

    public String getAnswerSource() {
        return answerSource;
    }

    public void setAnswerSource(String answerSource) {
        this.answerSource = answerSource;
    }

    public String getMatrix() {
        return matrix;
    }

    public void setMatrix(String matrix) {
        this.matrix = matrix;
    }
    public String getCorrectorRole() {
        return correctorRole;
    }

    public void setCorrectorRole(String correctorRole) {
        this.correctorRole = correctorRole;
    }

    public Double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Double totalScore) {
        this.totalScore = totalScore;
    }
}
