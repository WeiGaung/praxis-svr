package com.noriental.praxissvr.answer.listener;

import com.noriental.log.TraceKeyHolder;
import com.noriental.praxissvr.answer.bean.*;
import com.noriental.praxissvr.answer.dao.StudentExerciseDao;
import com.noriental.praxissvr.answer.mappers.AnswerCorrectMapper;
import com.noriental.praxissvr.answer.service.StudentErrorExeService;
import com.noriental.praxissvr.answer.util.IntellUtil;
import com.noriental.praxissvr.answer.util.PraxisSsdbUtil;
import com.noriental.praxissvr.wrongQuestion.util.TableNameUtil;
import com.noriental.utils.json.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.context.ApplicationContext;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author kate
 * @create 2017-12-26 9:48
 * @desc 一键批改监听器
 **/
public class BatchUpdateCorrectListener implements MessageListener {

    private static Logger logger = LoggerFactory.getLogger(BatchUpdateCorrectListener.class);
    @Resource
    StudentExerciseDao studentExerciseDao;
    @Resource
    private StudentErrorExeService studentErrorExeService;
    @Resource
    AnswerCorrectMapper answerCorrectMapper;
    @Resource
    private ApplicationContext applicationContext;

    @Override
    public void onMessage(Message message) {
        Long startTime = System.currentTimeMillis();
        try {
            AnswerMqVo in = (AnswerMqVo) IntellUtil.getFromMessage(message);
            if (in != null) {
                IntellUtil.initLogRequestId(in.getRequestId());
                logger.info("BatchUpdateCorrectListener recive message:" + JsonUtil.obj2Json(in));
                porcessInfo(in);
            } else {
                logger.warn("一键批改接收的消息不合法！in:null");
            }
        } catch (Exception e) {
            logger.error("一键批改消息处理异常！", e);
        }
        Long endTime = System.currentTimeMillis();
        logger.info("BatchUpdateCorrectListener cost time:{} ms", (endTime - startTime));
    }

    private void porcessInfo(AnswerMqVo in) {
        //如果为按人的一键批改和按题的一键批改和按题的一键智能批改
        List<StudentExercise> seList = in.getStudentExerciseList();
        if (OperateType.BATCHUPDATE.equals(in.getOperateType())) {
            TableNameUtil.updateTableName(seList);
            answerCorrectMapper.batchUpdateCorrect(seList);
        }
        for (StudentExercise studentExercise : seList) {
            //批改错题记录入库规则：复合体一道题错整个复合体的答题记录数据都要进入错题记录
            studentErrorExeService.updateOne(studentExercise);
            //后置业务处理改为监听机制
            String traceKey = TraceKeyHolder.getTraceKey();
            CorrectAnswerEvent event = new CorrectAnswerEvent(new CorrectAnswerEntity(studentExercise, null,
                    OperateType.BATCHUPDATE, traceKey));
            applicationContext.publishEvent(event);
        }
    }
}
