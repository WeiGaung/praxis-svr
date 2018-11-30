package com.noriental.praxissvr.wrong.request;

import com.noriental.praxissvr.common.StuAnswerConstant.DataTypeEnum;
import com.noriental.praxissvr.common.StuAnswerConstant.LevelEnum;
import com.noriental.validate.bean.BaseRequest;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;


public class FindWrongQuesAnswersRequest extends BaseRequest {
    @NotNull
    private LevelEnum level;
    @NotNull
    private Long levelId;
    @Min(1)
    private Integer pageSize;
    @Min(0)
    private Integer fromIndex;
    @NotNull
    private Long studentId;
    @NotNull
    private DataTypeEnum dataType;

    public LevelEnum getLevel() {
        return level;
    }

    public void setLevel(LevelEnum level) {
        this.level = level;
    }

    public Long getLevelId() {
        return levelId;
    }

    public void setLevelId(Long levelId) {
        this.levelId = levelId;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getFromIndex() {
        return fromIndex;
    }

    public void setFromIndex(Integer fromIndex) {
        this.fromIndex = fromIndex;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public DataTypeEnum getDataType() {
        return dataType;
    }

    public void setDataType(DataTypeEnum dataType) {
        this.dataType = dataType;
    }
}
