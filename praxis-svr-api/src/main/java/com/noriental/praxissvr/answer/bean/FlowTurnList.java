package com.noriental.praxissvr.answer.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.noriental.praxissvr.common.StuAnswerConstant;
import com.noriental.validate.bean.BaseRequest;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * 求助批改
 * 学生每一个题目的答题记录
 *
 * @author
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FlowTurnList extends BaseRequest{

    private static final long serialVersionUID = 5266343020421718153L;
    private Long id;
    /**
     * 表id
     **/
    private Integer tableId;
    /**
     * 原表主键id
     **/
    private Integer orgId;
    /**
     * 表id
     **/
    private Long studentId;
    /**
     * 学生姓名
     **/
    private String studentName;
    /**
     * 问题
     **/
    private Long questionId;

    /**
     * 问题 ,分割的
     **/
    private String questionIds;

    /**
     * 做题的来源
     **/
    private String exerciseSource;
    /**
     * 做题的来源
     **/
    private String exerciseSourceZh;
    /**
     * 创建的时间
     **/
    private Date createTime;
    /**
     * 答案公布时间
     **/
    private Date promulgate;
    /**
     * 学生提交答案
     **/
    private String submitAnswer;
    /**
     * 答题图片来源
     **/
    private Integer pictureSource;
    /**
     * 答题状态 7/无答案，6/有答案未批改，1/对，2/错，5/半对
     **/
    private String result;

    /**
     * 添加智能批改结果
     */
    private String intellResult;
    /***
     * 当前批改结果
     */
    private String currentResult;

    /**
     * 批改时，作为入参blankResult的备份。blankResult用来存合并的值用来入库。
     * 教师批改目前是单空批改，所以统计的时候要按照单空统计答题正确次数
     */
    private String blankResultInit;
    /**
     * 提交答案时间
     **/
    private Date submitTime;
    /**
     * 班级Id
     **/
    private Long classId;
    /**
     * 批改人Id
     **/
    private Long correctorId;
    /**
     * 批改角色
     **/
    private String correctorRole;
    private Long parentQuestionId;
    private Date correctorTime;
    private Date lastUpdateTime;
    private Integer structId;
    private String questionType;

    /**
     * 题目创建人id
     **/
    private Long creatorId;

    /**
     * 附加数据更新状态 默认0；教师批改或者智能批改：1
     **/
    private Integer extraFlag;

    private List<Integer> structIdList;
    private CorrectOperateType correctOperateType;

    public Integer getStructId() {
        return structId;
    }

    public void setStructId(Integer structId) {
        this.structId = structId;
    }

    /**
     * 题目list
     **/
    private List<Long> questionIdList;
    private List<Long> parentQuesIdList;
    private List<UpdateSubmitAnswerVo> updateSubmitAnswerVoList;

    /**
     * 场景id：作业测评发布表id、错题重做源答题记录id、错题强化源答题记录id、课程同步习题发布表id。
     */
    private Long resourceId;
    private List<Long> resourceIdList;
    /**
     * 教师批注
     */
    private String postilTeacher;
    /**
     * 教师批注时间
     **/
    private Date postilTeacherDate;
    /**
     * 错题历史列表
     */
    private List<FlowTurnList> studentExerciseList;

    /**
     * 错题来源场景
     */
    private String redoSourceZh;

    private Integer year;

    /***
     * 同一答题场景下 通过子场景区分不同的答题业务场景
     */
    private Integer subExerciseSource;
    /***
     * 批改状态，老师重复批改后由错批改为对，将此字段更新为1
     */
    private Integer correctStatus;

    /***
     * 是否是智能批改
     */
    private Boolean isAi;

    /***
     * 是否是学生作答和老师批注合成一张图片
     */
    private Integer isNew;

    private Integer intellPostilStatus;

    private String intellPostil;

    /**
     * 第三方音频批改结果
     */

    private String audioResult;

    private String tableName;

    /***
     * 智能批改点阵数据
     */
    private String intellMatrix;

    private Integer flag;


    private Long publishId;
    private Long linkId;

    //是否支持智能批改：0-否；1-是
    private Integer intelligent;

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    public FlowTurnList() {
    }

    public FlowTurnList(Long studentId, Long questionId, String exerciseSource, Long resourceId, Long
            parentQuestionId) {
        this.studentId = studentId;
        this.questionId = questionId;
        this.exerciseSource = exerciseSource;
        this.resourceId = resourceId;
        this.parentQuestionId = parentQuestionId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getQuestionIds() {
        return questionIds;
    }

    public void setQuestionIds(String questionIds) {
        this.questionIds = questionIds;
    }

    public String getExerciseSource() {
        return exerciseSource;
    }

    public void setExerciseSource(String exerciseSource) {
        this.exerciseSource = exerciseSource;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public String getIntellResult() {
        return intellResult;
    }

    public void setIntellResult(String intellResult) {
        this.intellResult = intellResult;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getSubmitAnswer() {
        return submitAnswer;
    }

    public void setSubmitAnswer(String submitAnswer) {
        this.submitAnswer = submitAnswer;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Date getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(Date submitTime) {
        this.submitTime = submitTime;
    }


    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public Long getCorrectorId() {
        return correctorId;
    }

    public void setCorrectorId(Long correctorId) {
        this.correctorId = correctorId;
    }


    public Long getParentQuestionId() {
        return parentQuestionId;
    }

    public void setParentQuestionId(Long parentQuestionId) {
        this.parentQuestionId = parentQuestionId;
    }

    public Date getCorrectorTime() {
        return correctorTime;
    }

    public void setCorrectorTime(Date correctorTime) {
        this.correctorTime = correctorTime;
    }

    public String getCurrentResult() {
        return currentResult;
    }

    public void setCurrentResult(String currentResult) {
        this.currentResult = currentResult;
    }

    public void setPictureSource(Integer pictureSource) {
        this.pictureSource = pictureSource;
    }

    public Integer getPictureSource() {
        return pictureSource;
    }

    public String getCorrectorRole() {
        return correctorRole;
    }

    public void setCorrectorRole(String correctorRole) {
        this.correctorRole = correctorRole;
    }

    /**
     * @param questionIdList the questionIdList to set
     */
    public void setQuestionIdList(List<Long> questionIdList) {
        this.questionIdList = questionIdList;
    }

    /**
     * @return the questionIdList
     */
    public List<Long> getQuestionIdList() {
        return questionIdList;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setPostilTeacher(String postilTeacher) {
        this.postilTeacher = postilTeacher;
    }

    public String getPostilTeacher() {
        return postilTeacher;
    }

    public void setPostilTeacherDate(Date postilTeacherDate) {
        this.postilTeacherDate = postilTeacherDate;
    }

    public Date getPostilTeacherDate() {
        return postilTeacherDate;
    }

    public void setStudentExerciseList(List<FlowTurnList> studentExerciseList) {
        this.studentExerciseList = studentExerciseList;
    }

    public List<FlowTurnList> getStudentExerciseList() {
        return studentExerciseList;
    }

    public void setRedoSourceZh(String redoSourceZh) {
        this.redoSourceZh = redoSourceZh;
    }

    public void setExerciseSourceZh(String exerciseSourceZh) {
        this.exerciseSourceZh = exerciseSourceZh;
    }

    public String getExerciseSourceZh() {
        return StuAnswerConstant.ExerciseSource.getExerciseSourceNameByCode(exerciseSource);
    }

    public String getBlankResultInit() {
        return blankResultInit;
    }

    public void setBlankResultInit(String blankResultInit) {
        this.blankResultInit = blankResultInit;
    }


    public List<Integer> getStructIdList() {
        return structIdList;
    }

    public void setStructIdList(List<Integer> structIdList) {
        this.structIdList = structIdList;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public List<UpdateSubmitAnswerVo> getUpdateSubmitAnswerVoList() {
        return updateSubmitAnswerVoList;
    }

    public void setUpdateSubmitAnswerVoList(List<UpdateSubmitAnswerVo> updateSubmitAnswerVoList) {
        this.updateSubmitAnswerVoList = updateSubmitAnswerVoList;
    }

    public List<Long> getParentQuesIdList() {
        return parentQuesIdList;
    }

    public void setParentQuesIdList(List<Long> parentQuesIdList) {
        this.parentQuesIdList = parentQuesIdList;
    }

    public List<Long> getResourceIdList() {
        return resourceIdList;
    }

    public void setResourceIdList(List<Long> resourceIdList) {
        this.resourceIdList = resourceIdList;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public CorrectOperateType getCorrectOperateType() {
        return correctOperateType;
    }

    public void setCorrectOperateType(CorrectOperateType correctOperateType) {
        this.correctOperateType = correctOperateType;
    }

    public Integer getSubExerciseSource() {
        return subExerciseSource;
    }

    public void setSubExerciseSource(Integer subExerciseSource) {
        this.subExerciseSource = subExerciseSource;
    }

    public Integer getCorrectStatus() {
        return correctStatus;
    }

    public void setCorrectStatus(Integer correctStatus) {
        this.correctStatus = correctStatus;
    }

    public Boolean getAi() {
        return isAi;
    }

    public void setAi(Boolean ai) {
        isAi = ai;
    }

    public Integer getIsNew() {
        return isNew;
    }

    public void setIsNew(Integer isNew) {
        this.isNew = isNew;
    }

    public String getAudioResult() {
        if(StringUtils.isBlank(audioResult)){
            return null;
        }
        return audioResult;
    }

    public void setAudioResult(String audioResult) {
        this.audioResult = audioResult;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getIntellMatrix() {
        return intellMatrix;
    }

    public void setIntellMatrix(String intellMatrix) {
        this.intellMatrix = intellMatrix;
    }

    public Integer getTableId() {
        return tableId;
    }

    public void setTableId(Integer tableId) {
        this.tableId = tableId;
    }

    public Integer getOrgId() {
        return orgId;
    }

    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public Integer getExtraFlag() {
        return extraFlag;
    }

    public void setExtraFlag(Integer extraFlag) {
        this.extraFlag = extraFlag;
    }

    public String getRedoSourceZh() {
        return redoSourceZh;
    }

    public Long getPublishId() {
        return publishId;
    }

    public void setPublishId(Long publishId) {
        this.publishId = publishId;
    }

    public Long getLinkId() {
        return linkId;
    }

    public void setLinkId(Long linkId) {
        this.linkId = linkId;
    }

    public String getIntellPostil() {
        return intellPostil;
    }

    public void setIntellPostil(String intellPostil) {
        this.intellPostil = intellPostil;
    }

    public Integer getIntellPostilStatus() {
        return intellPostilStatus;
    }

    public void setIntellPostilStatus(Integer intellPostilStatus) {
        this.intellPostilStatus = intellPostilStatus;
    }

    public Integer getIntelligent() {
        return intelligent;
    }

    public void setIntelligent(Integer intelligent) {
        this.intelligent = intelligent;
    }

    public Date getPromulgate() {
        return promulgate;
    }

    public void setPromulgate(Date promulgate) {
        this.promulgate = promulgate;
    }
}
