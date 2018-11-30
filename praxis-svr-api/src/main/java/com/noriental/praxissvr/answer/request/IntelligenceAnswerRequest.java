package com.noriental.praxissvr.answer.request;

import com.noriental.validate.bean.BaseRequest;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by kate on 2017/3/8.
 * 智能批改请求参数实体
 */
public class IntelligenceAnswerRequest extends BaseRequest {

    /***
     * 题集id/题集发布id
     */
    @NotNull
    private Long resourceId;
    /***
     * 答题场景
     */
    @NotNull
    private String exerciseSource;
    /***
     * 	学生id
     */
    @NotNull
    private Long studentId;
    /***
     * 智能批改结果集
     */
    @NotEmpty
    private List<IntelligenceAnswer> intelligenceAnswerList;

    /****
     * 批改人ID
     */
    @NotNull
    private Long correctorId;

    private Long timeStamp;


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

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public List<IntelligenceAnswer> getIntelligenceAnswerList() {
        return intelligenceAnswerList;
    }

    public void setIntelligenceAnswerList(List<IntelligenceAnswer> intelligenceAnswerList) {
        this.intelligenceAnswerList = intelligenceAnswerList;
    }
    public Long getCorrectorId() {
        return correctorId;
    }

    public void setCorrectorId(Long correctorId) {
        this.correctorId = correctorId;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
