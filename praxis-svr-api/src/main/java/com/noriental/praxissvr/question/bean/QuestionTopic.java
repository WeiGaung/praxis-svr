package com.noriental.praxissvr.question.bean;

import com.noriental.framework.base.BaseBean;

/**
 * @author xiangfei
 * @date 2015年10月28日 下午5:12:24
 */
public class QuestionTopic extends BaseBean {
    private static final long serialVersionUID = -3289720098441559341L;

    private Long id; //主键
    private Long questionId;//试题id
    private Long topicId;// 主题id
    private Integer mastery;//掌握程度

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    public Integer getMastery() {
        return mastery;
    }

    public void setMastery(Integer mastery) {
        this.mastery = mastery;
    }


}
