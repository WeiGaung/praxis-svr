package com.noriental.praxissvr.question.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by hushuang on 2016/12/22.
 */
public class QuestionHtml implements Serializable {

    private TypeHtml type;
    private String body;
    private String difficulty;
    private String analysis;
    private Object answer;
    private List<String> options;
    private CommonMap map;

    public QuestionHtml() {
    }

    public CommonMap getMap() {
        return map;
    }

    public void setMap(CommonMap map) {
        this.map = map;
    }

    public TypeHtml getType() {
        return type;
    }

    public void setType(TypeHtml type) {
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


    public Object getAnswer() {
        return answer;
    }

    public void setAnswer(Object answer) {
        this.answer = answer;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    @Override
    public String toString() {
        return "QuestionHtml{" +
                "type=" + type +
                ", body='" + body + '\'' +
                ", difficulty='" + difficulty + '\'' +
                ", analysis='" + analysis + '\'' +
                ", answer=" + answer +
                ", options=" + options +
                ", map=" + map +
                '}';
    }
}
