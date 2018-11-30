package com.noriental.praxissvr.question.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.noriental.validate.bean.CommonDes;

/**
 * Created by hushuang on 2016/12/30.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonResult extends CommonDes {


    /**
     * 习题Id
     */
    private  Long id;

    private boolean myCollection;

    public boolean isMyCollection() {
        return myCollection;
    }

    public void setMyCollection(boolean myCollection) {
        this.myCollection = myCollection;
    }

    public CommonResult() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


}
