package com.noriental.praxissvr.answer.request;

import com.noriental.validate.bean.BaseRequest;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author kate
 * @create 2017-12-28 16:36
 * @desc 一键批改向统计服务推送消息实体
 **/
public class BatchUpdateCorrectTrailRequest extends BaseRequest {
    /**
     * 做题的来源
     **/
    @NotNull
    private String exerciseSource;
    /**
     * 场景id：作业测评发布表id、错题重做源答题记录id、错题强化源答题记录id、课程同步习题发布表id。
     */
    @NotNull
    private Long resourceId;

    private String requestId;

    private List<Long> studentIds;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
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

    public List<Long> getStudentIds() {
        return studentIds;
    }

    public void setStudentIds(List<Long> studentIds) {
        this.studentIds = studentIds;
    }
}
