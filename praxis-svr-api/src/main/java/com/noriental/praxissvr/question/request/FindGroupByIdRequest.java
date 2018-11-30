package com.noriental.praxissvr.question.request;

import com.noriental.validate.bean.BaseRequest;

import javax.validation.constraints.Min;

/**
 * @author chenlihua
 * @date 2015/12/23
 * @time 11:16
 */

public class FindGroupByIdRequest extends BaseRequest {
    @Min(1)
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
