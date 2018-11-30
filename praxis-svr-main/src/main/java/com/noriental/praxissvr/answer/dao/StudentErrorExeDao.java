package com.noriental.praxissvr.answer.dao;

import com.noriental.praxissvr.answer.bean.StudentExercise;

import java.util.List;

public interface StudentErrorExeDao {


    boolean insert(StudentExercise se);

    boolean creates(List<StudentExercise> studentExercises);

    /**
     * 根据questionIdList查找所有相关答题记录，单题答题记录及复合题答题记录
     *
     * @return
     */
    List<StudentExercise> findByQuesIds(StudentExercise se);

    boolean update(StudentExercise seInput);

    boolean updateBatch(StudentExercise seInput);

    boolean delete(StudentExercise studentExercises);


}
