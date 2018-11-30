package com.noriental.praxissvr.question.request;

import com.noriental.praxissvr.common.QuestionTypeEnum;
import com.noriental.validate.bean.BaseRequest;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by hushuang on 2016/11/21.
 * 更新习题的请求
 */
public class UpdateQuestionRequest extends BaseRequest {

    private long id;//习题ID
    @NotBlank
    private String html;//含html的json习题内容
    private List<Long> topicIds;//主题
    private long chapterId;//章节
    @Min(1)@Max(4)
    private int level;//难易程度
    @NotNull
    private int type;//题型
    @Min(0)
    private long groupId; //自定义目录体系ID
    @Min(0)
    private Long customerDirectory;//自定义目录
    @Min(0)@Max(1)
    private int ref;//0没有外部引用；1有外部引用
    @Min(1)
    private long subjectId;//科目ID
    @Min(1)
    private long uploadId;//上传人ID
    @Min(1)@Max(3)
    private int uploadSrc;//区分上传人来源： 1运营平台 2教师空间-公立教师 3教师空间-私立教师
    @Min(0)
    private int newFormat;//新题标志
    private String source;//习题来源
    private long orgId;//题目创建机构id
    private int orgType; //创建机构类型

    //题目关联的专题ID 一个题目可以关联多个专题
    private List<Long> specials;

    public UpdateQuestionRequest() {
    }

    public List<Long> getSpecials() {
        return specials;
    }

    public void setSpecials(List<Long> specials) {
        this.specials = specials;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public List<Long> getTopicIds() {
        return topicIds;
    }

    public void setTopicIds(List<Long> topicIds) {
        this.topicIds = topicIds;
    }

    public long getChapterId() {
        return chapterId;
    }

    public void setChapterId(long chapterId) {
        this.chapterId = chapterId;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public Long getCustomerDirectory() {
        return customerDirectory;
    }

    public void setCustomerDirectory(Long customerDirectory) {
        this.customerDirectory = customerDirectory;
    }

    public int getRef() {
        return ref;
    }

    public void setRef(int ref) {
        this.ref = ref;
    }

    public long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(long subjectId) {
        this.subjectId = subjectId;
    }

    public long getUploadId() {
        return uploadId;
    }

    public void setUploadId(long uploadId) {
        this.uploadId = uploadId;
    }

    public int getUploadSrc() {
        return uploadSrc;
    }

    public void setUploadSrc(int uploadSrc) {
        this.uploadSrc = uploadSrc;
    }

    public int getNewFormat() {
        return newFormat;
    }

    public void setNewFormat(int newFormat) {
        this.newFormat = newFormat;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
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

    @Override
    public String toString() {
        return "UpdateQuestionRequest{" +
                "id=" + id +
                ", html='" + html + '\'' +
                ", topicIds=" + topicIds +
                ", chapterId=" + chapterId +
                ", level=" + level +
                ", type=" + type +
                ", groupId=" + groupId +
                ", customerDirectory=" + customerDirectory +
                ", ref=" + ref +
                ", subjectId=" + subjectId +
                ", uploadId=" + uploadId +
                ", uploadSrc=" + uploadSrc +
                ", newFormat=" + newFormat +
                ", source='" + source + '\'' +
                ", orgId=" + orgId +
                ", orgType=" + orgType +
                ", specials=" + specials +
                '}';
    }
}
