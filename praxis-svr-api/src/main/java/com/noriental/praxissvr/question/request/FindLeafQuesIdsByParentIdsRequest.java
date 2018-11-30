package com.noriental.praxissvr.question.request;

import com.noriental.validate.bean.BaseRequest;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;

/**
 * @author chenlihua
 * @date 2015/12/25
 * @time 10:34
 */
public class FindLeafQuesIdsByParentIdsRequest extends BaseRequest {

    @NotEmpty
    private List<Long> parentQuesIds;

    public List<Long> getParentQuesIds() {
        return parentQuesIds;
    }

    public void setParentQuesIds(List<Long> parentQuesIds) {
        this.parentQuesIds = parentQuesIds;
    }
}
