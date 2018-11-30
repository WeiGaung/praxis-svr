package com.noriental.praxissvr.answer.service.impl;

import com.noriental.lessonsvr.rservice.LessonService;
import com.noriental.log.TraceKeyHolder;
import com.noriental.praxissvr.answer.bean.AnswerMqVo;
import com.noriental.praxissvr.answer.bean.OperateType;
import com.noriental.praxissvr.answer.bean.StudentExercise;
import com.noriental.praxissvr.answer.bean.UpdateSubmitAnswerVo;
import com.noriental.praxissvr.answer.dao.StudentExerciseDao;
import com.noriental.praxissvr.answer.mappers.PigaiCirculationMapper;
import com.noriental.praxissvr.answer.request.FlowTurnCorrectRequest;
import com.noriental.praxissvr.answer.service.StudentErrorExeService;
import com.noriental.praxissvr.answer.service.StudentExerciseService;
import com.noriental.praxissvr.answer.util.PraxisSsdbUtil;
import com.noriental.praxissvr.answer.util.StuAnswerUtil;
import com.noriental.praxissvr.brush.dao.StudentWorkDao;
import com.noriental.praxissvr.common.StuAnswerConstant;
import com.noriental.praxissvr.exception.PraxisErrorCode;
import com.noriental.praxissvr.question.bean.Question;
import com.noriental.praxissvr.questionSearch.service.QuestionSearchService;
import com.noriental.praxissvr.wrong.service.AnswerChalService;
import com.noriental.praxissvr.wrong.util.WrongUtil;
import com.noriental.praxissvr.wrong.vo.GetsonBatchIdParam;
import com.noriental.praxissvr.wrong.vo.GetsonBatchIdsParam;
import com.noriental.praxissvr.wrongQuestion.service.WrongQuestionService;
import com.noriental.trailsvr.service.TrailCountService;
import com.noriental.utils.json.JsonUtil;
import com.noriental.utils.redis.RedisUtil;
import com.noriental.validate.exception.BizLayerException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.*;
import java.util.Map.Entry;

import static com.noriental.praxissvr.exception.PraxisErrorCode.ANSWER_RECORD_NOT_FOUND;

/**
 * @author:kate
 */
@SuppressWarnings({"SpringJavaAutowiringInspection", "SpringAutowiredFieldsWarningInspection"})
@Service("interaction.studentExerciseService")
public class StudentExerciseServiceImpl implements StudentExerciseService {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @Autowired
    private StudentExerciseDao studentExerciseDao;
    @Autowired
    private StudentErrorExeService studentErrorExeService;
    @Autowired
    private StudentWorkDao studentWorkDao;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private AnswerChalService answerChalService;
    @Autowired
    private LessonService lessonService;
    @Autowired
    private TrailCountService trailCountService;
    @Autowired
    private WrongQuestionService wrongQuestionService;
    @Autowired
    private QuestionSearchService questionSearchService;
    @Autowired
    private PigaiCirculationMapper pigaiCirculationMapper;

    /***
     * 答题场景上课：优先通过缓存查询、缓存失效通过数据库查询 答题记录
     * 其他业务场景直接通过数据库查询
     * @param studentExercise
     * @return
     */
    @Override
    public StudentExercise getRecord(StudentExercise studentExercise) {
        StudentExercise oldObject = null;
        if (StuAnswerUtil.isExerciseSource(studentExercise)) {
            oldObject = redisUtil.viewGet(StuAnswerUtil.getRedisKeyOfStudentExercise(studentExercise),
                    StudentExercise.class);
        }
        if (oldObject == null) {
            StudentExercise se = new StudentExercise();
            BeanUtils.copyProperties(studentExercise, se);
            //上课、作业、测评 查询学生的学年和班级  自主练习和强化练习 查询学生的学年和学生ID
            StuAnswerUtil.setShardKey(se, redisUtil, studentWorkDao, lessonService, trailCountService);
            //如果答题场景为消灭错题，需要将错题消灭表的主键ID作为题集发布ID
            List<StudentExercise> studentExercises = studentExerciseDao.findByStudentExercise(se);
            if (CollectionUtils.isEmpty(studentExercises)) {
                throw new BizLayerException("答题记录数:" + (studentExercises == null ? 0 : studentExercises.size()) +
                        "，", ANSWER_RECORD_NOT_FOUND);
            } else {
                oldObject = studentExercises.get(0);
            }
        }
        return oldObject;
    }

