package com.noriental.praxissvr.question.request;

import com.noriental.validate.bean.BaseRequest;

import javax.validation.constraints.NotNull;

/**
 * Created by hushuang on 2017/7/21.
 * 更新自定义目录的请求
 */
public class UpCusDirRequest extends BaseRequest{

    /*
        题目ID
     */
    @NotNull
    private Long questionId;
    /*
        自定义目录ID
     */
    @NotNull
    private Long cusDirId;
    /*
        用户ID
     */
    @NotNull
    private Long systemId;

    /*
        自定义目录体系ID
     */
    @NotNull
    private Long groupId;

    public UpCusDirRequest() {
    }

    public Long getSystemId() {
        return systemId;
    }

    public void setSystemId(Long systemId) {
        this.systemId = systemId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Long getCusDirId() {
        return cusDirId;
    }

    public void setCusDirId(Long cusDirId) {
        this.cusDirId = cusDirId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    @Override
    public String toString() {
        return "UpCusDirRequest{" +
                "questionId=" + questionId +
                ", cusDirId=" + cusDirId +
                ", systemId=" + systemId +
                ", groupId=" + groupId +
                '}';
    }
}
