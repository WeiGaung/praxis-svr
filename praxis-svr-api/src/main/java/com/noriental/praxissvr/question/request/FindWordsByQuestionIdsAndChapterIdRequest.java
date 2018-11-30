package com.noriental.praxissvr.question.request;

import com.noriental.validate.bean.BaseRequest;

import java.util.List;

/**
 * Created by liujiang on 2017/11/14.
 */
public class FindWordsByQuestionIdsAndChapterIdRequest extends BaseRequest {
    private List<Long> questionIds;
    private Long chapterId;

    public FindWordsByQuestionIdsAndChapterIdRequest() {
    }

    public List<Long> getQuestionIds() {
        return questionIds;
    }

    public void setQuestionIds(List<Long> questionIds) {
        this.questionIds = questionIds;
    }

    public Long getChapterId() {
        return chapterId;
    }

    public void setChapterId(Long chapterId) {
        this.chapterId = chapterId;
    }

    @Override
    public String toString() {
        return "FindWordsByQuestionIdsAndChapterIdRequest{" +
                "questionIds=" + questionIds +
                ", chapterId=" + chapterId +
                '}';
    }
}
