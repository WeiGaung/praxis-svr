package com.noriental.praxissvr.question.service;

import com.noriental.praxissvr.question.request.FindQuestionsByIdsRequest;
import com.noriental.praxissvr.question.response.FindQuestionsStateResponse;
import com.noriental.validate.exception.BizLayerException;

/**
 * Created by liujiang on 2018/5/17.
 */
public interface QuestionPartQueryService {
    /**
     * 根据传入的题目ID列表查询题目状态
     * @param request
     * @return
     * @throws BizLayerException
     */
    FindQuestionsStateResponse findQuestionsState(FindQuestionsByIdsRequest request) throws BizLayerException;
}
