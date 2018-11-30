package com.noriental.praxissvr.question.bean;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.TreeMap;

/**
 * 单题内容类
 *
 * @author xiangfei
 * @date 2015年9月17日 下午5:49:16
 */
public class SingleQuestionContent extends QuestionContent {

    private static final long serialVersionUID = -8074097026710635869L;
    private Object body; // 题干
    private Object interpret; //解读
    private Object image; //作图底图
    private Object model_essay; // 范文
    private Object options; //选项
    private Integer answerNum; //选择题答案个数
    private Object answer; //答案
    private Object analysis; //解析
    private Object prompt; //单词
    private Object base_image;
    private Object third_party_use;//第三方数据
    private QuestionAudio audio;//音频

    @Override
    public String toString() {
        return "SingleQuestionContent{" +
                "body=" + body +
                ", interpret=" + interpret +
                ", image=" + image +
                ", model_essay=" + model_essay +
                ", options=" + options +
                ", answerNum=" + answerNum +
                ", answer=" + answer +
                ", analysis=" + analysis +
                ", prompt=" + prompt +
                ", base_image=" + base_image +
                ", third_party_use=" + third_party_use +
                ", audio=" + getAudioString() +
                '}';
    }
    public String getAudioString(){
        if(getAudio()==null){
            return "";
        }else{
            return getAudio().toString();
        }
    }

    public QuestionAudio getAudio() {
        return audio;
    }

    public void setAudio(QuestionAudio audio) {
        this.audio = audio;
    }

    public SingleQuestionContent() {
    }

    public Object getThird_party_use() {
        return third_party_use;
    }

    public void setThird_party_use(Object third_party_use) {
        this.third_party_use = third_party_use;
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

    public Object getOptions() {
        return options;
    }

    public void setOptions(Object options) {
        this.options = options;
    }

    public Integer getAnswerNum() {
        return answerNum;
    }

    public void setAnswerNum(Integer answerNum) {
        this.answerNum = answerNum;
    }

    public Object getAnswer() {
        return answer;
    }

    public void setAnswer(Object answer) {
        this.answer = answer;
    }

    public Object getAnalysis() {
        return StringUtils.isBlank(analysis+"") ? Arrays.asList(new TreeMap<String, String>() {
            {
                this.put("type", "text");
                this.put("value", "略");
            }
        }) : analysis;
    }

    public void setAnalysis(Object analysis) {
        this.analysis = analysis;
    }

    public Object getPrompt() {
        return prompt;
    }

    public void setPrompt(Object prompt) {
        this.prompt = prompt;
    }

    public Object getBase_image() {
        return base_image;
    }

    public void setBase_image(Object base_image) {
        this.base_image = base_image;
    }

}
