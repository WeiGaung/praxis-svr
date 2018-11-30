package com.noriental.praxissvr.wrongQuestion.util;

import com.noriental.praxissvr.answer.bean.StudentExercise;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * Created by kate on 2016/12/19.
 */
public class TableNameUtil {
    private static final String STUKNOWLEDGETABLENAME = "neworiental_answer.entity_stu_ques_knowledge_";
    private static final String STUDENTERROREXETABLENAME = "neworiental_answer.entity_student_error_exe_";
    private static final String STUDENTEXETABLENAME = "neworiental_answer.entity_student_exercise_";

    /**
     * entity_stu_ques_knowledge
     */
    public static String getStuQuesKnowledge(Long studentId) {
        Long suffixNum = studentId % 100;
        String tableName = STUKNOWLEDGETABLENAME + suffixNum;
        return tableName;
    }


    /**
     * entity_student_error_exe
     *
     * @param studentId
     * @return
     */
    public static String getStudentErrorExe(Long studentId) {
        Long suffixNum = studentId % 100;
        String tableName = STUDENTERROREXETABLENAME + suffixNum;
        return tableName;
    }


    /**
     * 答题场景为上课、作业、测评、预习
     *
     * @param exercise
     * @return
     */
    public static String getStudentExercise(StudentExercise exercise) {
        int year = exercise.getYear();
        long classId = exercise.getClassId();
        String tableName = STUDENTEXETABLENAME + year + "_" + classId % 100 / 2;
        return tableName;
    }

    /**
     * 答题场景为自主练习
     *
     * @param exercise
     * @return
     */
    public static String getTableName(StudentExercise exercise) {
        int year = exercise.getYear();
        long studentId = exercise.getStudentId();
        String tableName = STUDENTEXETABLENAME + year + "_" + studentId % 100 / 2;
        return tableName;
    }


    public static void updateTableName(List<StudentExercise> list) {
        if (CollectionUtils.isNotEmpty(list)) {
            StudentExercise exercise = list.get(0);
            int year = exercise.getYear();
            long classId = exercise.getClassId();
            String tableName = STUDENTEXETABLENAME + year + "_" + classId % 100 / 2;
            for (StudentExercise se : list) {
                se.setTableName(tableName);
            }
        }

    }

}
