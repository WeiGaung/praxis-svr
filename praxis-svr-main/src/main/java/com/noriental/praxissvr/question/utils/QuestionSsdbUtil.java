package com.noriental.praxissvr.question.utils;

/**
 * Created by kate on 2016/11/30.
 * 根据主键ID获取SSDB数据存储主键生成规则
 */
public class QuestionSsdbUtil {

    public static final String PREFIX_KEY = "ssdb_question_";

    /**
     * @param rowkey 数据存入Mysql数据库表这条数据的主键ID
     * @return
     */
    public static String getKey(String rowkey, String dataType) {
        return PREFIX_KEY + dataType + "_" + rowkey;
    }
}
