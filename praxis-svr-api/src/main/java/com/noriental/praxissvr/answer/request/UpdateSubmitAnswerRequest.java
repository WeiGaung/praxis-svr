package com.noriental.praxissvr.answer.request;

import com.noriental.validate.bean.BaseRequest;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.List;


public class UpdateSubmitAnswerRequest extends BaseRequest {
    @NotNull
    private Long resourceId;
    @NotNull
    private String exerciseSource;
    @NotNull
    private Long studentId;
    @NotEmpty
    private List<UpdateSubmitAnswer> updateSubmitAnswerList;
    @NotBlank
    private String correctorRole;
    @NotNull
    private Long correctorId;

    private String redoSource;

    private Integer subExerciseSource;//错题  1、主屏 2、预习 3、复习

    private String uniqueKey;

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


    public String getRedoSource() {
        return redoSource;
    }

    public void setRedoSource(String redoSource) {
        this.redoSource = redoSource;
    }

    public List<UpdateSubmitAnswer> getUpdateSubmitAnswerList() {
        return updateSubmitAnswerList;
    }

    public void setUpdateSubmitAnswerList(List<UpdateSubmitAnswer> updateSubmitAnswerList) {
        this.updateSubmitAnswerList = updateSubmitAnswerList;
    }

    public Integer getSubExerciseSource() {
        return subExerciseSource;
    }

    public void setSubExerciseSource(Integer subExerciseSource) {
        this.subExerciseSource = subExerciseSource;
    }

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }
}
