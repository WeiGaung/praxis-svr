package com.noriental.praxissvr.wrongQuestion.response;

import com.noriental.praxissvr.answer.bean.StudentExercise;
import com.noriental.validate.bean.CommonDes;

import java.util.List;

/**
 * @author kate
 * @create 2017-10-27 14:15
 * @desc 根据题目ID和学生ID查询学生的所有错题
 **/
public class WrongQuestionsResp extends CommonDes{

   private List<StudentExercise> resultList;

    public List<StudentExercise> getResultList() {
        return resultList;
    }

    public void setResultList(List<StudentExercise> resultList) {
        this.resultList = resultList;
    }
}
