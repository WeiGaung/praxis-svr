package com.noriental.praxissvr.answer.request;

import com.noriental.validate.bean.BaseRequest;

import javax.validation.constraints.NotNull;

/***
 * 智能批注实体
 */
public class IntellPostilRequest extends BaseRequest {

    //答题场景
    @NotNull
    private String exerciseSource;
    //同步习题id / 题集发布id / 刷题批次id
    @NotNull
    private Long resourceId;
    //题目id 必填。大题id会返回所有小题id结果
    @NotNull
    private Long questionId;

    @NotNull
    private Long studentId;

    private String status="0";

    private Integer structId;


    public String getExerciseSource() {
        return exerciseSource;
    }

    public void setExerciseSource(String exerciseSource) {
        this.exerciseSource = exerciseSource;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getStructId() {
        return structId;
    }

    public void setStructId(Integer structId) {
        this.structId = structId;
    }
}
