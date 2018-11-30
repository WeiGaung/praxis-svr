package com.noriental.praxissvr.question.response;

import com.noriental.praxissvr.question.bean.QuestionFeedback;
import com.noriental.validate.bean.CommonDes;

import java.util.List;

/**
 * @author kate
 * @create 2017-11-07 15:20
 * @desc 查询学生习题纠错数据
 **/
public class FindQuestionFeedBackResp extends CommonDes{

    List<QuestionFeedback> dataList;

    public List<QuestionFeedback> getDataList() {
        return dataList;
    }

    public void setDataList(List<QuestionFeedback> dataList) {
        this.dataList = dataList;
    }
}
