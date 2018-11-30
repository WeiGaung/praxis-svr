package com.noriental.praxissvr.wrong.service;


import com.noriental.praxissvr.answer.bean.StudentExercise;

import java.util.List;

/**
 * Created by bluesky on 2016/7/6.
 */
public interface StuRedoTempService {

    /**
     * 作答时，生成消灭错题的答题记录----》数据存放在答题记录表  entity_student_exercise
     * @param allSeList
     */
    void createRedoStuExes(List<StudentExercise> allSeList);

    /**
     * 批改时，生成消灭错题的答题记录
     * @param se
     * @param answerChalService
     * @param parentBatchId
     */
    void createRedoStuExesCorrect(StudentExercise se, AnswerChalService answerChalService, Long parentBatchId);
}
