package com.noriental.praxissvr.brush.bean;

import java.util.List;

/**
 * 根据学生提交的答案或批改信息，组合成的中间结果。
 *
 * @author shengxian.xiao
 */
public class StudentWorkAnswer {
    private Long studentId;
    //题目id：单题或者大题
    private Long questionId;
    //批改结果，提交答案客观题有该值。批改也有该值
    private String result;

    //填空题多个空的批改结果
    private String blankResult;
    //题型
    private String questionType;
    private Integer structId;

    //该题目直接关联的知识点
    private List<Long> topicIds;
    //学生答案
    private String submitAnswer;
    //子题答案信息（如果有）
    private List<StudentWorkAnswer> subQuesAnswers;
    //体系类型
    private Integer resourceType;
    //该题目直接关联的知识点-单元
    private List<Long> unitIds;
    //该题目直接关联的知识点-模块
    private List<Long> moduleIds;
    //大题id（如果有）
    private Long parentQuestionId;

    //填空题空个数
    private Integer answerNum;

    public void setParentQuestionId(Long parentQuestionId) {
        this.parentQuestionId = parentQuestionId;
    }

    public Long getParentQuestionId() {
        return parentQuestionId;
    }

    public List<Long> getUnitIds() {
        return unitIds;
    }

    public void setUnitIds(List<Long> unitIds) {
        this.unitIds = unitIds;
    }

    public List<Long> getModuleIds() {
        return moduleIds;
    }

    public void setModuleIds(List<Long> moduleIds) {
        this.moduleIds = moduleIds;
    }

    //update module unit statis end-----------------


    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<Long> getTopicIds() {
        return topicIds;
    }

    public void setTopicIds(List<Long> topicIds) {
        this.topicIds = topicIds;
    }

    public List<StudentWorkAnswer> getSubQuesAnswers() {
        return subQuesAnswers;
    }

    public void setSubQuesAnswers(List<StudentWorkAnswer> subQuesAnswers) {
        this.subQuesAnswers = subQuesAnswers;
    }

    public void setSubmitAnswer(String submitAnswer) {
        this.submitAnswer = submitAnswer;
    }

    public String getSubmitAnswer() {
        return submitAnswer;
    }


    public String getBlankResult() {
        return blankResult;
    }

    public void setBlankResult(String blankResult) {
        this.blankResult = blankResult;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public void setResourceType(Integer resourceType) {
        this.resourceType = resourceType;
    }

    public Integer getResourceType() {
        return resourceType;
    }

    @Override
    public int hashCode() {
        return questionId == null ? 0 : questionId.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof StudentWorkAnswer)) {
            return false;
        }
        StudentWorkAnswer s = (StudentWorkAnswer) obj;
        return this.getQuestionId() != null && this.getQuestionId().equals(s.getQuestionId());
    }

    public void setStructId(Integer structId) {
        this.structId = structId;
    }

    public Integer getStructId() {
        return structId;
    }

    public Integer getAnswerNum() {
        return answerNum;
    }

    public void setAnswerNum(Integer answerNum) {
        this.answerNum = answerNum;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }
}
