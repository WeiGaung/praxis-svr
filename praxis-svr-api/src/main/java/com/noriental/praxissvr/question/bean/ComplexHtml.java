package com.noriental.praxissvr.question.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by hushuang on 2016/12/22.
 */
public class ComplexHtml implements Serializable{

    private CommonAudio audio;
    private String material;
    private List<QuestionHtml> questions;
    private String translation;
    private CommonMap map;

    public ComplexHtml() {
    }

    public CommonMap getMap() {
        return map;
    }

    public void setMap(CommonMap map) {
        this.map = map;
    }

    public CommonAudio getAudio() {
        return audio;
    }

    public void setAudio(CommonAudio audio) {
        this.audio = audio;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public List<QuestionHtml> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionHtml> questions) {
        this.questions = questions;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    @Override
    public String toString() {
        return "ComplexHtml{" +
                "audio=" + audio +
                ", material='" + material + '\'' +
                ", questions=" + questions +
                ", translation='" + translation + '\'' +
                ", map=" + map +
                '}';
    }
}
