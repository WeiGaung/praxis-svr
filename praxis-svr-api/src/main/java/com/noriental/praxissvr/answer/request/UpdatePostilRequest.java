package com.noriental.praxissvr.answer.request;

import com.noriental.validate.bean.BaseRequest;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;


public class UpdatePostilRequest extends BaseRequest {
    @NotNull
    private Long resourceId;
    @NotNull
    private String exerciseSource;
    @NotNull
    private Long studentId;
    @NotNull
    private Long questionId;
    @NotBlank
    private String postilTeacher;

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

    public String getPostilTeacher() {
        return postilTeacher;
    }

    public void setPostilTeacher(String postilTeacher) {
        this.postilTeacher = postilTeacher;
    }
}
