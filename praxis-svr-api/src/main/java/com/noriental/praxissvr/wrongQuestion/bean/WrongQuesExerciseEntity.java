package com.noriental.praxissvr.wrongQuestion.bean;

import com.noriental.praxissvr.answer.bean.StudentExercise;

import java.io.Serializable;
import java.util.List;

/**
 * Created by kate on 2016/12/21.
 * 某学生某道习题答题记录
 */
public class WrongQuesExerciseEntity implements Serializable {

    /**
     * 创建的时间
     **/
    private Long updateTime;

    /***
     * 答题记录列表
     */
    private  List<StudentExercise>  studentExerciseList;

    private boolean isSingle;

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public List<StudentExercise> getStudentExerciseList() {
        return studentExerciseList;
    }

    public void setStudentExerciseList(List<StudentExercise> studentExerciseList) {
        this.studentExerciseList = studentExerciseList;
    }

    public boolean isSingle() {
        return isSingle;
    }

    public void setSingle(boolean single) {
        isSingle = single;
    }

}
