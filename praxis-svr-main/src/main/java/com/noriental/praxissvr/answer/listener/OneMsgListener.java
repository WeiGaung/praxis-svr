package com.noriental.praxissvr.answer.listener;

import com.noriental.praxissvr.answer.bean.AnswerMqVo;
import com.noriental.praxissvr.answer.bean.OperateType;
import com.noriental.praxissvr.answer.bean.StudentExercise;
import com.noriental.praxissvr.answer.dao.StudentExerciseDao;
import com.noriental.praxissvr.answer.service.StuAnswerService;
import com.noriental.praxissvr.answer.service.StudentErrorExeService;
import com.noriental.praxissvr.answer.util.IntellUtil;
import com.noriental.praxissvr.answer.util.PraxisSsdbUtil;
import com.noriental.utils.json.JsonUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 2016-3-30
 *
 * @author kate
 *         一条消息即入库,不再进行累积消息
 *         消息处理失败，三次重试机制
 */

public class OneMsgListener implements MessageListener {

    private static Logger logger = LoggerFactory.getLogger(OneMsgListener.class);

    @Resource
    StudentExerciseDao studentExerciseDao;
    @Resource
    StuAnswerService stuAnswerService;
    @Resource
    private StudentErrorExeService studentErrorExeService;
    @Resource
    private RabbitTemplate rabbitTemplate;

    @Override
    public void onMessage(Message message) {
        Long startTime=System.currentTimeMillis();
        try {
            AnswerMqVo in = (AnswerMqVo) IntellUtil.getFromMessage(message);
            if (in != null) {
                IntellUtil.initLogRequestId(in.getRequestId());
                logger.info("OneMsgListener recive message:" + JsonUtil.obj2Json(in));
                porcessInfo(in);
            } else {
                logger.warn("学生做答|批改|批注接收消息不合法！in:null");
            }
        } catch (Exception e) {
            logger.error("学生做答|批改|批注消息处理异常！", e);
        }
        Long endTime=System.currentTimeMillis();
        logger.info("OneMsgListener cost time:{} ms",(endTime-startTime));
    }

    public void porcessInfo(AnswerMqVo in) throws InterruptedException {
        try {
            if (OperateType.ANSWER.equals(in.getOperateType())) {
                List<StudentExercise> seList = in.getStudentExerciseList();
                if (CollectionUtils.isNotEmpty(seList)) {
                    studentExerciseDao.creates(seList);
                    studentErrorExeService.creates(seList);
                    PraxisSsdbUtil.logDataToFile(seList, in.getOperateType());
                }
            } else if (OperateType.INTELL.equals(in.getOperateType())) {
                List<StudentExercise> seList = in.getStudentExerciseList();
                for (StudentExercise studentExercise : seList) {
                    boolean flag = studentExerciseDao.update(studentExercise);
                    if (!flag) {
                        if (repeatSendData(in)) {
                            logger.warn("智能批改点阵数据异步持久化mysql失败，失败的原因是答题数据异步数据未写入库,重新放入MQ队列");
                            return;
                        } else {
                            logger.error("智能批改点阵数据异步持久化mysql失败，持久化失败的数据是:{}", JsonUtil.obj2Json(in));
                            return;
                        }
                    } else {
                        studentErrorExeService.updateOne(studentExercise);
                    }
                }
            } else if (OperateType.AUDIO.equals(in.getOperateType())) {
                StudentExercise studentExercise = in.getStudentExercise();
                if (studentExercise != null) {
                    boolean flag = studentExerciseDao.update(studentExercise);
                    if (!flag) {
                        if (repeatSendData(in)) {
                            logger.warn("第三方音频地址修改为七牛更新失败，失败的原因是答题数据异步数据未写入库,重新放入MQ队列");
                            return;
                        } else {
                            logger.error("第三方音频地址修改为七牛更新重试三次后还是失败，持久化失败的数据是:{}", JsonUtil.obj2Json(in));
                            return;
                        }
                    }
                }
            } else {
                List<StudentExercise> seList = new ArrayList<>();
                StudentExercise studentExercise = in.getStudentExercise();
                if (studentExercise != null) {
                    boolean flag = studentExerciseDao.update(studentExercise);
                    if (!flag) {
                        if (repeatSendData(in)) {
                            logger.warn("老师批改数据更新失败，失败的原因是答题数据异步数据未写入库,重新放入MQ队列");
                            return;
                        } else {
                            logger.error("老师批改数据重试三次后还是失败，持久化失败的数据是:{}", JsonUtil.obj2Json(in));
                            return;
                        }
                    } else {
                        studentErrorExeService.updateOne(studentExercise);
                        seList.add(studentExercise);
                    }
                }
                PraxisSsdbUtil.logDataToFile(seList, in.getOperateType());
            }
        } catch (Exception e) {

            if (!repeatSendData(in)) {
                logger.error("MQ异步持久化MySql业务数据:{}失败，失败的原因是:{},重新将数据放入MQ队列", JsonUtil.obj2Json(in), e);
            }
        }

    }

    /**
     * 是否允许重复发送
     *
     * @param in
     * @return
     * @throws InterruptedException
     */
    private boolean repeatSendData(AnswerMqVo in) throws InterruptedException {
        if (in.getCounts() == null || in.getCounts() < 3) {
            in.setCounts(in.getCounts() == null ? 1 : (in.getCounts() + 1));
            TimeUnit.MILLISECONDS.sleep(5000);
            rabbitTemplate.convertAndSend(in);
            return true;
        }
        return false;
    }





}
