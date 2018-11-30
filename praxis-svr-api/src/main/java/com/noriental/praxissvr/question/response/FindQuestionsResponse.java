package com.noriental.praxissvr.question.response;

import com.noriental.praxissvr.question.bean.Question;
import com.noriental.validate.bean.CommonDes;

import javax.validation.constraints.Min;
import java.util.List;

/**
 * @author chenlihua
 * @date 2015/12/22
 * @time 11:27
 */
public class FindQuestionsResponse extends CommonDes {
    @Min(0)
    private long totalCount;
    @Min(0)
    private long totalPageCount;
    @Min(0)
    private long currentPage;
    private List<Question> questionList;

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public long getTotalPageCount() {
        return totalPageCount;
    }

    public void setTotalPageCount(long totalPageCount) {
        this.totalPageCount = totalPageCount;
    }

    public long getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(long currentPage) {
        this.currentPage = currentPage;
    }

    public List<Question> getQuestionList() {
        return questionList;
    }

    public void setQuestionList(List<Question> questionList) {
        this.questionList = questionList;
    }
}