    @Override
    public void updateRecord(StudentExercise studentExercise, OperateType operateType) {
        StuAnswerUtil.setShardKey(studentExercise, redisUtil, studentWorkDao, lessonService, trailCountService);
        //如果答题场景为上课 1、缓存答题记录数据   2、异步发送消息队列持久化答题记录数据
        if (StuAnswerUtil.isExerciseSource(studentExercise)) {
            toCache(studentExercise, operateType);
        } else {
            persistantData(studentExercise, operateType);
        }
    }

    @Override
    public void updateRecord(StudentExercise studentExercise, OperateType operateType,boolean isMQ) {
        StuAnswerUtil.setShardKey(studentExercise, redisUtil, studentWorkDao, lessonService, trailCountService);
        //如果答题场景为上课 1、缓存答题记录数据   2、异步发送消息队列持久化答题记录数据
        if (StuAnswerUtil.isExerciseSource(studentExercise)) {
            toCache(studentExercise, operateType,isMQ);
        } else {
            persistantData(studentExercise, operateType, isMQ);
        }
    }


    public void persistantData(StudentExercise studentExercise, OperateType operateType) {
        try {
            //更新学生作答记录 主要更新批改时间、批改角色
            studentExerciseDao.update(studentExercise);

            //数据流转
            StuAnswerUtil.updateFlowTurnCorrectState(studentExercise);
            int i = pigaiCirculationMapper.updateFlowTurnCorrectState(studentExercise);
            logger.info("批改 - IP数据流转,更新状态:{},更新参数:{}",i,JsonUtil.obj2Json(studentExercise));
            /*delRedisKeyOfFlowTurnList(studentExercise, i);*/

            //将教师批改的结果分成两部分：1、批改为错的  2、批改为对的   老师批注不更新错题本数据
            studentErrorExeService.updateOne(studentExercise);
            PraxisSsdbUtil.logDataToFile(Collections.singletonList(studentExercise), operateType);
        } catch (Exception e) {
            logger.error("做答|批改数据:{}mysql持久化更新失败,失败的原因是:", JsonUtil.obj2Json(studentExercise), e);
            throw new BizLayerException("", PraxisErrorCode.MYSQL_DATA_PERSISTANT);
        }
    }


    public void persistantData(StudentExercise studentExercise, OperateType operateType, boolean isMQ) {
        try {
            //更新学生作答记录 主要更新批改时间、批改角色
            studentExerciseDao.update(studentExercise);

            //数据流转
            StuAnswerUtil.updateFlowTurnCorrectState(studentExercise);
            int i = pigaiCirculationMapper.updateFlowTurnCorrectState(studentExercise);
            logger.info("批改 - IP数据流转,更新状态:{},更新参数:{}", i, JsonUtil.obj2Json(studentExercise));
            /*delRedisKeyOfFlowTurnList(studentExercise, i);*/
            if(isMQ) {
                //将教师批改的结果分成两部分：1、批改为错的  2、批改为对的   老师批注不更新错题本数据
                studentErrorExeService.updateOne(studentExercise);
                PraxisSsdbUtil.logDataToFile(Collections.singletonList(studentExercise), operateType);
            }
        } catch (Exception e) {
            logger.error("做答|批改数据:{}mysql持久化更新失败,失败的原因是:", JsonUtil.obj2Json(studentExercise), e);
            throw new BizLayerException("", PraxisErrorCode.MYSQL_DATA_PERSISTANT);
        }
    }

    @Override
    public List<StudentExercise> getDbRecords(StudentExercise se) {
        StudentExercise entity = new StudentExercise();
        BeanUtils.copyProperties(se, entity);
        StuAnswerUtil.setShardKey(entity, redisUtil, studentWorkDao, lessonService, trailCountService);
        if (CollectionUtils.isNotEmpty(entity.getQuestionIdList())) {
            entity.setQuestionId(null);
        }
        return studentExerciseDao.findByStudentExercise(entity);
    }

    @Override
    public List<StudentExercise> getMysqlRecords(StudentExercise studentExercise) {
        StudentExercise se = new StudentExercise();
        BeanUtils.copyProperties(studentExercise, se);
        //设置分表的年和班级
        StuAnswerUtil.setShardKey(se, redisUtil, studentWorkDao, lessonService, trailCountService);
        return wrongQuestionService.findStudentAnswer(se);
    }

