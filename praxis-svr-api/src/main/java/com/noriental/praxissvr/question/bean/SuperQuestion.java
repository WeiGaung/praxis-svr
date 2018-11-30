package com.noriental.praxissvr.question.bean;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 试题超类
 *
 * @author xiangfei
 * @date 2015年9月17日 下午5:22:42
 */
public class SuperQuestion implements Serializable {
    private static final long serialVersionUID = 8982453890502155862L;
    private Integer qCode; // 试题状态:0-试题不存在;1-新试题；2-老试题
    private String qInfo; // 试题状态信息
    private Long id; //试题 id
    private String tag; //预留字段
    private Map<String, Object> level; // 难度: 1-易 2-中 3-难 4-极难
    private List<Map<String, Object>> theme;//主题
    private List<Map<String, Object>> special;//专题
    private Boolean isSingle;//是否为单题：1-单题 非1－复合题
    private Map<String, Object> topic_type; //题型: 1-选择题 2-填空题 3-判断题 4-简答题 5-实验题 。pad端用来显示名字
    private Integer answer_num;//选择题答案个数
    private int choiceType;//选择题类型:单选、多选.pad端控制结构
    private QuestionContent content; //内容
    private List<Long> topicIds;
    private List<String> topicNames;
    /**
     * 末级章节
     */
    private List<Long> chapterIds;
    private long structId;
    private long subjectId;
    //是否支持智能批改   0-否；1-是'
    private Integer intelligent;
    private Object htmlData; //题目的html数据
    private Object jsonData; //题目的json数据


    public Boolean getSingle() {
        return isSingle;
    }

    public void setSingle(Boolean single) {
        isSingle = single;
    }

    public Object getHtmlData() {
        return htmlData;
    }

    public void setHtmlData(Object htmlData) {
        this.htmlData = htmlData;
    }

    public Object getJsonData() {
        return jsonData;
    }

    public void setJsonData(Object jsonData) {
        this.jsonData = jsonData;
    }

    public long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(long subjectId) {
        this.subjectId = subjectId;
    }

    public Integer getIntelligent() {
        return intelligent;
    }

    public void setIntelligent(Integer intelligent) {
        this.intelligent = intelligent;
    }

    public Integer getqCode() {
        return qCode;
    }

    public void setqCode(Integer qCode) {
        this.qCode = qCode;
    }

    public String getqInfo() {
        return qInfo;
    }

    public void setqInfo(String qInfo) {
        this.qInfo = qInfo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Map<String, Object> getLevel() {
        return level;
    }

    public void setLevel(Map<String, Object> level) {
        this.level = level;
    }

    public List<Map<String, Object>> getTheme() {
        return theme;
    }

    public void setTheme(List<Map<String, Object>> theme) {
        this.theme = theme;
    }

    public List<Map<String, Object>> getSpecial() {
        return special;
    }

    public void setSpecial(List<Map<String, Object>> special) {
        this.special = special;
    }

    public Boolean getIsSingle() {
        return isSingle;
    }

    public void setIsSingle(Boolean isSingle) {
        this.isSingle = isSingle;
    }

    public Map<String, Object> getTopic_type() {
        return topic_type;
    }

    public void setTopic_type(Map<String, Object> topic_type) {
        this.topic_type = topic_type;
    }

    public Integer getAnswer_num() {
        return answer_num;
    }

    public void setAnswer_num(Integer answer_num) {
        this.answer_num = answer_num;
    }

    public QuestionContent getContent() {
        return content;
    }

    public int getChoiceType() {
        return choiceType;
    }

    public void setChoiceType(int choiceType) {
        this.choiceType = choiceType;
    }

    public void setContent(QuestionContent content) {
        this.content = content;
    }

    public List<Long> getTopicIds() {
        return topicIds;
    }

    public void setTopicIds(List<Long> topicIds) {
        this.topicIds = topicIds;
    }

    public List<String> getTopicNames() {
        return topicNames;
    }

    public void setTopicNames(List<String> topicNames) {
        this.topicNames = topicNames;
    }

    public long getStructId() {
        return structId;
    }

    public void setStructId(long structId) {
        this.structId = structId;
    }

    public List<Long> getChapterIds() {
        return chapterIds;
    }

    public void setChapterIds(List<Long> chapterIds) {
        this.chapterIds = chapterIds;
    }

    @Override
    public String toString() {
        return "SuperQuestion{" +
                "qCode=" + qCode +
                ", qInfo='" + qInfo + '\'' +
                ", id=" + id +
                ", tag='" + tag + '\'' +
                ", level=" + level +
                ", theme=" + theme +
                ", special=" + special +
                ", isSingle=" + isSingle +
                ", topic_type=" + topic_type +
                ", answer_num=" + answer_num +
                ", choiceType=" + choiceType +
                ", content=" + content +
                ", topicIds=" + topicIds +
                ", topicNames=" + topicNames +
                ", chapterIds=" + chapterIds +
                ", structId=" + structId +
                ", subjectId=" + subjectId +
                ", intelligent=" + intelligent +
                ", htmlData=" + htmlData +
                ", jsonData=" + jsonData +
                '}';
    }
}
