package com.noriental.praxissvr.answer.bean;

import com.noriental.validate.bean.MQRequest;

import java.util.List;

public class IntellMqEntity extends MQRequest {
   private String tableName;
   private Long id;
   private Long reqId;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getReqId() {
        return reqId;
    }

    public void setReqId(Long reqId) {
        this.reqId = reqId;
    }
}
