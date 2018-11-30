package com.noriental.praxissvr.answer.bean;

import org.springframework.context.ApplicationEvent;

/**
 * @author kate
 * @create 2017-12-20 14:48
 * @desc 学生做答、老师批改消息实体
 **/
public class AnswerSubmitEvent extends ApplicationEvent {
    /**
     * Create a new ApplicationEvent.
     *
     * @param event the object on which the event initially occurred (never {@code null})
     */
    public AnswerSubmitEvent(AnswerSubmitEntity event) {
        super(event);
    }
}
