package com.noriental.praxissvr.brush.service;

import com.noriental.praxissvr.answer.request.CreateRecordsRequest;
import com.noriental.praxissvr.brush.bean.StudentWork;
import com.noriental.praxissvr.brush.request.*;
import com.noriental.praxissvr.brush.response.*;
import com.noriental.praxissvr.answer.request.StudentExerciseIn;
import com.noriental.validate.bean.CommonDes;
import com.noriental.validate.exception.BizLayerException;

/**
 * 学生刷体
 *
 * @author bluesky
 */
public interface StuBrushService {

    StudentWorkOut getWork(StudentWorkIn in) throws BizLayerException;

    @Deprecated
    StudentWorkPageOutput findWorksPage(StudentWorkPageInput studentWorkPageInput) throws BizLayerException;
    @Deprecated
    CreateEnchanceResponse createEnchance(CreateEnchanceRequest in);

    /**
     * 获取单个 复习（学生作业记录表对象）
     */
    StudentWork findById(long id);

    void updateWorkStatus(String exerciseSource, Long resourceId);

    /***
     * 创建学生题集
     * 中间层调用  提交答案前调用，在entity_student_work 插入数据，用作习题记录分表
     * @param request
     * @return
     * @throws BizLayerException
     */
    CreateStudentWorkResponse createStudentWork(CreateStudentWorkRequest request) throws BizLayerException;

    /**
     * 创建刷题答题记录：刷题调用
     *
     * @param createRecordsRequest
     * @return
     * @throws BizLayerException
     */
    CommonDes createBrushRecords(CreateRecordsRequest createRecordsRequest) throws BizLayerException;

    /**
     * 刷题日志打印功能
     *
     * @param exerciseSource
     * @param resourceId
     */
    void setLogger(String exerciseSource, Long resourceId);
}
