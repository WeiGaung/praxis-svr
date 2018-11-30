package com.noriental.praxissvr.question.bean;

import java.io.Serializable;

/**
 * Created by hushuang on 2017/7/6.
 * 单词描述
 */
public class Word implements Serializable {

    /*
        单词ID
     */
    private Long wordId;
    /*
        单词拼写
     */
    private String spell;
    /*
        单词翻译
     */
    private String translation;

    private Long translationId;

    @Override
    public String toString() {
        return "Word{" +
                "wordId=" + wordId +
                ", spell='" + spell + '\'' +
                ", translation='" + translation + '\'' +
                ", translationId=" + translationId +
                '}';
    }

    public Long getTranslationId() {
        return translationId;
    }

    public void setTranslationId(Long translationId) {
        this.translationId = translationId;
    }

    public Word() {
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

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

}
