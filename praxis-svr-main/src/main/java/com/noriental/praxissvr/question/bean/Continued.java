package com.noriental.praxissvr.question.bean;

/**
 * Created by hushuang on 2017/3/8.
 */
public class Continued {

    private Long reqId;
    private String object;

    public Continued() {
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
        return "Continued{" +
                "reqId=" + reqId +
                ", object='" + object + '\'' +
                '}';
    }
}
