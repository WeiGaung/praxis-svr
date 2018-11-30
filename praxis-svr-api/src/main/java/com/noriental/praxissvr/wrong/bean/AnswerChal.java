package com.noriental.praxissvr.wrong.bean;

import java.util.List;

/**
 * Created by bluesky on 2016/7/5.
 */
public class AnswerChal {
    private Long id;
    private Long studentId;
    private Long questionId;
    private String exerciseSource;
    private Long resourceId;
    private String result;
    private String flag;
    private String flagNew;
    private List<Long> quesIdList;

    public List<Long> getQuesIdList() {
        return quesIdList;
    }

    public void setQuesIdList(List<Long> quesIdList) {
        this.quesIdList = quesIdList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getExerciseSource() {
        return exerciseSource;
    }

    public void setExerciseSource(String exerciseSource) {
        this.exerciseSource = exerciseSource;
    }


    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }


    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getFlagNew() {
        return flagNew;
    }

    public void setFlagNew(String flagNew) {
        this.flagNew = flagNew;
    }
}
