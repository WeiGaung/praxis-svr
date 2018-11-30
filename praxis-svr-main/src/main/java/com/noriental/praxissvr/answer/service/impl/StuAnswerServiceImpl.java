package com.noriental.praxissvr.answer.service.impl;

import com.noriental.adminsvr.bean.teaching.Chapter;
import com.noriental.adminsvr.request.RequestIdLong;
import com.noriental.adminsvr.response.ResponseEntity;
import com.noriental.adminsvr.service.teaching.ChapterService;
import com.noriental.log.TraceKeyHolder;
import com.noriental.praxissvr.answer.bean.StudentExercise;
import com.noriental.praxissvr.answer.request.UpdateCorrectRequest;
import com.noriental.praxissvr.answer.response.CorrectInfoRes;
import com.noriental.praxissvr.answer.service.AnswerCommonService;
import com.noriental.praxissvr.answer.service.StuAnswerService;
import com.noriental.praxissvr.answer.util.PraxisSsdbUtil;
import com.noriental.praxissvr.answer.util.StuAnswerUtil;
import com.noriental.praxissvr.brush.bean.StudentWork;
import com.noriental.praxissvr.brush.bean.StudentWorkAnswer;
import com.noriental.praxissvr.question.bean.Question;
import com.noriental.praxissvr.statis.bean.AnswAndResultSatis;
import com.noriental.praxissvr.statis.service.StuWorkStatisService;
import com.noriental.praxissvr.wrong.bean.SimpleKnowVo;
import com.noriental.trailsvr.bean.AnswAndResultSatisByChapter;
import com.noriental.trailsvr.bean.SendTrailCountRequest;
import com.noriental.trailsvr.service.TrailStudentChapterService;
import com.noriental.utils.json.JsonUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.invoke.MethodHandles;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("SpringJavaAutowiringInspection")
@Service(value = "stuAnswerService")
public class StuAnswerServiceImpl implements StuAnswerService {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @Resource
    private ChapterService chapterService;
    @Resource
    private StuWorkStatisService stuWorkStatisService;
    @Resource
    private TrailStudentChapterService trailStudentChapterService;
    @Resource
    private RabbitTemplate trailRabbitTemplate;
    @Resource
    private AnswerCommonService answerCommonService;

    @Override
    public void notifyTrail(StudentExercise se, int operaType, StudentExercise existedRecord) throws Exception {
        Long st = System.currentTimeMillis();
        SendTrailCountRequest req = new SendTrailCountRequest();
        List<StudentExercise> resultList = new ArrayList<>();
        req.setType(operaType);
        //如果操作类型为学生做答
        if (PraxisSsdbUtil.SUBMITTYPE == operaType) {
            List<StudentExercise> dataList = se.getStudentExerciseList();
            if (CollectionUtils.isNotEmpty(dataList)) {
                se = dataList.get(0);
            }
            for (StudentExercise exercise : dataList) {
                exercise.setQuestionIdList(null);
                resultList.add(exercise);
            }
        }else if(PraxisSsdbUtil.INTELIGENCORRECTTYPE == operaType) {//智能批改自动生效
                UpdateCorrectRequest in = new UpdateCorrectRequest();
                BeanUtils.copyProperties(se, in);
                se.setQuestionIdList(null);
                String intellCorrect = se.getIntellResult();
                if (null != intellCorrect) {
                    se.setIntellResult(intellCorrect.split("_")[2]);
                }
                resultList.add(se);
                //智能批改数据回写成功
                if (null != existedRecord && null != existedRecord.getResult()) {
                    //CorrectInfoRes correctCollectionInfoRes = answerCommonService.correctInfo(se, existedRecord);
                    //req.setCorrectStatus("7");
                    CorrectInfoRes correctCollectionInfoRes = answerCommonService.autoIntellCorrectInfo(se, existedRecord);
                    req.setCorrectStatus(correctCollectionInfoRes.getCoorectStatus());
                    req.setHistoryResult(existedRecord.getResult());
                } else {
                req.setType(PraxisSsdbUtil.SECSTATICTYPE);
            }
        }else {//如果操作类型为批改||智能批改
            //查询批改是否为重复批改
            UpdateCorrectRequest in = new UpdateCorrectRequest();
            BeanUtils.copyProperties(se, in);
            se.setQuestionIdList(null);
            String intellCorrect = se.getIntellResult();
            if (null != intellCorrect) {
                se.setIntellResult(intellCorrect.split("_")[2]);
            }
            resultList.add(se);
            //智能批改数据回写成功
            if (null != existedRecord && null != existedRecord.getResult()) {
                CorrectInfoRes correctCollectionInfoRes = answerCommonService.correctInfo(se, existedRecord);
                req.setCorrectStatus(correctCollectionInfoRes.getCoorectStatus());
                req.setHistoryResult(existedRecord.getResult());
            } else {
                req.setType(PraxisSsdbUtil.SECSTATICTYPE);
            }
        }
        req.setExercises(resultList);
        req.setStudentId(se.getStudentId());
        req.setExerciseType(Integer.valueOf(se.getExerciseSource()));
        req.setHistoryId(se.getResourceId());
        req.setRedoType(se.getRedoSource() != null ? Integer.valueOf(se.getRedoSource()) : 0);
        req.setRequestId(TraceKeyHolder.getTraceKey());
        trailRabbitTemplate.convertAndSend(req);
        /*if(1 == operaType){
            logger.info("mq已发送 学生做答 内容:{}" ,JsonUtil.obj2Json(req));
        }else if(8 == operaType) {
            logger.info("mq已发送 智能批改自动生效 内容:{}" ,JsonUtil.obj2Json(req));
        }else {
            logger.info("mq已发送 批改 内容:{}" ,JsonUtil.obj2Json(req));
        }*/
        logger.info("notifyTrail  json Data:" + JsonUtil.obj2Json(req));
        Long et = System.currentTimeMillis();
        logger.info("notifyTrail cost time {} ms:", (et - st));
    }


