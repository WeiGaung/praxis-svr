package com.noriental.praxissvr.question.service.impl;

import com.noriental.global.dict.AppType;
import com.noriental.praxissvr.exception.PraxisErrorCode;
import com.noriental.praxissvr.question.bean.QuestionType;
import com.noriental.praxissvr.question.dao.QuestionTypeDao;
import com.noriental.praxissvr.question.request.FindAllQuestionTypeRequest;
import com.noriental.praxissvr.question.request.FindQuestionTypesBySubjectIdRequest;
import com.noriental.praxissvr.question.request.FindQuestionTypesZhBySubjectIdRequest;
import com.noriental.praxissvr.question.request.FindStructIdByQuesTypeIdRequest;
import com.noriental.praxissvr.question.response.FindAllQuestionTypeResponse;
import com.noriental.praxissvr.question.response.FindQuestionTypesBySubjectIdResponse;
import com.noriental.praxissvr.question.response.FindQuestionTypesZhBySubjectIdResponse;
import com.noriental.praxissvr.question.response.FindStructIdByQuesTypeIdResponse;
import com.noriental.praxissvr.question.service.QuestionTypeService;
import com.noriental.utils.redis.RedisUtil;
import com.noriental.validate.exception.BizLayerException;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chenlihua
 * @date 2015/12/22
 * @time 15:53
 */
@Service("quiz2.questionTypeService")
public class QuestionTypeServiceImpl implements QuestionTypeService {

    private static final String REDIS_KEY_QUES_TYPE_ALL = "QUESTION_TYPE_SUBJECT_ALL";

    private static final String REDIS_KEY_QUES_TYPE_ALL_DATA = "QUES_TYPE_ALL_DATA";//不排除无效的题型
    @Resource
    private QuestionTypeDao questionTypeDao;

    @Resource
    private RedisUtil redisUtil;

    @Override
    public FindQuestionTypesBySubjectIdResponse findQuestionTypeBySubjectId(FindQuestionTypesBySubjectIdRequest request) {
        long subjectId = request.getSubjectId();
        List<QuestionType> questionTypes = getQuestionTypes(subjectId);
        FindQuestionTypesBySubjectIdResponse resp = new FindQuestionTypesBySubjectIdResponse();
        resp.setList(questionTypes);
        return resp;
    }

    @SuppressWarnings("unchecked")
    private List<QuestionType> getQuestionTypes(long subjectId) {
        List<QuestionType> questionTypes;
        Map<String, Object> params = new HashMap<>();
        params.put("subjectId", subjectId);
        questionTypes = questionTypeDao.findQuesTypeBySubjectId(params);

        return questionTypes;
    }

    @Override
    public FindQuestionTypesZhBySubjectIdResponse findQuestionTypesZhBySubjectId(FindQuestionTypesZhBySubjectIdRequest request) {
        List<QuestionType> quesTypes = getQuestionTypes(request.getSubjectId());
        List<String> quesTypesZh = new ArrayList<>();
        if (quesTypes != null) {
            for (QuestionType quesType : quesTypes) {
                quesTypesZh.add(quesType.getTypeName());
            }
        }
        FindQuestionTypesZhBySubjectIdResponse resp = new FindQuestionTypesZhBySubjectIdResponse();
        resp.setList(quesTypesZh);
        return resp;
    }

    @SuppressWarnings("unchecked")
    @Override
    public FindAllQuestionTypeResponse findAllQuestionType(FindAllQuestionTypeRequest request) throws BizLayerException {
        FindAllQuestionTypeResponse resp = new FindAllQuestionTypeResponse();
        List<QuestionType> questionTypes;
        Object listO = redisUtil.get(REDIS_KEY_QUES_TYPE_ALL);
        if (listO == null) {
            questionTypes = questionTypeDao.findAllEnable();
            redisUtil.set(REDIS_KEY_QUES_TYPE_ALL, questionTypes, AppType.QuestionTypeRedis.REDIS_EXPIRE_SECONDS);

        } else {
            questionTypes = (List<QuestionType>) listO;
        }
        if (CollectionUtils.isEmpty(questionTypes)) {
            throw new BizLayerException("查询数据异常", PraxisErrorCode.PRAXIS_QUESTION_TYPE_NOT_FOUND);
        }

        resp.setList(questionTypes);
        return resp;
    }

    @SuppressWarnings("unchecked")
    @Override
    public FindStructIdByQuesTypeIdResponse findStructIdByQuesTypeId(FindStructIdByQuesTypeIdRequest request) throws BizLayerException {

        Long quesTypeId =request.getQuesTypeId();
        if(quesTypeId==null){
            throw new BizLayerException("[题型id]",PraxisErrorCode.ANSWER_PARAMETER_ILL);
        }

        FindStructIdByQuesTypeIdResponse resp = new FindStructIdByQuesTypeIdResponse();
        QuestionType qt;
        Object listO = redisUtil.get(REDIS_KEY_QUES_TYPE_ALL_DATA);
        if (listO == null) {
            List<QuestionType> questionTypes = questionTypeDao.findAll();
            redisUtil.set(REDIS_KEY_QUES_TYPE_ALL_DATA, questionTypes,AppType.QuestionTypeRedis.REDIS_EXPIRE_SECONDS);

            qt = findStructIdByQuesTypeId(quesTypeId,questionTypes);
        } else {
            List<QuestionType> questionTypes = (List<QuestionType>) listO;

            qt = findStructIdByQuesTypeId(quesTypeId,questionTypes);
        }
        resp.setQuestionType(qt);
        return resp;
    }

    private  QuestionType findStructIdByQuesTypeId(Long quesTypeId,List<QuestionType> questionTypes) {
        for (int i = 0; CollectionUtils.isNotEmpty(questionTypes) && i < questionTypes.size(); i++) {
            QuestionType qt = questionTypes.get(i);
            Long typeId = qt.getTypeId();
            if (quesTypeId.equals(typeId)) {
                return qt;
            }
        }
        throw new BizLayerException("[题型id]"+quesTypeId,PraxisErrorCode.PRAXIS_QUESTION_TYPE_NOT_FOUND);
    }
}
