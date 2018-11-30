package com.noriental.praxissvr.answer.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.noriental.praxissvr.answer.bean.StudentExercise;
import com.noriental.validate.bean.CommonDes;

import java.util.List;

/**
 * Created by kate on 2017/9/18.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FindHistoryDataResponse extends CommonDes {

    private List<StudentExercise> studentExerciseList;

    public List<StudentExercise> getStudentExerciseList() {
        return studentExerciseList;
    }

    public void setStudentExerciseList(List<StudentExercise> studentExerciseList) {
        this.studentExerciseList = studentExerciseList;
    }
}
