package com.noriental.praxissvr.question.constaints;

/**
 * Created by chenlihua on 2016/10/9.
 * praxis-svr
 */
public enum EntrustStatusEnum {
    SUBMIT(1, "已提交"),
    PROCESSING(2, "处理中"),
    SUCCESS(3, "成功"),
    FAIL(4, "失败"),
    ;

    EntrustStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private int code;
    private String desc;

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
