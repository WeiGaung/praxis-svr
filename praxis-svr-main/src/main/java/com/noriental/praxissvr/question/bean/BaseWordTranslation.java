package com.noriental.praxissvr.question.bean;

/**
 * Created by liujiang on 2017/11/16.
 */
public class BaseWordTranslation {

    private Long id;
    private Long wordId; //单词ID
    private String spell; //单词拼写
    private Long versionId;//教材版本ID
    private Long directoryId;//教材分册ID
    private Long chapterId;//教材章节ID
    private String translation;//单词释义

    public BaseWordTranslation() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getVersionId() {
        return versionId;
    }

    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }

    public Long getDirectoryId() {
        return directoryId;
    }

    public void setDirectoryId(Long directoryId) {
        this.directoryId = directoryId;
    }

    public Long getChapterId() {
        return chapterId;
    }

    public void setChapterId(Long chapterId) {
        this.chapterId = chapterId;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    @Override
    public String toString() {
        return "BaseWordTranslation{" +
                "id=" + id +
                ", wordId=" + wordId +
                ", spell='" + spell + '\'' +
                ", versionId=" + versionId +
                ", directoryId=" + directoryId +
                ", chapterId=" + chapterId +
                ", translation='" + translation + '\'' +
                '}';
    }
}