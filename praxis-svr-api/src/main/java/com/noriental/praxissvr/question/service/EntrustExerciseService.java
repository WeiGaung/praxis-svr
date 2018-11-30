package com.noriental.praxissvr.question.service;

import com.noriental.praxissvr.common.PageBaseResponse;
import com.noriental.praxissvr.question.bean.EntrustUpload;
import com.noriental.praxissvr.question.request.CreateEntrustExerciseRequest;
import com.noriental.praxissvr.question.request.FindEntrustExercisesRequest;
import com.noriental.validate.bean.CommonDes;
import com.noriental.validate.exception.BizLayerException;

/**
 * @author chenlihua
 * @date 2015/12/23
 * @time 11:12
 */
public interface EntrustExerciseService {

//    CommonDes updateEntrustExercise(UpdateEntrustExerciseRequest request) throws BizLayerException;

    PageBaseResponse<EntrustUpload> findEntrustExercises(FindEntrustExercisesRequest request) throws BizLayerException;


    CommonDes createEntrustExercise(CreateEntrustExerciseRequest request) throws BizLayerException;
}
