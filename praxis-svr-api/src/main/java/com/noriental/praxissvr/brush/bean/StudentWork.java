package com.noriental.praxissvr.brush.bean;

import com.noriental.praxissvr.answer.bean.StudentExercise;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * 学生作业记录表对象
 *
 * @author shengxian.xiao
 */
public class StudentWork implements Serializable {
    private static final long serialVersionUID = -1693614147622767749L;



    public static class WorkLevel {
        public static final Integer MODULE = 1;
        public static final Integer UNIT = 2;
        public static final Integer TOPIC = 3;
    }

    //数据库字段开始
    private Long id;
    private String workName;//作业名称
    private Integer resourceType;//体系
    private Integer workLevel;//作业级别
    private Long studentId;//学生id
    private Long subjectId;//科目id
    private Long moduleId;//模块id
    private Long unitId;//单元id
    private Long topicId;//主题id
    private Integer workStatus;//作业状态1完成，2未完成
    private Date createTime;//创建时间
    private Date lastUpdateTime;//最后更新时间
    private Integer year;
    private Integer type;
    //数据库字段结束

    //业务字段开始
    List<StudentExercise> studentExercises;//答题记录
    private Integer parentQuesNumber;//大题数量
    private Integer quesNumber;//小题数量
    private Integer rightNumber;//答对数量
    private String baseurl;//答案图片地址
    @Deprecated
    private String moduleName;//模块id
    //业务字段结束

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getWorkName() {
        return workName;
    }

    public void setWorkName(String workName) {
        this.workName = workName;
    }

    public Integer getWorkLevel() {
        return workLevel;
    }

    public void setWorkLevel(Integer workLevel) {
        this.workLevel = workLevel;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
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

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    public Integer getWorkStatus() {
        return workStatus;
    }

    public void setWorkStatus(Integer workStatus) {
        this.workStatus = workStatus;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public void setStudentExercises(List<StudentExercise> studentExercises) {
        this.studentExercises = studentExercises;
    }

    public List<StudentExercise> getStudentExercises() {
        return studentExercises;
    }

    public Integer getQuesNumber() {
        return quesNumber;
    }

    public void setQuesNumber(Integer quesNumber) {
        this.quesNumber = quesNumber;
    }

    public Integer getRightNumber() {
        return rightNumber;
    }

    public void setRightNumber(Integer rightNumber) {
        this.rightNumber = rightNumber;
    }

    public String getBaseurl() {
        return baseurl;
    }

    public void setBaseurl(String baseurl) {
        this.baseurl = baseurl;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }
   public void setParentQuesNumber(Integer parentQuesNumber) {
	this.parentQuesNumber = parentQuesNumber;
   }
   public Integer getParentQuesNumber() {
	return parentQuesNumber;
   }
   public void setResourceType(Integer resourceType) {
	this.resourceType = resourceType;
}
   
  public Integer getResourceType() {
	return resourceType;
}

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * 获得刷题级别
     * @return
     */
    public Long getLevelId() {
        if(workLevel!=null){
            if(workLevel.equals(WorkLevel.MODULE)){
                return moduleId;
            }
            if(workLevel.equals(WorkLevel.UNIT)){
                return unitId;
            }
            if(workLevel.equals(WorkLevel.TOPIC)){
                return topicId;
            }
        }
        return 0L;
    }
}