    @Override
    public void createRecords(final List<StudentExercise> seList, OperateType operateType, StudentExercise
            historyData,boolean isMQ) {
        setShardKey(seList);
        //上课、预习、作业、测评做答数据都先写入缓存、再持久化到mysql库
        if (StuAnswerUtil.isExerciseSource(seList.get(0))) {
            long difHours = StuAnswerUtil.getTimeDiff(historyData != null ? historyData.getSubmitTime() : null, new
                    Date());
            final Map<String, Object> keyOneMap = new HashMap<>(seList.size());
            final Map<String, List<String>> keySetMap = new HashMap<>(seList.size());
            if (difHours < StuAnswerUtil.HOURS_INTERVAL) {
                //缓存没有失效，直接加入缓存，如果缓存已失效，则不走缓存
                if (seList.size() > 0) {
                    toQueues(seList, operateType, "学生做答数据发送MQ失败,失败的数据:{},失败的原因:{}");
                    try {
                        for (StudentExercise se2 : seList) {
                            //答题记录添加redis缓存
                            addCacheData(se2, keyOneMap, keySetMap);
                        }
                        long timeFlag = StuAnswerUtil.getSecondsDiff(historyData != null ? historyData.getSubmitTime
                                () : null, new Date());
                        //过期时间
                        int difSeconds = Integer.parseInt(String.valueOf(timeFlag));
                        //保存做答数据到redis缓存
                        redisUtil.viewmSet(keyOneMap, difSeconds);
                        for (Entry<String, List<String>> entry : keySetMap.entrySet()) {
                            String[] arr = new String[entry.getValue().size()];
                            entry.getValue().toArray(arr);
                            redisUtil.sadd(entry.getKey(), arr);
                            StuAnswerUtil.setTtl(redisUtil, entry.getKey(), difSeconds);
                            logger.info("redis key:{},value:{}", entry.getKey(), JsonUtil.obj2Json(arr));
                        }
                        logger.info("difSeconds:{}", difSeconds);
                    } catch (Exception e) {
                        logger.error("学生做答数据缓存操作失败,失败的原因是:", e);
                        throw new BizLayerException("", PraxisErrorCode.PRAXIS_SUBMIT_FAIL);
                    }
                }
            } else {
                logger.info("updateSubmitAnswer学生作答缓存失效,直接走数据库持久化");
                //redis容错处理
               // StuAnswerUtil.delRedisKey(redisUtil, seList.get(0));
                persistData(seList, operateType,isMQ);
            }
        } else {
            persistData(seList, operateType,isMQ);
        }
    }

    @Override
    public void sendAudioCalbackMq(StudentExercise studentExercise, OperateType operateType) {
        StuAnswerUtil.setShardKey(studentExercise, redisUtil, studentWorkDao, lessonService, trailCountService);
        //听口题错题挑战场景resourceId需要特殊处理
        toQueue(studentExercise, operateType, "听口学生做答音频地址切换七牛地址失败,失败的数据:{},失败的原因:{}");
    }

    @Override
    public void createRecords(List<StudentExercise> seList) {
        setShardKey(seList);
        List<Long> questionIds = getQuestionIds(seList);
        StudentExercise se = new StudentExercise();
        BeanUtils.copyProperties(seList.get(0), se);
        se.setQuestionIdList(questionIds);
        studentExerciseDao.deleteErrorQuestionChallenge(se);
        studentExerciseDao.creates(seList);
    }

    private List<Long> getQuestionIds(List<StudentExercise> notExistChalSesInit) {
        List<Long> ids = new ArrayList<>();
        for (StudentExercise se : notExistChalSesInit) {
            ids.add(se.getQuestionId());
        }
        return ids;
    }


    private void persistData(final List<StudentExercise> seList, OperateType operateType, boolean isMQ) {
        try {
            submitPersistent(seList, operateType, isMQ);
        } catch (Exception e) {
            logger.error("学生做答数据:{}持久化数据库失败,失败的原因是:{}", JsonUtil.obj2Json(seList), e);
            throw new BizLayerException("", PraxisErrorCode.PRAXIS_SUBMIT_FAIL);
        }
    }

    private void submitPersistent(final List<StudentExercise> seList, OperateType operateType, boolean isMQ) {
        //入库答题记录和错记录
        studentExerciseDao.creates(seList);
         if(isMQ) {
             studentErrorExeService.creates(seList);
             PraxisSsdbUtil.logDataToFile(seList, operateType);
        }
    }


