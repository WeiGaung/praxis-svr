package com.noriental.praxissvr.wrong.request;

import com.noriental.validate.bean.BaseRequest;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;

public class FindChalQuessRequest extends BaseRequest {
    @Min(1)
    private long studentId;
    @Min(value = 1)
    private long resouceId;
    private long courseId;
    private long classId;
    @NotEmpty
    private String exerciseSource;

    public long getStudentId() {
        return studentId;
    }

    public void setStudentId(long studentId) {
        this.studentId = studentId;
    }

    public long getResouceId() {
        return resouceId;
    }

    public void setResouceId(long resouceId) {
        this.resouceId = resouceId;
    }

    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    public long getClassId() {
        return classId;
    }

    public void setClassId(long classId) {
        this.classId = classId;
    }

    public String getExerciseSource() {
        return exerciseSource;
    }

    public void setExerciseSource(String exerciseSource) {
        this.exerciseSource = exerciseSource;
    }
}
