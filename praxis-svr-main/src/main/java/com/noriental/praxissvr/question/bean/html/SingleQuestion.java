package com.noriental.praxissvr.question.bean.html;

import java.io.Serializable;
import java.util.List;

/**
 * Created by hushuang on 2016/11/29.
 */
public class SingleQuestion implements Serializable{
    private String body;
    private List<String> options;
    private Audio audio;
    private List<Object> answer;
    private String analysis;
    private List<String> questions;
    private NewMap map;
    private String prompt;
    private String third_party_use;


    public SingleQuestion() {
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public Audio getAudio() {
        return audio;
    }

    public void setAudio(Audio audio) {
        this.audio = audio;
    }

    public List<Object> getAnswer() {
        return answer;
    }

    public void setAnswer(List<Object> answer) {
        this.answer = answer;
    }

    public String getAnalysis() {
        return analysis;
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }

    public List<String> getQuestions() {
        return questions;
    }

    public void setQuestions(List<String> questions) {
        this.questions = questions;
    }

    public NewMap getMap() {
        return map;
    }

    public void setMap(NewMap map) {
        this.map = map;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getThird_party_use() {
        return third_party_use;
    }

    public void setThird_party_use(String third_party_use) {
        this.third_party_use = third_party_use;
    }

    @Override
    public String toString() {
        return "SingleQuestion{" +
                "body='" + body + '\'' +
                ", options=" + options +
                ", audio=" + audio +
                ", answer=" + answer +
                ", analysis='" + analysis + '\'' +
                ", questions=" + questions +
                ", map=" + map +
                ", prompt='" + prompt + '\'' +
                ", third_party_use='" + third_party_use + '\'' +
                '}';
    }
}
