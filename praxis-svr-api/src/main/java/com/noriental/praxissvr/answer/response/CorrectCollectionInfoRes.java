package com.noriental.praxissvr.answer.response;

import com.noriental.validate.bean.CommonDes;

/**
 * Created by kate on 2017/4/13.
 */
public class CorrectCollectionInfoRes extends CommonDes {

    /***
     * 是否重复批改
     */
    private boolean isRepeatCorrect;

    /***
     * 批改结果状态变化
     * 0未发生变化1由错批改为对2由对批改为错
     */
    private Integer coorectStatus;

    public boolean isRepeatCorrect() {
        return isRepeatCorrect;
    }

    public void setRepeatCorrect(boolean repeatCorrect) {
        isRepeatCorrect = repeatCorrect;
    }

    public Integer getCoorectStatus() {
        return coorectStatus;
    }

    public void setCoorectStatus(Integer coorectStatus) {
        this.coorectStatus = coorectStatus;
    }
}
