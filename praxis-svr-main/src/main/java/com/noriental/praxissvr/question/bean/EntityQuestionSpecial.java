package com.noriental.praxissvr.question.bean;

/**
 * Created by liujiang on 2018/4/26.
 */
public class EntityQuestionSpecial {
    //题目ID
    private Long questionId;
    //专题ID
    private Long examSitesId;
    //专题名
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Long getExamSitesId() {
        return examSitesId;
    }

    public void setExamSitesId(Long examSitesId) {
        this.examSitesId = examSitesId;
    }

    @Override
    public String toString() {
        return "EntityQuestionSpecial{" +
                "questionId=" + questionId +
                ", examSitesId=" + examSitesId +
                ", name='" + name + '\'' +
                '}';
    }
}
