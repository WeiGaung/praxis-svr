package com.noriental.praxissvr.question.mapper;

import com.noriental.praxissvr.question.bean.EntityTeachingChapter;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by hushuang on 2017/4/11.
 */
@Repository
public interface TeacherChapterMapper {

    /**
     * 通过章节ID查询章节全部级联信息
     * @param chapterId chapterId
     * @return
     */
    List<EntityTeachingChapter> findChaptersById(long chapterId);


    Map<String,Object> findVersionByDirectoryId(Long directoryId);



}
