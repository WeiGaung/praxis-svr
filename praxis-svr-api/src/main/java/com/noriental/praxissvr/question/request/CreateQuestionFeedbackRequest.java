package com.noriental.praxissvr.question.request;

import com.noriental.validate.bean.BaseRequest;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import java.util.List;

public class CreateQuestionFeedbackRequest extends BaseRequest {
    @Min(1)
    private Long question_id;
    private Long exercise_id;
    @Min(1)
    private Long submit_by;
    @Min(1)
    private Integer user_type;
    @NotEmpty
    private List<String> error_type;
    private String error_detail;
    @NotBlank
    private String source_sys;

    //资源类型:1,题目;2,微课;3,音视频1111
    private Integer ques_resource_type;

    public CreateQuestionFeedbackRequest(Long question_id, Long submit_by, Integer user_type, List<String> error_type, String error_detail) {
        this.question_id = question_id;
        this.submit_by = submit_by;
        this.user_type = user_type;
        this.error_type = error_type;
        this.error_detail = error_detail;
    }
    public CreateQuestionFeedbackRequest(Long question_id,Long exercise_id, Long submit_by, Integer user_type,String source_sys,  List<String> error_type, String error_detail) {
        this.exercise_id = exercise_id;
        this.source_sys = source_sys;
        this.question_id = question_id;
        this.submit_by = submit_by;
        this.user_type = user_type;
        this.error_type = error_type;
        this.error_detail = error_detail;
    }

    public CreateQuestionFeedbackRequest(Long question_id,Long exercise_id, Long submit_by,
                                         Integer user_type,String source_sys,  List<String> error_type,
                                         String error_detail,Integer ques_resource_type) {
        this.exercise_id = exercise_id;
        this.source_sys = source_sys;
        this.question_id = question_id;
        this.submit_by = submit_by;
        this.user_type = user_type;
        this.error_type = error_type;
        this.error_detail = error_detail;
        this.ques_resource_type = ques_resource_type;
    }

    public Integer getQues_resource_type() {
        return ques_resource_type;
    }

    public CreateQuestionFeedbackRequest setQues_resource_type(Integer ques_resource_type) {
        this.ques_resource_type = ques_resource_type;
        return this;
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

    public Integer getUser_type() {
        return user_type;
    }

    public void setUser_type(Integer user_type) {
        this.user_type = user_type;
    }

    public List<String> getError_type() {
        return error_type;
    }

    public void setError_type(List<String> error_type) {
        this.error_type = error_type;
    }

    public String getError_detail() {
        return error_detail;
    }

    public void setError_detail(String error_detail) {
        this.error_detail = error_detail;
    }


    public Long getExercise_id() {
        return exercise_id;
    }

    public void setExercise_id(Long exercise_id) {
        this.exercise_id = exercise_id;
    }

    public String getSource_sys() {
        return source_sys;
    }

    public void setSource_sys(String source_sys) {
        this.source_sys = source_sys;
    }

}
