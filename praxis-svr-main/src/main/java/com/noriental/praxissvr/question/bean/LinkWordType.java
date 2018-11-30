package com.noriental.praxissvr.question.bean;

/**
 * Created by hushuang on 2017/7/7.
 */
public class LinkWordType {

    private Long id;
    private Long questionId;
    private Long baseWordTranslationId;
    private int questionTypeId;
    private Long wordId;
    private Long chapterId;

    @Override
    public String toString() {
        return "LinkWordType{" +
                "id=" + id +
                ", questionId=" + questionId +
                ", baseWordTranslationId=" + baseWordTranslationId +
                ", questionTypeId=" + questionTypeId +
                ", wordId=" + wordId +
                ", chapterId=" + chapterId +
                '}';
    }

    public Long getChapterId() {
        return chapterId;
    }

    public void setChapterId(Long chapterId) {
        this.chapterId = chapterId;
    }

    public Long getWordId() {
        return wordId;
    }

    public void setWordId(Long wordId) {
        this.wordId = wordId;
    }


    public LinkWordType() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Long getBaseWordTranslationId() {
        return baseWordTranslationId;
    }

    public void setBaseWordTranslationId(Long baseWordTranslationId) {
        this.baseWordTranslationId = baseWordTranslationId;
    }

    public int getQuestionTypeId() {
        return questionTypeId;
    }

    public void setQuestionTypeId(int questionTypeId) {
        this.questionTypeId = questionTypeId;
    }

}
