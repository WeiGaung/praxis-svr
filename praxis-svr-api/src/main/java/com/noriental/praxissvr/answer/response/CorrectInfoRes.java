package com.noriental.praxissvr.answer.response;

import com.noriental.validate.bean.CommonDes;

/**
 * Created by kate on 2017/4/13.
 */
public class CorrectInfoRes extends CommonDes {

    /***
     * 批改结果状态变化
     * 0未发生变化1由错批改为对2由对批改为错
     */
    private String coorectStatus;

    public String getCoorectStatus() {
        return coorectStatus;
    }

    public void setCoorectStatus(String coorectStatus) {
        this.coorectStatus = coorectStatus;
    }
}
