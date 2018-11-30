package com.noriental.praxissvr.question.request;

import com.noriental.validate.bean.BaseRequest;

import java.util.List;

/**
 * Created by hushuang on 2017/4/7.
 * 批量查询题目基本信息的请求
 */
public class BatchQueryQuestionsRequest extends BaseRequest {

    /**
     * 题目IDS
     */
    private List<Long> ids;

    public BatchQueryQuestionsRequest() {
    }

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    @Override
    public String toString() {
        return "BatchQueryQuestionsRequest{" +
                "ids=" + ids +
                '}';
    }
}
