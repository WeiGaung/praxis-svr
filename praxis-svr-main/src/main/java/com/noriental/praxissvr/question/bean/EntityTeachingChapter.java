package com.noriental.praxissvr.question.bean;

/**
 * Created by hushuang on 2017/4/11.
 */
public class EntityTeachingChapter {
    private Long id;
    //教材体系id
    private long directoryId;
    //父级id，指向本表
    private long parentId;
    //章节前缀
    private String prefixName;
    //章节名称
    private String name;
    //'章节序号，用于排序
    private int chapterNumber;
    //章节深度，章为1，结为2，子结为3，以此类推
    private int level;
    //学段id，与directory对应的stage_id一致
    private long stageId;
    //学科id，与directory的subject_id一致
    private long subjectId;

    public EntityTeachingChapter() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getDirectoryId() {
        return directoryId;
    }

    public void setDirectoryId(long directoryId) {
        this.directoryId = directoryId;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public String getPrefixName() {
        return prefixName;
    }

    public void setPrefixName(String prefixName) {
        this.prefixName = prefixName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getChapterNumber() {
        return chapterNumber;
    }

    public void setChapterNumber(int chapterNumber) {
        this.chapterNumber = chapterNumber;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public long getStageId() {
        return stageId;
    }

    public void setStageId(long stageId) {
        this.stageId = stageId;
    }

    public long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(long subjectId) {
        this.subjectId = subjectId;
    }

    @Override
    public String toString() {
        return "EntityTeachingChapter{" +
                "id=" + id +
                ", directoryId=" + directoryId +
                ", parentId=" + parentId +
                ", prefixName='" + prefixName + '\'' +
                ", name='" + name + '\'' +
                ", chapterNumber=" + chapterNumber +
                ", level=" + level +
                ", stageId=" + stageId +
                ", subjectId=" + subjectId +
                '}';
    }
}
