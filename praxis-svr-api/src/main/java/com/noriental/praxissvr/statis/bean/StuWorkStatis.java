package com.noriental.praxissvr.statis.bean;

import java.io.Serializable;
import java.util.Date;

public class StuWorkStatis implements Serializable {
    private static final long serialVersionUID = 8478681738129547468L;
    // 数据库字段
    private Long id;
    private Long studentId;
    private Long subjectId;
    private Long moduleId;
    private Long unitId;
    private Long topicId;
    private Double rightNumber;
    private Integer answerNumber;
    private Date createTime;
    private Date lastUpdateTime;
    // 数据库字段

    // 业务字段
    private Integer level;
//    private List<StuWorkStatisQues> StuWorkStatisQues;


    // 业务字段

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }


    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public Long getModuleId() {
        return moduleId;
    }

    public void setModuleId(Long moduleId) {
        this.moduleId = moduleId;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    public Double getRightNumber() {
        return rightNumber;
    }

    public void setRightNumber(Double rightNumber) {
        this.rightNumber = rightNumber;
    }

    public Integer getAnswerNumber() {
        return answerNumber;
    }

    public void setAnswerNumber(Integer answerNumber) {
        this.answerNumber = answerNumber;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getLevel() {
        return level;
    }

//    public void setStuWorkStatisQues(List<StuWorkStatisQues> stuWorkStatisQues) {
//        StuWorkStatisQues = stuWorkStatisQues;
//    }
//
//    public List<StuWorkStatisQues> getStuWorkStatisQues() {
//        return StuWorkStatisQues;
//    }

}
