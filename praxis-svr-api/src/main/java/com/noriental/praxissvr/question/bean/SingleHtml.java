package com.noriental.praxissvr.question.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by hushuang on 2016/12/22.
 */
public class SingleHtml  implements Serializable{
    private String body;
    private List<String> options;
    private CommonAudio audio;
    private Object answer;
    private String analysis;
    private List<String> questions;
    private CommonMap map;

    public SingleHtml() {
    }

    public CommonMap getMap() {
        return map;
    }

    public void setMap(CommonMap map) {
        this.map = map;
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

    public CommonAudio getAudio() {
        return audio;
    }

    public void setAudio(CommonAudio audio) {
        this.audio = audio;
    }

    public Object getAnswer() {
        return answer;
    }

    public void setAnswer(Object answer) {
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

    @Override
    public String toString() {
        return "SingleHtml{" +
                "body='" + body + '\'' +
                ", options=" + options +
                ", audio=" + audio +
                ", answer=" + answer +
                ", analysis='" + analysis + '\'' +
                ", questions=" + questions +
                ", map=" + map +
                '}';
    }
}
