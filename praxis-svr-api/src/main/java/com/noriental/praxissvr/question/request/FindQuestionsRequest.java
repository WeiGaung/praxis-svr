package com.noriental.praxissvr.question.request;

import com.noriental.praxissvr.common.*;
import com.noriental.praxissvr.common.constraints.Pageable;
import com.noriental.praxissvr.question.bean.QuesSearchPerm;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author chenlihua
 * @date 2015/12/22
 * @time 11:26
 */
@Pageable
public class FindQuestionsRequest extends PageBaseRequest {

    private boolean sameFilter = false;

    private Long uploadId;//上传者
    private Long subjectId;//学科ID

    private boolean newFormat;//新题型
    private QuestionVisibleEnum visible = QuestionVisibleEnum.ALL;//习题是否可见，默认不过滤此条件

    @NotNull
    private QuestionTypeEnum questionType;

    private Integer questionTypeId;

    private Difficulty difficulty; //难易程度

    private Integer questionGroup;

    private List<Long> moduleIds;

    private List<Long> unitIds;

    private List<Long> topicIds;

    private List<Integer> seriesIds;

    private List<Long> ids;

    private Long paperId;

    private Long paperTypeId;

    private Long gradeId;

    private String paperRegion;
    private String paperProv;
    private String paperCity;
    private String paperCounty;
    private Mastery mastery;
    private Boolean highQual;
    private List<QuestionSort> sorts;
    private Long chapterId;
    private boolean queryTrunk;
    private List<QuestionState> states = Collections.singletonList(QuestionState.ENABLED);
    private Boolean isAudio;
    private Date uploadTimeStart;
    private Date uploadTimeEnd;
    //搜索 试卷名称/试卷题号/创建人/创建人ID
    private String paperKeyword;
    private boolean basic;//true：查询基本信息
    private List<QuestionState> subStates;//子题的状态
    private long orgId;
    private QuesSearchPerm quesSearchPerm;//题目查询权限，其它私立机构查询不到

    //当前访问系统的老师ID，用以判断题目是不是该老师的
    private Long currentSystemId;

    public Long getCurrentSystemId() {
        return currentSystemId;
    }

    @Override
    public String toString() {
        return "FindQuestionsRequest{" +
                "sameFilter=" + sameFilter +
                ", uploadId=" + uploadId +
                ", subjectId=" + subjectId +
                ", newFormat=" + newFormat +
                ", visible=" + visible +
                ", questionType=" + questionType +
                ", questionTypeId=" + questionTypeId +
                ", difficulty=" + difficulty +
                ", questionGroup=" + questionGroup +
                ", moduleIds=" + moduleIds +
                ", unitIds=" + unitIds +
                ", topicIds=" + topicIds +
                ", seriesIds=" + seriesIds +
                ", ids=" + ids +
                ", paperId=" + paperId +
                ", paperTypeId=" + paperTypeId +
                ", gradeId=" + gradeId +
                ", paperRegion='" + paperRegion + '\'' +
                ", paperProv='" + paperProv + '\'' +
                ", paperCity='" + paperCity + '\'' +
                ", paperCounty='" + paperCounty + '\'' +
                ", mastery=" + mastery +
                ", highQual=" + highQual +
                ", sorts=" + sorts +
                ", chapterId=" + chapterId +
                ", queryTrunk=" + queryTrunk +
                ", states=" + states +
                ", isAudio=" + isAudio +
                ", uploadTimeStart=" + uploadTimeStart +
                ", uploadTimeEnd=" + uploadTimeEnd +
                ", paperKeyword='" + paperKeyword + '\'' +
                ", basic=" + basic +
                ", subStates=" + subStates +
                ", orgId=" + orgId +
                ", quesSearchPerm=" + quesSearchPerm +
                ", currentSystemId=" + currentSystemId +
                '}';
    }

    public void setCurrentSystemId(Long currentSystemId) {
        this.currentSystemId = currentSystemId;
    }


    public FindQuestionsRequest() {
        setPageable(true);
    }

    public Long getUploadId() {
        return uploadId;
    }

