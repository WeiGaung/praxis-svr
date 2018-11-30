package com.noriental.praxissvr.statis.bean;

import java.util.List;

public class DataWrongQuesVo extends DataWrongQues{
    private List<Long> knowledgeIdList;
    private List<Long> classIdList;
    private Integer knowledgeLevel;
    private Integer sortType;
    private String sources;
    private Long teacherId;
    private List<Long> schoolIds;

    public List<Long> getSchoolIds() {
        return schoolIds;
    }

    public void setSchoolIds(List<Long> schoolIds) {
        this.schoolIds = schoolIds;
    }

    public DataWrongQuesVo(List<Long> knowledgeIdList, List<Long> classIdList, Integer knowledgeLevel, Integer
            sortType, Integer questionType, Integer difficulty, Long subjectId, List<Long> schoolIds) {
        this.knowledgeIdList = knowledgeIdList;
        this.classIdList = classIdList;
        this.knowledgeLevel = knowledgeLevel;
        this.sortType = sortType;
        this.setQuestion_type(questionType);
        this.setDifficulty(difficulty);
        this.setSubject_id(subjectId);
        //this.setSchool_id(school_id);
       // this.teacherId=teacherId;
        this.schoolIds=schoolIds;
    }

    public DataWrongQuesVo() {
    }

    public List<Long> getKnowledgeIdList() {
        return knowledgeIdList;
    }

    public void setKnowledgeIdList(List<Long> knowledgeIdList) {
        this.knowledgeIdList = knowledgeIdList;
    }

    public List<Long> getClassIdList() {
        return classIdList;
    }

    public void setClassIdList(List<Long> classIdList) {
        this.classIdList = classIdList;
    }

    public Integer getKnowledgeLevel() {
        return knowledgeLevel;
    }

    public void setKnowledgeLevel(Integer knowledgeLevel) {
        this.knowledgeLevel = knowledgeLevel;
    }

    public Integer getSortType() {
        return sortType;
    }

    public void setSortType(Integer sortType) {
        this.sortType = sortType;
    }

    public String getSources() {
        return sources;
    }

    public void setSources(String sources) {
        this.sources = sources;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }
}
