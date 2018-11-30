package com.noriental.praxissvr.question.response;

import com.noriental.praxissvr.question.bean.CusDirQuestion;
import com.noriental.validate.bean.CommonDes;

import java.util.List;

/**
 * Created by hushuang on 2017/7/28.
 */
public class QueryCusDirResponse extends CommonDes {

    private List<CusDirQuestion> data;

    public QueryCusDirResponse() {
    }

    public QueryCusDirResponse(List<CusDirQuestion> data) {
        this.data = data;
    }

    public List<CusDirQuestion> getData() {
        return data;
    }

    public void setData(List<CusDirQuestion> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "QueryCusDirResponse{" +
                "data=" + data +
                '}';
    }
}
