package com.noriental.praxissvr.question.bean;

import java.util.List;

/**
 * @author chenlihua
 * @date 2016/7/19
 * @time 16:41
 */
public class QuestionSsdbContentHtml {
    private String body;
    private String material;
    private String model_essay;
    private String source;
    private String interpret;
    private String translation;
    private List<String> options;
    private String analysis;
    private Object answer;
    private QuestionAudio audio;
    private QuestionMap map;
    private Object prompt;
    private List<QuestionSsdbContentHtml> questions;

    public Object getPrompt() {
        return prompt;
    }

    public void setPrompt(Object prompt) {
        this.prompt = prompt;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getModel_essay() {
        return model_essay;
    }

    public void setModel_essay(String model_essay) {
        this.model_essay = model_essay;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getInterpret() {
        return interpret;
    }

    public void setInterpret(String interpret) {
        this.interpret = interpret;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
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

    public QuestionAudio getAudio() {
        return audio;
    }

    public void setAudio(QuestionAudio audio) {
        this.audio = audio;
    }

    public List<QuestionSsdbContentHtml> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionSsdbContentHtml> questions) {
        this.questions = questions;
    }

    public QuestionMap getMap() {
        return map;
    }

    public void setMap(QuestionMap map) {
        this.map = map;
    }

    @Override
    public String toString() {
        return "QuestionSsdbContentHtml{" +
                "body='" + body + '\'' +
                ", material='" + material + '\'' +
                ", model_essay='" + model_essay + '\'' +
                ", source='" + source + '\'' +
                ", interpret='" + interpret + '\'' +
                ", translation='" + translation + '\'' +
                ", options=" + options +
                ", analysis='" + analysis + '\'' +
                ", answer=" + answer +
                ", audio=" + audio +
                ", map=" + map +
                ", prompt=" + prompt +
                ", questions=" + questions +
                '}';
    }
}
