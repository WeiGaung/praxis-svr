package com.noriental.praxissvr.wrongQuestion.response;

import com.noriental.praxissvr.wrongQuestion.bean.WrongQuesChapterEntity;
import com.noriental.validate.bean.CommonDes;

import java.util.List;

/**
 * Created by kate on 2016/12/16.
 */
public class WrongQuesChapQueryResp extends CommonDes {
    List<WrongQuesChapterEntity> dataList;
    private Integer pageSize;
    private Integer currentPage;
    private Integer totalCount;
    private Integer totalPage;
    private boolean isLastPage;
    private Long lastIndexId;

    public List<WrongQuesChapterEntity> getDataList() {
        return dataList;
    }

    public void setDataList(List<WrongQuesChapterEntity> dataList) {
        this.dataList = dataList;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public boolean isLastPage() {
        return isLastPage;
    }

    public void setLastPage(boolean lastPage) {
        isLastPage = lastPage;
    }

    public Long getLastIndexId() {
        return lastIndexId;
    }

    public void setLastIndexId(Long lastIndexId) {
        this.lastIndexId = lastIndexId;
    }
}
