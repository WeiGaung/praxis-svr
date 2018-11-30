package com.noriental.praxissvr.question.request;

import com.noriental.validate.bean.BaseRequest;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;

/**
 * @author chenlihua
 * @date 2015/11/30
 * @time 18:12
 */
public class FindQuestionsByIdsRequest extends BaseRequest {
    @NotEmpty
    private List<Long> questionIds;
    private boolean recursive;

    public List<Long> getQuestionIds() {
        return questionIds;
    }

    public void setQuestionIds(List<Long> questionIds) {
        this.questionIds = questionIds;
    }

    public boolean isRecursive() {
        return recursive;
    }

    public void setRecursive(boolean recursive) {
        this.recursive = recursive;
    }
}
