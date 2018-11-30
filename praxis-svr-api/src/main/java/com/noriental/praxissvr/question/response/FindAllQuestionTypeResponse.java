package com.noriental.praxissvr.question.response;

import com.noriental.validate.bean.CommonDes;
import com.noriental.praxissvr.question.bean.QuestionType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chenlihua
 * @date 2016/3/10
 * @time 16:00
 */
public class FindAllQuestionTypeResponse extends CommonDes {
    private List<QuestionType> list;
    private Map<Long, Long> typeIdStructIdMap = new HashMap<>();

    public List<QuestionType> getList() {
        return list;
    }

    public void setList(List<QuestionType> list) {
        this.list = list;
        for (QuestionType questionType : list) {
            typeIdStructIdMap.put(questionType.getTypeId(), questionType.getStructId());
        }
    }

    public long findStructIdByTypeId(long questionTypeId) {
        return typeIdStructIdMap.get(questionTypeId);
    }
}
