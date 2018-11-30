package com.noriental.praxissvr.question.request;

import com.noriental.validate.bean.BaseRequest;

import javax.validation.constraints.NotNull;


public class FindQuestionFeedbacksRequest extends BaseRequest {

    @NotNull
    private Long studentId;
    @NotNull
    private Long questionId;
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

}
