package com.noriental.praxissvr.question.request;

import com.noriental.praxissvr.common.QuestionVisibleEnum;
import com.noriental.praxissvr.question.bean.QuesSearchPerm;
import com.noriental.validate.bean.BaseRequest;

import javax.validation.constraints.Min;
import java.util.List;

/**
 * @author chenlihua
 * @date 2015/12/25
 * @time 10:34
 */
public class FindFacetQuestionCount1Request extends BaseRequest {

    private List<Long> chapterIds;
    private List<Long> moduleIds;
    private List<Long> unitIds;
    private List<Long> topicIds;
    private QuestionVisibleEnum visible = QuestionVisibleEnum.ALL;
    private boolean sameFilter;

    @Min(1)
    private long subjectId;

    private QuesSearchPerm quesSearchPerm;

    public long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(long subjectId) {
        this.subjectId = subjectId;
    }

    public List<Long> getChapterIds() {
        return chapterIds;
    }

    public void setChapterIds(List<Long> chapterIds) {
        this.chapterIds = chapterIds;
    }

    public List<Long> getModuleIds() {
        return moduleIds;
    }

    public void setModuleIds(List<Long> moduleIds) {
        this.moduleIds = moduleIds;
    }

    public List<Long> getUnitIds() {
        return unitIds;
    }

    public void setUnitIds(List<Long> unitIds) {
        this.unitIds = unitIds;
    }

    public List<Long> getTopicIds() {
        return topicIds;
    }

    public void setTopicIds(List<Long> topicIds) {
        this.topicIds = topicIds;
    }

    public QuestionVisibleEnum getVisible() {
        return visible;
    }

    public void setVisible(QuestionVisibleEnum visible) {
        this.visible = visible;
    }

    public boolean isSameFilter() {
        return sameFilter;
    }

    public void setSameFilter(boolean sameFilter) {
        this.sameFilter = sameFilter;
    }

    public QuesSearchPerm getQuesSearchPerm() {
        return quesSearchPerm;
    }

    public void setQuesSearchPerm(QuesSearchPerm quesSearchPerm) {
        this.quesSearchPerm = quesSearchPerm;
    }
}
