package com.noriental.praxissvr.resourcegroup.request;

import com.noriental.validate.bean.BaseRequest;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Created by kate on 2016/11/18.
 */
public class ResGroupCreateRequest extends BaseRequest {
    private Long id;

    /**
     * 组名称
     */
    @NotNull
    private String name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSystemId() {
        return systemId;
    }

    public void setSystemId(Long systemId) {
        this.systemId = systemId;
    }
}
