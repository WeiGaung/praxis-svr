package com.noriental.praxissvr.question.response;

import com.noriental.validate.bean.CommonDes;

import java.util.List;
import java.util.Map;

/**
 * Created by hushuang on 2017/7/6.
 * 通过题型和单词查询到的单词题题目列表
 */
public class FindQuestionsByTypesAndWordsResponse extends CommonDes {

    /*
        查询到的题目列表
        Map的数据为
        key:questionId          value:1000L
        key:question_type_id    value:1
        key:question_type_name  value:选择题

     */
    private List<Map<String,Object>> questionDataList;

    public FindQuestionsByTypesAndWordsResponse() {
    }

    public FindQuestionsByTypesAndWordsResponse(List<Map<String, Object>> questionDataList) {
        this.questionDataList = questionDataList;
    }

    public List<Map<String, Object>> getQuestionDataList() {
        return questionDataList;
    }

    public void setQuestionDataList(List<Map<String, Object>> questionDataList) {
        this.questionDataList = questionDataList;
    }

    @Override
    public String toString() {
        return "FindQuestionsByTypesAndWordsResponse{" +
                "questionDataList=" + questionDataList +
                '}';
    }
}
