package com.noriental.praxissvr.question.request;

import com.noriental.praxissvr.common.QuestionState;
import com.noriental.validate.bean.BaseRequest;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Author ssc
 * @Date 2017/5/24 22:19
 */
public class UpdateQuestionStateRequest extends BaseRequest {
    @NotNull
    @NotEmpty
    private List<Long> questionIds;
    @NotNull
    private QuestionState state;

    public List<Long> getQuestionIds() {
        return questionIds;
    }

    public void setQuestionIds(List<Long> questionIds) {
        this.questionIds = questionIds;
    }

    public QuestionState getState() {
        return state;
    }

    public void setState(QuestionState state) {
        this.state = state;
    }
}
