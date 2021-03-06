package com.noriental.praxissvr.question.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @author xiangfei
 * @date 2015年9月18日 下午4:41:00
 */
public class SuperQuestionMongoContent implements Serializable {

    private static final long serialVersionUID = -8331031635889008464L;
    private String id;
    private Object tag;
    private Object body; // 题干
    private Object interpret; // 解读
    private Object image; // 作图底图
    private Object model_essay; // 范文
    private Object source;// 原文
    private Object options; // 选择题的选项
    private Object answer; // 答案
    private Object analysis; // 解析
    private Object material; // 材料
    private List<SuperQuestionMongoContent> questions; // 综合题下的小题
    private Object translation; // 译文
    private Long question_id; // 试题id;
    private QuestionAudio audio; //音频
    private Object map; //底图
    private Object prompt; //文章
    private Object third_party_use;//第三方专用进行新题型提交数据
    private Object difficulty;
    private Object type;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Object getThird_party_use() {
        return third_party_use;
    }

    public void setThird_party_use(Object third_party_use) {
        this.third_party_use = third_party_use;
    }

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

    public List<SuperQuestionMongoContent> getQuestions() {
        return questions;
    }

    public void setQuestions(List<SuperQuestionMongoContent> questions) {
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

    public Object getMap() {
        return map;
    }

    public void setMap(Object map) {
        this.map = map;
    }

    public Object getPrompt() {
        return prompt;
    }

    public void setPrompt(Object prompt) {
        this.prompt = prompt;
    }

    public Object getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Object difficulty) {
        this.difficulty = difficulty;
    }

    public Object getType() {
        return type;
    }

    public void setType(Object type) {
        this.type = type;
    }

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "SuperQuestionMongoContent{" +
                "id='" + id + '\'' +
                ", tag=" + tag +
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
                ", map=" + map +
                ", prompt=" + prompt +
                ", third_party_use=" + third_party_use +
                ", difficulty=" + difficulty +
                ", type=" + type +
                '}';
    }
}
