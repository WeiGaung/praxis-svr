package com.noriental.praxissvr.question.request;

import com.noriental.validate.bean.BaseRequest;

/**
 * @author chenlihua
 * @date 2015/12/22
 * @time 15:50
 */
public class FindStructIdByQuesTypeIdRequest extends BaseRequest {
    private Long quesTypeId;

    public Long getQuesTypeId() {
        return quesTypeId;
    }

    public void setQuesTypeId(Long quesTypeId) {
        this.quesTypeId = quesTypeId;
    }
}
