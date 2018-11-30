package com.noriental.praxissvr.question.request;

import com.noriental.validate.bean.BaseRequest;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;

/**
 * @author chenlihua
 * @date 2015/11/30
 * @time 14:14
 */
public class FindPadQuestionListByQuestionIdsRequest extends BaseRequest {

    @NotEmpty
    private List<Long> questionIds;
    private Long exerciseId;

    public Long getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(Long exerciseId) {
        this.exerciseId = exerciseId;
    }

    public List<Long> getQuestionIds() {
        return questionIds;
    }

    public void setQuestionIds(List<Long> questionIds) {
        this.questionIds = questionIds;
    }

}
