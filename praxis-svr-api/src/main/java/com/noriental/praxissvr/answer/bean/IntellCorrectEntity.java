package com.noriental.praxissvr.answer.bean;

import java.io.Serializable;

/**
 * Created by kate on 2017/3/13.
 * 智能批改返回批改结果实体
 */
public class IntellCorrectEntity implements Serializable{

    /***
     * 习题ID
     */
    private Long questionId;

    /***
     * 智能批改结果
     */
    private String result;

    /***
     *  批改人id
     */
    private Long correctorId;

    /***
     * 批改角色
     */
    private String correctorRole;

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Long getCorrectorId() {
        return correctorId;
    }

    public void setCorrectorId(Long correctorId) {
        this.correctorId = correctorId;
    }

    public String getCorrectorRole() {
        return correctorRole;
    }

    public void setCorrectorRole(String correctorRole) {
        this.correctorRole = correctorRole;
    }
}
