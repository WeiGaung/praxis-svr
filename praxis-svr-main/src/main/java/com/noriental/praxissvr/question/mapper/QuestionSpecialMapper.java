package com.noriental.praxissvr.question.mapper;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.noriental.praxissvr.question.bean.EntityQuestionSpecial;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by liujiang on 2018/4/25.
 * mybatis数据库操作接口
 */
@Repository
public interface QuestionSpecialMapper {

    /**
     * 根据题目ID查询所属专题
     *
     */
    PageList<EntityQuestionSpecial> queryQuestionSpecialList(@Param("questionId") Long questionId, PageBounds pageBounds);

    /**
     * 创建题目挂架专题
     * @param entityQuestionSpecial
     * @return
     */
    int createQuestionSpecial(EntityQuestionSpecial entityQuestionSpecial);

    /**
     * 批量创建题目专题信息
     * @param entityQuestionSpecialList
     * @return
     */
    int batchInsertQuestionSpecial(List<EntityQuestionSpecial> entityQuestionSpecialList);

    /**
     *通过题目ID删除题目挂架的专题信息
     * @param questionId
     * @return
     */
    int deleteQuestionSpecialById(Long questionId);
}
