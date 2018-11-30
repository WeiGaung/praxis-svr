package com.noriental.praxissvr.statis.util;

import java.lang.invoke.MethodHandles;
import java.util.*;

import com.noriental.praxissvr.wrong.bean.StuQuesKnowledge;
import com.noriental.praxissvr.answer.bean.StudentExercise;
import com.noriental.praxissvr.question.bean.QuesitonRecommend;
import com.noriental.praxissvr.statis.bean.StuWrongQuesStatis;
import com.noriental.praxissvr.brush.bean.StudentWork;
import com.noriental.praxissvr.common.StuAnswerConstant;
import com.noriental.praxissvr.answer.util.StuAnswerUtil;
import com.noriental.praxissvr.wrongQuestion.util.WrongQuestionUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

public class StuStatisUtil {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static List<StudentExercise> getLatestStuQues1(List<StudentExercise> studentExercises) {

        List<StudentExercise> sesLatest = new ArrayList<>();

        //单题id
        List<Long> sigleIds = new ArrayList<>();
        //复合题id
        List<Long> complexIds = new ArrayList<>();

        for (StudentExercise se : studentExercises) {
            Long quesId = se.getQuestionId();
            Long parentQuesId = se.getParentQuestionId();
            if (parentQuesId == null) {
                sigleIds.remove(quesId);
                sigleIds.add(quesId);
            } else {
                complexIds.remove(parentQuesId);
                complexIds.add(parentQuesId);
            }
        }

        //找单题id最近操作过的一条记录
        for (Long sigleId : sigleIds) {
            StudentExercise se = getLatestSigleExiercise(sigleId, studentExercises);
            if (se != null) {
                sesLatest.add(se);
            }
        }
        //找复合题id最近操作过的一批记录
        for (Long complexId : complexIds) {
            List<StudentExercise> ses = getLatestComplexExiercise(complexId, studentExercises);

            sesLatest.addAll(ses);
        }

        return sesLatest;
    }

    /**
     * 获得复合题最新的一次做错记录
     *
     * @param studentExercises
     * @return
     */
    public static List<StudentExercise> getLatestComplexExiercise(Long complexId, List<StudentExercise>
            studentExercises) {
        List<StudentExercise> studeExercisesR = new ArrayList<>();

        //找到最近操作的一条错误记录
        //找到批次号，把该批次的全部取出来
        StudentExercise se0 = null;
        Date lastModifyTime0 = null;

        for (StudentExercise se : studentExercises) {
            Long parentQuesId = se.getParentQuestionId();
            String result = se.getResult();
            Integer structId = se.getStructId();

            Date lastModifyTime = getLastModifyTime(se);

            if (complexId.equals(parentQuesId) && StuAnswerUtil.isWrong(structId, result)) {
                //第一次
                if (lastModifyTime0 == null) {
                    lastModifyTime0 = lastModifyTime;
                    se0 = se;
                    //如果更新
                } else if (lastModifyTime.compareTo(lastModifyTime0) >= 0) {
                    lastModifyTime0 = lastModifyTime;
                    se0 = se;
                }
            }
        }

        if (se0 != null) {
            Long resourceId0 = se0.getResourceId();
            String exerciseSource0 = se0.getExerciseSource();


            for (StudentExercise se : studentExercises) {
                Long parentQuesId = se.getParentQuestionId();
                Long resourceId = se.getResourceId();
                String exerciseSource = se.getExerciseSource();
                //找出同一组的
                if (complexId.equals(parentQuesId) && exerciseSource0.equals(exerciseSource)) {
                    if (resourceId0 != null && resourceId != null && resourceId0.equals(resourceId)) {
                        studeExercisesR.add(se);
                    }
                }
            }

        }


        return studeExercisesR;
    }

