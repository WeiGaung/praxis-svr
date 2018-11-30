package com.noriental.praxissvr.statis.bean;

public enum WrongQuesSortType {
    WRONG_COUNT_DESC(1),
    WRONG_COUNT_ASC(2),
    TIME_DESC(3),
    TIME_ASC(4);
    private int sort;
    WrongQuesSortType(int sort){
        this.sort=sort;
    }

    public int getSort() {
        return sort;
    }
}
