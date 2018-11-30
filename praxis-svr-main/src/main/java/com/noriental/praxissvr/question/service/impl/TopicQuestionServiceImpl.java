package com.noriental.praxissvr.question.service.impl;

import com.noriental.adminsvr.bean.knowledge.Topic;
import com.noriental.adminsvr.request.RequestEntity;
import com.noriental.adminsvr.response.ResponseEntity;
import com.noriental.adminsvr.service.knowledge.TopicService;
import com.noriental.praxissvr.common.CommonRequest;
import com.noriental.praxissvr.question.bean.QuestionTopic;
import com.noriental.praxissvr.question.bean.TopicQuestion;
import com.noriental.praxissvr.question.dao.QuestionTopicDao;
import com.noriental.praxissvr.question.service.TopicQuestionService;
import com.noriental.validate.bean.CommonResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chenlihua
 * @date 2016/7/18
 * @time 14:53
 */
@Service("topicQuestionService")
public class TopicQuestionServiceImpl implements TopicQuestionService {

    @Resource
    private QuestionTopicDao questionTopicDao;

    @Resource
    private TopicService topicService;

    @Override
    public CommonResponse<List<TopicQuestion>> getTopicQuestionsByQuestionId(CommonRequest<Long> questionId) {
        List<QuestionTopic> questionTopicList = questionTopicDao.findByQuestionId(questionId.getRequest());
        List<TopicQuestion> tqList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(questionTopicList)) {
            List<Long> topicIds = new ArrayList<>();
            for (QuestionTopic questionTopic : questionTopicList) {
                Long topicId = questionTopic.getTopicId();
                topicIds.add(topicId);
            }
            if (CollectionUtils.isNotEmpty(topicIds)) {
                ResponseEntity<List<Topic>> topicList = topicService.findByIds(new RequestEntity<>(topicIds));
                Map<Long, Topic> topicMap = new HashMap<>();
                for (Topic topic : topicList.getEntity()) {
                    topicMap.put(topic.getId(), topic);
                }
                for (QuestionTopic questionTopic : questionTopicList) {
                    Long topicId = questionTopic.getTopicId();
                    Topic entity = topicMap.get(topicId);
                    entity.setMastery(questionTopic.getMastery());
                    TopicQuestion topicQuestion = new TopicQuestion();
                    BeanUtils.copyProperties(questionTopic, topicQuestion);
                    topicQuestion.setTopic(entity);
                    tqList.add(topicQuestion);
                }
            }

        }
        return CommonResponse.success(tqList);
    }
}
