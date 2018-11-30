package com.noriental.praxissvr.question.bean;

import java.io.Serializable;

/**
 * @author chenlihua
 * @date 2016/3/8
 * @time 10:58
 */
public class QuestionAudio implements Serializable {
    private String url;
    private String name;
    private String size;

    @Override
    public String toString() {
        return "QuestionAudio{" +
                "url='" + url + '\'' +
                ", name='" + name + '\'' +
                ", size='" + size + '\'' +
                '}';
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
