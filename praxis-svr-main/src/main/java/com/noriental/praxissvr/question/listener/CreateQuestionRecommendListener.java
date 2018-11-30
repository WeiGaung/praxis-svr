package com.noriental.praxissvr.question.listener;

import com.noriental.praxissvr.answer.util.IntellUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

/**
 * Created by liujiang on 2018/5/16.
 */
public class CreateQuestionRecommendListener implements MessageListener {
    private static Logger logger = LoggerFactory.getLogger(CreateQuestionRecommendListener.class);


    @Override
    public void onMessage(Message message) {
        try {
            System.out.println("创建题目收到的部分字段消息内容是："+ IntellUtil.getFromMessage(message).toString());
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
