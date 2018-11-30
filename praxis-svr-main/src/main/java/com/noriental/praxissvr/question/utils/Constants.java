package com.noriental.praxissvr.question.utils;

/**
 * Created by kate on 2016/12/2.
 */
public interface Constants {

    /**
     * 指定要删除的索引的类名称
     */
    public static final String _DOC_CLASS_NAME="_docClassName";
    /***
     * 指定要删除的数据主键ID
     */
    public static final String _PRIMARY_KEY="_primaryKey";
    /***
     * 指定操作为删除索引
     */
    public static final String KEY_DELETE_FLG="delete";

    /**
     * 批量删除索引
     */
    public static final String KEY_DOC_LIST="docList";


    public static final String _PRIMARY_KEY_VALUE_DEFAULT = "id";

    public static final String GROUP_NAME="默认分组";
    public static final Long  GROUP_ID=0L;
}
