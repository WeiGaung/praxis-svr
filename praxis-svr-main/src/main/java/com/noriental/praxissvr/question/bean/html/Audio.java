package com.noriental.praxissvr.question.bean.html;

import java.io.Serializable;

/**
 * Created by hushuang on 2016/11/26.
 */
public class Audio implements Serializable{

    private String url;
    private String name;
    private String size;

    public Audio() {
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

    @Override
    public String toString() {
        return "Audio{" +
                "url='" + url + '\'' +
                ", name='" + name + '\'' +
                ", size='" + size + '\'' +
                '}';
    }
}
