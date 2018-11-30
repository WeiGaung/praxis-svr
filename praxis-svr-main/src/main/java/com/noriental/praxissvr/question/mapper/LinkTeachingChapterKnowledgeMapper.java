package com.noriental.praxissvr.question.mapper;

import com.noriental.praxissvr.question.bean.LinkTeachingChapterKnowledge;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by hushuang on 2016/12/1.
 */
@Repository
public interface LinkTeachingChapterKnowledgeMapper {
    /**
     * 根据主题ID获取教材章节知识点主题列表
     * @param topicId
     * @return
     */
    public List<LinkTeachingChapterKnowledge> findLinkTeachingChapterKnowledgeByTopicId(long topicId);
}
