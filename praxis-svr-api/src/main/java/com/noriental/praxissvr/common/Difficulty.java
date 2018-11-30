package com.noriental.praxissvr.common;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author chenlihua
 * @date 2015/11/30
 * @time 16:20
 */
public enum Difficulty {
    EASY(1, "易"),
    NORMAL(2, "中"),
    HARD(3, "难"),
    MASTER(4, "极难"),
    ;
    private int code;
    private String desc;

    private static Map<Integer, Difficulty> map = new TreeMap<>();

    static {
        for (Difficulty d : values()) {
            map.put(d.getCode(), d);
        }
    }

    public static Difficulty getDifficultyByCode(int code) {
        return map.get(code);
    }


    Difficulty(int code, String desc) {
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
