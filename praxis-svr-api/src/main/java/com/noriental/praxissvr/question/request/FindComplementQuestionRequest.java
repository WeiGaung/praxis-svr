package com.noriental.praxissvr.question.request;

import com.noriental.validate.bean.BaseRequest;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import java.util.Set;

/**
 * @author chenlihua
 * @date 2016/7/22
 * @time 11:09
 */
public class FindComplementQuestionRequest extends BaseRequest {
    @NotEmpty
    private Set<Long> subjectIds;
    @Min(1)
    private int maxCount;
    @Min(1)
    private long orgId;
    @Min(1)
    private int orgType;

    public Set<Long> getSubjectIds() {
        return subjectIds;
    }

    public void setSubjectIds(Set<Long> subjectIds) {
        this.subjectIds = subjectIds;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    public long getOrgId() {
        return orgId;
    }

    public void setOrgId(long orgId) {
        this.orgId = orgId;
    }

    public int getOrgType() {
        return orgType;
    }

    public void setOrgType(int orgType) {
        this.orgType = orgType;
    }

}
