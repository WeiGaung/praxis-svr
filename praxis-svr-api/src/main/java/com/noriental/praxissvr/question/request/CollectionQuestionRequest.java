package com.noriental.praxissvr.question.request;

import com.noriental.validate.bean.BaseRequest;

import javax.validation.constraints.NotNull;

/**
 * Created by hushuang on 2017/7/17.
 */
public class CollectionQuestionRequest extends BaseRequest {

    @NotNull
    private Long questionId; //题目ID
    @NotNull
    private Long customerDirectoryId;//自定义目录ID
    @NotNull
    private Long uploadId; //上传人ID
    @NotNull
    private Long groupId;//自定义目录体系ID

    @Override
    public String toString() {
        return "CollectionQuestionRequest{" +
                "questionId=" + questionId +
                ", customerDirectoryId=" + customerDirectoryId +
                ", uploadId=" + uploadId +
                ", groupId=" + groupId +
                '}';
    }

    public CollectionQuestionRequest() {
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Long getCustomerDirectoryId() {
        return customerDirectoryId;
    }

    public void setCustomerDirectoryId(Long customerDirectoryId) {
        this.customerDirectoryId = customerDirectoryId;
    }

    public Long getUploadId() {
        return uploadId;
    }

    public void setUploadId(Long uploadId) {
        this.uploadId = uploadId;
    }

}
