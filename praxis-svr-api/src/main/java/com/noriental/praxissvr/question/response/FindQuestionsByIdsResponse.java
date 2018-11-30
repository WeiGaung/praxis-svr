package com.noriental.praxissvr.question.response;

import com.noriental.praxissvr.question.bean.Question;
import com.noriental.validate.bean.CommonDes;

import java.util.List;

/**
 * @author chenlihua
 * @date 2015/11/30
 * @time 18:12
 */
public class FindQuestionsByIdsResponse extends CommonDes {
    private List<Question> questionList;

    public List<Question> getQuestionList() {
        return questionList;
    }

    public void setQuestionList(List<Question> questionList) {
        this.questionList = questionList;
    }

}
