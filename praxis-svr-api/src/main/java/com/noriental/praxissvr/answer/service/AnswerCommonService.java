package com.noriental.praxissvr.answer.service;

import com.noriental.praxissvr.answer.bean.FlowTurn;
import com.noriental.praxissvr.answer.bean.StudentExercise;
import com.noriental.praxissvr.answer.request.*;
import com.noriental.praxissvr.answer.response.*;
import com.noriental.validate.bean.CommonDes;
import com.noriental.validate.bean.CommonResponse;
import com.noriental.validate.bean.LongRequest;
import com.noriental.validate.exception.BizLayerException;

/**
 * @author kate
 */
public interface AnswerCommonService {


    /****
     * 作答
     * 所有场景习题答案提交
     * 1、上课  按题提交，一次提交一个题
     * 2、其他答题场景一次提交一个题集（里面包含多个题）
     *
     * 数据流转：
     * 上课：一个题一个redis key缓存  作业、测评、预习 一个题集一个key
     * 其他答题场景直接走mysql数据库
     * 后置业务：
     * mysql操作：entity_stu_ques_knowledge（章节知识点错题记录）、entity_statis_stu_work（知识点统计即答题个数）、entity_answer_chal（错题消灭表）
     *
     * @param in
     * @return
     * @throws BizLayerException
     */
    CommonDes updateSubmitAnswer(UpdateSubmitAnswerRequest in) throws BizLayerException;

    /***
     * 批注
     * 上课：数据流转 通过MQ异步入mysql(答题记录、错体表) 和ssdb
     * 其他业务场景：直接更新mysql和ssdb
     * 1、教师批注业务场景：填空题、简答题、上课、作业
     *    同批改业务相同
     * @param in
     * @return
     * @throws BizLayerException
     */
    UpdatePostilResponse updatePostil(UpdatePostilRequest in)     throws BizLayerException;

    /****
     * 批改
     * 1、上课：更新redis的答题记录缓存，MQ异步写入mysql数据库   其他答题场景：直接更新mysql答题记录表
     * 2、所有答题场景更新ssdb
     * 3、后置业务处理
     * @param in
     * @return
     * @throws BizLayerException
     */
    CommonDes updateCorrect(UpdateCorrectRequest in) throws BizLayerException;

    /**
     * 查询指定学生指定题目的答题记录
     * 查询redis缓存和mysql数据，结果合并。
     * 如果是复合题，查询下面所有小题的答题记录。
     * @param in
     * @return
     * @throws BizLayerException
     */
    StudentExerciseListOut findStuQuesAnswOnBatch(FindStuQuesAnswOnBatchRequest in) throws BizLayerException;

    /**
     * 查询英语作文题指定题的部分学生的答题记录
     * @param in
     * @return
     * @throws BizLayerException
     */
    StudentExerciseListOut findStuListQuesAnsw(FindSomeStuQuesAnswOnBatchRequest in) throws BizLayerException;

    /**
     * 查询指定学生的所有答题记录
     * 查询redis缓存和mysql数据，结果合并。
     * 作业测评预习优先查库，再查redis缓存（缓存是一个学生该resourceId下所有的答题记录）
     * @param in
     * @return
     * @throws BizLayerException
     */
    StudentExerciseListOut findStuAnswsOnBatch(FindStuAnswsOnBatchRequest in) throws BizLayerException;
    /**
     * 查询指定题目的所有答题记录
     * 查询redis缓存和mysql数据，结果合并。
     * 如果是复合题，查询下面所有小题的答题记录。
     * @param in
     * @return
     * @throws BizLayerException
     */
    StudentExerciseListOut findQuesAnswsOnBatch(FindQuesAnswsOnBatchRequest in) throws BizLayerException;

    /***
     *智能批改结果提交
     * @param in
     * @return
     * @throws BizLayerException
     */
    CommonDes intelligenceCorrectAnswer(IntelligenceAnswerRequest in) throws BizLayerException;

    /***
     *智能批改是否存在白名单
     * @param longRequest
     * @return
     * @throws BizLayerException
     */
    CommonResponse<Boolean> intellIsWhiteList(LongRequest longRequest) throws BizLayerException;


    /****
     * 获取智能批改结果
     * @param in
     * @return
     * @throws BizLayerException
     */
    CommonDes submitIntellCorrectAnswer(IntellCorrectStuQueRequest in)throws BizLayerException;


    /***
     * 批改是否重复批改，批改结果变化（错批改为对和对批改为错）数据采集
     * @param in
     * @return
     * @throws BizLayerException
     */
    CorrectCollectionInfoRes correctCollectionInfo(UpdateCorrectRequest in)throws BizLayerException;

    /**
     * 判断当前批改是否为重复批改，批改状态变化
     * @param in
     * @param exitedRecord
     * @return
     * @throws BizLayerException
     */
    CorrectCollectionInfoRes correctCollectionInfo(StudentExercise in, StudentExercise exitedRecord)throws
            BizLayerException;

    /**
     * 判断当前批改是否为重复批改，批改状态变化
     * @param in
     * @param exitedRecord
     * @return
     * @throws BizLayerException
     */
    CorrectInfoRes correctInfo(StudentExercise in, StudentExercise exitedRecord)throws
            BizLayerException;

    /**
     * 智能批改自动生效
     * 判断当前批改是否为重复批改，批改状态变化
     * @param in
     * @param exitedRecord
     * @return
     * @throws BizLayerException
     */
    CorrectInfoRes autoIntellCorrectInfo(StudentExercise in, StudentExercise exitedRecord)throws
            BizLayerException;

    /***
     * 使用智能批改
     * @param in
     * @return
     * @throws BizLayerException
     */
    @Deprecated
    UpdatePostilResponse submitIntellInfo(IntellCorrectStuQueRequest in)throws BizLayerException;


    /**
     * 查询所有学生所有题的做答记录
     * @param request
     * @return
     * @throws BizLayerException
     */
    FindHistoryDataResponse findHistoryDataList(FindHistoryDataRequest request) throws BizLayerException;


    /***
     * 学生做答记录的音频地址由第三方更改为七牛音频地址
     * @param request
     * @return
     * @throws BizLayerException
     */
    CommonDes updateAudioAnswer(AudioUrlUpdateRequest request)throws BizLayerException;

    /***
     * 求助批改
     * @param in
     * @return
     * @throws BizLayerException
     */
    FlowTurnOut findFlowTurnList(FlowTurnCorrectRequest in) throws BizLayerException;

    /***
     * 求助批改 - 批改查询
     * @param in
     * @return
     * @throws BizLayerException
     */
    FlowTurnListOut findFlowTurnStuAnswsOnBatch(FlowTurn in) throws BizLayerException;
}
