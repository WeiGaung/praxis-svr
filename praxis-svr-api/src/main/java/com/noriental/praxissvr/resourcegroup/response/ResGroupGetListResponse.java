package com.noriental.praxissvr.resourcegroup.response;

import com.noriental.praxissvr.resourcegroup.bean.ResourceGroup;
import com.noriental.validate.bean.CommonDes;

import java.util.List;

/**
 * Created by kate on 2016/11/18.
 */
public class ResGroupGetListResponse extends CommonDes {

    private List<ResourceGroup> group_list;
    private int default_num;

    public List<ResourceGroup> getGroup_list() {
        return group_list;
    }

    public void setGroup_list(List<ResourceGroup> group_list) {
        this.group_list = group_list;
    }

    public int getDefault_num() {
        return default_num;
    }

    public void setDefault_num(int default_num) {
        this.default_num = default_num;
    }
}
