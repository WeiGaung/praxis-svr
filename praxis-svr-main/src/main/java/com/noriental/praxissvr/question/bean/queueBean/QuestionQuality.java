package com.noriental.praxissvr.question.bean.queueBean;

import java.io.Serializable;

/**
 * Created by liujiang on 2018/5/16.
 */
public class QuestionQuality implements Serializable {

    private Integer difficulty;
    private Long questionId;
    private Long uploadId;

    public Integer getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Long getUploadId() {
        return uploadId;
    }

    public void setUploadId(Long uploadId) {
        this.uploadId = uploadId;
    }

    @Override
    public String toString() {
        return "QuestionQuality{" +
                "difficulty=" + difficulty +
                ", questionId=" + questionId +
                ", uploadId=" + uploadId +
                '}';
    }
}
