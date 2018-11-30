package com.noriental.praxissvr.question.response;

import com.noriental.praxissvr.question.bean.Question;
import com.noriental.validate.bean.CommonDes;

/**
 * @author chenlihua
 * @date 2015/11/30
 * @time 14:35
 */
public class FindQuestionByIdResponse extends CommonDes {
    private Question question;

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }
}
