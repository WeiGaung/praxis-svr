package com.noriental.praxissvr.statis.request;

import com.noriental.praxissvr.statis.bean.WrongQuesSortType;
import com.noriental.validate.bean.BaseRequest;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

public class FindKnowledgeListRequest extends BaseRequest implements Serializable{
    @NotEmpty
    private List<Long> classIdList;
    @NotNull
    private Long subjectId;
    @Min(1)
    private Integer knowledgeLevel;
    @Min(0)
    private Long parKnowledgeId;

    public FindKnowledgeListRequest() {
    }

    public FindKnowledgeListRequest(List<Long> classIdList, Long subjectId, Integer knowledgeLevel, Long parKnowledgeId) {
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
