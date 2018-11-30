package com.noriental.praxissvr.common;

/**
 * Created by chenlihua on 2016/12/22.
 * praxis-svr
 */
public enum QuestionStructEnum {
    STRUCT_XZT(1),//选择题
    STRUCT_PDT(2),//判断题
    STRUCT_JDT(3),//简答题
    STRUCT_TKT(4),//填空题
    STRUCT_ZTT(8),//作图题
    STRUCT_7X5(9),//7选5
    STRUCT_PZT(10),//拍照题
    ;

    private int code;

    QuestionStructEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
