package com.noriental.praxissvr.question.bean;

import java.util.Date;

/**
 * Created by hushuang on 2016/12/1.
 */
public class LinkTeachingChapterKnowledge {


    private Long id;
    private int type;//类型，1为主题，2为专题
    private long topicId;//主题id，指向表t_topic
    private long seriesId;//专题id，指向表t_series
    private String topicName;//主题名称，指向表t_topic
    private String seriesName;//专题名称，指向表series
    private Date createTime;
    private long chapterId;//章节id，指向表t_teaching_chapter

    public LinkTeachingChapterKnowledge() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getTopicId() {
        return topicId;
    }

    public void setTopicId(long topicId) {
        this.topicId = topicId;
    }

    public long getSeriesId() {
        return seriesId;
    }

    public void setSeriesId(long seriesId) {
        this.seriesId = seriesId;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getSeriesName() {
        return seriesName;
    }

    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public long getChapterId() {
        return chapterId;
    }

    public void setChapterId(long chapterId) {
        this.chapterId = chapterId;
    }

    @Override
    public String toString() {
        return "LinkTeachingChapterKnowledge{" +
                "id=" + id +
                ", type=" + type +
                ", topicId=" + topicId +
                ", seriesId=" + seriesId +
                ", topicName='" + topicName + '\'' +
                ", seriesName='" + seriesName + '\'' +
                ", createTime=" + createTime +
                ", chapterId=" + chapterId +
                '}';
    }
}
