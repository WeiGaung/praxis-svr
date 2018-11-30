package com.noriental.praxissvr.wrong.service;


import com.noriental.praxissvr.answer.bean.StudentExercise;
import com.noriental.praxissvr.brush.bean.StudentWorkAnswer;
import com.noriental.praxissvr.common.TrailBaseErrorRequestRequest;
import com.noriental.praxissvr.common.TrailBaseErrorRequestResponse;
import com.noriental.praxissvr.wrong.bean.SimpleKnowVo;
import com.noriental.praxissvr.wrong.request.DeleteWrongQuesRequest;
import com.noriental.praxissvr.wrong.request.FindWrongQuesAnswersRequest;
import com.noriental.praxissvr.wrong.response.FindWrongQuesAnswersResponse;
import com.noriental.validate.bean.CommonDes;
import com.noriental.validate.exception.BizLayerException;

import java.util.List;
import java.util.Map;

public interface StuQuesKnowledgeService {

    /****
     * 查找错题统计数据
     *
     * @param request
     * @return
     * @throws BizLayerException
     */
    TrailBaseErrorRequestResponse findTrailBaseError(TrailBaseErrorRequestRequest request) throws BizLayerException;

    /****
     *  错题明细
     * 分页查询某个学生某个知识点或者章节下面某些题目答题记录+答题详情
     * @param request
     * @return
     * @throws BizLayerException
     */
    FindWrongQuesAnswersResponse findWrongQuesAnswers(FindWrongQuesAnswersRequest request) throws BizLayerException;

    /***
     *   根据入参物理删除这entity_stu_ques_knowledge张表的数据
     * @param in
     * @return
     * @throws BizLayerException
     */
    CommonDes deleteWrongQues(DeleteWrongQuesRequest in) throws BizLayerException;

    /**
     * 答题 记录知识点或者章节已做做错题目
     *
     * @param studentWorkAnswer
     * @param knowOrChapterMap
     * @throws BizLayerException
     */
    void recordAnsweredWrongQues(List<StudentWorkAnswer> studentWorkAnswer, Map<Long, SimpleKnowVo> knowOrChapterMap)
            throws BizLayerException;

    /***
     * 学生做答知识点或者章节错题做对状态修改
     * @param studentWorkAnswer
     * @param knowOrChapterMap
     * @throws BizLayerException
     */
    void recordAnsweredRightQues(List<StudentWorkAnswer> studentWorkAnswer, Map<Long, SimpleKnowVo> knowOrChapterMap)
            throws BizLayerException;

    /**
     * 批改   记录知识点或者章节错题
     *
     * @param studentWorkAnswers
     * @param knowOrChapterMap
     * @throws BizLayerException
     */
    void recordWrongQues(StudentWorkAnswer studentWorkAnswers, Map<Long, SimpleKnowVo> knowOrChapterMap) throws
            BizLayerException;

    /***
     * 老师批改记录知识点或者章节做错后重新做对状态修改
     * @param studentWorkAnswers
     * @param knowOrChapterMap
     * @param dataStatu
     * @throws BizLayerException
     */
    @Deprecated
    void recordWrightQues(StudentWorkAnswer studentWorkAnswers, Map<Long, SimpleKnowVo> knowOrChapterMap, int
            dataStatu) throws BizLayerException;

    /***
     * 学生在智批的基础上重复批改和老师重复批改由错批改为对，修改错题状态
     * @param se
     * @throws BizLayerException
     */
    void updataStuQueKnowledgeStatu(StudentExercise se,int dataStatu) throws BizLayerException;

}
