package com.noriental.praxissvr.question.request;

import com.noriental.praxissvr.question.bean.LinkCusQuesRes;
import com.noriental.validate.bean.BaseRequest;

import java.util.List;

/**
 * Created by hushuang on 2017/8/10.
 * 题目挂接自定义目录请求
 */
public class InsertCusDirRequest extends BaseRequest {
    /*
        批量插入自定义目录和题目的关联数据
     */
    private List<LinkCusQuesRes> linkCusQuesRes;

    public InsertCusDirRequest() {
    }

    public List<LinkCusQuesRes> getLinkCusQuesRes() {
        return linkCusQuesRes;
    }

    public void setLinkCusQuesRes(List<LinkCusQuesRes> linkCusQuesRes) {
        this.linkCusQuesRes = linkCusQuesRes;
    }

    @Override
    public String toString() {
        return "InsertCusDirRequest{" +
                "linkCusQuesRes=" + linkCusQuesRes +
                '}';
    }
}
