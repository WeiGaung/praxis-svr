package com.noriental.praxissvr.question.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by hushuang on 2017/7/28.
 */
public class CusDirQuestion implements Serializable{


    private Long id;
    //上传人ID
    private Long systemId;
    //自定义目录体系ID
    private Long groupId;
    //自定义目录ID
    private Long customListId;
    //是否是收藏的：1-是；0-否
    private Integer isFav;
    //题目ID
    private Long questionId;
    //资源状态 1-启用；0-停用
    private Integer resourceStatus;
    //创建时间
    private Date createTime;
    //更新时间
    private Date updateTime;

    public CusDirQuestion() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSystemId() {
        return systemId;
    }

    public void setSystemId(Long systemId) {
        this.systemId = systemId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long getCustomListId() {
        return customListId;
    }

    public void setCustomListId(Long customListId) {
        this.customListId = customListId;
    }

    public Integer getIsFav() {
        return isFav;
    }

    public void setIsFav(Integer isFav) {
        this.isFav = isFav;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Integer getResourceStatus() {
        return resourceStatus;
    }

    public void setResourceStatus(Integer resourceStatus) {
        this.resourceStatus = resourceStatus;
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

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "CusDirQuestion{" +
                "id=" + id +
                ", systemId=" + systemId +
                ", groupId=" + groupId +
                ", customListId=" + customListId +
                ", isFav=" + isFav +
                ", questionId=" + questionId +
                ", resourceStatus=" + resourceStatus +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
