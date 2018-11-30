package com.noriental.praxissvr.answer.util;

import com.noriental.validate.error.ErrorCode;

/**
 * Discribe:错误码封装
 * Project:app_middle
 * Package: com.noriental.app.util
 * Add user: Chengwenbo
 * Date:  2016-05-25 14:34:09
 * 错误码范围 app-middle 40000-49999
 */
public enum AppReturnCode implements ErrorCode {
    APP_OK(0, "成功"),
    APP_FAIL(40000, "失败"),
    APP_ERROR_GENER(40001, "服务器开小差了，请您稍后再试！"),
    APP_ERROR_PARAM(40002, "参数错误"),
    APP_ERROR_NEEDLOGIN(40003, "登录状态已失效，请重新登录"),//前端有用到不能修改
    APP_USER_SECURITY_CODE_ERROR(40004, "验证码错误，请检查"),
    APP_BADPWD(40005, "手机号和密码不匹配"),
//    APP_USER_NOT_EXIST(40006, "手机号未注册"),
    APP_USER_NOT_EXIST(40006, "账号不存在请使用验证码登录"),
    APP_USER_DISABLE(40007, "用户被停用或已删除"),
    APP_USER_EXISTED(1016, "用户已存在"),
    APP_LOGIN_TYPE_ILLEGAL(40009, "登录类型不合法"),
    APP_BADOLDPWD(40010, "旧密码错误"),
    APP_PWD_SAME(40012, "新密码与旧密码相同"),
    APP_ERROR_PHONE(40013, "手机号码有误"),
    NO_SUBJECT_ID(40015, "需要subjectid"),
    NO_SUBJECT(40016, "获取subject列表失败"),
    NO_UPDATE(40017, "没有查询到对应的信息"),
    APP_ERROR_HEADER_PARAM(40019, "请传入参数requestid"),
    APP_ERROR_KEY(40020, "获取加密key失败"),
    APP_STUDENT_BADPWD(40021, "学生密码错误"),
    APP_STUDENT_NOT_EXIST(40022, "学生不存在"),
    APP_STUDENT_DELETE(40023, "学生已删除"),
    APP_STUDENT_NOT_BIND(40024, "未绑定学生"),  //code固定,不能修改
    APP_ALREADY_SUB(40025,"已订阅"),
    APP_STUDENT_BIND_FULL(40027, "已绑定三名家长,不能继续绑定"),
    APP_HAVE_BIND_STUDENT(40028, "学生已添加"),
    APP_SECURITY_CODE_FAIL(40030, "验证码发送失败"),
    APP_LOGINAPP_ERROR(40031, "此号已在教师空间注册,请更换手机号"),
    ERROR_STUDENT_UPDATE(40033, "账号已激活，不可修改"),
    APP_ERROR_UPLOAD_AVATAR(1013, "上传头像失败，请重新上传"),
    APP_DATA_REPORT_NOT_EXIST(40034, "数据报告不存在"),
    APP_TOPIC_OFF(40035,"专栏已下架"),
    APP_WX_ALREADY_BIND(40036,"该微信已授权其他账号，请更换微信账号后授权");
    private final int value;
    private final String comment;

    private AppReturnCode(int value, String comment) {
        this.value = value;
        this.comment = comment;
    }

    @Override
    public int getValue() {
        return value;
    }

    @Override
    public String getComment() {
        return comment;
    }
}
