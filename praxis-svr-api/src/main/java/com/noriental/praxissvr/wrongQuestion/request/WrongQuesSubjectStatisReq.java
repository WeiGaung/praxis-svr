package com.noriental.praxissvr.wrongQuestion.request;

import com.noriental.validate.bean.BaseRequest;

import javax.validation.constraints.NotNull;

/**
 * Created by kate on 2016/12/16.
 * 查询某个学生按照学科分组的所有错题个数
 */
public class WrongQuesSubjectStatisReq extends BaseRequest {
    /**
     * 学生ID
     */
    @NotNull
    private Long studentId;


    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

}
