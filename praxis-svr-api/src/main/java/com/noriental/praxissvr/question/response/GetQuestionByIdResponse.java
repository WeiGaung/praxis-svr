package com.noriental.praxissvr.question.response;

import com.noriental.praxissvr.question.bean.QuestionData;
import com.noriental.validate.bean.CommonDes;

/**
 * Created by hushuang on 2016/11/21.
 * 通过ID获取习题
 */
public class GetQuestionByIdResponse extends CommonDes {

    private QuestionData questionData;

    public GetQuestionByIdResponse() {
    }

    public QuestionData getQuestionData() {
        return questionData;
    }

    public void setQuestionData(QuestionData questionData) {
        this.questionData = questionData;
    }

    @Override
    public String toString() {
        return "GetQuestionByIdResponse{" +
                "question1=" + questionData +
                '}';
    }
}
