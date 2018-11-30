package com.noriental.praxissvr.question.request;

import com.noriental.validate.bean.BaseRequest;

/**
 * Created by hushuang on 2017/7/28.
 */
public class DeleteCusDirRequest extends BaseRequest {

    private Long systemId;
    private Long cusDirId;

    public DeleteCusDirRequest() {
    }

    public Long getSystemId() {
        return systemId;
    }

    public void setSystemId(Long systemId) {
        this.systemId = systemId;
    }

    public Long getCusDirId() {
        return cusDirId;
    }

    public void setCusDirId(Long cusDirId) {
        this.cusDirId = cusDirId;
    }

    @Override
    public String toString() {
        return "DeleteCusDirRequest{" +
                "systemId=" + systemId +
                ", cusDirId=" + cusDirId +
                '}';
    }
}
