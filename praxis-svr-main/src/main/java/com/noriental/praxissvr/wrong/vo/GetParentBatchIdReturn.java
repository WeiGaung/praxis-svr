package com.noriental.praxissvr.wrong.vo;

/**
 * Created by bluesky on 2016/7/6.
 */
public class GetParentBatchIdReturn {
    private Long classId;
    private Long resourceId;
    private Long courseId;

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }
}
