package com.noriental.praxissvr.answer.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.noriental.validate.bean.BaseRequest;

import java.util.Date;
import java.util.List;

/**
 * 求助批改
 *
 * @author
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FlowTurn extends BaseRequest{

    private static final long serialVersionUID = 5266343020421718153L;

    //题id
    private Long questionId;
    //父题id
    private Long parentQuestionId;
    //自定义目录id
    private Long customListId;
    //自定义目录分组id
    private Long groupId;
    //自定义目录分组名称
    private String groupName;
    //题类型
    private String questionType;
    //题简介
    private String htmlData;
    //题创建时间
    private Date uploadTime;
    //是否批改  3:已批改   4:未批改
    private Integer flag;
    //该题未批改/已批改人数
    private Integer num;
    //该题的总人数
    private Integer totalNum;
    //流转截止时间
    private Date deadline;
    //是否支持智能批改：0-否；1-是
    private Integer intelligent;
    //老师system_id
    private Long systemId;

    //学生id
    private Long studentId;

    //题目结构id
    private Integer structId;

    //最后批改时间
    private Date correctorTime;

    //是否已批改 3:已批改 4:未批改
    private Integer is_corrected;

    //章节全称
    private String chapterFullName;

    //答题公布时间
    private Date promulgate;

    private List<FlowTurn> questionIdList;



    /**
     * 选项数量
     */
    private int countOptions = 0;
    /**
     * 难度
     */
    private int difficulty;
    /**
     * 是否精品题
     */
    private int highQual;
    /**
     * 掌握层级
     */
    private int mastery;
    /**
     * 历史优选题答案 Format example: {"A":10,"B":9,"C":5,"D":1}
     */
    private String multiScoreAnswer;
    /**
     * 正确选项 Format example: "A" or "A,B,D"
     */
    private String rightOption;

    private boolean isSingle;
    /**
     * 状态: UNEVALED(未审核)/ENABLED(启用)/DISABLED(停用)
     */
    private String state;

    private long qrId;

    /**
     * 关联知识点数
     */
    private int countTopic = 0;

    private Long subjectId;

    private Date updateTime;

    private Long uploadId;

    private Integer uploadSrc;

    private Integer newFormat;// 新题标志

    private Long questionGroup;// 组ID,详见entity_group

    private Integer answerNum;// 答案个数

    private Integer questionTypeId;//题型id

    private int visible;

    private Long orgId;

    private Integer orgType;

    private String source;//原文

    private String jsonData;

    /**
     * 新题型作图题的底图数据
     */
    private Object jsonMap;

    /*public FlowTurn(Integer num){
        super();
        this.num = num;
    }*/


    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getHtmlData() {
        return htmlData;
    }

    public void setHtmlData(String htmlData) {
        this.htmlData = htmlData;
    }

    public Date getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public Integer getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(Integer totalNum) {
        this.totalNum = totalNum;
    }

    public Integer getIntelligent() {
        return intelligent;
    }

    public void setIntelligent(Integer intelligent) {
        this.intelligent = intelligent;
    }

    public Long getSystemId() {
        return systemId;
    }

    public void setSystemId(Long systemId) {
        this.systemId = systemId;
    }

    public String getChapterFullName() {
        return chapterFullName;
    }

    public void setChapterFullName(String chapterFullName) {
        this.chapterFullName = chapterFullName;
    }

    public Long getCustomListId() {
        return customListId;
    }

    public void setCustomListId(Long customListId) {
        this.customListId = customListId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Integer getStructId() {
        return structId;
    }

    public void setStructId(Integer structId) {
        this.structId = structId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Date getCorrectorTime() {
        return correctorTime;
    }

    public void setCorrectorTime(Date correctorTime) {
        this.correctorTime = correctorTime;
    }

    public Integer getIs_corrected() {
        return is_corrected;
    }

    public void setIs_corrected(Integer is_corrected) {
        this.is_corrected = is_corrected;
    }

    public int getCountOptions() {
        return countOptions;
    }

    public void setCountOptions(int countOptions) {
        this.countOptions = countOptions;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getHighQual() {
        return highQual;
    }

    public void setHighQual(int highQual) {
        this.highQual = highQual;
    }

    public int getMastery() {
        return mastery;
    }

    public void setMastery(int mastery) {
        this.mastery = mastery;
    }

    public String getMultiScoreAnswer() {
        return multiScoreAnswer;
    }

    public void setMultiScoreAnswer(String multiScoreAnswer) {
        this.multiScoreAnswer = multiScoreAnswer;
    }

    public String getRightOption() {
        return rightOption;
    }

    public void setRightOption(String rightOption) {
        this.rightOption = rightOption;
    }

    public boolean isSingle() {
        return isSingle;
    }

    public void setSingle(boolean single) {
        isSingle = single;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public long getQrId() {
        return qrId;
    }

    public void setQrId(long qrId) {
        this.qrId = qrId;
    }

    public int getCountTopic() {
        return countTopic;
    }

    public void setCountTopic(int countTopic) {
        this.countTopic = countTopic;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Long getUploadId() {
        return uploadId;
    }

    public void setUploadId(Long uploadId) {
        this.uploadId = uploadId;
    }

    public Integer getUploadSrc() {
        return uploadSrc;
    }

    public void setUploadSrc(Integer uploadSrc) {
        this.uploadSrc = uploadSrc;
    }

    public Integer getNewFormat() {
        return newFormat;
    }

    public void setNewFormat(Integer newFormat) {
        this.newFormat = newFormat;
    }

    public Long getQuestionGroup() {
        return questionGroup;
    }

    public void setQuestionGroup(Long questionGroup) {
        this.questionGroup = questionGroup;
    }

    public Integer getAnswerNum() {
        return answerNum;
    }

    public void setAnswerNum(Integer answerNum) {
        this.answerNum = answerNum;
    }

    public Integer getQuestionTypeId() {
        return questionTypeId;
    }

    public void setQuestionTypeId(Integer questionTypeId) {
        this.questionTypeId = questionTypeId;
    }

    public int getVisible() {
        return visible;
    }

    public void setVisible(int visible) {
        this.visible = visible;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public Integer getOrgType() {
        return orgType;
    }

    public void setOrgType(Integer orgType) {
        this.orgType = orgType;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
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

    public Long getParentQuestionId() {
        return parentQuestionId;
    }

    public void setParentQuestionId(Long parentQuestionId) {
        this.parentQuestionId = parentQuestionId;
    }

    public List<FlowTurn> getQuestionIdList() {
        return questionIdList;
    }

    public void setQuestionIdList(List<FlowTurn> questionIdList) {
        this.questionIdList = questionIdList;
    }

    public Date getPromulgate() {
        return promulgate;
    }

    public void setPromulgate(Date promulgate) {
        this.promulgate = promulgate;
    }
}
