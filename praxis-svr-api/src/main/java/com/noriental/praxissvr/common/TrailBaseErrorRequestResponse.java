//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.noriental.praxissvr.common;

import com.noriental.utils.json.JsonUtil;
import com.noriental.validate.bean.CommonDes;
import java.util.Map;

public class TrailBaseErrorRequestResponse extends CommonDes {
    Map<String, CountEntity> resultMap = null;

    public TrailBaseErrorRequestResponse() {
    }

    public Map<String, CountEntity> getResultMap() {
        return this.resultMap;
    }

    public void setResultMap(Map<String, CountEntity> resultMap) {
        this.resultMap = resultMap;
    }

    public String toString() {
        return JsonUtil.obj2Json(this);
    }
}
