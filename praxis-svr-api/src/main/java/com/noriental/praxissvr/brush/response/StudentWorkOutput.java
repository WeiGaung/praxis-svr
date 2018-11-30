package com.noriental.praxissvr.brush.response;

import java.io.Serializable;
import java.util.List;

import com.noriental.validate.bean.CommonDes;

/**
 * 创建新作业输出
 *
 * @author shengxian.xiao
 */
@Deprecated
public class StudentWorkOutput extends CommonDes implements Serializable {
    private static final long serialVersionUID = -970072600096900694L;
    //作业id
    private Long workId;
    //单题或者大题id
    private List<Long> quesIds;

    public Long getWorkId() {
        return workId;
    }

    public void setWorkId(Long workId) {
        this.workId = workId;
    }

    public List<Long> getQuesIds() {
        return quesIds;
    }

    public void setQuesIds(List<Long> quesIds) {
        this.quesIds = quesIds;
    }

}
