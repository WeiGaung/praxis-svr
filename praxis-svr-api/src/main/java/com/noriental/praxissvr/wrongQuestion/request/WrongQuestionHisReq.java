package com.noriental.praxissvr.wrongQuestion.request;

import com.noriental.validate.bean.BaseRequest;

import javax.validation.constraints.NotNull;

/**
 * Created by kate on 2016/12/19.
 * 某学生某题的所有错题记录
 */
public class WrongQuestionHisReq extends BaseRequest {
    /***
     * 学生ID
     */
    @NotNull
    private Long studentId;
    /***
     * 习题ID
     */
    @NotNull
    private Long questionId;

    private int pageSize;
    /**
     * 当前页码
     */
    private int currentPage;

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
}
