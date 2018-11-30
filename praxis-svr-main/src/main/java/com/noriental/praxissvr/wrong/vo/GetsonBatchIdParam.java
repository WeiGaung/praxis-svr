package com.noriental.praxissvr.wrong.vo;

/**
 * Created by bluesky on 2016/7/6.
 */
public class GetsonBatchIdParam {
    private Long studentId;
    private Long questionId;
    private String exerciseSource;
    private Long resourceId;

    public GetsonBatchIdParam() {
    }

    public GetsonBatchIdParam(Long studentId, Long questionId, String exerciseSource, Long resourceId) {
        this.studentId = studentId;
        this.questionId = questionId;
        this.exerciseSource = exerciseSource;
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

}