    /**
     * 获得单题最新的做错记录
     *
     * @param sigleId
     * @param studentExercises
     * @return
     */
    public static StudentExercise getLatestSigleExiercise(Long sigleId, List<StudentExercise> studentExercises) {
        StudentExercise se0 = null;
        Date lastModifyTime0 = null;
        for (StudentExercise se : studentExercises) {
            Long quesId = se.getQuestionId();
            String result = se.getResult();
            Integer structId = se.getStructId();

            Date lastModifyTime = getLastModifyTime(se);

            if (sigleId.equals(quesId) && StuAnswerUtil.isWrong(structId, result)) {
                //第一次
                if (lastModifyTime0 == null) {
                    lastModifyTime0 = lastModifyTime;
                    se0 = se;
                    //如果更新
                } else if (lastModifyTime.compareTo(lastModifyTime0) >= 0) {
                    lastModifyTime0 = lastModifyTime;
                    se0 = se;
                }
            }
        }
        return se0;
    }

    public static Date getLastModifyTime(StudentExercise se) {
        Date createT = se.getCreateTime();
        Date submitT = se.getSubmitTime();
        Date correctT = se.getCorrectorTime();
        Date lastModifyTime = createT;
        if (submitT != null) {
            lastModifyTime = submitT;
        }
        if (correctT != null) {
            lastModifyTime = correctT;
        }
        return lastModifyTime;
    }

    /**
     * 得到每个级别知识点做错的错题
     *
     * @param stuQuesKnowlegeList
     * @return
     */
    public static List<StuWrongQuesStatis> getKnowledgeWrongQueses(List<StuQuesKnowledge> stuQuesKnowlegeList) {
        List<StuWrongQuesStatis> knlowdgeStatis = new ArrayList<>();

        for (StuQuesKnowledge stuQuesKnowledge : stuQuesKnowlegeList) {
            Long topicId = stuQuesKnowledge.getTopicId();
            Long unitId = stuQuesKnowledge.getUnitId();
            Long moduleId = stuQuesKnowledge.getModuleId();

            if (topicId != null) {
                addToStatis(knlowdgeStatis, stuQuesKnowledge, StudentWork.WorkLevel.TOPIC);
            }

            if (unitId != null) {
                addToStatis(knlowdgeStatis, stuQuesKnowledge, StudentWork.WorkLevel.UNIT);
            }

            if (moduleId != null) {
                addToStatis(knlowdgeStatis, stuQuesKnowledge, StudentWork.WorkLevel.MODULE);
            }
        }
        return knlowdgeStatis;
    }

    /**
     * 填加到统计（大题个数累加，小题个数累加）
     *
     * @param knlowdgeStatis
     * @param stuQuesKnowledge
     * @param level
     */
    private static void addToStatis(List<StuWrongQuesStatis> knlowdgeStatis, StuQuesKnowledge stuQuesKnowledge,
                                    Integer level) {

        StuWrongQuesStatis existedStatis = getExistStatis(stuQuesKnowledge, knlowdgeStatis, level);

        Long questionId = stuQuesKnowledge.getQuestionId();
        Integer subQuesCount = stuQuesKnowledge.getQuestionCount();
        if (existedStatis == null) {

            Long topicId = stuQuesKnowledge.getTopicId();
            Long unitId = stuQuesKnowledge.getUnitId();
            Long moduleId = stuQuesKnowledge.getModuleId();
            Long studentId = stuQuesKnowledge.getStudentId();
            Long subjectId = stuQuesKnowledge.getSubjectId();

            StuWrongQuesStatis newStatis = new StuWrongQuesStatis();
            newStatis.setStudentId(studentId);
            newStatis.setSubjectId(subjectId);
            newStatis.setModuleId(moduleId);
            newStatis.setUnitId(unitId);
            newStatis.setTopicId(topicId);
            newStatis.setLevel(level);
            newStatis.setSubQuesCount(subQuesCount);

            List<QuesitonRecommend> quesitonRecommends = new ArrayList<>();
            QuesitonRecommend quesitonRecommend = new QuesitonRecommend();
            quesitonRecommend.setQuestionId(questionId);
            quesitonRecommends.add(quesitonRecommend);

            newStatis.setQuesitonRecommends(quesitonRecommends);

            knlowdgeStatis.add(newStatis);
        } else {
            QuesitonRecommend quesitonRecommend = new QuesitonRecommend();
            quesitonRecommend.setQuestionId(questionId);

            List<QuesitonRecommend> existedQuestionList = existedStatis.getQuesitonRecommends();
            if (!existedQuestionList.remove(quesitonRecommend)) {//去重大题id，如果不包含大题统计小题个数
                existedStatis.setSubQuesCount(existedStatis.getSubQuesCount() + subQuesCount);
            }
            existedQuestionList.add(quesitonRecommend);
        }
    }

