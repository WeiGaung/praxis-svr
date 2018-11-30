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
 * 上传习题的请求
 */
public class UploadQuestionRequest extends BaseRequest {

    @Min(0)
    private Long id;
    @NotBlank
    private String html;//含html的json习题内容
    //主题章节不能同时为空
    private List<Long> topicIds;//知识点
    //主题章节不能同时为空
    private long chapterId;//章节
    @Min(1)@Max(4)
    private int level;//难易程度
    @NotNull
    private int type;//题型
    @NotNull
    private Long groupId; //自定义目录体系ID
    @Min(0)
    private long customerDirectory;//自定义目录ID
    @Min(0)@Max(1)
    private int ref;//0没有外部引用；1有外部引用
    @Min(1)
    private long subjectId;//科目ID
    @Min(1)
    private long uploadId;//上传人ID
    @Min(1)@Max(3)
    private int uploadSrc;//区分上传人来源： 1运营平台 2教师空间-公立教师 3教师空间-私立教师
    private String source;//习题来源
    private long orgId;//创建机构id
    private int orgType;//创建机构类型
    private String statue;//题目状态

    //题目关联的专题ID 一个题目可以关联多个专题
    private List<Long> specials;

    public UploadQuestionRequest() {
    }

    public List<Long> getSpecials() {
        return specials;
    }

    public void setSpecials(List<Long> specials) {
        this.specials = specials;
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

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
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

    public String getStatue() {
        return statue;
    }

    public void setStatue(String statue) {
        this.statue = statue;
    }

    public long getCustomerDirectory() {
        return customerDirectory;
    }

    public void setCustomerDirectory(long customerDirectory) {
        this.customerDirectory = customerDirectory;
    }

    @Override
    public String toString() {
        return "UploadQuestionRequest{" +
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
                ", source='" + source + '\'' +
                ", orgId=" + orgId +
                ", orgType=" + orgType +
                ", statue='" + statue + '\'' +
                ", specials=" + specials +
                '}';
    }
}
