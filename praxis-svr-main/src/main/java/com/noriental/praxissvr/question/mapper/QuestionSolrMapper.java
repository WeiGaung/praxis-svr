package com.noriental.praxissvr.question.mapper;

import com.noriental.praxissvr.question.bean.EntityQuestion;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface QuestionSolrMapper {

    List<EntityQuestion> selectById(Long id);

    List<Map<String, Object>> selectByQuestionIdList(@Param("list") List<Long> idList);

}