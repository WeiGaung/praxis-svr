package com.noriental.praxissvr.wrongQuestion.service;

import com.noriental.praxissvr.answer.bean.StudentExercise;
import com.noriental.praxissvr.wrongQuestion.request.*;
import com.noriental.praxissvr.wrongQuestion.response.*;
import com.noriental.validate.exception.BizLayerException;

import java.util.List;

/**
 * Created by kate on 2016/12/16.
 * 错题本业务服务接口
 */
public interface WrongQuestionService {

    /**
     * 根据学生ID返回按照学科统计的错题个数数据
     *
     * @param wrongQuesGroupReq
     * @return
     * @throws BizLayerException
     */
    WrongQuesSubjectStatisResp findWrongQuestionGroup(WrongQuesSubjectStatisReq wrongQuesGroupReq) throws
            BizLayerException;

    /***
     * 根据章节、分册、学生ID、学科分页查询习题、错误数、答题来源
     * @param wrongQuesChapQueryReq
     * @return
     * @throws BizLayerException
     */
    @Deprecated
    WrongQuesChapQueryResp findWrongQuestionChapter(WrongQuesChapQueryReq wrongQuesChapQueryReq) throws
            BizLayerException;

    /***
     * 根据学生ID、课程ID、习题ID查询该学生错题历史记录
     * @param wrongQuestionHisReq
     * @return
     * @throws BizLayerException
     */

    WrongQuestionHisResp findWrongQuestionHis(WrongQuestionHisReq wrongQuestionHisReq) throws BizLayerException;

    /***
     * 错误习题按照章节分页查询
     */
    WrongQuesQueryResp findWrongQuestion(WrongQuesQueryReq wrongQuesQueryReq) throws BizLayerException;

    /**
     * 错误习题按照章节分页查询
     *
     * @param wrongQuesQueryReq
     * @return
     * @throws BizLayerException
     */
    WrongQuesQueryResp findRealWrongQuestion(WrongQuesQueryReq wrongQuesQueryReq) throws BizLayerException;


    /***
     * 根据学生ID、知识点或章节查询学生的错题
     * @param wrongQuesListReq
     * @return
     * @throws BizLayerException
     */
    WrongQuesListResp findWrongQuestions(WrongQuesListReq wrongQuesListReq) throws BizLayerException;

    /***
     * 根据学生ID、知识点或章节查询学生的错题(复合题)
     * @param wrongQuesListReq
     * @return
     * @throws BizLayerException
     */
    WrongQuesListResp findWrongParentQuestions(WrongQuesListReq wrongQuesListReq) throws BizLayerException;

    /**
     * 根据学生ID和所有错题ID查询错题次数
     *
     * @param wrongQuesNumQueryReq
     * @return
     * @throws BizLayerException
     */
    WrongQuesNumQueryResp findWrongQuestionsNum(WrongQuesNumQueryReq wrongQuesNumQueryReq) throws BizLayerException;

    /**
     * 根据资源ID、答题场景查询学生的做答记录
     *
     * @param exercises
     * @return
     * @throws BizLayerException
     */
    List<StudentExercise> findStudentAnswer(StudentExercise exercises) throws BizLayerException;

    /**
     * 批量更新智能批改数据
     *
     * @param list
     * @throws BizLayerException
     */

    void updateIntellInfo(List<StudentExercise> list) throws BizLayerException;

    /***
     * 根据表名和ID查询做答记录
     * @param tableName
     * @param id
     * @return
     * @throws BizLayerException
     */

    StudentExercise findStudentExerciseInfo(String tableName, Long id) throws BizLayerException;

    /**
     * 根据题集ID获取学生智能批改结果
     *
     * @param exercises
     * @return
     * @throws BizLayerException
     */

    List<StudentExercise> findIntellStudentAnswer(StudentExercise exercises) throws BizLayerException;

    /**
     * 教师空间学生知识点章节错题次数统计
     *
     * @param req
     * @return
     * @throws BizLayerException
     */
    WrongQuesNumQueryResp findErrorQuestionsNum(ErrorQuesChapQueryReq req) throws BizLayerException;

    /**
     * 某学生某题的所有错题记录
     *
     * @param wrongQuestionHisReq
     * @return
     * @throws BizLayerException
     */
    WrongQuestionsResp findWrongQuestions(WrongQuestionHisReq wrongQuestionHisReq) throws BizLayerException;


}
