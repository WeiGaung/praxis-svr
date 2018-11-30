package com.noriental.praxissvr;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * @author chenlihua
 * @date 2016/3/31
 * @time 16:44
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Person {
    private String name;
    private int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
