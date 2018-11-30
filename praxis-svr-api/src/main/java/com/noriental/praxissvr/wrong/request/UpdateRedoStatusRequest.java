package com.noriental.praxissvr.wrong.request;

import com.noriental.validate.bean.BaseRequest;

import javax.validation.constraints.NotNull;

/**
 * Created by bluesky on 2016/6/7.
 */
public class UpdateRedoStatusRequest extends BaseRequest {
    @NotNull
    private Long resourceId;
    @NotNull
    private Long studentId;
    @NotNull
    private Long questionId;
    @NotNull
    private String redoSource;

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
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

    public String getRedoSource() {
        return redoSource;
    }

    public void setRedoSource(String redoSource) {
        this.redoSource = redoSource;
    }

}
