package com.noriental.praxissvr.question.response;

import com.noriental.validate.bean.CommonDes;

import java.util.List;

/**
 * @author chenlihua
 * @date 2015/12/22
 * @time 15:48
 */
public class FindQuestionTypesZhBySubjectIdResponse extends CommonDes {
    private List<String> list;

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }
}
