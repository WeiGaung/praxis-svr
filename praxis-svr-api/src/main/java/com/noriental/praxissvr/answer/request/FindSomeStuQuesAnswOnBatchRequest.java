package com.noriental.praxissvr.answer.request;

import com.noriental.validate.bean.BaseRequest;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * Created by gb on 2018/9/26.
 */
public class FindSomeStuQuesAnswOnBatchRequest extends BaseRequest implements Serializable{
    @NotNull
    private List<Long> studentIdList;
    @NotNull
    private Long questionId;
    @NotNull
    private Long resourceId;
    @NotNull
    private String exerciseSource;

    private String methodType;


    public List<Long> getStudentIdList() {
        return studentIdList;
    }

    public void setStudentIdList(List<Long> studentIdList) {
        this.studentIdList = studentIdList;
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

    public String getMethodType() {
        return methodType;
    }

    public void setMethodType(String methodType) {
        this.methodType = methodType;
    }
}
