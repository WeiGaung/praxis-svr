package com.noriental.praxissvr.resourcegroup.response;

import com.noriental.validate.bean.CommonDes;

/**
 * Created by kate on 2016/11/21.
 */
public class ResGroupCreateResponse extends CommonDes {

    private Long id;
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
