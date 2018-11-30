package com.noriental.praxissvr.answer.dao;

import com.noriental.praxissvr.answer.bean.StudentExercise;

import java.util.List;

public interface StudentExerciseDao {

    /**
     * 插入学生作答记录
     * @param se
     * @return
     */
    boolean insert(StudentExercise se);

    /**
     * 批量插入学生做答记录
     * @param studentExercises
     * @return
     */
    boolean creates(List<StudentExercise> studentExercises);

    /**
     * 查询数量
     *
     * @param studentExercises
     * @return
     */
    int getCountByStuExeList(List<StudentExercise> studentExercises);

    /**
     * 查询作答记录
     *
     * @param studentExercise
     * @return
     */
    List<StudentExercise> findByStudentExercise(StudentExercise studentExercise);

    /**
     * 批量更新
     *
     * @param studentExerciseList
     * @return
     */
    boolean updataByStuExes(List<StudentExercise> studentExerciseList);


    List<StudentExercise> findListByStuExeList(List<StudentExercise> studentExerciseList);

    boolean update(StudentExercise seInput);

    /**
     * 重复批改数据更新
     * @param seInput
     * @return
     */
    boolean updateRepeatCorrect(StudentExercise seInput);

    /**
     * 批量更新
     * @param seInput
     * @return
     */
    boolean updateBatch(StudentExercise seInput);

    /**
     * 更新智能批改信息
     * @param se
     * @return
     */
    boolean updateIntellInfo(StudentExercise se);

    /**
     * 删除错题挑战重复批改为对的数据
     */
    boolean deleteErrorQuestionChallenge(StudentExercise se);

    /**
     * 查询所有题所有学生的答题记录复合题
     * @param studentExercise
     * @return
     */
    List<StudentExercise> findHistoryData(StudentExercise studentExercise);

}
