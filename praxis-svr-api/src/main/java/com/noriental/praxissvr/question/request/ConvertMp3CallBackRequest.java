package com.noriental.praxissvr.question.request;

import com.noriental.validate.bean.BaseRequest;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Created by luozukai on 2016/11/25.
 */
public class ConvertMp3CallBackRequest extends BaseRequest{
    // 音频的数据库id
    @NotBlank
    private String entityId;

    // 七牛回调的json数据
    @NotBlank
    private String jsonStatusDescription;

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getJsonStatusDescription() {
        return jsonStatusDescription;
    }

    public void setJsonStatusDescription(String jsonStatusDescription) {
        this.jsonStatusDescription = jsonStatusDescription;
    }


}
