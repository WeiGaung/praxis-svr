package com.noriental.praxissvr.question.request;

import com.noriental.validate.bean.BaseRequest;

import java.util.Date;
import java.util.List;

/**
 * Created by hushuang on 2017/3/6.
 */
public class QuestionContinuedRequest extends BaseRequest {

    private int typeId;//题型ID
    private String typeName;//题型名称
    private int src;
    private long group;
    private int uploadSrc;//区分上传人来源： 1运营平台 2教师空间-公立教师 3教师空间-私立教师
    private int level;//难易程度
    private List<Long> topicIds;//主题
    private long subjectId;//科目ID
    private String html;//含html的json习题内容
    private int ref;//0没有外部引用；1有外部引用
    private long uploadId;//上传人ID
    private String source;//习题来源
    private long chapterId;//章节
    private long orgId;//题目创建机构id
    private int orgType;
    private int new_format;//是否是新题
    private Date createTime;//创建时间
    private String state; //题目状态
    private int intelligent;  //智能批改

    public int getIntelligent() {
        return intelligent;
    }

    public void setIntelligent(int intelligent) {
        this.intelligent = intelligent;
    }

    public QuestionContinuedRequest() {
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public int getSrc() {
        return src;
    }

    public void setSrc(int src) {
        this.src = src;
    }

    public int getUploadSrc() {
        return uploadSrc;
    }

    public void setUploadSrc(int uploadSrc) {
        this.uploadSrc = uploadSrc;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public List<Long> getTopicIds() {
        return topicIds;
    }

    public void setTopicIds(List<Long> topicIds) {
        this.topicIds = topicIds;
    }

    public long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(long subjectId) {
        this.subjectId = subjectId;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public int getRef() {
        return ref;
    }

    public void setRef(int ref) {
        this.ref = ref;
    }

    public long getUploadId() {
        return uploadId;
    }

    public void setUploadId(long uploadId) {
        this.uploadId = uploadId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public long getChapterId() {
        return chapterId;
    }

    public void setChapterId(long chapterId) {
        this.chapterId = chapterId;
    }

    public long getOrgId() {
        return orgId;
    }

    public void setOrgId(long orgId) {
        this.orgId = orgId;
    }

    public int getOrgType() {
        return orgType;
    }

    public void setOrgType(int orgType) {
        this.orgType = orgType;
    }

    public int getNew_format() {
        return new_format;
    }

    public void setNew_format(int new_format) {
        this.new_format = new_format;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public long getGroup() {
        return group;
    }

    public void setGroup(long group) {
        this.group = group;
    }

    @Override
    public String toString() {
        return "QuestionContinuedRequest{" +
                "typeId=" + typeId +
                ", typeName='" + typeName + '\'' +
                ", src=" + src +
                ", group=" + group +
                ", uploadSrc=" + uploadSrc +
                ", level=" + level +
                ", topicIds=" + topicIds +
                ", subjectId=" + subjectId +
                ", html='" + html + '\'' +
                ", ref=" + ref +
                ", uploadId=" + uploadId +
                ", source='" + source + '\'' +
                ", chapterId=" + chapterId +
                ", orgId=" + orgId +
                ", orgType=" + orgType +
                ", new_format=" + new_format +
                ", createTime=" + createTime +
                ", state='" + state + '\'' +
                ", intelligent=" + intelligent +
                '}';
    }
}
