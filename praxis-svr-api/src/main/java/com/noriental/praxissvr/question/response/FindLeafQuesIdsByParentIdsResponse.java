package com.noriental.praxissvr.question.response;

import com.noriental.validate.bean.CommonDes;

import java.util.List;

/**
 * @author chenlihua
 * @date 2015/12/25
 * @time 10:38
 */
public class FindLeafQuesIdsByParentIdsResponse extends CommonDes {
    private List<Long> leafQuesIds;

    public List<Long> getLeafQuesIds() {
        return leafQuesIds;
    }

    public void setLeafQuesIds(List<Long> leafQuesIds) {
        this.leafQuesIds = leafQuesIds;
    }
}
