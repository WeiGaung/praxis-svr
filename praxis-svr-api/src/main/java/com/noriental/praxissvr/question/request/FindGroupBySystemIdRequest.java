package com.noriental.praxissvr.question.request;

import com.noriental.validate.bean.BaseRequest;

import javax.validation.constraints.Min;

/**
 * @author chenlihua
 * @date 2015/12/23
 * @time 11:13
 */
public class FindGroupBySystemIdRequest extends BaseRequest {
    @Min(1)
    private long systemId;

    public long getSystemId() {
        return systemId;
    }

    public void setSystemId(long systemId) {
        this.systemId = systemId;
    }
}
