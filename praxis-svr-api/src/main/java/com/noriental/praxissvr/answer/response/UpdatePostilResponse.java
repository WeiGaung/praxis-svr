package com.noriental.praxissvr.answer.response;

import com.noriental.validate.bean.CommonDes;

import java.util.List;

/**
 * Created by bluesky on 2016/5/28.
 */
public class UpdatePostilResponse extends CommonDes {
    private List<String> completedPostils;

    public List<String> getCompletedPostils() {
        return completedPostils;
    }

    public void setCompletedPostils(List<String> completedPostils) {
        this.completedPostils = completedPostils;
    }
}
