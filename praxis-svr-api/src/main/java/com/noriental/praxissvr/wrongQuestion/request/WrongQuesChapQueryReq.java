package com.noriental.praxissvr.wrongQuestion.request;

import com.noriental.validate.bean.BaseRequest;

import javax.validation.constraints.NotNull;

/**
 * Created by kate on 2016/12/16.
 * 错误习题按照章节分页查询
 */
public class WrongQuesChapQueryReq extends BaseRequest {
    /**
     * 学生ID
     */
    @NotNull
    private Long studentId;
    /***
     * 学科ID
     */
    @NotNull
    private Long subjectId;
    /**
     * 分册ID
     */

    private Long directoryId;
    /***
     * 章节ID
     */
    private Long chapterId;

    /***
     * 章节级别
     */
    private int chapterLevel;
    /***
     * 分页大小
     */

    private int pageSize;
    /**
     * 当前页码
     */

    private int currentPage;
    /***
     * 查询起始位置
     */
    private Long fromIndex;

    private String chapterOrKnowledge;

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public Long getDirectoryId() {
        return directoryId;
    }

    public void setDirectoryId(Long directoryId) {
        this.directoryId = directoryId;
    }

    public Long getChapterId() {
        return chapterId;
    }

    public void setChapterId(Long chapterId) {
        this.chapterId = chapterId;
    }

    public int getChapterLevel() {
        return chapterLevel;
    }

    public void setChapterLevel(int chapterLevel) {
        this.chapterLevel = chapterLevel;
    }

    public Long getFromIndex() {
        return fromIndex;
    }

    public void setFromIndex(Long fromIndex) {
        this.fromIndex = fromIndex;
    }

    public String getChapterOrKnowledge() {
        return chapterOrKnowledge;
    }

    public void setChapterOrKnowledge(String chapterOrKnowledge) {
        this.chapterOrKnowledge = chapterOrKnowledge;
    }
}
