package com.noriental.praxissvr.wrong.response;

import com.noriental.validate.bean.CommonDes;

import java.util.List;

/**
 * Created by bluesky on 2016/7/5.
 */
public class FindChalQuessResponse  extends CommonDes {
    private List<Long> quesIds;

    public List<Long> getQuesIds() {
        return quesIds;
    }

    public void setQuesIds(List<Long> quesIds) {
        this.quesIds = quesIds;
    }
}
