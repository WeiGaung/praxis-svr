package com.noriental.praxissvr.wrongQuestion.service.impl;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.github.miemiedev.mybatis.paginator.domain.Paginator;
import com.noriental.lessonsvr.rservice.LessonService;
import com.noriental.praxissvr.answer.bean.StudentExercise;
import com.noriental.praxissvr.answer.mappers.AnswerCorrectMapper;
import com.noriental.praxissvr.answer.util.StuAnswerUtil;
import com.noriental.praxissvr.brush.dao.StudentWorkDao;
import com.noriental.praxissvr.common.StuAnswerConstant;
import com.noriental.praxissvr.exception.PraxisErrorCode;
import com.noriental.praxissvr.wrong.bean.StuQuesKnowledge;
import com.noriental.praxissvr.wrongQuestion.bean.WrongQuesChapterEntity;
import com.noriental.praxissvr.wrongQuestion.bean.WrongQuesExerciseEntity;
import com.noriental.praxissvr.wrongQuestion.bean.WrongQuesListEntity;
import com.noriental.praxissvr.wrongQuestion.bean.WrongQuesSubjectStatisEntity;
import com.noriental.praxissvr.wrongQuestion.mappers.WrongQuestionMapper;
import com.noriental.praxissvr.wrongQuestion.request.*;
import com.noriental.praxissvr.wrongQuestion.response.*;
import com.noriental.praxissvr.wrongQuestion.service.WrongQuestionService;
import com.noriental.praxissvr.wrongQuestion.util.TableNameUtil;
import com.noriental.praxissvr.wrongQuestion.util.WrongQuestionUtil;
import com.noriental.trailsvr.service.TrailCountService;
import com.noriental.utils.json.JsonUtil;
import com.noriental.utils.redis.RedisUtil;
import com.noriental.validate.exception.BizLayerException;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kate on 2016/12/19.
 */
@Service("wrongQuestionService")
public class WrongQuestionServiceImpl implements WrongQuestionService {
    private static final Logger logger = LoggerFactory.getLogger(WrongQuestionServiceImpl.class);
    @Resource
    private WrongQuestionMapper wrongQuestionMapper;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private LessonService lessonService;
    @Resource
    private TrailCountService trailCountService;
    @Resource
    private StudentWorkDao studentWorkDao;
    @Resource
    private AnswerCorrectMapper answerCorrectMapper;

    @Override
    public WrongQuesSubjectStatisResp findWrongQuestionGroup(WrongQuesSubjectStatisReq wrongQuesGroupReq) throws BizLayerException {
        long starTime = System.currentTimeMillis();
        String tableName = TableNameUtil.getStuQuesKnowledge(wrongQuesGroupReq.getStudentId());
        Map map = new HashMap(2);
        map.put("studentId", wrongQuesGroupReq.getStudentId());
        map.put("tableName", tableName);
        List<WrongQuesSubjectStatisEntity> groupList = wrongQuestionMapper.findWrongQuesSubjectStatis(map);
        logger.debug("根据学生ID按照学科分组查询统计错题数量花费时间:" + (System.currentTimeMillis() - starTime));
        WrongQuesSubjectStatisResp resp = new WrongQuesSubjectStatisResp();
        resp.setGroupList(groupList);
        return resp;
    }

    @Override
    public WrongQuesChapQueryResp findWrongQuestionChapter(WrongQuesChapQueryReq in) throws BizLayerException {
        long starTime = System.currentTimeMillis();
        Map map = WrongQuestionUtil.assembleMapParam(in);
        map.put("dataType", "2,4");
        WrongQuesChapQueryResp resp = getWrongQuesChapQueryResp(map, in);
        logger.debug("根据学生ID章节知识点分页查询错题数据花费时间:" + (System.currentTimeMillis() - starTime));
        logger.debug("错题本根据章节查询错题及错题个数返回结果信息:" + JsonUtil.obj2Json(resp));
        return resp;
    }


