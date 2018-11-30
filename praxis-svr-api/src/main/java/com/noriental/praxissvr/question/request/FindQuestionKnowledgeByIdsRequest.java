package com.noriental.praxissvr.question.request;

import com.noriental.validate.bean.BaseRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by chenlihua on 2016/9/23.
 * praxis-svr
 */
public class FindQuestionKnowledgeByIdsRequest extends BaseRequest {
    @NotEmpty
    private List<Long> questionIds;

    public List<Long> getQuestionIds() {
        return questionIds;
    }

    public void setQuestionIds(List<Long> questionIds) {
        if (CollectionUtils.isNotEmpty(questionIds)) {
            this.questionIds = new ArrayList<>(new HashSet<>(questionIds));
        }
    }
}
