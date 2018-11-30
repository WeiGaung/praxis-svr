package com.noriental.praxissvr.answer.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.noriental.lessonsvr.rservice.LessonService;
import com.noriental.log.TraceKeyHolder;
import com.noriental.praxissvr.answer.bean.*;
import com.noriental.praxissvr.answer.mappers.AnswerCorrectMapper;
import com.noriental.praxissvr.answer.mappers.PigaiCirculationMapper;
import com.noriental.praxissvr.answer.request.*;
import com.noriental.praxissvr.answer.service.BatchUpdateCorrectService;
import com.noriental.praxissvr.answer.service.StudentExerciseService;
import com.noriental.praxissvr.answer.util.*;
import com.noriental.praxissvr.brush.dao.StudentWorkDao;
import com.noriental.praxissvr.common.StuAnswerConstant;
import com.noriental.praxissvr.exception.PraxisErrorCode;
import com.noriental.praxissvr.question.service.QuestionService;
import com.noriental.praxissvr.wrongQuestion.util.TableNameUtil;
import com.noriental.trailsvr.service.TrailCountService;
import com.noriental.utils.json.JsonUtil;
import com.noriental.utils.redis.RedisUtil;
import com.noriental.validate.bean.CommonDes;
import com.noriental.validate.bean.CommonResponse;
import com.noriental.validate.exception.BizLayerException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.noriental.praxissvr.exception.PraxisErrorCode.INNER_ERROR;

/**
 * @author kate
 * @create 2017-12-25 10:09
 * @desc 一键批改（按人、按题）的、按题的一键智能批改业务实现类
 **/
@Service("batchUpdateCorrectService")
public class BatchUpdateCorrectServiceImpl implements BatchUpdateCorrectService {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @Resource
    private StudentExerciseService stuExeService;
    @Resource
    private RabbitTemplate batchUpdateCorrectTemplate;
    @Resource
    private AnswerCorrectMapper answerCorrectMapper;
    @Resource
    private QuestionService questionService;
    @Resource
    private RabbitTemplate batchUpdateCorrectTrailTemplate;
    @Resource
    private LessonService lessonService;
    @Resource
    private TrailCountService trailCountService;
    @Resource
    private StudentWorkDao studentWorkDao;
    @Resource
    private RedisUtilExtend redisUtilExtend;
    @Resource
    private PigaiCirculationMapper pigaiCirculationMapper;
    @Resource
    private RedisUtil redisUtil;
    @Autowired
    public AnswerBranchParam answerBranchParam;


    private ExecutorService threadPool = Executors.newFixedThreadPool(100);

    /***
     * 操作步骤
     * 一键批改只针对未批改的题目进行一键批改
     * 1、按题批改缓存查询、按人批改缓存查询
     * 2、判断缓存数据是否存在
     *  2.1 缓存存在，更新缓存数据，异步MQ更新mysql数据
     *  2.2 缓存不存在，直接更新mysql
     * 3、不论缓存是否存在查询出来的数据都要做业务筛选
     *    3.1 先筛选未批改的数据、再根据题目筛选、再根据学生ID筛选要一键批改的数据
     * 4、一键批改的数据量过大是否启用多线程处理
     * 5、错题记录的录入
     * 6、通知统计服务
     * 7、批改的后置业务处理
     * @param request
     * @return
     * @throws BizLayerException
     */
    private final String CorrectBatchByPerson = "correct_byperson_";
    private final String CorrectBatchByQuestion = "correct_byquestion_";
    private final String IntellCorrectByQuestion = "intell_correct_byquestion_";


