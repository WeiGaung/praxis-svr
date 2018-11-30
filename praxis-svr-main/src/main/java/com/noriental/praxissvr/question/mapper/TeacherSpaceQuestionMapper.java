package com.noriental.praxissvr.question.mapper;

import com.noriental.praxissvr.question.bean.TeacherSpaceQuestion;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by chenlihua on 2016/9/29.
 * praxis-svr
 */
@Repository
public interface TeacherSpaceQuestionMapper {

    List<TeacherSpaceQuestion> findByQuestionIds(@Param("list") List<Long> questionIds);

}
