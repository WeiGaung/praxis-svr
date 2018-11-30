package com.noriental.praxissvr.answer.response;

import com.noriental.praxissvr.answer.bean.IntellCorrectEntity;
import com.noriental.validate.bean.CommonDes;

import java.util.List;

/**
 * Created by kate on 2017/3/13.
 * 智能批改学生某道习题的智能批改结果集
 */
public class IntellCorrectAnswerResponse extends CommonDes {

    /***
     * 学生ID
     */
    private Long studentId;

    private String exerciseSource;//答题场景

    /***
     * 题集ID/题集发布id
     */
    private Long resourceId;
    /***
     * 智能批改结果集
     */
    List<IntellCorrectEntity> resultList;

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getExerciseSource() {
        return exerciseSource;
    }

    public void setExerciseSource(String exerciseSource) {
        this.exerciseSource = exerciseSource;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public List<IntellCorrectEntity> getResultList() {
        return resultList;
    }

    public void setResultList(List<IntellCorrectEntity> resultList) {
        this.resultList = resultList;
    }
}
