package com.noriental.praxissvr.question.dao;

import com.noriental.dao.BaseDao;
import com.noriental.praxissvr.question.bean.QuestionTopic;

import java.util.List;

/**
 * 试题主题关联信息
 *
 * @author xiangfei
 * @date 2015年10月29日 上午10:17:21
 */
public interface QuestionTopicDao extends BaseDao<QuestionTopic, Long> {

    List<QuestionTopic> findByQuestionIds(List<Long> questionIds);

    List<QuestionTopic> findByQuestionId(long id);
}
