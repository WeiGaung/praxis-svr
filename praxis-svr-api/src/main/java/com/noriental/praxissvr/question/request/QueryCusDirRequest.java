package com.noriental.praxissvr.question.request;

import com.noriental.validate.bean.BaseRequest;

/**
 * Created by hushuang on 2017/7/28.
 */
public class QueryCusDirRequest extends BaseRequest {

    /*
        用户ID
     */
    private Long systemId;

    /*
        自定义目录ID
     */
    private Long cusDirId;

    public QueryCusDirRequest() {
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
        return "QueryCusDirRequest{" +
                "systemId=" + systemId +
                ", cusDirId=" + cusDirId +
                '}';
    }
}
