package com.noriental.praxissvr.question.bean;

import com.noriental.validate.bean.BaseRequest;

/**
 * Created by luozukai on 2016/11/23.
 */
public class CompleteItem extends BaseRequest {
    // 所执行的云处理操作命令 fopN
    private String cmd;
    // 状态码 0：成功，1：等待处理，2：正在处理，3：处理失败，4：成功但通知失败
    private int code;
    // 与状态码相对应的详细描述
    private String desc;
    // 如果处理失败，该字段会给出失败的详细原因。
    private String error;
    // 云处理结果保存在服务端的唯一 hash 标识
    private String hash;
    // 云处理结果的外链资源名 Key
    private String key;
    // 默认为 0。当用户执行 saveas 时，如果未加 force 且指定的 bucket：key 存在，则返回 1，告诉用户返回的是旧数据
    private int returnOld;

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
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

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getReturnOld() {
        return returnOld;
    }

    public void setReturnOld(int returnOld) {
        this.returnOld = returnOld;
    }
}
