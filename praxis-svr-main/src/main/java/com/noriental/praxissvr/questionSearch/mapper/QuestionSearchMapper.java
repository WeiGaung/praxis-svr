package com.noriental.praxissvr.questionSearch.mapper;

import com.noriental.praxissvr.question.bean.Question;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by kate on 2017/8/8.
 */
@Repository
public interface QuestionSearchMapper {

    List<Question> getQuesListByIds(List<Long> ids);

}
