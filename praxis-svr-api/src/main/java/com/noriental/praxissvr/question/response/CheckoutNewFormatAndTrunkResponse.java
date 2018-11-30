package com.noriental.praxissvr.question.response;

import com.noriental.validate.bean.CommonDes;

import java.util.List;

/**
 * @author chenlihua
 * @date 2016/4/29
 * @time 16:20
 */
public class CheckoutNewFormatAndTrunkResponse extends CommonDes {
    private List<Long> ids;

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }
}
