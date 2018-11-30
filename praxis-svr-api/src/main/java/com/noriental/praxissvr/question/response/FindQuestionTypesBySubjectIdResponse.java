package com.noriental.praxissvr.question.response;

import com.noriental.praxissvr.question.bean.QuestionType;
import com.noriental.validate.bean.CommonDes;

import java.util.List;

/**
 * @author chenlihua
 * @date 2015/12/22
 * @time 15:50
 */
public class FindQuestionTypesBySubjectIdResponse extends CommonDes {
    private List<QuestionType> list;

    public List<QuestionType> getList() {
        return list;
    }

    public void setList(List<QuestionType> list) {
        this.list = list;
    }
}
