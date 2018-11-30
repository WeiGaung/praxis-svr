package com.noriental.praxissvr.question.bean.html;

import java.io.Serializable;
import java.util.List;

/**
 * Created by hushuang on 2016/11/29.
 * 复合题通用解析类
 */
public class ComplexQuestion implements Serializable{

    private Audio audio;
    private String material;
    private List<Question> questions;
    private String translation;
    private NewMap map;


    public ComplexQuestion() {
    }

    public NewMap getMap() {
        return map;
    }

    public void setMap(NewMap map) {
        this.map = map;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public Audio getAudio() {
        return audio;
    }

    public void setAudio(Audio audio) {
        this.audio = audio;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    @Override
    public String toString() {
        return "ComplexQuestion{" +
                "audio=" + audio +
                ", material='" + material + '\'' +
                ", questions=" + questions +
                ", translation='" + translation + '\'' +
                ", map=" + map +
                '}';
    }
}
