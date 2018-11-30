package com.noriental.praxissvr.question.request;

import com.noriental.validate.bean.BaseRequest;

import javax.validation.constraints.NotNull;

/**
 * Created by hushuang on 2017/7/6.
 * 根据章节ID查询单词列表的请求
 */
public class FindWordByChapterRequest extends BaseRequest {

    @NotNull
    /**
     * 章节ID
     */
    private Long chapterId;
    /**
     * 当前页
     */
    private int pageNumber;
    /**
     * 页容量
     */
    private int pageSize;

    public FindWordByChapterRequest() {
    }

    public Long getChapterId() {
        return chapterId;
    }

    public void setChapterId(Long chapterId) {
        this.chapterId = chapterId;
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

    @Override
    public String toString() {
        return "FindWordByChapterRequest{" +
                "chapterId=" + chapterId +
                ", pageNumber=" + pageNumber +
                ", pageSize=" + pageSize +
                '}';
    }
}
