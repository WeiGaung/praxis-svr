package com.noriental.praxissvr.question.bean.solrBean;

/**
 * Created by kate on 2016/12/1.
 */
public class QueryDataForSolrBean {

    private String orgId;
    private String series;
    private String topic;
    private String chapter;
    private String chapter1;
    private String chapter2;
    private String chapter3;
    private String questionGroupName;

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public String getChapter1() {
        return chapter1;
    }

    public void setChapter1(String chapter1) {
        this.chapter1 = chapter1;
    }

    public String getChapter2() {
        return chapter2;
    }

    public void setChapter2(String chapter2) {
        this.chapter2 = chapter2;
    }

    public String getChapter3() {
        return chapter3;
    }

    public void setChapter3(String chapter3) {
        this.chapter3 = chapter3;
    }

    public String getQuestionGroupName() {
        return questionGroupName;
    }

    public void setQuestionGroupName(String questionGroupName) {
        this.questionGroupName = questionGroupName;
    }
}
