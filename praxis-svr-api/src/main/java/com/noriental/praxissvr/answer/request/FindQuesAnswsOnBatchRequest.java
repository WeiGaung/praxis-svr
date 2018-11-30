package com.noriental.praxissvr.answer.request;

import com.noriental.validate.bean.BaseRequest;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * Created by bluesky on 2016/5/10.
 */
public class FindQuesAnswsOnBatchRequest extends BaseRequest implements Serializable{
    @NotNull
    private Long questionId;
    @NotNull
    private Long resourceId;
    @NotNull
    private String exerciseSource;
    private List<Integer> structIdList;
    private SortType sortType = SortType.TIME;
    private boolean isForPad=false; //true不实用智能批改查询 false使用智能批改查询
    public FindQuesAnswsOnBatchRequest(){

    }
    public FindQuesAnswsOnBatchRequest(String exerciseSource,Long resourceId,Long questionId,List<Integer> structIdList){
        this.questionId = questionId;
        this.resourceId = resourceId;
        this.exerciseSource = exerciseSource;
        this.structIdList = structIdList;
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

    public List<Integer> getStructIdList() {
        return structIdList;
    }

    public void setStructIdList(List<Integer> structIdList) {
        this.structIdList = structIdList;
    }

    public SortType getSortType() {
        return sortType;
    }

    public void setSortType(SortType sortType) {
        this.sortType = sortType;
    }

    public enum SortType{
        TIME,
        SUB_QUES_ID
    }

    public boolean isForPad() {
        return isForPad;
    }

    public void setForPad(boolean forPad) {
        isForPad = forPad;
    }
}
