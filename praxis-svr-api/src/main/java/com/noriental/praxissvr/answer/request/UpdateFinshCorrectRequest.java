package com.noriental.praxissvr.answer.request;

import com.noriental.praxissvr.answer.bean.ExerciseTypeEnum;
import com.noriental.validate.bean.BaseRequest;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class UpdateFinshCorrectRequest extends BaseRequest {
    @NotNull
    private ExerciseTypeEnum exerciseTypeEnum;
    @NotNull
    private Long linkId;
    @NotNull
    private Long studentId;
    @Min(0)
    private int rightCount;
    @Min(1)
    private int totalCount;
    //包含主观题
    @NotNull
    private boolean isContain;
    //老师批改
    @NotNull
    private boolean isTeacher;

    public UpdateFinshCorrectRequest(ExerciseTypeEnum exerciseTypeEnum, Long linkId, Long studentId) {
        this.exerciseTypeEnum = exerciseTypeEnum;
        this.linkId = linkId;
        this.studentId = studentId;
    }

    public UpdateFinshCorrectRequest() {
    }

    public ExerciseTypeEnum getExerciseTypeEnum() {
        return exerciseTypeEnum;
    }

    public void setExerciseTypeEnum(ExerciseTypeEnum exerciseTypeEnum) {
        this.exerciseTypeEnum = exerciseTypeEnum;
    }

    public Long getLinkId() {
        return linkId;
    }

    public void setLinkId(Long linkId) {
        this.linkId = linkId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public int getRightCount() {
        return rightCount;
    }

    public void setRightCount(int rightCount) {
        this.rightCount = rightCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public boolean isContain() {
        return isContain;
    }

    public void setContain(boolean contain) {
        isContain = contain;
    }

    public boolean isTeacher() {
        return isTeacher;
    }

    public void setTeacher(boolean teacher) {
        isTeacher = teacher;
    }
}
