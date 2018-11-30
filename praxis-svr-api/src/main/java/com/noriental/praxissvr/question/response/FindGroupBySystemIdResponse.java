package com.noriental.praxissvr.question.response;

import com.noriental.praxissvr.question.bean.Group;
import com.noriental.validate.bean.CommonDes;

import java.util.List;

/**
 * @author chenlihua
 * @date 2015/12/23
 * @time 11:13
 */
public class FindGroupBySystemIdResponse extends CommonDes {
    private List<Group> list;

    public List<Group> getList() {
        return list;
    }

    public void setList(List<Group> list) {
        this.list = list;
    }
}
