package com.noriental.praxissvr.question.bean.questionpart;

import com.noriental.framework.base.BaseBean;

/**
 * Created by liujiang on 2018/5/17.
 */
public class QuestionStateResponse extends BaseBean {
    private static final long serialVersionUID = -2161937527053849446L;
    private Long id;
    private String state;

    @Override
    public String toString() {
        return "QuestionStateResponse{" +
                "id=" + id +
                ", state='" + state + '\'' +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