    @Override
    public List<StudentExercise> getFromCacheStuQueses(StudentExercise se) {
        List<StudentExercise> resultList = new ArrayList<>();
        if (StuAnswerUtil.isExerciseSource(se)) {
            List<Long> questionIdList = se.getQuestionIdList();
            if (CollectionUtils.isNotEmpty(questionIdList)) {
                Set<String> keys = new HashSet<>();
                for (Long quesId : questionIdList) {
                    se.setQuestionId(quesId);
                    String redisKey = StuAnswerUtil.getRedisKeyOfStudentExercise(se);
                    keys.add(redisKey);
                }
                Set resultSet = redisUtil.viewGetSet(keys, StudentExercise.class);
                if (CollectionUtils.isNotEmpty(resultSet)) {
                    resultList.addAll(new ArrayList<>(resultSet));
                }
            }
        }
        return resultList;
    }

    @Override
    public void updateBatch(StudentExercise se) {
        try {
            StuAnswerUtil.setShardKey(se, redisUtil, studentWorkDao, lessonService, trailCountService);
            studentExerciseDao.updateBatch(se);
            studentErrorExeService.updateBatch(se);
            List<StudentExercise> seList = convertSeList(se, se.getUpdateSubmitAnswerVoList());
            PraxisSsdbUtil.logDataToFile(seList, OperateType.CORRECT);
        } catch (Exception e) {
            logger.error("学生做答的数据批量持久化数据库失败，失败的原因是:", e);
            throw new BizLayerException("", PraxisErrorCode.PRAXIS_SUBMIT_FAIL);
        }

    }

    @Override
    public void updateBatch(StudentExercise se, boolean isMQ) {
        try {
            StuAnswerUtil.setShardKey(se, redisUtil, studentWorkDao, lessonService, trailCountService);
            studentExerciseDao.updateBatch(se);
            studentErrorExeService.updateBatch(se);
            List<StudentExercise> seList = convertSeList(se, se.getUpdateSubmitAnswerVoList());
            if(isMQ) {
                PraxisSsdbUtil.logDataToFile(seList, OperateType.CORRECT);
            }
        } catch (Exception e) {
            logger.error("学生做答的数据批量持久化数据库失败，失败的原因是:", e);
            throw new BizLayerException("", PraxisErrorCode.PRAXIS_SUBMIT_FAIL);
        }

    }

    @Override
    public void updateIntellInfo(List<StudentExercise> seList) {
        try {
            //更新答题记录表
            wrongQuestionService.updateIntellInfo(seList);
            for(StudentExercise studentExercise:seList){
                //错题记录表的更新不能批量更新
                studentErrorExeService.updateOne(studentExercise);
            }
            //自动生效日志打印
            PraxisSsdbUtil.logDataToFile(seList, OperateType.CORRECT);
        } catch (Exception e) {
            logger.error("自主练习智能批改mysql持久化失败,失败的原因是:", e);
            throw new BizLayerException("", PraxisErrorCode.INTELL_CORRECT_EXCEPTION);
        }

    }

    @Override
    public void updateIntellInfo(List<StudentExercise> seList, boolean isMQ) {
        try {
            //更新答题记录表
            wrongQuestionService.updateIntellInfo(seList);
            for(StudentExercise studentExercise:seList){
                //错题记录表的更新不能批量更新
                studentErrorExeService.updateOne(studentExercise);
            }
            if(isMQ) {
                //自动生效日志打印
                PraxisSsdbUtil.logDataToFile(seList, OperateType.CORRECT);
            }
        } catch (Exception e) {
            logger.error("自主练习智能批改mysql持久化失败,失败的原因是:", e);
            throw new BizLayerException("", PraxisErrorCode.INTELL_CORRECT_EXCEPTION);
        }

    }

    @Override
    public void getTableRoute(List<StudentExercise> seList) {
        if (CollectionUtils.isNotEmpty(seList)) {
            StudentExercise exercise = seList.get(0);
            StuAnswerUtil.setShardKey(exercise, redisUtil, studentWorkDao, lessonService, trailCountService);
            for (StudentExercise se : seList) {
                se.setYear(exercise.getYear());
                se.setClassId(exercise.getClassId());
            }
        }

    }

    @Override
    public void deleteErrorQuestionChallenge(StudentExercise se) {
        studentExerciseDao.deleteErrorQuestionChallenge(se);
    }

    @Override
    public List<StudentExercise> getHistoryDbRecords(StudentExercise studentExercise) {
        StudentExercise entity = new StudentExercise();
        BeanUtils.copyProperties(studentExercise, entity);
        //设置分表的年和班级
        StuAnswerUtil.setShardKey(entity, redisUtil, studentWorkDao, lessonService, trailCountService);
        if (CollectionUtils.isNotEmpty(entity.getQuestionIdList())) {
            entity.setQuestionId(null);
        }
        return studentExerciseDao.findHistoryData(entity);

    }

