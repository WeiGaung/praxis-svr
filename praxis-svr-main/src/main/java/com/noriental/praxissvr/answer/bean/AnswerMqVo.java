package com.noriental.praxissvr.answer.bean;

import com.noriental.validate.bean.MQRequest;

import java.util.List;

public class AnswerMqVo extends MQRequest {
    private StudentExercise studentExercise;
    protected List<StudentExercise> studentExerciseList;
    private OperateType operateType;
    private Integer counts;

    public StudentExercise getStudentExercise() {
        return studentExercise;
    }

    public void setStudentExercise(StudentExercise studentExercise) {
        this.studentExercise = studentExercise;
    }

    public List<StudentExercise> getStudentExerciseList() {
        return studentExerciseList;
    }

    public void setStudentExerciseList(List<StudentExercise> studentExerciseList) {
        this.studentExerciseList = studentExerciseList;
    }

    public OperateType getOperateType() {
        return operateType;
    }

    public void setOperateType(OperateType operateType) {
        this.operateType = operateType;
    }

    public Integer getCounts() {
        return counts;
    }

    public void setCounts(Integer counts) {
        this.counts = counts;
    }
}
