package com.noriental.praxissvr.question.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by hushuang on 2016/11/21.
 * 封装返回习题
 */
public class QuestionData implements Serializable{

    private Object html;//含html的json习题内容

    private List<Map<String,Object>> topics;//主题
    private List<Map<String,Object>> chapterInfo;//章节
    private long versionId; //教材版本ID
    //private long directoryId;  //教材分册ID  在 chapterInfo 里面
    private int level;//难易程度
    private TypeHtml type;//题型
    private long groupId;//自定义目录体系ID
    private List<Map<String,Object>> customerDirectory; //自定义目录
    private long structId;//题型结构ID
    private String source;//习题来源
    private Date updateTime;//更新时间
    private Date createTime;//创建时间
    private int intelligent;//是否支持智能批改：0-否；1-是
    private Long uploadId;
    //题目所属专题信息
    private List<Map<String,Object>> specialInfo;

    public QuestionData() {
    }

    public List<Map<String, Object>> getSpecialInfo() {
        return specialInfo;
    }

    public void setSpecialInfo(List<Map<String, Object>> specialInfo) {
        this.specialInfo = specialInfo;
    }

    public long getVersionId() {
        return versionId;
    }

    public void setVersionId(long versionId) {
        this.versionId = versionId;
    }

    public Object getHtml() {
        return html;
    }

    public void setHtml(Object html) {
        this.html = html;
    }

    public List<Map<String, Object>> getTopics() {
        return topics;
    }

    public void setTopics(List<Map<String, Object>> topics) {
        this.topics = topics;
    }

    public List<Map<String, Object>> getChapterInfo() {
        return chapterInfo;
    }

    public void setChapterInfo(List<Map<String, Object>> chapterInfo) {
        this.chapterInfo = chapterInfo;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public TypeHtml getType() {
        return type;
    }

    public void setType(TypeHtml type) {
        this.type = type;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public List<Map<String, Object>> getCustomerDirectory() {
        return customerDirectory;
    }

    public void setCustomerDirectory(List<Map<String, Object>> customerDirectory) {
        this.customerDirectory = customerDirectory;
    }

    public long getStructId() {
        return structId;
    }

    public void setStructId(long structId) {
        this.structId = structId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getIntelligent() {
        return intelligent;
    }

    public void setIntelligent(int intelligent) {
        this.intelligent = intelligent;
    }

    public Long getUploadId() {
        return uploadId;
    }

    public void setUploadId(Long uploadId) {
        this.uploadId = uploadId;
    }

    @Override
    public String toString() {
        return "QuestionData{" +
                "html=" + html +
                ", topics=" + topics +
                ", chapterInfo=" + chapterInfo +
                ", versionId=" + versionId +
                ", level=" + level +
                ", type=" + type +
                ", groupId=" + groupId +
                ", customerDirectory=" + customerDirectory +
                ", structId=" + structId +
                ", source='" + source + '\'' +
                ", updateTime=" + updateTime +
                ", createTime=" + createTime +
                ", intelligent=" + intelligent +
                ", uploadId=" + uploadId +
                ", specialInfo=" + specialInfo +
                '}';
    }
}
