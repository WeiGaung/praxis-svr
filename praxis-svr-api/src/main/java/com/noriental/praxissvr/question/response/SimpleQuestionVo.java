package com.noriental.praxissvr.question.response;

import java.io.Serializable;

/**
 * @author chenlihua
 * @date 2015/12/1
 * @time 17:39
 */
public class SimpleQuestionVo implements Serializable {
    private long id;
    private long subId;
    private String type;
    private String no = "";
    private String subNo = "";
    private int answerNum;
    private int typeId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSubId() {
        return subId;
    }

    public void setSubId(long subId) {
        this.subId = subId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getSubNo() {
        return subNo;
    }

    public void setSubNo(String subNo) {
        this.subNo = subNo;
    }

    public int getAnswerNum() {
        return answerNum;
    }

    public void setAnswerNum(int answerNum) {
        this.answerNum = answerNum;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    @Override
    public String toString() {
        return "SimpleQuestionVo{" +
                "id=" + id +
                ", subId=" + subId +
                ", type='" + type + '\'' +
                ", no='" + no + '\'' +
                ", subNo='" + subNo + '\'' +
                ", answerNum=" + answerNum +
                ", typeId=" + typeId +
                '}';
    }
}