    @Override
    public void statisAnsw(List<StudentWorkAnswer> studentWorkAnswersKnow, Map<Long, Question> quesMap, Long
            studentId, Map<Long, SimpleKnowVo> topicKnowMap) {
        try {
            long st = System.currentTimeMillis();
            Map<String, SimpleKnowVo> allKnowMap = convertAllKnowMap(topicKnowMap);
            //统计知识点已做做对数量
            List<AnswAndResultSatis> answAndResultSatises = new ArrayList<>();
            statisWorkAnswResult(answAndResultSatises, studentWorkAnswersKnow, allKnowMap);
            logger.debug("didAndRightCountKnow:" + JsonUtil.obj2Json(answAndResultSatises));

            //库和缓存更新
            if (answAndResultSatises.size() > 0) {
                for (AnswAndResultSatis an : answAndResultSatises) {
                    an.setStudentId(studentId);
                }
                stuWorkStatisService.updateStuWorkSatisList(answAndResultSatises);
            }
            long et = System.currentTimeMillis();
            if ((et - st) > 1000) {
                logger.warn("statisAnsw method cost :" + (et - st) + " millis");
            } else {
                logger.debug("statisAnsw method cost :" + (et - st) + " millis");
            }
        } catch (Exception e) {
            logger.error("答题后置业务-知识点统计正确错误个数失败", e);
        }

    }

    @Override
    public void statisCorrect(Long correctQuesId, StudentWorkAnswer studentWorkAnswerKnow, Map<Long, SimpleKnowVo>
            topicKnowMap) {
        try {
            long startMill = System.currentTimeMillis();
            //统计结果
            List<AnswAndResultSatis> countSatises = new ArrayList<>();
            Map<String, SimpleKnowVo> allKnowMap = convertAllKnowMap(topicKnowMap);
            //子题
            if (CollectionUtils.isNotEmpty(studentWorkAnswerKnow.getSubQuesAnswers())) {
                //统计知识点正确数量
                correctStatisSubQues(studentWorkAnswerKnow, correctQuesId, countSatises, allKnowMap);
                //单题
            } else {
                //统计知识点正确数量
                correctStatisSingleQues(studentWorkAnswerKnow, countSatises, allKnowMap);
            }
            //更新知识点答题个数
            Long studentId = studentWorkAnswerKnow.getStudentId();
            logger.debug("rigthCount:" + JsonUtil.obj2Json(countSatises));
            if (countSatises.size() > 0) {
                for (AnswAndResultSatis an : countSatises) {
                    an.setStudentId(studentId);
                }
                stuWorkStatisService.updateStuWorkSatisList(countSatises);
            }
            long endMill = System.currentTimeMillis();
            if ((endMill - startMill) > 1000) {
                logger.warn("statisCorrect method cost:" + (endMill - startMill));
            } else {
                logger.debug("statisCorrect method  cost:" + (endMill - startMill));
            }
        } catch (Exception e) {
            logger.error("批改后置业务-知识点统计失败", e);
        }

    }

    /**
     * 单独统计 一个题时
     * 其统计结果  统计这一个的正确与否
     *
     * @return
     */
    @Override
    public Map<Long, AnswAndResultSatisByChapter> statisAnswCorrectChapter(StudentExercise se, Map<Long, Question>
            quesMap) {
        //统计结果
        Map<Long, AnswAndResultSatisByChapter> chapterMap = new HashMap<>();
        try {
            long st = System.currentTimeMillis();
            Long studentId = se.getStudentId();
            Long questionId = se.getQuestionId();
            String result = se.getResult();
            Integer structId = se.getStructId();
            Question q = quesMap.get(questionId);
            if (q != null) {
                Integer answerNum = q.getAnswerNum();
                Double rightNumber = StuAnswerUtil.getRightNumberC(structId, result, result, answerNum);
                produceChapterMap(chapterMap, q.getChapterId1(), studentId, rightNumber);
                produceChapterMap(chapterMap, q.getChapterId2(), studentId, rightNumber);
                produceChapterMap(chapterMap, q.getChapterId3(), studentId, rightNumber);
            }
            if (chapterMap.size() > 0) {
                List<AnswAndResultSatisByChapter> answAndResultSatisByChapters = new ArrayList<>(chapterMap.values());
                logger.debug("answAndResultSatisByChapters:" + JsonUtil.obj2Json(answAndResultSatisByChapters));
                trailStudentChapterService.updateStuWorkSatisChapter(answAndResultSatisByChapters);
            }
            long et = System.currentTimeMillis();
            if ((et - st) > 1000) {
                logger.warn("statisCorrectChapter method cost :" + (et - st) + " millis");
            } else {
                logger.debug("statisCorrectChapter method cost :" + (et - st) + " millis");
            }
        } catch (Exception e) {
            logger.error("学生做答|批改后置业务-章节统计失败", e);
        }

        return chapterMap;
    }

