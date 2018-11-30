package com.noriental.praxissvr.question.bean;

import java.io.Serializable;

/**
 * Created by hushuang on 2016/12/22.
 */
public class CommonAudio implements Serializable{
    private String url;
    private String name;
    private String size;

    public CommonAudio() {
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
        return "CommonAudio{" +
                "url='" + url + '\'' +
                ", name='" + name + '\'' +
                ", size='" + size + '\'' +
                '}';
    }
}
