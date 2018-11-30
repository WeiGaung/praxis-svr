package com.noriental.praxissvr.wrongQuestion.bean;

import java.io.Serializable;

/**
 * Created by kate on 2016/12/16.
 */
public class WrongQuesSubjectStatisEntity implements Serializable
{
    /***
     * 学科ID
     */
    private Long subjectId;
    /***
     * 学科名称
     */
    private String subjectName;
    /***
     * 错题个数
     */
    private int questionNum;
    /***
     * 学科图片
     */
    private String iconurlWrong;

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public int getQuestionNum() {
        return questionNum;
    }

    public void setQuestionNum(int questionNum) {
        this.questionNum = questionNum;
    }

    public String getIconurlWrong() {
        return iconurlWrong;
    }

    public void setIconurlWrong(String iconurlWrong) {
        this.iconurlWrong = iconurlWrong;
    }
}
