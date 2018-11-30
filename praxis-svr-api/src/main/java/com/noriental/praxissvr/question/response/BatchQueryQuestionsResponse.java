package com.noriental.praxissvr.question.response;

import com.noriental.praxissvr.question.bean.QuestionBean;
import com.noriental.validate.bean.CommonDes;

import java.util.List;

/**
 * Created by hushuang on 2017/4/7.
 */
public class BatchQueryQuestionsResponse extends CommonDes {

    private List<QuestionBean> questionBeanList;

    public BatchQueryQuestionsResponse() {
    }

    public List<QuestionBean> getQuestionBeanList() {
        return questionBeanList;
    }

    public void setQuestionBeanList(List<QuestionBean> questionBeanList) {
        this.questionBeanList = questionBeanList;
    }

    @Override
    public String toString() {
        return "BatchQueryQuestionsResponse{" +
                "questionBeanList=" + questionBeanList +
                '}';
    }
}
