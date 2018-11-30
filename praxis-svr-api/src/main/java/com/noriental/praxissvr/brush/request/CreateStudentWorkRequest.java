package com.noriental.praxissvr.brush.request;

import com.noriental.validate.bean.BaseRequest;

import javax.validation.constraints.Min;

/**
 * Created by shengxian on 2016/12/15.
 */
public class CreateStudentWorkRequest extends BaseRequest{
    @Min(1)
    private Long studentId;
    /**
     * 答题场景
     */
    @Min(1)
    private Integer type;

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
