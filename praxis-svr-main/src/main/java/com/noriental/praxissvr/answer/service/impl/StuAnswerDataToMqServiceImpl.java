package com.noriental.praxissvr.answer.service.impl;

import com.noriental.praxissvr.answer.service.StuAnswerDataToMqService;
import com.noriental.praxissvr.answer.util.StuAnswerUtil;
import com.noriental.praxissvr.brush.bean.StudentWorkAnswer;
import com.noriental.praxissvr.common.StuAnswerConstant;
import com.noriental.praxissvr.exception.PraxisErrorCode;
import com.noriental.praxissvr.question.utils.JsonUtils;
import com.noriental.validate.exception.BizLayerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kate on 2016/12/26.
 * 将已做对的习题和为批改的习题通过MQ发送到习题推荐业务
 */
@Service("stuAnswerDataToMqServiceImpl")
public class StuAnswerDataToMqServiceImpl implements StuAnswerDataToMqService {
    private static final Logger logger = LoggerFactory.getLogger(StuAnswerDataToMqServiceImpl.class);
    @Resource
    private RabbitTemplate stuAnswerDataRabbitTemplate;

    public void sendStuAnswerData2Mq(List<StudentWorkAnswer> dataList) throws BizLayerException {
        Map resultMap = new HashMap();
        List<Long> resultList = new ArrayList<>();
        for (StudentWorkAnswer data : dataList) {
            Long studentId = data.getStudentId();
            Long questionId = data.getQuestionId();
            resultMap.put("studentId", studentId);
            resultList.add(questionId);
        }
        if(resultList.size()>0){
            resultMap.put("questionIdList", resultList);
            logger.info("向RabbitMq发送习题推荐列表的业务数据{}", JsonUtils.getObjectToJson(resultMap));
            try {
                stuAnswerDataRabbitTemplate.convertAndSend(JsonUtils.getObjectToJson(resultMap));
            } catch (BizLayerException e) {
                logger.error("将已做对的习题和已批改的习题通过MQ发送到习题推荐业务失败",e);
                throw  new BizLayerException("",PraxisErrorCode.SEND_MQ_FAIL);
            }
        }
    }

    /***
     * 对数据进行删选
     * 1、已做对的数据筛选

     * @param dataList
     * @return
     */
    public List<StudentWorkAnswer> filterStuExerciseList(List<StudentWorkAnswer> dataList) {
        List<StudentWorkAnswer> resultList = new ArrayList<>();
        for (StudentWorkAnswer data : dataList) {
            String result = data.getResult();
            Integer structId = data.getStructId();
            String returnData = StuAnswerUtil.getExerciseResult(structId, result);
            //筛选已做对的数据
            if (StuAnswerConstant.ExerciseResult.RIGHT.equals(returnData)) {
                //判断当前的题型是否为复合题
                Long parentQuestionId = data.getParentQuestionId();
                //判断该题是否为复合题
                if (null != parentQuestionId) {
                    boolean flag = true;
                    List<StudentWorkAnswer> childQuestions = data.getSubQuesAnswers();
                    for (StudentWorkAnswer childData : childQuestions) {
                        String childResult = childData.getResult();
                        Integer childStructId = childData.getStructId();
                        String childReturnData = StuAnswerUtil.getExerciseResult(childStructId, childResult);
                        if (!StuAnswerConstant.ExerciseResult.RIGHT.equals(childReturnData)) {
                            flag = false;
                        }
                    }
                    if (flag) {
                        resultList.add(data);
                    }
                } else {
                    resultList.add(data);
                }

            }
        }
        return resultList;
    }


    /***
     * 未批改的数据筛选
     *  1、复合题如果包含错题 那么表示已批改
     *  2、复合题如果全部正确 表示已批改
     * @param dataList
     * @return
     */

    public List<StudentWorkAnswer> filterNotMarkStuExerciseList(List<StudentWorkAnswer> dataList) {
        List<StudentWorkAnswer> resultList = new ArrayList<>();
        for (StudentWorkAnswer data : dataList) {
            String result = data.getResult();
            Integer structId = data.getStructId();
            String returnData = StuAnswerUtil.getExerciseResult(structId, result);
            //筛选未批改的数据
            if (StuAnswerConstant.ExerciseResult.SUBMITED.equals(returnData)) {
                //判断当前的题型是否为复合题
                Long parentQuestionId = data.getParentQuestionId();
                if (null != parentQuestionId) {
                    boolean flag = false;
                    List<StudentWorkAnswer> childQuestions = data.getSubQuesAnswers();
                    for (StudentWorkAnswer childData : childQuestions) {
                        String childResult = childData.getResult();
                        Integer childStructId = childData.getStructId();
                        String childReturnData = StuAnswerUtil.getExerciseResult(childStructId, childResult);
                        //如果有错题，直接跳出循环
                        if (StuAnswerConstant.ExerciseResult.ERROR.equals(childReturnData)) {
                            break;
                        }
                        //判断所有子体是否都正确，若都正确则排除
                        if (!StuAnswerConstant.ExerciseResult.RIGHT.equals(childReturnData)) {
                            flag = true;
                        }
                    }
                    if (flag) {
                        resultList.add(data);
                    }
                } else {
                    resultList.add(data);
                }

            }
        }
        return resultList;
    }


    @Override
    public void sendExerciseData2Mq(List<StudentWorkAnswer> dataList) throws BizLayerException {
        List<StudentWorkAnswer> resList = filterStuExerciseList(dataList);
        List<StudentWorkAnswer> resList2 = filterNotMarkStuExerciseList(dataList);
        resList.addAll(resList2);
        try {
            sendStuAnswerData2Mq(resList);
        } catch (BizLayerException e) {
           throw e;
        }
    }


}
