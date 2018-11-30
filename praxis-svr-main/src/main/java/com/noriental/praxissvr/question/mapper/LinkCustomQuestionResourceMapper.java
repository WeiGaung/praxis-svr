package com.noriental.praxissvr.question.mapper;

import com.noriental.praxissvr.question.bean.CustomQuestionResource;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by hushuang on 2017/7/11.
 */
@Repository
public interface LinkCustomQuestionResourceMapper {


    /*
        1.创建自定义目录和收藏题目走同一个接口
        什么时候收藏接口走这个：
        当此题目的自定义目录不存在时走此接口
        如果此题目的自定义目录存在，直接收藏，不走此接口，直接走收藏接口
     */
    int insertLinkCustomQuestionResource(CustomQuestionResource customQuestionResource);

    /*
        根据题目ID和用户ID查询题目自定义目录关联关系
     */
    CustomQuestionResource queryCustomQuestionResourceByQuesIdAndSysId(@Param("questionId") Long questionId, @Param("systemId") Long systemId);

    /*
        根据题目ID，自定义目录ID,用户ID查询此题目是否存在
     */
    CustomQuestionResource queryCustomQuestionBySysIdAndCusDirIdAndQuestId(@Param("questionId") Long questionId,@Param("systemId") Long systemId,@Param("cusDirId") Long cusDirId);

    /*
        根据题目ID更新自定义目录和题目收藏关系,当收藏题目时，如果题目存在自定义目录章节知识点关系，直接更新
     */
    int updateLinkCustomQuestionResource(CustomQuestionResource customQuestionResource);


    /*
        根据自定义目录ID查询题目资源是否存在
     */
    List<CustomQuestionResource> queryCustomQuestionResourceByCusDirId(@Param("systemId") Long systemId, @Param("cusDirId") Long cusDirId);

    /*
        更新自定义目录
     */
    int updateLinkCustomDirectoryBySysIdAndCusDirId(@Param("oldCusDirId") Long oldCusDirId,@Param("cusDirId") Long cusDirId,@Param("systemId") Long systemId,@Param("groupId") Long groupId);


    /*
        删除自定义目录
     */
    int deleteCustomDiritory(@Param("questionId") Long questionId, @Param("systemId") Long systemId);

    /*
        删除自定义目录和题目的关联
     */
    int deleteLinkCusDirQuestion(@Param("cusDirId") Long cusDirId, @Param("systemId") Long systemId,@Param("questionId") Long questionId);

    /*
        批量插入
     */
    int batchInsertLinkCustomQuestionResource(List<CustomQuestionResource> customQuestionResources);

    /*
        批量更新
     */
    int batchUpdateLinkCustomQuestionResource(List<CustomQuestionResource> customQuestionResources);

    /*
        查询自定义目录题目关系表
     */
    List<Long> findLinkCustomQuestionResource(@Param("systemId") Long systemId, @Param("catalogGroupId") Long catalogGroupId,@Param("isCustomListid") Integer isCustomListid,@Param("level") Integer level,@Param("catalogId") Long catalogId,@Param("catalogIdFirst") Long catalogIdFirst,@Param("catalogIdSecond") Long catalogIdSecond,@Param("customListId") List<Long> customListId);

}
