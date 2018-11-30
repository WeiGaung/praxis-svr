package com.noriental.praxissvr.statis.request;

import com.noriental.validate.bean.BaseRequest;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * Created by bluesky on 2016/5/12.
 */
public class FindClassQuesReportRequest extends BaseRequest implements Serializable{
    @NotNull
    private Long resourceId;
    @NotEmpty
    private List<Long> questionIdList;

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }


    public List<Long> getQuestionIdList() {
        return questionIdList;
    }

    public void setQuestionIdList(List<Long> questionIdList) {
        this.questionIdList = questionIdList;
    }
}
