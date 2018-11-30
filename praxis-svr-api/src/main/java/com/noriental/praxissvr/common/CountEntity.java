//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.noriental.praxissvr.common;

import com.noriental.utils.json.JsonUtil;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class CountEntity implements Serializable {
    private Long key;
    boolean isAllSingle;
    private int questionNum;
    private int questionNumSub;
    private Set<Long> questionSet = new HashSet();

    public CountEntity() {
    }

    public CountEntity(Long key, int questionNum, int questionNumSub) {
        this.key = key;
        this.questionNum = questionNum;
        this.questionNumSub = questionNumSub;
    }

    public int getQuestionNum() {
        return this.questionNum;
    }

    public void setQuestionNum(int questionNum) {
        this.questionNum = questionNum;
    }

    public void addQuestionNum(Long questionId, int questionNum, int questionNumSub) {
        if(!this.questionSet.contains(questionId)) {
            this.questionSet.add(questionId);
            this.questionNum += questionNum;
            this.questionNumSub += questionNumSub == 0?1:questionNumSub;
        }

    }

    public boolean isAllSingle() {
        return this.isAllSingle;
    }

    public void setIsAllSingle(boolean isAllSingle) {
        this.isAllSingle = isAllSingle;
    }

    public int getQuestionNumSub() {
        return this.questionNumSub;
    }

    public void setQuestionNumSub(int questionNumSub) {
        this.questionNumSub = questionNumSub;
    }

    public Long getKey() {
        return this.key;
    }

    public void setKey(Long key) {
        this.key = key;
    }

    public String toString() {
        return JsonUtil.obj2Json(this);
    }
}
