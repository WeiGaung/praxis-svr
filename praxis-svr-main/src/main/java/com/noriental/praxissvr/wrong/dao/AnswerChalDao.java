package com.noriental.praxissvr.wrong.dao;

import com.noriental.dao.BaseDao;
import com.noriental.praxissvr.wrong.bean.AnswerChal;

import java.util.List;

/**
 * Created by bluesky on 2016/7/5.
 */
@Deprecated
public interface AnswerChalDao extends BaseDao<AnswerChal,Long> {
    List<AnswerChal> findByAnswerChal(AnswerChal answerChal);
    void creates(List<AnswerChal> answerChals);
    void deleteAnswerChal(AnswerChal answerChal);
}
