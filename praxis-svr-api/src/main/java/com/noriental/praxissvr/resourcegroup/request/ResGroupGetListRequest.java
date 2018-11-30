package com.noriental.praxissvr.resourcegroup.request;

import com.noriental.validate.bean.BaseRequest;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Created by kate on 2016/11/18.
 */
public class ResGroupGetListRequest extends BaseRequest {
    /***
     * 时间戳
     */
    @NotNull
    private String timestamp;
    /**
     * 密钥
     */
    @NotNull
    private String secret;
    /***
     * 系统用户ID
     */
    @Min(1)
    private Long systemId;


    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public Long getSystemId() {
        return systemId;
    }

    public void setSystemId(Long systemId) {
        this.systemId = systemId;
    }
}