    /***
     *
     * @param paramMap sql语句参数
     * @param wrongQuesChapQueryReq
     * @return
     * @throws BizLayerException
     */
    private WrongQuesChapQueryResp getWrongQuesChapQueryResp(Map paramMap, WrongQuesChapQueryReq wrongQuesChapQueryReq) throws BizLayerException {
        WrongQuesChapQueryResp resp = new WrongQuesChapQueryResp();
        int pageSize = wrongQuesChapQueryReq.getPageSize();
        logger.debug("错题本根据章节查询错题及错题个数请求参数信息:" + JsonUtil.obj2Json(paramMap));
        int totalNum = wrongQuestionMapper.findWrongQuesChapNum(paramMap);
        PageList<WrongQuesChapterEntity> resultList = wrongQuestionMapper.findWrongQuesChapQuery(paramMap, new PageBounds(0, pageSize));
        Paginator paginator = resultList.getPaginator();
        resp.setPageSize(wrongQuesChapQueryReq.getPageSize());
        resp.setTotalCount(totalNum);
        resp.setTotalPage(paginator.getTotalPages());
        int totalCount = paginator.getTotalCount();
        //根据条件查询获取的总条数来判断当前查询是否为最后一页
        if (totalCount >= 0 && totalCount <= pageSize) {
            resp.setLastPage(true);
        } else {
            resp.setLastPage(false);
        }
        if (resultList.size() > 0) {
            StringBuilder questionIds = new StringBuilder();
            for (WrongQuesChapterEntity data : resultList) {
                questionIds.append(data.getQuestionId()).append(",");
            }
            String quesIds = questionIds.substring(0, questionIds.length() - 1);
            paramMap.put("questionIds", quesIds);
            paramMap.put("tableName", TableNameUtil.getStudentErrorExe(wrongQuesChapQueryReq.getStudentId()));
            //查询根据章节已查询出来错题的错题个数
            List<WrongQuesSubjectStatisEntity> questionErrorNumList = wrongQuestionMapper.findChapterNum(paramMap);
            Map resultMap = new HashMap(questionErrorNumList.size());
            for (WrongQuesSubjectStatisEntity data : questionErrorNumList) {
                resultMap.put(data.getSubjectId(), data.getQuestionNum());
            }
            //根据当前的questionId查询答题错误次数
            for (WrongQuesChapterEntity entity : resultList) {
                Long questionId = entity.getQuestionId();
                //根据习题ID如果没有查询到错题个数，默认给0
                entity.setQuestionErrorNum(resultMap.get(questionId) != null ? (Integer) resultMap.get(questionId) : 0);
            }
            resp.setDataList(resultList);
            //设置分页查询最后一条数据ID
            resp.setLastIndexId(resultList.get(resultList.size() - 1).getId());
        }
        return resp;
    }

