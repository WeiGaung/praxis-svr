package com.noriental.praxissvr.question.bean.html;

import org.omg.CORBA.PRIVATE_MEMBER;

import java.io.Serializable;
import java.util.List;

/**
 * Created by liujiang
 * 为了解决查答案时，答案不规范导致的问题。
 */

public class QuestionV1 implements Serializable{

    private Type type;
    private String body;
    private String difficulty;
    private String analysis;
    //private List<Object> answer;
    private Object answer;
    private List<Object> options;
    private NewMap map;
    private String audio;
    private String material;
    private String translation;
    private int intelligent;

    @Override
    public String toString() {
        return "Question{" +
                "type=" + type +
                ", body='" + body + '\'' +
                ", difficulty='" + difficulty + '\'' +
                ", analysis='" + analysis + '\'' +
                ", answer=" + answer +
                ", options=" + options +
                ", map=" + map +
                ", audio='" + audio + '\'' +
                ", material='" + material + '\'' +
                ", translation='" + translation + '\'' +
                ", intelligent=" + intelligent +
                '}';
    }

    public int getIntelligent() {
        return intelligent;
    }

    public void setIntelligent(int intelligent) {
        this.intelligent = intelligent;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public QuestionV1() {
    }

    public NewMap getMap() {
        return map;
    }

    public void setMap(NewMap map) {
        this.map = map;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getAnalysis() {
        return analysis;
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }

//    public List<Object> getAnswer() {
//        return answer;
//    }
//
//    public void setAnswer(List<Object> answer) {
//        this.answer = answer;
//    }


	public Object getAnswer() {
		return answer;
	}
	public void setAnswer(Object answer) {
		this.answer = answer;
	}

    public List<Object> getOptions() {
        return options;
    }

    public void setOptions(List<Object> options) {
        this.options = options;
    }

}
