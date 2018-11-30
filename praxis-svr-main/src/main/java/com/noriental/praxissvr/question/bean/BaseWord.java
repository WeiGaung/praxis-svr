package com.noriental.praxissvr.question.bean;

/**
 * Created by hushuang on 2017/7/7.
 */
public class BaseWord {

    private Long wordId;//单词ID
    private String spell;//单词拼写

    public BaseWord() {
    }

    public Long getWordId() {
        return wordId;
    }

    public void setWordId(Long wordId) {
        this.wordId = wordId;
    }

    public String getSpell() {
        return spell;
    }

    public void setSpell(String spell) {
        this.spell = spell;
    }

    @Override
    public String toString() {
        return "BaseWord{" +
                "wordId=" + wordId +
                ", spell='" + spell + '\'' +
                '}';
    }
}
