package com.noriental.praxissvr.wrongQuestion.response;

import com.noriental.praxissvr.wrongQuestion.bean.WrongQuesListEntity;
import com.noriental.validate.bean.CommonDes;

import java.util.List;

/**
 * Created by kate on 2017/5/31.
 */
public class WrongQuesListResp extends CommonDes {

    List<WrongQuesListEntity> groupList;  //返回的数组

    public List<WrongQuesListEntity> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<WrongQuesListEntity> groupList) {
        this.groupList = groupList;
    }
}
