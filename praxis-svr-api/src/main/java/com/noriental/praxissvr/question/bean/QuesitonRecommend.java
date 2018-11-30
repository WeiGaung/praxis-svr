package com.noriental.praxissvr.question.bean;

import java.io.Serializable;
import java.util.List;

public class QuesitonRecommend implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -7341188898864521434L;
    private Long questionId;
    private Integer difficulty;
    private String questionType;

    private List<Long> topicIds;

    //个性字段
    Integer recommedPrior;//推荐优先级
    //个性字段

    //错题强化中间结果用到
    private Long parentQuestionId;
    private Integer isSingle;
    //错题强化中间结果用到

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Integer getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public void setTopicIds(List<Long> topicIds) {
        this.topicIds = topicIds;
    }

    public List<Long> getTopicIds() {
        return topicIds;
    }

    public Integer getRecommedPrior() {
        return recommedPrior;
    }

    public void setRecommedPrior(Integer recommedPrior) {
        this.recommedPrior = recommedPrior;
    }


    public Long getParentQuestionId() {
        return parentQuestionId;
    }

    public void setParentQuestionId(Long parentQuestionId) {
        this.parentQuestionId = parentQuestionId;
    }

    public Integer getIsSingle() {
        return isSingle;
    }

    public void setIsSingle(Integer isSingle) {
        this.isSingle = isSingle;
    }


    @Override
    public int hashCode() {
        return questionId == null ? 0 : questionId.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof QuesitonRecommend)) {
            return false;
        }
        QuesitonRecommend q = (QuesitonRecommend) obj;
        return questionId.equals(q.getQuestionId());
    }
}
