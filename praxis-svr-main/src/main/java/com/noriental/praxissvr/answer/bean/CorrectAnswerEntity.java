package com.noriental.praxissvr.answer.bean;

import java.io.Serializable;

/**
 * @author kate
 * @create 2017-12-20 15:52
 * @desc 学生做答、老师批改实体类
 **/
public class CorrectAnswerEntity implements Serializable {
    private StudentExercise newRecord;
    private StudentExercise exitRecord;
    private String traceKey;
    private OperateType operateType;

    public CorrectAnswerEntity(StudentExercise newRecord, StudentExercise exitRecord, OperateType operateType, String traceKey) {
        this.newRecord = newRecord;
        this.exitRecord = exitRecord;
        this.operateType = operateType;
        this.traceKey = traceKey;
    }

    public String getTraceKey() {
        return traceKey;
    }

    public void setTraceKey(String traceKey) {
        this.traceKey = traceKey;
    }

    public StudentExercise getNewRecord() {
        return newRecord;
    }

    public void setNewRecord(StudentExercise newRecord) {
        this.newRecord = newRecord;
    }

    public StudentExercise getExitRecord() {
        return exitRecord;
    }

    public void setExitRecord(StudentExercise exitRecord) {
        this.exitRecord = exitRecord;
    }

    public OperateType getOperateType() {
        return operateType;
    }

    public void setOperateType(OperateType operateType) {
        this.operateType = operateType;
    }
}
