package com.noriental.praxissvr.question.response;

import com.noriental.validate.bean.CommonDes;

import java.util.List;
import java.util.Map;

/**
 * Created by shengxian on 2016/12/2.
 */
public class FindParentIdLeafQuesIdsMapResponse  extends CommonDes {
    private  Map<Long,List<Long>> parentIdLeafQuesIdsMap;

    public Map<Long, List<Long>> getParentIdLeafQuesIdsMap() {
        return parentIdLeafQuesIdsMap;
    }

    public void setParentIdLeafQuesIdsMap(Map<Long, List<Long>> parentIdLeafQuesIdsMap) {
        this.parentIdLeafQuesIdsMap = parentIdLeafQuesIdsMap;
    }
}
