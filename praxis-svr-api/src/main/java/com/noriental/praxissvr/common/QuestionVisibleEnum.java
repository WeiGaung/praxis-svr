package com.noriental.praxissvr.common;

/**
 * @author chenlihua
 * @date 2016/3/3
 * @time 16:14
 */
public enum QuestionVisibleEnum {
    ALL("*"), VISIBLE("1"), INVISIBLE("0"),;

    private String solrValue;

    QuestionVisibleEnum(String solrValue) {
        this.solrValue = solrValue;
    }

    public String getSolrValue() {
        return solrValue;
    }
}
