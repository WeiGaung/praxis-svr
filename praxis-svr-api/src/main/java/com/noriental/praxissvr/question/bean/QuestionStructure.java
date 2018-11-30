package com.noriental.praxissvr.question.bean;

import java.io.Serializable;

/**
 * @author chenlihua
 * @date 2016/3/8
 * @time 14:41
 */
public class QuestionStructure implements Serializable {
    private Long id;
    private long structId;
    private String name;
    private String type;
    private String info;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getStructId() {
        return structId;
    }

    public void setStructId(long structId) {
        this.structId = structId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
