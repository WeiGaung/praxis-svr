package com.noriental.praxissvr.question.request;

import com.noriental.praxissvr.common.QuestionState;
import com.noriental.validate.bean.BaseRequest;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * @author chenlihua
 * @date 2016/4/29
 * @time 16:18
 */
public class CheckoutNewFormatAndTrunkRequest extends BaseRequest {
    @NotEmpty
    private List<Long> ids;
    @NotEmpty
    private List<QuestionState> states;

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = new ArrayList<>(new HashSet<>(ids));
    }

    public List<QuestionState> getStates() {
        return states;
    }

    public void setStates(List<QuestionState> states) {
        this.states = states;
    }
}
