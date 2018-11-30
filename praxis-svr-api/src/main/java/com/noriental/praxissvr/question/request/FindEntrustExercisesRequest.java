package com.noriental.praxissvr.question.request;

import com.noriental.praxissvr.common.PageBaseRequest;
import com.noriental.praxissvr.question.constaints.EntrustStatusEnum;

import javax.validation.constraints.Min;

/**
 * @author chenlihua
 * @date 2015/12/23
 * @time 11:13
 */
public class FindEntrustExercisesRequest extends PageBaseRequest {
    @Min(1)
    private long teacherId;

    private EntrustStatusEnum status;

    public long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(long teacherId) {
        this.teacherId = teacherId;
    }

    public EntrustStatusEnum getStatus() {
        return status;
    }

    public void setStatus(EntrustStatusEnum status) {
        this.status = status;
    }
}
