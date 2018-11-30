package com.noriental.praxissvr.question.bean;

import java.io.Serializable;

/**
 * Created by shengxian on 2016/12/20.
 * 题目查询权限，其它私立机构查询不到
 */
public class QuesSearchPerm implements Serializable {
    private long currentOrgId;
    private int currentOrgType;

    public QuesSearchPerm() {

    }

    public QuesSearchPerm(long currentOrgId, int currentOrgType) {
        this.currentOrgId = currentOrgId;
        this.currentOrgType = currentOrgType;
    }

    public long getCurrentOrgId() {
        return currentOrgId;
    }

    public void setCurrentOrgId(long currentOrgId) {
        this.currentOrgId = currentOrgId;
    }

    public int getCurrentOrgType() {
        return currentOrgType;
    }

    public void setCurrentOrgType(int currentOrgType) {
        this.currentOrgType = currentOrgType;
    }
}
