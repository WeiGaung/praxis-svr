package com.noriental.praxissvr.common;

/**
 * @author chenlihua
 * @date 2016/1/3
 * @time 11:23
 */
public enum QuestionState {
    ALL,//所有状态
    ENABLED,//ENABLED
    RAW,//未提交 -未使用
    UNEVALED,//未处理 -未使用
    EVALING,//处理中 -未使用
    EVALED,//待审核 -未使用
    DISABLED,//停用
    BANNED, //原定义：退回；现定义：屏蔽
    PREVIEWED,//待审核
    INCOMPLETE,//套卷中间状态
}