    /**
     * 一键批改
     *  按人
     *  按题
     * @param request
     */
    @Override
    public CommonDes batchUpdateCorrect(BatchUpdateRequest request) throws BizLayerException {

        //暂不添加防重复提交的处理 redisUtil缺少setnx的基础方法
        Long startTime = System.currentTimeMillis();
        request.setReqId(TraceKeyHolder.getTraceKey());

        CommonDes commonDes = new CommonDes();
        //是否发MQ     true : 发    false : 不发
        final boolean isMQ;
        //判断发布时间 上线时间   true : 调别人  false : 调自己
        boolean flag = AnswerCommonUtil.getQuestionPublicTime(request.getResourceId(), lessonService, redisUtilExtend,answerBranchParam.getPythonServerOnlineTime());
        if(StuAnswerUtil.isWrongQuesChalRecorded(request.getExerciseSource())) {     //任务场景
            if (!flag) {  //调自己
                isMQ = true;
                commonDes = batchUpdateCorrectToOwn(request, isMQ);
                return commonDes;
            }
        }
        //任务场景 自主学习场景
        //向洪清组发送数据
        final String param = JsonUtil.obj2Json(request);
        final String url = answerBranchParam.getAnswerBranchUrl() + "/correct/batch_update_correct";
        String s = AnswerCommonUtil.doPost(param, url);
        Map<String, Object> resultList = JsonUtil.readValue(s, new
                TypeReference<Map<String, Object>>() {
                });
        //需要对返回值进行判断
        if(resultList != null && resultList.size() >= 0 && "success".equals(resultList.get("message"))) {
            //返回成功
            isMQ = false;
            final BatchUpdateRequest target = new BatchUpdateRequest();
            BeanUtils.copyProperties(request,target);
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    batchUpdateCorrectToOwn(target, isMQ);
                }
            });
        }else {     //返回空或失败
            if(resultList == null || resultList.size() == 0) {
                throw new BizLayerException("", INNER_ERROR);
            }else if(resultList.containsKey("code") && resultList.containsKey("message")) {
                commonDes.setCode( Integer.parseInt(resultList.get("code").toString()));
                commonDes.setMessage(resultList.get("message").toString());
                return commonDes;
            }
        }
        Long endTime = System.currentTimeMillis();
        logger.info("batchUpdateCorrect cost time:{} ms", (endTime - startTime));
        return commonDes;

    }
    /**
     * 一键批改
     *  按人
     *  按题
     * @param request
     */
    public CommonDes batchUpdateCorrectToOwn(BatchUpdateRequest request, boolean isMQ) throws BizLayerException {
        //暂不添加防重复提交的处理 redisUtil缺少setnx的基础方法

        Long startTime = System.currentTimeMillis();
        /*doBatchCorrectByPerson(request);
        doBatchCorrectByQuestion(request);*/
        doBatchCorrectByPerson(request,isMQ);
        doBatchCorrectByQuestion(request,isMQ);
        Long endTime = System.currentTimeMillis();
        logger.info("batchUpdateCorrect cost time:{} ms", (endTime - startTime));
        return new CommonDes();
    }

    /**
     * 数据流转一键批改
     *  按人
     *  按题
     * @param requestList
     */
    @Override
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public CommonDes batchUpdateCorrectList(List<BatchUpdateRequest> requestList) throws BizLayerException {
        logger.info("batchUpdateCorrectList 数据流转一键批改:{}", requestList);
        //暂不添加防重复提交的处理 redisUtil缺少setnx的基础方法
        Long startTime = System.currentTimeMillis();
        int count = 0;
        if(CollectionUtils.isNotEmpty(requestList) && requestList.size() > 0) {
            for(BatchUpdateRequest request : requestList) {
                delRedisKeyOfFlowTurnStu(request);
                request.setI(count++);
                doBatchCorrectByPerson(request);
                //doBatchCorrectByQuestion(request);
                doBatchCorrectByQuestionList(request);
            }
        }
        Long endTime = System.currentTimeMillis();
        logger.info("batchUpdateCorrectList cost time:{} ms", (endTime - startTime));
        return new CommonDes();
    }

    /**
     * 按人的一键批改数据查询
     *
     * @param request
     */
    private void doBatchCorrectByPerson(BatchUpdateRequest request) {
        //按人的一键批改
        if (request.getBusType().equals(StuAnswerConstant.CorrectType.BATCH_UPDATE_BY_PERSON)) {
            Long startTime = System.currentTimeMillis();
            List<Long> studentIds = request.getStudentIds();
            StudentExercise se = new StudentExercise();
            se.setExerciseSource(request.getExerciseSource());
            se.setResourceId(request.getResourceId());
            se.setStudentId(studentIds.get(0));
            // correct_byperson_
            String redisKey = CorrectBatchByPerson + request.getExerciseSource() + "_" + request.getResourceId() +
                    "_" + studentIds.get(0);
            // redis加锁
            boolean flag = redisUtilExtend.simpleLock(0, redisKey, 1);
            if (!flag) {    //一键批改不允许重复提交
                throw new BizLayerException("", PraxisErrorCode.BATCH_CORRECT_REPEAT_EXCEPTION);
            }
            List<StudentExercise> allSes = StuAnswerUtil.getFromCacheStuBatch(se, redisUtilExtend);
            boolean isCatchExit = false;
            if (CollectionUtils.isNotEmpty(allSes)) {
                isCatchExit = true;
            } else {
                allSes = stuExeService.getDbRecords(se);
            }
            logger.info("doBatchCorrectByPerson allSes data:"+JsonUtil.obj2Json(allSes));
            // 按人,按题的一键批改数据存储
            batchCorrectDataUpdate(allSes, isCatchExit, request, OperateType.BATCHUPDATEREDIS);
            Long endTime = System.currentTimeMillis();
            logger.info("batchUpdateCorrect doBatchCorrectByPerson cost time:{} ms", (endTime - startTime));

        }


    }

    /**
     * 按人的一键批改数据查询
     *
     * @param request
     */
    private void doBatchCorrectByPerson(BatchUpdateRequest request, boolean isMQ) {
        //按人的一键批改
        if (request.getBusType().equals(StuAnswerConstant.CorrectType.BATCH_UPDATE_BY_PERSON)) {
            Long startTime = System.currentTimeMillis();
            List<Long> studentIds = request.getStudentIds();
            StudentExercise se = new StudentExercise();
            se.setExerciseSource(request.getExerciseSource());
            se.setResourceId(request.getResourceId());
            se.setStudentId(studentIds.get(0));
            // correct_byperson_
            String redisKey = CorrectBatchByPerson + request.getExerciseSource() + "_" + request.getResourceId() +
                    "_" + studentIds.get(0);
            // redis加锁
            boolean flag = redisUtilExtend.simpleLock(0, redisKey, 1);
            if (!flag) {    //一键批改不允许重复提交
                throw new BizLayerException("", PraxisErrorCode.BATCH_CORRECT_REPEAT_EXCEPTION);
            }
            List<StudentExercise> allSes = StuAnswerUtil.getFromCacheStuBatch(se, redisUtilExtend);
            boolean isCatchExit = false;
            if (CollectionUtils.isNotEmpty(allSes)) {
                isCatchExit = true;
            } else {
                allSes = stuExeService.getDbRecords(se);
            }
            logger.info("doBatchCorrectByPerson allSes data:"+JsonUtil.obj2Json(allSes));
            // 按人,按题的一键批改数据存储
            //batchCorrectDataUpdate(allSes, isCatchExit, request, OperateType.BATCHUPDATEREDIS);
            batchCorrectDataUpdate(allSes, isCatchExit, request, OperateType.BATCHUPDATEREDIS, isMQ);
            Long endTime = System.currentTimeMillis();
            logger.info("batchUpdateCorrect doBatchCorrectByPerson cost time:{} ms", (endTime - startTime));

        }


    }


    /**
     * 按题的一键批改数据查询
     *
     * @param request
     */
    private void doBatchCorrectByQuestion(BatchUpdateRequest request) {
        if (request.getBusType().equals(StuAnswerConstant.CorrectType.BATCH_UPDATE_BY_QUESTION)) {
            Long startTime = System.currentTimeMillis();
            Long questionId = request.getQuestionId();
            //查询复合题的所有子题ID
            List<Long> quesIds = BatchUpdateCorrectUtil.getLeafQuesIds(questionId, redisUtilExtend, questionService);
            StudentExercise se = new StudentExercise();
            se.setExerciseSource(request.getExerciseSource());
            se.setResourceId(request.getResourceId());
            se.setQuestionIdList(quesIds);
            //correct_byquestion_
            String redisKey = CorrectBatchByQuestion + request.getExerciseSource() + "_" + request.getResourceId() +
                    "_" + questionId;
            boolean flag = redisUtilExtend.simpleLock(0, redisKey, 1);
            if (!flag) {
                throw new BizLayerException("", PraxisErrorCode.BATCH_CORRECT_REPEAT_EXCEPTION);
            }
            List<StudentExercise> allSes = StuAnswerUtil.getFromCacheQeusesBatch(se, redisUtilExtend);
            boolean isCatchExit = false;
            if (CollectionUtils.isEmpty(allSes)) {
                allSes = stuExeService.getDbRecords(se);
            } else {
                isCatchExit = true;
            }
            logger.info("doBatchCorrectByQuestion allSes data:"+JsonUtil.obj2Json(allSes));
            batchCorrectDataUpdate(allSes, isCatchExit, request, OperateType.BATCHUPDATEREDIS);
            Long endTime = System.currentTimeMillis();
            logger.info("batchUpdateCorrect doBatchCorrectByQuestion cost time:{} ms", (endTime - startTime));
        }

    }

    /**
     * 按题的一键批改数据查询
     *
     * @param request
     */
    private void doBatchCorrectByQuestion(BatchUpdateRequest request, boolean isMQ) {
        if (request.getBusType().equals(StuAnswerConstant.CorrectType.BATCH_UPDATE_BY_QUESTION)) {
            Long startTime = System.currentTimeMillis();
            Long questionId = request.getQuestionId();
            //查询复合题的所有子题ID
            List<Long> quesIds = BatchUpdateCorrectUtil.getLeafQuesIds(questionId, redisUtilExtend, questionService);
            StudentExercise se = new StudentExercise();
            se.setExerciseSource(request.getExerciseSource());
            se.setResourceId(request.getResourceId());
            se.setQuestionIdList(quesIds);
            //correct_byquestion_
            String redisKey = CorrectBatchByQuestion + request.getExerciseSource() + "_" + request.getResourceId() +
                    "_" + questionId;
            boolean flag = redisUtilExtend.simpleLock(0, redisKey, 1);
            if (!flag) {
                throw new BizLayerException("", PraxisErrorCode.BATCH_CORRECT_REPEAT_EXCEPTION);
            }
            List<StudentExercise> allSes = StuAnswerUtil.getFromCacheQeusesBatch(se, redisUtilExtend);
            boolean isCatchExit = false;
            if (CollectionUtils.isEmpty(allSes)) {
                allSes = stuExeService.getDbRecords(se);
            } else {
                isCatchExit = true;
            }
            logger.info("doBatchCorrectByQuestion allSes data:"+JsonUtil.obj2Json(allSes));
            batchCorrectDataUpdate(allSes, isCatchExit, request, OperateType.BATCHUPDATEREDIS, isMQ);
            Long endTime = System.currentTimeMillis();
            logger.info("batchUpdateCorrect doBatchCorrectByQuestion cost time:{} ms", (endTime - startTime));
        }

    }
    /**
     * 按题的一键批改数据查询
     *
     * @param request
     */
    private void doBatchCorrectByQuestionList(BatchUpdateRequest request) {
        if (request.getBusType().equals(StuAnswerConstant.CorrectType.BATCH_UPDATE_BY_QUESTION)) {
            Long startTime = System.currentTimeMillis();
            Long questionId = request.getQuestionId();
            List<Long> quesIds = BatchUpdateCorrectUtil.getLeafQuesIds(questionId, redisUtilExtend, questionService);
            StudentExercise se = new StudentExercise();
            se.setExerciseSource(request.getExerciseSource());
            se.setResourceId(request.getResourceId());
            se.setQuestionIdList(quesIds);
            String redisKey = CorrectBatchByQuestion + request.getExerciseSource() + "_" + request.getResourceId() +
                    "_" + questionId + "_" + request.getI();
            boolean flag = redisUtilExtend.simpleLock(0, redisKey, 1);
            if (!flag) {
                throw new BizLayerException("", PraxisErrorCode.BATCH_CORRECT_REPEAT_EXCEPTION);
            }
            List<StudentExercise> allSes = StuAnswerUtil.getFromCacheQeusesBatch(se, redisUtilExtend);
            boolean isCatchExit = false;
            if (CollectionUtils.isEmpty(allSes)) {
                allSes = stuExeService.getDbRecords(se);
            } else {
                isCatchExit = true;
            }
            logger.info("doBatchCorrectByQuestion allSes data:"+JsonUtil.obj2Json(allSes));
            batchCorrectDataUpdate(allSes, isCatchExit, request, OperateType.BATCHUPDATEREDIS);
            Long endTime = System.currentTimeMillis();
            logger.info("batchUpdateCorrect doBatchCorrectByQuestion cost time:{} ms", (endTime - startTime));
        }

    }

    /**
     * 按人、按题的一键批改数据存储（缓存的数据存储和异步、同步数据的录入）
     *
     * @param allSes
     * @param isCatchExit
     * @param request
     */
    private void batchCorrectDataUpdate(List<StudentExercise> allSes, boolean isCatchExit, BatchUpdateRequest
            request, OperateType type) {
        Long startTime = System.currentTimeMillis();
        //创建数据筛选的工具类
        List<StudentExercise> filterDataList = BatchUpdateCorrectUtil.filterBatchUpdateCorrectData(allSes, request);
        if (CollectionUtils.isNotEmpty(filterDataList)) {
            if (isCatchExit) {  //缓存存在，更新缓存，异步更新MQ
                logger.info("batchCorrectDataUpdate 一键批改走缓存处理");
                type = OperateType.BATCHUPDATE;
                //错题记录的录入、老师的批改后置业务处理 放入异步消息队列MQ惊醒mysql的持久化
                BatchUpdateCorrectUtil.sendDataToMq(filterDataList, batchUpdateCorrectTemplate, type, "一键批改数据发送MQ失败,"
                        + "失败的数据:{},失败的原因:{}");
                //对filterDataList做数据衡量,超过20条数据，就进行多线程更新缓存
                List<List<StudentExercise>> dataList = BatchUpdateCorrectUtil.spliceArrays(filterDataList, 20);
                BatchUpdateCorrectUtil.updateCorrectRedisData(dataList, redisUtilExtend);

                //数据流转
                int i = pigaiCirculationMapper.updateFlowTurnCorrectStateList(filterDataList);
                logger.info("批改 - IP数据流转,更新状态:{},更新参数:{}",i,JsonUtil.obj2Json(filterDataList));
                /*delRedisKeyOfFlowTurnList(filterDataList, i);*/

            } else {
                BatchUpdateCorrectUtil.sendDataToMq(filterDataList, batchUpdateCorrectTemplate, type, "一键批改数据发送MQ失败,"
                        + "失败的数据:{},失败的原因:{}");
                Long startSecTime = System.currentTimeMillis();
                //同步更新mysql数据(存在隐患)
                TableNameUtil.updateTableName(filterDataList);
                answerCorrectMapper.batchUpdateCorrect(filterDataList);

                //数据流转
                int i = pigaiCirculationMapper.updateFlowTurnCorrectStateList(filterDataList);
                logger.info("批改 - IP数据流转,更新状态:{},更新参数:{}",i,JsonUtil.obj2Json(filterDataList));
                /*delRedisKeyOfFlowTurnList(filterDataList, i);*/

                Long endSecTime = System.currentTimeMillis();
                logger.info("batchUpdateCorrect mysql update cost time:{} ms", (endSecTime - startSecTime));
            }
            PraxisSsdbUtil.logDataToFile(filterDataList, OperateType.CORRECT);
            //通知统计服务
            sendDataToTrailMq(request);
        }
        Long endTime = System.currentTimeMillis();
        logger.info("batchUpdateCorrect batchCorrectDataUpdate cost time:{} ms", (endTime - startTime));
    }

    /**
     * 按人、按题的一键批改数据存储（缓存的数据存储和异步、同步数据的录入）
     *
     * @param allSes
     * @param isCatchExit
     * @param request
     */
    private void batchCorrectDataUpdate(List<StudentExercise> allSes, boolean isCatchExit, BatchUpdateRequest
            request, OperateType type, boolean isMQ) {
        Long startTime = System.currentTimeMillis();
        //创建数据筛选的工具类
        List<StudentExercise> filterDataList = BatchUpdateCorrectUtil.filterBatchUpdateCorrectData(allSes, request);
        if (CollectionUtils.isNotEmpty(filterDataList)) {
            if (isCatchExit) {  //缓存存在，更新缓存，异步更新MQ
                logger.info("batchCorrectDataUpdate 一键批改走缓存处理");

                type = OperateType.BATCHUPDATE;
                //错题记录的录入、老师的批改后置业务处理 放入异步消息队列MQ惊醒mysql的持久化
                BatchUpdateCorrectUtil.sendDataToMq(filterDataList, batchUpdateCorrectTemplate, type, "一键批改数据发送MQ失败,"
                            + "失败的数据:{},失败的原因:{}");
                //对filterDataList做数据衡量,超过20条数据，就进行多线程更新缓存
                List<List<StudentExercise>> dataList = BatchUpdateCorrectUtil.spliceArrays(filterDataList, 20);
                BatchUpdateCorrectUtil.updateCorrectRedisData(dataList, redisUtilExtend);

                //数据流转
                int i = pigaiCirculationMapper.updateFlowTurnCorrectStateList(filterDataList);
                logger.info("批改 - IP数据流转,更新状态:{},更新参数:{}",i,JsonUtil.obj2Json(filterDataList));
                /*delRedisKeyOfFlowTurnList(filterDataList, i);*/

            } else {

                BatchUpdateCorrectUtil.sendDataToMq(filterDataList, batchUpdateCorrectTemplate, type, "一键批改数据发送MQ失败,"
                            + "失败的数据:{},失败的原因:{}");

                Long startSecTime = System.currentTimeMillis();
                //同步更新mysql数据(存在隐患)
                TableNameUtil.updateTableName(filterDataList);
                answerCorrectMapper.batchUpdateCorrect(filterDataList);

                //数据流转
                int i = pigaiCirculationMapper.updateFlowTurnCorrectStateList(filterDataList);
                logger.info("批改 - IP数据流转,更新状态:{},更新参数:{}",i,JsonUtil.obj2Json(filterDataList));
                /*delRedisKeyOfFlowTurnList(filterDataList, i);*/

                Long endSecTime = System.currentTimeMillis();
                logger.info("batchUpdateCorrect mysql update cost time:{} ms", (endSecTime - startSecTime));
            }

            if(isMQ) {
                PraxisSsdbUtil.logDataToFile(filterDataList, OperateType.CORRECT);
                //通知统计服务
                sendDataToTrailMq(request);
            }

        }
        Long endTime = System.currentTimeMillis();
        logger.info("batchUpdateCorrect batchCorrectDataUpdate cost time:{} ms", (endTime - startTime));
    }

    /***
     * 向统计服务发送MQ消息
     * @param request
     */
    private void sendDataToTrailMq(BatchUpdateRequest request) {
        BatchUpdateCorrectTrailRequest trailCountRequest = new BatchUpdateCorrectTrailRequest();
        trailCountRequest.setExerciseSource(request.getExerciseSource());
        trailCountRequest.setResourceId(request.getResourceId());
        trailCountRequest.setRequestId(TraceKeyHolder.getTraceKey());
        trailCountRequest.setStudentIds(request.getStudentIds());
        batchUpdateCorrectTrailTemplate.convertAndSend(trailCountRequest);
        logger.info("一键批改向统计服务发送消息:{}成功。", JsonUtil.obj2Json(trailCountRequest));
    }


    /***
     * 一键使用智能批改
     *  只有按照题的一键智能批改
     *
     * @param request
     * @return
     * @throws BizLayerException
     */
    @Override
    public CommonDes batchUpdateIntellCorrect(BatchIntellUpdateRequest request) throws BizLayerException {

        Long startTime = System.currentTimeMillis();
        request.setReqId(TraceKeyHolder.getTraceKey());

        CommonDes commonDes = new CommonDes();
        //是否发MQ     true : 发    false : 不发
        final boolean isMQ;
        //判断发布时间 上线时间   true : 调别人  false : 调自己
        boolean flag = AnswerCommonUtil.getQuestionPublicTime(request.getResourceId(), lessonService, redisUtilExtend,answerBranchParam.getPythonServerOnlineTime());
        if(StuAnswerUtil.isWrongQuesChalRecorded(request.getExerciseSource())) {     //任务场景
            if (!flag) {  //调自己
                isMQ = true;
                commonDes = batchUpdateIntellCorrectToOwn(request, isMQ);
                return commonDes;
            }
        }
        //任务场景 自主学习场景
        //向洪清组发送数据
        final String param = JsonUtil.obj2Json(request);
        final String url = answerBranchParam.getAnswerBranchUrl() + "/correct/batch_use_intell_correct";
        String s = AnswerCommonUtil.doPost(param, url);
        Map<String, Object> resultList = JsonUtil.readValue(s, new
                TypeReference<Map<String, Object>>() {
                });
        //需要对返回值进行判断
        if(resultList != null && resultList.size() >= 0 && "success".equals(resultList.get("message"))) {
            //返回成功
            isMQ = false;
            final BatchIntellUpdateRequest target = new BatchIntellUpdateRequest();
            BeanUtils.copyProperties(request,target);
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    batchUpdateIntellCorrectToOwn(target, isMQ);
                }
            });
        }else {     //返回空或失败
            if(resultList == null || resultList.size() == 0) {
                throw new BizLayerException("", INNER_ERROR);
            }else if(resultList.containsKey("code") && resultList.containsKey("message")) {
                commonDes.setCode( Integer.parseInt(resultList.get("code").toString()));
                commonDes.setMessage(resultList.get("message").toString());
                return commonDes;
            }
        }
        Long endTime = System.currentTimeMillis();
        logger.info("batchUpdateIntellCorrect cost time:{} ms", (endTime - startTime));
        return commonDes;

    }

    /***
     * 一键使用智能批改
     *  只有按照题的一键智能批改
     *
     * @param request
     * @return
     * @throws BizLayerException
     */
    public CommonDes batchUpdateIntellCorrectToOwn(BatchIntellUpdateRequest request, boolean isMQ) throws BizLayerException {
        Long startTime = System.currentTimeMillis();
        doBatchIntellCorrectByQuestion(request, isMQ);
        Long endTime = System.currentTimeMillis();
        logger.info("batchUpdateIntellCorrect cost time:{} ms", (endTime - startTime));
        return new CommonDes();
    }


    /***
     * 数据流转     一键使用智能批改
     *  只有按照题的一键智能批改
     *
     * @param requestList
     * @return
     * @throws BizLayerException
     */
    @Override
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public CommonDes batchUpdateIntellCorrectList(List<BatchIntellUpdateRequest> requestList) throws BizLayerException {
        logger.info("batchUpdateIntellCorrectList 数据流转一键智能批改:{}", requestList);
        Long startTime = System.currentTimeMillis();
        int count = 0;
        if(CollectionUtils.isNotEmpty(requestList) && requestList.size() > 0) {
            for(BatchIntellUpdateRequest request : requestList) {
                delRedisKeyOfFlowTurnStu(request);
                request.setI(count++);
                //doBatchIntellCorrectByQuestion(request);
                doBatchIntellCorrectByQuestionList(request);
            }
        }
        Long endTime = System.currentTimeMillis();
        logger.info("batchUpdateIntellCorrectList cost time:{} ms", (endTime - startTime));
        return new CommonDes();
    }

    @Override
    public CommonResponse<List<StudentExercise>> findStudentAnswerList(StudentExercise exercise) throws
            BizLayerException {
        CommonResponse<List<StudentExercise>> response = new CommonResponse<>();

        //判断发布时间 上线时间   true : 调别人  false : 调自己
        boolean flag = AnswerCommonUtil.getQuestionPublicTime(exercise.getResourceId(), lessonService, redisUtilExtend,answerBranchParam.getPythonServerOnlineTime());
        Map<String,Object> param = new HashedMap<String,Object>();
        param.put("param",JsonUtil.obj2Json(exercise));
        param.put("methodType","findStudentAnswerList");

        //final String url = answerBranchParam.getAnswerBranchUrl() + StuAnswerUtil.ANSWER_OPERATE_SPLIT + AnswerOperateType.query + StuAnswerUtil.ANSWER_OPERATE_SPLIT + AnswerOperateType.query;
        final String url = answerBranchParam.getAnswerBranchUrl() + "/query/query";
        if(StuAnswerUtil.isWrongQuesChalRecorded(exercise.getExerciseSource())) {     //任务场景
            if(flag) {  //调别人
                //向洪清组发送数据
                String s = AnswerCommonUtil.doGet(param, url);
                Map<String, Object> resultList = JsonUtil.readValue(s, new
                        TypeReference<Map<String, Object>>() {
                        });
                //需要对返回值进行判断
                if(resultList != null && resultList.size() > 0 && "success".equals(resultList.get("message"))) {
                    List<StudentExercise> answerList = (List<StudentExercise>)resultList.get("data");
                    response.setData(answerList);
                    return response;
                }
            }
        }else {     //自主学习场景
            //向洪清组发送数据
            String s = AnswerCommonUtil.doGet(param, url);
            Map<String, Object> resultList = JsonUtil.readValue(s, new
                    TypeReference<Map<String, Object>>() {
                    });
            //需要对返回值进行判断
            if(resultList != null && resultList.size() > 0 && "success".equals(resultList.get("message"))) {
                List<StudentExercise> answerList = (List<StudentExercise>)resultList.get("studentExerciseList");
                response.setData(answerList);
                return response;
            }
        }

        //调自己
        response = findStudentAnswerListToOwn(exercise);
        return response;
    }


    public CommonResponse<List<StudentExercise>> findStudentAnswerListToOwn(StudentExercise exercise) throws
            BizLayerException {
        Long startTime = System.currentTimeMillis();
        StudentExercise se = new StudentExercise();
        se.setExerciseSource(exercise.getExerciseSource());
        se.setResourceId(exercise.getResourceId());
        List<StudentExercise> allSes = StuAnswerUtil.getFromCacheStuBatch(se, redisUtilExtend);
        if (CollectionUtils.isEmpty(allSes)) {
            Map<String, java.io.Serializable> paramMap;
            paramMap = new HashMap<>(1);
            StuAnswerUtil.setShardKey(exercise, redisUtilExtend, studentWorkDao, lessonService, trailCountService);
            paramMap.put("tableName", TableNameUtil.getStudentExercise(exercise));
            paramMap.put("resourceId", exercise.getResourceId());
            paramMap.put("exerciseSource", exercise.getExerciseSource());
            allSes = answerCorrectMapper.findStudentAnswerList(paramMap);
        }

        TKTand7x5Detail(allSes);

        CommonResponse<List<StudentExercise>> response = new CommonResponse<>();
        if (CollectionUtils.isNotEmpty(allSes)) {
            response.setData(allSes);
        }
        Long endTime = System.currentTimeMillis();
        logger.info("findStudentAnswerList cost time:{} ms", endTime - startTime);

        //向洪清组透传数据
        Map<String,Object> param = new HashedMap<String,Object>();
        param.put("param",JsonUtil.obj2Json(response));
        param.put("methodType","findStudentAnswerList");
        //final String url = answerBranchParam.getAnswerBranchUrl() + StuAnswerUtil.ANSWER_OPERATE_SPLIT + AnswerOperateType.query + StuAnswerUtil.ANSWER_OPERATE_SPLIT + AnswerOperateType.query_result;
        final String url = answerBranchParam.getAnswerBranchUrl() + "/query/query_result";
        AnswerCommonUtil.doGet(param, url);

        return response;
    }

    private void TKTand7x5Detail(List<StudentExercise> allSes) {
        if(CollectionUtils.isNotEmpty(allSes)) {
            for (StudentExercise se1 : allSes) {
                Double totalScore1 = se1.getTotalScore();
                if (totalScore1 != null) {
                    Double d = new Double(0);
                    if (se1.getStructId().equals(StuAnswerConstant.StructType.STRUCT_TKT) || se1.getStructId().equals(StuAnswerConstant.StructType.STRUCT_7x5)) {
                        Map<String, Map<String, Double>> scoreByStruct = StuAnswerUtil.getEveryItemScoreByStruct(se1.getStructId(), se1.getTotalScore(), se1.getResult());
                        if (scoreByStruct != null && !scoreByStruct.isEmpty() && scoreByStruct.size() > 0) {
                            if (scoreByStruct.containsKey("resultTKTMap") && scoreByStruct.get("resultTKTMap").size() > 0) {
                                Map<String, Double> resultTKTMap = scoreByStruct.get("resultTKTMap");
                                if (resultTKTMap != null && !resultTKTMap.isEmpty()) {
                                    Set<String> strings = resultTKTMap.keySet();
                                    for (String s : strings) {
                                        d += resultTKTMap.get(s);
                                    }
                                    se1.setScore(d);
                                    se1.setEveryTKTscore(resultTKTMap);
                                }
                            } else if (scoreByStruct.containsKey("result7x5Map") && scoreByStruct.get("result7x5Map").size() > 0) {
                                Map<String, Double> result7x5Map = scoreByStruct.get("result7x5Map");
                                if (result7x5Map != null && !result7x5Map.isEmpty()) {
                                    Set<String> strings = result7x5Map.keySet();
                                    for (String s : strings) {
                                        d += result7x5Map.get(s);
                                    }
                                    se1.setScore(d);
                                    se1.setEvery7x5score(result7x5Map);
                                }
                            }
                        }
                    } else if (se1.getStructId().equals(StuAnswerConstant.StructType.PARAGRAPH_READ) || se1.getStructId().equals(StuAnswerConstant.StructType.WORD_READ)) {
                        String audioResult = se1.getAudioResult();
                        logger.info("TKTand7x5Detail :{} + :{}", JsonUtil.obj2Json(se1), JsonUtil.obj2Json(audioResult));
                        if (audioResult != null && !"".equals(audioResult)) {
                            Map<String, Object> audioResultList = JsonUtil.readValue(audioResult, new
                                    TypeReference<Map<String, Object>>() {
                                    });
                            if (audioResultList != null && audioResultList.size() > 0) {
                                Double totalScore = se1.getTotalScore();
                                Object pScore = audioResultList.get("score");
                                Double percentScore = Double.parseDouble(pScore.toString());
                                BigDecimal b = new BigDecimal(100);
                                BigDecimal b1 = new BigDecimal(Double.toString(totalScore));
                                BigDecimal b2 = new BigDecimal(Double.toString(percentScore)).setScale(0, BigDecimal.ROUND_DOWN);
                                double score = b1.multiply(b2.divide(b)).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
                                se1.setScore(score);
                            }
                        }
                    }
                }
            }
        }
    }
    /**
     * 按题查询智能批改的数据
     *
     * @param request
     */
    private void doBatchIntellCorrectByQuestion(BatchIntellUpdateRequest request) {
        Long startTime = System.currentTimeMillis();
        Long questionId = request.getQuestionId();
        List<Long> quesIds = BatchUpdateCorrectUtil.getLeafQuesIds(questionId, redisUtilExtend, questionService);
        StudentExercise se = new StudentExercise();
        se.setExerciseSource(request.getExerciseSource());
        se.setResourceId(request.getResourceId());
        se.setQuestionIdList(quesIds);
        String redisKey = IntellCorrectByQuestion + request.getExerciseSource() + "_" + request.getResourceId() + "_"
                + questionId;
        boolean flag = redisUtilExtend.simpleLock(0, redisKey, 1);
        if (!flag) {
            throw new BizLayerException("", PraxisErrorCode.BATCH_INTELL_CORRECT_REPEAT_EXCEPTION);
        }
        List<StudentExercise> allSes = StuAnswerUtil.getFromCacheQeusesBatch(se, redisUtilExtend);
        boolean isCatchExit = false;
        if (CollectionUtils.isEmpty(allSes)) {
            allSes = stuExeService.getDbRecords(se);
        } else {
            isCatchExit = true;
        }
        List<StudentExercise> resultList = null;
        if (CollectionUtils.isNotEmpty(allSes)) {
            //resultList = StuAnswerUtil.getIntellCorrectInfo(allSes);
            resultList=BatchUpdateCorrectUtil.getBatchIntellCorectInfo(allSes);
        }
        batchIntellCorrectDataUpdate(resultList, isCatchExit, request, OperateType.BATCHUPDATEREDIS);
        Long endTime = System.currentTimeMillis();
        logger.info("batchUpdateIntellCorrect doBatchIntellCorrectByQuestion cost time:{} ms", (endTime - startTime));
    }

    /**
     * 按题查询智能批改的数据
     *
     * @param request
     */
    private void doBatchIntellCorrectByQuestion(BatchIntellUpdateRequest request, boolean isMQ) {
        Long startTime = System.currentTimeMillis();
        Long questionId = request.getQuestionId();
        List<Long> quesIds = BatchUpdateCorrectUtil.getLeafQuesIds(questionId, redisUtilExtend, questionService);
        StudentExercise se = new StudentExercise();
        se.setExerciseSource(request.getExerciseSource());
        se.setResourceId(request.getResourceId());
        se.setQuestionIdList(quesIds);
        String redisKey = IntellCorrectByQuestion + request.getExerciseSource() + "_" + request.getResourceId() + "_"
                + questionId;
        boolean flag = redisUtilExtend.simpleLock(0, redisKey, 1);
        if (!flag) {
            throw new BizLayerException("", PraxisErrorCode.BATCH_INTELL_CORRECT_REPEAT_EXCEPTION);
        }
        List<StudentExercise> allSes = StuAnswerUtil.getFromCacheQeusesBatch(se, redisUtilExtend);
        boolean isCatchExit = false;
        if (CollectionUtils.isEmpty(allSes)) {
            allSes = stuExeService.getDbRecords(se);
        } else {
            isCatchExit = true;
        }
        List<StudentExercise> resultList = null;
        if (CollectionUtils.isNotEmpty(allSes)) {
            //resultList = StuAnswerUtil.getIntellCorrectInfo(allSes);
            resultList=BatchUpdateCorrectUtil.getBatchIntellCorectInfo(allSes);
        }
        batchIntellCorrectDataUpdate(resultList, isCatchExit, request, OperateType.BATCHUPDATEREDIS, isMQ);
        Long endTime = System.currentTimeMillis();
        logger.info("batchUpdateIntellCorrect doBatchIntellCorrectByQuestion cost time:{} ms", (endTime - startTime));
    }
    /**
     * 数据流转
     * 按题查询智能批改的数据
     *
     * @param request
     */
    private void doBatchIntellCorrectByQuestionList(BatchIntellUpdateRequest request) {
        Long startTime = System.currentTimeMillis();
        Long questionId = request.getQuestionId();
        List<Long> quesIds = BatchUpdateCorrectUtil.getLeafQuesIds(questionId, redisUtilExtend, questionService);
        StudentExercise se = new StudentExercise();
        se.setExerciseSource(request.getExerciseSource());
        se.setResourceId(request.getResourceId());
        se.setQuestionIdList(quesIds);
        String redisKey = IntellCorrectByQuestion + request.getExerciseSource() + "_" + request.getResourceId() + "_"
                + questionId + "_" + request.getI();
        boolean flag = redisUtilExtend.simpleLock(0, redisKey, 1);
        if (!flag) {
            throw new BizLayerException("", PraxisErrorCode.BATCH_INTELL_CORRECT_REPEAT_EXCEPTION);
        }
        List<StudentExercise> allSes = StuAnswerUtil.getFromCacheQeusesBatch(se, redisUtilExtend);
        boolean isCatchExit = false;
        if (CollectionUtils.isEmpty(allSes)) {
            allSes = stuExeService.getDbRecords(se);
        } else {
            isCatchExit = true;
        }
        List<StudentExercise> resultList = null;
        if (CollectionUtils.isNotEmpty(allSes)) {
            //resultList = StuAnswerUtil.getIntellCorrectInfo(allSes);
            resultList=BatchUpdateCorrectUtil.getBatchIntellCorectInfo(allSes);
        }
        batchIntellCorrectDataUpdate(resultList, isCatchExit, request, OperateType.BATCHUPDATEREDIS);
        Long endTime = System.currentTimeMillis();
        logger.info("batchUpdateIntellCorrect doBatchIntellCorrectByQuestion cost time:{} ms", (endTime - startTime));
    }

    /**
     * 对智能批改结果进行筛选
     * 1、筛选含有智批标识的数据
     * 2、根据传递的学生IDS过滤数据
     * 3、
     *
     * @param allSes
     * @param isCatchExit
     * @param request
     * @param type
     */
    private void batchIntellCorrectDataUpdate(List<StudentExercise> allSes, boolean isCatchExit,
                                              BatchIntellUpdateRequest request, OperateType type, boolean isMQ) {
        Long startTime = System.currentTimeMillis();
        List<StudentExercise> filterDataList = BatchUpdateCorrectUtil.filterBatchUpdateIntellCorrectData(allSes,
                request);
        if (CollectionUtils.isNotEmpty(filterDataList)) {
            if (isCatchExit) {
                logger.info("batchIntellCorrectDataUpdate 按题的一键批改走缓存处理");

                type = OperateType.BATCHUPDATE;
                BatchUpdateCorrectUtil.sendDataToMq(filterDataList, batchUpdateCorrectTemplate, type,
                            "一键智能批改批改数据发送MQ失败," + "失败的数据:{},失败的原因:{}");
                List<List<StudentExercise>> dataList = BatchUpdateCorrectUtil.spliceArrays(filterDataList, 20);
                BatchUpdateCorrectUtil.updateCorrectRedisData(dataList, redisUtilExtend);

                //数据流转
                int i = pigaiCirculationMapper.updateFlowTurnCorrectStateList(filterDataList);
                logger.info("批改 - IP数据流转,更新状态:{},更新参数:{}",i,JsonUtil.obj2Json(filterDataList));
                /*delRedisKeyOfFlowTurnList(filterDataList, i);*/

            } else {
                BatchUpdateCorrectUtil.sendDataToMq(filterDataList, batchUpdateCorrectTemplate, type,
                        "一键智能批改批改数据发送MQ失败," + "失败的数据:{},失败的原因:{}");
                TableNameUtil.updateTableName(filterDataList);
                //直接更新做答记录的批改结果数据
                answerCorrectMapper.batchUpdateCorrect(filterDataList);

                //数据流转
                int i = pigaiCirculationMapper.updateFlowTurnCorrectStateList(filterDataList);
                logger.info("批改 - IP数据流转,更新状态:{},更新参数:{}",i,JsonUtil.obj2Json(filterDataList));
                /*delRedisKeyOfFlowTurnList(filterDataList, i);*/

            }
            if(isMQ) {
                //日志打点
                PraxisSsdbUtil.logDataToFile(filterDataList, OperateType.CORRECT);
                //通知统计服务
                BatchUpdateRequest batchUpdateRequest = new BatchUpdateRequest();
                BeanUtils.copyProperties(request, batchUpdateRequest);
                sendDataToTrailMq(batchUpdateRequest);
            }
        }
        Long endTime = System.currentTimeMillis();
        logger.info("batchUpdateIntellCorrect batchIntellCorrectDataUpdate cost time:{} ms", (endTime - startTime));
    }

    /**
     * 对智能批改结果进行筛选
     * 1、筛选含有智批标识的数据
     * 2、根据传递的学生IDS过滤数据
     * 3、
     *
     * @param allSes
     * @param isCatchExit
     * @param request
     * @param type
     */
    private void batchIntellCorrectDataUpdate(List<StudentExercise> allSes, boolean isCatchExit,
                                              BatchIntellUpdateRequest request, OperateType type) {
        Long startTime = System.currentTimeMillis();
        List<StudentExercise> filterDataList = BatchUpdateCorrectUtil.filterBatchUpdateIntellCorrectData(allSes,
                request);
        if (CollectionUtils.isNotEmpty(filterDataList)) {
            if (isCatchExit) {
                logger.info("batchIntellCorrectDataUpdate 按题的一键批改走缓存处理");
                type = OperateType.BATCHUPDATE;
                BatchUpdateCorrectUtil.sendDataToMq(filterDataList, batchUpdateCorrectTemplate, type,
                        "一键智能批改批改数据发送MQ失败," + "失败的数据:{},失败的原因:{}");
                List<List<StudentExercise>> dataList = BatchUpdateCorrectUtil.spliceArrays(filterDataList, 20);
                BatchUpdateCorrectUtil.updateCorrectRedisData(dataList, redisUtilExtend);

                //数据流转
                int i = pigaiCirculationMapper.updateFlowTurnCorrectStateList(filterDataList);
                logger.info("批改 - IP数据流转,更新状态:{},更新参数:{}",i,JsonUtil.obj2Json(filterDataList));
                /*delRedisKeyOfFlowTurnList(filterDataList, i);*/

            } else {
                BatchUpdateCorrectUtil.sendDataToMq(filterDataList, batchUpdateCorrectTemplate, type,
                        "一键智能批改批改数据发送MQ失败," + "失败的数据:{},失败的原因:{}");
                TableNameUtil.updateTableName(filterDataList);
                //直接更新做答记录的批改结果数据
                answerCorrectMapper.batchUpdateCorrect(filterDataList);

                //数据流转
                int i = pigaiCirculationMapper.updateFlowTurnCorrectStateList(filterDataList);
                logger.info("批改 - IP数据流转,更新状态:{},更新参数:{}",i,JsonUtil.obj2Json(filterDataList));
                /*delRedisKeyOfFlowTurnList(filterDataList, i);*/

            }
            //日志打点
            PraxisSsdbUtil.logDataToFile(filterDataList, OperateType.CORRECT);
            //通知统计服务
            BatchUpdateRequest batchUpdateRequest = new BatchUpdateRequest();
            BeanUtils.copyProperties(request, batchUpdateRequest);
            sendDataToTrailMq(batchUpdateRequest);
        }
        Long endTime = System.currentTimeMillis();
        logger.info("batchUpdateIntellCorrect batchIntellCorrectDataUpdate cost time:{} ms", (endTime - startTime));
    }

    private void delRedisKeyOfFlowTurnList(List<StudentExercise> seList, int i) {
        if(i > 0) {
            FlowTurnCorrectRequest request = new FlowTurnCorrectRequest();
            for(StudentExercise se : seList) {
                Long uploadId = pigaiCirculationMapper.findQuestionUploadId(se.getQuestionId());
                request.setSystemId(uploadId);
                String key = StuAnswerUtil.getRedisKeyOfFlowTurnList(request);
                Set<String> keys = redisUtil.keys(key);
                logger.info("批改 - IP数据流转,批改/一键批改/一键智能批改 删除redis key:{}",keys);
                if(CollectionUtils.isNotEmpty(keys) && keys.size() > 0) {
                    for (String s : keys) {
                        redisUtil.del(s);
                    }
                }
            }
        }
    }

    private void delRedisKeyOfFlowTurnStu(BatchUpdateRequest bRequest) {
        FlowTurn request = new FlowTurn();
        request.setSystemId(bRequest.getCorrectorId());
        request.setQuestionId(bRequest.getQuestionId());
        String key = StuAnswerUtil.getRedisKeyOfFindFlowTurnStu(request);
        if(key != null && !"".equals(key)) {
            redisUtil.del(key);
            logger.info("批改 - IP数据流转题详情,一键批改 删除redis key:{}成功",key);
        }else {
            logger.info("批改 - IP数据流转题详情,一键批改 删除redis key:{}失败",key);
        }
    }
    private void delRedisKeyOfFlowTurnStu(BatchIntellUpdateRequest bURequest) {
        FlowTurn request = new FlowTurn();
        request.setSystemId(bURequest.getCorrectorId());
        request.setQuestionId(bURequest.getQuestionId());
        String key = StuAnswerUtil.getRedisKeyOfFindFlowTurnStu(request);
        if(key != null && !"".equals(key)) {
            redisUtil.del(key);
            logger.info("批改 - IP数据流转题详情,一键智能批改 删除redis key:{}成功",key);
        }else {
            logger.info("批改 - IP数据流转题详情,一键智能批改 删除redis key:{}失败",key);
        }
    }
}
