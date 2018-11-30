package com.noriental.praxissvr.wrong.vo;

/**
 * Created by bluesky on 2016/7/6.
 */
public class GetsonBatchIdsParam {
    private Long studentId;
    private String exerciseSource;
    private Long resourceId;

    public GetsonBatchIdsParam() {
    }

    public GetsonBatchIdsParam(Long studentId, String exerciseSource, Long resourceId) {
        this.studentId = studentId;
        this.exerciseSource = exerciseSource;
        this.resourceId = resourceId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
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
