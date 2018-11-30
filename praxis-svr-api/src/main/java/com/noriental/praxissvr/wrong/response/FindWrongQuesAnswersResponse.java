package com.noriental.praxissvr.wrong.response;

import com.noriental.praxissvr.answer.bean.StudentExercise;
import com.noriental.validate.bean.CommonDes;
import org.apache.commons.collections4.MapUtils;

import java.util.List;
import java.util.Map;

public class FindWrongQuesAnswersResponse extends CommonDes{
    private Integer pageCount;
    private Integer pageSize;
    private Integer totalCount;
    private Map<Long,List<StudentExercise>> longSeListMap;

    public Integer getPageCount() {
        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Map<Long, List<StudentExercise>> getLongSeListMap() {
        return longSeListMap;
    }

    public void setLongSeListMap(Map<Long, List<StudentExercise>> longSeListMap) {
        this.longSeListMap = longSeListMap;
    }

    @Override
    public String toString() {
        return "FindWrongQuesAnswersResponse{" +
                "pageCount=" + pageCount +
                ", pageSize=" + pageSize +
                ", totalCount=" + totalCount +
                ", longSeListMap=" + (MapUtils.isNotEmpty(longSeListMap) ? longSeListMap.size() : "null") +
                '}';
    }
}
