package com.noriental.praxissvr.answer.service;

import com.noriental.praxissvr.answer.bean.StudentExercise;

import java.util.List;

/**
 * Created by bluesky on 2016/5/24.
 */
public interface StudentErrorExeService {

    void updateOne(StudentExercise studentExercise);

    void updateBatch(StudentExercise se);

    void creates(List<StudentExercise> seList);

    List<StudentExercise> findByQuesIds(StudentExercise se);
}
