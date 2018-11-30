package com.noriental.praxissvr.answer.util;

import com.noriental.praxissvr.answer.bean.StudentExercise;
import com.noriental.utils.json.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author kate
 * @create 2017-11-20 11:35
 * @desc 学生做答日志打印
 **/
public class PraxisAnswerLogUtil {

    private static final Logger logger = LoggerFactory.getLogger(PraxisAnswerLogUtil.class);

    public static void logDataToFile(List<StudentExercise> seList, String uuidKey) {
        getCommonKey(seList, uuidKey);

    }

    public static void getCommonKey(List<StudentExercise> seList, String uuidKey) {
        if (null==uuidKey){
            logger.error("学生作答客户端传递的唯一主键为空");
            return;
        }
        List resultList = new ArrayList();
        for (StudentExercise se : seList) {
            StudentExercise entity = new StudentExercise();
            entity.setQuestionId(se.getQuestionId());
            entity.setExerciseSource(se.getExerciseSource());
            entity.setStudentId(se.getStudentId());
            entity.setResourceId(se.getResourceId());
            resultList.add(entity);
        }
        Map dataResult = new HashMap(1);
        dataResult.put("answerData", resultList);
        dataResult.put("uniqueKey", uuidKey);
        logger.info(JsonUtil.obj2Json(dataResult));
    }


}
