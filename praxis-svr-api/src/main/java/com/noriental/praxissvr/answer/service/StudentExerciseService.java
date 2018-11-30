package com.noriental.praxissvr.answer.service;

import com.noriental.praxissvr.answer.bean.OperateType;
import com.noriental.praxissvr.answer.bean.StudentExercise;

import java.util.List;

public interface StudentExerciseService {


    /****
     * 获取答题记录数据
     * @param studentExercise
     * @return
     */
    StudentExercise getRecord(StudentExercise studentExercise);

    void updateRecord(StudentExercise studentExercise, OperateType operateType);
    void updateRecord(StudentExercise studentExercise, OperateType operateType,boolean isMQ);

    //void updateAudioResult(StudentExercise studentExercise);

    List<StudentExercise> getDbRecords(StudentExercise studentExercise);

    List<StudentExercise> getMysqlRecords(StudentExercise studentExercise);

    /**
     * 创建答题记录：来源有预习，上课，作业，测评，自主练习，错题强化，消灭错题
     *
     * @param seList
     */
    void createRecords(List<StudentExercise> seList, OperateType operateType, StudentExercise currentData,boolean isMQ);

    void sendAudioCalbackMq(StudentExercise studentExercise, OperateType operateType);

    void createRecords(List<StudentExercise> seList);

    List<StudentExercise> getFromCacheStuQueses(StudentExercise se);

    void updateBatch(StudentExercise se);
    void updateBatch(StudentExercise se, boolean isMQ);

    void updateIntellInfo(List<StudentExercise> seList);
    void updateIntellInfo(List<StudentExercise> seList, boolean isMQ);

    void getTableRoute(List<StudentExercise> seList);


    void deleteErrorQuestionChallenge(StudentExercise se);


    List<StudentExercise> getHistoryDbRecords(StudentExercise studentExercise);

    void mqIntellCorrectInfo(List<StudentExercise> studentExerciseList);
    void mqAutoIntellCorrectInfo(List<StudentExercise> studentExerciseList);



}
