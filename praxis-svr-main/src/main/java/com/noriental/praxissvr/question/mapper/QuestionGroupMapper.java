package com.noriental.praxissvr.question.mapper;

import com.noriental.praxissvr.question.bean.Group;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
@Repository
public interface QuestionGroupMapper{

    Group findById(@Param("id") long id);

    List<Group> findBySystemId(@Param("systemId") long systemId);

    List<Group> findByIds(@Param("list") Set<Long> ids);

}
