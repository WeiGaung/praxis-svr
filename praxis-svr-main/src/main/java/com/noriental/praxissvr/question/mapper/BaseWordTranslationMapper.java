package com.noriental.praxissvr.question.mapper;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.noriental.praxissvr.question.bean.BaseWordTranslation;
import com.noriental.praxissvr.question.bean.LinkWordType;
import com.noriental.praxissvr.question.bean.WordAndChapter;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by hushuang on 2017/7/7.
 */
@Repository
public interface BaseWordTranslationMapper {


    /*
        根据单词ID查询单词列表,批量查询
     */
    PageList<BaseWordTranslation> findWordsByIds(@Param("wordIds") List<Long> wordIds, PageBounds pageBounds);

    /*
        根据章节ID查询章节列表
     */
    PageList<BaseWordTranslation> findWordTranslationsByChpterId(@Param("chapterId") Long chapterId,PageBounds pageBounds);

    /*
        根据题目类型IDs和单词IDs查询题目信息
     */
    PageList<LinkWordType> findWordTypeByTypesAndWords(@Param("directoryId")Long directoryId, @Param("chapterId") Long chapterId,@Param("versionId") Long versionId, @Param("typeIds") List<Integer> typeIds, @Param("wordIds") List<Long> wordIds, PageBounds pageBounds);

    PageList<WordAndChapter> findWordsByQuestionIdsAndChapterId(@Param("questionIds") List<Long> questionIds, @Param("chapterId") Long chapterId,PageBounds pageBounds);
}
