package com.noriental.praxissvr.brush.request;

import java.io.Serializable;

import com.noriental.validate.bean.BaseRequest;

import javax.validation.constraints.NotNull;

/**
 * 刷题
 * 获得作业时的参数
 *
 * @author shengxian.xiao
 */
@Deprecated
public class StudentWorkInput extends BaseRequest implements Serializable {
    private static final long serialVersionUID = 5899297073442540187L;
    //作业名称
    private String workName;
    //体系 1知识体系 2教材体系
    @NotNull
    private Integer resourceType;
    //作业级别 1模块，2单元，3主题
    @NotNull
    private Integer workLevel;
    //学生id
    @NotNull
    private Long studentId;
    //科目id
    private Long subjectId;
    //	//模块id
//	private Long moduleId;
//	//单元id
//	private Long unitId;
//	//主题id
//	private Long topicId;
    //知识点id（模块，单元，主题）
    @NotNull
    private Long levelId;

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

    public Long getLevelId() {
        return levelId;
    }

    public void setLevelId(Long levelId) {
        this.levelId = levelId;
    }

   public void setResourceType(Integer resourceType) {
	this.resourceType = resourceType;
   }
   public Integer getResourceType() {
	return resourceType;
   }


}
