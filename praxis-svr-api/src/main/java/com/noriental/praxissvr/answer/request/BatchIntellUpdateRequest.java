package com.noriental.praxissvr.answer.request;

import com.noriental.validate.bean.BaseRequest;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author kate
 * @create 2017-12-22 17:21
 * @desc 一键智能批改请求参数
 **/
public class BatchIntellUpdateRequest extends BaseRequest {
    @NotNull
    private Long resourceId;
    @NotNull
    private String exerciseSource;
    @NotNull
    private Long correctorId;
    @NotBlank
    private String correctorRole;
    @NotNull
    private List<Long> studentIds;
    @NotNull
    private Long questionId;
    private Integer i;

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

    public Integer getI() {
        return i;
    }

    public void setI(Integer i) {
        this.i = i;
    }
}
