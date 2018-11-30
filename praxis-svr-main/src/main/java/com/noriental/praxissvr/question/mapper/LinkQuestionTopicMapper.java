package com.noriental.praxissvr.question.mapper;

import com.noriental.praxissvr.question.bean.LinkQuestionTopic;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by hushuang on 2016/12/1.
 */
@Repository
public interface LinkQuestionTopicMapper {

    /**
     * 插入题目知识点主题关联
     * @param questionId
     * @param topicId
     * @return
     */
    public int createQuestionTopicLink(@Param("questionId")long  questionId, @Param("topicId")long topicId);


    /**
     * 批量插入
     * @param list
     * @return
     */
    public int batchQuestionTopicLink(List<Map<String, Object>> list);


    /**
     * 根据questionId删除主题关联
     * @param questionId
     * @return
     */
    public int deleteQuestionTopicLinkById(Long questionId);


    /**
     * 通过questionId获取主题列表
     * @param questionId
     * @return
     */
    public List<LinkQuestionTopic> findLinkQuestionTopicByQuestionId(Long questionId);

}
