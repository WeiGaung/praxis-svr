package com.noriental.praxissvr.question.bean;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by liujiang on 2018/8/22.
 */
public class CollectionQuestion implements Serializable{

    private static final long serialVersionUID = 5017126879008963471L;
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
        return "CollectionQuestion{" +
                "questionId=" + questionId +
                ", customerDirectoryId=" + customerDirectoryId +
                ", uploadId=" + uploadId +
                ", groupId=" + groupId +
                '}';
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

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }
}
