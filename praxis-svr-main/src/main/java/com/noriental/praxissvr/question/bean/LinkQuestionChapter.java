package com.noriental.praxissvr.question.bean;

/**
 * Created by hushuang on 2016/11/22.
 */
public class LinkQuestionChapter {

    private Long id;
    private long questionId;
    private long chapterId;
    private int srcFlag;

    public LinkQuestionChapter() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(long questionId) {
        this.questionId = questionId;
    }

    public long getChapterId() {
        return chapterId;
    }

    public void setChapterId(long chapterId) {
        this.chapterId = chapterId;
    }

    public int getSrcFlag() {
        return srcFlag;
    }

    public void setSrcFlag(int srcFlag) {
        this.srcFlag = srcFlag;
    }

    @Override
    public String toString() {
        return "LinkQuestionChapter{" +
                "id=" + id +
                ", questionId=" + questionId +
                ", chapterId=" + chapterId +
                ", srcFlag=" + srcFlag +
                '}';
    }
}
