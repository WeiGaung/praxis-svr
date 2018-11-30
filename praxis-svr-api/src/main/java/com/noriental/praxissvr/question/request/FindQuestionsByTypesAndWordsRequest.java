package com.noriental.praxissvr.question.request;

import com.noriental.praxissvr.question.bean.WordAndChapter;
import com.noriental.validate.bean.BaseRequest;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by hushuang on 2017/7/6.
 * 通过题型IDS和单词IDS进行查询题目列表
 */
public class FindQuestionsByTypesAndWordsRequest extends BaseRequest {

    /**
     * 题型IDS
     */
    @NotNull
    private List<Integer> questionTypeIds;
    /**
     * 单词IDS
     */
    private List<Long> wordIds;

    @Override
    public String toString() {
        return "FindQuestionsByTypesAndWordsRequest{" +
                "questionTypeIds=" + questionTypeIds +
                ", wordIds=" + wordIds +
                ", wordAndChapters=" + wordAndChapters +
                ", chapterId=" + chapterId +
                ", versionId=" + versionId +
                ", directoryId=" + directoryId +
                ", pageNumber=" + pageNumber +
                ", pageSize=" + pageSize +
                '}';
    }

    /**
     * 聯合查詢
     */
    private List<WordAndChapter> wordAndChapters;

    public List<WordAndChapter> getWordAndChapters() {
        return wordAndChapters;
    }

    public void setWordAndChapters(List<WordAndChapter> wordAndChapters) {
        this.wordAndChapters = wordAndChapters;
    }

    /**
     * 章节ID
     */
    private Long chapterId;
    /**
     * 版本ID
     */
    private Long versionId;
    /**
     * 分册Id
     */
    private Long directoryId;

    /**
     * 当前页
     */
    private int pageNumber;
    /**
     * 页容量
     */
    private int pageSize;

    public FindQuestionsByTypesAndWordsRequest() {
    }

    public Long getDirectoryId() {
        return directoryId;
    }

    public void setDirectoryId(Long directoryId) {
        this.directoryId = directoryId;
    }

    public List<Integer> getQuestionTypeIds() {
        return questionTypeIds;
    }

    public void setQuestionTypeIds(List<Integer> questionTypeIds) {
        this.questionTypeIds = questionTypeIds;
    }

    public List<Long> getWordIds() {
        return wordIds;
    }

    public void setWordIds(List<Long> wordIds) {
        this.wordIds = wordIds;
    }

    public Long getChapterId() {
        return chapterId;
    }

    public void setChapterId(Long chapterId) {
        this.chapterId = chapterId;
    }

    public Long getVersionId() {
        return versionId;
    }

    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

}
