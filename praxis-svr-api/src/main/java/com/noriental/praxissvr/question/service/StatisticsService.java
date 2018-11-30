package com.noriental.praxissvr.question.service;


import com.noriental.praxissvr.question.bean.StatisticsVo;
import com.sumory.mybatis.pagination.result.PageResult;

import java.util.List;
import java.util.Map;


public interface StatisticsService {

    /**
     * 上传数量统计
     *
     * @return
     */
    PageResult<StatisticsVo> getUplodCount(Map<String, Object> param);

    /**
     * 审核数量统计
     *
     * @return
     */
    PageResult<StatisticsVo> getEvaledCount(Map<String, Object> param);

    /**
     * 学科题目数量
     *
     * @return
     */
    PageResult<StatisticsVo> getPaperSubjectCount(Map<String, Object> param);

    /**
     * 知识点题目数量
     *
     * @return
     */
    PageResult<StatisticsVo> getKnowledgeSubjectCount(Map<String, Object> param, Boolean isTopic);

    /**
     * 报错题目数量
     *
     * @return
     */
    PageResult<StatisticsVo> getErrorQustCount(Map<String, Object> param);

    /**
     * 查询主题或者专题下难度的题
     *
     * @param param
     * @return
     */
    PageResult<StatisticsVo> getMastery(Map<String, Object> param, Boolean isTopic);

    /**
     * 查询题型下所有的题
     *
     * @param param
     * @return
     */
    List<StatisticsVo> getQuestionByType(Map<String, Object> param);

}
