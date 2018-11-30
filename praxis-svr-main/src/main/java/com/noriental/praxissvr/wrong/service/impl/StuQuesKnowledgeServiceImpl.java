package com.noriental.praxissvr.wrong.service.impl;

import com.noriental.praxissvr.answer.bean.StudentExercise;
import com.noriental.praxissvr.answer.dao.StuQuesKnowledgeShardDao;
import com.noriental.praxissvr.answer.service.StudentErrorExeService;
import com.noriental.praxissvr.answer.util.BatchUpdateCorrectUtil;
import com.noriental.praxissvr.answer.util.StuAnswerUtil;
import com.noriental.praxissvr.brush.bean.StudentWorkAnswer;
import com.noriental.praxissvr.common.CountEntity;
import com.noriental.praxissvr.common.StuAnswerConstant;
import com.noriental.praxissvr.common.StuAnswerConstant.DataTypeEnum;
import com.noriental.praxissvr.common.StuAnswerConstant.LevelEnum;
import com.noriental.praxissvr.common.TrailBaseErrorRequestRequest;
import com.noriental.praxissvr.common.TrailBaseErrorRequestResponse;
import com.noriental.praxissvr.question.bean.Question;
import com.noriental.praxissvr.question.request.FindQuestionsByIdsRequest;
import com.noriental.praxissvr.question.response.FindQuestionsByIdsResponse;
import com.noriental.praxissvr.question.service.QuestionService;
import com.noriental.praxissvr.questionSearch.service.QuestionSearchService;
import com.noriental.praxissvr.statis.util.StuStatisUtil;
import com.noriental.praxissvr.wrong.bean.SimpleKnowVo;
import com.noriental.praxissvr.wrong.bean.StuQuesKnowledge;
import com.noriental.praxissvr.wrong.request.DeleteWrongQuesRequest;
import com.noriental.praxissvr.wrong.request.FindWrongQuesAnswersRequest;
import com.noriental.praxissvr.wrong.response.FindWrongQuesAnswersResponse;
import com.noriental.praxissvr.wrong.service.StuQuesKnowledgeService;
import com.noriental.praxissvr.wrongQuestion.mappers.WrongQuestionMapper;
import com.noriental.praxissvr.wrongQuestion.util.TableNameUtil;
import com.noriental.utils.json.JsonUtil;
import com.noriental.utils.pager.PageUtil;
import com.noriental.validate.bean.CommonDes;
import com.noriental.validate.exception.BizLayerException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.invoke.MethodHandles;
import java.util.*;

@SuppressWarnings("SpringJavaAutowiringInspection")
@Service(value = "stuQuesKnowledgeService")
public class StuQuesKnowledgeServiceImpl implements StuQuesKnowledgeService {
    private static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @Resource
    private StuQuesKnowledgeShardDao stuQuesKnowledgeDao;
    @Resource
    private QuestionService questionService;
    @Resource
    private StudentErrorExeService studentErrorExeService;
    @Resource
    private WrongQuestionMapper wrongQuestionMapper;
    @Autowired
    private QuestionSearchService questionSearchService;

    /**
     * 查询 学生的 所有的
     * 错误的统计信息
     * 1、查询某个学生某个科目下知识点或章节的题目统计
     * 2、计算时，题目重复会去重
     *
     * @param request
     * @return key：知识点或者章节 value：题目数量
     * @see CountEntity addQuestionNum
     */
    @Override
    public TrailBaseErrorRequestResponse findTrailBaseError(TrailBaseErrorRequestRequest request) {
        List<StuQuesKnowledge> stuQuesKnowledgeList = stuQuesKnowledgeDao.findStuQuesKnowledgeInfo(request);
        Map<String, CountEntity> map = new HashMap<>();
        String modelPrefix = request.isTopic() ? "M" : "C";
        String unitPrefix = request.isTopic() ? "U" : "C";
        String topicPrefix = request.isTopic() ? "T" : "C";

        //是否全是单题
        for (StuQuesKnowledge info : stuQuesKnowledgeList) {
            Long modelId = info.getModuleId();
            Long unitId = info.getUnitId();
            Long topicId = info.getTopicId();
            //将 此题计入 CountEntity 并将CountEntity 按 modelPrefix+moduleId 的key 放入到 map 保证 不重复
            //像moduleId 放入到 map中为 : modelPrefix + modelId 这样保证 module - unit - topic 不会因id重复 相互覆盖
            countTrailEntity(map, modelPrefix, modelId, info.getQuestionId(), info.getQuestionCount());
            countTrailEntity(map, unitPrefix, unitId, info.getQuestionId(), info.getQuestionCount());
            countTrailEntity(map, topicPrefix, topicId, info.getQuestionId(), info.getQuestionCount());
        }
        TrailBaseErrorRequestResponse trailError = new TrailBaseErrorRequestResponse();
        trailError.setResultMap(map);
        return trailError;
    }

