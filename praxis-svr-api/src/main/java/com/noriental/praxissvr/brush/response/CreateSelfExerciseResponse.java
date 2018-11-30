package com.noriental.praxissvr.brush.response;

import com.noriental.validate.bean.CommonDes;

import java.io.Serializable;
import java.util.List;

public class CreateSelfExerciseResponse extends CommonDes implements Serializable {
    private static final long serialVersionUID = -970072600096900694L;
    private Long resourceId;//题集id
    //单题或者大题id
    private List<Long> quesIds;

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public List<Long> getQuesIds() {
        return quesIds;
    }

    public void setQuesIds(List<Long> quesIds) {
        this.quesIds = quesIds;
    }

}
