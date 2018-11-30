package com.noriental.praxissvr.resourcegroup.request;

import com.noriental.validate.bean.BaseRequest;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Created by kate on 2016/11/18.
 */
public class ResGroupTransferRequest extends BaseRequest {
    /***
     * 习题ID
     */
    @NotNull
    private Long questionId;
    /**
     * 新的组ID
     */
    @NotNull
    private Long groupId;

    /***
     * 系统用户ID
     */
    @Min(1)
    private Long systemId;

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long getSystemId() {
        return systemId;
    }

    public void setSystemId(Long systemId) {
        this.systemId = systemId;
    }
}
