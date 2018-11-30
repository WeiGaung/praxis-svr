package com.noriental.praxissvr.question.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by shengxian on 2016/9/29.
 */
public class QuestionFeedback implements Serializable{
    Long id;
    Long question_id;
    long exercise_id;
    Long submit_by;
    String submit_name;
    Long process_by;
    Date submit_time;
    Date process_time;
    Integer user_type;
    String source_sys;
    Integer status;
    String error_detail;
    String remark;
    Long subject_id;
    Long stage_id;
    String ques_upload_name;

    //错误类型(逗号分隔）：1-题干有误；2-章节有误；3-解析有误；4-知识点有误；5-题型有误；6；其他；8:图片不清楚;9:题目重复;
    String error_type;

    //资源类型 1,题目;2,微课;3,音视频
    private Integer ques_resource_type;
    //题目上传人id
    private Long ques_upload_id;
    //题目上传人所属学校
    private String ques_upload_school;

    public enum UserType{
        TEACHER(1,""),
        STUDENT(2,"");
        int code;
        String name;
        UserType(int code,String name){
            this.code=code;
            this.name=name;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
    public enum ErrorType{
        BODY(1,"题干有误"),
        CHAPTER(2,"章节有误"),
        ANA(3,"解析有误"),
        KNOW(4,"知识点有误"),
        QUES_TYPE(5,"题型有误"),
        OTHER(6,"其他");
        int code;
        String name;
        ErrorType(int code,String name){
            this.code=code;
            this.name=name;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    };
    public enum SourceSys{
        stuspace("stuspace","学生空间"),
        teaspace("teaspace","教师空间"),
        pad("pad","pad"),
        admin("admin","运营平台");
        String code;
        String name;
        SourceSys(String code,String name){
            this.code=code;
            this.name=name;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    };
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(Long question_id) {
        this.question_id = question_id;
    }

    public Long getSubmit_by() {
        return submit_by;
    }

    public void setSubmit_by(Long submit_by) {
        this.submit_by = submit_by;
    }

    public Long getProcess_by() {
        return process_by;
    }

    public void setProcess_by(Long process_by) {
        this.process_by = process_by;
    }

    public Date getSubmit_time() {
        return submit_time;
    }

    public void setSubmit_time(Date submit_time) {
        this.submit_time = submit_time;
    }

    public Date getProcess_time() {
        return process_time;
    }

    public void setProcess_time(Date process_time) {
        this.process_time = process_time;
    }

    public Integer getUser_type() {
        return user_type;
    }

    public void setUser_type(Integer user_type) {
        this.user_type = user_type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getError_type() {
        return error_type;
    }

    public void setError_type(String error_type) {
        this.error_type = error_type;
    }

    public String getError_detail() {
        return error_detail;
    }

    public void setError_detail(String error_detail) {
        this.error_detail = error_detail;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public long getExercise_id() {
        return exercise_id;
    }

    public void setExercise_id(long exercise_id) {
        this.exercise_id = exercise_id;
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

    public Integer getQues_resource_type() {
        return ques_resource_type;
    }

    public QuestionFeedback setQues_resource_type(Integer ques_resource_type) {
        this.ques_resource_type = ques_resource_type;
        return this;
    }

    public Long getQues_upload_id() {
        return ques_upload_id;
    }

    public QuestionFeedback setQues_upload_id(Long ques_upload_id) {
        this.ques_upload_id = ques_upload_id;
        return this;
    }

    public String getQues_upload_school() {
        return ques_upload_school;
    }

    public QuestionFeedback setQues_upload_school(String ques_upload_school) {
        this.ques_upload_school = ques_upload_school;
        return this;
    }

    public void setQues_upload_name(String ques_upload_name) {
        this.ques_upload_name = ques_upload_name;
    }
}
