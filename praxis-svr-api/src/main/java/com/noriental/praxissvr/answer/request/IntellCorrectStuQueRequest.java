package com.noriental.praxissvr.answer.request;

import com.noriental.validate.bean.BaseRequest;

import javax.validation.constraints.NotNull;

/**
 * Created by kate on 2017/3/13.
 * 使用智能批改请求入参
 *
 *
 */
public class IntellCorrectStuQueRequest extends BaseRequest {

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
    // 批改人ID	必填
    @NotNull
    private Long correctorId;

    //题目结构Id
    @NotNull
    private Integer structId;

    //图片地址
    private String postilUrl;




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

    public Integer getStructId() {
        return structId;
    }

    public void setStructId(Integer structId) {
        this.structId = structId;
    }

    public Long getCorrectorId() {
        return correctorId;
    }

    public void setCorrectorId(Long correctorId) {
        this.correctorId = correctorId;
    }

    public String getPostilUrl() {
        return postilUrl;
    }

    public void setPostilUrl(String postilUrl) {
        this.postilUrl = postilUrl;
    }


}
