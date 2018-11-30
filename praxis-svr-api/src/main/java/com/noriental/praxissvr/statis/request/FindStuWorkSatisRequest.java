package com.noriental.praxissvr.statis.request;

import com.noriental.validate.bean.BaseRequest;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by bluesky on 2016/7/1.
 */
public class FindStuWorkSatisRequest  extends BaseRequest implements Serializable {
    @NotNull
    private Long studentId;
    @NotNull
    private Long subjectId;

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }
}
