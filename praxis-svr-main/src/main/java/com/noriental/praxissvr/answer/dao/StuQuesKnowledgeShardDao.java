package com.noriental.praxissvr.answer.dao;

import com.noriental.praxissvr.common.TrailBaseErrorRequestRequest;
import com.noriental.praxissvr.wrong.bean.StuQuesKnowledge;

import java.util.List;

public interface StuQuesKnowledgeShardDao {
    /**
     * 查找 错本的列表
     */
    List<StuQuesKnowledge> findStuQuesKnowledgeInfo(TrailBaseErrorRequestRequest request);

    List<StuQuesKnowledge> findByStuQuesKnow(StuQuesKnowledge stuQuesKnowledge);

    void deleteByStuQuesKnow(StuQuesKnowledge stuQuesKnowledge);

    /**
     * 批量查询
     *
     * @param stuQuesKnowledges
     * @return
     */
    List<StuQuesKnowledge> findByIdses(List<StuQuesKnowledge> stuQuesKnowledges);

    /**
     * 批量插入
     *
     * @return
     */
    boolean creates(List<StuQuesKnowledge> stuQuesKnowledges);

    /**
     * 删除错题
     *
     * @param stuQuesKnowledge
     * @return
     */
    boolean deleteWrongQues(StuQuesKnowledge stuQuesKnowledge);

    /**
     * 查询学生某个科目下的错题
     *
     * @param stuQuesKnowledge
     * @return
     */
    List<StuQuesKnowledge> findStuQues(StuQuesKnowledge stuQuesKnowledge);

    /**
     * 查询学生某个知识点下的错题
     *
     * @return
     */
    // PageResult<StuQuesKnowledge> findLeveledStuQues(Map<String, Object> params);

    //没有调用
    boolean deleteByStuQuesKnowledges(List<StuQuesKnowledge> stuQuesKnowledges);

    boolean deleteByIds(StuQuesKnowledge stuQuesKnowledge);

    /***
     * 多条件查询学生习题答题信息
     * @param stuQuesKnowledge
     * @return
     */
    List<StuQuesKnowledge> findStuQuesKnowledge(StuQuesKnowledge stuQuesKnowledge);

    int updateStatusList(StuQuesKnowledge stuQuesKnowledges);

    int updateKnowledgeStatusList(StuQuesKnowledge stuQuesKnowledges);

}
