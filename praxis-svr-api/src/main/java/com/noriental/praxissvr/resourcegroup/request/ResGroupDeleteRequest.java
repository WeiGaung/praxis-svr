package com.noriental.praxissvr.resourcegroup.request;

import com.noriental.validate.bean.BaseRequest;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Created by kate on 2016/11/18.
 */
public class ResGroupDeleteRequest extends BaseRequest {


    /**
     * 组ID
     */
    @NotNull
    private Long id;

    /***
     * 系统用户ID
     */
    @Min(1)
    private Long systemId;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSystemId() {
        return systemId;
    }

    public void setSystemId(Long systemId) {
        this.systemId = systemId;
    }
}
