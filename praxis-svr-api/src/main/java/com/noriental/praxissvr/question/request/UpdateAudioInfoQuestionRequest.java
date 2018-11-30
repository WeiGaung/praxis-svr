package com.noriental.praxissvr.question.request;

import com.noriental.validate.bean.BaseRequest;

import javax.validation.constraints.NotNull;

/**
 * Created by hushuang on 2016/11/21.
 * 更新音频的请求
 */
public class UpdateAudioInfoQuestionRequest extends BaseRequest {
    @NotNull
    private String url;
    private String name;
    @NotNull
    private String size;
    @NotNull
    private Long id;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "UpdateAudioInfoQuestionRequest{" +
                "url='" + url + '\'' +
                ", name='" + name + '\'' +
                ", size='" + size + '\'' +
                ", id=" + id +
                '}';
    }
}
