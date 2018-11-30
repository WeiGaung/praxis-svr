package com.noriental.praxissvr.question.response;

import com.noriental.praxissvr.question.bean.QuestionTopicVo;
import com.noriental.validate.bean.CommonDes;

import java.util.List;
import java.util.Map;

/**
 * Created by chenlihua on 2016/9/23.
 * praxis-svr
 */
public class FindQuestionKnowledgeByIdsResponse extends CommonDes {

    private Map<Long, List<QuestionTopicVo>> questionTopicMap;

    public Map<Long, List<QuestionTopicVo>> getQuestionTopicMap() {
        return questionTopicMap;
    }

    public void setQuestionTopicMap(Map<Long, List<QuestionTopicVo>> questionTopicMap) {
        this.questionTopicMap = questionTopicMap;
    }
}
