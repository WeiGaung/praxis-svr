package com.noriental.praxissvr.wrongQuestion.request;

import com.noriental.validate.bean.BaseRequest;

import javax.validation.constraints.NotNull;

/**
 * Created by kate on 2017/5/31.
 */
public class WrongQuesListReq extends BaseRequest {
    @NotNull
    private Long studentId;
    @NotNull
    private int level;
    @NotNull
    private Long knowledgeId;
    //1知识点已做题目2知识点错题3章节已做题目4章节错题
    @NotNull
    private String dataType;

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Long getKnowledgeId() {
        return knowledgeId;
    }

    public void setKnowledgeId(Long knowledgeId) {
        this.knowledgeId = knowledgeId;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
}