    @Override
    public Map<Long, AnswAndResultSatisByChapter> statisAnswCorrectChapter(List<StudentExercise> list, Map<Long,
            Question> quesMap) {
        //统计结果
        long st = System.currentTimeMillis();
        Map<Long, AnswAndResultSatisByChapter> chapterMap = new HashMap<>(5);
        try {
            for (StudentExercise se : list) {
                Long studentId = se.getStudentId();
                Long questionId = se.getQuestionId();
                String result = se.getResult();
                Integer structId = se.getStructId();
                Question q = quesMap.get(questionId);
                if (q != null) {
                    Integer answerNum = q.getAnswerNum();
                    Double rightNumber = StuAnswerUtil.getRightNumberC(structId, result, result, answerNum);
                    produceChapterMap(chapterMap, q.getChapterId1(), studentId, rightNumber);
                    produceChapterMap(chapterMap, q.getChapterId2(), studentId, rightNumber);
                    produceChapterMap(chapterMap, q.getChapterId3(), studentId, rightNumber);
                }
            }
            if (chapterMap.size() > 0) {
                List<AnswAndResultSatisByChapter> answAndResultSatisByChapters = new ArrayList<>(chapterMap.values());
                logger.debug("answAndResultSatisByChapters:" + JsonUtil.obj2Json(answAndResultSatisByChapters));
                trailStudentChapterService.updateStuWorkSatisChapter(answAndResultSatisByChapters);
            }
            long et = System.currentTimeMillis();
            if ((et - st) > 1000) {
                logger.warn("statisAnswCorrectChapter method cost :" + (et - st) + " millis");
            } else {
                logger.debug("statisAnswCorrectChapter method cost :" + (et - st) + " millis");
            }
        } catch (Exception e) {
            logger.error("智能批改后置业务-章节统计失败", e);
        }


        return chapterMap;
    }

    /**
     * 统计多个题的正确 错误
     * <p/>
     * 多题一起统计
     *
     * @param studentId
     * @param seList
     * @return
     */
    @Override
    public Map<Long, AnswAndResultSatisByChapter> statisAnswCorrectChapter(Long studentId, List<StudentExercise>
            seList, Map<Long, Question> questionMap) {
        long st = System.currentTimeMillis();
        //统计结果
        Map<Long, AnswAndResultSatisByChapter> chapterMap = new HashMap<>(5);
        try {
            //记录 某个 子题对应的章节信息
            Map<Long, Set<Long>> chapterIdMap = new HashMap<>(5);
            //遍历 所有的答案
            for (StudentExercise answer : seList) {
                Integer structId = answer.getStructId();
                //统计 正确的 计算次数
                Double rightNumber = StuAnswerUtil.getRightNumberS(structId, answer.getResult(), answer.getResult());

                int allNum = 1;
                Long questionId = answer.getQuestionId();
                if (answer.getParentQuestionId() != null && answer.getParentQuestionId() > 0) {
                    allNum = questionMap.get(answer.getParentQuestionId()).getAllLeafQuesIds().size();
                    questionId = answer.getParentQuestionId();
                }
                Question q = questionMap.get(questionId);
                //记录chapter1 的数据 正确题数的数据
                produceChapterMap(chapterMap, q.getChapterId1(), studentId, rightNumber / allNum);
                produceChapterMap(chapterMap, q.getChapterId2(), studentId, rightNumber / allNum);
                produceChapterMap(chapterMap, q.getChapterId3(), studentId, rightNumber / allNum);

                //记录chapter1 的数据 大题的数据
                produceChapterMap(chapterMap, q.getChapterId1(), studentId, chapterIdMap, questionId);
                produceChapterMap(chapterMap, q.getChapterId2(), studentId, chapterIdMap, questionId);
                produceChapterMap(chapterMap, q.getChapterId3(), studentId, chapterIdMap, questionId);
            }
            if (MapUtils.isNotEmpty(chapterMap)) {
                List<AnswAndResultSatisByChapter> answAndResultSatisByChapters = new ArrayList<>(chapterMap.values());
                logger.debug("answAndResultSatisByChapters:" + JsonUtil.obj2Json(answAndResultSatisByChapters));
                trailStudentChapterService.updateStuWorkSatisChapter(answAndResultSatisByChapters);
            }
            long et = System.currentTimeMillis();
            if ((et - st) > 1000) {
                logger.warn("statisAnswChapter method cost :" + (et - st) + " millis");
            } else {
                logger.debug("statisAnswChapter method cost :" + (et - st) + " millis");
            }
        } catch (Exception e) {
            logger.error("答题后置业务-章节统计多个题的正确错误失败", e);
        }

        return chapterMap;
    }


    /**
     * 统一处理
     *
     * @param chapterMap
     * @param chapterIdList
     * @param studentId
     * @param rightNum
     */
    private void produceChapterMap(Map<Long, AnswAndResultSatisByChapter> chapterMap, List<Long> chapterIdList, Long
            studentId, Double rightNum) {
        Map<Long, Long> subjectChapterMap = new ConcurrentHashMap<>(10);
        if (CollectionUtils.isNotEmpty(chapterIdList)) {
            for (Long chapterId : chapterIdList) {
                AnswAndResultSatisByChapter chapterObj = chapterMap.get(chapterId);
                if (chapterObj == null) {
                    Long subjectId = getSubjectIdByChapterId(chapterId,subjectChapterMap);
                    chapterObj = new AnswAndResultSatisByChapter(chapterId, subjectId, studentId);
                    chapterObj.setRightNumber(0D);
                    chapterObj.setAnswerNumber(0);
                    chapterMap.put(chapterId, chapterObj);
                }
                chapterObj.setRightNumber(chapterObj.getRightNumber() + rightNum);
            }
        }
    }

