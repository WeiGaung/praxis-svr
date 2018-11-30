package com.noriental.praxissvr.wrongQuestion.mappers;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.noriental.praxissvr.answer.bean.StudentExercise;
import com.noriental.praxissvr.wrong.bean.StuQuesKnowledge;
import com.noriental.praxissvr.wrongQuestion.bean.WrongQuesChapterEntity;
import com.noriental.praxissvr.wrongQuestion.bean.WrongQuesListEntity;
import com.noriental.praxissvr.wrongQuestion.bean.WrongQuesSubjectStatisEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by kate on 2016/12/19.
 */
@Repository
public interface WrongQuestionMapper {

    /***
     * 根据学科分类获取某学生所有学科错题数统计
     * @param map
     * @return
     */
    List<WrongQuesSubjectStatisEntity> findWrongQuesSubjectStatis(Map map);


    /***
     * 根据分册章节知识点查询某学生某学科某章节错题分页列表
     * @param map
     * @return
     */
    // List<WrongQuesChapterEntity> findWrongQuesChapQuery(Map map);

    PageList<WrongQuesChapterEntity> findWrongQuesChapQuery(Map map, PageBounds pageBounds);

    int findWrongQuesChapNum(Map map);

    int findRealWrongQuesChapNum(Map map);

    PageList<WrongQuesChapterEntity> findRealWrongQuesChapQuery(Map map, PageBounds pageBounds);

    /***
     * 根据学生ID、题目ID查询某道题错题次数
     * @param map
     * @return
     */
    List<WrongQuesSubjectStatisEntity> findChapterNum(Map map);

    /***
     * 根据学生ID、题目ID查询单题的所有作答记录
     * @param map
     * @return
     */
    List<StudentExercise> findWrongQuestionHis(Map map);

    /***
     * 根据学生ID、题目ID查询复合题的所有作答记录
     * @param map
     * @return
     */
    List<StudentExercise> findWrongSingleQuestionHis(Map map);


    /***
     * 根据学生ID、知识点或章节查询学生的错题
     * @param map
     * @return
     */

    List<WrongQuesListEntity> findWrongQuestions(Map map);

    /***
     * 根据学生ID、知识点或章节查询学生的错题
     * @param map
     * @return
     */

    List<WrongQuesListEntity> findWrongParentQuestions(Map map);

    /***
     * 批量更新学生做答错题批改为对的状态
     * @param list
     * @return
     */
    int updateStatusList(List<StuQuesKnowledge> list);
    /***
     * 批量更新学生做答错题状态
     * @param list
     * @return
     */
    int updateKnowledgeStatusList(List<StuQuesKnowledge> list);

    /***
     * 查询知识点、章节错题个数
     * @param map
     * @return
     */
    List<WrongQuesChapterEntity> findWrongQuestionsNum(Map map);


    /**
     * 根据知识点章节查询错题ids
     * @param map
     * @return
     */

    List<StuQuesKnowledge> findKnowledgeQuestionIds(Map map);


    /***
     * 根据学生ID和习题ID查询错题记录
     * @param map
     * @return
     */
    List<StudentExercise> findQuestionsByIds(Map map);


    /**
     * 更新错题本知识点、章节错题批改为对的状态
     * @param map
     * @return
     */
    int updateKnowledgeStatu(Map map);

}
