package com.noriental.praxissvr.brush.request;

import java.io.Serializable;

import com.noriental.validate.bean.BaseRequest;


public class StudentWorkPageInput extends BaseRequest implements Serializable {

    private static final long serialVersionUID = 1321709436293806587L;
    private Long studentId;
    @Deprecated
    private Integer stuType;
    private Integer pagesize;
    private Integer currentpage;
    private Long subjectId;


    private Integer workLevel;
    private Long levelId;

    private WorkStatusEnum workStatusEnum;

    public WorkStatusEnum getWorkStatusEnum() {
        return workStatusEnum;
    }

    public void setWorkStatusEnum(WorkStatusEnum workStatusEnum) {
        this.workStatusEnum = workStatusEnum;
    }

    public Long getLevelId() {
        return levelId;
    }

    public void setLevelId(Long levelId) {
        this.levelId = levelId;
    }

    public Integer getWorkLevel() {
        return workLevel;
    }

    public void setWorkLevel(Integer workLevel) {
        this.workLevel = workLevel;
    }

    public Integer getPagesize() {
        return pagesize;
    }

    public void setPagesize(Integer pagesize) {
        this.pagesize = pagesize;
    }

    public Integer getCurrentpage() {
        return currentpage;
    }

    public void setCurrentpage(Integer currentpage) {
        this.currentpage = currentpage;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getStudentId() {
        return studentId;
    }
    @Deprecated
    public Integer getStuType() {
        return stuType;
    }
    @Deprecated
    public void setStuType(Integer stuType) {
        this.stuType = stuType;
    }

    public enum WorkStatusEnum{
        NO_COMPLETE(2),
        COMPLETE(1);
        int status;
        WorkStatusEnum(int status){
            this.status = status;
        }
        public int getStatus(){
            return status;
        }
    }

}
