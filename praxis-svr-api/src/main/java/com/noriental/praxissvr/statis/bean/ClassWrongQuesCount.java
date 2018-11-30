package com.noriental.praxissvr.statis.bean;

import java.io.Serializable;
import java.util.Date;

public class ClassWrongQuesCount implements Serializable{
    private Long questionId;
    private Integer wrongCount;
    private Date lastUpdateTime;

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Integer getWrongCount() {
        return wrongCount;
    }

    public void setWrongCount(Integer wrongCount) {
        this.wrongCount = wrongCount;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
}
