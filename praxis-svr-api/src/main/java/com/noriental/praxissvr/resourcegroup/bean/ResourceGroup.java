package com.noriental.praxissvr.resourcegroup.bean;

import java.io.Serializable;

/**
 * Created by kate on 2016/11/18.
 */
public class ResourceGroup implements Serializable {

    private Long id;
    private String name;
    private int num;

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

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
