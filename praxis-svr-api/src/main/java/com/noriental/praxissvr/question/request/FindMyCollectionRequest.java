package com.noriental.praxissvr.question.request;

import com.noriental.validate.bean.BaseRequest;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Created by hushuang on 2016/11/21.
 * 更新习题的请求
 */
public class FindMyCollectionRequest extends BaseRequest {
    @NotNull
    private long id;//习题ID

    @Min(1)
    private long uploadId;//上传人ID


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUploadId() {
        return uploadId;
    }

    public void setUploadId(long uploadId) {
        this.uploadId = uploadId;
    }
}
