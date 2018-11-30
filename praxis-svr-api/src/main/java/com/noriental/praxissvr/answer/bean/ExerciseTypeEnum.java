package com.noriental.praxissvr.answer.bean;

/**
 * @author chenlihua
 * @date 2015/12/19
 * @time 14:44
 */
public enum ExerciseTypeEnum {
    ZUO_YE(6, "作业"),
    CE_PING(7, "测评"),
    TONG_BU_XI_TI(1, "同步习题"),
    YUXI(8,"预习"),
    FUXI(9,"复习")
    ;
    private int code;
    private String desc;



    ExerciseTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

}
