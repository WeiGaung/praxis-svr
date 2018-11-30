package com.noriental.praxissvr.answer.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.noriental.lessonsvr.entity.request.LinkResourceWithPublishInfo;
import com.noriental.lessonsvr.entity.request.ResourceParam;
import com.noriental.lessonsvr.rservice.LessonService;
import com.noriental.log.TraceKeyHolder;
import com.noriental.praxissvr.answer.bean.*;
import com.noriental.praxissvr.answer.mappers.PigaiCirculationMapper;
import com.noriental.praxissvr.answer.request.*;
import com.noriental.praxissvr.answer.response.*;
import com.noriental.praxissvr.answer.service.AnswerCommonService;
import com.noriental.praxissvr.answer.service.StudentExerciseService;
import com.noriental.praxissvr.answer.util.*;
import com.noriental.praxissvr.brush.service.StuBrushService;
import com.noriental.praxissvr.common.StuAnswerConstant;
import com.noriental.praxissvr.exception.PraxisErrorCode;
import com.noriental.praxissvr.question.bean.EntityQuestion;
import com.noriental.praxissvr.question.bean.Question;
import com.noriental.praxissvr.question.mapper.EntityQuestionMapper;
import com.noriental.praxissvr.question.service.QuestionService;
import com.noriental.praxissvr.questionSearch.service.QuestionSearchService;
import com.noriental.praxissvr.wrongQuestion.util.TableNameUtil;
import com.noriental.resourcesvr.common.request.IdRequest;
import com.noriental.resourcesvr.customlist.service.CustomListService;
import com.noriental.resourcesvr.customlist.vo.CustomListVo;
import com.noriental.usersvr.bean.request.UserBaseRequest;
import com.noriental.usersvr.bean.user.domain.Teacher;
import com.noriental.usersvr.service.okuser.UserService;
import com.noriental.utils.json.JsonUtil;
import com.noriental.utils.redis.RedisUtil;
import com.noriental.validate.bean.BaseRequest;
import com.noriental.validate.bean.CommonDes;
import com.noriental.validate.bean.CommonResponse;
import com.noriental.validate.bean.LongRequest;
import com.noriental.validate.exception.BizLayerException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;
import static com.noriental.praxissvr.answer.util.BatchUpdateCorrectUtil.correctBusDeal;
import static com.noriental.praxissvr.exception.PraxisErrorCode.*;

/***
 * @author :kate
 */
@SuppressWarnings("SpringJavaAutowiringInspection")
@Service(value = "answerCommonService")
public class AnswerCommonServiceImpl implements AnswerCommonService {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @Resource
    private StudentExerciseService stuExeService;
    @Resource
    private StuBrushService stuBrushService;
    @Resource
    private QuestionService questionService;
    @Resource
    private QuestionSearchService questionSearchService;
    @Resource
    private RedisUtilExtend redisUtilExtend;
    @Resource
    private ApplicationContext applicationContext;
    @Autowired
    private LessonService lessonService;
    @Autowired
    public AnswerBranchParam answerBranchParam;
    public static final String INTELL_WHITE = "OKAY_INTELL_WHITE";
    @Resource
    private PigaiCirculationMapper pigaiCirculationMapper;
    @Resource
    private CustomListService customListService;
    @Resource
    private EntityQuestionMapper entityQuestionMapper;
    @Resource
    private UserService userService;
    @Autowired
    private RedisUtil redisUtil;
    ThreadPoolExecutor threadPool = new ThreadPoolExecutor(50, 100, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(1000));
    //添加白名单
    public @PostConstruct
    void  init(){
        logger.info("========智能批改自动生效白名单添加========");
        List<Object> okay_intell_white = redisUtilExtend.getListAll("OKAY_INTELL_WHITE");
        logger.info("智能批改自动生效白名单 :{}", JsonUtil.obj2Json(okay_intell_white));
        List<String> strings = pigaiCirculationMapper.selectWhiteSchool();

        if(CollectionUtils.isNotEmpty(strings) && strings.size() > 0) {
            if(CollectionUtils.isNotEmpty(okay_intell_white) && okay_intell_white.size() > 0) {
                for (String s : strings) {
                    if(!okay_intell_white.contains(s)) {
                        redisUtilExtend.addToListLeft(INTELL_WHITE,s);
                    }
                }
            }else {
                for (String s : strings) {
                    redisUtilExtend.addToListLeft(INTELL_WHITE,s);
                }
            }
        }
        logger.info("========智能批改自动生效白名单添加成功========");
    }

