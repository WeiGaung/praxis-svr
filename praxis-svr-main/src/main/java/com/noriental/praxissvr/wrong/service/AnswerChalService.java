package com.noriental.praxissvr.wrong.service;

import com.noriental.praxissvr.answer.bean.StudentExercise;
import com.noriental.praxissvr.wrong.vo.GetParentBatchIdReturn;
import com.noriental.praxissvr.wrong.vo.GetsonBatchIdParam;
import com.noriental.praxissvr.wrong.vo.GetsonBatchIdsParam;
import com.noriental.praxissvr.wrong.vo.OperateType;

import java.util.List;
import java.util.Map;

/**
 * Created by bluesky on 2016/7/5.
 */
public interface AnswerChalService {

    Long getSonBatchId(GetsonBatchIdParam param);

    List<Long> getSonBatchIds(GetsonBatchIdsParam param);


//    GetParentBatchIdReturn getParentBatchId(Long sonBatchId);

    /**
     * 创建消灭错题（预习，上课，作业，测评做错进入消灭错题；错题消灭做错，生成新的错题消灭）
     * @param seList
     * @param operateType
     */
    void createAnswerChals(List<StudentExercise> seList, OperateType operateType);

}
