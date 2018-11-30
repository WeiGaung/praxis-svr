package com.noriental.praxissvr.answer.util;

import com.noriental.global.dict.QiniuConstant;

/**
 * Project:app-middle
 * Package: com.noriental.app.middle.utils
 * User: liuhuapeng
 * Date: 2016年5月28日16:47:06
 */

public class AppConstants {

    public static  String AVATAR_URL =  "https://" + QiniuConstant.URL_PREFIX.HEAD_IMAGE + "/";
    public static  String ASSISTANT_AVATAR_URL = "https://" + QiniuConstant.URL_PREFIX.MEDIA_INFO + "/";

    /**
     *  Http Header
    */
    public static  String STRING_RID = "requestid";
    public static  String STRING_POST = "post";
     public static  String STRING_GET = "get";
    public static  String STRING_UTF8 = "UTF-8";

    /**
     * 公共上行
    */
    public static  String STRING_VS = "vs";
    public static  String STRING_UA = "ua";
    public static  String STRING_OS = "os";
    public static  String STRING_VC = "vc";
    public static  String STRING_SW = "sw";
    public static  String STRING_SH = "sh";
    public static  String STRING_CONTYPE = "contype";
    public static  String STRING_IMEI = "imei";
    public static  String STRING_IMSI = "imsi";
    public static  String STRING_MAC = "mac";
    public static  String STRING_CHANNLE = "channel";
    public static  String STRING_UDID = "udid";
    public static  String STRING_SERIAL = "serial";

    /**
     * 用户模块
    */
    public static  String STRING_TOKEN = "token";
    public static  String STRING_USERID = "uid";
   // public static final String STRING_ACOUNT = "account";
    public static  String STRING_PWD = "pwd";
    public static  String STRING_OLDPWD = "oldpwd";
    public static  String STRING_NEWPWD = "newpwd";
    public static  String STRING_TRACENO = "traceno";
    public static  String STRING_PHONE = "phone";
    public static  String STRING_CODE = "code";

    /**
     * Login type
     */
    public static   int INT_LOGINTYPE_APP = 2;
   /* public static  final int INT_LOGINTYPE_WEB = 1;
    public static  final int INT_LOGINTYPE_PAD = 3;*/

    /**
     * User bind phone status
     */
    public static  int INT_USERSTATUS_NONEACTIVITE = 0;
    public static  int INT_USERSTATUS_BIND_WX = 1;
    public static  int INT_USERSTATUS_UNBIND_WX = 2;
    public static  int INT_USERSTATUS_ACTIVITE = 3;

    /**
     * 设置，包括升级等
     */
    public static  String STRING_OS_ADNROID = "android";
    public static  String STRING_OS_IOS = "ios";

    /**
     * User type
     * Should be define in user service
     */
    public static  int INT_USERTYPE_STUDENT = 2;
    public static  int INT_USERTYPE_TEACHER = 1;
    public static  int INT_USERTYPE_PARENT = 3;

    /**
     * redis key
     */
    public static  String APP_VERIFY= "app_verify_";
    /**
     * 间隔时间
     */
    public static  String APP_REQUEST_VERFITY_TIME = "1";

    /**
     * 向用户发送短信设置
     */
    public static  int expireMin=3; //验证码redis存储时间
    public static  int TRADE_NO_LENGTH = 18; //用于取redis中key的长度
    public static  int SECURITY_CODE_LENGTH = 4;  //验证码位数

    /**
     * 用户状态 0 停用or删除   1启用
     */
    public static  int INT_USER_STOP_STATUS = 0;
    public static  int INT_USER_ACTIVE_STATUS = 1;

    //用户头像大小限制
    public static  long STUDENT_HEAD_IMAGE_SIZE_LIMIT = 5 * 1024 * 1024;

    /**
     * 本地图片路径
     */
    public static class ImagePath {
        /**头像路径*/
        public static final String HEAD_IMAGE = "/xdfapp/app/hd.okjiaoyu.cn/";
        /**答案路径*/
        public static final String ANSWER_PICTRUE = "/xdfapp/app/ap.okjiaoyu.cn/";
    }

    //机构头像
    public static final String DEFAULT_ORG_LIST_IMG = "/resources/img/org_avatar.png";
}
