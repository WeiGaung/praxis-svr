package com.noriental.praxissvr.question.bean;

import java.io.Serializable;

/**
 * Created by liujiang on 2017/11/15.
 */
public class WordAndChapter  implements Serializable {
    private Long id;
    private Long wordId;
    private int questionTypeId;
    private String spell;
    private Long charpterId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "WordAndChapter{" +
                "id=" + id +
                ", wordId=" + wordId +
                ", questionTypeId=" + questionTypeId +
                ", spell='" + spell + '\'' +
                ", charpterId=" + charpterId +
                '}';
    }

    public Long getCharpterId() {
        return charpterId;
    }

    public void setCharpterId(Long charpterId) {
        this.charpterId = charpterId;
    }

    public String getSpell() {
        return spell;
    }

    public void setSpell(String spell) {
        this.spell = spell;
    }

    public Long getWordId() {
        return wordId;
    }

    public void setWordId(Long wordId) {
        this.wordId = wordId;
    }

    public int getQuestionTypeId() {
        return questionTypeId;
    }

    public void setQuestionTypeId(int questionTypeId) {
        this.questionTypeId = questionTypeId;
    }

    public WordAndChapter() {
    }
}
