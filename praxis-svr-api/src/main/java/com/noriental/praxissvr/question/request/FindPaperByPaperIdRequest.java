package com.noriental.praxissvr.question.request;

import com.noriental.validate.bean.BaseRequest;

import javax.validation.constraints.Min;

/**
 * @author chenlihua
 * @date 2015/11/30
 * @time 17:53
 */
public class FindPaperByPaperIdRequest extends BaseRequest {
    @Min(value = 1)
    private long paperId;

    public long getPaperId() {
        return paperId;
    }

    public void setPaperId(long paperId) {
        this.paperId = paperId;
    }
}
