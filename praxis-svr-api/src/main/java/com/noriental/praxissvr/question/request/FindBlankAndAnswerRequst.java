package com.noriental.praxissvr.question.request;

import com.noriental.praxissvr.question.bean.BlankAndAnswer;
import com.noriental.validate.bean.BaseRequest;

import java.util.List;

/**
 * Created by hushuang on 2017/3/14.
 */
public class FindBlankAndAnswerRequst extends BaseRequest {
    private List<BlankAndAnswer> blankAndAnswers;

    public FindBlankAndAnswerRequst() {
    }

    public List<BlankAndAnswer> getBlankAndAnswers() {
        return blankAndAnswers;
    }

    public void setBlankAndAnswers(List<BlankAndAnswer> blankAndAnswers) {
        this.blankAndAnswers = blankAndAnswers;
    }

    @Override
    public String toString() {
        return "FindBlankAndAnswerRequst{" +
                "blankAndAnswers=" + blankAndAnswers +
                '}';
    }
}
