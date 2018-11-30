package com.noriental.praxissvr.question.bean;

/**
 * Created by hushuang on 2016/11/25.
 */
public class LinkExerciseQuestion {

    private Long id;
    private long exerciseId;//题集ID
    private long questionId;//习题ID
    private int sequencel;//习题排序
    private int questionTypeId;//习题类型
    private int exerciseType;//场景模板
    private long chapter1Id;//一级章节ID
    private long chapter2Id;//二级章节ID
    private long chapter3Id;//三级章节ID

    public LinkExerciseQuestion() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(long exerciseId) {
        this.exerciseId = exerciseId;
    }

    public long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(long questionId) {
        this.questionId = questionId;
    }

    public int getSequencel() {
        return sequencel;
    }

    public void setSequencel(int sequencel) {
        this.sequencel = sequencel;
    }

    public int getQuestionTypeId() {
        return questionTypeId;
    }

    public void setQuestionTypeId(int questionTypeId) {
        this.questionTypeId = questionTypeId;
    }

    public int getExerciseType() {
        return exerciseType;
    }

    public void setExerciseType(int exerciseType) {
        this.exerciseType = exerciseType;
    }

    public long getChapter1Id() {
        return chapter1Id;
    }

    public void setChapter1Id(long chapter1Id) {
        this.chapter1Id = chapter1Id;
    }

    public long getChapter2Id() {
        return chapter2Id;
    }

    public void setChapter2Id(long chapter2Id) {
        this.chapter2Id = chapter2Id;
    }

    public long getChapter3Id() {
        return chapter3Id;
    }

    public void setChapter3Id(long chapter3Id) {
        this.chapter3Id = chapter3Id;
    }

    @Override
    public String toString() {
        return "LinkExerciseQuestion{" +
                "id=" + id +
                ", exerciseId=" + exerciseId +
                ", questionId=" + questionId +
                ", sequencel=" + sequencel +
                ", questionTypeId=" + questionTypeId +
                ", exerciseType=" + exerciseType +
                ", chapter1Id=" + chapter1Id +
                ", chapter2Id=" + chapter2Id +
                ", chapter3Id=" + chapter3Id +
                '}';
    }
}
