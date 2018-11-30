package com.noriental.praxissvr.question.response;

import com.noriental.praxissvr.question.bean.questionpart.QuestionStateResponse;
import com.noriental.validate.bean.CommonDes;

import java.util.List;

/**
 * Created by liujiang on 2018/5/17.
 */
public class FindQuestionsStateResponse extends CommonDes {
    private List<QuestionStateResponse> questionStateResponseList;

    public List<QuestionStateResponse> getQuestionStateResponseList() {
        return questionStateResponseList;
    }

    public void setQuestionStateResponseList(List<QuestionStateResponse> questionStateResponseList) {
        this.questionStateResponseList = questionStateResponseList;
    }

    @Override
    public String toString() {
        return "FindQuestionsStateResponse{" +
                "questionStateResponseList=" + questionStateResponseList +
                '}';
    }
}
