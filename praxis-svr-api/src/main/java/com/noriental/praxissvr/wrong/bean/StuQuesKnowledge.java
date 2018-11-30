package com.noriental.praxissvr.wrong.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 学生分知识点已做题目、错题记录
 *
 * @author shengxian.xiao
 */
public class StuQuesKnowledge implements Serializable {

    

    private static final long serialVersionUID = -6298988063858102189L;

    private List<Long> idList;
    private Long id;
    private Long studentId;
    private Integer dataType;
    private List<Integer> dataTypeList;
    private Long subjectId;
    private Long moduleId;
    private Long unitId;
    private Long topicId;
    private Long questionId;
    private Date createTime;
    private int questionCount;
    private Integer seq;
    private Long directoryId;
    private Integer dataStatus;
    private String tableName;

    public Long getDirectoryId() {
        return directoryId;
    }

    public void setDirectoryId(Long directoryId) {
        this.directoryId = directoryId;
    }

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    private List<StuQuesKnowledge> stuQuesKnowledgeList;

    public List<StuQuesKnowledge> getStuQuesKnowledgeList() {
        return stuQuesKnowledgeList;
    }

    public void setStuQuesKnowledgeList(List<StuQuesKnowledge> stuQuesKnowledgeList) {
        this.stuQuesKnowledgeList = stuQuesKnowledgeList;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getTopicId() {
        return topicId;
    }


    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    public List<Long> getIdList() {
        return idList;
    }

    public void setIdList(List<Long> idList) {
        this.idList = idList;
    }


    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public Long getModuleId() {
        return moduleId;
    }

    public void setModuleId(Long moduleId) {
        this.moduleId = moduleId;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Integer getDataType() {
        return dataType;
    }

    public void setDataType(Integer dataType) {
        this.dataType = dataType;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setQuestionCount(int questionCount) {
        this.questionCount = questionCount;
    }

    public int getQuestionCount() {
        return questionCount;
    }

    public List<Integer> getDataTypeList() {
        return dataTypeList;
    }

    public void setDataTypeList(List<Integer> dataTypeList) {
        this.dataTypeList = dataTypeList;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null) {
            if(obj.getClass().equals(this.getClass())){
                StuQuesKnowledge s1 = (StuQuesKnowledge) obj;
                Integer dataType = s1.getDataType();
                Long topicId = s1.getTopicId();
                Long moduleId = s1.getModuleId();
                Long unitId = s1.getUnitId();
                Long quesId = s1.getQuestionId();

                if (dataType.equals(this.dataType) && quesId.equals(this.questionId)) {
                    if( this.topicId!=null && topicId!=null && this.topicId.equals(topicId) ){ //从主题依次比较，如果相等，则是同一个
                        return true;
                    }else  if(this.topicId==null && topicId==null){
                        if( this.unitId!=null && unitId!=null && this.unitId.equals(unitId) ){
                            return true;
                        }else if(this.unitId==null && unitId==null ){
                            if( this.moduleId!=null && moduleId!=null && this.moduleId.equals(moduleId)){
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    public Integer getDataStatus() {
        return dataStatus;
    }

    public void setDataStatus(Integer dataStatus) {
        this.dataStatus = dataStatus;
    }
}
