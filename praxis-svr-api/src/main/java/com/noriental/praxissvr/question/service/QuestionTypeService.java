package com.noriental.praxissvr.question.service;

import com.noriental.praxissvr.question.request.FindAllQuestionTypeRequest;
import com.noriental.praxissvr.question.request.FindQuestionTypesBySubjectIdRequest;
import com.noriental.praxissvr.question.request.FindQuestionTypesZhBySubjectIdRequest;
import com.noriental.praxissvr.question.request.FindStructIdByQuesTypeIdRequest;
import com.noriental.praxissvr.question.response.FindAllQuestionTypeResponse;
import com.noriental.praxissvr.question.response.FindQuestionTypesBySubjectIdResponse;
import com.noriental.praxissvr.question.response.FindQuestionTypesZhBySubjectIdResponse;
import com.noriental.praxissvr.question.response.FindStructIdByQuesTypeIdResponse;
import com.noriental.validate.exception.BizLayerException;

/**
 * @author chenlihua
 * @date 2015/12/22
 * @time 15:45
 */
public interface QuestionTypeService {


    /**
     * 根据学科查询题目类型
     * @param request req
     * @return resp
     */
    FindQuestionTypesBySubjectIdResponse findQuestionTypeBySubjectId(FindQuestionTypesBySubjectIdRequest request) throws BizLayerException;

    /**
     * 查询支持的题型中文
     *
     * @param request req
     * @return resp
     */
    FindQuestionTypesZhBySubjectIdResponse findQuestionTypesZhBySubjectId(FindQuestionTypesZhBySubjectIdRequest request) throws BizLayerException;

    FindAllQuestionTypeResponse findAllQuestionType(FindAllQuestionTypeRequest request) throws BizLayerException;

    FindStructIdByQuesTypeIdResponse findStructIdByQuesTypeId(FindStructIdByQuesTypeIdRequest request) throws BizLayerException;
}