    @Override
    public WrongQuestionHisResp findWrongQuestionHis(WrongQuestionHisReq wrongQuestionHisReq) throws BizLayerException {
        long starTime = System.currentTimeMillis();
        Map paramMap = new HashMap(3);
        paramMap.put("questionId", wrongQuestionHisReq.getQuestionId());
        paramMap.put("studentId", wrongQuestionHisReq.getStudentId());
        paramMap.put("tableName", TableNameUtil.getStudentErrorExe(wrongQuestionHisReq.getStudentId()));
        logger.debug("错题本错题列表获取请求参数信息:" + JsonUtil.obj2Json(paramMap));
        //查询根据参数查询单题的数据列表
        List<StudentExercise> dataList = wrongQuestionMapper.findWrongSingleQuestionHis(paramMap);
        List<WrongQuesExerciseEntity> responseList = new ArrayList<>();
        if (null != dataList && dataList.size() > 0) {
            //initTextFieldFromSsdb(dataList);
            for (StudentExercise exercise : dataList) {
                //根据当前批改结果查询错题里面的批改是正确还是错误
                if (StuAnswerUtil.isWrong(exercise.getStructId(), exercise.getResult())) {
                    exercise.setCorrectStatus(0);
                } else {
                    if (exercise.getCorrectorRole().contains(StuAnswerConstant.CorrectorRole.STUDENT)){
                        exercise.setCorrectStatus(2);
                    }else{
                        exercise.setCorrectStatus(1);
                    }

                }
            }
            WrongQuestionUtil.assembleSingleQuestion(dataList, responseList);
        } else {
            List<StudentExercise> resultList = wrongQuestionMapper.findWrongQuestionHis(paramMap);
            //initTextFieldFromSsdb(resultList);
            for (StudentExercise exercise : resultList) {
                //根据当前批改结果查询错题里面的批改是正确还是错误
                if (StuAnswerUtil.isWrong(exercise.getStructId(), exercise.getResult())) {
                    exercise.setCorrectStatus(0);
                } else {
                    exercise.setCorrectStatus(1);
                }
            }
            WrongQuestionUtil.assembleComplexQuestion(resultList, responseList);
        }
        WrongQuestionHisResp resp = new WrongQuestionHisResp();
        resp.setDataList(responseList);
        logger.debug("错题本错题列表返回的数据信息:" + JsonUtil.obj2Json(resp));
        logger.debug("根据学生ID、习题ID查询错题历史记录花费时间:" + (System.currentTimeMillis() - starTime));
        return resp;
    }

    @Override
    public WrongQuesQueryResp findWrongQuestion(WrongQuesQueryReq wrongQuesQueryReq) throws BizLayerException {
        WrongQuesChapQueryReq in = new WrongQuesChapQueryReq();
        BeanUtils.copyProperties(wrongQuesQueryReq, in);
        Map map = WrongQuestionUtil.assembleMapParam(in);
        // 1:知识点刷题已做题目  2:错题题目,知识点下错题   3:教材体系刷题已做题目    4:错题题目,教材体系下错题
        map.put("dataType", in.getChapterOrKnowledge());
        WrongQuesChapQueryResp resp = getWrongQuesChapQueryResp(map, in);
        WrongQuesQueryResp res = new WrongQuesQueryResp();
        BeanUtils.copyProperties(resp, res);
        return res;
    }

    @Override
    public WrongQuesQueryResp findRealWrongQuestion(WrongQuesQueryReq wrongQuesQueryReq) throws BizLayerException {
        WrongQuesChapQueryReq in = new WrongQuesChapQueryReq();
        BeanUtils.copyProperties(wrongQuesQueryReq, in);
        Map map = WrongQuestionUtil.assembleMapParam(in);
        map.put("dataType", in.getChapterOrKnowledge());
        WrongQuesChapQueryResp resp = getRealWrongQuesChapQueryResp(map, in);
        WrongQuesQueryResp res = new WrongQuesQueryResp();
        BeanUtils.copyProperties(resp, res);
        return res;
    }

