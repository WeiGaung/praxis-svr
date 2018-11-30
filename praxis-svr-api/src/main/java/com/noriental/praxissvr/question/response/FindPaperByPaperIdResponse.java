package com.noriental.praxissvr.question.response;

import com.noriental.praxissvr.question.bean.Question;
import com.noriental.validate.bean.CommonDes;

import javax.validation.constraints.Min;
import java.util.List;

/**
 * @author chenlihua
 * @date 2015/11/30
 * @time 14:35
 */
public class FindPaperByPaperIdResponse extends CommonDes {
    @Min(value = 1)
    private long paperId;

    private List<Question> questionList;

    public long getPaperId() {
        return paperId;
    }

    public void setPaperId(long paperId) {
        this.paperId = paperId;
    }

    public List<Question> getQuestionList() {
        return questionList;
    }

    public void setQuestionList(List<Question> questionList) {
        this.questionList = questionList;
    }

}