    public void setUploadId(Long uploadId) {
        this.uploadId = uploadId;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public boolean isNewFormat() {
        return newFormat;
    }

    public void setNewFormat(boolean newFormat) {
        this.newFormat = newFormat;
    }

    public QuestionTypeEnum getQuestionType() {
        return questionType;
    }

    public void setQuestionType(QuestionTypeEnum questionType) {
        this.questionType = questionType;
    }

    public Integer getQuestionTypeId() {
        return questionTypeId;
    }

    public void setQuestionTypeId(Integer questionTypeId) {
        this.questionTypeId = questionTypeId;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public Integer getQuestionGroup() {
        return questionGroup;
    }

    public void setQuestionGroup(Integer questionGroup) {
        this.questionGroup = questionGroup;
    }

    public List<Long> getModuleIds() {
        return moduleIds;
    }

    public void setModuleIds(List<Long> moduleIds) {
        this.moduleIds = moduleIds;
    }

    public List<Long> getUnitIds() {
        return unitIds;
    }

    public void setUnitIds(List<Long> unitIds) {
        this.unitIds = unitIds;
    }

    public List<Long> getTopicIds() {
        return topicIds;
    }

    public void setTopicIds(List<Long> topicIds) {
        this.topicIds = topicIds;
    }

    public List<Integer> getSeriesIds() {
        return seriesIds;
    }

    public void setSeriesIds(List<Integer> seriesIds) {
        this.seriesIds = seriesIds;
    }

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
        setStates(Collections.singletonList(QuestionState.ALL));
    }

    public Long getPaperId() {
        return paperId;
    }

    public void setPaperId(Long paperId) {
        this.paperId = paperId;
    }

    public Long getGradeId() {
        return gradeId;
    }

    public void setGradeId(Long gradeId) {
        this.gradeId = gradeId;
    }

    public Long getPaperTypeId() {
        return paperTypeId;
    }

    public void setPaperTypeId(Long paperTypeId) {
        this.paperTypeId = paperTypeId;
    }

    public String getPaperRegion() {
        return paperRegion;
    }

    public void setPaperRegion(String paperRegion) {
        this.paperRegion = paperRegion;
    }

    public String getPaperProv() {
        return paperProv;
    }

    public void setPaperProv(String paperProv) {
        this.paperProv = paperProv;
    }

    public String getPaperCity() {
        return paperCity;
    }

    public void setPaperCity(String paperCity) {
        this.paperCity = paperCity;
    }

    public String getPaperCounty() {
        return paperCounty;
    }

    public void setPaperCounty(String paperCounty) {
        this.paperCounty = paperCounty;
    }

    public Mastery getMastery() {
        return mastery;
    }

    public void setMastery(Mastery mastery) {
        this.mastery = mastery;
    }

    public Boolean getHighQual() {
        return highQual;
    }

    public void setHighQual(Boolean highQual) {
        this.highQual = highQual;
    }

    public List<QuestionSort> getSorts() {
        return sorts;
    }

    public void setSorts(List<QuestionSort> sorts) {
        this.sorts = sorts;
    }

    public Long getChapterId() {
        return chapterId;
    }

    public void setChapterId(Long chapterId) {
        this.chapterId = chapterId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public List<QuestionState> getStates() {
        return states;
    }

    public void setStates(List<QuestionState> states) {
        this.states = states;
    }

    public boolean isQueryTrunk() {
        return queryTrunk;
    }

    public void setQueryTrunk(boolean queryTrunk) {
        this.queryTrunk = queryTrunk;
    }

    public QuestionVisibleEnum getVisible() {
        return visible;
    }

    public void setVisible(QuestionVisibleEnum visible) {
        this.visible = visible;
    }

    public Boolean getAudio() {
        return isAudio;
    }

    public void setAudio(Boolean audio) {
        isAudio = audio;
    }

    public Date getUploadTimeStart() {
        return uploadTimeStart;
    }

    public void setUploadTimeStart(Date uploadTimeStart) {
        this.uploadTimeStart = uploadTimeStart;
    }

    public Date getUploadTimeEnd() {
        return uploadTimeEnd;
    }

    public void setUploadTimeEnd(Date uploadTimeEnd) {
        this.uploadTimeEnd = uploadTimeEnd;
    }

    public String getPaperKeyword() {
        return paperKeyword;
    }

    public void setPaperKeyword(String paperKeyword) {
        this.paperKeyword = paperKeyword;
    }

    public boolean isSameFilter() {
        return sameFilter;
    }

    public void setSameFilter(boolean sameFilter) {
        this.sameFilter = sameFilter;
    }

    public boolean isBasic() {
        return basic;
    }

    public void setBasic(boolean basic) {
        this.basic = basic;
    }

    public List<QuestionState> getSubStates() {
        return subStates;
    }

    public void setSubStates(List<QuestionState> subStates) {
        this.subStates = subStates;
    }

    public long getOrgId() {
        return orgId;
    }

    public void setOrgId(long orgId) {
        this.orgId = orgId;
    }

    public QuesSearchPerm getQuesSearchPerm() {
        return quesSearchPerm;
    }

    public void setQuesSearchPerm(QuesSearchPerm quesSearchPerm) {
        this.quesSearchPerm = quesSearchPerm;
    }
}
