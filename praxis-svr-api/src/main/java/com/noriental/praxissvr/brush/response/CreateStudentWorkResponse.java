package com.noriental.praxissvr.brush.response;

import com.noriental.validate.bean.CommonDes;

/**
 * Created by shengxian on 2016/12/15.
 */
public class CreateStudentWorkResponse extends CommonDes{
    /**
     * 答题批次id
     */
    private  Long workId;

    public Long getWorkId() {
        return workId;
    }

    public void setWorkId(Long workId) {
        this.workId = workId;
    }
}
