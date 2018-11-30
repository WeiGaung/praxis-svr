package com.noriental.praxissvr.answer.bean;

import com.noriental.utils.json.JsonUtil;

import java.io.Serializable;

/**
 * Created by gaobin on 2018/8/27/029.
 */
public class AnswerBranchParam implements Serializable{
    private String answerBranchUrl;
    private long pythonServerOnlineTime;

    public long getPythonServerOnlineTime() {
        return pythonServerOnlineTime;
    }

    public void setPythonServerOnlineTime(long pythonServerOnlineTime) {
        this.pythonServerOnlineTime = pythonServerOnlineTime;
    }

    public String getAnswerBranchUrl() { return answerBranchUrl;}
    public void setAnswerBranchUrl(String bindKsuOrderInfoUrl) {this.answerBranchUrl = bindKsuOrderInfoUrl;}

    @Override
    public String toString() {
        return JsonUtil.obj2Json(this);
    }
}
