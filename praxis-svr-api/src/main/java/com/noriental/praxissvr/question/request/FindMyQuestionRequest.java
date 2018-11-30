package com.noriental.praxissvr.question.request;

import com.noriental.praxissvr.common.Difficulty;
import com.noriental.praxissvr.common.PageBaseRequest;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by chenlihua on 2016/12/16.
 * praxis-svr
 */
public class FindMyQuestionRequest extends PageBaseRequest {
    //@Min(1)
    private Long systemId;//老师ID
    //@Min(1)
    private Long subjectId;//学科ID
    @NotNull
    private int questionType;//题目类型

    private Difficulty difficulty; //难易程度

    private Long cusDirId1; //一级目录
    private Long cusDirId2; //二级目录
    private Long cusDirId3; //三级目录

    private Integer isFav;      //是否我收藏的   1代表我收藏的
    private Integer isCreate;   //是否我创建的   1代表我创建的
    private Integer isOffset;   //是否使用偏移量分页    1代表使用偏移量分页

    @Override
    public String toString() {
        return "FindMyQuestionRequest{" +
                "systemId=" + systemId +
                ", subjectId=" + subjectId +
                ", questionType=" + questionType +
                ", difficulty=" + difficulty +
                ", cusDirId1=" + cusDirId1 +
                ", cusDirId2=" + cusDirId2 +
                ", cusDirId3=" + cusDirId3 +
                ", isFav=" + isFav +
                ", isCreate=" + isCreate +
                ", isOffset=" + isOffset +
                ", questionGroupId=" + questionGroupId +
                ", moduleIds=" + moduleIds +
                ", unitIds=" + unitIds +
                ", topicIds=" + topicIds +
                '}';
    }

    public Integer getIsOffset() {
        return isOffset;
    }

    public void setIsOffset(Integer isOffset) {
        this.isOffset = isOffset;
    }

    private Integer questionGroupId;//题目分组ID

    private List<Long> moduleIds;//模块IDs

    private List<Long> unitIds;//单元IDs

    private List<Long> topicIds;//主题IDs

    public Integer getIsFav() {
        return isFav;
    }

    public void setIsFav(Integer isFav) {
        this.isFav = isFav;
    }

    public Integer getIsCreate() {
        return isCreate;
    }

    public void setIsCreate(Integer isCreate) {
        this.isCreate = isCreate;
    }

    public FindMyQuestionRequest() {
    }


    public int getQuestionType() {
        return questionType;
    }

    public void setQuestionType(int questionType) {
        this.questionType = questionType;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public Long getCusDirId1() {
        return cusDirId1;
    }

    public void setCusDirId1(Long cusDirId1) {
        this.cusDirId1 = cusDirId1;
    }

    public Long getCusDirId2() {
        return cusDirId2;
    }

    public void setCusDirId2(Long cusDirId2) {
        this.cusDirId2 = cusDirId2;
    }

    public Long getCusDirId3() {
        return cusDirId3;
    }

    public void setCusDirId3(Long cusDirId3) {
        this.cusDirId3 = cusDirId3;
    }

    public List<Long> getModuleIds() {
        return moduleIds;
    }

    public void setModuleIds(List<Long> moduleIds) {
        this.moduleIds = moduleIds;
    }

    public List<Long> getUnitIds() {
        return unitIds;
    }

    public void setUnitIds(List<Long> unitIds) {
        this.unitIds = unitIds;
    }

    public List<Long> getTopicIds() {
        return topicIds;
    }

    public void setTopicIds(List<Long> topicIds) {
        this.topicIds = topicIds;
    }

    public Long getSystemId() {
        return systemId;
    }

    public void setSystemId(Long systemId) {
        this.systemId = systemId;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public Integer getQuestionGroupId() {
        return questionGroupId;
    }

    public void setQuestionGroupId(Integer questionGroupId) {
        this.questionGroupId = questionGroupId;
    }

}