    private WrongQuesChapQueryResp getRealWrongQuesChapQueryResp(Map paramMap, WrongQuesChapQueryReq wrongQuesChapQueryReq) {
        WrongQuesChapQueryResp resp = new WrongQuesChapQueryResp();
        int pageSize = wrongQuesChapQueryReq.getPageSize();
        logger.debug("错题本根据章节查询错题及错题个数请求参数信息:" + JsonUtil.obj2Json(paramMap));
        int totalNum = wrongQuestionMapper.findRealWrongQuesChapNum(paramMap);
        PageList<WrongQuesChapterEntity> resultList = wrongQuestionMapper.findRealWrongQuesChapQuery(paramMap, new PageBounds(0, pageSize));
        Paginator paginator = resultList.getPaginator();
        resp.setPageSize(wrongQuesChapQueryReq.getPageSize());
        resp.setTotalCount(totalNum);
        resp.setTotalPage(paginator.getTotalPages());
        int totalCount = paginator.getTotalCount();
        //根据条件查询获取的总条数来判断当前查询是否为最后一页
        if (totalCount >= 0 && totalCount <= pageSize) {
            resp.setLastPage(true);
        } else {
            resp.setLastPage(false);
        }
        if (resultList.size() > 0) {
            StringBuilder questionIds = new StringBuilder();
            for (WrongQuesChapterEntity data : resultList) {
                questionIds.append(data.getQuestionId()).append(",");
            }
            String quesIds = questionIds.substring(0, questionIds.length() - 1);
            paramMap.put("questionIds", quesIds);
            paramMap.put("tableName", TableNameUtil.getStudentErrorExe(wrongQuesChapQueryReq.getStudentId()));
            //查询根据章节已查询出来错题的错题个数
            List<WrongQuesSubjectStatisEntity> questionErrorNumList = wrongQuestionMapper.findChapterNum(paramMap);
            Map resultMap = new HashMap(5);
            for (WrongQuesSubjectStatisEntity data : questionErrorNumList) {
                resultMap.put(data.getSubjectId(), data.getQuestionNum());
            }
            //根据当前的questionId查询答题错误次数
            for (WrongQuesChapterEntity entity : resultList) {
                Long questionId = entity.getQuestionId();
                //根据习题ID如果没有查询到错题个数，默认给0
                entity.setQuestionErrorNum(resultMap.get(questionId) != null ? (Integer) resultMap.get(questionId) : 0);
            }
            resp.setDataList(resultList);
            //设置分页查询最后一条数据ID
            resp.setLastIndexId(resultList.get(resultList.size() - 1).getId());
        }
        return resp;
    }

    @Override
    public WrongQuesListResp findWrongQuestions(WrongQuesListReq wrongQuesListReq) throws BizLayerException {
        // 某学生某题的所有错题记录
        Map paramMap = WrongQuestionUtil.assembleWrongQuestions(wrongQuesListReq);
        List<WrongQuesListEntity> resultList = wrongQuestionMapper.findWrongQuestions(paramMap);
        WrongQuesListResp resp = new WrongQuesListResp();
        resp.setGroupList(resultList);
        return resp;
    }

    @Override
    public WrongQuesListResp findWrongParentQuestions(WrongQuesListReq wrongQuesListReq) throws BizLayerException {
        // 根据学生ID、知识点或章节查询学生的错题(复合题)
        Map paramMap = WrongQuestionUtil.assembleWrongQuestions(wrongQuesListReq);
        List<WrongQuesListEntity> resultList = wrongQuestionMapper.findWrongParentQuestions(paramMap);
        WrongQuesListResp resp = new WrongQuesListResp();
        resp.setGroupList(resultList);
        return resp;
    }

    @Override
    public WrongQuesNumQueryResp findWrongQuestionsNum(WrongQuesNumQueryReq wrongQuesNumQueryReq) throws BizLayerException {
        List<Long> questionIds = wrongQuesNumQueryReq.getQuestionIds();
        if (questionIds.size() > 50) {
            throw new BizLayerException("", PraxisErrorCode.WRONG_QUESTIONS_OUT_RANGE);
        }
        Map paramMap = new HashMap(4);
        paramMap.put("studentId", wrongQuesNumQueryReq.getStudentId());
        paramMap.put("tableName", TableNameUtil.getStudentErrorExe(wrongQuesNumQueryReq.getStudentId()));
        String ids = "";
        for (Long id : wrongQuesNumQueryReq.getQuestionIds()) {
            ids = ids + id + ",";
        }
        paramMap.put("questionIds", ids.substring(0, ids.length() - 1));
        List<WrongQuesChapterEntity> dataList = wrongQuestionMapper.findWrongQuestionsNum(paramMap);
        WrongQuesNumQueryResp resp = new WrongQuesNumQueryResp();
        resp.setDataList(dataList);
        return resp;
    }

