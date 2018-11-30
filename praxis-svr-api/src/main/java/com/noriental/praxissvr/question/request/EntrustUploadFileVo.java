package com.noriental.praxissvr.question.request;

import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;

/**
 * Created by shengxian on 2016/11/22.
 */
public class EntrustUploadFileVo implements Serializable {
    @NotBlank
    private String fileName;
    @NotBlank
    private String qiniuUrl;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getQiniuUrl() {
        return qiniuUrl;
    }

    public void setQiniuUrl(String qiniuUrl) {
        this.qiniuUrl = qiniuUrl;
    }
}
