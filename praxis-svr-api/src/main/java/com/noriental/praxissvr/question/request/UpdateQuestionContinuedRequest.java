package com.noriental.praxissvr.question.request;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by hushuang on 2017/5/31.
 */
public class UpdateQuestionContinuedRequest {

    private Long id;//习题ID
    @NotBlank
    private String html;//含html的json习题内容

    private String json;

    private List<Long> topicIds;//主题

    private Long chapterId;//章节
    @Min(1)@Max(4)
    private Integer level;//难易程度
    @NotNull
    private String type;//题型
    @Min(0)
    private Long group;//组ID
    @Min(0)@Max(1)
    private Integer ref;//0没有外部引用；1有外部引用
    @Min(1)
    private Long subjectId;//科目ID
    @Min(1)
    private Long uploadId;//上传人ID
    @Min(1)@Max(3)
    private Integer uploadSrc;//区分上传人来源： 1运营平台 2教师空间-公立教师 3教师空间-私立教师
    @Min(0)
    private Integer newFormat;//新题标志

    private String source;//习题来源

    private Long orgId;//题目创建机构id
    private Integer orgType;

    private String state; //题目状态
    private int intelligent;//智能批改

    @Override
    public String toString() {
        return "UpdateQuestionContinuedRequest{" +
                "id=" + id +
                ", html='" + html + '\'' +
                ", json='" + json + '\'' +
                ", topicIds=" + topicIds +
                ", chapterId=" + chapterId +
                ", level=" + level +
                ", type='" + type + '\'' +
                ", group=" + group +
                ", ref=" + ref +
                ", subjectId=" + subjectId +
                ", uploadId=" + uploadId +
                ", uploadSrc=" + uploadSrc +
                ", newFormat=" + newFormat +
                ", source='" + source + '\'' +
                ", orgId=" + orgId +
                ", orgType=" + orgType +
                ", state='" + state + '\'' +
                ", intelligent=" + intelligent +
                '}';
    }

    public int getIntelligent() {
        return intelligent;
    }

    public void setIntelligent(int intelligent) {
        this.intelligent = intelligent;
    }

    public UpdateQuestionContinuedRequest() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public List<Long> getTopicIds() {
        return topicIds;
    }

    public void setTopicIds(List<Long> topicIds) {
        this.topicIds = topicIds;
    }

    public Long getChapterId() {
        return chapterId;
    }

    public void setChapterId(Long chapterId) {
        this.chapterId = chapterId;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getGroup() {
        return group;
    }

    public void setGroup(Long group) {
        this.group = group;
    }

    public Integer getRef() {
        return ref;
    }

    public void setRef(Integer ref) {
        this.ref = ref;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public Long getUploadId() {
        return uploadId;
    }

    public void setUploadId(Long uploadId) {
        this.uploadId = uploadId;
    }

    public Integer getUploadSrc() {
        return uploadSrc;
    }

    public void setUploadSrc(Integer uploadSrc) {
        this.uploadSrc = uploadSrc;
    }

    public Integer getNewFormat() {
        return newFormat;
    }

    public void setNewFormat(Integer newFormat) {
        this.newFormat = newFormat;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public Integer getOrgType() {
        return orgType;
    }

    public void setOrgType(Integer orgType) {
        this.orgType = orgType;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

}
