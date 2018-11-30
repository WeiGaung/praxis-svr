package com.noriental.praxissvr.question.bean;

import java.io.Serializable;

/**
 * 试题内容父类
 *
 * @author xiangfei
 * @date 2015年9月18日 上午9:42:42
 */
public class QuestionContent implements Serializable {

    private static final long serialVersionUID = 1769723437157879326L;

    private String c;

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }
}
