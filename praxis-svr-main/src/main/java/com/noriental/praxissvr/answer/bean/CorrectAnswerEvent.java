package com.noriental.praxissvr.answer.bean;

import org.springframework.context.ApplicationEvent;

/**
 * @author kate
 * @create 2017-12-20 15:51
 * @desc 学生批改、老师批改消息实体
 **/
public class CorrectAnswerEvent extends ApplicationEvent {
    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public CorrectAnswerEvent(CorrectAnswerEntity source) {
        super(source);
    }
}
