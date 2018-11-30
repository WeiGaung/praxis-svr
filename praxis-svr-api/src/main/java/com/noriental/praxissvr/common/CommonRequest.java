package com.noriental.praxissvr.common;

import com.noriental.validate.bean.BaseRequest;

import javax.validation.constraints.NotNull;

/**
 * @author chenlihua
 * @date 2016/4/26
 * @time 17:07
 */
public final class CommonRequest<T> extends BaseRequest {

    @NotNull
    private T request;

    private CommonRequest() {

    }

    public T getRequest() {
        return request;
    }

    public void setRequest(T request) {
        this.request = request;
    }
    public static <T> CommonRequest<T> build(T t){
        CommonRequest<T> req = new CommonRequest<>();
        req.setRequest(t);
        return req;
    }
}
