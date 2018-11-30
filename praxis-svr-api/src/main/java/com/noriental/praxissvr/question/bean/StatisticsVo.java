package com.noriental.praxissvr.question.bean;


import com.noriental.publicsvr.bean.Grade;
import com.noriental.publicsvr.bean.Subject;

import java.io.Serializable;

public class StatisticsVo implements Serializable {

    public Long id;

    public Long topicId;

    public Long seriesId;
    /**
     * 上传人 和 审核人
     **/
    private String operator;

    /**
     * 上传试卷数量 和 审核试卷数量
     **/
    private int paperCount;

    /**
     * 上传题目数量 和 审核题目数量
     **/
    private int questionCount;
    /**
     * 小题数量
     **/
    private int smQuestionCount;

    /**
     * 报错题目数量
     **/
    private int errorQuestionCount;

    /**
     * 已解决数量
     **/
    private int solvedCount;

    /**
     * 解决率
     **/
    private double solved;

    /**
     * 学段
     **/
    private String paperGrade;

    private Grade grade;

    /**
     * 学科
     **/
    private String paperSubject;

    private Subject subject;

    /**
     * 待审核题目数量
     **/
    private int unevaledCount;
    private int smUnevaledCount;
    /**
     * 已审核
     **/
    private int evaledCount;
    private int smEvaledCount;

    /**
     * 启用题目数量
     **/
    private int enabledCount;
    /**
     * 启动小题数量
     **/
    private int smEnabledCount;

    /**
     * 停用题目数量
     **/
    private int disableCount;
    /**
     * 停用小题题目数量
     **/
    private int smDisableCount;

    /**
     * 模块
     **/
    private String moduleName;
    /**
     * 单元
     **/
    private String unitName;
    /**
     * 主题
     **/
    private String topicName;
    /**
     * 专题
     **/
    private String seriesName;
    /**
     * 难度
     **/
    private int difficulty;
    /**
     * 题型
     **/
    private String questionType;

    private Long moduleId;

    private Long unitId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public int getPaperCount() {
        return paperCount;
    }

    public void setPaperCount(int paperCount) {
        this.paperCount = paperCount;
    }

    public int getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(int questionCount) {
        this.questionCount = questionCount;
    }

    public int getSmQuestionCount() {
        return smQuestionCount;
    }

    public void setSmQuestionCount(int smQuestionCount) {
        this.smQuestionCount = smQuestionCount;
    }

    public int getErrorQuestionCount() {
        return errorQuestionCount;
    }

    public void setErrorQuestionCount(int errorQuestionCount) {
        this.errorQuestionCount = errorQuestionCount;
    }

    public int getSolvedCount() {
        return solvedCount;
    }

    public void setSolvedCount(int solvedCount) {
        this.solvedCount = solvedCount;
    }

    public String getPaperGrade() {
        return paperGrade;
    }

    public void setPaperGrade(String paperGrade) {
        this.paperGrade = paperGrade;
    }

    public String getPaperSubject() {
        return paperSubject;
    }

    public void setPaperSubject(String paperSubject) {
        this.paperSubject = paperSubject;
    }

    public int getUnevaledCount() {
        return unevaledCount;
    }

    public void setUnevaledCount(int unevaledCount) {
        this.unevaledCount = unevaledCount;
    }

    public int getEnabledCount() {
        return enabledCount;
    }

    public void setEnabledCount(int enabledCount) {
        this.enabledCount = enabledCount;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
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

    public double getSolved() {
        return solved;
    }

    public void setSolved(double solved) {
        this.solved = solved;
    }

    public Grade getGrade() {
        return grade;
    }

    public void setGrade(Grade grade) {
        this.grade = grade;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    public Long getSeriesId() {
        return seriesId;
    }

    public void setSeriesId(Long seriesId) {
        this.seriesId = seriesId;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public int getSmUnevaledCount() {
        return smUnevaledCount;
    }

    public void setSmUnevaledCount(int smUnevaledCount) {
        this.smUnevaledCount = smUnevaledCount;
    }

    public int getEvaledCount() {
        return evaledCount;
    }

    public void setEvaledCount(int evaledCount) {
        this.evaledCount = evaledCount;
    }

    public int getSmEvaledCount() {
        return smEvaledCount;
    }

    public void setSmEvaledCount(int smEvaledCount) {
        this.smEvaledCount = smEvaledCount;
    }

    public int getSmEnabledCount() {
        return smEnabledCount;
    }

    public void setSmEnabledCount(int smEnabledCount) {
        this.smEnabledCount = smEnabledCount;
    }

    public int getDisableCount() {
        return disableCount;
    }

    public void setDisableCount(int disableCount) {
        this.disableCount = disableCount;
    }

    public int getSmDisableCount() {
        return smDisableCount;
    }

    public void setSmDisableCount(int smDisableCount) {
        this.smDisableCount = smDisableCount;
    }

    public Long getModuleId() {
        return moduleId;
    }

    public void setModuleId(Long moduleId) {
        this.moduleId = moduleId;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }


}