    /**
     * 获得该错题对应的统计信息
     *
     * @param stuQuesKnowledge 错题
     * @param wrongStatis      统计信息
     * @param level            级别
     * @return null没有被统计  有值：被统计，返回统计记录
     */
    public static StuWrongQuesStatis getExistStatis(StuQuesKnowledge stuQuesKnowledge, List<StuWrongQuesStatis>
            wrongStatis, Integer level) {
        Long topicId = stuQuesKnowledge.getTopicId();
        Long unitId = stuQuesKnowledge.getUnitId();
        Long moduleId = stuQuesKnowledge.getModuleId();

        for (StuWrongQuesStatis addedStatis : wrongStatis) {
            Integer addedLevel = addedStatis.getLevel();
            Long addedTopicId = addedStatis.getTopicId();
            Long addedUnitId = addedStatis.getUnitId();
            Long addedModuleId = addedStatis.getModuleId();
            if (level.equals(StudentWork.WorkLevel.TOPIC) && addedLevel.equals(StudentWork.WorkLevel.TOPIC) &&
                    addedTopicId.equals(topicId)) {
                return addedStatis;
            }
            if (level.equals(StudentWork.WorkLevel.UNIT) && addedLevel.equals(StudentWork.WorkLevel.UNIT) &&
                    addedUnitId.equals(unitId)) {
                return addedStatis;
            }
            if (level.equals(StudentWork.WorkLevel.MODULE) && addedLevel.equals(StudentWork.WorkLevel.MODULE) &&
                    addedModuleId.equals(moduleId)) {
                return addedStatis;
            }
        }

        return null;
    }

    /**
     * 对于因为垃圾数据获得不到的题目，自己造一条未作答的题目:出现未作答的题目再查具体原因
     */
    public static void processWrongQuesAnswer(Long studentId, List<Long> targetQuesIds, List<StudentExercise>
            studentExercises) {

        List<Long> subQuesIdlistResult = new ArrayList<>();
        for (StudentExercise se : studentExercises) {
            Long quesId = se.getQuestionId();
            Long parentQuesId = se.getParentQuestionId();
            if (parentQuesId != null) {
                subQuesIdlistResult.add(parentQuesId);
            } else {
                subQuesIdlistResult.add(quesId);
            }
        }

        targetQuesIds.removeAll(subQuesIdlistResult);

        for (Long targetQuesId : targetQuesIds) {
            StudentExercise se = new StudentExercise();
            se.setQuestionId(targetQuesId);
            se.setStudentId(studentId);
            se.setResult(StuAnswerConstant.ExerciseResult.NOANSWER);
            se.setExerciseSource(StuAnswerConstant.ExerciseSource.WORK);
            se.setId(-1L);

            studentExercises.add(se);
        }
    }

    /**
     * 在studentExercisesAll获得和se同一个学生同一个题目的错题或者未作答记录
     *
     * @param se
     * @param studentExercisesAll
     * @return
     */
    public static List<StudentExercise> getHistory(StudentExercise se, List<StudentExercise> studentExercisesAll) {
        List<StudentExercise> history = new ArrayList<>();
        for (StudentExercise seH : studentExercisesAll) {
            Long studentId = se.getStudentId();
            Long questionId = se.getQuestionId();
            Long seHQuestionId = seH.getQuestionId();
            Long seHStudentId = seH.getStudentId();
            if (studentId.equals(seHStudentId) && questionId.equals(seHQuestionId)) {
                String result = seH.getResult();
                Integer structId = seH.getStructId();
                if (result != null && StuAnswerUtil.isWrong(structId, result)) {
                    StudentExercise seNew = new StudentExercise();
                    BeanUtils.copyProperties(seH, seNew);
                    history.add(seNew);
                }
            }
        }
        if (CollectionUtils.isNotEmpty(history)){
            Collections.sort(history,new StudentExerciseComparator());
        }
        return history;
    }

