package com.noriental.praxissvr.answer.service;

import com.noriental.praxissvr.answer.bean.StudentExercise;
import com.noriental.praxissvr.answer.request.BatchIntellUpdateRequest;
import com.noriental.praxissvr.answer.request.BatchUpdateRequest;
import com.noriental.validate.bean.CommonDes;
import com.noriental.validate.bean.CommonResponse;
import com.noriental.validate.exception.BizLayerException;

import java.util.List;

/**
 * @author kate
 * @create 2017-12-22 16:54
 * @desc 一键批改对外接口
 **/
public interface BatchUpdateCorrectService {

    /***
     * 按人的一键批改、按题的一键批改
     * @param request
     * @return
     * @throws BizLayerException
     */
    CommonDes  batchUpdateCorrect(BatchUpdateRequest request)throws BizLayerException;



    /***
     * 数据流转一键批改
     *  按人
     *  按题
     * @param requestlist
     * @return
     * @throws BizLayerException
     */
    CommonDes  batchUpdateCorrectList(List<BatchUpdateRequest> requestlist)throws BizLayerException;



    /***
     * 按人的一键智能批改、按题的一键智能批改
     * @param request
     * @return
     * @throws BizLayerException
     */
    CommonDes  batchUpdateIntellCorrect(BatchIntellUpdateRequest request)throws BizLayerException;



    /***
     * 数据流转
     * 按人的一键智能批改、按题的一键智能批改
     * @param requestList
     * @return
     * @throws BizLayerException
     */
    CommonDes  batchUpdateIntellCorrectList(List<BatchIntellUpdateRequest> requestList)throws BizLayerException;


    /**
     * 根据答题场景和题集ID查询学生的所有答题记录
     * @param exercise
     * @return
     * @throws BizLayerException
     */
    CommonResponse<List<StudentExercise>> findStudentAnswerList(StudentExercise exercise) throws BizLayerException;


}