    /**
     * 统一处理
     *
     * @param chapterMap
     * @param chapterIdList
     * @param studentId
     */
    private void produceChapterMap(Map<Long, AnswAndResultSatisByChapter> chapterMap, List<Long> chapterIdList, Long
            studentId, Map<Long, Set<Long>> chapterIdMap, Long parentQuestionId) {
        Map<Long, Long> subjectChapterMap = new ConcurrentHashMap<>(10);
        if (CollectionUtils.isNotEmpty(chapterIdList)) {
            for (Long chapterId : chapterIdList) {
                AnswAndResultSatisByChapter chapterObj = chapterMap.get(chapterId);
                if (chapterObj == null) {
                    Long subjectId = getSubjectIdByChapterId(chapterId,subjectChapterMap);
                    chapterObj = new AnswAndResultSatisByChapter(chapterId, subjectId, studentId);
                    chapterObj.setRightNumber(0D);
                    chapterObj.setAnswerNumber(0);
                    chapterMap.put(chapterId, chapterObj);
                }
                if (!chapterIdMap.containsKey(chapterId)) {
                    chapterIdMap.put(chapterId, new HashSet<Long>());
                }
                chapterIdMap.get(chapterId).add(parentQuestionId);
                chapterObj.setAnswerNumber(chapterIdMap.get(chapterId).size());
            }
        }
    }



    /**
     * 获取 章节信息
     * @param chapterId
     * @return
     */
    private Long getSubjectIdByChapterId(long chapterId, Map<Long, Long> subjectChapterMap ) {
        if (subjectChapterMap.containsKey(chapterId)) {
            return subjectChapterMap.get(chapterId);
        }
        ResponseEntity<Chapter> byId = chapterService.findById(new RequestIdLong(chapterId));
        if (byId.success() && byId.getEntity() != null) {
            Long subjectId1 = byId.getEntity().getSubjectId();
            subjectChapterMap.put(chapterId, subjectId1);
            return subjectId1;
        }
        return 0L;
    }




