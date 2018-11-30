package com.noriental.praxissvr.answer.request;

import com.noriental.validate.bean.BaseRequest;

import javax.validation.constraints.NotNull;

/**
 * Created by kate on 2017/9/18.
 * 根据发布资源ID和答题场景查询学生做答记录
 */
public class FindHistoryDataRequest extends BaseRequest {

    @NotNull
    private Long resourceId;
    @NotNull
    private String exerciseSource;

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
