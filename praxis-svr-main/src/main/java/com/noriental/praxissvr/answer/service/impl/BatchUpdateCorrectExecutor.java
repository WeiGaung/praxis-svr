package com.noriental.praxissvr.answer.service.impl;

import com.noriental.praxissvr.answer.bean.StudentExercise;
import com.noriental.praxissvr.answer.util.AnswerCommonUtil;
import com.noriental.praxissvr.answer.util.RedisUtilExtend;
import com.noriental.praxissvr.answer.util.StuAnswerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * @author kate
 * @create 2017-12-25 14:09
 * @desc 一键批改缓存更新执行线程
 **/
public class BatchUpdateCorrectExecutor implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private CountDownLatch end;
    private List<StudentExercise> dataList;
   // private RedisUtil redisUtil;
    private String traceKey;
    private RedisUtilExtend redisUtilExtend;

    public BatchUpdateCorrectExecutor( CountDownLatch end, List<StudentExercise> dataList,
                                      String traceKey,RedisUtilExtend redisUtilExtend) {
        this.end = end;
        this.dataList = dataList;
       // this.redisUtil = redisUtil;
        this.traceKey = traceKey;
        this.redisUtilExtend=redisUtilExtend;
    }

    @Override
    public void run() {
        Long startTime = System.currentTimeMillis();
        AnswerCommonUtil.initLogRequestId(traceKey);
        try {
            //使用pipline批量更新数据和过期时间
            Map redisMap=new HashMap();
            Map expireMap=new HashMap();
            for (StudentExercise studentExercise : dataList) {
                String studentExerciseRedisKey = StuAnswerUtil.getRedisKeyOfStudentExercise(studentExercise);
                Long timeFlag = StuAnswerUtil.getDetailRedisKeyTtl(redisUtilExtend, studentExercise);
                //redisUtil.viewSet(studentExerciseRedisKey, studentExercise, timeFlag.intValue());
                redisMap.put(studentExerciseRedisKey,studentExercise);
                expireMap.put(studentExerciseRedisKey,timeFlag.intValue());
            }
            redisUtilExtend.viewmSet(0,redisMap,expireMap);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            end.countDown();
        }
        Long endTime = System.currentTimeMillis();
        logger.info("BatchUpdateCorrectExecutor cost time:{} ms", (endTime - startTime));
    }


}