    /**
     * 统计作业的已做做对数量
     *
     * @param answAndResultSatises 作业数量统计
     * @param studentWorkAnswers   中间结果
     */
    private void statisWorkAnswResult(List<AnswAndResultSatis> answAndResultSatises, List<StudentWorkAnswer>
            studentWorkAnswers, Map<String, SimpleKnowVo> allKnowMap) {
        for (StudentWorkAnswer studentWorkAnswer : studentWorkAnswers) {
            Integer resourceType = studentWorkAnswer.getResourceType();
            String result = studentWorkAnswer.getResult();
            String blankResult = studentWorkAnswer.getBlankResult();
            Integer structId = studentWorkAnswer.getStructId();
            List<StudentWorkAnswer> subStudentWorkAnswerses = studentWorkAnswer.getSubQuesAnswers();
            List<Long> topicIds = studentWorkAnswer.getTopicIds();

            List<Long> unitIds = studentWorkAnswer.getUnitIds();
            List<Long> moduleIds = studentWorkAnswer.getModuleIds();
            //如果是单题
            if (subStudentWorkAnswerses == null) {
                //遍历单题的知识点
                for (Long topicId : topicIds) {
                    //正确次数
                    Double rightNumber = StuAnswerUtil.getRightNumberS(structId, result, blankResult);

                    //答题次数
                    Integer answerNumber = 1;
                    //添加作业数量统计
                    addToAnswAndResultSatises(resourceType, StudentWork.WorkLevel.TOPIC, topicId, rightNumber,
                            answerNumber, answAndResultSatises, allKnowMap);
                }

                for (Long unitId : unitIds) {
                    //正确次数
                    Double rightNumber = StuAnswerUtil.getRightNumberS(structId, result, blankResult);

                    //答题次数
                    Integer answerNumber = 1;
                    //添加作业数量统计
                    addToAnswAndResultSatises(resourceType, StudentWork.WorkLevel.UNIT, unitId, rightNumber,
                            answerNumber, answAndResultSatises, allKnowMap);
                }
                for (Long moduleId : moduleIds) {
                    //正确次数
                    Double rightNumber = StuAnswerUtil.getRightNumberS(structId, result, blankResult);

                    //答题次数
                    Integer answerNumber = 1;
                    //添加作业数量统计
                    addToAnswAndResultSatises(resourceType, StudentWork.WorkLevel.MODULE, moduleId, rightNumber,
                            answerNumber, answAndResultSatises, allKnowMap);
                }

                //组合题
            } else {

                //小题答对比例，保留两位小数
                Double subQuesRightPercent = 0D;
                //小题答对次数
                Double subQuesRightNumber = 0d;
                Double rightNumberQueses = StuAnswerUtil.getRightNumberQueses(subStudentWorkAnswerses);
                subQuesRightNumber += rightNumberQueses;
                //小题总数
                Integer subQuesNumber = subStudentWorkAnswerses.size();
                //小题答对比例 = 小题答对次数/小题总数
                if (!subQuesRightNumber.equals(0d)) {
                    subQuesRightPercent = subQuesRightNumber / Double.valueOf(subQuesNumber);
                    DecimalFormat df = new DecimalFormat("######0.00");
                    String subQuesRightPercentStr = df.format(subQuesRightPercent);
                    subQuesRightPercent = new Double(subQuesRightPercentStr);
                }


                //-----------------------------------------------------------------------------------主题统计-----------
                //大题和小题去重后的所有知识点
                Set<Long> allTopicIds = new HashSet<>();
                //父题知识点
                //子题知识点
                for (StudentWorkAnswer subStudentWorkAnswers : subStudentWorkAnswerses) {
                    List<Long> subTopicIds = subStudentWorkAnswers.getTopicIds();
                    //增加父题知识点
                    allTopicIds.addAll(topicIds);
                    //增加子题知识点
                    allTopicIds.addAll(subTopicIds);
                }

                //每一个知识点的答题次数
                for (Long topicId : allTopicIds) {
                    Integer answerNumber = 1;
                    //添加作业数量统计
                    addToAnswAndResultSatises(resourceType, StudentWork.WorkLevel.TOPIC, topicId, 0d, answerNumber,
                            answAndResultSatises, allKnowMap);
                }
                //每个知识点答对次数
                for (Long topicId : allTopicIds) {
                    //小题中是否存在该知识点
                    List<StudentWorkAnswer> subquesOfTopic = getSubsQuesOfLevelId(StudentWork.WorkLevel.TOPIC,
                            topicId, subStudentWorkAnswerses);

                    //单独的大题知识点：按小题答对比例计算
                    if (subquesOfTopic.size() == 0) {
                        if (!subQuesRightPercent.equals(0D)) {
                            //添加作业数量统计
                            addToAnswAndResultSatises(resourceType, StudentWork.WorkLevel.TOPIC, topicId,
                                    subQuesRightPercent, 0, answAndResultSatises, allKnowMap);
                        }
                        //该知识点属于小题:有该知识点的小题答对次数/有该知识点的小题数
                    } else {
                        //有该知识点的小题答对数
                        Double subQuesRightNumberWithTopic = 0D;

                        Double rightNumberQuesesT = StuAnswerUtil.getRightNumberQueses(subquesOfTopic);
                        subQuesRightNumberWithTopic += rightNumberQuesesT;

                        //有该知识点的小题数
                        Integer subQuesNumberWithTopic = subquesOfTopic.size();
                        // 两者相÷
                        Double topicRightNumber = subQuesRightNumberWithTopic / Double.valueOf(subQuesNumberWithTopic);
                        if (!topicRightNumber.equals(0D)) {
                            DecimalFormat df = new DecimalFormat("######0.00");
                            String topicRightNumberStr = df.format(topicRightNumber);
                            addToAnswAndResultSatises(resourceType, StudentWork.WorkLevel.TOPIC, topicId, new Double
                                    (topicRightNumberStr), 0, answAndResultSatises, allKnowMap);
                        }
                    }
                }


                //-----------------------------------------------------------------------------------单元统计-------
                //大题和小题去重后的所有单元id
                Set<Long> allUnitIds = new HashSet<>();
                //父题单元ids
                //增加父题单元id
                allUnitIds.addAll(unitIds);
                //子题单元ids
                for (StudentWorkAnswer subStudentWorkAnswers : subStudentWorkAnswerses) {
                    List<Long> subUnitIds = subStudentWorkAnswers.getUnitIds();
                    //增加子题单元id
                    allUnitIds.addAll(subUnitIds);
                }
                //每一个单元的答题次数
                for (Long unitId : allUnitIds) {
                    Integer answerNumber = 1;
                    addToAnswAndResultSatises(resourceType, StudentWork.WorkLevel.UNIT, unitId, 0d, answerNumber,
                            answAndResultSatises, allKnowMap);
                }

                //每一个单元的答对题目数量
                for (Long unitId : allUnitIds) {
                    List<StudentWorkAnswer> subquesOfUnit = getSubsQuesOfLevelId(StudentWork.WorkLevel.UNIT, unitId,
                            subStudentWorkAnswerses);
                    //单独的大题知识点：按小题答对比例计算
                    if (subquesOfUnit.size() == 0) {
                        if (!subQuesRightPercent.equals(0D)) {
                            //添加作业数量统计
                            addToAnswAndResultSatises(resourceType, StudentWork.WorkLevel.UNIT, unitId,
                                    subQuesRightPercent, 0, answAndResultSatises, allKnowMap);
                        }
                        //该知识点属于小题:有该知识点的小题答对，该知识点答对次数增加  1/有该知识点的小题数
                    } else {
                        //有该知识点的小题答对数
                        Double subQuesRightNumberWithUnit = 0D;
                        Double rightNumberQuesesU = StuAnswerUtil.getRightNumberQueses(subquesOfUnit);
                        subQuesRightNumberWithUnit += rightNumberQuesesU;

                        //有该知识点的小题数
                        Integer subQuesNumberWithUnit = subquesOfUnit.size();
                        //该知识点答对次数 两者相÷
                        Double knowledgeRightNumber = subQuesRightNumberWithUnit / Double.valueOf
                                (subQuesNumberWithUnit);
                        if (!knowledgeRightNumber.equals(0D)) {
                            DecimalFormat df = new DecimalFormat("######0.00");
                            String knowledgeRightNumberStr = df.format(knowledgeRightNumber);
                            addToAnswAndResultSatises(resourceType, StudentWork.WorkLevel.UNIT, unitId, new Double
                                    (knowledgeRightNumberStr), 0, answAndResultSatises, allKnowMap);
                        }
                    }
                }

                //-----------------------------------------------------------------------------------模块统计---------
                //大题和小题去重后的所有模块id
                Set<Long> allmoduleIds = new HashSet<>();
                //父题模块ids
                //增加父题模块id
                allmoduleIds.addAll(moduleIds);
                //子题模块ids
                for (StudentWorkAnswer subStudentWorkAnswers : subStudentWorkAnswerses) {
                    List<Long> submoduleIds = subStudentWorkAnswers.getModuleIds();
                    //增加子题模块id
                    allmoduleIds.addAll(submoduleIds);
                }
                //每一个模块的已做题目数量和已做题目
                for (Long moduleId : allmoduleIds) {
                    //答题次数
                    Integer answerNumber = 1;
                    addToAnswAndResultSatises(resourceType, StudentWork.WorkLevel.MODULE, moduleId, 0d, answerNumber,
                            answAndResultSatises, allKnowMap);
                }
                //每一个模块的答对题目数量
                for (Long moduleId : allmoduleIds) {
                    List<StudentWorkAnswer> subquesOfmodule = getSubsQuesOfLevelId(StudentWork.WorkLevel.MODULE,
                            moduleId, subStudentWorkAnswerses);
                    //单独的大题知识点：按小题答对比例计算
                    if (subquesOfmodule.size() == 0) {
                        if (!subQuesRightPercent.equals(0D)) {
                            //添加作业数量统计
                            addToAnswAndResultSatises(resourceType, StudentWork.WorkLevel.MODULE, moduleId,
                                    subQuesRightPercent, 0, answAndResultSatises, allKnowMap);
                        }
                        //该知识点属于小题:有该知识点的小题答对，该知识点答对次数增加  1/有该知识点的小题数
                    } else {
                        //有该知识点的小题答对数
                        Double subQuesRightNumberWithModule = 0D;
                        Double rightNumberQuesesM = StuAnswerUtil.getRightNumberQueses(subquesOfmodule);
                        subQuesRightNumberWithModule += rightNumberQuesesM;

                        //有该知识点的小题数
                        Integer subQuesNumberWithModule = subquesOfmodule.size();
                        //该知识点答对次数 两者相÷
                        Double knowledgeRightNumber = Double.valueOf(subQuesRightNumberWithModule) / Double.valueOf
                                (subQuesNumberWithModule);
                        if (!knowledgeRightNumber.equals(0D)) {
                            DecimalFormat df = new DecimalFormat("######0.00");
                            String knowledgeRightNumberStr = df.format(knowledgeRightNumber);
                            addToAnswAndResultSatises(resourceType, StudentWork.WorkLevel.MODULE, moduleId, new
                                    Double(knowledgeRightNumberStr), 0, answAndResultSatises, allKnowMap);
                        }
                    }
                }
            }
        }

    }

