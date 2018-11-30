package com.noriental.praxissvr.question.response;

import com.noriental.praxissvr.question.bean.QuestionType;
import com.noriental.validate.bean.CommonDes;

/**
 * @author chenlihua
 * @date 2015/12/22
 * @time 15:50
 */
public class FindStructIdByQuesTypeIdResponse extends CommonDes {
    private QuestionType questionType;

    public QuestionType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }
}
