package com.noriental.praxissvr.statis.statismapper;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.noriental.praxissvr.statis.bean.ClassWrongQuesCount;
import com.noriental.praxissvr.statis.bean.DataWrongQuesVo;
import com.noriental.praxissvr.statis.bean.KnowledgeVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeacherWebWrongQuesMapper {

    PageList<ClassWrongQuesCount> findClassWrongQuesCount(DataWrongQuesVo vo, PageBounds pageBounds);

    // 知识点id 知识点级别 知识点名称 知识点父id     entity_data_wrong_ques_knom 班级学科错题知识点展示表
    List<KnowledgeVo> findKnowledgeList(@Param("classIdList") List<Long> classIdList, @Param("subjectId") Long
            subjectId, @Param("knowledgeLevel") Integer knowledgeLevel, @Param("parKnowledgeId") Long parKnowledgeId);

    // entity_data_wrong_ques   班级科目错题次数统计 link_data_wrong_knowledge 题目知识点关联表
    List<Long> findStudentIds(DataWrongQuesVo vo);

}
