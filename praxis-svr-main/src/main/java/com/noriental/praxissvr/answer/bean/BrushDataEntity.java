package com.noriental.praxissvr.answer.bean;

import java.util.Date;

/**
 * @author kate
 * @create 2018-01-26 11:52
 * @desc 刷题数据流转到cms
 **/
public class BrushDataEntity {

    private Long id;
    private String exerciseSource;
    private Long resourceId;
    private Long studentId;
    private Integer busFlag=0;
    private Date createTime=new Date();
    private Date updateTime=new Date();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExerciseSource() {
        return exerciseSource;
    }

    public void setExerciseSource(String exerciseSource) {
        this.exerciseSource = exerciseSource;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Integer getBusFlag() {
        return busFlag;
    }

    public void setBusFlag(Integer busFlag) {
        this.busFlag = busFlag;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
