package com.noriental.praxissvr.question.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.noriental.adminsvr.bean.knowledge.Series;
import com.noriental.adminsvr.bean.knowledge.Topic;
import com.noriental.framework.base.BaseBean;
import com.noriental.publicsvr.bean.Subject;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Author : Lance lance7in_gmail_com Date : 11/12/2013 09:07 Since : 0.1
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Question extends BaseBean {

    private long id;
    /**
     * 题目语义编码
     */

    private String code;

    private boolean single;

    /**
     * 题型
     */
    private String questionType;
    /**
     * 难度
     */
    private int difficulty;
    /**
     * 掌握层级
     */
    private int mastery;
    /**
     * 分值
     */
    private float score = 0;
    /**
     * 是否精品题
     */
    private int highQual;
    /**
     * 音频文件路径
     */
    private String audio;

    /**
     * 对应mongo中的audio json
     */
    private QuestionAudio audioJson;
    /**
     * 新题型作图题的底图数据
     */
    private Object jsonMap;
    /**
     * 视频文件路径
     */
    // @JsonSerialize(using = VideoPathSerializer.class)
    private String video;
    /**
     * 引用次数
     */
    @Deprecated
    private long countRef = 0;
    /**
     * 关联知识点数
     */
    private int countTopic = 0;
    /**
     * 状态: UNEVALED(未审核)/ENABLED(启用)/DISABLED(停用)
     */
    private String state;

    /**
     * 题干图片路径
     */
    private String questionBody;

    /**
     * 听口题文章字段
     */
    private Object prompt;

    /**
     * 选项
     */
    private String questionOptions;
    /**
     * 解析图片路径
     */
    private String questionAnalysis;
    /**
     * 材料图片路径
     */
    private String material;
    /**
     * 译文图片路径
     */
    private String translation;
    /**
     * 选项数量
     */
    private int countOptions = 0;
    /**
     * 正确选项 Format example: "A" or "A,B,D"
     */
    private String rightOption;
    /**
     * 历史优选题答案 Format example: {"A":10,"B":9,"C":5,"D":1}
     */
    private String multiScoreAnswer;
    /**
     * 父题
     */
    private Question parentQuestion;
    /**
     * 子题
     */
    private List<Question> subQuestions;
    /**
     * 父题 id
     */
    private long parentQuestionId;

    private Subject subject;

    private long qrId;
    private Date updateTime;
    private Long subjectId;
    private List<Series> seriesList;
    private List<Topic> topicList;

    private List<Long> allLeafQuesIds;
    private List<Long> allTopicIds;
    private List<Long> allUnitIds;
    private List<Long> allModuleIds;
    private String allTopicIdStr;
    private String allUnitIdStr;
    private String allModuleIdStr;
    private Integer newFormat;// 新题标志
    private Integer questionTypeId;//题型id
    private String model_essay;//范文
    private String source;//原文
    private String interpret;//解读
    private Object questionOptionList;
    private Object questionAnswerList;//答案列表
    private List<Long> topicId;// 主题
    private List<Long> seriesId;
    private Long questionGroup;// 组ID,详见entity_group
    private String questionGroupName;  //组名称
    private String questionTypeDetail; //题型细分:如选择题题型（单选题，多选题）
    private Integer answerNum;// 答案个数
    private Integer intelligent;

    private Integer subQuestionSeqNum; //是第几个小题

    //是否老师题目
    private int isTeacherQuestion;

    private List<Long> chapterId1;
    private List<Long> chapterId2;
    private List<Long> chapterId3;

    private int visible;
    private long structId;

    private int quoteNum;//引用量
    private int submitNum;//作答量
    private double accuracy;//正确率

    private String paperName;

    private Integer orgType;

    private Long orgId;

    private Long groupId;//自定义目录体系ID

    private List<Map<String,Object>> customerDirectoryIds;//自定义目录ID

    private String htmlData;

    private String jsonData;
    private Integer uploadSrc;
    private Long uploadId;
    private Date uploadTime;


    //资源主
    private String uploadName;
    //操作 1创建 3编辑 4收藏
    private Integer questionSource;

    //题目关联的专题ID 一个题目可以关联多个专题
    private List<Long> specials;
    //收藏量
    private Long collectionNum;
    //收藏时间
    private Date favTime;

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", single=" + single +
                ", questionType='" + questionType + '\'' +
                ", difficulty=" + difficulty +
                ", mastery=" + mastery +
                ", score=" + score +
                ", highQual=" + highQual +
                ", audio='" + audio + '\'' +
                ", audioJson=" + audioJson +
                ", jsonMap=" + jsonMap +
                ", video='" + video + '\'' +
                ", countRef=" + countRef +
                ", countTopic=" + countTopic +
                ", state='" + state + '\'' +
                ", questionBody='" + questionBody + '\'' +
                ", prompt=" + prompt +
                ", questionOptions='" + questionOptions + '\'' +
                ", questionAnalysis='" + questionAnalysis + '\'' +
                ", material='" + material + '\'' +
                ", translation='" + translation + '\'' +
                ", countOptions=" + countOptions +
                ", rightOption='" + rightOption + '\'' +
                ", multiScoreAnswer='" + multiScoreAnswer + '\'' +
                ", parentQuestion=" + parentQuestion +
                ", subQuestions=" + subQuestions +
                ", parentQuestionId=" + parentQuestionId +
                ", subject=" + subject +
                ", qrId=" + qrId +
                ", updateTime=" + updateTime +
                ", subjectId=" + subjectId +
                ", seriesList=" + seriesList +
                ", topicList=" + topicList +
                ", allLeafQuesIds=" + allLeafQuesIds +
                ", allTopicIds=" + allTopicIds +
                ", allUnitIds=" + allUnitIds +
                ", allModuleIds=" + allModuleIds +
                ", allTopicIdStr='" + allTopicIdStr + '\'' +
                ", allUnitIdStr='" + allUnitIdStr + '\'' +
                ", allModuleIdStr='" + allModuleIdStr + '\'' +
                ", newFormat=" + newFormat +
                ", questionTypeId=" + questionTypeId +
                ", model_essay='" + model_essay + '\'' +
                ", source='" + source + '\'' +
                ", interpret='" + interpret + '\'' +
                ", questionOptionList=" + questionOptionList +
                ", questionAnswerList=" + questionAnswerList +
                ", topicId=" + topicId +
                ", seriesId=" + seriesId +
                ", questionGroup=" + questionGroup +
                ", questionGroupName='" + questionGroupName + '\'' +
                ", questionTypeDetail='" + questionTypeDetail + '\'' +
                ", answerNum=" + answerNum +
                ", intelligent=" + intelligent +
                ", subQuestionSeqNum=" + subQuestionSeqNum +
                ", isTeacherQuestion=" + isTeacherQuestion +
                ", chapterId1=" + chapterId1 +
                ", chapterId2=" + chapterId2 +
                ", chapterId3=" + chapterId3 +
                ", visible=" + visible +
                ", structId=" + structId +
                ", quoteNum=" + quoteNum +
                ", submitNum=" + submitNum +
                ", accuracy=" + accuracy +
                ", paperName='" + paperName + '\'' +
                ", orgType=" + orgType +
                ", orgId=" + orgId +
                ", groupId=" + groupId +
                ", customerDirectoryIds=" + customerDirectoryIds +
                ", htmlData='" + htmlData + '\'' +
                ", jsonData='" + jsonData + '\'' +
                ", uploadSrc=" + uploadSrc +
                ", uploadId=" + uploadId +
                ", uploadTime=" + uploadTime +
                ", uploadName='" + uploadName + '\'' +
                ", questionSource=" + questionSource +
                ", specials=" + specials +
                ", collectionNum=" + collectionNum +
                ", favTime=" + favTime +
                '}';
    }

    public Date getFavTime() {
        return favTime;
    }

    public void setFavTime(Date favTime) {
        this.favTime = favTime;
    }

    public Long getCollectionNum() {
        return collectionNum;
    }

    public void setCollectionNum(Long collectionNum) {
        this.collectionNum = collectionNum;
    }

    public List<Long> getSpecials() {
        return specials;
    }

    public void setSpecials(List<Long> specials) {
        this.specials = specials;
    }

    public String getUploadName() {
        return uploadName;
    }

    public void setUploadName(String uploadName) {
        this.uploadName = uploadName;
    }

    public Integer getQuestionSource() {
        return questionSource;
    }

    public void setQuestionSource(Integer questionSource) {
        this.questionSource = questionSource;
    }

    public int getIsTeacherQuestion() {
        return isTeacherQuestion;
    }

    public void setIsTeacherQuestion(int isTeacherQuestion) {
        this.isTeacherQuestion = isTeacherQuestion;
    }

    public Integer getSubQuestionSeqNum() {
        return subQuestionSeqNum;
    }

    public void setSubQuestionSeqNum(Integer subQuestionSeqNum) {
        this.subQuestionSeqNum = subQuestionSeqNum;
    }

    public Integer getIntelligent() {
        return intelligent;
    }

    public void setIntelligent(Integer intelligent) {
        this.intelligent = intelligent;
    }




    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getMastery() {
        return mastery;
    }

    public void setMastery(int mastery) {
        this.mastery = mastery;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public void setHighQual(int highQual) {
        this.highQual = highQual;
    }

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

    public long getCountRef() {
        return countRef;
    }

    public void setCountRef(long countRef) {
        this.countRef = countRef;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public int getCountOptions() {
        return countOptions;
    }

    public void setCountOptions(int countOptions) {
        this.countOptions = countOptions;
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

    public String getMultiScoreAnswer() {
        return multiScoreAnswer;
    }

    public void setMultiScoreAnswer(String multiScoreAnswer) {
        this.multiScoreAnswer = multiScoreAnswer;
    }

    public Question getParentQuestion() {
        return parentQuestion;
    }

    public void setParentQuestion(Question parentQuestion) {
        this.parentQuestion = parentQuestion;
    }

    public List<Question> getSubQuestions() {
        return subQuestions;
    }

    public void setSubQuestions(List<Question> subQuestions) {
        this.subQuestions = subQuestions;
    }


    public int getCountTopic() {
        return countTopic;
    }

    public void setCountTopic(int countTopic) {
        this.countTopic = countTopic;
    }


    public long getParentQuestionId() {
        return parentQuestionId;
    }

    public void setParentQuestionId(long parentQuestionId) {
        this.parentQuestionId = parentQuestionId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public long getQrId() {
        return qrId;
    }

    public void setQrId(long qrId) {
        this.qrId = qrId;
    }


    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public List<Series> getSeriesList() {
        return seriesList;
    }

    public void setSeriesList(List<Series> seriesList) {
        this.seriesList = seriesList;
    }

    public List<Topic> getTopicList() {
        return topicList;
    }

    public void setTopicList(List<Topic> topicList) {
        this.topicList = topicList;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }



    public Integer getUploadSrc() {
        return uploadSrc;
    }

    public void setUploadSrc(Integer uploadSrc) {
        this.uploadSrc = uploadSrc;
    }

    public Long getUploadId() {
        return uploadId;
    }

    public void setUploadId(Long uploadId) {
        this.uploadId = uploadId;
    }

    public Date getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }

    public List<Long> getAllTopicIds() {
        return allTopicIds;
    }

    public void setAllTopicIds(List<Long> allTopicIds) {
        this.allTopicIds = allTopicIds;
    }

    public List<Long> getAllUnitIds() {
        return allUnitIds;
    }

    public void setAllUnitIds(List<Long> allUnitIds) {
        this.allUnitIds = allUnitIds;
    }

    public List<Long> getAllModuleIds() {
        return allModuleIds;
    }

    public void setAllModuleIds(List<Long> allModuleIds) {
        this.allModuleIds = allModuleIds;
    }

    public String getAllTopicIdStr() {
        return allTopicIdStr;
    }

    public void setAllTopicIdStr(String allTopicIdStr) {
        this.allTopicIdStr = allTopicIdStr;
    }

    public String getAllUnitIdStr() {
        return allUnitIdStr;
    }

    public void setAllUnitIdStr(String allUnitIdStr) {
        this.allUnitIdStr = allUnitIdStr;
    }

    public String getAllModuleIdStr() {
        return allModuleIdStr;
    }

    public void setAllModuleIdStr(String allModuleIdStr) {
        this.allModuleIdStr = allModuleIdStr;
    }

    public List<Long> getAllLeafQuesIds() {
        return allLeafQuesIds;
    }

    public void setAllLeafQuesIds(List<Long> allLeafQuesIds) {
        this.allLeafQuesIds = allLeafQuesIds;
    }

    public Integer getNewFormat() {
        return newFormat;
    }

    public void setNewFormat(Integer newFormat) {
        this.newFormat = newFormat;
    }

    public Integer getQuestionTypeId() {
        return questionTypeId;
    }

    public void setQuestionTypeId(Integer questionTypeId) {
        this.questionTypeId = questionTypeId;
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

    public Object getQuestionOptionList() {
        return questionOptionList;
    }

    public void setQuestionOptionList(Object questionOptionList) {
        this.questionOptionList = questionOptionList;
    }

    public Object getQuestionAnswerList() {
        return questionAnswerList;
    }

    public void setQuestionAnswerList(Object questionAnswerList) {
        this.questionAnswerList = questionAnswerList;
    }

    public List<Long> getTopicId() {
        return topicId;
    }

    public void setTopicId(List<Long> topicId) {
        this.topicId = topicId;
    }

    public List<Long> getSeriesId() {
        return seriesId;
    }

    public void setSeriesId(List<Long> seriesId) {
        this.seriesId = seriesId;
    }

    public Long getQuestionGroup() {
        return questionGroup;
    }

    public void setQuestionGroup(Long questionGroup) {
        this.questionGroup = questionGroup;
    }

    public String getQuestionGroupName() {
        return questionGroupName;
    }

    public void setQuestionGroupName(String questionGroupName) {
        this.questionGroupName = questionGroupName;
    }

    public String getQuestionTypeDetail() {
        return questionTypeDetail;
    }

    public void setQuestionTypeDetail(String questionTypeDetail) {
        this.questionTypeDetail = questionTypeDetail;
    }

    public Integer getAnswerNum() {
        return answerNum;
    }

    public void setAnswerNum(Integer answerNum) {
        this.answerNum = answerNum;
    }

    public int getHighQual() {
        return highQual;
    }

    public List<Long> getChapterId1() {
        return chapterId1;
    }

    public void setChapterId1(List<Long> chapterId1) {
        this.chapterId1 = chapterId1;
    }

    public List<Long> getChapterId2() {
        return chapterId2;
    }

    public void setChapterId2(List<Long> chapterId2) {
        this.chapterId2 = chapterId2;
    }

    public List<Long> getChapterId3() {
        return chapterId3;
    }

    public void setChapterId3(List<Long> chapterId3) {
        this.chapterId3 = chapterId3;
    }

    public String getQuestionBody() {
        return questionBody;
    }

    public void setQuestionBody(String questionBody) {
        this.questionBody = questionBody;
    }

    public String getQuestionOptions() {
        return questionOptions;
    }

    public void setQuestionOptions(String questionOptions) {
        this.questionOptions = questionOptions;
    }

    public String getQuestionAnalysis() {
        return StringUtils.isBlank(questionAnalysis) ? "略" : questionAnalysis; // FIXME: 2016/11/25 如果解析是空，返回“略”临时上线需要 在下一版本中整体改正
    }

    public void setQuestionAnalysis(String questionAnalysis) {
        this.questionAnalysis = questionAnalysis;
    }

    public boolean isSingle() {
        return this.single;
    }

    public void setSingle(boolean single) {
        this.single = single;
    }

    public int getVisible() {
        return visible;
    }

    public void setVisible(int visible) {
        this.visible = visible;
    }

    public QuestionAudio getAudioJson() {
        return audioJson;
    }

    public void setAudioJson(QuestionAudio audioJson) {
        this.audioJson = audioJson;
    }

    public long getStructId() {
        return structId;
    }

    public void setStructId(long structId) {
        this.structId = structId;
    }

    public int getQuoteNum() {
        return quoteNum;
    }

    public void setQuoteNum(int quoteNum) {
        this.quoteNum = quoteNum;
    }

    public int getSubmitNum() {
        return submitNum;
    }

    public void setSubmitNum(int submitNum) {
        this.submitNum = submitNum;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public String getPaperName() {
        return paperName;
    }

    public void setPaperName(String paperName) {
        this.paperName = paperName;
    }

    public Integer getOrgType() {
        return orgType;
    }

    public void setOrgType(Integer orgType) {
        this.orgType = orgType;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }


    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public List<Map<String, Object>> getCustomerDirectoryIds() {
        return customerDirectoryIds;
    }

    public void setCustomerDirectoryIds(List<Map<String, Object>> customerDirectoryIds) {
        this.customerDirectoryIds = customerDirectoryIds;
    }

    public Object getPrompt() {
        return prompt;
    }

    public void setPrompt(Object prompt) {
        this.prompt = prompt;
    }

    public String getHtmlData() {
        return htmlData;
    }

    public void setHtmlData(String htmlData) {
        this.htmlData = htmlData;
    }

    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }

    public Object getJsonMap() {
        return jsonMap;
    }

    public void setJsonMap(Object jsonMap) {
        this.jsonMap = jsonMap;
    }

}
