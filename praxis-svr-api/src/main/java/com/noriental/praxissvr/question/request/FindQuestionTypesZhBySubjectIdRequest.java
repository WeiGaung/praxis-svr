package com.noriental.praxissvr.question.request;

import com.noriental.validate.bean.BaseRequest;

import javax.validation.constraints.Min;

/**
 * @author chenlihua
 * @date 2015/12/22
 * @time 15:49
 */
public class FindQuestionTypesZhBySubjectIdRequest extends BaseRequest {
    @Min(1)
    private long subjectId;

    public long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(long subjectId) {
        this.subjectId = subjectId;
    }
}
