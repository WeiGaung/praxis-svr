package com.noriental.praxissvr.statis.bean;


import com.noriental.praxissvr.brush.bean.StudentWork;

import java.io.Serializable;

/**
 * 作业统计
 *
 * @author shengxian.xiao
 */
public class AnswAndResultSatis implements Serializable {
    private static final long serialVersionUID = 87366452695857714L;
    private Long studentId;
    private Integer stuType;
    private Long subjectId;
    private Long moduleId;
    private Long unitId;
    private Long topicId;
    private Double rightNumber;
    private Integer answerNumber;
    private Integer level;

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getLevel() {
        return level;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Integer getStuType() {
        return stuType;
    }

    public void setStuType(Integer stuType) {
        this.stuType = stuType;
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

    @Override
    public int hashCode() {
        if (StudentWork.WorkLevel.TOPIC.equals(this.level)) {
            return StudentWork.WorkLevel.TOPIC | this.topicId.hashCode() << 2;
        } else if (StudentWork.WorkLevel.UNIT.equals(this.level)) {
            return StudentWork.WorkLevel.UNIT | this.unitId.hashCode() << 2;
        } else if (StudentWork.WorkLevel.MODULE.equals(this.level)) {
            return StudentWork.WorkLevel.MODULE | this.moduleId.hashCode() << 2;
        }
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof AnswAndResultSatis)) {
            return false;
        }
        AnswAndResultSatis a1 = (AnswAndResultSatis) obj;
        Integer level1 = a1.getLevel();
        if (level1 != null && level1.equals(level)) {
            if (level1.equals(StudentWork.WorkLevel.TOPIC)) {
                return this.getTopicId().equals(a1.getTopicId());
            } else if (level1.equals(StudentWork.WorkLevel.UNIT)) {
                return this.getUnitId().equals(a1.getUnitId());
            } else if (level1.equals(StudentWork.WorkLevel.MODULE)) {
                return this.getModuleId().equals(a1.getModuleId());
            }
        }

        return false;

    }

}
