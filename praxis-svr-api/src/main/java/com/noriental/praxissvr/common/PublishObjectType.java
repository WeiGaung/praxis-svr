package com.noriental.praxissvr.common;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author chenlihua
 * @date 2015/11/30
 * @time 16:20
 */
public enum PublishObjectType {
    KLASS(0, "班级"),
    STUDENT(1, "学生"),
    GROUP(2, "分组"),
    ;
    private int code;
    private String desc;

    private static Map<Integer, PublishObjectType> map = new TreeMap<>();

    static {
        for (PublishObjectType d : values()) {
            map.put(d.getCode(), d);
        }
    }

    public static PublishObjectType getByCode(int code) {
        return map.get(code);
    }


    PublishObjectType(int code, String desc) {
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
