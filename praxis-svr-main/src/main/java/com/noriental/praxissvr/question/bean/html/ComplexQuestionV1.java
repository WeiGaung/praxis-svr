package com.noriental.praxissvr.question.bean.html;

import java.io.Serializable;
import java.util.List;

/**
 * Created by liujiang
 * 为了解决查答案时，答案不规范导致的问题。
 */
public class ComplexQuestionV1 implements Serializable{

    private Audio audio;
    private String material;
    private List<QuestionV1> questions;
    private String translation;
    private NewMap map;


    public ComplexQuestionV1() {
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

    public List<QuestionV1> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionV1> questions) {
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
