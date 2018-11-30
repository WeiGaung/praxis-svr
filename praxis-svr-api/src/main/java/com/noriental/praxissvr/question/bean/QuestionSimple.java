package com.noriental.praxissvr.question.bean;

import java.io.Serializable;
import java.util.List;

public class QuestionSimple implements Serializable, Comparable<QuestionSimple> {
    private static final long serialVersionUID = 8203405037411811041L;

    private String audio;
    private String video;
    private String qqqDocx;
    private String qqqBody;
    private String qqqOptions;
    private String qqqAnswer;
    private String qqqAnalysis;
    private String translation;
    private String rightOption;
    private String questionType;
    private long id;
    private int countOptions;
    private List<QuestionSimple> subQuestions;
    private int seq;
    private long parentQuestionId;
    private boolean single;
    private int difficulty;
    private List<Long> topicIdList;
    private List<Long> seriesIdList;

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getQqqDocx() {
        return qqqDocx;
    }

    public void setQqqDocx(String qqqDocx) {
        this.qqqDocx = qqqDocx;
    }

    public String getQqqBody() {
        return qqqBody;
    }

    public void setQqqBody(String qqqBody) {
        this.qqqBody = qqqBody;
    }

    public String getQqqOptions() {
        return qqqOptions;
    }

    public void setQqqOptions(String qqqOptions) {
        this.qqqOptions = qqqOptions;
    }

    public String getQqqAnswer() {
        return qqqAnswer;
    }

    public void setQqqAnswer(String qqqAnswer) {
        this.qqqAnswer = qqqAnswer;
    }

    public String getQqqAnalysis() {
        return qqqAnalysis;
    }

    public void setQqqAnalysis(String qqqAnalysis) {
        this.qqqAnalysis = qqqAnalysis;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public String getRightOption() {
        return rightOption;
    }

    public void setRightOption(String rightOption) {
        this.rightOption = rightOption;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getCountOptions() {
        return countOptions;
    }

    public void setCountOptions(int countOptions) {
        this.countOptions = countOptions;
    }

    public List<QuestionSimple> getSubQuestions() {
        return subQuestions;
    }

    public void setSubQuestions(List<QuestionSimple> subQuestions) {
        this.subQuestions = subQuestions;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public long getParentQuestionId() {
        return parentQuestionId;
    }

    public void setParentQuestionId(long parentQuestionId) {
        this.parentQuestionId = parentQuestionId;
    }

    public boolean isSingle() {
        return single;
    }

    public void setSingle(boolean single) {
        this.single = single;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public List<Long> getTopicIdList() {
        return topicIdList;
    }

    public void setTopicIdList(List<Long> topicIdList) {
        this.topicIdList = topicIdList;
    }

    public List<Long> getSeriesIdList() {
        return seriesIdList;
    }

    public void setSeriesIdList(List<Long> seriesIdList) {
        this.seriesIdList = seriesIdList;
    }

    @Override
    public int compareTo(QuestionSimple o) {
        if (this.getSeq() > o.getSeq()) {
            return 1;
        }
        if (this.getSeq() == o.getSeq()) {
            return 0;
        }
        return -1;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
