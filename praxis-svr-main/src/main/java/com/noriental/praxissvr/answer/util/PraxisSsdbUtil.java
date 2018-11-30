package com.noriental.praxissvr.answer.util;


import com.noriental.praxissvr.answer.bean.OperateType;
import com.noriental.praxissvr.answer.bean.StudentExercise;
import com.noriental.praxissvr.utils.LoggerHolder;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.List;

public class PraxisSsdbUtil {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    public static final int CORRECTTYPE = 2;
    public static final int SUBMITTYPE = 1;
    public static final int INTELIGENTYPE = 3;
    public static final int INTELIGENCORRECTTYPE = 8;
    public static final int SECSTATICTYPE = 4;
    public static final  int BATCHUPDATE=5;
    public static final  int OTHER=10;


    public static final String GET_SSDB_KYE_PRE(StudentExercise se) {
        return "se" + se.getExerciseSource() + "_" + se.getResourceId() + "_" + se.getStudentId() + "_" + se
                .getQuestionId();
    }


    public static void logDataToFile(List<StudentExercise> seList, OperateType operateType) {
        if (OperateType.NONE == operateType) {
            return;
        }
        long start = System.currentTimeMillis();
        int size = CollectionUtils.isNotEmpty(seList) ? seList.size() : 0;
        if (operateType.equals(OperateType.CORRECT)||operateType.equals(OperateType.ANSWER)) {
            for (StudentExercise studentExercise : seList) {
                String key = GET_SSDB_KYE_PRE(studentExercise);
                String value;
                key += "r";
                value = studentExercise.getResult();
                String brushInfo = LoggerHolder.getBrushLogger(key);
                logger.info("req: set {} {},resp: ok {} {}", key, value, size, brushInfo);
            }
        }
        LoggerHolder.clear();
        long end = System.currentTimeMillis();
        PraxisSsdbCostUtil.logger("+++++ Ssdb Time =" + Double.toString((double) (end - start) / 1000.0D) + "s ");
    }

    public static String getCommonKey(StudentExercise exercise) {
        String key = exercise.getExerciseSource() + "_" + exercise.getResourceId() + "_" + exercise.getStudentId() +
                "_" + exercise.getQuestionId();
        return key;
    }


}



