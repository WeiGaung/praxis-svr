package com.noriental.praxissvr.question.common;

public enum ResultConfigEnum {
    /**
     * 调用接口成功
     */
    BACKINFO_SUCCESS_INVOKE(0, "调用成功"),
    BACKINFO_FAIL_INVOKE(1, "调用失败"),
    /**
     * 新试题
     */
    QUESTION_NOTEXIST(0, "试题不存在"),
    /**
     * 老试题
     */
    QUESTION_NEW(1, "新试题"), QUESTION_OLD(2, "老试题");
    private Integer code;
    private String info;

    ResultConfigEnum(Integer code, String info) {
        this.code = code;
        this.info = info;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

}
