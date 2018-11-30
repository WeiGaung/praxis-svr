package com.noriental.praxissvr.answer.request;

import com.noriental.validate.bean.BaseRequest;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * @author kate
 * @create 2017-12-22 16:44
 * @desc 按人、按题一键批改请求实体，按人可以不传questionId,按题必须传
 * 带有分值的一键批改按题批改可以、按人批改协议改动很大
 **/
public class BatchUpdateRequest extends BaseRequest {

    @NotNull
    private Long resourceId;
    @NotNull
    private String exerciseSource;
    @NotNull
    private Long correctorId;
    @NotBlank
    private String correctorRole;
    @NotBlank
    private String result;
    @NotNull
    private List<Long> studentIds;

    private Long questionId;
    //业务类型
    //  1: 按人一键批改   2: 按题一键批改
    @NotNull
    private Integer busType;
    private Integer i;



    /*private Double totalScore;*/

    /**
     * key:题目的ID
     * value:题的分值
     */
    private Map<Long,Double> totalScoreMap;

    //private List<Integer> structIdList;

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

    public Long getCorrectorId() {
        return correctorId;
    }

    public void setCorrectorId(Long correctorId) {
        this.correctorId = correctorId;
    }

    public String getCorrectorRole() {
        return correctorRole;
    }

    public void setCorrectorRole(String correctorRole) {
        this.correctorRole = correctorRole;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<Long> getStudentIds() {
        return studentIds;
    }

    public void setStudentIds(List<Long> studentIds) {
        this.studentIds = studentIds;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Integer getBusType() {
        return busType;
    }

    public void setBusType(Integer busType) {
        this.busType = busType;
    }

    public Map<Long, Double> getTotalScoreMap() {
        return totalScoreMap;
    }
    public Integer getI() {
        return i;
    }

    public void setTotalScoreMap(Map<Long, Double> totalScoreMap) {
        this.totalScoreMap = totalScoreMap;
    }
    public void setI(Integer i) {
        this.i = i;
    }
}
