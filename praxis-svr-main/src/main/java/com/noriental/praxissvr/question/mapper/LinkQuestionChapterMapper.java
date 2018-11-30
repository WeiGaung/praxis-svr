package com.noriental.praxissvr.question.mapper;

import com.noriental.praxissvr.question.bean.LinkQuestionChapter;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by hushuang on 2016/12/1.
 */
@Repository
public interface LinkQuestionChapterMapper {

    /**
     * 创建题目章节关联
     * @param questionId
     * @param chapterId
     * @return
     */
    int createLinkQuestionChapter(@Param("questionId") long questionId,@Param("chapterId") long chapterId);

    /**
     * 批量插入
     * @param map
     * @return
     */
    int batchLinkQuestionChapter(List<Map<String,Object>> map);


    /**
     * 通过习题ID删除章节关联
     * @param questionId
     * @return
     */
    int deleteLinkQuestionChapterById(Long questionId);

    /**
     * 通过习题ID查询章节
     * @param questionId
     * @return
     */
    List<LinkQuestionChapter> findLinkQuestionChapterById(Long questionId);

}
