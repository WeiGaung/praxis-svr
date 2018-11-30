package com.noriental.praxissvr.statis.bean;

import java.io.Serializable;


public class KnowledgeVo implements Serializable {
    private Long knowledgeId;
    private Integer knowledgeLevel;
    private String knowledgeName;
    private Long praKnowledgeId;

    public KnowledgeVo() {
    }

    public KnowledgeVo(Long knowledgeId, Integer knowledgeLevel, String knowledgeName, Long praKnowledgeId) {
        this.knowledgeId = knowledgeId;
        this.knowledgeLevel = knowledgeLevel;
        this.knowledgeName = knowledgeName;
        this.praKnowledgeId = praKnowledgeId;
    }

    public Long getKnowledgeId() {
        return knowledgeId;
    }

    public void setKnowledgeId(Long knowledgeId) {
        this.knowledgeId = knowledgeId;
    }

    public Integer getKnowledgeLevel() {
        return knowledgeLevel;
    }

    public void setKnowledgeLevel(Integer knowledgeLevel) {
        this.knowledgeLevel = knowledgeLevel;
    }

    public String getKnowledgeName() {
        return knowledgeName;
    }

    public void setKnowledgeName(String knowledgeName) {
        this.knowledgeName = knowledgeName;
    }

    public Long getPraKnowledgeId() {
        return praKnowledgeId;
    }

    public void setPraKnowledgeId(Long praKnowledgeId) {
        this.praKnowledgeId = praKnowledgeId;
    }
}
