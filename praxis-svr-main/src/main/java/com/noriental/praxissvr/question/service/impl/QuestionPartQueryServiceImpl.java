package com.noriental.praxissvr.question.service.impl;

import com.noriental.praxissvr.question.bean.EntityQuestion;
import com.noriental.praxissvr.question.bean.questionpart.QuestionStateResponse;
import com.noriental.praxissvr.question.mapper.EntityQuestionMapper;
import com.noriental.praxissvr.question.request.FindQuestionsByIdsRequest;
import com.noriental.praxissvr.question.response.FindQuestionsStateResponse;
import com.noriental.praxissvr.question.service.QuestionPartQueryService;
import com.noriental.validate.exception.BizLayerException;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

import static com.noriental.praxissvr.exception.PraxisErrorCode.ANSWER_PARAMETER_NULL;
import static com.noriental.praxissvr.exception.PraxisErrorCode.CUS_DIR_INVALID;

/**
 * Created by liujiang on 2018/5/17.
 */
@Service("questionPartQueryService")
public class QuestionPartQueryServiceImpl implements QuestionPartQueryService{
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @Resource
    private EntityQuestionMapper entityQuestionMapper;

    @Override
    public FindQuestionsStateResponse findQuestionsState(FindQuestionsByIdsRequest request) throws BizLayerException {
        logger.info("查询题目状态入参参数是：{}",request.getQuestionIds());
        if(request.getQuestionIds().size()<1){
            throw new BizLayerException("",ANSWER_PARAMETER_NULL);
        }
        FindQuestionsStateResponse response=new FindQuestionsStateResponse();
        List<EntityQuestion> entityQuestions = entityQuestionMapper.batchQueryQuestionsByIds(request.getQuestionIds());


        if(CollectionUtils.isNotEmpty(entityQuestions)){
            List<QuestionStateResponse> questionSateList=new ArrayList<>();

            for(EntityQuestion question:entityQuestions){
                QuestionStateResponse questionState=new QuestionStateResponse();
                questionState.setId(question.getId());
                questionState.setState(question.getState().name());
                questionSateList.add(questionState);
            }
            response.setQuestionStateResponseList(questionSateList);
        }
        return response;
    }
}
