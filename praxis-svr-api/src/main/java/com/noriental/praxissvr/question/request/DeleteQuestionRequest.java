package com.noriental.praxissvr.question.request;

import com.noriental.validate.bean.BaseRequest;

/**
 * Created by hushuang on 2017/8/2.
 */
public class DeleteQuestionRequest extends BaseRequest {

    /*
        题目ID
     */
    private Long id;
    /*
        自定义目录末级ID
     */
    private Long cusDirId;
    /*
        用户ID
     */
    private Long systemId;

    public DeleteQuestionRequest() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCusDirId() {
        return cusDirId;
    }

    public void setCusDirId(Long cusDirId) {
        this.cusDirId = cusDirId;
    }

    public Long getSystemId() {
        return systemId;
    }

    public void setSystemId(Long systemId) {
        this.systemId = systemId;
    }

    @Override
    public String toString() {
        return "DeleteQuestionRequest{" +
                "id=" + id +
                ", cusDirId=" + cusDirId +
                ", systemId=" + systemId +
                '}';
    }
}
