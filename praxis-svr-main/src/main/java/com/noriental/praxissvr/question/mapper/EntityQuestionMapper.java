package com.noriental.praxissvr.question.mapper;

import com.hyd.ssdb.util.Str;
import com.noriental.praxissvr.question.bean.EntityQuestion;
import com.noriental.praxissvr.question.request.UpdateAudioInfoQuestionRequest;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by hushuang on 2016/11/21.
 * mybatis数据库操作接口
 */
@Repository
public interface EntityQuestionMapper {


    /**
     * 添加习题
     * @param entityQuestion
     * @return
     */
    int createQuestion(EntityQuestion entityQuestion);

    /**
     * 通过ID查询习题
     * @param id
     * @return
     */
    EntityQuestion findQuestionById(Long id);

    /**
     * 通过ID删除习题非物理删除，更新给定字段，为不可见
     * @param id
     * @return
     */
    int deleteQuestion(Long id);

    /**
     * 更新习题
     * @param entityQuestion
     * @return
     */
    int updateQuestion(EntityQuestion entityQuestion);

    /**
     * 更新子题为不可见通过大题ID
     * @param parentQuestionId
     * @return
     */
    int deleteSubjectQuestionById(Long parentQuestionId);


    int updateAudioInfoQuestion(UpdateAudioInfoQuestionRequest entity);


    /**
     * 习题批量插入
     * @param entityQuestion
     * @return
     */
    int batchInsertQuestion(List<EntityQuestion> entityQuestion);


    /**
     * 通过大题ID更新习题state
     * @param parent_question_id
     * @return
     */
    int updateEntityQuestionState(Long parent_question_id);

    /**
     * 通过习题ID获取子题ID
     * @param parentId
     * @return
     */
    List<Long> findQuestionSubjIdByParentId(Long parentId);

    /**
     * 通过用户ID和习题ID查询当前用户当前组的题目信列表信息
     * @param uploadId
     * @param groupId
     * @return
     */
    List<EntityQuestion> findQuestinByUploadIdAndGroupId(@Param("uploadId") Long uploadId, @Param("groupId") Long groupId);


    /**
     *
     * @param parentId
     * @return
     */
    List<Long> findQuestionSubjIdByParentIdAndVisible(Long parentId);

    /**
     * 根据大题ID，删除该大题下的所有子题
     * @param parentId
     * @return
     */
    int deleteQuestionByParentId(Long parentId);

    /**
     * 批量更新status DISABLE
     * @param questionIds q
     * @return
     */
    int batchUpdateQuestionStatusDISABLE(List<Long> questionIds);

    /**
     * 批量更新status ABLE
     * @param questionIds q
     * @return
     */
    int batchUpdateQuestionStatusABLE(List<Long> questionIds);

    /**
     * 通过题目questionIds批量获取基本的题目信息
     * @param questionIds ids
     * @return
     */

    List<EntityQuestion> batchQueryQuestionsByIds(List<Long> questionIds);

    /**
     * 通过id和ParentId查询出符合题目的所有结果集---为了满足只有一个题目id的情况下查询题目的答案（复合题）
     * @param questionIds
     * @return
     */
    List<EntityQuestion> batchQueryQuestionsByIdsAndParentIds(List<Long> questionIds);


    int updateStateByIds(@Param("list") List<Long> ids, @Param("state") String state);

    /**
     * 更新音频数据
     * @param audioMap audioMap
     * @return
     */
    int updateAudioData(Map<String,Object> audioMap);


    List<EntityQuestion> getQuestionList(@Param("question_type_id_a") int question_type_id_a,@Param("question_type_id_b") int question_type_id_b);


}
