package com.noriental.praxissvr.question.service;

import com.noriental.praxissvr.question.request.CreateQuestionFeedbackRequest;
import com.noriental.praxissvr.question.request.FindQuestionFeedbacksRequest;
import com.noriental.praxissvr.question.response.FindQuestionFeedBackResp;
import com.noriental.validate.bean.CommonDes;
import com.noriental.validate.exception.BizLayerException;

public interface QuestionFeedbackService {
    CommonDes createQuestionFeedback(CreateQuestionFeedbackRequest request) throws BizLayerException;

    FindQuestionFeedBackResp findQuestionFeedbacks(FindQuestionFeedbacksRequest request) throws BizLayerException;

}
