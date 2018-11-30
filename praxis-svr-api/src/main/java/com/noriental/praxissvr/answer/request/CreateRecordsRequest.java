package com.noriental.praxissvr.answer.request;

import com.noriental.validate.bean.BaseRequest;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by shengxian on 2017/1/14.
 */
public class CreateRecordsRequest extends BaseRequest {

    @NotEmpty
    private List<Long> quesIds;

    @NotNull
    private Long studentId;

    @NotNull
    private Long resourceId;

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public List<Long> getQuesIds() {
        return quesIds;
    }

    public void setQuesIds(List<Long> quesIds) {
        this.quesIds = quesIds;
    }

}
