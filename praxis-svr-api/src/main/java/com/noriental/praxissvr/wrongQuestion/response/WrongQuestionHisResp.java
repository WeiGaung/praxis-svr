package com.noriental.praxissvr.wrongQuestion.response;

import com.noriental.praxissvr.wrongQuestion.bean.WrongQuesExerciseEntity;
import com.noriental.validate.bean.CommonDes;

import java.util.List;

/**
 * Created by kate on 2016/12/19.
 * 某学生某学科某题的错题记录返回信息
 */
public class WrongQuestionHisResp extends CommonDes {
    private List<WrongQuesExerciseEntity> dataList;

    public List<WrongQuesExerciseEntity> getDataList() {
        return dataList;
    }

    public void setDataList(List<WrongQuesExerciseEntity> dataList) {
        this.dataList = dataList;
    }
}