    @Override
    public void mqIntellCorrectInfo(List<StudentExercise> studentExerciseList) {
        // toIntellQueues(studentExerciseList);
        toQueues(studentExerciseList, OperateType.INTELL, "智能批改数据发送MQ失败,失败的数据:{},失败的原因:{}");
    }

    @Override
    public void mqAutoIntellCorrectInfo(List<StudentExercise> studentExerciseList) {
        // toIntellQueues(studentExerciseList);
        toQueues(studentExerciseList, OperateType.INTELLCORRECT, "智能批改数据发送MQ失败,失败的数据:{},失败的原因:{}");
    }


    private List<StudentExercise> convertSeList(StudentExercise se, List<UpdateSubmitAnswerVo>
            updateSubmitAnswerVoList) {
        List<StudentExercise> seList = new ArrayList<>();
        for (UpdateSubmitAnswerVo vo : updateSubmitAnswerVoList) {
            StudentExercise se1 = new StudentExercise();
            BeanUtils.copyProperties(se, se1);
            se1.setQuestionId(vo.getQuestionId());
            se1.setResult(vo.getResult());
            se1.setCorrectorRole(vo.getCorrectorRole());
            se1.setSubmitAnswer(vo.getSubmitAnswer());
            seList.add(se1);
        }
        return seList;
    }


    private void initParentQuestionId(StudentExercise se) {
        String exerciseSource = se.getExerciseSource();
        if (WrongUtil.isChal(exerciseSource)) {
            Long questionId = se.getQuestionId();
            Long parentQuesId = StuAnswerUtil.getParentQuesId(questionSearchService, questionId);
            se.setParentQuestionId(parentQuesId);
        }
    }

    private List<Long> getQuesIdsExceptLeaf(List<Long> quesIds) {
        List<Question> questions = StuAnswerUtil.getQuesListByIds(questionSearchService, quesIds);
        return StuAnswerUtil.getQuesIdListExceptLeaf(questions);
    }

    private Long convertResourceIdList(StudentExercise se) {
        Long resourceId = se.getResourceId();
        if (WrongUtil.isChal(se.getExerciseSource())) {
            checkChalStudentId(se);
            List<Long> quesIds = checkAndGetQuesIds(se);
            List<Long> quesIdsExceptLeaf = getQuesIdsExceptLeaf(quesIds);

            if (CollectionUtils.isEmpty(quesIdsExceptLeaf)) {
                GetsonBatchIdsParam param = new GetsonBatchIdsParam(se.getStudentId(), se.getRedoSource(), se
                        .getResourceId());
                List<Long> sonBatchIds = answerChalService.getSonBatchIds(param);
                if (CollectionUtils.isEmpty(sonBatchIds)) {
                    //标记没有找到
                    return -1L;
                }
                se.setResourceIdList(sonBatchIds);
                se.setResourceId(null);
            } else if (quesIdsExceptLeaf.size() == 1) {
                se.setResourceId(getSonBatchId(se.getStudentId(), quesIdsExceptLeaf.get(0), se.getRedoSource(), se
                        .getResourceId()));
            } else {
                throw new BizLayerException("", PraxisErrorCode.ANSWER_CHAL_QUES_THAN_ONE);
            }
        }
        return resourceId;
    }

    private Long getSonBatchId(Long studentId, Long questionId, String exerciseSource, Long resourceId) {
        GetsonBatchIdParam param = new GetsonBatchIdParam(studentId, questionId, exerciseSource, resourceId);
        return answerChalService.getSonBatchId(param);
    }

    private void checkChalStudentId(StudentExercise se) {
        if (se.getStudentId() == null) {
            throw new BizLayerException("", PraxisErrorCode.ANSWER_CHAL_FIND_STU_SHORT);
        }
    }

    private List<Long> checkAndGetQuesIds(StudentExercise se) {
        List<Long> quesIds = new ArrayList<>();
        int hasValue = 0;
        if (se.getQuestionId() != null) {
            quesIds.add(se.getQuestionId());
            hasValue++;
        }
        if (se.getParentQuestionId() != null) {
            quesIds.add(se.getParentQuestionId());
            hasValue++;
        }
        if (se.getQuestionIdList() != null) {
            quesIds.addAll(se.getQuestionIdList());
            hasValue++;
        }
        if (hasValue > 1) {
            throw new BizLayerException("", PraxisErrorCode.ANSWER_CHAL_FIND_QUES_THAN_ONE);
        }
        return quesIds;
    }


