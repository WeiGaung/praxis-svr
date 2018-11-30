package com.noriental.praxissvr.question.request;

import com.noriental.validate.bean.BaseRequest;

/**
 * Created by hushuang on 2017/7/28.
 */
public class UpdateCusDirRequest extends BaseRequest {

    /*
        新自定义目录
     */
    private Long cusDirId;
    /*
        老的自定义目录
     */
    private Long oldCusDirId;
    /*
        用户ID
     */
    private Long systemId;

    public UpdateCusDirRequest() {
    }

    public Long getCusDirId() {
        return cusDirId;
    }

    public void setCusDirId(Long cusDirId) {
        this.cusDirId = cusDirId;
    }

    public Long getOldCusDirId() {
        return oldCusDirId;
    }

    public void setOldCusDirId(Long oldCusDirId) {
        this.oldCusDirId = oldCusDirId;
    }

    public Long getSystemId() {
        return systemId;
    }

    public void setSystemId(Long systemId) {
        this.systemId = systemId;
    }

    @Override
    public String toString() {
        return "UpdateCusDirRequest{" +
                "cusDirId=" + cusDirId +
                ", oldCusDirId=" + oldCusDirId +
                ", systemId=" + systemId +
                '}';
    }
}