    // 根据资源ID,答题场景查询学生的做答记录 entity_student_exercise_
    @Override
    public List<StudentExercise> findStudentAnswer(StudentExercise exercises) throws BizLayerException {
        Map paramMap = new HashMap(3);
        //任务场景
        paramMap.put("tableName", TableNameUtil.getStudentExercise(exercises));
        paramMap.put("questionId", exercises.getQuestionId());
        paramMap.put("resourceId", exercises.getResourceId());
        return answerCorrectMapper.findStudentAnswers(paramMap);
    }

    @Override
    public void updateIntellInfo(List<StudentExercise> list) {
        answerCorrectMapper.updateIntellInfo(list);
    }

    @Override
    public StudentExercise findStudentExerciseInfo(String tableName, Long id) throws BizLayerException {
        Map paramMap = new HashMap(2);
        paramMap.put("tableName", tableName);
        paramMap.put("id", id);
        return answerCorrectMapper.findStudentExerciseInfo(paramMap);
    }

    @Override
    public List<StudentExercise> findIntellStudentAnswer(StudentExercise exercises) throws BizLayerException {
        Map paramMap = new HashMap(3);
        StuAnswerUtil.setShardKey(exercises, redisUtil, studentWorkDao, lessonService, trailCountService);
        paramMap.put("tableName", TableNameUtil.getStudentExercise(exercises));
        paramMap.put("resourceId", exercises.getResourceId());
        paramMap.put("exerciseSource", exercises.getExerciseSource());
        List<StudentExercise> result = answerCorrectMapper.findIntellStudentAnswer(paramMap);
        if (CollectionUtils.isNotEmpty(result)) {
            result = StuAnswerUtil.getIntellCorrectInfo(result);
        }
        return result;
    }


    //教师空间学生知识点,章节错题次数统计
    @Override
    public WrongQuesNumQueryResp findErrorQuestionsNum(ErrorQuesChapQueryReq req) throws BizLayerException {
        //先根据知识点章节表查询错题的ids
        long startTime = System.currentTimeMillis();
        Map paramMap = WrongQuestionUtil.assembleErrorMap(req);
        List<StuQuesKnowledge> list = wrongQuestionMapper.findKnowledgeQuestionIds(paramMap);
        List<WrongQuesChapterEntity> dataList = new ArrayList<>();
        for (StuQuesKnowledge entity : list) {
            WrongQuesChapterEntity wrongQuesChapterEntity = new WrongQuesChapterEntity();
            wrongQuesChapterEntity.setQuestionId(entity.getQuestionId());
            dataList.add(wrongQuesChapterEntity);
        }
        WrongQuesNumQueryResp resp = new WrongQuesNumQueryResp();
        resp.setDataList(dataList);
        logger.info("findErrorQuestionsNum cost time:{}ms", System.currentTimeMillis() - startTime);
        return resp;
    }


    @Override
    public WrongQuestionsResp findWrongQuestions(WrongQuestionHisReq wrongQuestionHisReq) throws BizLayerException {
        Map paramMap = new HashMap(3);
        paramMap.put("questionId", wrongQuestionHisReq.getQuestionId());
        paramMap.put("studentId", wrongQuestionHisReq.getStudentId());
        paramMap.put("tableName", TableNameUtil.getStudentErrorExe(wrongQuestionHisReq.getStudentId()));
        logger.debug("错题本错题列表获取请求参数信息:" + JsonUtil.obj2Json(paramMap));
        //查询根据参数查询单题的数据列表
        List<StudentExercise> dataList = wrongQuestionMapper.findQuestionsByIds(paramMap);
        List<StudentExercise> resultList =new ArrayList<>();
        for (StudentExercise exercise :dataList){
           if(StuAnswerUtil.isWrong(exercise)) {
               resultList.add(exercise);
            }
        }
        WrongQuestionsResp wrongQuestionsResp = new WrongQuestionsResp();
        wrongQuestionsResp.setResultList(resultList);
        return wrongQuestionsResp;
    }


}
