package com.noriental.praxissvr.answer.request;

import com.noriental.validate.bean.BaseRequest;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by bluesky on 2016/5/10.
 */
public class FindStuQuesAnswOnBatchRequest extends BaseRequest implements Serializable{
    @NotNull
    private Long studentId;
    @NotNull
    private Long questionId;
    @NotNull
    private Long resourceId;
    @NotNull
    private String exerciseSource;



    public FindStuQuesAnswOnBatchRequest() {
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


    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public String getExerciseSource() {
        return exerciseSource;
    }

    public void setExerciseSource(String exerciseSource) {
        this.exerciseSource = exerciseSource;
    }
}
