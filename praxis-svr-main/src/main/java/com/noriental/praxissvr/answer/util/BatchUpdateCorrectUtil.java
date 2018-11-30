package com.noriental.praxissvr.answer.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.noriental.log.TraceKeyHolder;
import com.noriental.praxissvr.answer.bean.AnswerMqVo;
import com.noriental.praxissvr.answer.bean.OperateType;
import com.noriental.praxissvr.answer.bean.StudentExercise;
import com.noriental.praxissvr.answer.request.BatchIntellUpdateRequest;
import com.noriental.praxissvr.answer.request.BatchUpdateRequest;
import com.noriental.praxissvr.answer.service.impl.BatchUpdateCorrectExecutor;
import com.noriental.praxissvr.common.StuAnswerConstant;
import com.noriental.praxissvr.exception.PraxisErrorCode;
import com.noriental.praxissvr.question.request.FindLeafQuesIdsByParentIdsRequest;
import com.noriental.praxissvr.question.response.FindLeafQuesIdsByParentIdsResponse;
import com.noriental.praxissvr.question.service.QuestionService;
import com.noriental.utils.json.JsonUtil;
import com.noriental.utils.redis.RedisUtil;
import com.noriental.validate.exception.BizLayerException;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.lang.invoke.MethodHandles;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.noriental.praxissvr.answer.util.StuAnswerUtil.*;

/**
 * @author kate
 * @create 2017-12-25 10:43
 * @desc 一键批改工具类
 **/
