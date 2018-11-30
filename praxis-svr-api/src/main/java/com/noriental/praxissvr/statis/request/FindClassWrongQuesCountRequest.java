package com.noriental.praxissvr.statis.request;

import com.noriental.praxissvr.statis.bean.WrongQuesSortType;
import com.noriental.validate.bean.BaseRequest;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

public class FindClassWrongQuesCountRequest extends BaseRequest {
    private Long gradeId;
    @NotEmpty
    private List<Long> classIdList;
    @NotNull
    private Long subjectId;
    private Integer quesTypeId;
    private Integer difficulty;
    private Integer knowledgeLevel;
    private Long knowledgeId;
    private Long questionId;
    @NotNull
    private WrongQuesSortType wrongQuesSortType;
    @Min(1)
    private int page;
    @Min(1)
    private int limit;
    @Min(1)
    private Long teacherId;
    private WrongQuesSourceEnum wrongQuesSource = WrongQuesSourceEnum.ALL;

    public FindClassWrongQuesCountRequest(Long subjectId, Long gradeId, List<Long> classIdList, Integer quesTypeId,
                                          Integer difficulty, Long knowledgeId, WrongQuesSortType wrongQuesSortType,
                                          int page, int limit) {
        this.subjectId = subjectId;
        this.gradeId = gradeId;
        this.classIdList = classIdList;
        this.quesTypeId = quesTypeId;
        this.difficulty = difficulty;
        this.knowledgeId = knowledgeId;
        this.wrongQuesSortType = wrongQuesSortType;
        this.page = page;
        this.limit = limit;
    }

    public FindClassWrongQuesCountRequest() {
    }

    public Long getGradeId() {
        return gradeId;
    }

    public void setGradeId(Long gradeId) {
        this.gradeId = gradeId;
    }

    public List<Long> getClassIdList() {
        return classIdList;
    }

    public void setClassIdList(List<Long> classIdList) {
        this.classIdList = classIdList;
    }

    public Integer getQuesTypeId() {
        return quesTypeId;
    }

    public void setQuesTypeId(Integer quesTypeId) {
        this.quesTypeId = quesTypeId;
    }

    public Integer getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }

    public Long getKnowledgeId() {
        return knowledgeId;
    }

    public void setKnowledgeId(Long knowledgeId) {
        this.knowledgeId = knowledgeId;
    }

    public WrongQuesSortType getWrongQuesSortType() {
        return wrongQuesSortType;
    }

    public void setWrongQuesSortType(WrongQuesSortType wrongQuesSortType) {
        this.wrongQuesSortType = wrongQuesSortType;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public Integer getKnowledgeLevel() {
        return knowledgeLevel;
    }

    public void setKnowledgeLevel(Integer knowledgeLevel) {
        this.knowledgeLevel = knowledgeLevel;
    }

    public WrongQuesSourceEnum getWrongQuesSource() {
        return wrongQuesSource;
    }

    public void setWrongQuesSource(WrongQuesSourceEnum wrongQuesSource) {
        this.wrongQuesSource = wrongQuesSource;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public static enum WrongQuesSourceEnum {
        //所有老师发布
        PUBLISH("1"),
        //我的发布
        OWN("1"),
        //学生自主做答
        STUDENT("2"),
        //所有
        ALL("1,2");
        private String source;

        WrongQuesSourceEnum(String source) {
            this.source = source;
        }

        public String getSource() {
            return source;
        }
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }
}
