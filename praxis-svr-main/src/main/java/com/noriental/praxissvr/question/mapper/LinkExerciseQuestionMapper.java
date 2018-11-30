package com.noriental.praxissvr.question.mapper;

import com.noriental.praxissvr.question.bean.LinkExerciseQuestion;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by hushuang on 2016/11/25.
 */
@Repository
public interface LinkExerciseQuestionMapper {
    /**
     * 通过ID查询题集习题
     * @param id
     * @return
     */
    List<LinkExerciseQuestion> findLinkExerciseQuestionById(long id);
}
