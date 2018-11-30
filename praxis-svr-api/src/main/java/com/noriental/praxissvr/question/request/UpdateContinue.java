package com.noriental.praxissvr.question.request;

/**
 * Created by hushuang on 2017/5/31.
 */
public class UpdateContinue {

    private Long reqId;
    private String object;

    public UpdateContinue() {
    }

    public Long getReqId() {
        return reqId;
    }

    public void setReqId(Long reqId) {
        this.reqId = reqId;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    @Override
    public String toString() {
        return "UpdateContinue{" +
                "reqId=" + reqId +
                ", object='" + object + '\'' +
                '}';
    }
}
