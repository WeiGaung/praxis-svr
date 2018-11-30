package com.noriental.praxissvr.common;

import java.util.*;

public class StuAnswerConstant {
    /**
     * 体系类型
     */
    public static class ResourceType {
        public static final Integer RESOURCE_TYPE_KNOWLEDGE = 1;//知识体系
        public static final Integer RESOURCE_TYPE_MATERIAL = 2;//教材体系
        private static Map<Integer, String> brushNameMap = new HashMap<>();

        static {
            brushNameMap.put(RESOURCE_TYPE_KNOWLEDGE, "专项练习");
            brushNameMap.put(RESOURCE_TYPE_MATERIAL, "同步练习");
        }

        public static String getBrushNameByCode(Integer code) {
            return brushNameMap.get(code);
        }
    }


    public static class BusType{
        public static final String BUS_TYPE_SUBMIT="submit";
        public static final String BUS_TYPE_INTELL_SUBMIT="intellSubmit";

    }

    /**
     * 学生做题数据类型
     *
     * @author sheng.xiao
     *         2015年12月24日
     */
    public static class DataType {
        public static final Integer WORK_ANSWERED = 1;//知识点刷题已做题目
        public static final Integer WORK_ANSWERED_MATERIAL = 3;//教材体系刷题已做题目
        public static final Integer WRONG_QUES = 2;//错题题目，知识点下错题
        public static final Integer WRONG_QUES_MATERIAL = 4;//错题题目，教材体系下错题
    }

    /**
     * 答题场景
     */
    public static class ExerciseSource {
        public static final String LESSON = "1";  //上课
        public static final String WRONGBOOK = "3"; //消灭错题->错题 @1.8
        public static final String ENHANCE = "4";//强化练习->测验 @1.8
        public static final String WORK = "5";//自主练习->测验 @1.8
        public static final String HO_WORK = "6";//作业
        public static final String EVALUATION = "7";//测评
        public static final String PREVIEW = "8";//预习
        public static final String GOOD_QUES = "9";//习题 @since1.8
        public static final String WRONG_QUES = "10";//错题重做 @since1.8

        private static Map<String, String> exerciseSourceMap = new HashMap<>();
        public static final List<String> OWNER_SOURCES = Arrays.asList(ENHANCE,WORK,GOOD_QUES,WRONG_QUES);//自主练习类，区别于教师发布
        static {
            exerciseSourceMap.put(ExerciseSource.LESSON, "上课");
            exerciseSourceMap.put(ExerciseSource.WRONGBOOK, "自主学习");//原来的消灭错题
            exerciseSourceMap.put(ExerciseSource.ENHANCE, "自主学习");//原来的错题强化
            exerciseSourceMap.put(ExerciseSource.WORK, "自主学习");//原来的自主练习
            exerciseSourceMap.put(ExerciseSource.HO_WORK, "作业");
            exerciseSourceMap.put(ExerciseSource.EVALUATION, "测评");
            exerciseSourceMap.put(ExerciseSource.PREVIEW, "预习");
            exerciseSourceMap.put(ExerciseSource.GOOD_QUES, "习题");//好题功能
            exerciseSourceMap.put(ExerciseSource.WRONG_QUES, "自主学习");//原来的错题本功能
        }

        public static String getExerciseSourceNameByCode(String code) {
            if (code == null) {
                return null;
            }
            return exerciseSourceMap.get(code);
        }
    }


    /**
     * 作答状态
     */
    public static class ExerciseResult {

        public static final String NOANSWER = "7";//未填答案
        public static final String SUBMITED = "6";//填答案未批改
        public static final String RIGHT = "1";//正确
        public static final String HALFRIGHT = "5";//半对半错
        public static final String ERROR = "2";//错误
        public static final String MASTERED = "9";//已掌握

        private static Map<String, String> exerciseResultMap = new HashMap<>();

        static {
            exerciseResultMap.put(ExerciseResult.NOANSWER, "未填答案");
            exerciseResultMap.put(ExerciseResult.SUBMITED, "有答案未批改");
            exerciseResultMap.put(ExerciseResult.RIGHT, "正确");
            exerciseResultMap.put(ExerciseResult.HALFRIGHT, "半对");
            exerciseResultMap.put(ExerciseResult.ERROR, "错误");
            exerciseResultMap.put(ExerciseResult.MASTERED, "已掌握");

        }

        public static String getExerciseResultNameByCode(String code) {
            return exerciseResultMap.get(code);
        }
    }

    /**
     * 刷题是否提交
     */
    public static class WorkStatus {
        public static final Integer NO_COMPLETE = 2;
        public static final Integer COMPLETE = 1;
    }




