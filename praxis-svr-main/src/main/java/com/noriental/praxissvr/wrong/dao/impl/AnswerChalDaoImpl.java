package com.noriental.praxissvr.wrong.dao.impl;

import com.noriental.dao.BaseDaoImpl;
import com.noriental.praxissvr.wrong.bean.AnswerChal;
import com.noriental.praxissvr.wrong.dao.AnswerChalDao;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by bluesky on 2016/7/5.
 */
@Repository
public class AnswerChalDaoImpl extends BaseDaoImpl<AnswerChal,Long> implements AnswerChalDao{
    private String namespace = AnswerChal.class.getName();
    @Override
    public List<AnswerChal> findByAnswerChal(AnswerChal answerChal) {
        return this.findList(namespace + ".findByAnswerChal", answerChal);
    }

    @Override
    public void creates(List<AnswerChal> answerChals) {
        this.update(namespace + ".inserts",answerChals);
    }

    @Override
    public void deleteAnswerChal(AnswerChal answerChal) {
        this.delete(namespace + ".deleteAnswerChal", answerChal);
    }
}
