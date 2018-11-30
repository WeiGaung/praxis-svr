package com.noriental.praxissvr.question.service;

import com.noriental.praxissvr.common.CommonRequest;
import com.noriental.praxissvr.question.bean.TopicQuestion;
import com.noriental.validate.bean.CommonResponse;

import java.util.List;

/**
 * @author chenlihua
 * @date 2016/7/18
 * @time 14:51z
 */
public interface TopicQuestionService {

    /**
     * 根据questionId查询相应的知识点
     * @param request questionId
     * @return topicQuestionList
     */
    CommonResponse<List<TopicQuestion>> getTopicQuestionsByQuestionId(CommonRequest<Long> request);

}
