package com.noriental.praxissvr.question.bean;

import com.noriental.praxissvr.common.QuestionState;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by hushuang on 2017/4/7.
 */
public class QuestionBean implements Serializable{

    private Long id;
    private Integer countOptions;//选项数量
    private Integer difficulty;//难度 1易 2中 3难 4极难
    private Integer highQual;//是否精品题
    private Integer mastery;//掌握层级 程序中没有使用
    private String multiScoreAnswer;//历史优选题答案 例{"A":10,"B":9,"C":5,"D":1}
    private Long parentQuestionId;//大题id
    private String questionType;//题型
    private String rightOption;//正确选项
    private Integer isSingle;//是否单题 0不是单题，下面有小题 1是单题
    private QuestionState state;//题目状态
    private Long qrId;//
    private Integer countTopic;//关联知识点数
    private Long subjectId;//科目id
    private Date updateTime;//更新时间
    private Integer src;//
    private Date uploadTime;
    private Long uploadId;//上传人id
    private Integer uploadSrc;//区分上传人来源： 1运营平台 2教师空间-公立教师 3教师空间-私立教师
    private Integer newFormat;//新题标志
    private Long questionGroup;//组ID
    private Integer answerNum;//答案个数
    private Integer questionTypeId;//题目类型ID
    private Integer visible;//教师空间是否可见 0-不可见；1-可见
    private String source;//习题来源
    private Long orgId;
    private Integer orgType;
    private Integer intelligent;//是否支持智能批改：0-否；1-是
    private String jsonData;//pad端JSON数据
    private String htmlData;//前端HTML数据

    public QuestionBean() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCountOptions() {
        return countOptions;
    }

    public void setCountOptions(Integer countOptions) {
        this.countOptions = countOptions;
    }

    public Integer getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }

    public Integer getHighQual() {
        return highQual;
    }

    public void setHighQual(Integer highQual) {
        this.highQual = highQual;
    }

    public Integer getMastery() {
        return mastery;
    }

    public void setMastery(Integer mastery) {
        this.mastery = mastery;
    }

    public String getMultiScoreAnswer() {
        return multiScoreAnswer;
    }

    public void setMultiScoreAnswer(String multiScoreAnswer) {
        this.multiScoreAnswer = multiScoreAnswer;
    }

    public Long getParentQuestionId() {
        return parentQuestionId;
    }

    public void setParentQuestionId(Long parentQuestionId) {
        this.parentQuestionId = parentQuestionId;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getRightOption() {
        return rightOption;
    }

    public void setRightOption(String rightOption) {
        this.rightOption = rightOption;
    }

    public Integer getIsSingle() {
        return isSingle;
    }

    public void setIsSingle(Integer isSingle) {
        this.isSingle = isSingle;
    }

    public QuestionState getState() {
        return state;
    }

    public void setState(QuestionState state) {
        this.state = state;
    }

    public Long getQrId() {
        return qrId;
    }

    public void setQrId(Long qrId) {
        this.qrId = qrId;
    }

    public Integer getCountTopic() {
        return countTopic;
    }

    public void setCountTopic(Integer countTopic) {
        this.countTopic = countTopic;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getSrc() {
        return src;
    }

    public void setSrc(Integer src) {
        this.src = src;
    }

    public Date getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
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

    public Long getQuestionGroup() {
        return questionGroup;
    }

    public void setQuestionGroup(Long questionGroup) {
        this.questionGroup = questionGroup;
    }

    public Integer getAnswerNum() {
        return answerNum;
    }

    public void setAnswerNum(Integer answerNum) {
        this.answerNum = answerNum;
    }

    public Integer getQuestionTypeId() {
        return questionTypeId;
    }

    public void setQuestionTypeId(Integer questionTypeId) {
        this.questionTypeId = questionTypeId;
    }

    public Integer getVisible() {
        return visible;
    }

    public void setVisible(Integer visible) {
        this.visible = visible;
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

    public Integer getIntelligent() {
        return intelligent;
    }

    public void setIntelligent(Integer intelligent) {
        this.intelligent = intelligent;
    }

    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }

    public String getHtmlData() {
        return htmlData;
    }

    public void setHtmlData(String htmlData) {
        this.htmlData = htmlData;
    }

    @Override
    public String toString() {
        return "QuestionBean{" +
                "id=" + id +
                ", countOptions=" + countOptions +
                ", difficulty=" + difficulty +
                ", highQual=" + highQual +
                ", mastery=" + mastery +
                ", multiScoreAnswer='" + multiScoreAnswer + '\'' +
                ", parentQuestionId=" + parentQuestionId +
                ", questionType='" + questionType + '\'' +
                ", rightOption='" + rightOption + '\'' +
                ", isSingle=" + isSingle +
                ", state=" + state +
                ", qrId=" + qrId +
                ", countTopic=" + countTopic +
                ", subjectId=" + subjectId +
                ", updateTime=" + updateTime +
                ", src=" + src +
                ", uploadTime=" + uploadTime +
                ", uploadId=" + uploadId +
                ", uploadSrc=" + uploadSrc +
                ", newFormat=" + newFormat +
                ", questionGroup=" + questionGroup +
                ", answerNum=" + answerNum +
                ", questionTypeId=" + questionTypeId +
                ", visible=" + visible +
                ", source='" + source + '\'' +
                ", orgId=" + orgId +
                ", orgType=" + orgType +
                ", intelligent=" + intelligent +
                ", jsonData='" + jsonData + '\'' +
                ", htmlData='" + htmlData + '\'' +
                '}';
    }
}