    /**
     * 在小题列表中获得某个知识点的相关的小题
     *
     * @param level                   级别
     * @param levelId                 模块单元主题id
     * @param subStudentWorkAnswerses 大题下所有的小题
     * @return 相关的小题
     */
    private List<StudentWorkAnswer> getSubsQuesOfLevelId(Integer level, Long levelId, List<StudentWorkAnswer>
            subStudentWorkAnswerses) {
        List<StudentWorkAnswer> repeateSubs = new ArrayList<>();
        for (StudentWorkAnswer subQues : subStudentWorkAnswerses) {
            List<Long> sublevleIds = new ArrayList<>();
            if (level.equals(StudentWork.WorkLevel.TOPIC)) {
                sublevleIds = subQues.getTopicIds();
            } else if (level.equals(StudentWork.WorkLevel.UNIT)) {
                sublevleIds = subQues.getUnitIds();
            } else if (level.equals(StudentWork.WorkLevel.MODULE)) {
                sublevleIds = subQues.getModuleIds();
            }
            if (sublevleIds.contains(levelId)) {
                repeateSubs.add(subQues);
            }
        }
        return repeateSubs;
    }

    /**
     * 作业数量增加到结果统计，一次发送到队列
     *
     * @param level                知识点级别 1,2,3模块单元主题
     * @param levelId              模块单元主题id
     * @param rightNumber          正确次数
     * @param answerNumber         回答次数
     * @param answAndResultSatises 数量统计结果
     * @param allKnowMap
     */
    private void addToAnswAndResultSatises(@SuppressWarnings("UnusedParameters") Integer resourceType, Integer level,
                                           Long levelId, Double rightNumber, Integer answerNumber,
                                           List<AnswAndResultSatis> answAndResultSatises, Map<String, SimpleKnowVo>
                                                   allKnowMap) {


        AnswAndResultSatis answAndResultSatis = new AnswAndResultSatis();
        answAndResultSatis.setLevel(level);
        if (level.equals(StudentWork.WorkLevel.TOPIC)) {
            SimpleKnowVo simpleKnowVo = allKnowMap.get("t" + levelId);
            if (simpleKnowVo == null) {////有些知识点或者章节可能被删除过
                return;
            }
            answAndResultSatis.setTopicId(levelId);
            answAndResultSatis.setUnitId(simpleKnowVo.getUnitId());
            answAndResultSatis.setModuleId(simpleKnowVo.getModuleId());
            answAndResultSatis.setSubjectId(simpleKnowVo.getSubjectId());
        } else if (level.equals(StudentWork.WorkLevel.UNIT)) {
            SimpleKnowVo simpleKnowVo = allKnowMap.get("u" + levelId);
            if (simpleKnowVo == null) {////有些知识点或者章节可能被删除过
                return;
            }
            answAndResultSatis.setUnitId(levelId);
            answAndResultSatis.setModuleId(simpleKnowVo.getModuleId());
            answAndResultSatis.setSubjectId(simpleKnowVo.getSubjectId());
        } else if (level.equals(StudentWork.WorkLevel.MODULE)) {
            SimpleKnowVo simpleKnowVo = allKnowMap.get("m" + levelId);
            if (simpleKnowVo == null) {////有些知识点或者章节可能被删除过
                return;
            }
            answAndResultSatis.setModuleId(levelId);
            answAndResultSatis.setSubjectId(simpleKnowVo.getSubjectId());
        }


        //统计中已经有该级别统计，累加
        if (answAndResultSatises.contains(answAndResultSatis)) {
            answAndResultSatis = answAndResultSatises.get(answAndResultSatises.indexOf(answAndResultSatis));
            //正确次数
            answAndResultSatis.setRightNumber(answAndResultSatis.getRightNumber() + rightNumber);
            //答题次数
            answAndResultSatis.setAnswerNumber(answAndResultSatis.getAnswerNumber() + answerNumber);
            //没有，新建
        } else {
            //正确次数
            answAndResultSatis.setRightNumber(rightNumber);
            //答题次数
            answAndResultSatis.setAnswerNumber(answerNumber);
            //加入统计
            answAndResultSatises.add(answAndResultSatis);
        }
    }


