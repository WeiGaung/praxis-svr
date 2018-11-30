package com.noriental.praxissvr.question.bean;

import java.io.Serializable;

/**
 * Created by hushuang on 2016/12/22.
 */
public class TypeHtml implements Serializable{
    private int id;
    private String name;

    public TypeHtml() {
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "TypeHtml{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
