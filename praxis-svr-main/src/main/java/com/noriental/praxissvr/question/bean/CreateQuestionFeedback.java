package com.noriental.praxissvr.question.bean;

/**
 * Created by shengxian on 2016/10/21.
 */
public class CreateQuestionFeedback {
    private Long question_id;
    private Long exercise_id;
    private Long submit_by;
    private Integer user_type;
    private String error_types;
    private String error_detail;
    private String source_sys;
    private Long subject_id;
    private String submit_name;
    private String ques_upload_name;

    //题目学段id
    private Long stage_id;
    //资源类型
    private Integer ques_resource_type;
    //题目上传人id
    private Long ques_upload_id;
    //题目上传人所属学校
    private String ques_upload_school;

    public Integer getQues_resource_type() {
        return ques_resource_type;
    }

    public CreateQuestionFeedback setQues_resource_type(Integer ques_resource_type) {
        this.ques_resource_type = ques_resource_type;
        return this;
    }

    public Long getQues_upload_id() {
        return ques_upload_id;
    }

    public CreateQuestionFeedback setQues_upload_id(Long ques_upload_id) {
        this.ques_upload_id = ques_upload_id;
        return this;
    }

    public String getQues_upload_school() {
        return ques_upload_school;
    }

    public CreateQuestionFeedback setQues_upload_school(String ques_upload_school) {
        this.ques_upload_school = ques_upload_school;
        return this;
    }

    public Long getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(Long question_id) {
        this.question_id = question_id;
    }

    public Long getExercise_id() {
        return exercise_id;
    }

    public void setExercise_id(Long exercise_id) {
        this.exercise_id = exercise_id;
    }

    public Long getSubmit_by() {
        return submit_by;
    }

    public void setSubmit_by(Long submit_by) {
        this.submit_by = submit_by;
    }

    public Integer getUser_type() {
        return user_type;
    }

    public void setUser_type(Integer user_type) {
        this.user_type = user_type;
    }

    public String getError_types() {
        return error_types;
    }

    public void setError_types(String error_types) {
        this.error_types = error_types;
    }

    public String getError_detail() {
        return error_detail;
    }

    public void setError_detail(String error_detail) {
        this.error_detail = error_detail;
    }

    public String getSource_sys() {
        return source_sys;
    }

    public void setSource_sys(String source_sys) {
        this.source_sys = source_sys;
    }

    public Long getSubject_id() {
        return subject_id;
    }

    public void setSubject_id(Long subject_id) {
        this.subject_id = subject_id;
    }

    public Long getStage_id() {
        return stage_id;
    }

    public void setStage_id(Long stage_id) {
        this.stage_id = stage_id;
    }

    public String getSubmit_name() {
        return submit_name;
    }

    public void setSubmit_name(String submit_name) {
        this.submit_name = submit_name;
    }

    public String getQues_upload_name() {
        return ques_upload_name;
    }

    public void setQues_upload_name(String ques_upload_name) {
        this.ques_upload_name = ques_upload_name;
    }
}