    private void correctStatisSingleQues(StudentWorkAnswer studentWorkAnswer, List<AnswAndResultSatis>
            answAndResultSatises, Map<String, SimpleKnowVo> allKnowMap) {
        String result = studentWorkAnswer.getResult();
        Integer structId = studentWorkAnswer.getStructId();
        String blankResult = studentWorkAnswer.getBlankResult();
        List<Long> questionTopics = studentWorkAnswer.getTopicIds();
        List<Long> questionModules = studentWorkAnswer.getModuleIds();
        List<Long> questionUnits = studentWorkAnswer.getUnitIds();
        Integer resourceType = studentWorkAnswer.getResourceType();
        Integer answerNum = studentWorkAnswer.getAnswerNum();


        for (Long topicId : questionTopics) {
            //正确次数
            Double rightNumber = StuAnswerUtil.getRightNumberC(structId, result, blankResult, answerNum);


            if (rightNumber.compareTo(0d) != 0) {
                addToAnswAndResultSatises(resourceType, StudentWork.WorkLevel.TOPIC, topicId, rightNumber, 0,
                        answAndResultSatises, allKnowMap);
            }
        }

        //单元统计---------------------------------------------------------------------------------------------------
        for (Long unitId : questionUnits) {
            //正确次数
            Double rightNumber = StuAnswerUtil.getRightNumberC(structId, result, blankResult, answerNum);

            if (Double.compare(rightNumber, 0d) == 1) {
                addToAnswAndResultSatises(resourceType, StudentWork.WorkLevel.UNIT, unitId, rightNumber, 0,
                        answAndResultSatises, allKnowMap);
            }

        }
        //模块统计---------------------------------------------------------------------------------------------------
        for (Long moduleId : questionModules) {
            //正确次数
            Double rightNumber = StuAnswerUtil.getRightNumberC(structId, result, blankResult, answerNum);

            if (Double.compare(rightNumber, 0d) == 1) {
                addToAnswAndResultSatises(resourceType, StudentWork.WorkLevel.MODULE, moduleId, rightNumber, 0,
                        answAndResultSatises, allKnowMap);
            }

        }
    }


