package com.noriental.praxissvr.answer.mappers;

import com.noriental.praxissvr.answer.bean.BrushDataEntity;
import com.noriental.praxissvr.answer.bean.StudentExercise;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author kate
 * @create 2017-12-25 16:22
 * @desc 学生做答、老师批改、答题记录查询表路由处理mapper
 **/
@Repository
public interface AnswerCorrectMapper {

    /**
     * 一键批改批量更新学生做答记录
     * @param list
     * @return
     */
    int batchUpdateCorrect(List<StudentExercise> list);


    /***
     * 查询学生作答记录
     * @param map
     * @return
     */
    List<StudentExercise> findStudentAnswers(Map map);

    /**
     * 批量更新智能批改结果
     * @param list
     * @return
     */
    int updateIntellInfo(List<StudentExercise> list);

    /***
     * 查询学生做答记录
     * @param map
     * @return
     */
    StudentExercise findStudentExerciseInfo(Map map);

    /***
     * 查询智能批改数据
     * @param map
     * @return
     */
    List<StudentExercise> findIntellStudentAnswer(Map map);

    /**
     * 查询任务场景所有学生的做答记录
     * @param map
     * @return
     */

    List<StudentExercise> findStudentAnswerList(Map map);



    boolean  insertDataToCms(BrushDataEntity entity);

}
