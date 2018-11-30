package com.noriental.praxissvr.question.request;

import com.noriental.validate.bean.BaseRequest;

import java.util.List;

/**
 * Created by hushuang on 2017/3/14.
 */
public class StateQuestionRequest extends BaseRequest{

    /**
     * 根据题目ID批量关闭题目
     */
    private List<Long> requestIds;

    public StateQuestionRequest() {
    }

    public List<Long> getRequestIds() {
        return requestIds;
    }

    public void setRequestIds(List<Long> requestIds) {
        this.requestIds = requestIds;
    }


}
