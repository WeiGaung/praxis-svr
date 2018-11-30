package com.noriental.praxissvr.question.mapper;

import com.noriental.praxissvr.question.bean.EntityCounterResources;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by liujiang on 2018/4/9.
 * mybatis数据库操作接口
 */
@Repository
public interface EntityCounterResMapper {

    /**
     * 查询题目收藏量
     *
     */
    List<EntityCounterResources> find(@Param("list")List<Long> questionIds);

    int updateCounter(@Param("favCount")long favCount,@Param("questionId")long questionId);
}