    public static StudentExercise getLatestWrongStuExe(Long quesId, List<StudentExercise> seList) {
        //找到最近操作的一条错误记录
        //找到批次号，把该批次的全部取出来
        StudentExercise seLatest = null;
        Date lastModifyTimeLatest = null;
        Date currentModifyTimeLatest=null;
        StudentExercise currentSe = null;
        for (StudentExercise se : seList) {
            String result = se.getResult();
            Integer structId = se.getStructId();
            Date lastModifyTime = se.getSubmitTime()==null?se.getLastUpdateTime():se.getSubmitTime();

            if ((quesId.equals(se.getParentQuestionId()) || quesId.equals(se.getQuestionId())) && StuAnswerUtil
                    .isWrong(structId, result)) {
                if (lastModifyTimeLatest == null) {
                    lastModifyTimeLatest = lastModifyTime;
                    seLatest = se;
                } else if (lastModifyTime.compareTo(lastModifyTimeLatest) >= 0) {
                    lastModifyTimeLatest = lastModifyTime;
                    seLatest = se;
                }
            }
            if ((quesId.equals(se.getParentQuestionId()) || quesId.equals(se.getQuestionId())) && !StuAnswerUtil
                    .isWrong(structId, result)) {
                if (currentModifyTimeLatest == null) {
                    currentModifyTimeLatest = lastModifyTime;
                    currentSe = se;
                } else if (lastModifyTime.compareTo(currentModifyTimeLatest) >= 0) {
                    currentModifyTimeLatest = lastModifyTime;
                    currentSe = se;
                }

            }

        }
        if (null == seLatest) {
            seLatest = currentSe;
        }
        return seLatest;

    }


    public static List<StudentExercise> getAllStuExesOfQues(StudentExercise latestWrongStuExe, List<StudentExercise>
            seList) {
        List<StudentExercise> allStuExesOfQues = new ArrayList<>();
        if (latestWrongStuExe == null) {
            return allStuExesOfQues;
        }
        if (latestWrongStuExe.getParentQuestionId() != null) {
            for (StudentExercise se : seList) {
                if (isSameBatchSameQues(se, latestWrongStuExe)) {
                    allStuExesOfQues.add(se);
                }
            }
        } else {
            allStuExesOfQues.add(latestWrongStuExe);
        }
        return allStuExesOfQues;
    }

    private static boolean isSameBatchSameQues(StudentExercise se, StudentExercise se1) {
        try {
            if (se == null || se1 == null) {
                return false;
            }
            if (se.getParentQuestionId().equals(se1.getParentQuestionId()) && (se.getExerciseSource().equals
                    (se1.getExerciseSource())) && se.getResourceId().equals(se1.getResourceId())) {

                if (se.getExerciseSource().equals(StuAnswerConstant.ExerciseSource.WRONGBOOK)) {
                    if (se.getRedoSource().equals(se1.getRedoSource())) {
                        return true;
                    }
                } else {
                    return true;
                }
            }
        } catch (Exception e) {
            //logger.error("se:{}, se1:{}", se, se1);
            return false;
        }
        return false;
    }

    static class StudentExerciseComparator implements Comparator {
        @Override
        public int compare(Object object1, Object object2) {
            // 强制转换
            StudentExercise p1 = (StudentExercise) object1;
            StudentExercise p2 = (StudentExercise) object2;
            if (null!=p1.getSubmitTime()&&null!=p2.getSubmitTime()){
                return p2.getSubmitTime().compareTo(p1.getSubmitTime());
            }else if(null!=p1.getCreateTime()&&null!=p2.getCreateTime()){
                return p2.getCreateTime().compareTo(p1.getCreateTime());
            }else{
               return 0;
            }
        }
    }

}
