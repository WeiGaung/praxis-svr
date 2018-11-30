package com.noriental.praxissvr.questionSearch.service;

import com.noriental.praxissvr.question.bean.Question;
import com.noriental.praxissvr.question.request.FindQuestionsRequest;
import com.noriental.praxissvr.question.response.FindQuestionsResponse;
import com.noriental.validate.exception.BizLayerException;

import java.util.List;

/**
 * Created by kate on 2017/8/8.
 */
public interface QuestionSearchService {

    //根据习题IDS查询所有习题的parent_question_id和struct_id
    List<Question> getQuesListByIds(List<Long> ids);


    FindQuestionsResponse findSolrQuestions(FindQuestionsRequest request) throws BizLayerException;


}
