package com.noriental.praxissvr.statis.response;

import com.noriental.validate.bean.CommonDes;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by bluesky on 2016/5/12.
 */
public class FindClassQuesReportResponse extends CommonDes implements Serializable{
   Map<Long,FindClassQuesReport> quesReportMap;

    public Map<Long, FindClassQuesReport> getQuesReportMap() {
        return quesReportMap;
    }

    public void setQuesReportMap(Map<Long, FindClassQuesReport> quesReportMap) {
        this.quesReportMap = quesReportMap;
    }
}
