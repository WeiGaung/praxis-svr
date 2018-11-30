package com.noriental.praxissvr.statis.response;

import com.noriental.praxissvr.statis.bean.StuWorkStatis;
import com.noriental.validate.bean.CommonDes;

import java.io.Serializable;
import java.util.List;

/**
 * Created by bluesky on 2016/7/1.
 */
public class FindStuWorkSatisResponse  extends CommonDes implements Serializable {
    private List<StuWorkStatis> stuWorkStatisList;

    public List<StuWorkStatis> getStuWorkStatisList() {
        return stuWorkStatisList;
    }

    public void setStuWorkStatisList(List<StuWorkStatis> stuWorkStatisList) {
        this.stuWorkStatisList = stuWorkStatisList;
    }
}
