package com.noriental.praxissvr.answer.util;

import java.util.Date;

/**
 * Created by shengxian on 2016/12/26.
 */
public class StuAnswerUtilTest {
   /* @Test
    public void s(){
        System.out.println("-----"+StuAnswerUtil.getExerciseResult(StuAnswerConstant.StructType.STRUCT_7x5,"x[1,1,
        1xxx]"));
    }*/

    public static void main(String[] args) {
       /* StudentExercise oldData = new StudentExercise();
        StudentExercise newData = new StudentExercise();
        oldData.setResult("[{\"result\":2,\"index\":\"1\"},{\"result\":1,\"index\":\"2\"},{\"result\":6," +
                "\"index\":\"3\"},{\"result\":1,\"index\":\"4\"}]");
        newData.setResult("[{\"result\":2,\"index\":\"1\"},{\"result\":2,\"index\":\"2\"}]");
        oldData.setStructId(4);

      *//*  oldData.setResult("1");
        newData.setResult("2");
        oldData.setStructId(1);*//*

        String response = StuAnswerUtil.getCorrectInfoStatus(oldData, newData);*/

       // System.out.println("response Data:"+getTimeDiff());

        //StuAnswerUtil.getScoreByStruct();
    }


    public static long getTimeDiff(Date endDate, Date nowDate) {
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        if (null != endDate) {
            long diff = nowDate.getTime() - endDate.getTime();
            long day = diff / nd;
            long hour = 0;
            if (day > 0) {
                hour = 24 * day;
            }
            hour = diff % nd / nh + hour;
            return hour;

        }
        return 0;
    }


}