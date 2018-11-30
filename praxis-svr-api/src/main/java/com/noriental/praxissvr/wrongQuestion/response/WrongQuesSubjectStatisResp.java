package com.noriental.praxissvr.wrongQuestion.response;

import com.noriental.praxissvr.wrongQuestion.bean.WrongQuesSubjectStatisEntity;
import com.noriental.validate.bean.CommonDes;

import java.util.List;

/**
 * Created by kate on 2016/12/16.
 * 习题本按照学科统计错误习题数量，相同的习题只统计一次
 */
public class WrongQuesSubjectStatisResp extends CommonDes {

    List<WrongQuesSubjectStatisEntity> groupList;

    public List<WrongQuesSubjectStatisEntity> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<WrongQuesSubjectStatisEntity> groupList) {
        this.groupList = groupList;
    }
}
