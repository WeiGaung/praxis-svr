package com.noriental.praxissvr.answer.util;

import com.noriental.praxissvr.answer.bean.StudentExercise;

/**
 * 负责 统一管理 redis类的 key值
 * 慢慢将其他 key 添加进来
 * Created by chendengyu on 2016/9/21.
 */
public class RedisKeyUtil {

    //问题结构的 存储过期时间
    public static final String QUESTION_KEY_STRUCT = "_QuerStructAndParent1_view_";
    public static final int QUESTION_KEY_EXPIRE = 3600*2;
    //存储 题 对应的子题 列表
    public  static final String LEAF_QUESTION_PREFIX = "_LeafQuestionIds_";
    public  static final int LEAF_QUESTION_EXPIRE = 3600*2;
    //存储 题 作答的lock信息
    public  static final String SUBMIT_ANSWER_LOCK_PREFIX = "_AnswerInfoLock_";
    public  static final int SUBMIT_ANSWER_LOCK_EXPIRE = 7;
    public static final  String  QUESTION_STRUCTID_PARENTID="qspid_";

    //按照题集缓存题目
    public  static final String GET_PAD_QUESTIONS = "_PadQuestions_";
    //白名单学校
    public static final String GET_WIHTE_SCHOOLS="_WhiteSchools_";

    //简单的 key拼接
    public static String makeKey(String key,String... keys)
    {
        StringBuilder stringBuffer = new StringBuilder(key);
        for(String k:keys)
        {
            stringBuffer.append("_"+k);
        }
        return stringBuffer.toString();
    }

    public static String getQueStructParentId(StudentExercise se){
        StringBuilder stringBuffer = new StringBuilder(QUESTION_STRUCTID_PARENTID);
        stringBuffer.append(se.getQuestionId());
        return stringBuffer.toString();
    }



    //存储 题 批改的lock信息
    public  static final String CORRECT_ANSWER_LOCK_PREFIX = "_CorrectInfoLock_";
    public  static final int CORRECT_ANSWER_LOCK_EXPIRE = 7;

    //存储批改后置业务的的lock信息
    public  static final String CORRECT_MORE_LOCK_PREFIX = "_CorrectMoreLock_";
    public  static final int CORRECT_MORE_LOCK_EXPIRE = 7;
}
