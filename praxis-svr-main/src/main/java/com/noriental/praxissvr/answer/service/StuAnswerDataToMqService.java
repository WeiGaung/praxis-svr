package com.noriental.praxissvr.answer.service;

import com.noriental.praxissvr.brush.bean.StudentWorkAnswer;
import com.noriental.validate.exception.BizLayerException;

import java.util.List;

/**
 * Created by kate on 2016/12/26.
 */
public interface StuAnswerDataToMqService {

    /**
     * 向RabbitMq推送已做对的、未批改的习题数据
     * @param dataList
     * @throws Exception
     */
     void sendExerciseData2Mq(List<StudentWorkAnswer> dataList) throws BizLayerException;


}
