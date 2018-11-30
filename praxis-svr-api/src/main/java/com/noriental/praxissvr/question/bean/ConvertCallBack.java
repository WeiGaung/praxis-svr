package com.noriental.praxissvr.question.bean;

import com.noriental.validate.bean.BaseRequest;

import java.util.List;

/**
 * Created by luozukai on 2016/11/23.
 */
public class ConvertCallBack extends BaseRequest {
    // 持久化处理的进程 ID，即前文中的<persistentId>
    private String id;
    // 状态码 0：成功，1：等待处理，2：正在处理，3：处理失败，4：成功但通知失败。
    private int code;
    // 与状态码相对应的详细描述
    private String desc;
    // 处理源文件的文件名。
    private String inputKey;
    // 处理源文件所在的空间名。
    private String inputBucket;
    // 云处理操作的处理队列，默认使用队列为共享队列 0.default。
    private String pipeline;
    // 云处理请求的请求 id，主要用于七牛技术人员的问题排查。
    private String reqid;
    // 云处理操作列表，包含每个云处理操作的状态信息
    private List<CompleteItem> items;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getInputKey() {
        return inputKey;
    }

    public void setInputKey(String inputKey) {
        this.inputKey = inputKey;
    }

    public String getInputBucket() {
        return inputBucket;
    }

    public void setInputBucket(String inputBucket) {
        this.inputBucket = inputBucket;
    }

    public String getPipeline() {
        return pipeline;
    }

    public void setPipeline(String pipeline) {
        this.pipeline = pipeline;
    }

    public String getReqid() {
        return reqid;
    }

    public void setReqid(String reqid) {
        this.reqid = reqid;
    }

    public List<CompleteItem> getItems() {
        return items;
    }

    public void setItems(List<CompleteItem> items) {
        this.items = items;
    }
}
