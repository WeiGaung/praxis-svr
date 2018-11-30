package com.noriental.praxissvr.common;

import com.noriental.praxissvr.common.constraints.Pageable;
import com.noriental.validate.bean.BaseRequest;

/**
 * @author chenlihua
 * @date 2016/6/17
 * @time 18:42
 */
@Pageable
public class PageBaseRequest extends BaseRequest {
    private int pageNo = 1;
    private int pageSize = 10;
    private boolean pageable = true;

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public boolean isPageable() {
        return pageable;
    }

    public void setPageable(boolean pageable) {
        this.pageable = pageable;
    }
}
