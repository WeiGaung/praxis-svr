package com.noriental.praxissvr.wrongQuestion.request;

import com.noriental.validate.bean.BaseRequest;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by kate on 2016/12/16.
 * 错误习题按照章节分页查询
 */
public class WrongQuesNumQueryReq extends BaseRequest {
    /**
     * 学生ID
     */
    @NotNull
    private Long studentId;
    @NotNull
    private List<Long> questionIds;


    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public List<Long> getQuestionIds() {
        return questionIds;
    }

    public void setQuestionIds(List<Long> questionIds) {
        this.questionIds = questionIds;
    }
}
