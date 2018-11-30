package com.noriental.praxissvr.common;

import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.github.miemiedev.mybatis.paginator.domain.Paginator;
import com.noriental.validate.bean.CommonDes;

import java.util.List;

/**
 * @author chenlihua
 * @date 2016/6/21
 * @time 11:27
 */
public class PageBaseResponse<T> extends CommonDes {
    private int totalCount;
    private int currentPage;
    private int totalPageCount;
    private List<T> list;

    public PageBaseResponse(PageList<T> pageList) {
        if (pageList != null) {
            Paginator paginator = pageList.getPaginator();
            setTotalCount(paginator.getTotalCount());
            setCurrentPage(paginator.getPage());
            setTotalPageCount(paginator.getTotalPages());
            setList(pageList);
        }
    }

    public PageBaseResponse() {
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalPageCount() {
        return totalPageCount;
    }

    public void setTotalPageCount(int totalPageCount) {
        this.totalPageCount = totalPageCount;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
