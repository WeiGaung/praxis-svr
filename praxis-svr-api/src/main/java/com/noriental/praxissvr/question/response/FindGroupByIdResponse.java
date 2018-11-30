package com.noriental.praxissvr.question.response;

import com.noriental.praxissvr.question.bean.Group;
import com.noriental.validate.bean.CommonDes;

/**
 * @author chenlihua
 * @date 2015/12/23
 * @time 11:17
 */
public class FindGroupByIdResponse extends CommonDes {

    private Group group;

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }
}