    /***
     * 将数据写入缓存，如果当前接口的数据时间和持久化库里面存在的时间间隔小于12小时，说明缓存没有失效，则更新缓存
     *  如果前接口的数据时间和持久化库里面存在的时间间隔大于于12小时，不走缓存，直接持久化到库
     * @param studentExercise
     * @param operateType
     * @return
     */
    private void toCache(StudentExercise studentExercise, OperateType operateType) {
        Long timeFlag = StuAnswerUtil.getDetailRedisKeyTtl(redisUtil, studentExercise);
        //缓存没有失效，直接加入缓存，如果缓存已失效，则不走缓存
        if (null != timeFlag) {
            toQueue(studentExercise, operateType, "老师批改或批注数据发送MQ失败,失败的数据:{},失败的原因:{}");
            try {
                //通过做答缓存查询做答记录
                String redisKey = StuAnswerUtil.getRedisKeyOfStudentExercise(studentExercise);
                StudentExercise currentData = redisUtil.viewGet(redisKey, StudentExercise.class);
                //如果是批改 更新做答数据和批注数据
                if (operateType.equals(OperateType.CORRECT)) {
                    studentExercise.setPostilTeacher(currentData.getPostilTeacher());
                    studentExercise.setSubmitAnswer(currentData.getSubmitAnswer());
                    studentExercise.setIsNew(currentData.getIsNew());
                }
                //如果是批注 更新做答信息和批改信息
                if (operateType.equals(OperateType.POSTIL)) {
                    studentExercise.setSubmitAnswer(currentData.getSubmitAnswer());
                    studentExercise.setResult(currentData.getResult());
                }
                //获取redis  key
                String studentExerciseRedisKey = StuAnswerUtil.getRedisKeyOfStudentExercise(studentExercise);
                // 更新数据以及更新数据过期时间
                redisUtil.viewSet(studentExerciseRedisKey, studentExercise, timeFlag.intValue());
                Map<String, List<String>> keySetMap = new HashMap<>(3);
                addToParentKey(studentExercise, StuAnswerConstant.FIELD.QUESTION_ID, keySetMap);
                addToParentKey(studentExercise, StuAnswerConstant.FIELD.STUDENET_ID, keySetMap);
                addToParentKey(studentExercise, StuAnswerConstant.FIELD.STU_QUES_ID, keySetMap);
                //只有一个题 直接 写入缓存
                for (Entry<String, List<String>> entry : keySetMap.entrySet()) {
                    String[] arr = new String[entry.getValue().size()];
                    entry.getValue().toArray(arr);
                    redisUtil.sadd(entry.getKey(), arr);
                    StuAnswerUtil.setTtl(redisUtil, entry.getKey(), timeFlag.intValue());
                }

                //数据流转
                StuAnswerUtil.updateFlowTurnCorrectState(studentExercise);
                int i = pigaiCirculationMapper.updateFlowTurnCorrectState(studentExercise);
                logger.info("批改 - IP数据流转,更新状态:{},更新参数:{}",i,JsonUtil.obj2Json(studentExercise));
                /*delRedisKeyOfFlowTurnList(studentExercise, i);*/

                logger.info("updateCorrect or updatePostil toCache timeFlag:{},studentExerciseRedisKey:{}," +
                        "keySetMap:{}", timeFlag, studentExerciseRedisKey, JsonUtil.obj2Json(keySetMap));
            } catch (Exception e) {
                logger.error("批注|批改缓存操作失败，失败的原因:", e);
                throw new BizLayerException("", PraxisErrorCode.CORRECT_EXCEPTION);
            }

        } else {//不走缓存，直接持久化到库
            logger.info("toCache 缓存失效,老师批改不走缓存，直接持久化到库");
            persistantData(studentExercise, operateType);
        }

    }