    /**
     * 题型结构
     */
    public static class StructType {
        /**
         * 选择题
         */
        public static final Integer STRUCT_XZT = 1;
        /**
         * 填空题
         */
        public static final Integer STRUCT_TKT = 4;
        /**
         * 判断题
         */
        public static final Integer STRUCT_PDT = 2;
        /**
         * 简答题
         */
        public static final Integer STRUCT_JDT = 3;
        /**
         * 作图题
         */
        public static final Integer STRUCT_ZTT = 8;
        /**
         * 七选五
         */
        public static final Integer STRUCT_7x5 = 9;
        /**
         * 开放题
         */
        public static final Integer STRUCT_PZT = 10;
        /***
         *口语文章朗读-英文段落朗读测评 paragraph
         */
        public static final Integer PARAGRAPH_READ = 11;

        /***
         *听单词跟读单词-英文单词评测
         */
        public static final Integer WORD_READ = 12;

        /**
         * 客观题题型
         */
        private static final List<Integer> STRUCT_OBJECTIVE = Arrays.asList(STRUCT_XZT,STRUCT_PDT,STRUCT_7x5);

        /**
         * 是否是客观题结构（不需要批改,程序对比得到答案）
         * @param structId
         * @return
         */
        public static boolean isObjectiveStruct(Integer structId){
            return STRUCT_OBJECTIVE.contains(structId);
        }

    }

    /**
     * 习题批改人角色
     *
     * @author sx.xiao @date 2015年3月18日
     */
    public static class CorrectorRole {
        public static final String TEACHER = "teacher";
        public static final String STUDENT = "student";

        public static final String TEACHER_NUM = "1";
        public static final String STUDENT_NUM = "2";
        private static Map<String, String> map = new HashMap<>();

        static {
            map.put(TEACHER, "老师");
            map.put(STUDENT, "学生");
        }

        public static String getNameByCode(String code) {
            return map.get(code);
        }
    }

    public static class FIELD {
        public static final String QUESTION_ID = "QUESTION_ID";
        public static final String STUDENET_ID = "STUDENET_ID";
        public static final String COURSE_ID = "COURSE_ID";
        public static final String CLASS_ID = "CLASS_ID";
        public static final String RESOURCE_ID = "RESOURCE_ID";
        public static final String EXERCISE_SOURCE = "EXERCISE_SOURCE";
        public static final String QUESTION_ID_LIST = "QUESTION_ID_LIST";
        public static final String STU_QUES_ID = "STU_QUES_ID";
        public static final String PARENT_QUESTION_ID = "PARENT_QUESTION_ID";
        public static final String CORRECTOR_ID = "CORRECTOR_ID";
        public static final String CORRECTOR_ROLE = "CORRECTOR_ROLE";
        public static final String POSTIL_TEACHER = "POSTIL_TEACHER";
        public static final String RESULT = "RESULT";
    }


    /**
     * 章节或知识点级别
     */
    public enum LevelEnum{
        LEVEL_1(1,"第一级"),
        LEVEL_2(2,"第二级"),
        LEVEL_3(3,"第三级");
        public static LevelEnum getEnumByCode(int code){
            for(LevelEnum e : LevelEnum.values()){
                if(e.getCode()==code){
                    return e;
                }
            }
            return null;
        }
        LevelEnum(int code,String desc){
            this.code=code;
            this.desc=desc;
        }
        private int code;

        private String desc;

        public int getCode() {
            return code;
        }
        public String getDesc() {
            return desc;
        }
    }

    public enum DataTypeEnum{
        ANSWERED_KONWLEDGE(1,"知识点已做题目"),
        ANSWERED_MATERIAL(3,"教材体系已做题目"),
        WRONG_KONWLEDGE(2,"知识点下错题"),
        WRONG_MATERIAL(4,"教材体系下错题");
        public static DataTypeEnum getEnumByCode(int code){
            for(DataTypeEnum e : DataTypeEnum.values()){
                if(e.getCode()==code){
                    return e;
                }
            }
            return null;
        }
        DataTypeEnum(int code,String desc){
            this.code=code;
            this.desc=desc;
        }
        private int code;

        private String desc;

        public int getCode() {
            return code;
        }
        public String getDesc() {
            return desc;
        }
    }


    public static class CorrectType{
        //按人的一键批改
        public static final Integer BATCH_UPDATE_BY_PERSON = 1;
        //按题的一键批改
        public static final Integer BATCH_UPDATE_BY_QUESTION = 2;

    }


}