    //学生做答
    @Override
    public CommonDes updateSubmitAnswer(UpdateSubmitAnswerRequest in) throws BizLayerException {
        CommonDes commonDes = new CommonDes();
        //判断发布时间 上线时间   true : 调别人  false : 调自己
        if(StuAnswerUtil.isWrongQuesChalRecorded(in.getExerciseSource())) {     //任务场景
            boolean flag = AnswerCommonUtil.getQuestionPublicTime(in.getResourceId(), lessonService, redisUtilExtend,answerBranchParam.getPythonServerOnlineTime());
            if (!flag) {  //调自己
                commonDes = updateSubmitAnswerToOwn(in, true);
                return commonDes;
            }
        }
        //任务场景 自主学习场景
        in.setReqId(TraceKeyHolder.getTraceKey());
        String url = answerBranchParam.getAnswerBranchUrl() + "/correct/submit_answer";
        //调用python组数据
        String s = AnswerCommonUtil.doPost(JsonUtil.obj2Json(in), url);
        Map<String, Object> resultList = JsonUtil.readValue(s, new TypeReference<Map<String, Object>>() {
        });
        //需要对返回值进行判断
        if(resultList != null && resultList.size() >= 0 && "success".equals(resultList.get("message"))) {
            //返回成功
            final UpdateSubmitAnswerRequest target = new UpdateSubmitAnswerRequest();
            BeanUtils.copyProperties(in,target);
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    updateSubmitAnswerToOwn(target, false);
                }
            });
        }else {     //返回空或失败
            if(resultList == null || resultList.size() == 0) {
                throw new BizLayerException("", INNER_ERROR);
            }else if(resultList.containsKey("code") && resultList.containsKey("message")) {
                commonDes.setCode(Integer.parseInt(resultList.get("code").toString()));
                commonDes.setMessage(resultList.get("message").toString());
                return commonDes;
            }
            throw new BizLayerException("", INNER_ERROR);
        }
        return commonDes;
    }

    private CommonDes updateSubmitAnswerToOwn(UpdateSubmitAnswerRequest in,boolean isMQ){
        long startTime = System.currentTimeMillis();
        List<UpdateSubmitAnswerVo> updateSubmitAnswerVoList;
        StudentExercise se = new StudentExercise();
        String exerciseSource = in.getExerciseSource();
        //获取作答结果list
        List<UpdateSubmitAnswer> updateSubmitAnswerList = in.getUpdateSubmitAnswerList();
        //验证作答记录是否可以提交
        List<StudentExercise> currentData = isExerciseAnswerSubmit(se, in);
        //将习题作答结果数据转换成UpdateSubmitAnswerVo 数组
        updateSubmitAnswerVoList = AnswerCommonUtil.convertUpdateSubmitAnswerVo(updateSubmitAnswerList);
        //查询习题结构和parentId
        AnswerCommonUtil.setQuesProperty(updateSubmitAnswerVoList, questionSearchService);
        //验证答案结构是否合法
        StuAnswerUtil.checkSubmitResult(updateSubmitAnswerVoList);
        submitDataPersistent(se, exerciseSource, in, currentData, updateSubmitAnswerVoList, isMQ);
        List<StudentExercise> seList = AnswerCommonUtil.covertSeList(se, updateSubmitAnswerVoList);

        if(isMQ) {
            //通过事件监听完成后置业务的处理
            String traceKey = TraceKeyHolder.getTraceKey();
            AnswerSubmitEntity event = new AnswerSubmitEntity(traceKey, seList, OperateType.ANSWER);
            applicationContext.publishEvent(new AnswerSubmitEvent(event));
            PraxisAnswerLogUtil.logDataToFile(seList, in.getUniqueKey());
        }

        long endTime = System.currentTimeMillis();
        logger.info("updateSubmitAnswer cost time:{} ms", endTime - startTime);
        return new CommonDes();
    }

    /***
     * 学生做答数据任务场景和自主学习场景的数据存储
     * @param se
     * @param exerciseSource
     * @param in
     * @param currentData
     * @param updateSubmitAnswerVoList
     */
    private void submitDataPersistent(StudentExercise se, String exerciseSource, UpdateSubmitAnswerRequest in,
                                      List<StudentExercise> currentData, List<UpdateSubmitAnswerVo>
                                              updateSubmitAnswerVoList, boolean isMQ) {
        //上课 作业 测评
        if (StuAnswerUtil.AnswerType.INSERT == StuAnswerUtil.getAnswerType(exerciseSource)) {
            List<StudentExercise> createList = StuAnswerUtil.initSeList(se, updateSubmitAnswerVoList, in
                    .getCorrectorRole(), in.getCorrectorId());
            stuExeService.createRecords(createList, OperateType.ANSWER, CollectionUtils.isNotEmpty(currentData) ?
                    currentData.get(0) : null,isMQ);
        } else if (StuAnswerUtil.AnswerType.UPDATE == StuAnswerUtil.getAnswerType(exerciseSource)) {
            ////强化练习、消灭错题、自主练习
            //设置提交时间和最后更新时间
            Date now = new Date();
            se.setSubmitTime(now);
            se.setLastUpdateTime(now);
           // se.setCreateTime(now);
            for (UpdateSubmitAnswerVo entity : updateSubmitAnswerVoList) {
                if (AnswerCommonUtil.isAllCorrect(entity.getResult(), entity.getStructId())) {
                    entity.setFlag(3);
                }
            }
            se.setUpdateSubmitAnswerVoList(updateSubmitAnswerVoList);
            StuAnswerUtil.setCorrectInfo(updateSubmitAnswerVoList, in.getCorrectorRole(), in.getCorrectorId());
            //这行代码在setLogger这之前，取readlocal里的数据，并清空  //这行代码在updateBatch这之前，设置treadlocal里的数据。
            stuBrushService.setLogger(in.getExerciseSource(), in.getResourceId());
            //stuExeService.updateBatch(se);
            stuExeService.updateBatch(se,isMQ);
            //数据流转
            StuAnswerUtil.updateFlowTurnCorrectState(se);
            int i = pigaiCirculationMapper.updateFlowTurnCorrectState(se);
            logger.info("批改 - IP数据流转,更新状态:{},更新参数:{}",i,JsonUtil.obj2Json(se));
            /*delRedisKeyOfFlowTurnList(se, i);*/
            //（自主练习和强化练习）更新刷题记录表表状态
            stuBrushService.updateWorkStatus(in.getExerciseSource(), in.getResourceId());
        }
    }

    /***
     * 验证作答记录是否可以提交
     * @param se
     * @param in
     * @throws BizLayerException
     */
    private List<StudentExercise> isExerciseAnswerSubmit(StudentExercise se, UpdateSubmitAnswerRequest in) {
        //获取作答list
        List<UpdateSubmitAnswer> updateSubmitAnswerList = in.getUpdateSubmitAnswerList();
        List<Long> questionIdList = AnswerCommonUtil.getQuestionIdList(updateSubmitAnswerList);
        se.setStudentId(in.getStudentId());
        se.setExerciseSource(in.getExerciseSource());
        se.setResourceId(in.getResourceId());
        //错题消灭来源
        se.setRedoSource(in.getRedoSource());
        //设置同一答题场景下的子场景
        se.setSubExerciseSource(in.getSubExerciseSource());
        se.setQuestionIdList(questionIdList);
        StudentExercise currentSe = new StudentExercise();
        BeanUtils.copyProperties(se, currentSe);
        //获取已经做答记录，先查询缓存如果没有数据在走库查询
        List<StudentExercise> seList = stuExeService.getFromCacheStuQueses(currentSe);
        if (CollectionUtils.isEmpty(seList)) {
            seList = stuExeService.getDbRecords(currentSe);
        }
        //判断 答案是否存在 和是否可以 提交
        StuAnswerUtil.checkExist(in.getExerciseSource(), seList);
        StuAnswerUtil.checkSubmit(seList);
        if (StuAnswerUtil.isExerciseSource(currentSe)) {
            //任务场景取同一题集发布id(resourceId)中的第一条数据(正序)
            return stuExeService.getMysqlRecords(currentSe);
        }
        return null;
    }

    @Override
    public UpdatePostilResponse updatePostil(UpdatePostilRequest in) throws BizLayerException {
        UpdatePostilResponse response = new UpdatePostilResponse();
        //判断发布时间 上线时间   true : 调别人  false : 调自己
        boolean flag = AnswerCommonUtil.getQuestionPublicTime(in.getResourceId(), lessonService, redisUtilExtend,answerBranchParam.getPythonServerOnlineTime());
         //任务场景
        if (!flag) {  //调自己
            response = updatePostilToOwn(in, true);
            return response;
        }
        //任务场景 向洪清组发送数据
        in.setReqId(TraceKeyHolder.getTraceKey());
        String param = JsonUtil.obj2Json(in);
        String url = answerBranchParam.getAnswerBranchUrl() + "/correct/postil/update";
        String s = AnswerCommonUtil.doPost(param, url);
        Map<String, Object> resultList = JsonUtil.readValue(s, new TypeReference<Map<String, Object>>() {
        });
        //需要对返回值进行判断
        if(resultList != null && resultList.size() >= 0 && "success".equals(resultList.get("message"))) {
            //返回成功
            final UpdatePostilRequest target = new UpdatePostilRequest();
            BeanUtils.copyProperties(in,target);
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    updatePostilToOwn(target, false);
                }
            });
        }else {     //返回空或失败
            if(resultList == null || resultList.size() == 0) {
                throw new BizLayerException("", INNER_ERROR);
            }else if(resultList.containsKey("code") && resultList.containsKey("message")) {
                response.setCode(Integer.parseInt(resultList.get("code").toString()));
                response.setMessage(resultList.get("message").toString());
                return response;
            }
            throw new BizLayerException("", INNER_ERROR);
        }
        return response;
    }


    public UpdatePostilResponse updatePostilToOwn(UpdatePostilRequest in, boolean isMQ) throws BizLayerException {
        long startTime = System.currentTimeMillis();
        StudentExercise se = new StudentExercise();
        BeanUtils.copyProperties(in, se);
        //获取习题结构和复合题大题ID
        getStructParentQuestionId(se);
        String key = lockCorrectRecord(in.getExerciseSource(), in.getResourceId(), in.getStudentId(), se
                .getParentQuestionId() != null ? se.getParentQuestionId() : in.getQuestionId());
        //查询学生批改结果、批改角色
        StudentExercise existedRecord = getCorrectInfo(se);
        try {
            BeanUtils.copyProperties(existedRecord, se);
            se.setPostilTeacher(in.getPostilTeacher());
            StuAnswerUtil.checkPostilLegal(existedRecord.getStructId(), se.getPostilTeacher());
            StuAnswerUtil.checkPostilCount(existedRecord.getStructId(), existedRecord.getSubmitAnswer(), se
                    .getPostilTeacher());
            //如果存在批注信息，合并批注信息
            StuAnswerUtil.joinPostil(se, existedRecord.getPostilTeacher());
            //查询老师是否批注过
            String postilContent = existedRecord.getPostilTeacher();
            if (StringUtils.isBlank(postilContent)) {
                se.setIsNew(1);
            }
            Date date = new Date();
            se.setPostilTeacherDate(date);
            se.setLastUpdateTime(date);
            stuExeService.updateRecord(se, OperateType.POSTIL,isMQ);
            UpdatePostilResponse response = new UpdatePostilResponse();
            List<String> postilList = JsonUtil.readValue(se.getPostilTeacher(), List.class);
            response.setCompletedPostils(postilList);
            long endTime = System.currentTimeMillis();
            logger.info("updatePostil cost time:{} ms", (endTime - startTime));
            return response;
        } finally {
            unLockCorrectRecord(key);
        }
    }

    /***
     * 老师批改可以重复批改
     * 1、去掉老师批改后不能批改的校验
     * 2、更新错题记录，重复批改：已做错批改为对，要更新批改标识字段
     *  已批改为对的再次批改为错，直接更新不带状态
     * @param in
     * @return
     * @throws BizLayerException
     */
    @Override
    public CommonDes updateCorrect(UpdateCorrectRequest in) throws BizLayerException {
        CommonDes commonDes = new CommonDes();
        //判断发布时间 上线时间   true : 调别人  false : 调自己
        boolean flag = AnswerCommonUtil.getQuestionPublicTime(in.getResourceId(), lessonService, redisUtilExtend,answerBranchParam.getPythonServerOnlineTime());
        if(StuAnswerUtil.isWrongQuesChalRecorded(in.getExerciseSource())) {     //任务场景
            if (!flag) {  //调自己
                commonDes = updateCorrectToOwn(in, true);
                return commonDes;
            }
        }
        //任务场景 向洪清组发送数据
        in.setReqId(TraceKeyHolder.getTraceKey());
        String url = answerBranchParam.getAnswerBranchUrl() + "/correct/manual";
        String s = AnswerCommonUtil.doPost(JsonUtil.obj2Json(in), url);
        Map<String, Object> resultList = JsonUtil.readValue(s, new TypeReference<Map<String, Object>>() {
        });
        //需要对返回值进行判断
        if(resultList != null && resultList.size() >= 0 && "success".equals(resultList.get("message"))) {
            //返回成功
            final UpdateCorrectRequest target = new UpdateCorrectRequest();
            BeanUtils.copyProperties(in,target);
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    updateCorrectToOwn(target, false);
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
            throw new BizLayerException("", INNER_ERROR);
        }
        return commonDes;
    }

    /***
     * 老师批改可以重复批改
     * 1、去掉老师批改后不能批改的校验
     * 2、更新错题记录，重复批改：已做错批改为对，要更新批改标识字段
     *  已批改为对的再次批改为错，直接更新不带状态
     *
     * @param
     * @return
     * @throws BizLayerException
     */
    public CommonDes updateCorrectToOwn(UpdateCorrectRequest in,boolean isMQ) throws BizLayerException {
        delRedisKeyOfFlowTurnStu(in);
        long startTime = System.currentTimeMillis();
        StuAnswerUtil.checkRoleLegal(in.getCorrectorRole());
        StudentExercise se = new StudentExercise();
        BeanUtils.copyProperties(in, se);
        //获取学生答题记录 先从缓存获取，如果缓存没有再走库查询
        StudentExercise existedRecord;
        existedRecord = StuAnswerUtil.getDataFromRedis(se, redisUtilExtend);
        if (null == existedRecord) {
            existedRecord = stuExeService.getRecord(se);
        }
        //加锁原因，线上发现的两个bug：1、对于同一个答题记录上课批改、批注同时触发，会导致答题记录缓存的数据不一致
        // 2、同时批改同一个大题下的两个小题，创建错题记录时会造成数据不一致，所以对大题id加锁
        String key = lockCorrectRecord(in.getExerciseSource(), in.getResourceId(), in.getStudentId(), existedRecord
                .getParentQuestionId() != null ? existedRecord.getParentQuestionId() : in.getQuestionId());
        try {
            //批改业务处理
            correctBusComponent(existedRecord, se, in);
            //批改更新
            //stuExeService.updateRecord(se, OperateType.CORRECT);
            stuExeService.updateRecord(se, OperateType.CORRECT,isMQ);
            se.setCurrentResult(in.getResult());
            //后置业务处理改为监听机制
            if(isMQ) {
                String traceKey = TraceKeyHolder.getTraceKey();
                CorrectAnswerEvent event = new CorrectAnswerEvent(new CorrectAnswerEntity(se, existedRecord, OperateType
                        .CORRECT, traceKey));
                applicationContext.publishEvent(event);
            }
        } finally {
            unLockCorrectRecord(key);
        }
        long endTime = System.currentTimeMillis();
        logger.info("updateCorrect cost time:{} ms", (endTime - startTime));
        return new CommonDes();
    }

    /**
     * 批改业务处理
     * @param existedRecord
     * @param se
     * @param in
     */
    private void correctBusComponent(StudentExercise existedRecord, StudentExercise se, UpdateCorrectRequest in) {
        BeanUtils.copyProperties(existedRecord, se);
        se.setResult(in.getResult());
        se.setCorrectorId(in.getCorrectorId());
        se.setCorrectorRole(in.getCorrectorRole());
        se.setRedoSource(in.getRedoSource());
        se.setCorrectorTime(new Date());
        se.setLastUpdateTime(new Date());
        se.setTotalScore(in.getTotalScore());
        se.setScore(StuAnswerUtil.getScoreByStruct(existedRecord.getStructId(), in.getTotalScore(), in.getResult()));
        //验证批改状态数据合法性
        StuAnswerUtil.checkCorrectResult(existedRecord.getStructId(), se.getResult());
        //验证学生批改过不能再批改
        StuAnswerUtil.checkCorrectRole(se, existedRecord);
        //防止并发情况下填空题批改状态被覆盖
        if (StuAnswerConstant.StructType.STRUCT_TKT.equals(existedRecord.getStructId())) {
            existedRecord = getCorrectInfo(se);
        }
        //增加或者合并批改后的角色
        StuAnswerUtil.joinBlankResultAndRole(existedRecord, se);
        //顺序1 treadlocal设置日志
        stuBrushService.setLogger(se.getExerciseSource(), se.getResourceId());
        //判断单题是否批改完成
        boolean isAllCorect = AnswerCommonUtil.isAllCorrect(se.getResult(), se.getStructId());
        if (isAllCorect) {
            se.setFlag(3);
        }

    }

    //查询指定学生指定题目的答题记录
    @Override
    public StudentExerciseListOut findStuQuesAnswOnBatch(FindStuQuesAnswOnBatchRequest in) throws BizLayerException {
        in.setReqId(TraceKeyHolder.getTraceKey());
        //判断发布时间 上线时间   true : 调别人  false : 调自己
        boolean flag = true;
        boolean isExerciseSource;   //true  任务场景    false 自主学习场景
        if(StuAnswerUtil.isWrongQuesChalRecorded(in.getExerciseSource())) {     //任务场景
            flag = AnswerCommonUtil.getQuestionPublicTime(in.getResourceId(), lessonService, redisUtilExtend,answerBranchParam.getPythonServerOnlineTime());
            isExerciseSource = true;
        }else {     //自主学习场景
            isExerciseSource = false;
        }
        return getStudentExerciseListOut(flag, "findStuQuesAnswOnBatch",isExerciseSource,in);
    }

    public StudentExerciseListOut findStuQuesAnswOnBatchToOwn(FindStuQuesAnswOnBatchRequest in) throws BizLayerException {
        List<Long> quesIds = BatchUpdateCorrectUtil.getLeafQuesIds(in.getQuestionId(),redisUtilExtend,questionService);
        StudentExercise se = new StudentExercise();
        se.setExerciseSource(in.getExerciseSource());
        se.setResourceId(in.getResourceId());
        se.setStudentId(in.getStudentId());
        se.setQuestionIdList(quesIds);
        //先通过缓存查询数据，如果数据为空，则通过数据库查询
        List<StudentExercise> allSes = stuExeService.getFromCacheStuQueses(se);
        if (CollectionUtils.isEmpty(allSes)) {
            allSes = stuExeService.getDbRecords(se);
        }
        List<StudentExercise> resultList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(allSes)) {
            resultList = StuAnswerUtil.getIntellCorrectInfo(allSes);
        }
        StudentExerciseListOut out = new StudentExerciseListOut();
        out.setStudentExerciseList(resultList);
        this.setQuesType(allSes);
        return out;
    }

    //查询英语作文题指定题的部分学生的答题记录
    @Override
    public StudentExerciseListOut findStuListQuesAnsw(FindSomeStuQuesAnswOnBatchRequest in) throws BizLayerException {
        in.setReqId(TraceKeyHolder.getTraceKey());
        String url = answerBranchParam.getAnswerBranchUrl() + "/query/query";
        Map<String,Object> param = new HashedMap<String,Object>();
        param.put("param",JsonUtil.obj2Json(in));
        param.put("methodType", "findStuListQuesAnsw");
        String s = AnswerCommonUtil.doGet(param, url);
        return JsonUtil.readValue(s, StudentExerciseListOut.class);
    }

    //查询指定学生所有的答题记录
    @Override
    public StudentExerciseListOut findStuAnswsOnBatch(FindStuAnswsOnBatchRequest in) throws BizLayerException {
        in.setReqId(TraceKeyHolder.getTraceKey());
        //判断发布时间 上线时间   true : 调别人  false : 调自己
        boolean flag = true;
        boolean isExerciseSource;   //true  任务场景    false 自主学习场景
        if(StuAnswerUtil.isWrongQuesChalRecorded(in.getExerciseSource())) {     //任务场景
            flag = AnswerCommonUtil.getQuestionPublicTime(in.getResourceId(), lessonService, redisUtilExtend,answerBranchParam.getPythonServerOnlineTime());
            isExerciseSource = true;
        }else {     //自主学习场景
            isExerciseSource = false;
        }
        return getStudentExerciseListOut(flag, "findStuAnswsOnBatch",isExerciseSource,in);
    }

    //查询指定学生所有的答题记录
    public StudentExerciseListOut findStuAnswsOnBatchToOwn(FindStuAnswsOnBatchRequest in) throws BizLayerException {
        long startTime = System.currentTimeMillis();
        //校验 "消灭错题"中的错题消灭来源不能为空
        StuAnswerUtil.checkRedoSource(in.getExerciseSource(), in.getRedoSource());
        StudentExercise se = new StudentExercise();
        se.setExerciseSource(in.getExerciseSource());
        se.setResourceId(in.getResourceId());
        se.setStudentId(in.getStudentId());
        se.setStructIdList(in.getStructIdList());
        se.setRedoSource(in.getRedoSource());
        //先通过缓存查询数据，如果数据为空，则通过数据库查询
        long redStartTime = System.currentTimeMillis();
        List<StudentExercise> allSes = StuAnswerUtil.getFromCacheStuBatch(se, redisUtilExtend);
        logger.info("findStuAnswsOnBatch redis search cost:{} ms", System.currentTimeMillis() - redStartTime);
        long busStartTime = System.currentTimeMillis();
        if (CollectionUtils.isEmpty(allSes)) {
            long startMysqlTime = System.currentTimeMillis();
            allSes = stuExeService.getDbRecords(se);
            logger.info("findStuAnswsOnBatch mysql search cost:{} ms", System.currentTimeMillis() - startMysqlTime);
        }
        TKTand7x5Detail(allSes);
        //handleScore(allSes);
        //子题答题记录有顺序
        if (in.isSubQuesSort()) {
            StuAnswerUtil.sortByParentSubQues(allSes, questionService);
        } /*else {
            StuAnswerUtil.sortList(in.getExerciseSource(), allSes);
        }*/
        List<StudentExercise> resultList = new ArrayList<>();
        if (!in.isForPad()) {   //true不实用智能批改查询 false使用智能批改查询
            //根据学生ID、习题ID、答题场景、题集ID查询智能批改信息
            if (CollectionUtils.isNotEmpty(allSes)) {
                resultList = StuAnswerUtil.getIntellCorrectInfo(allSes);    //智能批改查询
            }
        } else {
            resultList = allSes;
        }
        StudentExerciseListOut out = new StudentExerciseListOut();
        out.setStudentExerciseList(resultList);
        this.setQuesType(resultList);
        logger.info("findStuAnswsOnBatch business  cost:{} ms", System.currentTimeMillis() - busStartTime);
        long endTime = System.currentTimeMillis();
        logger.info("findStuAnswsOnBatch cost time:{} ms", endTime - startTime);
        return out;
    }

    private void TKTand7x5Detail(List<StudentExercise> allSes) {
        if(CollectionUtils.isNotEmpty(allSes)) {
            for (StudentExercise se1 : allSes) {
                Double totalScore1 = se1.getTotalScore();
                if(totalScore1 != null) {
                    Double d = new Double(0);
                    if(se1.getStructId().equals(StuAnswerConstant.StructType.STRUCT_TKT) || se1.getStructId().equals(StuAnswerConstant.StructType.STRUCT_7x5)) {
                        Map<String, Map<String, Double>> scoreByStruct = StuAnswerUtil.getEveryItemScoreByStruct(se1.getStructId(), se1.getTotalScore(), se1.getResult());
                        if(scoreByStruct != null && !scoreByStruct.isEmpty() && scoreByStruct.size() > 0) {
                            if(scoreByStruct.containsKey("resultTKTMap") && scoreByStruct.get("resultTKTMap").size() > 0){
                                Map<String, Double> resultTKTMap = scoreByStruct.get("resultTKTMap");
                                if(resultTKTMap != null && !resultTKTMap.isEmpty()) {
                                    Set<String> strings = resultTKTMap.keySet();
                                    for (String s : strings) {
                                        d  += resultTKTMap.get(s);
                                    }
                                    se1.setScore(d);
                                    se1.setEveryTKTscore(resultTKTMap);
                                }
                            }else if(scoreByStruct.containsKey("result7x5Map") && scoreByStruct.get("result7x5Map").size() > 0) {
                                Map<String, Double> result7x5Map = scoreByStruct.get("result7x5Map");
                                if(result7x5Map != null && !result7x5Map.isEmpty()) {
                                    Set<String> strings = result7x5Map.keySet();
                                    for (String s : strings) {
                                        d  += result7x5Map.get(s);
                                    }
                                    se1.setScore(d);
                                    se1.setEvery7x5score(result7x5Map);
                                }
                            }
                        }
                    }else if(se1.getStructId().equals(StuAnswerConstant.StructType.PARAGRAPH_READ) || se1.getStructId().equals(StuAnswerConstant.StructType.WORD_READ) ) {
                        String audioResult = se1.getAudioResult();
                        logger.info("TKTand7x5Detail :{} + :{}", JsonUtil.obj2Json(se1),JsonUtil.obj2Json(audioResult));
                        if(audioResult != null && !"".equals(audioResult)) {
                            Map<String, Object> audioResultList = JsonUtil.readValue(audioResult, new
                                    TypeReference<Map<String,Object>>() {
                                    });
                            if(audioResultList != null && audioResultList.size() > 0) {
                                Double totalScore = se1.getTotalScore();
                                Object pScore = audioResultList.get("score");
                                Double percentScore = Double.parseDouble(pScore.toString());
                                BigDecimal b = new BigDecimal(100);
                                BigDecimal b1 = new BigDecimal(Double.toString(totalScore));
                                BigDecimal b2 = new BigDecimal(Double.toString(percentScore)).setScale(0,BigDecimal.ROUND_DOWN);
                                double score = b1.multiply(b2.divide(b)).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
                                se1.setScore(score);
                            }
                        }
                    }
            }
         }
        }
    }

    //指定题目所有学生的答题记录
    @Override
    public StudentExerciseListOut findQuesAnswsOnBatch(FindQuesAnswsOnBatchRequest in) throws BizLayerException {
        in.setReqId(TraceKeyHolder.getTraceKey());
        //判断发布时间 上线时间   true : 调别人  false : 调自己
        boolean flag = true;
        boolean isExerciseSource;   //true  任务场景    false 自主学习场景
        if(StuAnswerUtil.isWrongQuesChalRecorded(in.getExerciseSource())) {     //任务场景
            flag = AnswerCommonUtil.getQuestionPublicTime(in.getResourceId(), lessonService, redisUtilExtend,answerBranchParam.getPythonServerOnlineTime());
            isExerciseSource = true;
        }else {     //自主学习场景
            isExerciseSource = false;
        }
        return getStudentExerciseListOut(flag, "findQuesAnswsOnBatch",isExerciseSource,in);
    }

    //指定题目所有学生的答题记录
    public StudentExerciseListOut findQuesAnswsOnBatchToOwn(FindQuesAnswsOnBatchRequest in) throws BizLayerException {
        long startTime = System.currentTimeMillis();
        List<Long> quesIds = BatchUpdateCorrectUtil.getLeafQuesIds(in.getQuestionId(),redisUtilExtend,questionService);
        StudentExercise se = new StudentExercise();
        se.setExerciseSource(in.getExerciseSource());
        se.setResourceId(in.getResourceId());
        se.setQuestionIdList(quesIds);
        se.setStructIdList(in.getStructIdList());
        long startRedisTime = System.currentTimeMillis();
        List<StudentExercise> allSes = StuAnswerUtil.getFromCacheQeusesBatch(se, redisUtilExtend);
        logger.info("findQuesAnswsOnBatch red search cost time:{} ms", System.currentTimeMillis() - startRedisTime);
        if (CollectionUtils.isEmpty(allSes)) {
            long startMysqlTime = System.currentTimeMillis();
            allSes = stuExeService.getDbRecords(se);
            logger.info("findQuesAnswsOnBatch mysql search cost time:{} ms", System.currentTimeMillis() -
                    startMysqlTime);
        }
        TKTand7x5Detail(allSes);
        long startBusTime = System.currentTimeMillis();
        List<StudentExercise> resultList = new ArrayList<>();
        if (in.isForPad()) {    //true不实用智能批改查询 false使用智能批改查询
            resultList = allSes;
        } else {
            //根据学生ID、习题ID、答题场景、题集ID查询智能批改信息
            if (CollectionUtils.isNotEmpty(allSes)) {
                resultList = StuAnswerUtil.getIntellCorrectInfo(allSes);    //智能批改查询
            }
        }
        logger.debug("查询指定题目的所有答题记录包含智能批改结果数据:" + JsonUtil.obj2Json(resultList));
        if (FindQuesAnswsOnBatchRequest.SortType.TIME == in.getSortType()) {
            StuAnswerUtil.sortListByTime(resultList);
        } else if (FindQuesAnswsOnBatchRequest.SortType.SUB_QUES_ID == in.getSortType() && !quesIds.contains(in
                .getQuestionId())) {//有子题
            StuAnswerUtil.sortListQuesId(quesIds, resultList);
        }
        StudentExerciseListOut out = new StudentExerciseListOut();
        out.setStudentExerciseList(resultList);
        this.setQuesType(resultList);
        logger.info("findQuesAnswsOnBatch bus cost time:{} ms", System.currentTimeMillis() - startBusTime);
        long endTime = System.currentTimeMillis();
        logger.info("findQuesAnswsOnBatch  cost time:{} ms", endTime - startTime);
        return out;
    }
    /****
     * // 1、自主学习智批结果自动生效
     * 2、任务场景智批结果需要老师确认
     * 3. 任务场景 不在白名单
     * @param in
     * @return
     * @throws BizLayerException
     */
    private void intelligenceN_WhiteCorrectAnswer(IntelligenceAnswerRequest in) throws BizLayerException {
        logger.info("智能批改任务场景不在白名单需要老师确认获取的入参:" + JsonUtil.obj2Json(in));
        List<StudentExercise> dataList = new ArrayList<>();
        List<IntelligenceAnswer> intellInfoList = in.getIntelligenceAnswerList();
        for (IntelligenceAnswer intelligenceAnswer : intellInfoList) {
            StudentExercise se = new StudentExercise();
            BeanUtils.copyProperties(in, se);
            se.setQuestionId(intelligenceAnswer.getQuestionId());
            StudentExercise result = getCorrectInfo(se);
            if (null == result) {
                throw new BizLayerException("", PraxisErrorCode.ANSWER_RECORD_NOT_FOUND);
            }
            BeanUtils.copyProperties(result, se);
            se.setCorrectorRole(intelligenceAnswer.getCorrectorRole());
            se.setCorrectorId(in.getCorrectorId());
            se.setIntellResult(se.getCorrectorId() + "_ai_" + intelligenceAnswer.getResult() + "_" + System
                    .currentTimeMillis());
            se.setIntellResource(StringEscapeUtils.escapeEcmaScript(intelligenceAnswer.getAnswerSource()));
            se.setLastUpdateTime(new Date());
            se.setStudentExerciseList(Collections.singletonList(result));
            //是否存在白名单 0 : 不存在 1 : 存在
            se.setIsWhiteList(0);
            dataList.add(se);
        }
        StudentExercise se = new StudentExercise();
        se.setExerciseSource(in.getExerciseSource());
        stuExeService.getTableRoute(dataList);
            try {
                for (StudentExercise studentExercise : dataList) {
                    String redisKey = "stuexe_" + PraxisSsdbUtil.getCommonKey(studentExercise);
                    StudentExercise currentData = redisUtilExtend.viewGet(redisKey, StudentExercise.class);
                    logger.info("intelligenceCorrectAnswer redis key:{},data:{}", redisKey, JsonUtil.obj2Json
                            (currentData));
                    if (null != currentData) {
                        String intellResource = studentExercise.getIntellResource();
                        String correctRole = studentExercise.getCorrectorRole();
                        Long expireTime = StuAnswerUtil.getRedisKeyTtl(redisUtilExtend, studentExercise);
                        currentData.setIntellResult(studentExercise.getIntellResult());
                        currentData.setCorrectorRole(correctRole);
                        currentData.setIsWhiteList(0);
                        BeanUtils.copyProperties(currentData, studentExercise);
                        redisUtilExtend.viewSet(redisKey, currentData, expireTime != null ? expireTime.intValue() : 0);
                        studentExercise.setIntellResource(intellResource);
                        studentExercise.setIsWhiteList(0);
                    }
                    //数据流转
                    StuAnswerUtil.updateFlowTurnCorrectState(studentExercise);
                    int i = pigaiCirculationMapper.updateFlowTurnCorrectState(studentExercise);
                    logger.info("批改 - IP数据流转,更新状态:{},更新参数:{}",i,JsonUtil.obj2Json(studentExercise));
                    /*delRedisKeyOfFlowTurnList(studentExercise, i);*/
                }
            } catch (Exception e) {
                logger.error("智能批改缓存更新失败,失败的原因是:", e);
                throw new BizLayerException("", PraxisErrorCode.INTELL_CORRECT_EXCEPTION);
            }
            stuExeService.mqIntellCorrectInfo(dataList);
        /*}*/
    }

    /****
     * 智能批改是否存在白名单
     * @param longRequest
     * @return
     * @throws BizLayerException
     */
    @Override
    public CommonResponse<Boolean> intellIsWhiteList(LongRequest longRequest) throws BizLayerException {
        CommonResponse<Boolean> result = new CommonResponse<>();
        Boolean isWhiteList;
        Long id = longRequest.getId();
        UserBaseRequest createdTeacherReq = new UserBaseRequest();
        createdTeacherReq.setSystemId(id);
        Teacher teacher = userService.findTeacher(createdTeacherReq).getData();
        logger.info("智能批改是否存在白名单,userServer返回信息:{}",JsonUtil.obj2Json(teacher));
        String orgId = String.valueOf(teacher.getOrgId());
        List<Object> okay_intell_white = redisUtilExtend.getListAll(INTELL_WHITE);
        if(CollectionUtils.isNotEmpty(okay_intell_white) && okay_intell_white.size() > 0) {
            if(StringUtils.isNotEmpty(orgId) && !okay_intell_white.contains(orgId)) {  //智批需要老师确认
                isWhiteList = false;
            }else {     //智批自动生效 在白名单中
                isWhiteList = true;
            }
        }else {
            isWhiteList = false;
        }
        result.setData(isWhiteList);
        return result;
    }

    /****
     * 1、任务场景和自主学习场景智批结果自动生效
     * @param in
     * @return
     * @throws BizLayerException
     */
    @Override
    public CommonDes intelligenceCorrectAnswer(IntelligenceAnswerRequest in) throws BizLayerException {
        CommonDes commonDes = new CommonDes();
        //判断发布时间 上线时间   true : 调别人  false : 调自己
        boolean flag = true;
        if(StuAnswerUtil.isWrongQuesChalRecorded(in.getExerciseSource())) {     //任务场景
            AnswerCommonUtil.getQuestionPublicTime(in.getResourceId(), lessonService, redisUtilExtend,answerBranchParam.getPythonServerOnlineTime());
            if (!flag) {  //调自己
                commonDes = intelligenceCorrectAnswerToOwn(in, true);
                return commonDes;
            }
        }
        //任务场景及自主学习场景
        //向洪清组发送数据
        in.setReqId(TraceKeyHolder.getTraceKey());
        String param = JsonUtil.obj2Json(in);
        String url = answerBranchParam.getAnswerBranchUrl() + "/correct/update_intell_correct";
        String s = AnswerCommonUtil.doPost(param, url);
        Map<String, Object> resultList = JsonUtil.readValue(s, new TypeReference<Map<String, Object>>() {
                });
        //需要对返回值进行判断
        if(resultList != null && resultList.size() >= 0 && "success".equals(resultList.get("message"))) {
            //返回成功
            final IntelligenceAnswerRequest target = new IntelligenceAnswerRequest();
            BeanUtils.copyProperties(in,target);
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    intelligenceCorrectAnswerToOwn(target, false);
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
        return commonDes;
    }

    /****
     * 1、任务场景和自主学习场景智批结果自动生效
     * @param in
     * @return
     * @throws BizLayerException
     */
    public CommonDes intelligenceCorrectAnswerToOwn(IntelligenceAnswerRequest in,boolean isMQ) throws BizLayerException {
        StudentExercise se = new StudentExercise();
        se.setResourceId(in.getResourceId());
        se.setExerciseSource(in.getExerciseSource());
        ResourceParam req = new ResourceParam(se.getResourceId());
        if(StuAnswerUtil.isExerciseSource(se)) {//任务场景
            String orgId;
            CommonResponse<LinkResourceWithPublishInfo> response = lessonService.fetchResourcePublicByLinkId
                    (req);
            if (response.success() && response.getData() != null) {
                Long creatorId = response.getData().getResPackagePublish().getCreatorId();
                UserBaseRequest createdTeacherReq = new UserBaseRequest();
                createdTeacherReq.setSystemId(creatorId);
                Teacher teacher = userService.findTeacher(createdTeacherReq).getData();
                orgId = String.valueOf(teacher.getOrgId());
                logger.info("智能批改白名单获取教师ID :" + creatorId + "学校ID :" + orgId + " res json " + "data:" +
                        JsonUtil.obj2Json(response));

            } else {
                throw new BizLayerException("lesson:" + response.getCode() + "," + response.getMessage() + ";" +
                        "" + "" + "" + "" + "" + "" + "" + "", ANSWER_PUBLISH_RESOURCE_ID_NOT_EXIST);
            }
            List<Object> okay_intell_white = redisUtilExtend.getListAll(INTELL_WHITE);
            logger.info("智能批改自动生效白名单 :{}", JsonUtil.obj2Json(okay_intell_white));
            boolean contains = okay_intell_white.contains(orgId);
            logger.info("智能批改教师是否存在白名单获取教师ID  :" + contains);
            if(CollectionUtils.isNotEmpty(okay_intell_white) && okay_intell_white.size() > 0 && !okay_intell_white.contains(orgId)) {  //智批需要老师确认
                //任务场景(1 6 7 8) 不在白名单
                intelligenceN_WhiteCorrectAnswer(in);
                logger.info("智能批改场景:{} 是否存在白名单:{}",in.getExerciseSource(),contains);
            }else {
                //1:任务场景 在白名单
                logger.info("智能批改自动生效获取的入参 in:" + JsonUtil.obj2Json(in));
                List<StudentExercise> dataList = new ArrayList<>();
                List<IntelligenceAnswer> intellInfoList = in.getIntelligenceAnswerList();
                //智能批改自动生效mysql数据组装
                intelligenceCorrectDataComponent(dataList,intellInfoList,in);
                logger.info("智能批改自动生效获取的入参 dataList:" + JsonUtil.obj2Json(dataList));

                intelligenceCorrectRedisDataComponent(dataList);
                if(isMQ) {
                    for (StudentExercise exercise : dataList) {
                        //后置业务处理
                        String traceKey = TraceKeyHolder.getTraceKey();
                        List<StudentExercise> studentExerciseList = exercise.getStudentExerciseList();
                        CorrectAnswerEvent event;
                        if(CollectionUtils.isNotEmpty(studentExerciseList) && studentExerciseList.size() > 0) {
                            event = new CorrectAnswerEvent(new CorrectAnswerEntity(exercise,studentExerciseList.get(0), OperateType.INTELLCORRECT, traceKey));
                        }else {
                            event = new CorrectAnswerEvent(new CorrectAnswerEntity(exercise,exercise, OperateType.INTELLCORRECT, traceKey));
                        }
                        applicationContext.publishEvent(event);
                    }
                }
                return new CommonDes();
            }
        }else {     //自主学习场景
            logger.info("智能批改自动生效获取的入参 in:" + JsonUtil.obj2Json(in));
            List<StudentExercise> dataList = new ArrayList<>();
            List<IntelligenceAnswer> intellInfoList = in.getIntelligenceAnswerList();
            //智能批改自动生效mysql数据组装
            intelligenceCorrectDataComponent(dataList,intellInfoList,in);
            logger.info("智能批改自动生效获取的入参 dataList:" + JsonUtil.obj2Json(dataList));
            /*if (!StuAnswerUtil.isExerciseSource(se)) {*/
                //智能批改直接生效mysql更新、错题记录的更新
                logger.info("智能批改自动生效自主学习场景获取的入参:" + JsonUtil.obj2Json(in));
                stuExeService.updateIntellInfo(dataList, isMQ);
            if(isMQ) {
                for (StudentExercise exercise : dataList) {
                    //后置业务处理
                    String traceKey = TraceKeyHolder.getTraceKey();
                    List<StudentExercise> studentExerciseList = exercise.getStudentExerciseList();
                    CorrectAnswerEvent event;
                    if(CollectionUtils.isNotEmpty(studentExerciseList) && studentExerciseList.size() > 0) {
                        event = new CorrectAnswerEvent(new CorrectAnswerEntity(exercise,studentExerciseList.get(0), OperateType.INTELL, traceKey));
                    }else {
                        event = new CorrectAnswerEvent(new CorrectAnswerEntity(exercise,exercise, OperateType.INTELL, traceKey));
                    }
                    applicationContext.publishEvent(event);
                }
            }
        }
        return new CommonDes();
    }

    /**
     * 智能批改自动生效任务场景redis数据组装
     * @param dataList
     */
    private void intelligenceCorrectRedisDataComponent(List<StudentExercise> dataList){
        //判断缓存是否有数据，如果有先更新缓存在更新库
        boolean flag = false;
        try {
            for (StudentExercise studentExercise : dataList) {
                String redisKey = "stuexe_" + PraxisSsdbUtil.getCommonKey(studentExercise);
                StudentExercise currentData = redisUtilExtend.viewGet(redisKey, StudentExercise.class);
                logger.info("intelligenceCorrectAnswer redis key:{},data:{}", redisKey, JsonUtil.obj2Json
                        (currentData));
                if (null != currentData) {
                    String intellResource = studentExercise.getIntellResource();
                    String correctRole = studentExercise.getCorrectorRole();
                    Long expireTime = StuAnswerUtil.getRedisKeyTtl(redisUtilExtend, studentExercise);
                    currentData.setIntellResult(studentExercise.getIntellResult());
                    currentData.setCorrectorRole(correctRole);
                    currentData.setResult(studentExercise.getResult());
                    currentData.setIsWhiteList(1);
                    BeanUtils.copyProperties(currentData, studentExercise);
                    redisUtilExtend.viewSet(redisKey, currentData, expireTime != null ? expireTime.intValue() : 0);
                    studentExercise.setIntellResource(intellResource);
                    studentExercise.setIsWhiteList(1);
                }else{
                    flag = true;
                }
            }
        } catch (Exception e) {
            logger.error("智能批改缓存更新失败,失败的原因是:", e);
            throw new BizLayerException("", PraxisErrorCode.INTELL_CORRECT_EXCEPTION);
        }
        //通过异步MQ将redis缓存中的数据持久化到数据库
        logger.info("智能批改自动生效获取的入参 dataList2:" + JsonUtil.obj2Json(dataList));
        //stuExeService.mqAutoIntellCorrectInfo(dataList);
        stuExeService.mqIntellCorrectInfo(dataList);
        if(flag){
            /*目前白名单生效的情况下，在缓存失效后，会存在没有执行完入库统计服务就调用的情况，目前分析，入库与查询基本在同一秒，所有
            在此休眠2秒，如上线后还有次问题，则改成同步入库。次方法为智能批改调用，对响应时间要求不高*/
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /***
     *智能批改自动生效mysql数据组装
     * @param dataList
     * @param intellInfoList
     * @param in
     */
    private void intelligenceCorrectDataComponent(List<StudentExercise> dataList, List<IntelligenceAnswer>
            intellInfoList,IntelligenceAnswerRequest in) {
        for (IntelligenceAnswer intelligenceAnswer : intellInfoList) {
            StudentExercise se = new StudentExercise();
            BeanUtils.copyProperties(in, se);
            se.setQuestionId(intelligenceAnswer.getQuestionId());
            //获取学生做答记录信息
            StudentExercise result = getCorrectInfo(se);
            if (null == result) {
                throw new BizLayerException("", PraxisErrorCode.ANSWER_RECORD_NOT_FOUND);
            }
            BeanUtils.copyProperties(result, se);
            se.setCorrectorRole(intelligenceAnswer.getCorrectorRole());
            se.setCorrectorId(in.getCorrectorId());
            se.setIntellResult(se.getCorrectorId() + "_ai_" + intelligenceAnswer.getResult() + "_" + System
                    .currentTimeMillis());
            se.setIntellResource(StringEscapeUtils.escapeEcmaScript(intelligenceAnswer.getAnswerSource()));
            se.setLastUpdateTime(new Date());
            se.setStudentExerciseList(Collections.singletonList(result));
            se.setResult(intelligenceAnswer.getResult());
            se.setTotalScore(intelligenceAnswer.getTotalScore());
            se.setScore(StuAnswerUtil.getScoreByStruct(se.getStructId(), intelligenceAnswer.getTotalScore(), intelligenceAnswer.getResult()));
            //是否存在白名单 0 : 不存在 1 : 存在
            se.setIsWhiteList(1);
            if (!StuAnswerUtil.isExerciseSource(se)) {
                //如果答题场景为自主练习
                se.setTableName(TableNameUtil.getTableName(se));
            }else {
                //如果答题场景为任务
                se.setTableName(TableNameUtil.getStudentExercise(se));
            }
            //任务场景和自主学习智批场景智批结果自动生效

            //添加单题是否批改完成的标识 3:已批改 空:未批改
            if (AnswerCommonUtil.isAllCorrect(se.getResult(), se.getStructId())) {
                se.setFlag(3);
            }
            dataList.add(se);
        }
    }

    /***
     * 使用智能批改结果
     *  单题是一个题的提交、复合体是每个小题的提交
     *  根据题型结构判断当前智能批改的结果
     * @param in
     * @return
     * @throws BizLayerException
     */
    @Override
    public CommonDes submitIntellCorrectAnswer(IntellCorrectStuQueRequest in) throws BizLayerException {

        in.setReqId(TraceKeyHolder.getTraceKey());
        CommonDes commonDes = new CommonDes();
        //是否发MQ     true : 发    false : 不发
        final boolean isMQ;
        if(StuAnswerUtil.isWrongQuesChalRecorded(in.getExerciseSource())) {     //任务场景
            //判断发布时间 上线时间   true : 调别人  false : 调自己
            boolean flag = AnswerCommonUtil.getQuestionPublicTime(in.getResourceId(), lessonService, redisUtilExtend,answerBranchParam.getPythonServerOnlineTime());
            if (!flag) {  //调自己
                isMQ = true;
                commonDes = submitIntellCorrectAnswerToOwn(in, isMQ);
                return commonDes;
            }
        }
        //任务场景 自主学习场景
        //向洪清组发送数据
        final String param = JsonUtil.obj2Json(in);
        final String url = answerBranchParam.getAnswerBranchUrl() + "/correct/use_intell_correct";
        String s = AnswerCommonUtil.doPost(param, url);
        Map<String, Object> resultList = JsonUtil.readValue(s, new
                TypeReference<Map<String, Object>>() {
                });
        //需要对返回值进行判断
        if(resultList != null && resultList.size() >= 0 && "success".equals(resultList.get("message"))) {
            //返回成功
            isMQ = false;
            final IntellCorrectStuQueRequest target = new IntellCorrectStuQueRequest();
            BeanUtils.copyProperties(in,target);
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    submitIntellCorrectAnswerToOwn(target, isMQ);
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
        return commonDes;
    }

    /***
     * 使用智能批改结果
     *  单题是一个题的提交、复合体是每个小题的提交
     *  根据题型结构判断当前智能批改的结果
     * @param in
     * @return
     * @throws BizLayerException
     */
    public CommonDes submitIntellCorrectAnswerToOwn(IntellCorrectStuQueRequest in, boolean isMQ) throws BizLayerException {
        logger.debug("[[submitIntellCorrectAnswer]]使用智能批改结果请求参数:" + JsonUtil.obj2Json(in));
        delRedisKeyOfFlowTurnStu(in);
        StudentExercise se = new StudentExercise();
        BeanUtils.copyProperties(in, se);
        //获取学生的答题记录
        List<StudentExercise> dbList = stuExeService.getDbRecords(se);
        if (CollectionUtils.isNotEmpty(dbList)) {
            StudentExercise studentExercise = dbList.get(0);
            BeanUtils.copyProperties(studentExercise, se);
            se.setCorrectorTime(new Date());
            se.setLastUpdateTime(new Date());
            se.setParentQuestionId(studentExercise.getParentQuestionId() == null ? null : studentExercise
                    .getParentQuestionId());
            correctBusDeal(se);
            logger.info("[[submitIntellCorrectAnswer]]使用智能批改结果获取的数据:" + JsonUtil.obj2Json(se));
            se.setIntellPostilStatus(1);
            //将数据更新到答题记录表、错体表、SSDB
            //stuExeService.updateRecord(se, OperateType.CORRECT);
            stuExeService.updateRecord(se, OperateType.CORRECT, isMQ);

            if(isMQ) {
                //调用批改后置业务
                String traceKey = TraceKeyHolder.getTraceKey();
                CorrectAnswerEvent event = new CorrectAnswerEvent(new CorrectAnswerEntity(se, studentExercise,
                        OperateType.INTELL, traceKey));
                applicationContext.publishEvent(event);
            }
        } else {
            throw new BizLayerException("", PraxisErrorCode.INTELL_CORRECT_NO_RESULT);
        }
        return new CommonDes();
    }

    /****
     * 批改是否重复批改，批改结果变化（错批改为对和对批改为错）数据采集
     * @param in
     * @return
     * @throws BizLayerException
     */
    @Override
    public CorrectCollectionInfoRes correctCollectionInfo(final UpdateCorrectRequest in) throws BizLayerException {
        in.setReqId(TraceKeyHolder.getTraceKey());
        final String url = answerBranchParam.getAnswerBranchUrl() + "/correct/collect_info";
        CorrectCollectionInfoRes resultList = null;
        CorrectCollectionInfoRes oneselfResult = new CorrectCollectionInfoRes();
        if (StuAnswerUtil.isWrongQuesChalRecorded(in.getExerciseSource())) {     //任务场景 发布时间 < 上线时间  调自己
            //判断发布时间 上线时间   true : 调别人  false : 调自己
            boolean flag = AnswerCommonUtil.getQuestionPublicTime(in.getResourceId(), lessonService, redisUtilExtend,answerBranchParam.getPythonServerOnlineTime());
            if(!flag) {
                CorrectCollectionInfoRes oneselfOut = correctCollectionInfoToOwn(in);
                return oneselfOut;
            }else {     //任务场景 发布时间 > 上线时间  调别人
                Map<String,Object> param = new HashedMap<String,Object>();
                param.put("param",JsonUtil.obj2Json(in));
                String s = AnswerCommonUtil.doGet(param, url);
                resultList = JsonUtil.readValue(s, CorrectCollectionInfoRes.class);
            }
        }else {     //自主学习场景    同时调别人和自己
            final String requestId = TraceKeyHolder.getTraceKey();
            FutureTask<CorrectCollectionInfoRes> futureTask = new FutureTask<CorrectCollectionInfoRes>(new Callable<CorrectCollectionInfoRes>() {
                public CorrectCollectionInfoRes call() {
                    //任务场景(发布时间 > 上线时间)     自主学习场景
                    //向洪清组发送数据
                    TraceKeyHolder.setTraceKey(requestId);
                    Map<String,Object> param = new HashedMap<String,Object>();
                    param.put("param",JsonUtil.obj2Json(in));
                    String s = AnswerCommonUtil.doGet(param, url);
                    CorrectCollectionInfoRes resultList = JsonUtil.readValue(s, CorrectCollectionInfoRes.class);
                    return resultList;
                }
            });
            threadPool.execute(futureTask);
            //自主学习场景
            if (!StuAnswerUtil.isWrongQuesChalRecorded(in.getExerciseSource())) {
                CorrectCollectionInfoRes stuQuesAnswOnBatchToOwn = correctCollectionInfoToOwn(in);
                if (stuQuesAnswOnBatchToOwn != null) {
                    BeanUtils.copyProperties(stuQuesAnswOnBatchToOwn, oneselfResult);
                }
                logger.info("correctCollectionInfo oneselfResult:{}", oneselfResult);
            }
            try {
                resultList = futureTask.get(5000, TimeUnit.MILLISECONDS);
                logger.info("correctCollectionInfo othersResult:{}", resultList);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            } finally {
                futureTask.cancel(true);
            }
        }
        if (StuAnswerUtil.isWrongQuesChalRecorded(in.getExerciseSource())) {      //任务场景
            if(resultList == null || "".equals(resultList)) {
                throw new BizLayerException("", INNER_ERROR);
            }else {
                return resultList;
            }
        } else {     //自主学习场景
            if (resultList.getCode() == 0 && "success".equals(resultList.getMessage())) {
                return resultList;
            }
        }
        return oneselfResult;
    }

    /****
     * 批改是否重复批改，批改结果变化（错批改为对和对批改为错）数据采集
     * @param in
     * @return
     * @throws BizLayerException
     */
    public CorrectCollectionInfoRes correctCollectionInfoToOwn(UpdateCorrectRequest in) throws BizLayerException {
        CorrectCollectionInfoRes res = new CorrectCollectionInfoRes();
        StudentExercise exercise = new StudentExercise();
        BeanUtils.copyProperties(in, exercise);
        getStructParentQuestionId(exercise);
        StudentExercise historyExercise = getCorrectInfo(exercise);
        exercise.setResult(in.getResult());
        boolean flag = StuAnswerUtil.getRepeatCorrect(historyExercise, exercise);
        res.setRepeatCorrect(flag);
        //判断批改结果是否发生改变
        //coorectStatus
        //批改结果状态变化
        //0未发生变化1由错批改为对2由对批改为错
        if (flag) {
            int number = StuAnswerUtil.getCorrectStatus(historyExercise, exercise);
            res.setCoorectStatus(number);
        } else {
            res.setCoorectStatus(0);
        }
        return res;
    }

    @Override
    public CorrectCollectionInfoRes correctCollectionInfo(StudentExercise in, StudentExercise historyExercise) throws
            BizLayerException {
        UpdateCorrectRequest request = new UpdateCorrectRequest();
        BeanUtils.copyProperties(in, request);
        return correctCollectionInfo(request);
    }

    @Override
    public CorrectInfoRes correctInfo(StudentExercise in, StudentExercise historyExercise) throws BizLayerException {
        CorrectInfoRes res = new CorrectInfoRes();
        in.setStructId(historyExercise.getStructId());
        //判断批改结果是否发生改变
        String number = StuAnswerUtil.getCorrectInfoStatus(historyExercise, in);
        res.setCoorectStatus(number);

        return res;
    }

    @Override
    public CorrectInfoRes autoIntellCorrectInfo(StudentExercise in, StudentExercise historyExercise) throws BizLayerException {
        CorrectInfoRes res = new CorrectInfoRes();
        in.setStructId(historyExercise.getStructId());
        //判断批改结果是否发生改变
        String number = StuAnswerUtil.getAutoIntellCorrectInfoStatus(historyExercise, in);
        res.setCoorectStatus(number);
        return res;
    }

    /***
     * 使用智能批改和智能批注
     * @param in
     * @return
     * @throws BizLayerException
     */
    @Override
    public UpdatePostilResponse submitIntellInfo(final IntellCorrectStuQueRequest in) throws BizLayerException {
        long startTime = System.currentTimeMillis();
        if (null == in.getPostilUrl()) {
            throw new BizLayerException("", PraxisErrorCode.POSTIL_NO_RESULT);
        }
        //使用智能批注 1、修改智能批注状态  2、传递的批注的数据存储到批注里面
        UpdatePostilRequest updatePostilRequest = new UpdatePostilRequest();
        BeanUtils.copyProperties(in, updatePostilRequest);
        updatePostilRequest.setPostilTeacher(in.getPostilUrl());
        UpdatePostilResponse updatePostilResponse = updatePostil(updatePostilRequest);
        final String traceKey = TraceKeyHolder.getTraceKey();
        Runnable t = new Runnable() {
            @Override
            public void run() {
                //使用智能批改
                TraceKeyHolder.setTraceKey(traceKey);
                submitIntellCorrectAnswer(in);
            }
        };
       /* threadPool.execute(t);  submitIntellCorrectAnswer(in);*/
        long endTime = System.currentTimeMillis();
        logger.info("submitIntellInfo cost time:{}ms", endTime - startTime);
        return updatePostilResponse;
    }

    @Override
    public FindHistoryDataResponse findHistoryDataList(FindHistoryDataRequest request) throws BizLayerException {
        request.setReqId(TraceKeyHolder.getTraceKey());
        FindHistoryDataResponse responseOut = new FindHistoryDataResponse();
        //判断发布时间 上线时间   true : 调别人  false : 调自己
        boolean flag = true;
        boolean isExerciseSource;   //true  任务场景    false 自主学习场景
        if(StuAnswerUtil.isWrongQuesChalRecorded(request.getExerciseSource())) {     //任务场景
            flag = AnswerCommonUtil.getQuestionPublicTime(request.getResourceId(), lessonService, redisUtilExtend,answerBranchParam.getPythonServerOnlineTime());
            isExerciseSource = true;
        }else {     //自主学习场景
            isExerciseSource = false;
        }
        StudentExerciseListOut out = getStudentExerciseListOut(flag, "findHistoryDataList",isExerciseSource,request);
        if(out != null && !"".equals(out) && "success".equals(out.getMessage()) && out.getStudentExerciseList() != null && !"".equals(out.getStudentExerciseList())) {
            responseOut.setStudentExerciseList(out.getStudentExerciseList());
        }
        return responseOut;
    }


    public FindHistoryDataResponse findHistoryDataListToOwn(FindHistoryDataRequest request) throws BizLayerException {
        StudentExercise studentExercise = new StudentExercise();
        BeanUtils.copyProperties(request, studentExercise);
        boolean flag = StuAnswerUtil.isExerciseSource(studentExercise);
        FindHistoryDataResponse response = new FindHistoryDataResponse();
        if (flag) {
            List<StudentExercise> historyDataList = stuExeService.getHistoryDbRecords(studentExercise);
            response.setStudentExerciseList(historyDataList);
            return response;
        } else {
            response.setStudentExerciseList(null);
        }
        return response;
    }

    @Override
    public CommonDes updateAudioAnswer(AudioUrlUpdateRequest request) throws BizLayerException {
        logger.info("听口题学生做答记录修改请求入参:" + JsonUtil.obj2Json(request));
        StudentExercise se = new StudentExercise();
        BeanUtils.copyProperties(request, se);
        stuExeService.sendAudioCalbackMq(se, OperateType.AUDIO);
        return new CommonDes();
    }

    /***
     *获取学生做答记录信息
     * @param se
     * @return
     */
    private StudentExercise getCorrectInfo(StudentExercise se) {
        StudentExercise entity = new StudentExercise();
        entity.setResourceId(se.getResourceId());   //刷题id/题集发布id
        entity.setStudentId(se.getStudentId());     //学生id
        entity.setQuestionId(se.getQuestionId());   //题目id
        entity.setExerciseSource(se.getExerciseSource());   //答题来源
        entity.setStructId(se.getStructId());       //题目结构id
        //如果做答场景为上课、预习、作业、测评，通过redis缓存查询数据
        if (StuAnswerUtil.isExerciseSource(se)) {
            StudentExercise allSes = StuAnswerUtil.getDataFromRedis(se, redisUtilExtend);
            if (null != allSes) {
                BeanUtils.copyProperties(allSes, entity);
            } else {
                //查询学生做答记录
                List<StudentExercise> dbList = stuExeService.getDbRecords(entity);
                if (CollectionUtils.isNotEmpty(dbList)) {
                    BeanUtils.copyProperties(dbList.get(0), entity);
                }
            }
        } else {
            List<StudentExercise> dbList = stuExeService.getDbRecords(entity);
            if (CollectionUtils.isNotEmpty(dbList)) {
                BeanUtils.copyProperties(dbList.get(0), entity);
            }
        }
        return entity;
    }

    /***
     * 根据题ID查询题型结构和复合题大题ID
     * @param se
     * @return
     */
    private StudentExercise getStructParentQuestionId(StudentExercise se) {
        //通过redis缓存查询的题型结构和复合体大题ID
        String redisKey = RedisKeyUtil.getQueStructParentId(se);
        String result = redisUtilExtend.getString(redisKey);
        if (null != result) {
            String[] results = result.split("_");
            se.setStructId(Integer.parseInt(results[0]));
            se.setParentQuestionId((results.length == 2) ? Long.valueOf(results[1]) : null);
        } else {
            //通过solr查询parent_question_id和题型结构struct_id
            List<Question> list = questionSearchService.getQuesListByIds(Collections.singletonList(se.getQuestionId()));
            if (CollectionUtils.isNotEmpty(list)) {
                se.setParentQuestionId(list.get(0).getParentQuestionId() == 0 ? null : list.get(0)
                        .getParentQuestionId());
                se.setStructId(Integer.parseInt(list.get(0).getStructId() + ""));
                redisUtilExtend.setString(redisKey, list.get(0).getStructId() + "_" + list.get(0).getParentQuestionId
                        (), StuAnswerUtil.REDIS_EXPIRE_FOR_QUESTION);
            }
        }
        return se;
    }

    /***
     * 加锁原因，线上发现的两个bug：1、对于同一个答题记录上课批改、批注同时触发，会导致答题记录缓存的数据不一致
     // 2、同时批改同一个大题下的两个小题，创建错题记录时会造成数据不一致，所以对大题id加锁
     * @param exerciseSource
     * @param resourceId
     * @param studentId
     * @param questionId
     * @return
     */
    private String lockCorrectRecord(String exerciseSource, Long resourceId, Long studentId, Long questionId) {
        final String key = RedisKeyUtil.makeKey(RedisKeyUtil.CORRECT_ANSWER_LOCK_PREFIX, exerciseSource, resourceId +
                "", studentId + "", questionId + "");
        redisUtilExtend.addLock(key, RedisKeyUtil.CORRECT_ANSWER_LOCK_EXPIRE, 20);
        return key;
    }

    private void unLockCorrectRecord(String key) {
        redisUtilExtend.unLock(key);
    }

    /***
     * 优化
     * @param allSes
     */
    private void setQuesType(List<StudentExercise> allSes) {
        if (CollectionUtils.isEmpty(allSes)) {
            return;
        }
        List<Long> quesIdList = StuAnswerUtil.getQuesIdList(allSes);
        Map<Long, QuestionStructAndParentId> map = new HashMap<>(quesIdList.size() + 1);
        AnswerCommonUtil.getQuestionStruct(quesIdList, map, questionSearchService);

        //将查找到的 所有的结构信息 设置到 对象里去
        for (StudentExercise se : allSes) {
            QuestionStructAndParentId getStruct = map.get(se.getQuestionId());
            if (getStruct != null) {
                se.setQuestionType(getStruct.getQuestionTypeId().toString());
            }
        }
    }

    /***
     * 求助批改
     * @param in
     */
    @Override
    public FlowTurnOut findFlowTurnList(FlowTurnCorrectRequest in) throws BizLayerException {
        logger.info("findFlowTurnList 入参:{}", in);
        long startTime = System.currentTimeMillis();
        FlowTurnOut out = new FlowTurnOut();
        List<FlowTurn> list = new ArrayList<FlowTurn>();
        com.noriental.resourcesvr.customlist.request.FlowTurnCorrectRequest in1 = new com.noriental.resourcesvr.customlist.request.FlowTurnCorrectRequest();
        BeanUtils.copyProperties(in, in1);
        try {
                //查询自定义目录题目关系表
            Integer isCustomListid = in.getIsCustomListid();
            logger.info("findFlowTurnList 入参是否是末级节点:{}", isCustomListid);
                in.setIsCustomListid(isCustomListid);
                //自定义目录第一级id
                Long catalogIdFirst = in.getCatalogIdFirst();
                //自定义目录第二级id
                Long catalogIdSecond = in.getCatalogIdSecond();
                ////自定义目录目录id
                Long catalogId = in.getCatalogId();

                //查询自定义目录表 节点查所有子节点
                CommonResponse<List<Long>> request = new CommonResponse<List<Long>>();
                //是否是未级节点   1:是     0:否
                if(isCustomListid == 0) {   //不是未级节点
                    if(catalogId != null && !"".equals(catalogId)) {
                        request = customListService.findIdByChild(in1);
                    }else {
                        request = customListService.findGroupByChild(in1);
                    }
                    if (request.success() && CollectionUtils.isNotEmpty(request.getData())) {
                        List<Long> data = request.getData();
                        in.setCustomListid(data);
                    }else {
                        return out;
                    }
                }
                //未级节点对应的题
                long findLinkCustomTime = System.currentTimeMillis();
                List<Long> questionList = questionService.findLinkCustomQuestionResource(in);
                logger.info("findLinkCustomQuestionResource mysql search cost time:{} ms", System.currentTimeMillis() - findLinkCustomTime);
                if(CollectionUtils.isNotEmpty(questionList) && questionList.size() > 0) {
                    int pageSize = in.getPageSize();
                    PageBounds pageBounds = in.getPageBounds();
                    pageBounds.setPage((pageBounds.getPage() - 1) * pageSize);
                    pageBounds.setLimit(pageSize);
                    in.setPageBounds(new PageBounds());
                    in.setQuestionList(questionList);
                    //用户创建未批题或已批题的总数
                    long findFlowTurnTotalCountTime = System.currentTimeMillis();
                    int totalCount= pigaiCirculationMapper.findFlowTurnTotalCount(in.getQuestionList(),in.getIs_corrected(),in.getSystemId());
                    logger.info("findFlowTurnTotalCount mysql search cost time:{} ms", System.currentTimeMillis() - findFlowTurnTotalCountTime);
                    out.setTotalCount(totalCount);
                    out.setCurrentPage(pageBounds.getPage());
                    out.setPageSize(pageBounds.getLimit());
                    out.setTotalPage(totalCount,pageBounds.getLimit());

                    long startFlowTime = System.currentTimeMillis();
                    //查询题的信息
                    list = pigaiCirculationMapper.findFlowTurn(in.getIs_corrected(),in.getSystemId(),in.getQuestionList(),in.getSort_type(),in.getSort_value(),pageBounds.getPage(),pageBounds.getLimit());
                    logger.info("findFlowTurn mysql mysql cost time:{} ms", System.currentTimeMillis() - startFlowTime);
                    if(CollectionUtils.isNotEmpty(list) && list.size() > 0) {
                        long startFlowTurnTotalNumTime = System.currentTimeMillis();
                        for(FlowTurn flowTurn : list) {
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append(flowTurn.getGroupName()).append(" > ");
                            //该题的总人数
                            int totalNum = 0;
                            //该题未批人数
                            int num = 0;
                            if(flowTurn.getParentQuestionId() != null && !"".equals(flowTurn.getParentQuestionId() )) {
                                FlowTurn flowTurn1 = new FlowTurn();
                                BeanUtils.copyProperties(flowTurn,flowTurn1);
                                flowTurn1.setQuestionId(null);
                                //复合题
                               /* List<FlowTurn> questionIdList = pigaiCirculationMapper.findQuestionIdList(flowTurn1);
                                if(CollectionUtils.isNotEmpty(questionIdList) && questionIdList.size() > 0) {
                                    flowTurn.setQuestionIdList(questionIdList);
                                }*/
                                totalNum = pigaiCirculationMapper.findFlowTurnListTotalNum(flowTurn1);
                                num = pigaiCirculationMapper.findFlowTurnListNum(flowTurn1);
                            }else {
                                //单题
                                totalNum = pigaiCirculationMapper.findFlowTurnListTotalNum(flowTurn);
                                num = pigaiCirculationMapper.findFlowTurnListNum(flowTurn);
                            }
                            flowTurn.setNum(num);
                            flowTurn.setTotalNum(totalNum);
                            IdRequest request1 = new IdRequest();
                            request1.setId(flowTurn.getCustomListId());
                            CommonResponse<List<CustomListVo>> parentsByLeafId = customListService.findParentsByLeafId(request1);
                            if (parentsByLeafId.success() && CollectionUtils.isNotEmpty(parentsByLeafId.getData())) {
                                List<CustomListVo> data = parentsByLeafId.getData();
                                if(CollectionUtils.isNotEmpty(data) && data.size() > 0) {
                                    for(CustomListVo vo : data) {
                                        int level = vo.getLevel();
                                        if(1 == level) {
                                            stringBuilder.append(vo.getName());
                                        }else if(2 == level) {
                                            stringBuilder.append(vo.getName()).append(" > ");
                                        }else if(3 == level){
                                            stringBuilder.append(vo.getName());
                                        }
                                    }
                                    flowTurn.setChapterFullName(stringBuilder.toString());
                                }
                            }else {
                                return new FlowTurnOut();
                            }
                        }
                        //排序
                        if(in.getSort_type() == 2 ) {
                            if(in.getSort_value() == 1) {
                                Collections.sort(list, new Comparator<FlowTurn>() {
                                    @Override
                                    public int compare(FlowTurn o1, FlowTurn o2) {
                                        int i = o1.getNum() - o2.getNum();
                                        return i;
                                    }
                                });
                            }else if(in.getSort_value() == 2) {
                                Collections.sort(list, new Comparator<FlowTurn>() {
                                    @Override
                                    public int compare(FlowTurn o1, FlowTurn o2) {
                                        int i = o2.getNum() - o1.getNum();
                                        return i;
                                    }
                                });
                            }
                        }
                        logger.info("findFlowTurnTotalNum mysql mysql cost time:{} ms", System.currentTimeMillis() - startFlowTurnTotalNumTime);
                    }else {
                        return new FlowTurnOut();
                    }
                }else {
                    return out;
                }
                logger.debug("查询求助批改列表结果数据:" + JsonUtil.obj2Json(list));
                out.setFlowTurn(list);
                //存入缓存
                /*int expire = pigaiCirculationMapper.findFlowTurnExpire();
                redisUtil.viewSet(key, out, expire);
                logger.debug("查询求助批改列表 存入缓存key{} 数据:{} 失效时间:{}",key,out,expire);*/
            /*}*/
        } catch (Exception e) {
            logger.error("查询求助批改列表失败,失败的原因是:", e);
            throw new BizLayerException("", PraxisErrorCode.FIND_FLOW_TURN_EXCEPTION);
        }
        long endTime = System.currentTimeMillis();
        logger.info("findFlowTurnList 出参:{}", out);
        logger.info("findFlowTurnList  cost time:{} ms", endTime - startTime);
        return out;
    }

    //求助批改 - 批改查询
    @Override
    public FlowTurnListOut findFlowTurnStuAnswsOnBatch(FlowTurn in) throws BizLayerException {
        logger.info("findFlowTurnStuAnswsOnBatch 入参:{}", in);
        long startTime = System.currentTimeMillis();
        FlowTurnListOut out = new FlowTurnListOut();
        List<FlowTurnList> list = new ArrayList<FlowTurnList>();
        try {
            Long questionId = in.getQuestionId();
            Long systemId = in.getSystemId();
            if(questionId == null && "".equals(questionId) && systemId == null && "".equals(systemId)) {
                throw new BizLayerException("", ANSWER_PARAMETER_NULL);
            }else {
                String key = StuAnswerUtil.getRedisKeyOfFindFlowTurnStu(in);
                boolean exists = redisUtil.exists(key);
                logger.info("findFlowTurnStuAnswsOnBatch key:{} 缓存是否存在:{}", key, exists);
                if(exists) {
                    out = redisUtil.viewGet(key, FlowTurnListOut.class);
                } else {

                    EntityQuestion eq = entityQuestionMapper.findQuestionById(questionId);
                    //'是否单题 0不是单题，下面有小题 1是单题'
                    Integer isSingle = eq.getIsSingle();
                    if(isSingle == null) {
                        //判断数据是否存在
                        int isFExist = pigaiCirculationMapper.selectFIsExist(in);
                        if(isFExist == 1) {
                            in.setParentQuestionId(in.getQuestionId());
                            in.setQuestionId(null);
                        }
                    }else if (isSingle == 0) {
                        in.setParentQuestionId(in.getQuestionId());
                        in.setQuestionId(null);
                    }
                    //该题的总人数
                    int totalNum = 0;
                    //该题未批人数
                    int num = 0;
                    if(in.getParentQuestionId() != null && !"".equals(in.getParentQuestionId() )) {
                        FlowTurn flowTurn1 = new FlowTurn();
                        BeanUtils.copyProperties(in,flowTurn1);
                        flowTurn1.setQuestionId(null);
                        //复合题
                        totalNum = pigaiCirculationMapper.findFlowTurnTotalNum(flowTurn1);
                        num = pigaiCirculationMapper.findFlowTurnNum(flowTurn1);
                    }else {
                        //单题
                        totalNum = pigaiCirculationMapper.findFlowTurnTotalNum(in);
                        num = pigaiCirculationMapper.findFlowTurnNum(in);
                    }
                    out.setTotalNum(totalNum);
                    out.setNum(num);
                    //查询该题的截止时间
                    FlowTurn flowTurn3 = pigaiCirculationMapper.findFlowTurnDeadline();
                    out.setDeadline(flowTurn3.getDeadline());
                    long selStuQuesAnswTime = System.currentTimeMillis();
                    list = pigaiCirculationMapper.selStuQuesAnswOnBatch(in);
                    if(CollectionUtils.isNotEmpty(list) && list.size() > 0) {
                        //子题排序
                        StuAnswerUtil.sortByParentSubQuesFlowTurn(list,questionService);
                        //智批题
                        list = StuAnswerUtil.getFTIntellCorrectInfo(list);
                    }
                    logger.info("findFlowTurnStuAnswsOnBatch mysql search cost:{} ms", System.currentTimeMillis() - selStuQuesAnswTime);

                    out.setFlowTurnList(list);
                    //存入缓存
                    int expire = pigaiCirculationMapper.findFlowTurnExpire();
                    redisUtil.viewSet(key, out, expire);
                    logger.debug("查询求助批改列表 存入缓存key{} 数据:{} 失效时间:{}",key,out,expire);
                }
            }
        }catch (Exception e) {
            logger.error("查询学生做答记录列表失败,失败的原因是:", e);
            throw new BizLayerException("", PraxisErrorCode.FIND_FLOW_TURN_EXCEPTION);
        }
        long endTime = System.currentTimeMillis();
        logger.info("findFlowTurnStuAnswsOnBatch 出参:{}", out);
        logger.info("findFlowTurnStuAnswsOnBatch cost time:{} ms", endTime - startTime);
        return out;
    }

    private void delRedisKeyOfFlowTurnStu(UpdateCorrectRequest bRequest) {
        FlowTurn request = new FlowTurn();
        request.setSystemId(bRequest.getCorrectorId());
        request.setQuestionId(bRequest.getQuestionId());
        String key = StuAnswerUtil.getRedisKeyOfFindFlowTurnStu(request);
        if(key != null && !"".equals(key)) {
            redisUtil.del(key);
            logger.info("批改 - IP数据流转题详情,批改 删除redis key:{}成功",key);
        }else {
            logger.info("批改 - IP数据流转题详情,批改 删除redis key:{}失败",key);
        }
    }
    private void delRedisKeyOfFlowTurnStu(IntellCorrectStuQueRequest bRequest) {
        FlowTurn request = new FlowTurn();
        request.setSystemId(bRequest.getCorrectorId());
        request.setQuestionId(bRequest.getQuestionId());
        String key = StuAnswerUtil.getRedisKeyOfFindFlowTurnStu(request);
        if(key != null && !"".equals(key)) {
            redisUtil.del(key);
            logger.info("批改 - IP数据流转题详情,使用智能批改结果 删除redis key:{}成功",key);
        }else {
            logger.info("批改 - IP数据流转题详情,使用智能批改结果 删除redis key:{}失败",key);
        }
    }

    private void delRedisKeyOfFlowTurnList(StudentExercise studentExercise, int i) {
        if(i > 0) {
            Long questionId = studentExercise.getQuestionId();
            if(questionId != null && !"".equals(questionId)) {
                Long uploadId = pigaiCirculationMapper.findQuestionUploadId(questionId);
                FlowTurnCorrectRequest request = new FlowTurnCorrectRequest();
                request.setSystemId(uploadId);
                String key = StuAnswerUtil.getRedisKeyOfFlowTurnList(request);
                Set<String> keys = redisUtil.keys(key);
                logger.info("批改 - IP数据流转,批改/一键批改/一键智能批改 删除redis key:{}", keys);
                if(CollectionUtils.isNotEmpty(keys) && keys.size() > 0) {
                    for(String s : keys) {
                        redisUtil.del(s);
                    }
                }
            }
        }
    }

    public StudentExerciseListOut getStudentExerciseListOut(boolean flag, final String methodType, final boolean isExerciseSource,final BaseRequest request) {

        final String url = answerBranchParam.getAnswerBranchUrl() + "/query/query";
        StudentExerciseListOut studentExerciseListOut = null;
        StudentExerciseListOut oneselfResult = new StudentExerciseListOut();
        if (isExerciseSource) {//任务场景 发布时间 < 上线时间  调自己
            if(!flag) {
                StudentExerciseListOut oneselfOut = getOneselfStudentExerciseListOut(methodType, request);
                return oneselfOut;
            }else{     //任务场景 发布时间 > 上线时间  调别人
                Map<String,Object> param = new HashedMap<String,Object>();
                param.put("param",JsonUtil.obj2Json(request));
                param.put("methodType",methodType);
                String s = AnswerCommonUtil.doGet(param, url);
                studentExerciseListOut = JsonUtil.readValue(s, StudentExerciseListOut.class);
            }
        }else {//自主学习场景    同时调别人和自己
            final String requestId = TraceKeyHolder.getTraceKey();
            FutureTask<StudentExerciseListOut> futureTask = new FutureTask<StudentExerciseListOut>(new Callable<StudentExerciseListOut>() {
                public StudentExerciseListOut call() {
                    TraceKeyHolder.setTraceKey(requestId);
                    //自主学习场景
                    Map<String,Object> param = new HashedMap<String,Object>();
                    param.put("param",JsonUtil.obj2Json(request));
                    param.put("methodType",methodType);
                    String s = AnswerCommonUtil.doGet(param, url);
                    StudentExerciseListOut resultList = JsonUtil.readValue(s, StudentExerciseListOut.class);
                    return resultList;
                }
            });
            threadPool.execute(futureTask);
            //自主学习场景
            StudentExerciseListOut stuQuesAnswOnBatchToOwn = getOneselfStudentExerciseListOut(methodType, request);
            if (stuQuesAnswOnBatchToOwn != null) {
                BeanUtils.copyProperties(stuQuesAnswOnBatchToOwn, oneselfResult);
            }
            logger.info(methodType + " oneselfResult:{}", oneselfResult);
            try {
                studentExerciseListOut = futureTask.get(5000, TimeUnit.MILLISECONDS);
                logger.info(methodType + " othersResult:{}", JsonUtil.obj2Json(studentExerciseListOut));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            } finally {
                futureTask.cancel(true);
            }
        }
        if (isExerciseSource) {      //任务场景
            if(studentExerciseListOut == null || "".equals(studentExerciseListOut)) {
                throw new BizLayerException("", INNER_ERROR);
            }else {
                return studentExerciseListOut;
            }
        } else {     //自主学习场景
            if (studentExerciseListOut.getCode() == 0 && studentExerciseListOut.getStudentExerciseList()!=null && studentExerciseListOut.getStudentExerciseList().size()>0) {
                return studentExerciseListOut;
            }
        }
        return oneselfResult;
    }

    public  StudentExerciseListOut getOneselfStudentExerciseListOut(String methodType, BaseRequest in) {

        StudentExerciseListOut out = new StudentExerciseListOut();
        if("findStuQuesAnswOnBatch".equals(methodType)) {
            out = findStuQuesAnswOnBatchToOwn((FindStuQuesAnswOnBatchRequest)in);
            out.setIsOldData(0);
            return out;
        }else if("findStuAnswsOnBatch".equals(methodType)) {
            out = findStuAnswsOnBatchToOwn((FindStuAnswsOnBatchRequest)in);
            out.setIsOldData(0);
            return out;
        }else if("findQuesAnswsOnBatch".equals(methodType)) {
            out = findQuesAnswsOnBatchToOwn((FindQuesAnswsOnBatchRequest)in);
            out.setIsOldData(0);
            return out;
        }else if("findHistoryDataList".equals(methodType)) {
            FindHistoryDataResponse response;
            response = findHistoryDataListToOwn((FindHistoryDataRequest)in);
            out.setStudentExerciseList(response.getStudentExerciseList());
            out.setIsOldData(0);
            return out;
        } return null;
    }
}