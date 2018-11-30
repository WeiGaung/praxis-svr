package com.noriental.praxissvr.question.mapper;

import com.noriental.praxissvr.question.bean.EntityQuestion;
import com.noriental.praxissvr.question.request.UpdateAudioInfoQuestionRequest;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by liujiang on 2018/4/9.
 * mybatis数据库操作接口
 */
@Repository
public interface AuditsedSchoolMapper {

    /**
     * 查询白名单学校
     *
     */
    List<Long> queryAuditsedSchoolList();

}
