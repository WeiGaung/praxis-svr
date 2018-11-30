package com.noriental.praxissvr.statis.bean;

import java.util.List;

public class DataKnowledgeVo extends DataWrongQues{
    private List<Long> classIdList;
    private Long subjectId;
    private Integer knowledgeLevel;
    private Long parKnowledgeId;

    public DataKnowledgeVo() {
    }

    public DataKnowledgeVo(List<Long> classIdList, Long subjectId, Integer knowledgeLevel, Long parKnowledgeId) {
        this.classIdList = classIdList;
        this.subjectId = subjectId;
        this.knowledgeLevel = knowledgeLevel;
        this.parKnowledgeId = parKnowledgeId;
    }

    public List<Long> getClassIdList() {
        return classIdList;
    }

    public void setClassIdList(List<Long> classIdList) {
        this.classIdList = classIdList;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public Integer getKnowledgeLevel() {
        return knowledgeLevel;
    }

    public void setKnowledgeLevel(Integer knowledgeLevel) {
        this.knowledgeLevel = knowledgeLevel;
    }

    public Long getParKnowledgeId() {
        return parKnowledgeId;
    }

    public void setParKnowledgeId(Long parKnowledgeId) {
        this.parKnowledgeId = parKnowledgeId;
    }
}
