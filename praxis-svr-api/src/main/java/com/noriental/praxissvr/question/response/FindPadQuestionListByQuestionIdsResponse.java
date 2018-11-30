package com.noriental.praxissvr.question.response;

import com.noriental.praxissvr.question.bean.SuperQuestion;
import com.noriental.validate.bean.CommonDes;

import java.util.List;

/**
 * @author chenlihua
 * @date 2015/11/30
 * @time 14:35
 */
public class FindPadQuestionListByQuestionIdsResponse extends CommonDes {

    private List<SuperQuestion> questionList;

    public List<SuperQuestion> getQuestionList() {
        return questionList;
    }

    public void setQuestionList(List<SuperQuestion> questionList) {
        this.questionList = questionList;
    }

}
