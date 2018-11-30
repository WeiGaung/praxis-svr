//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.noriental.praxissvr.common;

import com.noriental.utils.json.JsonUtil;
import com.noriental.validate.bean.BaseRequest;
import java.util.List;
import javax.validation.constraints.Min;

public class TrailBaseErrorRequestRequest extends BaseRequest {
    @Min(1L)
    private Long studentId;
    @Min(1L)
    private Long subjectId;
    private Long directoryId;
    private List<Long> queryIds = null;
    private boolean isTopic;

    public TrailBaseErrorRequestRequest() {
    }

    public boolean isTopic() {
        return this.isTopic;
    }

    public void setIsTopic(boolean isTopic) {
        this.isTopic = isTopic;
    }

    public List<Long> getQueryIds() {
        return this.queryIds;
    }

    public void setQueryIds(List<Long> queryIds) {
        this.queryIds = queryIds;
    }

    public Long getStudentId() {
        return this.studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getSubjectId() {
        return this.subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public String toString() {
        return JsonUtil.obj2Json(this);
    }

    public Long getDirectoryId() {
        return directoryId;
    }

    public void setDirectoryId(Long directoryId) {
        this.directoryId = directoryId;
    }
}