    @Override
    public FindWrongQuesAnswersResponse findWrongQuesAnswers(FindWrongQuesAnswersRequest request) {
        long l = System.currentTimeMillis();
        Long studentId = request.getStudentId();
        LevelEnum level = request.getLevel();
        Long levelId = request.getLevelId();
        Integer pageSize = request.getPageSize();
        Integer fromIndex = request.getFromIndex();
        DataTypeEnum dataType = request.getDataType();
        //根据学生ID、数据类型、章节知识点查询错题习题ID集合
        List<Long> quesIds = findWrongAvaQueses(studentId, dataType.getCode(), level, levelId);
        Collections.sort(quesIds);
        logger.info(" sorted quesIdlist " + JsonUtil.obj2Json(quesIds));
        int totalCount = quesIds.size();
        fromIndex = StuAnswerUtil.getFromIndex(fromIndex, totalCount);
        Integer endIndex = StuAnswerUtil.getEndIndex(fromIndex, pageSize, totalCount);
        List<Long> quesIds1 = quesIds.subList(fromIndex, endIndex);
        List<StudentExercise> seList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(quesIds1)) {
            StudentExercise se = new StudentExercise();
            se.setStudentId(studentId);
            se.setQuestionIdList(quesIds1);
            List<StudentExercise> seList1 = studentErrorExeService.findByQuesIds(se);
            StudentExercise se1 = new StudentExercise();
            se1.setStudentId(studentId);
            se1.setParentQuesIdList(quesIds1);
            List<StudentExercise> seList2 = studentErrorExeService.findByQuesIds(se1);
            seList.addAll(seList1);
            seList.addAll(seList2);
        }
        FindWrongQuesAnswersResponse out = new FindWrongQuesAnswersResponse();
        out.setTotalCount(totalCount);
        out.setPageCount(PageUtil.getTotalPage(pageSize, totalCount));
        out.setTotalCount(totalCount);
        out.setPageSize(pageSize);
        Map<Long, List<StudentExercise>> longSeListMap = getMapByQuesIdAndSeList(quesIds1, seList);
        out.setLongSeListMap(longSeListMap);
        long l1 = System.currentTimeMillis();
        logger.info("studentErrorExeService findQuestions cost:{}", l1 - l);
        return out;
    }

    @Override
    public CommonDes deleteWrongQues(DeleteWrongQuesRequest in) {
        Long stuId = in.getStudentId();
        Long questionId = in.getQuestionId();

        StuQuesKnowledge stuQuesKnowledge = new StuQuesKnowledge();
        stuQuesKnowledge.setStudentId(stuId);
        stuQuesKnowledge.setQuestionId(questionId);
        List<Integer> dataTypeList = new ArrayList<>();
        dataTypeList.add(StuAnswerConstant.DataType.WRONG_QUES);
        dataTypeList.add(StuAnswerConstant.DataType.WRONG_QUES_MATERIAL);
        stuQuesKnowledge.setDataTypeList(dataTypeList);
        List<StuQuesKnowledge> stuQuesKnowledgesList = stuQuesKnowledgeDao.findByStuQuesKnow(stuQuesKnowledge);
        List<Long> ids;
        if (CollectionUtils.isNotEmpty(stuQuesKnowledgesList)) {
            ids = getIdsByQuesKnow(stuQuesKnowledgesList);
            StuQuesKnowledge stuQuesKnowledge1 = new StuQuesKnowledge();
            stuQuesKnowledge1.setIdList(ids);
            stuQuesKnowledge1.setStudentId(stuId);
            stuQuesKnowledgeDao.deleteByIds(stuQuesKnowledge1);
        }
        return new CommonDes();
    }

    private List<Long> getIdsByQuesKnow(List<StuQuesKnowledge> stuQuesKnowledgesList) {
        List<Long> ids = new ArrayList<>();
        for (StuQuesKnowledge aList : stuQuesKnowledgesList) {
            ids.add(aList.getId());
        }
        return ids;
    }

    private Map<Long, List<StudentExercise>> getMapByQuesIdAndSeList(List<Long> quesIds, List<StudentExercise> seList) {
        Map<Long, List<StudentExercise>> longSeListMap = new HashMap<>();
        for (Long quesId : quesIds) {
            List<StudentExercise> ls = getSeListByQuesId(quesId, seList);
            if (CollectionUtils.isNotEmpty(ls)) {
                longSeListMap.put(quesId, ls);
            }
        }
        return longSeListMap;
    }

    private List<StudentExercise> getSeListByQuesId(Long quesId, List<StudentExercise> seList) {
        StudentExercise latestWrongStuExe = StuStatisUtil.getLatestWrongStuExe(quesId, seList);
        List<StudentExercise> allStuExesOfQues = StuStatisUtil.getAllStuExesOfQues(latestWrongStuExe, seList);
        for (StudentExercise se1 : allStuExesOfQues) {
            List<StudentExercise> wrongHisory = StuStatisUtil.getHistory(se1, seList);
            if (CollectionUtils.isNotEmpty(wrongHisory)) {
                BeanUtils.copyProperties(wrongHisory.get(0), se1);
            }
            se1.setStudentExerciseList(wrongHisory);
        }

        return allStuExesOfQues;
    }

    private List<Long> findWrongAvaQueses(long stuId, Integer dataType, LevelEnum level, Long levelId) {
        StuQuesKnowledge entity = new StuQuesKnowledge();
        entity.setStudentId(stuId);
        entity.setDataType(dataType);
        if (level == LevelEnum.LEVEL_3) {
            entity.setTopicId(levelId);
        } else if (level == LevelEnum.LEVEL_2) {
            entity.setUnitId(levelId);
        } else if (level == LevelEnum.LEVEL_1) {
            entity.setModuleId(levelId);
        }
        List<StuQuesKnowledge> resultList = stuQuesKnowledgeDao.findStuQuesKnowledge(entity);
        List<Long> quesIds = getQuesIdsByStuQuesKnowledge(resultList);
        List<Question> questions = StuAnswerUtil.getQuestionListByIds(questionSearchService, quesIds);
        List<Question> questions1 = StuAnswerUtil.getValidQuesList(questions);
        List<Long> quesIds1 = StuAnswerUtil.getIdsByQues(questions1);
        return new ArrayList<>(new HashSet<>(quesIds1));
    }

    private List<Long> getQuesIdsByStuQuesKnowledge(List<StuQuesKnowledge> resultList) {
        List<Long> quesIds = new ArrayList<>();
        if (null != resultList && resultList.size() > 0) {
            for (StuQuesKnowledge data : resultList) {
                quesIds.add(data.getQuestionId());
            }
        }
        return quesIds;
    }

    private void creates(List<StuQuesKnowledge> stuQuesKnowledges) {
        if (CollectionUtils.isNotEmpty(stuQuesKnowledges)) {
            //数据量过大删除过慢，优化为10道题做一次库删除操作
            List<List<StuQuesKnowledge>> splitList = BatchUpdateCorrectUtil.spliceArrays(stuQuesKnowledges, 10);
            for (List<StuQuesKnowledge> dataList : splitList) {
                StuQuesKnowledge stuQuesKnowledge = new StuQuesKnowledge();
                stuQuesKnowledge.setStuQuesKnowledgeList(dataList);
                stuQuesKnowledge.setStudentId(dataList.get(0).getStudentId());
                stuQuesKnowledgeDao.deleteByStuQuesKnow(stuQuesKnowledge);
            }
            //对于不是单题或者顶级大题的不保存，防止脏数据进入，影响后续业务逻辑(删除，推题等)
            validateQuesIdIsParent_V2(stuQuesKnowledges);
            logger.info("知识点章节错题录入数据:{}", JsonUtil.obj2Json(stuQuesKnowledges));
            stuQuesKnowledgeDao.creates(stuQuesKnowledges);
        }

    }

    @Override
    public void recordAnsweredWrongQues(List<StudentWorkAnswer> studentWorkAnswer, Map<Long, SimpleKnowVo>
            knowOrChapterMap) throws BizLayerException {
        try {
            if (MapUtils.isNotEmpty(knowOrChapterMap) && CollectionUtils.isNotEmpty(studentWorkAnswer)) {
                //记录已做题目
                List<StuQuesKnowledge> stuQuesKnowledges = new ArrayList<>();
                stuQuesKnowledges.addAll(getAnsweredQues(studentWorkAnswer, knowOrChapterMap));
                //记录错题
                stuQuesKnowledges.addAll(getWrongQues(studentWorkAnswer, knowOrChapterMap));
                logger.info("didAndWrongQues:" + JsonUtil.obj2Json(stuQuesKnowledges));

                //入库，入solr
                if (stuQuesKnowledges.size() > 0) {
                    creates(stuQuesKnowledges);
                }
            }
        } catch (Exception e) {
            logger.error("答题后置业务-知识点章节错题录入失败", e);
        }


    }

    @Override
    public void recordAnsweredRightQues(List<StudentWorkAnswer> studentWorkAnswer, Map<Long, SimpleKnowVo>
            knowOrChapterMap) throws BizLayerException {
        if (MapUtils.isNotEmpty(knowOrChapterMap) && CollectionUtils.isNotEmpty(studentWorkAnswer)) {
            List<StuQuesKnowledge> stuQuesKnowledges = new ArrayList<>();
            //记录正确的
            stuQuesKnowledges.addAll(getRightQues(studentWorkAnswer, knowOrChapterMap));
            if (stuQuesKnowledges.size() > 0) {
                for (StuQuesKnowledge s : stuQuesKnowledges) {
                    s.setDataStatus(0);
                    s.setCreateTime(new Date());
                    s.setTableName(TableNameUtil.getStuQuesKnowledge(s.getStudentId()));
                }
                validateQuesIdIsParent_V2(stuQuesKnowledges);
                List<List<StuQuesKnowledge>> splitList = BatchUpdateCorrectUtil.spliceArrays(stuQuesKnowledges, 10);
                for (List<StuQuesKnowledge> dataList : splitList) {
                    wrongQuestionMapper.updateKnowledgeStatusList(dataList);
                }
            }
        }

    }


    @Override
    public void recordWrongQues(StudentWorkAnswer studentWorkAnswer, Map<Long, SimpleKnowVo> knowOrChapterMap) {
        try {
            if (MapUtils.isNotEmpty(knowOrChapterMap) && null != studentWorkAnswer) {
                List<StuQuesKnowledge> stuQuesKnowledges = getWrongQues(studentWorkAnswer, knowOrChapterMap);
                if (stuQuesKnowledges.size() > 0) {
                    creates(stuQuesKnowledges);
                }
            }
        } catch (Exception e) {
            logger.error("批改后置业务-知识点章节错题入库失败", e);
        }


    }

    @Override
    public void recordWrightQues(StudentWorkAnswer studentWorkAnswers, Map<Long, SimpleKnowVo> knowOrChapterMap, int
            dataStatu) throws BizLayerException {
        try {
            if (MapUtils.isNotEmpty(knowOrChapterMap) && null != studentWorkAnswers) {
                List<StuQuesKnowledge> stuQuesKnowledges = getRightQues(studentWorkAnswers, knowOrChapterMap);
                if (CollectionUtils.isNotEmpty(stuQuesKnowledges)) {
                    for (StuQuesKnowledge s : stuQuesKnowledges) {
                        s.setDataStatus(dataStatu);
                        s.setCreateTime(new Date());
                        s.setTableName(TableNameUtil.getStuQuesKnowledge(s.getStudentId()));
                    }
                    List<List<StuQuesKnowledge>> splitList = BatchUpdateCorrectUtil.spliceArrays(stuQuesKnowledges, 10);
                    for (List<StuQuesKnowledge> dataList : splitList) {
                        wrongQuestionMapper.updateKnowledgeStatusList(stuQuesKnowledges);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("老师批改记录知识点或者章节做错后重新做对状态修改失败", e);
        }

    }

    @Override
    public void updataStuQueKnowledgeStatu(StudentExercise se,int dataStatu) throws BizLayerException {
        Map paraMap=new HashMap(4);
        paraMap.put("studentId",se.getStudentId());
        paraMap.put("questionId",se.getParentQuestionId()==null?se.getQuestionId():se.getParentQuestionId());
        paraMap.put("tableName",TableNameUtil.getStuQuesKnowledge(se.getStudentId()));
        paraMap.put("dataStatu",dataStatu);
        wrongQuestionMapper.updateKnowledgeStatu(paraMap);
    }


    /***
     * 获取所有做答的题目
     * @param studentWorkAnswers
     * @param chapterMap
     * @return
     */
    private List<StuQuesKnowledge> getAnsweredQues(List<StudentWorkAnswer> studentWorkAnswers, Map<Long,
            SimpleKnowVo> chapterMap) {
        List<StuQuesKnowledge> stuQuesKnowledges = new ArrayList<>();
        for (StudentWorkAnswer studentWorkAnswer : studentWorkAnswers) {
            Long questionId = studentWorkAnswer.getQuestionId();
            Long studentId = studentWorkAnswer.getStudentId();
            List<StudentWorkAnswer> subStudentWorkAnswerses = studentWorkAnswer.getSubQuesAnswers();
            List<Long> topicIds = studentWorkAnswer.getTopicIds();
            List<Long> unitIds = studentWorkAnswer.getUnitIds();
            List<Long> moduleIds = studentWorkAnswer.getModuleIds();
            Integer resourceType = studentWorkAnswer.getResourceType();
            Integer dataType = StuAnswerConstant.ResourceType.RESOURCE_TYPE_MATERIAL.equals(resourceType) ?
                    StuAnswerConstant.DataType.WORK_ANSWERED_MATERIAL : StuAnswerConstant.DataType.WORK_ANSWERED;

            topicIds = getLeafChapterIds(resourceType, topicIds, unitIds, moduleIds, chapterMap);
            //大题和小题去重后的所有知识点
            Set<Long> allTopicIds = new HashSet<>();
            //如果是单题
            if (subStudentWorkAnswerses == null) {
                //遍历单题的知识点
                for (Long topicId : topicIds) {
                    //已做题目
                    allTopicIds.add(topicId);
                }
                //组合题
            } else {
                //父题知识点
                List<Long> parentTopicIds = topicIds;
                //子题知识点
                for (StudentWorkAnswer subStudentWorkAnswers : subStudentWorkAnswerses) {
                    List<Long> subTopicIds = subStudentWorkAnswers.getTopicIds();
                    //子题作答，子题以及父知识点才可以进入统计
                    String subQuesResult = subStudentWorkAnswers.getResult();
                    Integer subStructId = subStudentWorkAnswers.getStructId();
                    boolean isAnswered = StuAnswerUtil.isAnswered(subStructId, subQuesResult);

                    if (isAnswered) {
                        //增加父题知识点
                        allTopicIds.addAll(parentTopicIds);
                        //增加子题知识点
                        allTopicIds.addAll(subTopicIds);
                    }
                }

            }
            //每一个知识点的答题次数
            for (Long topicId : allTopicIds) {
                if (chapterMap.get(topicId) != null) {//有些知识点或者章节可能被删除过
                    add(stuQuesKnowledges, getStuQuesKnowledge(studentId, dataType, questionId, chapterMap.get
                            (topicId)));
                }
            }
        }
        return stuQuesKnowledges;
    }

    private List<StuQuesKnowledge> getWrongQues(List<StudentWorkAnswer> studentWorkAnswers, Map<Long, SimpleKnowVo>
            chapterMap) {
        List<StuQuesKnowledge> stuQuesKnowledges = new ArrayList<>();
        for (StudentWorkAnswer studentWorkAnswer : studentWorkAnswers) {
            stuQuesKnowledges.addAll(getWrongQues(studentWorkAnswer, chapterMap));
        }
        return stuQuesKnowledges;
    }

    private List<StuQuesKnowledge> getRightQues(List<StudentWorkAnswer> studentWorkAnswers, Map<Long, SimpleKnowVo>
            chapterMap) {
        List<StuQuesKnowledge> stuQuesKnowledges = new ArrayList<>();
        for (StudentWorkAnswer studentWorkAnswer : studentWorkAnswers) {
            List<StuQuesKnowledge> result = getRightQues(studentWorkAnswer, chapterMap);
            if (CollectionUtils.isNotEmpty(result)) {
                stuQuesKnowledges.addAll(result);
            }
        }
        return stuQuesKnowledges;
    }

    private List<StuQuesKnowledge> getRightQues(StudentWorkAnswer studentWorkAnswer, Map<Long, SimpleKnowVo>
            chapterMap) {
        List<StuQuesKnowledge> stuQuesKnowledges = new ArrayList<>();
        Long questionId = studentWorkAnswer.getQuestionId();
        Long studentId = studentWorkAnswer.getStudentId();
        String result = studentWorkAnswer.getResult();
        Integer structId = studentWorkAnswer.getStructId();
        List<StudentWorkAnswer> subStudentWorkAnswerses = studentWorkAnswer.getSubQuesAnswers();
        List<Long> topicIds = studentWorkAnswer.getTopicIds();
        List<Long> unitIds = studentWorkAnswer.getUnitIds();
        List<Long> moduleIds = studentWorkAnswer.getModuleIds();

        Integer resourceType = studentWorkAnswer.getResourceType();
        Integer dataType = StuAnswerConstant.ResourceType.RESOURCE_TYPE_MATERIAL.equals(resourceType) ?
                StuAnswerConstant.DataType.WRONG_QUES_MATERIAL : StuAnswerConstant.DataType.WRONG_QUES;
        topicIds = getLeafChapterIds(resourceType, topicIds, unitIds, moduleIds, chapterMap);

        Set<Long> allTopicIds = new HashSet<>();
        //如果是单题
        if (subStudentWorkAnswerses == null) {
            //题目做错，遍历单题的知识点，每个知识点算做错一次
            boolean isWrong = StuAnswerUtil.isAbsolutelyWrong(structId, result);
            if (!isWrong) {
                for (Long topicId : topicIds) {
                    allTopicIds.add(topicId);
                }
            }
            //组合题
        } else {
            //父题知识点
            for (StudentWorkAnswer subStudentWorkAnswers : subStudentWorkAnswerses) {
                //子题知识点
                List<Long> subTopicIds = subStudentWorkAnswers.getTopicIds();
                subTopicIds = getLeafChapterIds(subStudentWorkAnswers.getResourceType(), subTopicIds,
                        subStudentWorkAnswers.getUnitIds(), subStudentWorkAnswers.getModuleIds(), chapterMap);
                //子题答错，子题每个知识点算答错，父题每个知识点算答错
                String subQuesResult = subStudentWorkAnswers.getResult();
                Integer subStructId = subStudentWorkAnswers.getStructId();

                boolean isWrong = StuAnswerUtil.isAbsolutelyWrong(subStructId, subQuesResult);
                if (isWrong) {
                    return null;
                } else {
                    for (Long parentTopicId : topicIds) {
                        allTopicIds.add(parentTopicId);
                    }
                    for (Long subTopicId : subTopicIds) {
                        allTopicIds.add(subTopicId);
                    }
                }
            }
        }
        for (Long topicId : allTopicIds) {
            //有些知识点或者章节可能被删除过
            if (chapterMap.get(topicId) != null) {
                add(stuQuesKnowledges, getStuQuesKnowledge(studentId, dataType, questionId, chapterMap.get(topicId)));
            }
        }
        return stuQuesKnowledges;
    }

    private List<StuQuesKnowledge> getWrongQues(StudentWorkAnswer studentWorkAnswer, Map<Long, SimpleKnowVo>
            chapterMap) {
        List<StuQuesKnowledge> stuQuesKnowledges = new ArrayList<>();
        Long questionId = studentWorkAnswer.getQuestionId();
        Long studentId = studentWorkAnswer.getStudentId();
        String result = studentWorkAnswer.getResult();
        Integer structId = studentWorkAnswer.getStructId();
        List<StudentWorkAnswer> subStudentWorkAnswerses = studentWorkAnswer.getSubQuesAnswers();
        List<Long> topicIds = studentWorkAnswer.getTopicIds();
        List<Long> unitIds = studentWorkAnswer.getUnitIds();
        List<Long> moduleIds = studentWorkAnswer.getModuleIds();
        Integer resourceType = studentWorkAnswer.getResourceType();
        Integer dataType = StuAnswerConstant.ResourceType.RESOURCE_TYPE_MATERIAL.equals(resourceType) ?
                StuAnswerConstant.DataType.WRONG_QUES_MATERIAL : StuAnswerConstant.DataType.WRONG_QUES;
        topicIds = getLeafChapterIds(resourceType, topicIds, unitIds, moduleIds, chapterMap);
        Set<Long> allTopicIds = new HashSet<>();
        //如果是单题
        if (subStudentWorkAnswerses == null) {
            //题目做错，遍历单题的知识点，每个知识点算做错一次
            boolean isWrong = StuAnswerUtil.isWrong(structId, result);
            if (isWrong) {
                for (Long topicId : topicIds) {
                    allTopicIds.add(topicId);
                }
            }
            //组合题
        } else {
            //父题知识点
            for (StudentWorkAnswer subStudentWorkAnswers : subStudentWorkAnswerses) {
                //子题答错，子题每个知识点算答错，父题每个知识点算答错
                String subQuesResult = subStudentWorkAnswers.getResult();
                Integer subStructId = subStudentWorkAnswers.getStructId();
                boolean isWrong = StuAnswerUtil.isWrong(subStructId, subQuesResult);
                if (isWrong) {
                    //子题知识点
                    List<Long> subTopicIds = subStudentWorkAnswers.getTopicIds();
                    subTopicIds = getLeafChapterIds(subStudentWorkAnswers.getResourceType(), subTopicIds,
                            subStudentWorkAnswers.getUnitIds(), subStudentWorkAnswers.getModuleIds(), chapterMap);
                    for (Long parentTopicId : topicIds) {
                        allTopicIds.add(parentTopicId);
                    }
                    for (Long subTopicId : subTopicIds) {
                        allTopicIds.add(subTopicId);
                    }
                }
            }
        }
        for (Long topicId : allTopicIds) {
            if (chapterMap.get(topicId) != null) {//有些知识点或者章节可能被删除过
                add(stuQuesKnowledges, getStuQuesKnowledge(studentId, dataType, questionId, chapterMap.get(topicId)));
            }
        }
        return stuQuesKnowledges;
    }

    private List<Long> getLeafChapterIds(Integer resourceType, List<Long> topicIds, List<Long> unitIds, List<Long>
            moduleIds, Map<Long, SimpleKnowVo> chapterMap) {
        Set<Long> leafChapterIds = new HashSet<>();

        if (StuAnswerConstant.ResourceType.RESOURCE_TYPE_MATERIAL.equals(resourceType)) {
            List<Long> unitIdsCopy;
            if (CollectionUtils.isNotEmpty(unitIds)) {
                unitIdsCopy = new ArrayList<>(unitIds);
            } else {
                unitIdsCopy = new ArrayList<>();
            }
            List<Long> moduleIdsCopy;
            if (CollectionUtils.isNotEmpty(moduleIds)) {
                moduleIdsCopy = new ArrayList<>(moduleIds);
            } else {
                moduleIdsCopy = new ArrayList<>();
            }
            //增加叶子
            //找出叶子的相关上级，并移除掉，寻找下一个叶子
            //重复以上两步骤
            if (CollectionUtils.isNotEmpty(topicIds)) {
                leafChapterIds.addAll(topicIds);
                List<Long> aboutParentIds = getAboutParentIds(topicIds, chapterMap);
                unitIdsCopy.removeAll(aboutParentIds);
                leafChapterIds.addAll(unitIdsCopy);
                moduleIdsCopy.removeAll(aboutParentIds);
                List<Long> aboutParentIds1 = getAboutParentIds(unitIdsCopy, chapterMap);
                moduleIdsCopy.removeAll(aboutParentIds1);
                leafChapterIds.addAll(moduleIdsCopy);
            } else if (CollectionUtils.isNotEmpty(unitIds)) {
                leafChapterIds.addAll(unitIds);
                List<Long> aboutParentIds = getAboutParentIds(unitIds, chapterMap);
                moduleIdsCopy.removeAll(aboutParentIds);
                leafChapterIds.addAll(moduleIdsCopy);
            } else {
                leafChapterIds.addAll(moduleIds);
            }
        } else {
            leafChapterIds.addAll(topicIds);
        }
        return new ArrayList<>(leafChapterIds);
    }


    private List<Long> getAboutParentIds(List<Long> chapterIds, Map<Long, SimpleKnowVo> chapterMap) {
        List<Long> aboutParentIds = new ArrayList<>();
        for (Long id : chapterIds) {
            SimpleKnowVo simpleKnowVo = chapterMap.get(id);
            if (null != simpleKnowVo) {
                Long moduleId = simpleKnowVo.getModuleId();
                Long unitId = simpleKnowVo.getUnitId();
                if (moduleId != null) {
                    aboutParentIds.add(moduleId);
                }
                if (unitId != null) {
                    aboutParentIds.add(unitId);
                }
            }
        }
        return aboutParentIds;
    }

    private void add(List<StuQuesKnowledge> stuQuesKnowledges, StuQuesKnowledge stuQuesKnowledge) {
        if (!stuQuesKnowledges.contains(stuQuesKnowledge)) {
            stuQuesKnowledges.add(stuQuesKnowledge);
        }
    }

    /**
     * 增加到已做题目或者错题统计，会按照 questionId，topiciId去重
     *
     * @param dataType
     * @param questionId
     */
    private StuQuesKnowledge getStuQuesKnowledge(Long studentId, Integer dataType, Long questionId, SimpleKnowVo
            simpleKnowVo) {
        StuQuesKnowledge stuQuesKnowledge = new StuQuesKnowledge();

        stuQuesKnowledge.setDataType(dataType);
        stuQuesKnowledge.setStudentId(studentId);

        stuQuesKnowledge.setQuestionId(questionId);
        stuQuesKnowledge.setTopicId(simpleKnowVo.getTopicId());
        stuQuesKnowledge.setUnitId(simpleKnowVo.getUnitId());
        stuQuesKnowledge.setModuleId(simpleKnowVo.getModuleId());
        stuQuesKnowledge.setSubjectId(simpleKnowVo.getSubjectId());
        stuQuesKnowledge.setDirectoryId(simpleKnowVo.getDirectoryId());
        return stuQuesKnowledge;
    }

    /**
     * 校验题目id是否是子题id，如果是直接删除，
     * <p>
     * 合法的话：设置大题下小题的个数
     *
     * @param stuQuesKnowledges
     */
    private void validateQuesIdIsParent_V2(List<StuQuesKnowledge> stuQuesKnowledges) {
        List<Long> params = new ArrayList<>();
        for (StuQuesKnowledge stuQuesKnowledge : stuQuesKnowledges) {
            params.add(stuQuesKnowledge.getQuestionId());
        }

        FindQuestionsByIdsRequest req = new FindQuestionsByIdsRequest();
        req.setQuestionIds(new ArrayList<Long>(new HashSet<>(params)));//去重
        req.setRecursive(false);
        FindQuestionsByIdsResponse rep = questionService.findQuestionsByIds(req);
        List<Question> queslist = rep.getQuestionList();

        if (CollectionUtils.isEmpty(queslist)) {
            logger.error("queslist is null!");
            return;
        }


        List<Long> isSubQuesIds = new ArrayList<>();
        for (Question q : queslist) {
            Long questiontId = q.getId();
            Long parentQuestionId = q.getParentQuestionId();
            boolean single = q.isSingle();
            if (parentQuestionId != null) {
                if (parentQuestionId.equals(0L)) {//单题或复合题
                    List<Long> leafQuesIds = q.getAllLeafQuesIds();
                    if (CollectionUtils.isNotEmpty(leafQuesIds) && !single) {//复合题
                        setSubQuesCount(stuQuesKnowledges, questiontId, leafQuesIds.size());
                    } else {//单题
                        setSubQuesCount(stuQuesKnowledges, questiontId, 0); //默认是0个子题
                    }
                } else {//是小题的，加入不合法列表
                    isSubQuesIds.add(questiontId);
                }
            }
        }
        //移除掉小题的记录
        if (isSubQuesIds.size() > 0) {
            List<StuQuesKnowledge> stuQuesKnowledgesSub = new ArrayList<>();
            for (StuQuesKnowledge s : stuQuesKnowledges) {
                if (isSubQuesIds.contains(s.getQuestionId())) {
                    stuQuesKnowledgesSub.add(s);
                }
            }
            stuQuesKnowledges.removeAll(stuQuesKnowledgesSub);
            logger.info("remove illegal stuQuesKnowledgesSubQues:" + JsonUtil.obj2Json(stuQuesKnowledgesSub));
        }


    }

    /**
     * 设置子题个数
     *
     * @param stuQuesKnowledges
     * @param size
     */
    private void setSubQuesCount(List<StuQuesKnowledge> stuQuesKnowledges, Long questiontId, int size) {
        for (StuQuesKnowledge sk : stuQuesKnowledges) {
            if (sk.getQuestionId().equals(questiontId)) {
                sk.setQuestionCount(size);
            }
        }
    }

    private List<Long> getLeafKnowOrChapter(Integer resourceType, List<Long> topicIds, List<Long> unitIds, List<Long>
            moduleIds) {
        if (StuAnswerConstant.ResourceType.RESOURCE_TYPE_MATERIAL.equals(resourceType)) {
            if (CollectionUtils.isEmpty(topicIds)) {
                return unitIds;
            }
            if (CollectionUtils.isEmpty(topicIds)) {
                return moduleIds;
            }
        }
        return topicIds;
    }

    /**
     * 临时 缓存 统计得到 map对象 并放到 map中
     *
     * @param map
     * @param prefix
     * @param typeId
     * @param questionId
     * @param subCount   子题数目
     */
    public void countTrailEntity(Map<String, CountEntity> map, String prefix, Long typeId, Long questionId, int
            subCount) {
        if (typeId != null && typeId != 0L) {
            CountEntity entity = map.get(prefix + typeId);
            if (entity == null) {
                entity = new CountEntity(typeId, 0, 0);
                entity.setIsAllSingle(true);
                map.put(prefix + typeId, entity);
            }
            entity.addQuestionNum(questionId, 1, subCount);
            if (entity.isAllSingle() && subCount > 0) {
                entity.setIsAllSingle(false);
            }
        }

    }
}
