package com.noriental.praxissvr.statis.bean;


import com.noriental.praxissvr.question.bean.QuesitonRecommend;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
@Deprecated
public class StuWrongQuesStatis implements Serializable {

    private static final long serialVersionUID = 6537998703187057529L;

    // 数据库字段
    private Long id;
    private Long studentId;
    private Integer stuType;
    private Long subjectId;
    private Long moduleId;
    private Long unitId;
    private Long topicId;
    private Long questionId;
    private Integer difficulty;
    private Date createTime;
    // 数据库字段

    // 业务字段
    List<QuesitonRecommend> quesitonRecommends;
    private Integer level;
    private int subQuesCount;
    // 业务字段

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
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

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }

    public Integer getDifficulty() {
        return difficulty;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public void setQuesitonRecommends(List<QuesitonRecommend> quesitonRecommends) {
        this.quesitonRecommends = quesitonRecommends;
    }

    public List<QuesitonRecommend> getQuesitonRecommends() {
        return quesitonRecommends;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public void setSubQuesCount(int subQuesCount) {
        this.subQuesCount = subQuesCount;
    }

    public int getSubQuesCount() {
        return subQuesCount;
    }
}
