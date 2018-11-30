package com.noriental.praxissvr.question.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by hushuang on 2017/8/10.
 */
public class LinkCusQuesRes implements Serializable {

    /*
        用户ID
     */
    private Long systemId;
    /*
        自定义目录体系ID
     */
    private Long groupId;
    /*
        自定义目录ID
     */
    private Long customListId;
    /*
        是否收藏 1：收藏 0：没有收藏
     */
    private Integer isFav;
    /*
        题目ID
     */
    private Long questionId;
    /*
        题目状态 0 ：停用 1：开启
     */
    private Integer resourceStatus;
    /*
        创建时间
     */
    private Date createTime;

    public LinkCusQuesRes() {
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

    @Override
    public String toString() {
        return "LinkCusQuesRes{" +
                "systemId=" + systemId +
                ", groupId=" + groupId +
                ", customListId=" + customListId +
                ", isFav=" + isFav +
                ", questionId=" + questionId +
                ", resourceStatus=" + resourceStatus +
                ", createTime=" + createTime +
                '}';
    }
}
