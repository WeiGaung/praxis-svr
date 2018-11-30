package com.noriental.praxissvr.statis.service;


import com.noriental.praxissvr.statis.bean.AnswAndResultSatis;
import com.noriental.praxissvr.statis.request.FindStuWorkSatisRequest;
import com.noriental.praxissvr.statis.request.StatisLevelsRequest;
import com.noriental.praxissvr.statis.response.FindStuWorkSatisResponse;
import com.noriental.praxissvr.statis.bean.StuWorkStatis;
import com.noriental.validate.bean.CommonResponse;
import com.noriental.validate.exception.BizLayerException;

import java.util.List;

public interface StuWorkStatisService {

    /**
     * 3.2 作业知识点接口
     * 获得学生作业题目统计信息 模块统计 单元统计 知识点统计 优先从缓存读取。
     *
     * @param stuType
     * @param stuId
     */
    @Deprecated
    List<StuWorkStatis> findStuWorkSatis(Integer stuType, Long stuId, Long subjectId)  throws BizLayerException;

    /***
     * 获得指定知识点答题数量
     * @param request
     * @return
     */
    @Deprecated
    CommonResponse<List<StuWorkStatis>> findStuWorkSatisByIds(StatisLevelsRequest request);

    /**
     * 获得学生科目下所有已作答知识点的答题数量和答对数量。
     * 优先从缓存读取。
     * @param request
     * @return
     * @throws BizLayerException
     */
    @Deprecated
    FindStuWorkSatisResponse findStuWorkSatis(FindStuWorkSatisRequest request)  throws BizLayerException;;

    @Deprecated
    void updateStuWorkSatisList(List<AnswAndResultSatis> countSatises);
}