    /***
     * 将数据写入缓存，如果当前接口的数据时间和持久化库里面存在的时间间隔小于12小时，说明缓存没有失效，则更新缓存
     *  如果前接口的数据时间和持久化库里面存在的时间间隔大于于12小时，不走缓存，直接持久化到库
     * @param studentExercise
     * @param operateType
     * @return
     */
    private void toCache(StudentExercise studentExercise, OperateType operateType,boolean isMQ) {
        Long timeFlag = StuAnswerUtil.getDetailRedisKeyTtl(redisUtil, studentExercise);
        //缓存没有失效，直接加入缓存，如果缓存已失效，则不走缓存
        if (null != timeFlag) {
            if(isMQ) {
                toQueue(studentExercise, operateType, "老师批改或批注数据发送MQ失败,失败的数据:{},失败的原因:{}");
            }
            try {
                //通过做答缓存查询做答记录
                String redisKey = StuAnswerUtil.getRedisKeyOfStudentExercise(studentExercise);
                StudentExercise currentData = redisUtil.viewGet(redisKey, StudentExercise.class);
                //如果是批改 更新做答数据和批注数据
                if (operateType.equals(OperateType.CORRECT)) {
                    studentExercise.setPostilTeacher(currentData.getPostilTeacher());
                    studentExercise.setSubmitAnswer(currentData.getSubmitAnswer());
                    studentExercise.setIsNew(currentData.getIsNew());
                }
                //如果是批注 更新做答信息和批改信息
                if (operateType.equals(OperateType.POSTIL)) {
                    studentExercise.setSubmitAnswer(currentData.getSubmitAnswer());
                    studentExercise.setResult(currentData.getResult());
                }
                //获取redis  key
                String studentExerciseRedisKey = StuAnswerUtil.getRedisKeyOfStudentExercise(studentExercise);
                // 更新数据以及更新数据过期时间
                redisUtil.viewSet(studentExerciseRedisKey, studentExercise, timeFlag.intValue());
                Map<String, List<String>> keySetMap = new HashMap<>(3);
                addToParentKey(studentExercise, StuAnswerConstant.FIELD.QUESTION_ID, keySetMap);
                addToParentKey(studentExercise, StuAnswerConstant.FIELD.STUDENET_ID, keySetMap);
                addToParentKey(studentExercise, StuAnswerConstant.FIELD.STU_QUES_ID, keySetMap);
                //只有一个题 直接 写入缓存
                for (Entry<String, List<String>> entry : keySetMap.entrySet()) {
                    String[] arr = new String[entry.getValue().size()];
                    entry.getValue().toArray(arr);
                    redisUtil.sadd(entry.getKey(), arr);
                    StuAnswerUtil.setTtl(redisUtil, entry.getKey(), timeFlag.intValue());
                }

                //数据流转
                StuAnswerUtil.updateFlowTurnCorrectState(studentExercise);
                int i = pigaiCirculationMapper.updateFlowTurnCorrectState(studentExercise);
                logger.info("批改 - IP数据流转,更新状态:{},更新参数:{}",i,JsonUtil.obj2Json(studentExercise));
                /*delRedisKeyOfFlowTurnList(studentExercise, i);*/

                logger.info("updateCorrect or updatePostil toCache timeFlag:{},studentExerciseRedisKey:{}," +
                        "keySetMap:{}", timeFlag, studentExerciseRedisKey, JsonUtil.obj2Json(keySetMap));
            } catch (Exception e) {
                logger.error("批注|批改缓存操作失败，失败的原因:", e);
                throw new BizLayerException("", PraxisErrorCode.CORRECT_EXCEPTION);
            }

        } else {//不走缓存，直接持久化到库
            logger.info("toCache 缓存失效,老师批改不走缓存，直接持久化到库");
            persistantData(studentExercise, operateType,isMQ);
        }

    }


    /***
     *                   单条答题记录key：    stuexe_${source}_${resourceId}_${studentId}_${questionId}
     *教师一次发布的一个题目所有答题记录key：   stuexe_${source}_${resourceId}_*_${questionId}
     *教师一次发布的一个学生所有答题记录key：   stuexe_${source}_${resourceId}_${studentId}_*
     * @param studentExercise
     * @param keyOneMap
     * @param keySetMap
     */
    private void addCacheData(StudentExercise studentExercise, Map<String, Object> keyOneMap, Map<String,
            List<String>> keySetMap) {

        String studentExerciseRedisKey = StuAnswerUtil.getRedisKeyOfStudentExercise(studentExercise);
        studentExercise.setIsNew(1);
        keyOneMap.put(studentExerciseRedisKey, studentExercise);
        //QUESTION_ID
        addToParentKey(studentExercise, StuAnswerConstant.FIELD.QUESTION_ID, keySetMap);
        //STUDENET_ID
        addToParentKey(studentExercise, StuAnswerConstant.FIELD.STUDENET_ID, keySetMap);
        //STU_QUES_ID
        addToParentKey(studentExercise, StuAnswerConstant.FIELD.STU_QUES_ID, keySetMap);
    }

