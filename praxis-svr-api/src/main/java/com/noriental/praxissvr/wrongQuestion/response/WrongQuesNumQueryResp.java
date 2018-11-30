package com.noriental.praxissvr.wrongQuestion.response;

import com.noriental.praxissvr.wrongQuestion.bean.WrongQuesChapterEntity;
import com.noriental.validate.bean.CommonDes;

import java.util.List;

/**
 * Created by kate on 2016/12/16.
 */
public class WrongQuesNumQueryResp extends CommonDes {
    List<WrongQuesChapterEntity> dataList;

    public List<WrongQuesChapterEntity> getDataList() {
        return dataList;
    }

    public void setDataList(List<WrongQuesChapterEntity> dataList) {
        this.dataList = dataList;
    }
}
