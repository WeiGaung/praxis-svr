package com.noriental.praxissvr.question.response;

import com.noriental.validate.bean.CommonDes;

import java.util.HashMap;
import java.util.Map;

/**
 * @author chenlihua
 * @date 2015/12/25
 * @time 10:38
 */
public class FindFacetQuestionCountResponse extends CommonDes {
    private Map<Long, Long> chapterFacet = new HashMap<>();
    private Map<Long, Long> moduleFacet = new HashMap<>();
    private Map<Long, Long> unitFacet = new HashMap<>();
    private Map<Long, Long> topicFacet = new HashMap<>();

    public Map<Long, Long> getChapterFacet() {
        return chapterFacet;
    }

    public void setChapterFacet(Map<Long, Long> chapterFacet) {
        this.chapterFacet = chapterFacet;
    }

    public Map<Long, Long> getModuleFacet() {
        return moduleFacet;
    }

    public void setModuleFacet(Map<Long, Long> moduleFacet) {
        this.moduleFacet = moduleFacet;
    }

    public Map<Long, Long> getUnitFacet() {
        return unitFacet;
    }

    public void setUnitFacet(Map<Long, Long> unitFacet) {
        this.unitFacet = unitFacet;
    }

    public Map<Long, Long> getTopicFacet() {
        return topicFacet;
    }

    public void setTopicFacet(Map<Long, Long> topicFacet) {
        this.topicFacet = topicFacet;
    }
}
