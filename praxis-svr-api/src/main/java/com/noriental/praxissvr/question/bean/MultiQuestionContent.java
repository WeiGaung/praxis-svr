package com.noriental.praxissvr.question.bean;

import java.util.List;

/**
 * 复合题内容类
 *
 * @author xiangfei
 * @date 2015年9月17日 下午5:48:56
 */
public class MultiQuestionContent extends QuestionContent {
    private static final long serialVersionUID = 5812881166339689059L;
    private Object source;  //原文
    private Object interpret;//解读
    private Object material;//资料
    private Object translation; //译文
    private QuestionAudio audio;//听力资料
    private List<SuperQuestion> questions; //子题

    public Object getSource() {
        return source;
    }

    public void setSource(Object source) {
        this.source = source;
    }

    public Object getInterpret() {
        return interpret;
    }

    public void setInterpret(Object interpret) {
        this.interpret = interpret;
    }

    public Object getMaterial() {
        return material;
    }

    public void setMaterial(Object material) {
        this.material = material;
    }

    public Object getTranslation() {
        return translation;
    }

    public void setTranslation(Object translation) {
        this.translation = translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public List<SuperQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(List<SuperQuestion> questions) {
        this.questions = questions;
    }

    public QuestionAudio getAudio() {
        return audio;
    }

    public void setAudio(QuestionAudio audio) {
        this.audio = audio;
    }

    @Override
    public String toString() {
        return "MultiQuestionContent{" +
                "source=" + source +
                ", interpret=" + interpret +
                ", material=" + material +
                ", translation=" + translation +
                ", audio=" + audio +
                ", questions=" + questions +
                '}';
    }
}
