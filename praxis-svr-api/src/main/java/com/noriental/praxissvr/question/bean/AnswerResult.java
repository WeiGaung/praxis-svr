package com.noriental.praxissvr.question.bean;

import java.io.Serializable;

/**
 * Created by hushuang on 2017/3/14.
 */
public class AnswerResult implements Serializable {

    /**
     * 填空题空的顺序序号
     */
    private Integer index;

    /**
     * 填空题空的答案
     */
    private String answer;

    public AnswerResult() {
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @Override
    public String toString() {
        return "AnswerResult{" +
                "index=" + index +
                ", answer='" + answer + '\'' +
                '}';
    }
}
