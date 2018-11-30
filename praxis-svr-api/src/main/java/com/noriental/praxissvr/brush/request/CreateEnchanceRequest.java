package com.noriental.praxissvr.brush.request;

import com.noriental.validate.bean.BaseRequest;

import javax.validation.constraints.NotNull;


public class CreateEnchanceRequest extends BaseRequest {
    @NotNull
    private Long questionId;
    @NotNull
    private Long studentId;

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }
}
