package com.noriental.praxissvr.question.request;

import com.noriental.praxissvr.common.QuestionState;
import com.noriental.validate.bean.BaseRequest;

import javax.validation.constraints.Min;
import java.util.List;

/**
 * @author chenlihua
 * @date 2015/11/30
 * @time 18:12
 */
public class FindQuestionByIdRequest extends BaseRequest{
    @Min(value = 1)
    private long questionId;
    private boolean recursive;
    private boolean basic;
    private List<QuestionState> subStates;//子题的状态

    public long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(long questionId) {
        this.questionId = questionId;
    }

    public boolean isRecursive() {
        return recursive;
    }

    public void setRecursive(boolean recursive) {
        this.recursive = recursive;
    }

    public boolean isBasic() {
        return basic;
    }

    public void setBasic(boolean basic) {
        this.basic = basic;
    }

    public List<QuestionState> getSubStates() {
        return subStates;
    }

    public void setSubStates(List<QuestionState> subStates) {
        this.subStates = subStates;
    }
}
