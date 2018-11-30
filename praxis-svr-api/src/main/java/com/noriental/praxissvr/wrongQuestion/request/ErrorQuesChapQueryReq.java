package com.noriental.praxissvr.wrongQuestion.request;

import com.noriental.validate.bean.BaseRequest;

import javax.validation.constraints.NotNull;

/**
 * @author kate
 * @create 2017-10-30 15:56
 * @desc 教师空间学生知识点章节错题次数统计
 **/
public class ErrorQuesChapQueryReq extends BaseRequest {

    //学生ID
    @NotNull
    private Long StudentId;
    //知识点章节id
    @NotNull
    private Long pointId;
    //知识点章节层级
    @NotNull
    private Integer level;
    //查询的时间段
    private Integer days;
    //错题类型
    @NotNull
    private Integer dataType;

    /**
     * 是否按照错题次数排序 ture升序 false降序 null 不做处理
     */
    private Boolean sortByErrorNum;

    public Long getStudentId() {
        return StudentId;
    }

    public void setStudentId(Long studentId) {
        StudentId = studentId;
    }

    public Long getPointId() {
        return pointId;
    }

    public void setPointId(Long pointId) {
        this.pointId = pointId;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public Boolean getSortByErrorNum() {
        return sortByErrorNum;
    }

    public void setSortByErrorNum(Boolean sortByErrorNum) {
        this.sortByErrorNum = sortByErrorNum;
    }

    public Integer getDataType() {
        return dataType;
    }

    public void setDataType(Integer dataType) {
        this.dataType = dataType;
    }
}
