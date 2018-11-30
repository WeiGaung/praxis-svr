package com.noriental.praxissvr.question;

import com.noriental.praxissvr.question.bean.QuestionSsdbContentHtml;

/**
 * Created by chenlihua on 2016/12/17.
 * praxis-svr
 */
public class QuestionSsdbHtml {
    private String questionId;
    private QuestionSsdbContentHtml content;

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public QuestionSsdbContentHtml getContent() {
        return content;
    }

    public void setContent(QuestionSsdbContentHtml content) {
        this.content = content;
    }
}
