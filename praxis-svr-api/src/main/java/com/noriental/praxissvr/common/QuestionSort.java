package com.noriental.praxissvr.common;

/**
 * @author chenlihua
 * @date 2015/12/1
 * @time 15:09
 */
public enum QuestionSort {
    ID_DESC("_9id"),
    UPDATE_TIME_DESC("_9updateTime"),
    ;
    private String sort;

    QuestionSort(String sort) {
        this.sort = sort;
    }

    public String getSort() {
        return sort;
    }
}