    /***
     * 老师批改
     * @param studentExercise
     * @param operateType
     */
    private void toQueue(StudentExercise studentExercise, OperateType operateType, String message) {
        AnswerMqVo in = new AnswerMqVo();
        try {
            in.setStudentExercise(studentExercise);
            in.setRequestId(TraceKeyHolder.getTraceKey());
            in.setOperateType(operateType);
            rabbitTemplate.convertAndSend(in);
            logger.info("老师、学生批改、批注、听口音频地址回写的MQ消息数据:"+JsonUtil.obj2Json(in));
        } catch (Exception e) {
            //"老师批改或批注数据发送MQ失败,失败的数据:{},失败的原因:{}"
            logger.error(message, JsonUtil.obj2Json(in), e);
            throw new BizLayerException("", PraxisErrorCode.CORRECT_EXCEPTION);
        }
    }

    /***
     *  //学生做答通知 进行处理
     * @param studentExerciseList
     */
    private void toQueues(List<StudentExercise> studentExerciseList, OperateType operateType, String message) {
        AnswerMqVo in = new AnswerMqVo();
        try {
            in.setStudentExerciseList(studentExerciseList);
            in.setOperateType(operateType);
            in.setRequestId(TraceKeyHolder.getTraceKey());
            logger.info("智能批改自动生效获取的入参 studentExerciseList:" + JsonUtil.obj2Json(studentExerciseList));
            rabbitTemplate.convertAndSend(in);
            logger.info("学生做答MQ消息数据:"+JsonUtil.obj2Json(in));
        } catch (Exception e) {
            logger.error(message, JsonUtil.obj2Json(in), e);
            throw new BizLayerException("", PraxisErrorCode.PRAXIS_SUBMIT_FAIL);
        }
    }

    /***
     *  //学生做答通知 进行处理
     * @param studentExerciseList
     */
    private void toQueues(List<StudentExercise> studentExerciseList, OperateType operateType, String message,boolean isMQ) {
        AnswerMqVo in = new AnswerMqVo();
        try {
            in.setStudentExerciseList(studentExerciseList);
            in.setOperateType(operateType);
            in.setRequestId(TraceKeyHolder.getTraceKey());
            logger.info("智能批改自动生效获取的入参 studentExerciseList:" + JsonUtil.obj2Json(studentExerciseList));
            rabbitTemplate.convertAndSend(in);
            logger.info("学生做答MQ消息数据:"+JsonUtil.obj2Json(in));
        } catch (Exception e) {
            logger.error(message, JsonUtil.obj2Json(in), e);
            throw new BizLayerException("", PraxisErrorCode.PRAXIS_SUBMIT_FAIL);
        }
    }


    /***
     *  设置三个纬度的key对应的value list
     * @param se
     * @param removedfield 去掉questionID或者StudentID
     * @param keySetMap 按题目或者学生的纬度获取的答题记录key的集合
     */
    private void addToParentKey(StudentExercise se, String removedfield, Map<String, List<String>> keySetMap) {
        String keyChild = StuAnswerUtil.getRedisKeyOfStudentExercise(se);
        Long studentId = se.getStudentId();
        Long questionId = se.getQuestionId();
        if (StringUtils.equals(removedfield, StuAnswerConstant.FIELD.QUESTION_ID)) {
            se.setQuestionId(null);
        } else if (StringUtils.equals(removedfield, StuAnswerConstant.FIELD.STUDENET_ID)) {
            se.setStudentId(null);
        } else {
            se.setQuestionId(null);
            se.setStudentId(null);
        }
        String keyParent = StuAnswerUtil.getRedisKeyOfStudentExercise(se);
        List<String> valueList = keySetMap.get(keyParent);
        if (valueList == null) {
            valueList = new ArrayList<>();
            keySetMap.put(keyParent, valueList);
        }
        valueList.add(keyChild);
        se.setStudentId(studentId);
        se.setQuestionId(questionId);
    }


    private void setShardKey(List<StudentExercise> seList) {
        StudentExercise se = seList.get(0);
        StuAnswerUtil.setShardKey(se, redisUtil, studentWorkDao, lessonService, trailCountService);
        for (StudentExercise se1 : seList) {
            se1.setClassId(se.getClassId());
            se1.setYear(se.getYear());
        }
    }

    private void delRedisKeyOfFlowTurnList(StudentExercise studentExercise, int i) {
        if(i > 0) {
            Long uploadId = pigaiCirculationMapper.findQuestionUploadId(studentExercise.getQuestionId());
            FlowTurnCorrectRequest request = new FlowTurnCorrectRequest();
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
