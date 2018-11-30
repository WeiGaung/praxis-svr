package com.noriental.praxissvr.question.bean;

import java.io.Serializable;
import java.util.List;

public class SuperQuestionSsdbContent implements Serializable {

    private static final long serialVersionUID = -8331031635889008464L;
    private String id;
    private Object body; // 题干
    private Object interpret; // 解读
    private Object image; // 作图底图
    private Object model_essay; // 范文
    private Object source;// 原文
    private Object options; // 选择题的选项
    private Object answer; // 答案
    private Object analysis; // 解析
    private Object material; // 材料
    private List<SuperQuestionSsdbContent> questions; // 综合题下的小题
    private Object translation; // 译文
    private Long question_id; // 试题id;
    private QuestionAudio audio;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    public Object getInterpret() {
        return interpret;
    }

    public void setInterpret(Object interpret) {
        this.interpret = interpret;
    }

    public Object getImage() {
        return image;
    }

    public void setImage(Object image) {
        this.image = image;
    }

    public Object getModel_essay() {
        return model_essay;
    }

    public void setModel_essay(Object model_essay) {
        this.model_essay = model_essay;
    }

    public Object getSource() {
        return source;
    }

    public void setSource(Object source) {
        this.source = source;
    }

    public Object getOptions() {
        return options;
    }

    public void setOptions(Object options) {
        this.options = options;
    }

    public Object getAnswer() {
        return answer;
    }

    public void setAnswer(Object answer) {
        this.answer = answer;
    }

    public Object getAnalysis() {
        return analysis;
    }

    public void setAnalysis(Object analysis) {
        this.analysis = analysis;
    }

    public Object getMaterial() {
        return material;
    }

    public void setMaterial(Object material) {
        this.material = material;
    }


    public List<SuperQuestionSsdbContent> getQuestions() {
        return questions;
    }

    public void setQuestions(List<SuperQuestionSsdbContent> questions) {
        this.questions = questions;
    }

    public Object getTranslation() {
        return translation;
    }

    public void setTranslation(Object translation) {
        this.translation = translation;
    }

    public Long getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(Long question_id) {
        this.question_id = question_id;
    }

    public QuestionAudio getAudio() {
        return audio;
    }

    public void setAudio(QuestionAudio audio) {
        this.audio = audio;
    }

    @Override
    public String toString() {
        return "SuperQuestionMongoContent{" +
                "id='" + id + '\'' +
                ", body=" + body +
                ", interpret=" + interpret +
                ", image=" + image +
                ", model_essay=" + model_essay +
                ", source=" + source +
                ", options=" + options +
                ", answer=" + answer +
                ", analysis=" + analysis +
                ", material=" + material +
                ", questions=" + questions +
                ", translation=" + translation +
                ", question_id=" + question_id +
                ", audio=" + audio +
                '}';
    }
}
