package com.noriental.praxissvr.answer.service;

import com.noriental.praxissvr.answer.bean.StudentExercise;
import com.noriental.praxissvr.brush.bean.StudentWorkAnswer;
import com.noriental.praxissvr.question.bean.Question;
import com.noriental.praxissvr.wrong.bean.SimpleKnowVo;
import com.noriental.trailsvr.bean.AnswAndResultSatisByChapter;

import java.util.List;
import java.util.Map;

public interface StuAnswerService {


    /**
     * 通知统计：
     * 上课、刷题给同步习题id和workId，subjectId
     * 作业测评给historyId
     */
    void notifyTrail(StudentExercise se, int operaType, StudentExercise existedRecord) throws Exception;


    /**
     * 作答时更新知识点的已做和做对数量
     *
     * @param studentWorkAnswersKnow 作答的答题数据
     * @param quesMap                题目
     * @param studentId              学生id
     * @param topicKnowMap           所有题目的知识点
     */
    @Deprecated
    void statisAnsw(List<StudentWorkAnswer> studentWorkAnswersKnow, Map<Long, Question> quesMap, Long studentId,
                    Map<Long, SimpleKnowVo> topicKnowMap);

    /**
     * 批改时更新知识点做对数量
     *
     * @param correctQuesId         当前批改题目id
     * @param studentWorkAnswerKnow 批改题目的答题数据（批改题目是小题时，为复合题的答题数据）
     * @param topicKnowMap          题目的知识点
     */
    @Deprecated
    void statisCorrect(Long correctQuesId, StudentWorkAnswer studentWorkAnswerKnow, Map<Long, SimpleKnowVo>
            topicKnowMap);

    /**
     * 批改时更新章节的已做和做对数量
     *
     * @param se
     * @param quesMap
     * @return
     */
    @Deprecated
    Map<Long, AnswAndResultSatisByChapter> statisAnswCorrectChapter(StudentExercise se, Map<Long, Question> quesMap);

    /***
     * 智能批改时更新章节的已做和做对数量
     * @param list
     * @param quesMap
     * @return
     */
    @Deprecated
    Map<Long, AnswAndResultSatisByChapter> statisAnswCorrectChapter(List<StudentExercise> list, Map<Long, Question>
            quesMap);

    /**
     * 作答时更新章节的已做和做对数量
     *
     * @param studentId
     * @param seList
     * @param questionMap
     * @return
     */
    @Deprecated
    Map<Long, AnswAndResultSatisByChapter> statisAnswCorrectChapter(Long studentId, List<StudentExercise> seList,
                                                                    Map<Long, Question> questionMap);




}