public class BatchUpdateCorrectUtil {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * 先筛选未批改的数据、再根据题目筛选、再根据学生ID筛选要一键批改的数据，并将批改结果追加上
     *
     * @param allSes
     * @param request
     * @return
     */
    public static List<StudentExercise> filterBatchUpdateCorrectData(List<StudentExercise> allSes, BatchUpdateRequest
            request) {
        Long questionId = request.getQuestionId();
        List<Long> studentIds = request.getStudentIds();
        List<StudentExercise> filterResult = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(allSes)) {
            for (StudentExercise exercise : allSes) {   //原来数据
                //exercise.setFlag(null);
                Long qId;
                if (null != exercise.getParentQuestionId()) {   //所属大题id  单题时为空
                    qId = exercise.getParentQuestionId();
                } else {
                    qId = exercise.getQuestionId();
                }
                //筛选未完成批改的题目 (exercise.getFlag() == null ? true : exercise.getFlag() != 3)
                //isAllCorrect 根据题型结构判断是否全部批改完成 (否)
                if (!AnswerCommonUtil.isAllCorrect(exercise.getResult(), exercise.getStructId()) && (null !=
                        questionId ? qId.equals(questionId) : true)) {
                    for (Long studentId : studentIds) {     //批改数据
                        if (exercise.getStudentId().equals(studentId)) {
                            //批改角色和批改结果的处理
                            componentCorrectIdAndRole(exercise, request);
                            exercise.setFlag(3);
                            // Map<Long,Double> totalScoreMap       key : 题目的ID     value : 题目的分值
                            Double score = request.getTotalScoreMap() != null ? (request.getTotalScoreMap().get
                                    (exercise.getQuestionId()) == null ? null : request.getTotalScoreMap().get
                                    (exercise.getQuestionId()) ): null;
                            exercise.setTotalScore(score);  //小题总分
                            exercise.setScore(StuAnswerUtil.getScoreByStruct(exercise.getStructId(), score,
                                    exercise.getResult())); //得分

                            //数据流转
                            StuAnswerUtil.updateFlowTurnCorrectState(exercise);

                            filterResult.add(exercise);
                        }
                    }
                }
            }
        }
        return filterResult;
    }

    /**
     * 批改角色和批改结果的处理
     *
     * @param exercise
     * @param request
     */
    private static void componentCorrectIdAndRole(StudentExercise exercise, BatchUpdateRequest request) {   //原来数据  批改数据
        //当前的批改结果
        String result = request.getResult();    //批改数据
        String oldResult = exercise.getResult();    //原来数据
        exercise.setCorrectorId(request.getCorrectorId());
        exercise.setCorrectorTime(new Date());
        exercise.setLastUpdateTime(new Date());
        //如果题型结构为填空题并且批改结果不为半对半错  填空题没有半对半错的批改结果，所以半对半错的数据要过滤掉
        if (StuAnswerConstant.StructType.STRUCT_TKT.equals(exercise.getStructId()) && !StuAnswerConstant
                .ExerciseResult.HALFRIGHT.equals(request.getResult())) {
            List<Map<Object, Object>> resultDataList = JsonUtil.readValue(oldResult, new
                    TypeReference<List<Map<String, Object>>>() {
            });
            List<Map<Object, Object>> finalResultList = new ArrayList<>();
            List<Map<Object, Object>> finalRoleList = new ArrayList<>();
            for (Map<Object, Object> dataMap : resultDataList) {    //原来数据
                Map<Object, Object> resultMap = new HashMap<>(1);
                Map<Object, Object> roleMap = new HashMap<>(1);
                int index = Integer.valueOf(dataMap.get(BLANK_INDEX) + "");     //index
                String value = dataMap.get(BLANK_RESULT) + "";      //result
                //已批改的填空题结果保持不变
                if (value.equals(StuAnswerConstant.ExerciseResult.SUBMITED)) {  //有答案未批改
                    value = result;
                }
                resultMap.put(BLANK_INDEX, index);
                resultMap.put(BLANK_RESULT, value);
                roleMap.put(BLANK_INDEX, index);
                roleMap.put(CORRECTOR_ROLE, request.getCorrectorRole());
                finalResultList.add(resultMap);
                finalRoleList.add(roleMap);
            }
            exercise.setCorrectorRole(JsonUtil.obj2Json(finalRoleList));
            exercise.setResult(JsonUtil.obj2Json(finalResultList));
        }
        if (!StuAnswerConstant.StructType.STRUCT_TKT.equals(exercise.getStructId())) {
            exercise.setCorrectorRole(request.getCorrectorRole());
            exercise.setResult(result);
        }
        logger.info("componentCorrectIdAndRole StudentExercise jsonData:{}", JsonUtil.obj2Json(exercise));
    }


    /**
     * 将数组根据要拆分的数据大小拆分成多个数组
     *
     * @param datas
     * @param splitSize
     * @return
     */
    public static <T> List<List<T>> spliceArrays(List<T> datas, int splitSize) {
        if (datas == null || splitSize < 1) {
            return null;
        }
        int totalSize = datas.size();
        int count = (totalSize % splitSize == 0) ? (totalSize / splitSize) : (totalSize / splitSize + 1);
        List<List<T>> rows = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            List<T> cols = datas.subList(i * splitSize, (i == count - 1) ? totalSize : splitSize * (i + 1));
            rows.add(cols);
        }
        return rows;
    }

    /**
     * 批量多线程更新缓存数据
     *
     * @param dataList
     * @param redisUtilExtend
     */
    public static void updateCorrectRedisData(List<List<StudentExercise>> dataList, RedisUtilExtend redisUtilExtend) {
        Long startTime = System.currentTimeMillis();
        int number = dataList.size();
        CountDownLatch end = new CountDownLatch(number);
        BatchUpdateCorrectExecutor[] excutors = new BatchUpdateCorrectExecutor[number];
        String traceKey = TraceKeyHolder.getTraceKey();
        for (int i = 0; i < number; i++) {
            excutors[i] = new BatchUpdateCorrectExecutor(end, dataList.get(i), traceKey, redisUtilExtend);
        }
        ExecutorService exe = Executors.newFixedThreadPool(number);
        for (BatchUpdateCorrectExecutor executor : excutors) {
            exe.execute(executor);
        }
        try {
            end.await();//等待结束
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            logger.info("updateCorrectRedisData all finished");
        }
        //注意：此时main线程已经要结束了，但是exe线程如果不关闭是不会结束的
        exe.shutdown();
        Long endTime = System.currentTimeMillis();
        logger.info("batchCorrect updateCorrectRedisData cost time:{} ms", (endTime - startTime));
    }


    /***
     * 将一键批改的结果数据通过异步MQ写人mysql
     * @param filterDataList
     * @param rabbitTemplate
     * @param operateType
     */
    public static void sendDataToMq(List<StudentExercise> filterDataList, RabbitTemplate rabbitTemplate, OperateType
            operateType, String message) {
        AnswerMqVo in = new AnswerMqVo();
        try {
            in.setStudentExerciseList(filterDataList);
            in.setRequestId(TraceKeyHolder.getTraceKey());
            in.setOperateType(operateType);
            rabbitTemplate.convertAndSend(in);
        } catch (Exception e) {
            //"老师批改或批注数据发送MQ失败,失败的数据:{},失败的原因:{}"
            logger.error(message, JsonUtil.obj2Json(in), e);
            throw new BizLayerException("", PraxisErrorCode.BATCH_CORRECT_EXCEPTION);
        }

    }

    /**
     * 查询复合题的所有子题ID
     *
     * @param questionId
     * @param redisUtil
     * @param questionService
     * @return
     */
    public static List<Long> getLeafQuesIds(Long questionId, RedisUtil redisUtil, QuestionService questionService) {
        Object obj = redisUtil.get(RedisKeyUtil.LEAF_QUESTION_PREFIX + questionId);
        if (obj != null) {
            return (List<Long>) obj;
        }
        FindLeafQuesIdsByParentIdsRequest req = new FindLeafQuesIdsByParentIdsRequest();
        List<Long> list = new ArrayList<>();
        list.add(questionId);
        req.setParentQuesIds(list);
        FindLeafQuesIdsByParentIdsResponse leafQuesIdsByParentIds = questionService.findLeafQuesIdsByParentIds(req);
        if (leafQuesIdsByParentIds.success() && CollectionUtils.isNotEmpty(leafQuesIdsByParentIds.getLeafQuesIds())) {
            redisUtil.set(RedisKeyUtil.LEAF_QUESTION_PREFIX + questionId, leafQuesIdsByParentIds.getLeafQuesIds(),
                    RedisKeyUtil.LEAF_QUESTION_EXPIRE);
            return leafQuesIdsByParentIds.getLeafQuesIds();
        } else {
            List<Long> sonList = new ArrayList<>();
            sonList.add(questionId);
            redisUtil.set(RedisKeyUtil.LEAF_QUESTION_PREFIX + questionId, sonList, RedisKeyUtil.LEAF_QUESTION_EXPIRE);
            return sonList;
        }
    }

    /**
     * 按题的一键使用智能批改数据过滤
     *
     * @param allSes
     * @param request
     * @return
     */
    public static List<StudentExercise> filterBatchUpdateIntellCorrectData(List<StudentExercise> allSes,
                                                                           BatchIntellUpdateRequest request) {
        List<Long> studentIds = request.getStudentIds();
        List<StudentExercise> filterResult = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(allSes)) {
            for (StudentExercise studentExercise : allSes) {
                if (studentExercise.getAi()) { //筛选包含智批标签的数据
                    for (Long studentId : studentIds) {
                        if (studentId.equals(studentExercise.getStudentId())) { //筛选指定的学生的做答记录
                            correctBusDeal(studentExercise);
                            studentExercise.setCorrectorId(request.getCorrectorId());
                            studentExercise.setFlag(3);

                            //数据流转
                            StuAnswerUtil.updateFlowTurnCorrectState(studentExercise);

                            filterResult.add(studentExercise);
                        }
                    }
                }
            }
        }
        return filterResult;
    }

    /**
     * 使用智能批改的批改角色和批改结果的处理
     *
     * @param se
     */
    public static void correctBusDeal(StudentExercise se) {
        //获取老师批改结果
        String correctResult = se.getResult();
        se.setCurrentResult(correctResult);
        //获取智能批改结果
        String intellCorrectResult = se.getIntellResult();
        //智能批改不为空
        if (null != intellCorrectResult) {
            String[] resulrArray = intellCorrectResult.split("_");
            //如果题型结构为填空题，老师批改的空数据保留，其他情况直接使用智能批改结果
            if (StuAnswerConstant.StructType.STRUCT_TKT.equals(se.getStructId())) {
                StuAnswerUtil.assembleTkt(resulrArray[2], se);
            } else {
                se.setResult(resulrArray[2]);
                se.setCorrectorRole(StuAnswerConstant.CorrectorRole.TEACHER);
            }
        } else {
            logger.error("[[submitIntellCorrectAnswer]]使用智能批改结果发现智能批改结果数据不存在！");
            throw new BizLayerException("", PraxisErrorCode.INTELL_CORRECT_RESULT);
        }
        se.setCorrectorTime(new Date());
        se.setLastUpdateTime(new Date());
    }

    public static List<StudentExercise> getBatchIntellCorectInfo(List<StudentExercise> allSes) {
        StudentExercise exercise = allSes.get(0);
        List<StudentExercise> resultList = new ArrayList<>();
        //智能批改
        if (StuAnswerUtil.isExerciseSource(exercise)) {
            for (StudentExercise entity : allSes) {
                //获取智能批改的value
                String intellCorrectResult = entity.getIntellResult();
                if (entity.getStructId().equals(StuAnswerConstant.StructType.STRUCT_TKT)) {
                    if (null != intellCorrectResult) {
                        if (entity.getCorrectorRole().contains(CORRECTOR_ROLE_AI)) {
                            entity.setAi(true);
                        } else {
                            entity.setAi(false);
                        }
                    } else {
                        entity.setAi(false);
                    }
                } else {
                    entity.setAi(false);
                }
                resultList.add(entity);
            }
        }
        return resultList;
    }


}