    /**
     * 复合题子题批改时统计做对已做数量
     *
     * @param studentWorkAnswer    整个复合题情况
     * @param currSubQuesId        当前批改子题id
     * @param answAndResultSatises 统计list
     * @param allKnowMap
     */
    private void correctStatisSubQues(StudentWorkAnswer studentWorkAnswer, Long currSubQuesId,
                                      List<AnswAndResultSatis> answAndResultSatises, Map<String, SimpleKnowVo>
                                              allKnowMap) {
        List<Long> parentQuestionTopics = studentWorkAnswer.getTopicIds();
        List<Long> parentQuestionUnits = studentWorkAnswer.getUnitIds();
        List<Long> parentQuestionModules = studentWorkAnswer.getModuleIds();
        List<StudentWorkAnswer> subQuesAnswers = studentWorkAnswer.getSubQuesAnswers();
        Integer resourceType = studentWorkAnswer.getResourceType();
        //当前小题
        StudentWorkAnswer currentSubQues = new StudentWorkAnswer();
        currentSubQues.setQuestionId(currSubQuesId);
        currentSubQues = subQuesAnswers.get(subQuesAnswers.indexOf(currentSubQues));

        String result = currentSubQues.getResult();
        //	        String questionType = currentSubQues.getQuestionType();
        Integer structId = currentSubQues.getStructId();
        String blankResult = currentSubQues.getBlankResult();
        Integer answerNum = currentSubQues.getAnswerNum();
        List<Long> questionTopics = currentSubQues.getTopicIds();
        List<Long> questionUnits = currentSubQues.getUnitIds();
        List<Long> questionModules = currentSubQues.getModuleIds();


        //主题统计---------------------------------------------------------------------------------------------------
        //循环大题的知识点
        for (Long thisParentQuesTopicId : parentQuestionTopics) {
            List<StudentWorkAnswer> subquesOfTopic = getSubsQuesOfLevelId(StudentWork.WorkLevel.TOPIC,
                    thisParentQuesTopicId, subQuesAnswers);
            //大题的知识点在小题中不存在
            if (subquesOfTopic.size() == 0) {
                //该大题知识点的答对次数：小题答对，按小题个数比例计算
                Double topicRightNumber = StuAnswerUtil.getRightNumberCs(structId, result, blankResult, answerNum,
                        subQuesAnswers.size());

                if (!topicRightNumber.equals(0D)) {
                    DecimalFormat df = new DecimalFormat("######0.00");
                    String topicRightNumberStr = df.format(topicRightNumber);
                    addToAnswAndResultSatises(resourceType, StudentWork.WorkLevel.TOPIC, thisParentQuesTopicId, new
                            Double(topicRightNumberStr), 0, answAndResultSatises, allKnowMap);
                }
            }
        }

        //循环本次小题的知识点
        for (Long thisSubQuesTopicId : questionTopics) {

            //正确次数：按该知识点分布在所有小题中的比例计算。 同时有该知识点小题数量
            List<StudentWorkAnswer> repeatSubquesOfTopic = getSubsQuesOfLevelId(StudentWork.WorkLevel.TOPIC,
                    thisSubQuesTopicId, subQuesAnswers);

            Double topicRightNumber = StuAnswerUtil.getRightNumberCs(structId, result, blankResult, answerNum,
                    repeatSubquesOfTopic.size());

            if (topicRightNumber != 0) {
                DecimalFormat df = new DecimalFormat("######0.00");
                String topicRightNumberStr = df.format(topicRightNumber);
                addToAnswAndResultSatises(resourceType, StudentWork.WorkLevel.TOPIC, thisSubQuesTopicId, new Double
                        (topicRightNumberStr), 0, answAndResultSatises, allKnowMap);
            }
        }

        //单元统计---------------------------------------------------------------------------------------------------
        //循环大题的知识点
        for (Long thisParentQuesUnitId : parentQuestionUnits) {
            List<StudentWorkAnswer> subquesOfUnit = getSubsQuesOfLevelId(StudentWork.WorkLevel.UNIT,
                    thisParentQuesUnitId, subQuesAnswers);
            //大题的知识点在小题中不存在
            if (subquesOfUnit.size() == 0) {
                //该大题知识点的答对次数：小题答对，按小题个数比例计算
                Double topicRightNumber = StuAnswerUtil.getRightNumberCs(structId, result, blankResult, answerNum,
                        subQuesAnswers.size());

                if (!topicRightNumber.equals(0D)) {
                    DecimalFormat df = new DecimalFormat("######0.00");
                    String topicRightNumberStr = df.format(topicRightNumber);
                    addToAnswAndResultSatises(resourceType, StudentWork.WorkLevel.UNIT, thisParentQuesUnitId, new
                            Double(topicRightNumberStr), 0, answAndResultSatises, allKnowMap);
                }
            }
        }
        //循环小题知识点
        for (Long thisSubQuesUnitId : questionUnits) {

            //正确次数：按该知识点分布在所有小题中的比例计算。 同时有该知识点小题数量
            List<StudentWorkAnswer> repeatSubquesOfUnit = getSubsQuesOfLevelId(StudentWork.WorkLevel.UNIT,
                    thisSubQuesUnitId, subQuesAnswers);

            Double topicRightNumber = StuAnswerUtil.getRightNumberCs(structId, result, blankResult, answerNum,
                    repeatSubquesOfUnit.size());

            if (topicRightNumber != 0) {
                DecimalFormat df = new DecimalFormat("######0.00");
                String topicRightNumberStr = df.format(topicRightNumber);
                addToAnswAndResultSatises(resourceType, StudentWork.WorkLevel.UNIT, thisSubQuesUnitId, new Double
                        (topicRightNumberStr), 0, answAndResultSatises, allKnowMap);
            }
        }

        //模块统计---------------------------------------------------------------------------------------------------
        //循环大题的知识点
        for (Long thisParentQuesModuleId : parentQuestionModules) {
            List<StudentWorkAnswer> subquesOfModule = getSubsQuesOfLevelId(StudentWork.WorkLevel.MODULE,
                    thisParentQuesModuleId, subQuesAnswers);
            //大题的知识点在小题中不存在
            if (subquesOfModule.size() == 0) {
                //该大题知识点的答对次数：小题答对，按小题个数比例计算
                Double topicRightNumber = StuAnswerUtil.getRightNumberCs(structId, result, blankResult, answerNum,
                        subQuesAnswers.size());

                if (!topicRightNumber.equals(0D)) {
                    DecimalFormat df = new DecimalFormat("######0.00");
                    String topicRightNumberStr = df.format(topicRightNumber);
                    addToAnswAndResultSatises(resourceType, StudentWork.WorkLevel.MODULE, thisParentQuesModuleId, new
                            Double(topicRightNumberStr), 0, answAndResultSatises, allKnowMap);
                }
            }
        }
        //循环小题知识点
        for (Long thisSubQuesModuleId : questionModules) {

            //正确次数：按该知识点分布在所有小题中的比例计算。 同时有该知识点小题数量
            List<StudentWorkAnswer> repeatSubquesOfModule = getSubsQuesOfLevelId(StudentWork.WorkLevel.MODULE,
                    thisSubQuesModuleId, subQuesAnswers);

            Double topicRightNumber = StuAnswerUtil.getRightNumberCs(structId, result, blankResult, answerNum,
                    repeatSubquesOfModule.size());

            if (topicRightNumber != 0) {
                DecimalFormat df = new DecimalFormat("######0.00");
                String topicRightNumberStr = df.format(topicRightNumber);
                addToAnswAndResultSatises(resourceType, StudentWork.WorkLevel.MODULE, thisSubQuesModuleId, new Double
                        (topicRightNumberStr), 0, answAndResultSatises, allKnowMap);
            }
        }

    }


    private Map<String, SimpleKnowVo> convertAllKnowMap(Map<Long, SimpleKnowVo> topicKnowMap) {
        Map<String, SimpleKnowVo> map = new HashMap<>();
        Collection<SimpleKnowVo> values = topicKnowMap.values();
        for (SimpleKnowVo vo : values) {
            map.put("u" + vo.getUnitId(), vo);
            map.put("m" + vo.getModuleId(), vo);
            map.put("t" + vo.getTopicId(), vo);
        }
        return map;
    }
}
