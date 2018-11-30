package com.noriental.praxissvr.question.response;

import com.noriental.praxissvr.question.bean.BlankAndAnswerResult;
import com.noriental.validate.bean.CommonDes;

import java.util.List;

/**
 * Created by hushuang on 2017/3/14.
 */
public class FindBlankAndAnswerResponse extends CommonDes {

    private List<BlankAndAnswerResult> result;

    public FindBlankAndAnswerResponse() {
    }

    public List<BlankAndAnswerResult> getResult() {
        return result;
    }

    public void setResult(List<BlankAndAnswerResult> result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "FindBlankAndAnswerResponse{" +
                "result=" + result +
                '}';
    }
}
