package com.noriental.praxissvr.wrong.request;

import com.noriental.validate.bean.BaseRequest;

import javax.validation.constraints.Min;

public class DeleteWrongQuesRequest extends BaseRequest {
    @Min(1)
    private long studentId;
    @Min(value = 1)
    private long questionId;

    public long getStudentId() {
        return studentId;
    }

    public void setStudentId(long studentId) {
        this.studentId = studentId;
    }

    public long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(long questionId) {
        this.questionId = questionId;
    }
}
