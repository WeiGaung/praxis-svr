package com.noriental.praxissvr.answer.util;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.noriental.global.dict.AppType;
import com.noriental.lessonsvr.entity.request.LinkResourceWithPublishInfo;
import com.noriental.lessonsvr.entity.request.ResourceParam;
import com.noriental.lessonsvr.rservice.LessonService;
import com.noriental.praxissvr.answer.bean.FlowTurn;
import com.noriental.praxissvr.answer.bean.FlowTurnList;
import com.noriental.praxissvr.answer.bean.StudentExercise;
import com.noriental.praxissvr.answer.bean.UpdateSubmitAnswerVo;
import com.noriental.praxissvr.answer.request.FlowTurnCorrectRequest;
import com.noriental.praxissvr.brush.bean.StudentWork;
import com.noriental.praxissvr.brush.bean.StudentWorkAnswer;
import com.noriental.praxissvr.brush.dao.StudentWorkDao;
import com.noriental.praxissvr.common.QuestionState;
import com.noriental.praxissvr.common.QuestionTypeEnum;
import com.noriental.praxissvr.common.StuAnswerConstant;
import com.noriental.praxissvr.exception.PraxisErrorCode;
import com.noriental.praxissvr.question.bean.Question;
import com.noriental.praxissvr.question.request.FindLeafQuesIdsByParentIdsRequest;
import com.noriental.praxissvr.question.request.FindQuestionsByIdsRequest;
import com.noriental.praxissvr.question.request.FindQuestionsRequest;
import com.noriental.praxissvr.question.response.FindParentIdLeafQuesIdsMapResponse;
import com.noriental.praxissvr.question.response.FindQuestionsByIdsResponse;
import com.noriental.praxissvr.question.response.FindQuestionsResponse;
import com.noriental.praxissvr.question.service.QuestionService;
import com.noriental.praxissvr.questionSearch.service.QuestionSearchService;
import com.noriental.praxissvr.utils.PraxisUtilForOther;
import com.noriental.praxissvr.wrong.util.WrongUtil;
import com.noriental.solr.bean.doc.QuestionDocument;
import com.noriental.trailsvr.bean.TrailCount;
import com.noriental.trailsvr.bean.TrailCountServiceRequest;
import com.noriental.trailsvr.service.TrailCountService;
import com.noriental.utils.entity.EntityUtils;
import com.noriental.utils.json.JsonUtil;
import com.noriental.utils.redis.RedisUtil;
import com.noriental.validate.bean.CommonResponse;
import com.noriental.validate.exception.BizLayerException;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.noriental.praxissvr.exception.PraxisErrorCode.*;

/**
 * 工具类
 *
 * @author sheng.xiao
 *         2015年12月21日
 */
public class StuAnswerUtil {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    //知识点字符串全匹配
    public static final String SOLR_GET_QUESTION_TYPE_EQUAL = "EQUAL";
    //知识点多值匹配
    public static final String SOLR_GET_QUESTION_TYPE_MULTI = "MULTI";
    public static final String CORRECTOR_ROLE = "corrector_role";
    public static final String BLANK_INDEX = "index";
    public static final String BLANK_RESULT = "result";
    public static final String BLANK_ROLE = "corrector_role";
    public static final String CORRECTOR_ROLE_AI = "ai";
    public static final String CORRECTOR_ROLE_TEACHER = "teacher";
    public static final String SHARD_KEY = "_shardKeyStore_";
    public static final int SHARD_KEY_EXPIRE = 3600 * 2;
    public static final String STUDENT_EXERCISE_REDIS_KEY_PREFIXX = "stuexe_";
    public static final String STUDENT_EXERCISE_REDIS_KEY_SPLIT = "_";
    public static final int REDIS_EXPIRE_SECONDS = 2 * 60 * 60;
    public static final int REDIS_EXPIRE_FOR_QUESTION = 2 * 60 * 60;
    public static final int HOURS_INTERVAL = 2;
    public static final String FLOW_TURN_REDIS_KEY_PREFIXX = "flow_";
    public static final String FLOW_TURN_STU_REDIS_KEY_PREFIXX = "flowstu_";
    public static final String FLOW_TURN_REDIS_KEY_SPLIT = "_";
    public static final String ANSWER_PUBLIC_TIME_REDIS_KEY_PREFIXX = "answer_public_time_";
    public static final int ANSWER_PUBLIC_EXPIRE_TIME = 60*60*24*30;
    //题库对接上线时间 2018-09-03 00:00:00
    //public static final int ANSWER_ONLINE_TIME = 1535971618;

    /**
     * 进入错题本和错题挑战的条件是否成立
     *
     * @param se
     * @return
     */
    public static boolean isWrong(StudentExercise se) {
        String result = se.getResult();
        Integer structId = se.getStructId();
        return isWrong(structId, result);
    }

    /**
     * 根据题型结构和批改结果判断是否为错题
     *
     * @param structId
     * @param result
     * @return
     */
    public static boolean isWrong(Integer structId, String result) {
        return PraxisUtilForOther.isWrong(PraxisUtilForOther.getQuesResultWrongBook(structId, result));
    }

    public static boolean isAbsolutelyWrong(Integer structId, String result) {
        return PraxisUtilForOther.isAbsolutelyWrong(PraxisUtilForOther.getQuesResultWrongBook(structId, result));
    }


    public static List<StudentExercise> getWrongList(List<StudentExercise> seList) {
        List<StudentExercise> wrongList = new ArrayList<>();
        for (StudentExercise se : seList) {
            if (isWrong(se)) {
                wrongList.add(se);
            }
        }
        return wrongList;
    }

    /**
     * @param structId
     * @param result
     * @return 仅有 1,2,6 @See ExerciseResult
     */
    public static String getExerciseResult(Integer structId, String result) {
        return PraxisUtilForOther.getQuesResultWrongBook(structId, result);
    }


    /**
     * 是否已作答
     * 根据填空题每个空的result获得整个题目的result。
     * 全部未作答才为未作答，否则已作答
     *
     * @return true 作答
     */
    public static boolean isAnswered(Integer structId, String result) {
        try {
            List<String> resultsThr = PraxisUtilForOther.getResultsThr(structId, result);
            if (resultsThr == null || resultsThr.size() == 0) {
                return false;
            }

            for (String s : resultsThr) {
                if (!StuAnswerConstant.ExerciseResult.NOANSWER.equals(s)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            logger.error("转换异常", e);
            return false;
        }
    }


    /**
     * 获得填空题的正确率
     *
     * @param blankResult
     * @return
     */
    public static Double getBlankRightNumber(String blankResult) {
        if (StringUtils.isBlank(blankResult)) {
            return 0d;
        }
        List<Map<String, Object>> resultList = JsonUtil.readValue(blankResult, new TypeReference<List<Map<String,
                Object>>>() {
        });

        int rightNumber = 0;
        int allNumber = resultList.size();
        if (allNumber == 0) {
            return 0d;
        }
        for (Map<String, Object> map : resultList) {
            Object subBlankResult = map.get("result");

            if (subBlankResult != null && subBlankResult.toString().equals(StuAnswerConstant.ExerciseResult.RIGHT)) {
                rightNumber++;
            }
        }
        Double subQuesRightPercent = (double) rightNumber / (double) allNumber;
        DecimalFormat df = new DecimalFormat("######0.00");
        String subQuesRightPercentStr = df.format(subQuesRightPercent);
        subQuesRightPercent = new Double(subQuesRightPercentStr);
        return subQuesRightPercent;
    }

    /**
     * 获得填空题的正确率
     *
     * @param blankResult
     * @return
     */
    public static Double getBlankRightNumber(String blankResult, Integer answerNum) {
        if (StringUtils.isBlank(blankResult)) {
            return 0d;
        }
        List<Map<String, Object>> resultList = JsonUtil.readValue(blankResult, new TypeReference<List<Map<String,
                Object>>>() {
        });

        int rightNumber = 0;
        if (answerNum == null || answerNum == 0) {
            logger.warn("填空题空个数不合法！");
            return 0d;
        }
        int allNumber = resultList.size();
        if (allNumber == 0) {
            return 0d;
        }
        for (Map<String, Object> map : resultList) {
            Object subBlankResult = map.get("result");

            if (subBlankResult != null && subBlankResult.toString().equals(StuAnswerConstant.ExerciseResult.RIGHT)) {
                rightNumber++;
            }
        }
        Double subQuesRightPercent = (double) rightNumber / Double.valueOf(answerNum);
        DecimalFormat df = new DecimalFormat("######0.00");
        String subQuesRightPercentStr = df.format(subQuesRightPercent);
        subQuesRightPercent = new Double(subQuesRightPercentStr);
        return subQuesRightPercent;
    }

    /**
     * 增加或者合并后批改角色
     *
     * @param oldBlankCorrectorRole 已有的填空题批改角色
     * @param blankResults          填空题若干空的批改结果
     * @param correctorRole         当前的批改角色
     * @return 增加或者合并后的批改角色
     * @author sx.xiao  @date 2015年11月20日 上午11:24:37
     */
    public static String joinBlankCorrectorRole(String oldBlankCorrectorRole, String blankResults, String
            correctorRole) {
        List<Map<String, Object>> oldblankCorrectorRoleList = JsonUtil.readValue(oldBlankCorrectorRole, new
                TypeReference<List<Map<String, Object>>>() {
        });
        List<Map<String, Object>> blankResultList = JsonUtil.readValue(blankResults, new
                TypeReference<List<Map<String, Object>>>() {
        });


        for (Map<String, Object> blankResultMap : blankResultList) {
            Object singleBlankResultIndex = blankResultMap.get(BLANK_INDEX);

            //从来没有批改过，直接新增
            if (oldblankCorrectorRoleList == null) {
                oldblankCorrectorRoleList = new ArrayList<>();
                Map<String, Object> newRoleMap = new HashMap<>(1);
                newRoleMap.put(BLANK_INDEX, singleBlankResultIndex);
                newRoleMap.put(CORRECTOR_ROLE, correctorRole);
                oldblankCorrectorRoleList.add(newRoleMap);
            }

            //找出教师批改的那个空
            int tagetIndex = -1;
            for (int j = 0; j < oldblankCorrectorRoleList.size(); j++) {
                Map<String, Object> oldMap = oldblankCorrectorRoleList.get(j);
                Object oldIndex = oldMap.get(BLANK_INDEX);
                if (singleBlankResultIndex.toString().equals(oldIndex.toString())) {
                    tagetIndex = j;
                }
            }

            //替换掉原来的批改角色
            if (tagetIndex > -1) {
                Map<String, Object> tagetMap = oldblankCorrectorRoleList.get(tagetIndex);
                tagetMap.put(CORRECTOR_ROLE, correctorRole);
                //新增角色
            } else {
                Map<String, Object> newRoleMap = new HashMap<>(1);
                newRoleMap.put(BLANK_INDEX, singleBlankResultIndex);
                newRoleMap.put(CORRECTOR_ROLE, correctorRole);
                oldblankCorrectorRoleList.add(newRoleMap);
            }

        }
        return JsonUtil.obj2Json(oldblankCorrectorRoleList);
    }

    /**
     * 传入的填空题批改结果同已有的批改结果组合： 已有批改结果替换，未有新增
     *
     * @param newBlankResults 新批改的若干空答案
     * @return 组合的后的restul
     * @author sx.xiao  @date 2015年11月9日 上午9:46:56
     */
    public static String joinBlankResult(String oldBlankResults, String newBlankResults) {
        List<Map<String, Object>> newBlankResultList = JsonUtil.readValue(newBlankResults, new
                TypeReference<List<Map<String, Object>>>() {
        });
        List<Map<String, Object>> oldBlankResultList;
        if (StringUtils.isBlank(oldBlankResults)) {
            oldBlankResultList = new ArrayList<>();
        } else {
            oldBlankResultList = JsonUtil.readValue(oldBlankResults, new TypeReference<List<Map<String, Object>>>() {
            });
        }

        for (Map<String, Object> newBlankResultMap : newBlankResultList) {
            //找出教师批改的那个空
            int arrayIndex = -1;
            for (int j = 0; j < oldBlankResultList.size(); j++) {
                Map<String, Object> oldMap = oldBlankResultList.get(j);
                Object teacherIndex = newBlankResultMap.get(BLANK_INDEX);
                Object oldIndex = oldMap.get(BLANK_INDEX);
                if (teacherIndex.toString().equals(oldIndex.toString())) {
                    arrayIndex = j;
                }
            }

            //如果已有，替换掉原来的
            if (arrayIndex > -1) {
                oldBlankResultList.remove(arrayIndex);
            }
            //增加新的空的批改结果
            oldBlankResultList.add(newBlankResultMap);
        }
        return JsonUtil.obj2Json(oldBlankResultList);
    }

    /**
     * 合并批注
     */
    public static String joinBlankPostil(String oldPostils, String newPostils) {
        if (StringUtils.isBlank(newPostils)) {
            return oldPostils;
        }
        List<Map<String, Object>> newPostilsList = JsonUtil.readValue(newPostils, new TypeReference<List<Map<String,
                Object>>>() {
        });
        List<Map<String, Object>> oldPostilsList;
        if (StringUtils.isBlank(oldPostils)) {
            oldPostilsList = new ArrayList<>();
        } else {
            oldPostilsList = JsonUtil.readValue(oldPostils, new TypeReference<List<Map<String, Object>>>() {
            });
        }

        for (Map<String, Object> newBlankResultMap : newPostilsList) {
            //找出教师批注的那个空
            int arrayIndex = -1;
            for (int j = 0; j < oldPostilsList.size(); j++) {
                Map<String, Object> oldMap = oldPostilsList.get(j);
                Object teacherIndex = newBlankResultMap.get(BLANK_INDEX);
                Object oldIndex = oldMap.get(BLANK_INDEX);
                if (teacherIndex.toString().equals(oldIndex.toString())) {
                    arrayIndex = j;
                }
            }

            //如果已有，替换掉原来的
            if (arrayIndex > -1) {
                oldPostilsList.remove(arrayIndex);
            }
            //增加新的批注
            oldPostilsList.add(newBlankResultMap);
        }
        return JsonUtil.obj2Json(oldPostilsList);
    }


    /**
     * 获得填空题空的批改角色：如果一个空被老师批改过，返回老师批改。没有空被老师批改过的前提下，有一个空被学生批改过，则显示学生批改。否则，没有人批改过
     *
     * @param singleBlankResult  若干个空的result  [{"index": 1,"result": "1"},{"index": 2,"result": "1"}]
     * @param blankCorrectorRole 所有空的批改角色  [ {    "index": 1,    "corrector_role": "teacher"  }, {    "index": 2,
     *                           "corrector_role": "student"  }, {    "index": 3,    "corrector_role": null  },  ...]
     * @return AppType.CorrectorRole.STUDENT学生批改，AppType.CorrectorRole.TEACHER教师批改过  ，null：没有被批改
     * @author sx.xiao  @date 2015年11月20日 上午10:32:40
     */
    public static String getTktblankCorrectorRole(String singleBlankResult, String blankCorrectorRole) {
        //没有批改角色
        if (StringUtils.isBlank(blankCorrectorRole)) {
            return null;
        }
        List<Map<String, Object>> singleBlankResultList = JsonUtil.readValue(singleBlankResult, new
                TypeReference<List<Map<String, Object>>>() {
        });
        List<Map<String, Object>> blankCorrectorRoleList = JsonUtil.readValue(blankCorrectorRole, new
                TypeReference<List<Map<String, Object>>>() {
        });
        String oldCorrectorRole;
        boolean isStudentCorrect = false;
        if (singleBlankResultList != null && singleBlankResultList.size() > 0) {
            //遍历批改结果
            for (Map<String, Object> singleBlankResultMap : singleBlankResultList) {
                Object singleBlankResultIndexObj = singleBlankResultMap.get(BLANK_INDEX);
                if (null != singleBlankResultIndexObj) {
                    //查看该空的批改角色
                    for (Map<String, Object> map : blankCorrectorRoleList) {
                        Object eachIndex = map.get(BLANK_INDEX);
                        if (singleBlankResultIndexObj.toString().equals(eachIndex.toString())) {
                            Object correctorRole = map.get(CORRECTOR_ROLE);
                            oldCorrectorRole = correctorRole.toString();

                            //老师已经批改过
                            if (StringUtils.equals(oldCorrectorRole, AppType.CorrectorRole.TEACHER)) {
                                return AppType.CorrectorRole.TEACHER;
                            }
                            if (StringUtils.equals(oldCorrectorRole, AppType.CorrectorRole.STUDENT)) {
                                isStudentCorrect = true;
                            }
                        }
                    }
                }
            }
        }
        if (isStudentCorrect) {
            return AppType.CorrectorRole.STUDENT;
        }
        return null;
    }

    public static List<Question> convertToQuestion(List<QuestionDocument> list) {
        List<Question> datalist = new ArrayList<>();
        for (QuestionDocument qd : list) {
            try {
                Question question = EntityUtils.copyValueDeep2Object(qd, 1, Question.class, 1);
                datalist.add(question);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return datalist;
    }


    /**
     * 该状态是否合法的批改状态
     *
     * @param resultInDb
     * @return
     */
    public static boolean isCorrectedResult(String resultInDb) {
        return StringUtils.equals(resultInDb, StuAnswerConstant.ExerciseResult.RIGHT) || StringUtils.equals
                (resultInDb, StuAnswerConstant.ExerciseResult.ERROR) || StringUtils.equals(resultInDb,
                StuAnswerConstant.ExerciseResult.HALFRIGHT) || StringUtils.equals(resultInDb, StuAnswerConstant
                .ExerciseResult.NOANSWER);
    }


    public static String getRedisKeyOfStudentExercise(StudentExercise studentExercise) {
        Long studentId = studentExercise.getStudentId();
        String studentIdStr = String.valueOf(studentId);
        if (studentId == null) {
            studentIdStr = "*";
        }

        Long questionId = studentExercise.getQuestionId();
        String questionIdStr = String.valueOf(questionId);
        if (questionId == null) {
            questionIdStr = "*";
        }
        //stuexe_6_2300168_81951145553_2575781
        return STUDENT_EXERCISE_REDIS_KEY_PREFIXX + studentExercise.getExerciseSource() +
                STUDENT_EXERCISE_REDIS_KEY_SPLIT + studentExercise.getResourceId() + STUDENT_EXERCISE_REDIS_KEY_SPLIT
                + studentIdStr + STUDENT_EXERCISE_REDIS_KEY_SPLIT + questionIdStr;
    }


    public static void joinPostil(StudentExercise se, String postilTeacherExist) {
        Integer structId = se.getStructId();
        String postilTeacherAdd = se.getPostilTeacher();
        if (StuAnswerConstant.StructType.STRUCT_TKT.equals(structId)) {
            se.setPostilTeacher(joinBlankPostil(postilTeacherExist, postilTeacherAdd));
        } else if (StuAnswerConstant.StructType.STRUCT_JDT.equals(structId) || StuAnswerConstant.StructType
                .STRUCT_PZT.equals(structId)) {
            se.setPostilTeacher(joinJdtPostil(postilTeacherExist, postilTeacherAdd));
        } else {
            se.setPostilTeacher(postilTeacherAdd);
        }
    }

    private static String joinJdtPostil(String postilTeacherExist, String postilTeacherAdd) {
        if (StringUtils.isBlank(postilTeacherExist)) {
            return postilTeacherAdd;
        }
        if (StringUtils.isBlank(postilTeacherAdd)) {
            return postilTeacherExist;
        }
        List<String> existList = JsonUtil.readValue(postilTeacherExist, List.class);
        if (CollectionUtils.isEmpty(existList)) {
            logger.error("" + postilTeacherExist);
            return postilTeacherAdd;
        }
        List<String> addList = JsonUtil.readValue(postilTeacherAdd, List.class);
        for (int i = 0; i < existList.size(); i++) {
            if (StringUtils.isNotBlank(addList.get(i))) {
                existList.set(i, addList.get(i));
            }
        }
        return JsonUtil.obj2Json(existList);
    }

    public static void checkCorrectResult(Integer structId, String result) {
        List<String> results = PraxisUtilForOther.getResultsThr(structId, result);
        for (String s : results) {
            if (!StuAnswerUtil.isCorrectedResult(s)) {
                throw new BizLayerException("[答题状态,result]", ANSWER_PARAMETER_INVALID_RESULT_VALUE);
            }
        }
    }

    public static void checkCorrectRole(StudentExercise seNew, StudentExercise seOld) {
        String result = seNew.getResult();
        String newCorrectorRole = seNew.getCorrectorRole();
        String oldCorrectorRole = seOld.getCorrectorRole();
        if (StuAnswerConstant.StructType.STRUCT_TKT.equals(seOld.getStructId())) {
            oldCorrectorRole = StuAnswerUtil.getTktblankCorrectorRole(result, oldCorrectorRole);
        }
        //老师批改过学生不能再批改
        if (StringUtils.equals(oldCorrectorRole, AppType.CorrectorRole.TEACHER) && StringUtils.equals
                (newCorrectorRole, AppType.CorrectorRole.STUDENT)) {
            //老师已经批改过
            throw new BizLayerException("", ANSWER_CORRECTED_BY_TEACHER);
        }
        //设置学生批改过不能再批改
        if (StringUtils.equals(newCorrectorRole, AppType.CorrectorRole.STUDENT) && StringUtils.equals
                (oldCorrectorRole, AppType.CorrectorRole.STUDENT)) {
            //学生已经批改过
            throw new BizLayerException("", ANSWER_CORRECTED_BY_STUDENT);
        }
    }

    public static void joinBlankResultAndRole(StudentExercise existedRecord, StudentExercise seInput) {
        String resultInput = seInput.getResult();
        String correctorRoleInput = seInput.getCorrectorRole();
        String correctorRoleDb = existedRecord.getCorrectorRole();
        String resultDb = existedRecord.getResult();
        if (StuAnswerConstant.StructType.STRUCT_TKT.equals(existedRecord.getStructId())) {
            String newBlankCorrectorRole = StuAnswerUtil.joinBlankCorrectorRole(correctorRoleDb, resultInput,
                    correctorRoleInput);
            String newBlankResults = StuAnswerUtil.joinBlankResult(resultDb, resultInput);
            seInput.setResult(newBlankResults);
            seInput.setCorrectorRole(newBlankCorrectorRole);
        } else {
            seInput.setCorrectorRole(seInput.getCorrectorRole());
            seInput.setResult(seInput.getResult());
        }
    }


    public static void checkPostilLegal(Integer structId, String postilTeacher) {
        boolean isLegal = true;
        try {
            if (StuAnswerConstant.StructType.STRUCT_TKT.equals(structId)) {
                List<Map<String, Object>> postilTeacherList = JsonUtil.readValue(postilTeacher, new
                        TypeReference<List<Map<String, Object>>>() {
                });
                if (CollectionUtils.isEmpty(postilTeacherList)) {
                    isLegal = false;
                }
            } else if (StuAnswerConstant.StructType.STRUCT_JDT.equals(structId) || StuAnswerConstant.StructType
                    .STRUCT_PZT.equals(structId)) {
                List<String> postilTeachers = JsonUtil.readValue(postilTeacher, List.class);
                if (CollectionUtils.isEmpty(postilTeachers)) {
                    isLegal = false;
                }
            }
        } catch (Exception e) {
            logger.error("格式异常", e);
            isLegal = false;
        }
        if (!isLegal) {
            throw new BizLayerException("批注:" + postilTeacher, ANSWER_PARAMETER_ILL);
        }
    }


    public static void checkRedoSource(String exerciseSource, String redoSource) {
        //消灭错题
        if (StringUtils.equals(exerciseSource, StuAnswerConstant.ExerciseSource.WRONGBOOK)) {
            if (StringUtils.isBlank(redoSource)) {
                throw new BizLayerException("redoSource", ANSWER_PARAMETER_NULL);
            }
        }
    }

    public static void checkSubmit(List<StudentExercise> seList) {
        boolean isSubmit = false;
        for (StudentExercise se : seList) {
            if (AnswerType.UPDATE == getAnswerType(se.getExerciseSource())) {
                if (StringUtils.isNotBlank(se.getResult())) {
                    isSubmit = true;
                    logger.warn("已提交记录:" + JsonUtil.obj2Json(se));
                    break;
                }
            }
        }
        if (isSubmit) {
            throw new BizLayerException("", ANSWER_SUBMITED);
        }
    }


    public static List<Long> getQuestionIdList(List<UpdateSubmitAnswerVo> updateSubmitAnswerList) {
        List<Long> ids = new ArrayList<>();
        for (UpdateSubmitAnswerVo updateSubmitAnswerVo : updateSubmitAnswerList) {
            ids.add(updateSubmitAnswerVo.getQuestionId());
        }
        return ids;
    }

    public static void setCorrectInfo(StudentExercise se, String correctorRole, Long correctorId) {
        if (isCorrectedResult(se.getResult())) {
            se.setCorrectorId(correctorId);
            se.setCorrectorTime(new Date());
            se.setCorrectorRole(correctorRole);
        }
    }

    public static void setRedoStatus(List<UpdateSubmitAnswerVo> list, String exerciseSource) {
        if (StringUtils.equals(exerciseSource, StuAnswerConstant.ExerciseSource.WRONGBOOK)) {
            for (UpdateSubmitAnswerVo vo : list) {
                vo.setRedoStatus(PraxisUtilForOther.getQuesResultWrongBook(vo.getStructId(), vo.getResult()));
            }
        }
    }

    public static void checkExist(String exerciseSource, List<StudentExercise> seList) {
        if (AnswerType.INSERT == getAnswerType(exerciseSource)) {
            if (CollectionUtils.isNotEmpty(seList)) {
                throw new BizLayerException("", ANSWER_RECORDE_EXIST);
            }
        } else {
            if (CollectionUtils.isEmpty(seList)) {
                throw new BizLayerException("", ANSWER_RECORD_NOT_FOUND);
            }
        }
    }

    public static boolean isComplexQues(Long parentQuestionId) {
        return parentQuestionId != null && !parentQuestionId.equals(0L);
    }

    public static void setCorrectInfo(List<UpdateSubmitAnswerVo> list, String correctorRole, Long correctorId) {
        for (UpdateSubmitAnswerVo vo : list) {
            if (isCorrectedResult(vo.getResult())) {
                vo.setCorrectorId(correctorId);
                vo.setCorrectorTime(new Date());
                vo.setCorrectorRole(correctorRole);
            }
        }
    }

    public static Integer getFromIndex(Integer fromIndex, int totalCount) {
        return fromIndex > (totalCount - 1) ? totalCount : fromIndex;
    }

    public static List<Long> getQuesIdListExceptLeaf(List<Question> questions) {
        List<Long> quesIds = new ArrayList<>();
        if (CollectionUtils.isEmpty(questions)) {
            return quesIds;
        }
        for (Question q : questions) {
            if (q.getParentQuestionId() > 0) {
                quesIds.add(q.getParentQuestionId());
            } else {
                quesIds.add(q.getId());
            }
        }
        quesIds = new ArrayList<>(new HashSet<>(quesIds));
        return quesIds;
    }

    public static Long getParentQuesId(QuestionSearchService questionSearchService, Long questionId) {
        List<Question> quesListByIds = getQuesListByIds(questionSearchService, Collections.singletonList(questionId));
        if (CollectionUtils.isEmpty(quesListByIds)) {
            throw new BizLayerException("", PraxisErrorCode.PRAXIS_QUESTION_NOT_FOUND);
        } else {
            Question question = quesListByIds.get(0);
            if (question.getParentQuestionId() > 0) {
                return question.getParentQuestionId();
            } else {
                return null;
            }
        }
    }

    public static boolean isRight(Integer structId, String result) {
        return StuAnswerConstant.ExerciseResult.RIGHT.equals(PraxisUtilForOther.getQuesResultWrongBook(structId,
                result));
    }

    public static void checkSubmitResult(List<UpdateSubmitAnswerVo> updateSubmitAnswerVoList) {
        if (CollectionUtils.isNotEmpty(updateSubmitAnswerVoList)) {
            for (UpdateSubmitAnswerVo vo : updateSubmitAnswerVoList) {
                checkSubmitResult(vo.getStructId(), vo.getResult());
            }
        }
    }

    private static void checkSubmitResult(Integer structId, String result) {

        List<String> resultsThr = PraxisUtilForOther.getResultsThr(structId, result);
        for (String r : resultsThr) {
            if (r == null || (StringUtils.isBlank(StuAnswerConstant.ExerciseResult.getExerciseResultNameByCode(r)))) {
                logger.error("不合法的：{}，{}", structId, result);
                throw new BizLayerException("result:" + result, ANSWER_PARAMETER_INVALID_RESULT_VALUE);
            }
        }

    }

    public static void sortListByTime(List<StudentExercise> allSes) {
        if (CollectionUtils.isNotEmpty(allSes)) {
            Collections.sort(allSes, TimeComparator);
        }
    }

    public static void sortListQuesId(List<Long> quesIds, List<StudentExercise> allSes) {
        //并且大于一个1个子题
        if (CollectionUtils.isNotEmpty(allSes) && quesIds.size() > 1) {
            QuesIdComparator quesIdComparator = new QuesIdComparator(quesIds);
            Collections.sort(allSes, quesIdComparator);
        }
    }


    /**
     * 题集类型
     */
    public enum QuizSetType {
        //通过班级发布类
        PUBLISH, //自主练习类
        OWNER
    }

    public static QuizSetType getQuizSetType(String source) {
        if (StuAnswerConstant.ExerciseSource.OWNER_SOURCES.contains(source)) {
            return QuizSetType.OWNER;
        } else {
            return QuizSetType.PUBLISH;
        }
    }

    /**
     * 作答时答题记录是否已存在数据库
     */
    public enum AnswerType {
        //插入类
        INSERT, //更新类
        UPDATE
    }

    public static AnswerType getAnswerType(String source) {
        //强化练习、消灭错题、自主练习
        if (StringUtils.equals(source, StuAnswerConstant.ExerciseSource.ENHANCE) || StringUtils.equals(source,
                StuAnswerConstant.ExerciseSource.WRONGBOOK) || StringUtils.equals(source, StuAnswerConstant
                .ExerciseSource.WORK)) {
            return AnswerType.UPDATE;
        } else {
            return AnswerType.INSERT;
        }
    }

    public static void checkRoleLegal(String correctorRole) {
        if (StringUtils.isBlank(StuAnswerConstant.CorrectorRole.getNameByCode(correctorRole))) {
            throw new BizLayerException("correctorRole", ANSWER_PARAMETER_ILL);
        }
    }

    public static boolean isWrongQuesChalRecorded(String exerciseSource) {
        return StringUtils.equals(exerciseSource, StuAnswerConstant.ExerciseSource.LESSON) || StringUtils.equals
                (exerciseSource, StuAnswerConstant.ExerciseSource.HO_WORK) || StringUtils.equals(exerciseSource,
                StuAnswerConstant.ExerciseSource.EVALUATION) || StringUtils.equals(exerciseSource, StuAnswerConstant
                .ExerciseSource.PREVIEW);
    }


    public static void checkPostilCount(Integer structId, String submitAnswer, String postilTeacher) {
        if (StuAnswerConstant.StructType.STRUCT_JDT.equals(structId) || StuAnswerConstant.StructType.STRUCT_PZT
                .equals(structId)) {
            List<String> submitAnswers = JsonUtil.readValue(submitAnswer, List.class);
            List<String> postilTeachers = JsonUtil.readValue(postilTeacher, List.class);
            if (submitAnswers.size() != postilTeachers.size()) {
                throw new BizLayerException("答案个数：" + submitAnswers.size() + ",批注个数：" + postilTeachers.size() +
                        "，个数必须相等。", ANSWER_PARAMETER_ILL);
            }
        }
    }


    public static List<StudentExercise> initSeList(StudentExercise se, List<UpdateSubmitAnswerVo>
            updateSubmitAnswerVoList, String correctorRole, Long correctorId) {
        List<StudentExercise> seList = new ArrayList<>();
        for (UpdateSubmitAnswerVo vo : updateSubmitAnswerVoList) {
            StudentExercise se1 = new StudentExercise();
            BeanUtils.copyProperties(se, se1);
            se1.setParentQuestionId(vo.getParentQuestionId());
            se1.setStructId(vo.getStructId());
            se1.setQuestionId(vo.getQuestionId());
            se1.setSubmitAnswer(vo.getSubmitAnswer());
            se1.setResult(vo.getResult());
            se1.setCreateTime(new Date());
            se1.setSubmitTime(new Date());
            se1.setLastUpdateTime(new Date());
            se1.setSubExerciseSource(se.getSubExerciseSource());
            se1.setAudioResult(vo.getAudioResult());
            se1.setTotalScore(vo.getTotalScore());
            se1.setScore(getScoreByStruct(vo.getStructId(),vo.getTotalScore(),vo.getResult()));
            StuAnswerUtil.setCorrectInfo(se1, correctorRole, correctorId);
            if (AnswerCommonUtil.isAllCorrect(vo.getResult(), vo.getStructId())) {
                se1.setFlag(3); //如果批改结果不包含有6的数据，则认为该题的已经批改完成
            }
            seList.add(se1);
        }
        return seList;
    }

    /**
     * 根据题型结构、批改结果、填空题/7x5每小题的得分
     *
     * @param structId
     * @param totalScore
     * @param result
     * @return
     */
    public static Map<String,Map<String, Double>> getEveryItemScoreByStruct(Integer structId, Double totalScore, String result) {
        if (null == totalScore) {
            return null;
        }
        Map<String,Map<String,Double>> lastResultMap = new HashedMap<>();
        Map<String,Double> resultTKTMap = new HashedMap<>();
        Map<String,Double> result7x5Map = new HashedMap<>();
        //题都被批改后才计算分值、未批改完成不做分值计算

        List<Map<String, Object>> resultList = JsonUtil.readValue(result, new TypeReference<List<Map<String,
                Object>>>() {
        });
        if(CollectionUtils.isNotEmpty(resultList)) {
            int completionSize = resultList.size();
            for (Map<String, Object> currentResult : resultList) {
                String indexValue = currentResult.get(BLANK_RESULT) + "";
                if (!indexValue.contains(StuAnswerConstant.ExerciseResult.SUBMITED)) {
                    if (structId.equals(StuAnswerConstant.StructType.STRUCT_TKT)) {
                        //填空题
                        Double everyIndexScore = totalScore / completionSize;
                        Double eueryCurrentScore = getScore(indexValue, everyIndexScore);
                        String index = currentResult.get(BLANK_INDEX) + "";
                        resultTKTMap.put(index, eueryCurrentScore);
                    }else if (structId.equals(StuAnswerConstant.StructType.STRUCT_7x5)) {
                        //七选五
                        Map<String, Double> quesResultOf7x5 = PraxisUtilForOther.getItemQuesResultOf7x5(result, totalScore);
                        if(quesResultOf7x5 != null && !quesResultOf7x5.isEmpty() && quesResultOf7x5.size() > 0) {
                            result7x5Map.putAll(quesResultOf7x5);
                        }else {
                            Map<String,Double> resultOf7x5 = new HashedMap<>(completionSize);
                            for (int i = 0; i < resultOf7x5.size(); i++) {
                                resultOf7x5.put(i + 1 + "",new Double(0));
                            }
                            result7x5Map.putAll(resultOf7x5);
                        }
                    }
                }
            }
        }else {
            if (!result.contains(StuAnswerConstant.ExerciseResult.SUBMITED)) {
                if (structId.equals(StuAnswerConstant.StructType.STRUCT_TKT)) {
                    //填空题
                    Double eueryCurrentScore = getScore(result, totalScore);
                    String index = "1";
                    resultTKTMap.put(index, eueryCurrentScore);
                }else if (structId.equals(StuAnswerConstant.StructType.STRUCT_7x5)) {
                    //七选五
                    Map<String, Double> quesResultOf7x5 = PraxisUtilForOther.getItemQuesResultOf7x5(result, totalScore);
                    if(quesResultOf7x5 != null && !quesResultOf7x5.isEmpty() && quesResultOf7x5.size() > 0) {
                        result7x5Map.putAll(quesResultOf7x5);
                    }else {
                        result7x5Map.put("1",new Double(0));
                    }
                }
            }
        }
        lastResultMap.put("resultTKTMap",resultTKTMap);
        lastResultMap.put("result7x5Map",result7x5Map);
        return lastResultMap;
    }
    /**
     * 根据题型结构、批改结果、小题的总分计算该道题的分值
     *
     * @param structId
     * @param totalScore
     * @param result
     * @return
     */
    public static Double getScoreByStruct(Integer structId, Double totalScore, String result) {
        if (null == totalScore) {
            return null;
        }
        Double calculateScore = new Double(0);
        //题都被批改后才计算分值、未批改完成不做分值计算

        List<Map<String, Object>> resultList = JsonUtil.readValue(result, new TypeReference<List<Map<String,
                Object>>>() {
        });
        if(CollectionUtils.isNotEmpty(resultList)) {
            int completionSize = resultList.size();
            for (Map<String, Object> currentResult : resultList) {
                String indexValue = currentResult.get(BLANK_RESULT) + "";
                if (!indexValue.contains(StuAnswerConstant.ExerciseResult.SUBMITED)) {
                    if (structId.equals(StuAnswerConstant.StructType.STRUCT_TKT)) {
                        //填空题
                        Double everyIndexScore = totalScore / completionSize;
                        Double eueryCurrentScore = getScore(indexValue, everyIndexScore);
                        if(calculateScore == null) {
                            calculateScore = new Double(0);
                        }
                        calculateScore += eueryCurrentScore;
                    }else if (structId.equals(StuAnswerConstant.StructType.STRUCT_7x5)) {
                        //七选五
                        Double score = PraxisUtilForOther.getScoreQuesResultOf7x5(result, totalScore);
                        if(score != null && !"".equals(score)) {
                                calculateScore = score;
                        }else {
                            calculateScore = new Double(0);
                        }
                    } else {
                        //其他题型结构
                        calculateScore = getScore(result, totalScore);
                    }
                }else {
                    calculateScore = null;
                }
            }
        }else {
            if (!result.contains(StuAnswerConstant.ExerciseResult.SUBMITED)) {
                if (structId.equals(StuAnswerConstant.StructType.STRUCT_TKT)) {
                    //填空题
                    Double eueryCurrentScore = getScore(result, totalScore);
                    if(calculateScore == null) {
                        calculateScore = new Double(0);
                    }
                    calculateScore += eueryCurrentScore;
                }else if (structId.equals(StuAnswerConstant.StructType.STRUCT_7x5)) {
                    //七选五
                    Double score = PraxisUtilForOther.getScoreQuesResultOf7x5(result, totalScore);
                    if(score != null && !"".equals(score)) {
                            calculateScore = score;
                    }else {
                        calculateScore = new Double(0);
                    }
                } else {
                    //其他题型结构
                    calculateScore = getScore(result, totalScore);
                }
            }else {
                calculateScore = null;
            }
        }
        if (null!=calculateScore){
            BigDecimal bg = new BigDecimal(calculateScore);
            calculateScore = bg.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
        }
        return calculateScore;
    }

    /**
     * 根据做答状态计算分值
     *
     * @param indexValue
     * @param everyIndexScore
     * @return
     */
    private static Double getScore(String indexValue, Double everyIndexScore) {
        if (indexValue.equals(StuAnswerConstant.ExerciseResult.RIGHT)) {
            return everyIndexScore;
        } else if (indexValue.equals(StuAnswerConstant.ExerciseResult.HALFRIGHT)) {
            return everyIndexScore / 2;
        } else if (indexValue.equals(StuAnswerConstant.ExerciseResult.ERROR) || indexValue.equals(StuAnswerConstant
                .ExerciseResult.NOANSWER)) {
            return new Double(0);
        } else {
            logger.warn("未批改的结果不做分值计算");
            return null;
        }

    }


    public static List<Question> getQuesListByIds(QuestionSearchService questionSearchService, List<Long>
            questionIdList) {
        if (CollectionUtils.isEmpty(questionIdList)) {
            return new ArrayList<>();
        }
        long s = System.currentTimeMillis();
        List<Question> list = questionSearchService.getQuesListByIds(questionIdList);
        logger.debug("findQuestions cost time:" + (System.currentTimeMillis() - s));
        return list;
    }


    public static List<Question> getQuestionListByIds(QuestionSearchService questionSearchService, List<Long>
            questionIdList) {
        if (CollectionUtils.isEmpty(questionIdList)) {
            return new ArrayList<>();
        }
        long s = System.currentTimeMillis();
        FindQuestionsRequest req = new FindQuestionsRequest();
        req.setIds(new ArrayList<>(new HashSet<>(questionIdList)));
        req.setBasic(true);
        req.setPageable(false);
        req.setQuestionType(QuestionTypeEnum.ALL);
        req.setStates(Collections.singletonList(QuestionState.ALL));
        FindQuestionsResponse questionsResponse = questionSearchService.findSolrQuestions(req);
        logger.debug("findQuestions cost time:" + (System.currentTimeMillis() - s));
        if (questionsResponse.success()) {
            return questionsResponse.getQuestionList();
        } else {
            logger.error("not found!" + questionIdList);
            return new ArrayList<>();
        }

    }

    public static List<Question> getQuesListByIdsFast(QuestionService questionService, List<Long> questionIdList) {
        if (CollectionUtils.isEmpty(questionIdList)) {
            return new ArrayList<>();
        }
        long s = System.currentTimeMillis();
        FindQuestionsByIdsRequest req = new FindQuestionsByIdsRequest();
        req.setRecursive(true);
        req.setQuestionIds(new ArrayList<>(new HashSet<>(questionIdList)));
        FindQuestionsByIdsResponse questionsResponse = questionService.findQuestionsByIdsFast(req);
        logger.debug("findQuestionsByIdsFast cost time:" + (System.currentTimeMillis() - s));
        if (questionsResponse.success()) {
            return questionsResponse.getQuestionList();
        } else {
            logger.error("not found!" + questionIdList);
            return new ArrayList<>();
        }
    }

    public static List<Question> getQuesListByIds(QuestionSearchService QuestionSearchService, List<Long>
            questionIdList, boolean basic) {
        if (CollectionUtils.isEmpty(questionIdList)) {
            return new ArrayList<>();
        }
        long s = System.currentTimeMillis();
        FindQuestionsRequest req = new FindQuestionsRequest();
        req.setIds(new ArrayList<>(new HashSet<>(questionIdList)));
        req.setBasic(basic);
        req.setPageable(false);
        req.setQuestionType(QuestionTypeEnum.ALL);
        req.setStates(Collections.singletonList(QuestionState.ALL));
        FindQuestionsResponse questionsResponse = QuestionSearchService.findSolrQuestions(req);
        logger.debug("findQuestions cost time:" + (System.currentTimeMillis() - s));
        if (questionsResponse.success()) {
            return questionsResponse.getQuestionList();
        } else {
            logger.error("not found!" + questionIdList);
            return new ArrayList<>();
        }
    }

    public static List<Question> getValidQuesList(List<Question> questions) {
        List<Question> avaliableQuesIds = new ArrayList<>();
        for (Question ques : questions) {
            Integer newFormat = ques.getNewFormat();
            if (new Integer(1).equals(newFormat)) {
                avaliableQuesIds.add(ques);
            }
        }
        return avaliableQuesIds;
    }

    public static List<Long> getIdsByQues(List<Question> quesList) {
        List<Long> qrList = new ArrayList<>();
        for (Question ques : quesList) {
            qrList.add(ques.getId());
        }
        return qrList;
    }

    public static int getEndIndex(Integer fromIndex, Integer pageSize, int totalCount) {
        int endIndex;
        if ((fromIndex + pageSize) <= totalCount) {
            endIndex = fromIndex + pageSize;
        } else if ((fromIndex + pageSize) > totalCount && fromIndex < totalCount) {
            endIndex = totalCount;
        } else {
            endIndex = fromIndex;
        }
        return endIndex;
    }



    static Comparator<StudentExercise> TimeComparator = new Comparator<StudentExercise>() {
        @Override
        public int compare(StudentExercise se1, StudentExercise se2) {
            if (se1 != null && se2 != null) {
                Date time1 = se1.getCreateTime();
                Date time12 = se2.getCreateTime();
                if (time1 == null || time12 == null) {
                    return 0;
                } else if (time1.after(time12)) {
                    return -1;
                } else if (time1.equals(time12)) {
                    return 0;
                } else {
                    return 1;
                }
            }
            return 0;
        }
    };

    static class QuesIdComparator implements Comparator<StudentExercise> {
        private List<Long> quesIds;

        public QuesIdComparator(List<Long> quesIds) {
            this.quesIds = quesIds;
        }

        @Override
        public int compare(StudentExercise se1, StudentExercise se2) {
            if (se1 != null && se2 != null) {
                Long questionId1 = se1.getQuestionId();
                Long questionId2 = se2.getQuestionId();

                return getCompareInt(quesIds, questionId1, questionId2);
            }
            return 0;
        }
    }


    /**
     * 所有题目id：包含小题
     *
     * @param seList
     * @return
     */
    public static List<Long> getQuesIdList(List<StudentExercise> seList) {
        List<Long> questionIdList = new ArrayList<>();
        if (CollectionUtils.isEmpty(seList)) {
            return questionIdList;
        }
        for (StudentExercise answer : seList) {
            if (answer.getParentQuestionId() != null && answer.getParentQuestionId() > 0) {
                questionIdList.add(answer.getParentQuestionId());
            }
            questionIdList.add(answer.getQuestionId());
        }
        return questionIdList;
    }

    public static Double getRightNumberS(Integer structId, String result, String blankResult) {
        Double rightNumber;

        //填空题正确次数
        if (StuAnswerConstant.StructType.STRUCT_TKT.equals(structId)) {
            rightNumber = StuAnswerUtil.getBlankRightNumber(blankResult);
        } else if (StuAnswerConstant.StructType.STRUCT_7x5.equals(structId)) {
            rightNumber = (double) (StuAnswerConstant.ExerciseResult.RIGHT.equals(PraxisUtilForOther
                    .getQuesResultOf7x5In(result)) ? 1 : 0);
        } else {
            rightNumber = (double) ((result != null && result.equals(StuAnswerConstant.ExerciseResult.RIGHT)) ? 1 : 0);
        }
        return rightNumber;
    }

    public static Double getRightNumberC(Integer structId, String result, String blankResult, Integer answerNum) {
        Double rightNumber;

        if (StuAnswerConstant.StructType.STRUCT_TKT.equals(structId)) {
            rightNumber = StuAnswerUtil.getBlankRightNumber(result, answerNum);
        } else if (StuAnswerConstant.StructType.STRUCT_7x5.equals(structId)) {
            rightNumber = (double) (StuAnswerConstant.ExerciseResult.RIGHT.equals(PraxisUtilForOther
                    .getQuesResultOf7x5In(result)) ? 1 : 0);
        } else {
            rightNumber = (double) (result.equals(StuAnswerConstant.ExerciseResult.RIGHT) ? 1 : 0);
        }
        return rightNumber;
    }

    public static Double getRightNumberCs(Integer structId, String result, String blankResult, Integer answerNum, int
            subQuesSize) {
        Double topicRightNumber;
        if (StuAnswerConstant.StructType.STRUCT_TKT.equals(structId)) {
            topicRightNumber = StuAnswerUtil.getBlankRightNumber(result, answerNum) / (double) subQuesSize;
        } else if (StuAnswerConstant.StructType.STRUCT_7x5.equals(structId)) {
            topicRightNumber = (double) (StuAnswerConstant.ExerciseResult.RIGHT.equals(PraxisUtilForOther
                    .getQuesResultOf7x5In(result)) ? 1 : 0) / (double) subQuesSize;
        } else {
            topicRightNumber = (result.equals(StuAnswerConstant.ExerciseResult.RIGHT) ? Double.valueOf(1) : Double
                    .valueOf(0)) / (double) subQuesSize;
        }
        return topicRightNumber;
    }

    public static Double getRightNumberQueses(List<StudentWorkAnswer> studentWorkAnswerses) {
        Double subQuesRightNumber = 0d;
        for (StudentWorkAnswer subQues : studentWorkAnswerses) {
            String subQuesResult = subQues.getResult();
            Integer subStructId = subQues.getStructId();
            String subQuesBlankResult = subQues.getBlankResult();

            Double rightNumberS = StuAnswerUtil.getRightNumberS(subStructId, subQuesResult, subQuesBlankResult);
            subQuesRightNumber += rightNumberS;

        }
        return subQuesRightNumber;
    }


    /***
     * 组装填空题 保留老师批改的空的数据，使用智能批改补充老师未批改的空
     * @param intellCorrectResult
     * @param se
     */
    public static void assembleTkt(String intellCorrectResult, StudentExercise se) {
        //老师批改结果解析
        List<Map<String, Object>> correctResultList = JsonUtil.readValue(se.getResult(), new
                TypeReference<List<Map<String, Object>>>() {
        });
        //智能批改结果解析数据
        List<Map<String, Object>> intellCorrectResultList = JsonUtil.readValue(intellCorrectResult, new
                TypeReference<List<Map<String, Object>>>() {
        });
        List<Map<String, Object>> dataCorrectList = new ArrayList<>();
        List<Map<String, Object>> dataRoleList = new ArrayList<>();
        for (Map<String, Object> intellCorrectResultMap : intellCorrectResultList) {
            String intellCorrectIndex = intellCorrectResultMap.get(BLANK_INDEX) + "";
            String intellCorrectValue = intellCorrectResultMap.get(BLANK_RESULT) + "";
            Map currenCorrecttMap = new HashMap(4);
            Map currentRoleMap = new HashMap(4);
            for (Map<String, Object> correctResultMap : correctResultList) {
                //[{"index":1,"result":"1"},{"index":2,"result":"2"}]  如果 index相同
                String correctIndex = correctResultMap.get(BLANK_INDEX) + "";
                String correctValue = correctResultMap.get(BLANK_RESULT) + "";
                if (intellCorrectIndex.equals(correctIndex) && !StuAnswerConstant.ExerciseResult.SUBMITED.equals
                        (correctValue)) {
                    currenCorrecttMap.put(BLANK_INDEX, intellCorrectIndex);
                    currenCorrecttMap.put(BLANK_RESULT, correctValue);
                    currentRoleMap.put(BLANK_INDEX, intellCorrectIndex);
                    currentRoleMap.put(CORRECTOR_ROLE, StuAnswerConstant.CorrectorRole.TEACHER);
                    break;
                } else {
                    currenCorrecttMap.put(BLANK_INDEX, intellCorrectIndex);
                    currenCorrecttMap.put(BLANK_RESULT, intellCorrectValue);
                    currentRoleMap.put(BLANK_INDEX, intellCorrectIndex);
                    currentRoleMap.put(CORRECTOR_ROLE, StuAnswerConstant.CorrectorRole.TEACHER);
                }
            }
            dataCorrectList.add(currenCorrecttMap);
            dataRoleList.add(currentRoleMap);
        }
        logger.debug("[[submitIntellCorrectAnswer]]为填空题时获取的批改结果:" + JsonUtil.obj2Json(dataCorrectList));
        logger.debug("[[submitIntellCorrectAnswer]]为填空题时获取的批改角色:" + JsonUtil.obj2Json(dataRoleList));
        se.setCorrectorRole(JsonUtil.obj2Json(dataRoleList));
        se.setResult(JsonUtil.obj2Json(dataCorrectList));

    }


    /***
     *  判断是否为重复批改
     * @param historyExercise
     * @param newExercise
     * @return
     */
    public static boolean getRepeatCorrect(StudentExercise historyExercise, StudentExercise newExercise) {
        String oldResult = historyExercise.getResult();
        if (historyExercise.getStructId().equals(StuAnswerConstant.StructType.STRUCT_TKT)) {
            String currentResult = newExercise.getCurrentResult() == null ? newExercise.getResult() : newExercise
                    .getCurrentResult();
            //这次的批改结果和上次的相同，默认不是重复批改
            if (currentResult.equals(oldResult)) {
                return false;
            } else {
                List<Map<String, Object>> currentResultList = JsonUtil.readValue(currentResult, new
                        TypeReference<List<Map<String, Object>>>() {
                });
                List<Map<String, Object>> oldResultList = JsonUtil.readValue(oldResult, new
                        TypeReference<List<Map<String, Object>>>() {
                });
                for (Map<String, Object> oldResultMap : oldResultList) {
                    String oldIndex = oldResultMap.get(BLANK_INDEX) + "";
                    for (Map<String, Object> currentResultMap : currentResultList) {
                        String currentIndex = currentResultMap.get(BLANK_INDEX) + "";
                        if (currentIndex.equals(oldIndex)) {
                            String currentValue = currentResultMap.get(BLANK_RESULT) + "";
                            String oldValue = oldResultMap.get(BLANK_RESULT) + "";
                            logger.info("getRepeatCorrect currentValue:" + currentValue + ";oldValue:" + oldValue);
                            //新的批改结果和老的批改结果不相同，并且老的结果为1、2、5、7的状态
                            if (!oldValue.equals(StuAnswerConstant.ExerciseResult.SUBMITED) && !oldValue.equals
                                    (StuAnswerConstant.ExerciseResult.NOANSWER)) {
                                return true;
                            }
                        }
                    }
                }
            }
            return false;
        } else {
            return !StuAnswerConstant.ExerciseResult.SUBMITED.equals(oldResult);

        }
    }

    public static int getCorrectStatus(StudentExercise historyExercise, StudentExercise newExercise) {
        String oldResult = historyExercise.getResult();
        String newResult = newExercise.getResult();
        logger.debug("getCorrectStatus oldResult:" + oldResult + ";newResult:" + newResult);
        if (historyExercise.getStructId().equals(StuAnswerConstant.StructType.STRUCT_TKT)) {
            List<Map<String, Object>> oldResultList = JsonUtil.readValue(oldResult, new
                    TypeReference<List<Map<String, Object>>>() {
            });
            List<Map<String, Object>> newResultList = JsonUtil.readValue(newResult, new
                    TypeReference<List<Map<String, Object>>>() {
            });
            for (Map<String, Object> oldResultMap : oldResultList) {
                String oldIndex = oldResultMap.get(BLANK_INDEX) + "";
                for (Map<String, Object> newResultMap : newResultList) {
                    String newIndex = newResultMap.get(BLANK_INDEX) + "";
                    if (oldIndex.equals(newIndex)) {
                        String oldValue = oldResultMap.get(BLANK_RESULT) + "";
                        String newValue = newResultMap.get(BLANK_RESULT) + "";
                        logger.debug("oldValue:" + oldValue + ";" + !oldValue.equals(newValue) + ";newValue:" +
                                newValue + ";" + !oldResult.equals(StuAnswerConstant.ExerciseResult.SUBMITED));
                        //新的批改结果和老的批改结果不相同，并且老的结果为1、2、5、7的状态
                        if (!oldValue.equals(newValue)) {
                            if (oldValue.equals(StuAnswerConstant.ExerciseResult.RIGHT)) {
                                return 2;
                            } else {
                                return 1;
                            }
                        }
                    }
                }
            }
            return 0;
        } else { //其他题型

            if (oldResult.equals(newResult)) {
                return 0;
            } else {
                //如果老的批改结果为半对半错
                if (StuAnswerConstant.ExerciseResult.HALFRIGHT.equals(oldResult)) {
                    if (newResult.equals(StuAnswerConstant.ExerciseResult.RIGHT)) {
                        //由半对半错批改为对
                        return 3;
                    } else {
                        //由半对半错批改为错
                        return 4;
                    }
                }
                //如果新的批改结果为半对半错
                if (StuAnswerConstant.ExerciseResult.HALFRIGHT.equals(newResult)) {
                    if (oldResult.equals(StuAnswerConstant.ExerciseResult.RIGHT)) {
                        //由对批改为半对半错
                        return 5;
                    } else {
                        //由错批改为半对半错
                        return 6;
                    }
                }
                if (oldResult.equals(StuAnswerConstant.ExerciseResult.RIGHT)) {
                    return 2;
                } else {
                    return 1;
                }
            }
        }
    }

    public static String getCorrectInfoStatus(StudentExercise historyExercise, StudentExercise newExercise) {

        String oldResult = historyExercise.getResult();
        String newResult = newExercise.getResult();
        logger.debug("getCorrectStatus oldResult:" + oldResult + ";newResult:" + newResult);
        List resultList = new ArrayList();
        if (historyExercise.getStructId().equals(StuAnswerConstant.StructType.STRUCT_TKT)) {
            List<Map<String, Object>> oldResultList = JsonUtil.readValue(oldResult, new
                    TypeReference<List<Map<String, Object>>>() {
            });
            List<Map<String, Object>> newResultList = JsonUtil.readValue(newResult, new
                    TypeReference<List<Map<String, Object>>>() {
            });
            for (Map<String, Object> newResultMap : newResultList) {
                String oldIndex = newResultMap.get(BLANK_INDEX) + "";
                Map returnMap = new HashMap(1);
                for (Map<String, Object> oldResultMap : oldResultList) {
                    String newIndex = oldResultMap.get(BLANK_INDEX) + "";
                    if (oldIndex.equals(newIndex)) {
                        String oldValue = oldResultMap.get(BLANK_RESULT) + "";
                        String newValue = newResultMap.get(BLANK_RESULT) + "";
                        logger.debug("oldValue:" + oldValue + ";" + !oldValue.equals(newValue) + ";newValue:" +
                                newValue + ";" + !oldResult.equals(StuAnswerConstant.ExerciseResult.SUBMITED));
                        //新的批改结果和老的批改结果不相同，并且老的结果为1、2、5、7的状态
                        returnMap.put(BLANK_INDEX, oldIndex);
                        if (!oldValue.equals(newValue)) {
                            //如果是第一次批改
                            if (oldValue.equals(StuAnswerConstant.ExerciseResult.SUBMITED)) {
                                returnMap.put(BLANK_RESULT, "7");
                            } else {
                                if (oldValue.equals(StuAnswerConstant.ExerciseResult.RIGHT)) {
                                    returnMap.put(BLANK_RESULT, "2");
                                } else {
                                    returnMap.put(BLANK_RESULT, "1");
                                }
                            }
                        } else {//新的批改结果和老的批改结果相同

                            returnMap.put(BLANK_RESULT, "0");
                        }
                    }
                }
                resultList.add(returnMap);
            }
            return JsonUtil.obj2Json(resultList);
        } else { //其他题型
            Map returnMap = new HashMap(1);
            returnMap.put(BLANK_INDEX, "1");
            if (oldResult.equals(StuAnswerConstant.ExerciseResult.SUBMITED)) {
                returnMap.put(BLANK_RESULT, "7");
                resultList.add(returnMap);
                return JsonUtil.obj2Json(resultList);
            } else {
                if (oldResult.equals(newResult)) {
                    returnMap.put(BLANK_RESULT, "0");
                    resultList.add(returnMap);
                    return JsonUtil.obj2Json(resultList);
                } else {
                    //如果老的批改结果为半对半错
                    if (StuAnswerConstant.ExerciseResult.HALFRIGHT.equals(oldResult)) {
                        if (newResult.equals(StuAnswerConstant.ExerciseResult.RIGHT)) {
                            //return "3";  //由半对半错批改为对
                            returnMap.put(BLANK_RESULT, "3");
                            resultList.add(returnMap);
                            return JsonUtil.obj2Json(resultList);
                        } else {
                            //return "4";//由半对半错批改为错
                            returnMap.put(BLANK_RESULT, "4");
                            resultList.add(returnMap);
                            return JsonUtil.obj2Json(resultList);
                        }
                    }
                    //如果新的批改结果为半对半错
                    if (StuAnswerConstant.ExerciseResult.HALFRIGHT.equals(newResult)) {
                        if (oldResult.equals(StuAnswerConstant.ExerciseResult.RIGHT)) {
                            // return "5";  //由对批改为半对半错
                            returnMap.put(BLANK_RESULT, "5");
                            resultList.add(returnMap);
                            return JsonUtil.obj2Json(resultList);
                        } else {
                            // return "6";//由错批改为半对半错
                            returnMap.put(BLANK_RESULT, "6");
                            resultList.add(returnMap);
                            return JsonUtil.obj2Json(resultList);
                        }
                    }
                    if (oldResult.equals(StuAnswerConstant.ExerciseResult.RIGHT)) {
                        //return "2";
                        returnMap.put(BLANK_RESULT, "2");
                        resultList.add(returnMap);
                        return JsonUtil.obj2Json(resultList);
                    } else {
                        // return "1";
                        returnMap.put(BLANK_RESULT, "1");
                        resultList.add(returnMap);
                        return JsonUtil.obj2Json(resultList);
                    }
                }
            }

        }
    }



    public static String getAutoIntellCorrectInfoStatus(StudentExercise historyExercise, StudentExercise newExercise) {
        String oldResult = historyExercise.getResult();
        String newResult = newExercise.getResult();
        logger.debug("getCorrectStatus oldResult:" + oldResult + ";newResult:" + newResult);
        List resultList = new ArrayList();
        if (historyExercise.getStructId().equals(StuAnswerConstant.StructType.STRUCT_TKT)) {
            List<Map<String, Object>> oldResultList = JsonUtil.readValue(oldResult, new
                    TypeReference<List<Map<String, Object>>>() {
            });
            List<Map<String, Object>> newResultList = JsonUtil.readValue(newResult, new
                    TypeReference<List<Map<String, Object>>>() {
            });
            for (Map<String, Object> newResultMap : newResultList) {
                String oldIndex = newResultMap.get(BLANK_INDEX) + "";
                Map returnMap = new HashMap(1);
                for (Map<String, Object> oldResultMap : oldResultList) {
                    String newIndex = oldResultMap.get(BLANK_INDEX) + "";
                    if (oldIndex.equals(newIndex)) {
                        String oldValue = oldResultMap.get(BLANK_RESULT) + "";
                        String newValue = newResultMap.get(BLANK_RESULT) + "";
                        logger.debug("oldValue:" + oldValue + ";" + !oldValue.equals(newValue) + ";newValue:" +
                                newValue + ";" + !oldResult.equals(StuAnswerConstant.ExerciseResult.SUBMITED));
                        //新的批改结果和老的批改结果不相同，并且老的结果为1、2、5、7的状态
                        returnMap.put(BLANK_INDEX, oldIndex);
                        returnMap.put(BLANK_RESULT, "7");
                        /*if (!oldValue.equals(newValue)) {
                            //如果是第一次批改
                            if (oldValue.equals(StuAnswerConstant.ExerciseResult.SUBMITED)) {
                                returnMap.put(BLANK_RESULT, "7");
                            } else {
                                if (oldValue.equals(StuAnswerConstant.ExerciseResult.RIGHT)) {
                                    returnMap.put(BLANK_RESULT, "2");
                                } else {
                                    returnMap.put(BLANK_RESULT, "1");
                                }
                            }
                        } else {//新的批改结果和老的批改结果相同

                            returnMap.put(BLANK_RESULT, "0");
                        }*/
                    }
                }
                resultList.add(returnMap);
            }
            return JsonUtil.obj2Json(resultList);
        } else { //其他题型
            Map returnMap = new HashMap(1);
            returnMap.put(BLANK_INDEX, "1");
            returnMap.put(BLANK_RESULT, "7");
            resultList.add(returnMap);
            return JsonUtil.obj2Json(resultList);

            /*if (oldResult.equals(StuAnswerConstant.ExerciseResult.SUBMITED)) {
                returnMap.put(BLANK_RESULT, "7");
                resultList.add(returnMap);
                return JsonUtil.obj2Json(resultList);
            } else {
                if (oldResult.equals(newResult)) {
                    returnMap.put(BLANK_RESULT, "0");
                    resultList.add(returnMap);
                    return JsonUtil.obj2Json(resultList);
                } else {
                    //如果老的批改结果为半对半错
                    if (StuAnswerConstant.ExerciseResult.HALFRIGHT.equals(oldResult)) {
                        if (newResult.equals(StuAnswerConstant.ExerciseResult.RIGHT)) {
                            //return "3";  //由半对半错批改为对
                            returnMap.put(BLANK_RESULT, "3");
                            resultList.add(returnMap);
                            return JsonUtil.obj2Json(resultList);
                        } else {
                            //return "4";//由半对半错批改为错
                            returnMap.put(BLANK_RESULT, "4");
                            resultList.add(returnMap);
                            return JsonUtil.obj2Json(resultList);
                        }
                    }
                    //如果新的批改结果为半对半错
                    if (StuAnswerConstant.ExerciseResult.HALFRIGHT.equals(newResult)) {
                        if (oldResult.equals(StuAnswerConstant.ExerciseResult.RIGHT)) {
                            // return "5";  //由对批改为半对半错
                            returnMap.put(BLANK_RESULT, "5");
                            resultList.add(returnMap);
                            return JsonUtil.obj2Json(resultList);
                        } else {
                            // return "6";//由错批改为半对半错
                            returnMap.put(BLANK_RESULT, "6");
                            resultList.add(returnMap);
                            return JsonUtil.obj2Json(resultList);
                        }
                    }
                    if (oldResult.equals(StuAnswerConstant.ExerciseResult.RIGHT)) {
                        //return "2";
                        returnMap.put(BLANK_RESULT, "2");
                        resultList.add(returnMap);
                        return JsonUtil.obj2Json(resultList);
                    } else {
                        // return "1";
                        returnMap.put(BLANK_RESULT, "1");
                        resultList.add(returnMap);
                        return JsonUtil.obj2Json(resultList);
                    }
                }
            }*/

        }
    }

    private static void assembleRoleResult(String teacheResult, String intellResult, StudentExercise entity) {
        //如果批改为填空题批改
        if (entity.getStructId().equals(StuAnswerConstant.StructType.STRUCT_TKT)) {
            //老师批改结果解析
            List<Map<String, Object>> teacheResultList = JsonUtil.readValue(teacheResult, new
                    TypeReference<List<Map<String, Object>>>() {
            });
            //智能批改结果解析数据
            List<Map<String, Object>> intellResultList = JsonUtil.readValue(intellResult, new
                    TypeReference<List<Map<String, Object>>>() {
            });
            //批改角色数据
            /*String correctorRole = entity.getCorrectorRole();
            List<Map<String, Object>> correctorRoleList = new ArrayList<Map<String, Object>>();
            if(correctorRole.contains("index")) {*/
            List<Map<String, Object>> correctorRoleList = JsonUtil.readValue(entity.getCorrectorRole(), new
                        TypeReference<List<Map<String, Object>>>() {
                        });
            /*}else {
                HashMap<String, Object> map = new HashMap<>();
                map.put("index",1);
                map.put("corrector_role",correctorRole);
                correctorRoleList.add(map);
            }*/

            Collections.sort(teacheResultList, new Comparator<Map<String,Object>>() {
                @Override
                public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                    int index1 = Integer.parseInt(o1.get("index").toString());
                    int index2 = Integer.parseInt(o2.get("index").toString());
                    return ((Integer)index1).compareTo((Integer)index2);
                }
            });
            Collections.sort(intellResultList, new Comparator<Map<String,Object>>() {
                @Override
                public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                    int index1 = Integer.parseInt(o1.get("index").toString());
                    int index2 = Integer.parseInt(o2.get("index").toString());
                    return ((Integer)index1).compareTo((Integer)index2);
                }
            });

            if(CollectionUtils.isNotEmpty(correctorRoleList) && correctorRoleList.size() > 0) {
                Collections.sort(correctorRoleList, new Comparator<Map<String,Object>>() {
                    @Override
                    public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                        int index1 = Integer.parseInt(o1.get("index").toString());
                        int index2 = Integer.parseInt(o2.get("index").toString());
                        return ((Integer)index1).compareTo((Integer)index2);
                    }
                });
            }

            List<Map<String, Object>> resultList = new ArrayList<>();
            List<Map<String, Object>> roleList = new ArrayList<>();
            if(CollectionUtils.isNotEmpty(correctorRoleList) && correctorRoleList.size() > 0) {
                //for(Map<String, Object> correctorRoleResultMap : correctorRoleList) {
                for(int i = 0; i < correctorRoleList.size(); i++) {
                /*String correctorRoleIndex = correctorRoleResultMap.get(BLANK_INDEX) + "";
                String correctorRoleValue = correctorRoleResultMap.get(BLANK_ROLE) + "";*/
                    String correctorRoleIndex = correctorRoleList.get(i).get(BLANK_INDEX) + "";
                    String correctorRoleValue = correctorRoleList.get(i).get(BLANK_ROLE) + "";
                    Map resultMap = new HashMap(4);
                    Map roleMap = new HashMap(4);
                    //for (Map<String, Object> intellCorrectResultMap : intellResultList) {
                    for(int j = i; j < intellResultList.size(); j++) {
                        String intellCorrectIndex = intellResultList.get(j).get(BLANK_INDEX) + "";
                        String intellCorrectValue = intellResultList.get(j).get(BLANK_RESULT) + "";
                    /*Map resultMap = new HashMap(4);
                    Map roleMap = new HashMap(4);*/
                        //for (Map<String, Object> correctResultMap : teacheResultList) {
                        for (int z = i; z < teacheResultList.size(); z++) {
                        /*String correctResult = correctResultMap.get(BLANK_RESULT) + "";
                        String correctIndex = correctResultMap.get(BLANK_INDEX) + "";*/
                            String correctResult = teacheResultList.get(z).get(BLANK_RESULT) + "";
                            String correctIndex = teacheResultList.get(z).get(BLANK_INDEX) + "";

                            if("ai".equals(correctorRoleValue)) {
                                resultMap.put(BLANK_INDEX, intellCorrectIndex);
                                resultMap.put(BLANK_RESULT, intellCorrectValue);
                                roleMap.put(BLANK_INDEX, intellCorrectIndex);
                                roleMap.put(CORRECTOR_ROLE, CORRECTOR_ROLE_AI);
                                break;
                            } else if("teacher".equals(correctorRoleValue)) {
                                resultMap.put(BLANK_INDEX, correctIndex);
                                resultMap.put(BLANK_RESULT, correctResult);
                                roleMap.put(BLANK_INDEX, correctIndex);
                                roleMap.put(CORRECTOR_ROLE, CORRECTOR_ROLE_TEACHER);
                            }
                            break;

                        /*if (intellCorrectIndex.equals(correctIndex) && !StuAnswerConstant.ExerciseResult.SUBMITED.equals
                                (correctResult)) {
                            resultMap.put(BLANK_INDEX, intellCorrectIndex);
                            resultMap.put(BLANK_RESULT, correctResult);
                            roleMap.put(BLANK_INDEX, intellCorrectIndex);
                            //修改成ai
                            //roleMap.put(CORRECTOR_ROLE, StuAnswerConstant.CorrectorRole.TEACHER);
                            if(correctorRoleValue != null && !"".equals(correctorRoleValue) && correctorRoleValue.equals("teacher")) {
                                roleMap.put(CORRECTOR_ROLE, CORRECTOR_ROLE_TEACHER);
                            }else {
                                roleMap.put(CORRECTOR_ROLE, CORRECTOR_ROLE_AI);
                            }
                            break;
                        }else {//如果老师批改的空和智能批改的空不为同一个空时，使用智能批改结果
                            resultMap.put(BLANK_INDEX, intellCorrectIndex);
                            resultMap.put(BLANK_RESULT, intellCorrectValue);
                            roleMap.put(BLANK_INDEX, intellCorrectIndex);
                            if(correctorRoleValue != null && !"".equals(correctorRoleValue) && correctorRoleValue.equals("teacher")) {
                                roleMap.put(CORRECTOR_ROLE, CORRECTOR_ROLE_TEACHER);
                            }else {
                                roleMap.put(CORRECTOR_ROLE, CORRECTOR_ROLE_AI);
                            }
                        }
                        break;*/
                        }
                    /*resultList.add(resultMap);
                    roleList.add(roleMap);*/
                        break;
                    }
                    resultList.add(resultMap);
                    roleList.add(roleMap);
                }
                entity.setResult(JsonUtil.obj2Json(resultList));
                entity.setCorrectorRole(JsonUtil.obj2Json(roleList));
                entity.setIntellResult(null);
            }else {
                for (Map<String, Object> intellCorrectResultMap : intellResultList) {
                    String intellCorrectIndex = intellCorrectResultMap.get(BLANK_INDEX) + "";
                    String intellCorrectValue = intellCorrectResultMap.get(BLANK_RESULT) + "";
                    Map resultMap = new HashMap(4);
                    Map roleMap = new HashMap(4);
                    for (Map<String, Object> correctResultMap : teacheResultList) {
                        String correctResult = correctResultMap.get(BLANK_RESULT) + "";
                        String correctIndex = correctResultMap.get(BLANK_INDEX) + "";
                        if (intellCorrectIndex.equals(correctIndex) && !StuAnswerConstant.ExerciseResult.SUBMITED.equals
                                (correctResult)) {
                            resultMap.put(BLANK_INDEX, intellCorrectIndex);
                            resultMap.put(BLANK_RESULT, correctResult);
                            roleMap.put(BLANK_INDEX, intellCorrectIndex);
                            //修改成ai
                            //roleMap.put(CORRECTOR_ROLE, StuAnswerConstant.CorrectorRole.TEACHER);
                            roleMap.put(CORRECTOR_ROLE, CORRECTOR_ROLE_AI);
                            break;
                        } else {//如果老师批改的空和智能批改的空不为同一个空时，使用智能批改结果
                            resultMap.put(BLANK_INDEX, intellCorrectIndex);
                            resultMap.put(BLANK_RESULT, intellCorrectValue);
                            roleMap.put(BLANK_INDEX, intellCorrectIndex);
                            roleMap.put(CORRECTOR_ROLE, CORRECTOR_ROLE_AI);
                        }
                    }
                    resultList.add(resultMap);
                    roleList.add(roleMap);
                }
                entity.setResult(JsonUtil.obj2Json(resultList));
                entity.setCorrectorRole(JsonUtil.obj2Json(roleList));
                entity.setIntellResult(null);
            }
        } else {
            //如果批改结果为提交未批改，直接使用智能批改结果
            if (StuAnswerConstant.ExerciseResult.SUBMITED.equals(teacheResult)) {
                entity.setResult(intellResult);
                entity.setCorrectorRole(CORRECTOR_ROLE_AI);
            } else {//否则以老师批改结果为准
                entity.setResult(teacheResult);
                entity.setCorrectorRole(entity.getCorrectorRole() != null ? entity.getCorrectorRole() :
                        StuAnswerConstant.CorrectorRole.TEACHER);
            }
        }

    }

    private static void assembleRoleResult(String teacheResult, String intellResult, FlowTurnList entity) {
        //如果批改为填空题批改
        if (entity.getStructId().equals(StuAnswerConstant.StructType.STRUCT_TKT)) {
            //老师批改结果解析
            List<Map<String, Object>> teacheResultList = JsonUtil.readValue(teacheResult, new
                    TypeReference<List<Map<String, Object>>>() {
            });
            //智能批改结果解析数据
            List<Map<String, Object>> intellResultList = JsonUtil.readValue(intellResult, new
                    TypeReference<List<Map<String, Object>>>() {
            });
            List<Map<String, Object>> resultList = new ArrayList<>();
            List<Map<String, Object>> roleList = new ArrayList<>();
            for (Map<String, Object> intellCorrectResultMap : intellResultList) {
                String intellCorrectIndex = intellCorrectResultMap.get(BLANK_INDEX) + "";
                String intellCorrectValue = intellCorrectResultMap.get(BLANK_RESULT) + "";
                Map resultMap = new HashMap(4);
                Map roleMap = new HashMap(4);
                for (Map<String, Object> correctResultMap : teacheResultList) {
                    String correctResult = correctResultMap.get(BLANK_RESULT) + "";
                    String correctIndex = correctResultMap.get(BLANK_INDEX) + "";
                    if (intellCorrectIndex.equals(correctIndex) && !StuAnswerConstant.ExerciseResult.SUBMITED.equals
                            (correctResult)) {
                        resultMap.put(BLANK_INDEX, intellCorrectIndex);
                        resultMap.put(BLANK_RESULT, correctResult);
                        roleMap.put(BLANK_INDEX, intellCorrectIndex);
                        roleMap.put(CORRECTOR_ROLE, StuAnswerConstant.CorrectorRole.TEACHER);
                        break;
                    } else {//如果老师批改的空和智能批改的空不为同一个空时，使用智能批改结果
                        resultMap.put(BLANK_INDEX, intellCorrectIndex);
                        resultMap.put(BLANK_RESULT, intellCorrectValue);
                        roleMap.put(BLANK_INDEX, intellCorrectIndex);
                        roleMap.put(CORRECTOR_ROLE, CORRECTOR_ROLE_AI);
                    }
                }
                resultList.add(resultMap);
                roleList.add(roleMap);
            }
            entity.setResult(JsonUtil.obj2Json(resultList));
            entity.setCorrectorRole(JsonUtil.obj2Json(roleList));
            entity.setIntellResult(null);

        } else {
            //如果批改结果为提交未批改，直接使用智能批改结果
            if (StuAnswerConstant.ExerciseResult.SUBMITED.equals(teacheResult)) {
                entity.setResult(intellResult);
                entity.setCorrectorRole(CORRECTOR_ROLE_AI);
            } else {//否则以老师批改结果为准
                entity.setResult(teacheResult);
                entity.setCorrectorRole(entity.getCorrectorRole() != null ? entity.getCorrectorRole() :
                        StuAnswerConstant.CorrectorRole.TEACHER);
            }
        }

    }


    /***
     * 如果所有的角色为老师，则ai为false，否则ai为true
     * @param exercise
     * @return
     */
    public static Boolean isRoleAllTeacher(StudentExercise exercise) {
        String correctorRole = exercise.getCorrectorRole();
        if (exercise.getStructId().equals(StuAnswerConstant.StructType.STRUCT_TKT)) {
            List<Map<String, Object>> roleList = JsonUtil.readValue(correctorRole, new TypeReference<List<Map<String,
                    Object>>>() {
            });
            Set roleSet = new HashSet();
            for (Map<String, Object> entity : roleList) {
                String roleName = entity.get(StuAnswerUtil.CORRECTOR_ROLE) + "";
                roleSet.add(roleName);
            }
            //false && teacher teacher      isAi(true)
            return !(roleSet.size() == 1 && roleSet.contains(StuAnswerConstant.CorrectorRole.TEACHER));
            //true && ai ai/teacher                 isAi(true)
            //true && teacher teacher               isAi(fasle)
            //true && teacher                       isAi(false)
            //true && ai                            isAi(true)
            //return roleSet.size() > 1  ? (roleSet.contains(StuAnswerConstant.CorrectorRole.AI)) :!(roleSet.size() == 1 && roleSet.contains(StuAnswerConstant.CorrectorRole.TEACHER));
        } else {//其他题型，老师批改了
            //ai                isAi(true)
            //ai teacher        isAi(true)
            //teacher teacher   isAi(fasle)
            return correctorRole.equals(CORRECTOR_ROLE_AI);

        }
    }
    /***
     * 如果所有的角色为老师，则ai为false，否则ai为true
     * @param exercise
     * @return
     */
    public static Boolean isRoleAllTeacher(FlowTurnList exercise) {
        String correctorRole = exercise.getCorrectorRole();
        if (exercise.getStructId().equals(StuAnswerConstant.StructType.STRUCT_TKT)) {
            List<Map<String, Object>> roleList = JsonUtil.readValue(correctorRole, new TypeReference<List<Map<String,
                    Object>>>() {
            });
            Set roleSet = new HashSet();
            for (Map<String, Object> entity : roleList) {
                String roleName = entity.get(StuAnswerUtil.CORRECTOR_ROLE) + "";
                roleSet.add(roleName);
            }
            return !(roleSet.size() == 1 && roleSet.contains(StuAnswerConstant.CorrectorRole.TEACHER));
        } else {//其他题型，老师批改了
            return correctorRole.equals(CORRECTOR_ROLE_AI);

        }
    }


    /***
     * 根据已有的习题查询是否存在智能批改结果
     * @param allSes
     * @return
     */
    public static List<StudentExercise> getIntellCorrectInfo(List<StudentExercise> allSes) {
        StudentExercise exercise = allSes.get(0);
        List<StudentExercise> resultList = new ArrayList<>();
        //智能批改
        if (StuAnswerUtil.isExerciseSource(exercise)) {
            for (StudentExercise entity : allSes) {
                /*String correctorRole = entity.getCorrectorRole();
                if(correctorRole != null && !"".equals(correctorRole)) {
                    if(correctorRole.contains("teacher")) {
                        entity.setAi(false);
                        resultList.add(entity);
                        continue;
                    }
                }*/
                //获取智能批改的value
                String intellCorrectResult = entity.getIntellResult();
                //获取智能批注的value
                String intellPostilResult = entity.getIntellPostil();
                logger.debug("getIntellCorrectInfo intellCorrectResult:" + intellCorrectResult + ";" +
                        "intellPostilResult:" + intellPostilResult);
                //如果智能批改数据不存在，智能批注信息不展示
                String currentResult = entity.getResult();
                /*
                 * 如果智能批改和智能批注都存在
                 */
                //简答题 和 作图题
                if (entity.getStructId().equals(StuAnswerConstant.StructType.STRUCT_JDT) || entity.getStructId()
                        .equals(StuAnswerConstant.StructType.STRUCT_ZTT)) {
                    //智能批改状态标识 0表示展示 1表示不展示
                    String dataStatus = entity.getIntellPostilStatus() + "";

                    //如果智能批改和批注都存在
                    if (null != intellCorrectResult && null != intellPostilResult) {
                        String[] resulrArray = intellCorrectResult.split("_");
                        //0表示智能批注需要显示
                        if (dataStatus.equals("0")) {
                            entity.setIntellPostil(intellPostilResult);
                            entity.setIntellResult(resulrArray[2]);
                            entity.setResult(resulrArray[2]);
                            entity.setCorrectorRole(CORRECTOR_ROLE_AI);
                            entity.setAi(true);
                        } else { //智能批改、批注的数据都不展示
                            entity.setIntellPostil(null);
                            entity.setIntellResult(null);
                            entity.setAi(false);
                        }
                    }//智能批改存在、智能批注不存在
                    else if (null != intellCorrectResult && null == intellPostilResult) {
                        if (dataStatus.equals("1")) {
                            entity.setIntellPostil(null);
                            entity.setIntellResult(null);
                            entity.setAi(false);
                        } else {
                            String[] resulrArray = intellCorrectResult.split("_");
                            entity.setAi(false);
                            entity.setIntellResult(resulrArray[2]);
                            entity.setResult(resulrArray[2]);
                            entity.setCorrectorRole(CORRECTOR_ROLE_AI);
                        }
                    }//智能批改不存在、智能批注存在
                    else if (null == intellCorrectResult && null != intellPostilResult) {
                        entity.setAi(false);
                        entity.setIntellPostil(null);
                    } else {
                        //智能批改和批注都不存在
                        entity.setAi(false);
                    }
                } else {//智能批改的场景
                    if (null != intellCorrectResult) {
                        String[] resulrArray = intellCorrectResult.split("_");
                        StuAnswerUtil.assembleRoleResult(currentResult, resulrArray[2], entity);
                        entity.setAi(StuAnswerUtil.isRoleAllTeacher(entity));
                    } else {
                        entity.setAi(false);
                    }

                }
                resultList.add(entity);
            }
        }else {
            resultList.add(exercise);
        }
        return resultList;
    }


    /***
     * 根据已有的习题查询是否存在智能批改结果
     * @param allSes
     * @return
     */
    public static List<FlowTurnList> getFTIntellCorrectInfo(List<FlowTurnList> allSes) {
        FlowTurnList exercise = allSes.get(0);
        List<FlowTurnList> resultList = new ArrayList<>();
        //智能批改
        if (StuAnswerUtil.isExerciseSource(exercise)) {
            for (FlowTurnList entity : allSes) {
                //获取智能批改的value
                String intellCorrectResult = entity.getIntellResult();
                //获取智能批注的value
                String intellPostilResult = entity.getIntellPostil();
                logger.debug("getIntellCorrectInfo intellCorrectResult:" + intellCorrectResult + ";" +
                        "intellPostilResult:" + intellPostilResult);
                //如果智能批改数据不存在，智能批注信息不展示
                String currentResult = entity.getResult();
                /*
                 * 如果智能批改和智能批注都存在
                 */
                if (entity.getStructId().equals(StuAnswerConstant.StructType.STRUCT_JDT) || entity.getStructId()
                        .equals(StuAnswerConstant.StructType.STRUCT_ZTT)) {
                    String dataStatus = entity.getIntellPostilStatus() + "";

                    //如果智能批改和批注都存在
                    if (null != intellCorrectResult && null != intellPostilResult) {
                        String[] resulrArray = intellCorrectResult.split("_");
                        //0表示智能批注需要显示
                        if (dataStatus.equals("0")) {
                            entity.setIntellPostil(intellPostilResult);
                            entity.setIntellResult(resulrArray[2]);
                            entity.setResult(resulrArray[2]);
                            entity.setCorrectorRole(CORRECTOR_ROLE_AI);
                            entity.setAi(true);
                        } else { //智能批改、批注的数据都不展示
                            entity.setIntellPostil(null);
                            entity.setIntellResult(null);
                            entity.setAi(false);
                        }
                    }//智能批改存在、智能批注不存在
                    else if (null != intellCorrectResult && null == intellPostilResult) {
                        if (dataStatus.equals("1")) {
                            entity.setIntellPostil(null);
                            entity.setIntellResult(null);
                            entity.setAi(false);
                        } else {
                            String[] resulrArray = intellCorrectResult.split("_");
                            entity.setAi(false);
                            entity.setIntellResult(resulrArray[2]);
                            entity.setResult(resulrArray[2]);
                            entity.setCorrectorRole(CORRECTOR_ROLE_AI);
                        }
                    }//智能批改不存在、智能批注存在
                    else if (null == intellCorrectResult && null != intellPostilResult) {
                        entity.setAi(false);
                        entity.setIntellPostil(null);
                    } else {
                        //智能批改和批注都不存在
                        entity.setAi(false);
                    }
                } else {//智能批改的场景
                    if (null != intellCorrectResult && !"".equals(intellCorrectResult)) {
                        String[] resulrArray = intellCorrectResult.split("_");
                        StuAnswerUtil.assembleRoleResult(currentResult, resulrArray[2], entity);
                        entity.setAi(StuAnswerUtil.isRoleAllTeacher(entity));
                    } else {
                        entity.setAi(false);
                    }

                }
                resultList.add(entity);
            }
        }
        return resultList;
    }


    public static List<StudentExercise> getFromCacheStuBatch(StudentExercise se, RedisUtil redisUtil) {
        List<StudentExercise> resultList = new ArrayList<>();
        if (StuAnswerUtil.isExerciseSource(se)) {   //任务场景
            String redisKey = StuAnswerUtil.getRedisKeyOfStudentExercise(se);
            Set<String> keys = redisUtil.smembers(redisKey);
            List<StudentExercise> cacheList = getSeListByKeys(keys, redisUtil);
            for (StudentExercise ser : cacheList) {
                //是否过滤题型结构
                if (CollectionUtils.isNotEmpty(se.getStructIdList())) {
                    if (se.getStructIdList().contains(ser.getStructId())) {
                        resultList.add(ser);
                    }
                } else {
                    resultList.add(ser);
                }
            }
        }
        return resultList;
    }


    public static List<StudentExercise> getSeListByKeys(Set<String> keys, RedisUtil redisUtil) {
        List<StudentExercise> resultList = new ArrayList<>();
        if (CollectionUtils.isEmpty(keys)) {
            return resultList;
        }
        //通过pipleLine 批量获取数据，容易造成redis线程阻塞，解决方法：控制取数据的量级，达到高效的目的
        List<List<String>> dataList = BatchUpdateCorrectUtil.spliceArrays(new ArrayList<>(keys), 10);
        for (List<String> data : dataList) {
            Set<String> staffsSet = new HashSet<>(data);
            Set<StudentExercise> dataMap = redisUtil.viewGetSet(staffsSet, StudentExercise.class);
            resultList.addAll(new ArrayList<>(dataMap));
        }
        return resultList;
    }


    public static List<StudentExercise> getFromCacheQeusesBatch(StudentExercise se, RedisUtil redisUtil) {
        List<StudentExercise> resultList = new ArrayList<>();
        if (StuAnswerUtil.isExerciseSource(se)) {
            List<Long> questionIdList = se.getQuestionIdList();
            if (CollectionUtils.isNotEmpty(questionIdList)) {
                for (Long quesId : questionIdList) {
                    se.setQuestionId(quesId);
                    String redisKey = StuAnswerUtil.getRedisKeyOfStudentExercise(se);
                    Set<String> keys = redisUtil.smembers(redisKey);
                    List<StudentExercise> cacheList = getSeListByKeys(keys, redisUtil);
                    //是否过滤题型结构
                    if (CollectionUtils.isNotEmpty(se.getStructIdList())) {
                        for (StudentExercise ser : cacheList) {
                            if (se.getStructIdList().contains(ser.getStructId())) {
                                resultList.add(ser);
                            }
                        }
                    } else {
                        resultList.addAll(cacheList);
                    }
                }
            }

        }
        return resultList;
    }

    public static boolean isExerciseSource(StudentExercise se) {
        return StringUtils.equals(se.getExerciseSource(), StuAnswerConstant.ExerciseSource.LESSON) || StringUtils
                .equals(se.getExerciseSource(), StuAnswerConstant.ExerciseSource.HO_WORK) || StringUtils.equals(se
                .getExerciseSource(), StuAnswerConstant.ExerciseSource.EVALUATION) || StringUtils.equals(se
                .getExerciseSource(), StuAnswerConstant.ExerciseSource.PREVIEW);

    }

    public static boolean isExerciseSource(FlowTurnList se) {
        return StringUtils.equals(se.getExerciseSource(), StuAnswerConstant.ExerciseSource.LESSON) || StringUtils
                .equals(se.getExerciseSource(), StuAnswerConstant.ExerciseSource.HO_WORK) || StringUtils.equals(se
                .getExerciseSource(), StuAnswerConstant.ExerciseSource.EVALUATION) || StringUtils.equals(se
                .getExerciseSource(), StuAnswerConstant.ExerciseSource.PREVIEW);

    }

    /**
     * 判断2个时间相差多多少小时
     *
     * @param endDate 开始时间<br>
     * @param nowDate 结束时间<br>
     * @return String 计算结果<br>
     * @Exception 发生异常<br>
     */
    public static long getTimeDiff(Date endDate, Date nowDate) {
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        if (null != endDate) {
            long diff = nowDate.getTime() - endDate.getTime();
            long day = diff / nd;
            long hour = 0;
            if (day > 0) {
                hour = 24 * day;
            }
            hour = diff % nd / nh + hour;
            return hour;

        }
        return 0;
    }

    public static int getSecondsDiff(Date endDate, Date nowDate) {
        long maxTime = REDIS_EXPIRE_SECONDS;
        long nd = 1000;
        if (null != endDate) {
            long diff = nowDate.getTime() - endDate.getTime();
            long seconds = diff / nd;
            if (maxTime - seconds > 0) {
                return (int) (maxTime - seconds);
            } else {
                return 0;
            }
        } else {
            return (int) maxTime;
        }
    }

    public static StudentExercise getDataFromRedis(StudentExercise exercise, RedisUtil redisUtil) {
        String studentExerciseRedisKey = StuAnswerUtil.getRedisKeyOfStudentExercise(exercise);
        return redisUtil.viewGet(studentExerciseRedisKey, StudentExercise.class);
    }


    /**
     * 获取做答记录的year和classid
     * 分支表的 year 和 class 字段
     * 根据答题场景和题集ID查询分表的year和class
     *
     * @param se
     */
    public static void setShardKey(StudentExercise se, RedisUtil redisUtil, StudentWorkDao studentWorkDao,
                                   LessonService lessonService, TrailCountService trailCountService) {
        long start1 = System.currentTimeMillis();
        try {
            String exerciseSource = se.getExerciseSource();
            Long resourceId = se.getResourceId();
            StuAnswerUtil.QuizSetType quizSetType = StuAnswerUtil.getQuizSetType(exerciseSource);
            //如果答题场景为自主练习，则通过查询entity_student_work表
            boolean flag = quizSetType == StuAnswerUtil.QuizSetType.PUBLISH && (se.getYear() == null || se.getClassId
                    () == null);
            if (quizSetType == StuAnswerUtil.QuizSetType.OWNER && se.getYear() == null) {
                StudentWork work = studentWorkDao.findById(resourceId);
                se.setYear(work.getYear());
            }
            //通过班级发布 先查询redis缓存，如果没有则通过调用lesson服务获取对应的学年和班级ID
            else if (flag) {
                //_shardKeyStore_
                String yearAndClass = (String) redisUtil.get(SHARD_KEY + se.getResourceId());
                //year 数据 增加使用缓存
                Integer year;
                Long klass;
                if (yearAndClass != null && yearAndClass.indexOf(",") > 0) {
                    String[] split = yearAndClass.split(",", 2);
                    year = Integer.parseInt(split[0]);
                    klass = Long.parseLong(split[1]);
                } else {
                    ResourceParam req = new ResourceParam(se.getResourceId());
                    CommonResponse<LinkResourceWithPublishInfo> response = lessonService.fetchResourcePublicByLinkId
                            (req);
                    logger.debug("setShardKey json data req  :" + se.getResourceId() + " res json " + "data:" +
                            JsonUtil.obj2Json(response));
                    if (response.success() && response.getData() != null) {
                        Integer dataFlag = response.getData().getResPackagePublish().getFlag();
                        //'0:默认(老数据） 1：新数据   兼容老数据，新数据入库都是存储在题集创建的年份
                        if (dataFlag == 1) {
                            year = Integer.valueOf(getYear(response.getData().getResPackagePublish().getCreateTime()));
                        } else {
                            year = response.getData().getLinkPublishResource().getYear();
                        }
                        klass = response.getData().getResPackagePublish() != null ? response.getData()
                                .getResPackagePublish().getClassId() : getClassIdForCancelCourse(se, trailCountService);
                        redisUtil.set(SHARD_KEY + se.getResourceId(), year + "," + klass, SHARD_KEY_EXPIRE);
                    } else {
                        throw new BizLayerException("lesson:" + response.getCode() + "," + response.getMessage() + ";" +
                                "" + "" + "" + "" + "" + "" + "" + "", ANSWER_PUBLISH_RESOURCE_ID_NOT_EXIST);
                    }
                }
                se.setYear(year);
                se.setClassId(klass);
            }
        } catch (Exception e) {
            logger.error("获取学生的学年和班级ID失败,失败的原因是:", e);
            throw new BizLayerException("", PraxisErrorCode.STUDENT_YEAR_CLASS_EXCEPTION);

        }
        logger.info("setShardKey 花费时间:{}ms", System.currentTimeMillis() - start1);
    }


    public static Long getClassIdForCancelCourse(StudentExercise se, TrailCountService trailCountService) {
        if (WrongUtil.isChal(se.getExerciseSource())) {
            //教师取消课程后，已经生成的错题无法从lesson服务查询到班级信息，暂时从统计服务查询班级信息
            Long linkId = se.getResourceId();
            TrailCountServiceRequest request = new TrailCountServiceRequest();
            request.setStudentId(se.getStudentId());
            request.setHistoryId(linkId);
            request.setExerciseType(Integer.valueOf(se.getRedoSource()));
            CommonResponse<List<TrailCount>> trailCountByHistoryId = trailCountService.findTrailCountByHistoryId
                    (request);
            if (trailCountByHistoryId.success() && CollectionUtils.isNotEmpty(trailCountByHistoryId.getData())) {
                return trailCountByHistoryId.getData().get(0).getClassId();
            } else {
                throw new BizLayerException("trail error:", ANSWER_PUBLISH_RESOURCE_ID_NOT_EXIST);
            }
        } else {
            throw new BizLayerException("unknow source error:", ANSWER_PUBLISH_RESOURCE_ID_NOT_EXIST);
        }
    }

    public static Long getRedisKeyTtl(RedisUtil redisUtil, StudentExercise studentExercise) {
        StudentExercise exercise = new StudentExercise();
        BeanUtils.copyProperties(studentExercise, exercise);
        exercise.setStudentId(null);
        exercise.setQuestionId(null);
        String key = getRedisKeyOfStudentExercise(exercise);
        Long remianTime = redisUtil.ttl(key);
        if (remianTime > 0L) {
            return remianTime;
        } else {
            return null;
        }
    }

    public static Long getDetailRedisKeyTtl(RedisUtil redisUtil, StudentExercise studentExercise) {
        StudentExercise exercise = new StudentExercise();
        BeanUtils.copyProperties(studentExercise, exercise);
        String key = getRedisKeyOfStudentExercise(exercise);
        Long remianTime = redisUtil.ttl(key);
        if (remianTime > 0L) {
            return remianTime;
        } else {
            return null;
        }
    }


    public static void setTtl(RedisUtil redisUtil, String redisKey, int timeFlag) {
        if (redisKey.contains("_*_*")) {
            Long commonTimeFlag = redisUtil.ttl(redisKey);
            if (commonTimeFlag > 0L) {
                redisUtil.expire(redisKey, commonTimeFlag.intValue());
            } else {
                redisUtil.expire(redisKey, timeFlag);
            }
        } else {
            redisUtil.expire(redisKey, timeFlag);
        }

    }

    public static void delRedisKey(RedisUtil redisUtil, StudentExercise studentExercise) {
        String commonKey = STUDENT_EXERCISE_REDIS_KEY_PREFIXX + studentExercise.getExerciseSource() + "_" +
                studentExercise.getResourceId() + "_*_*";
        if (redisUtil.exists(commonKey)) {
            String questionKey = STUDENT_EXERCISE_REDIS_KEY_PREFIXX + studentExercise.getExerciseSource() + "_" +
                    studentExercise.getResourceId() + "_*_" + studentExercise.getQuestionId();
            String studentKey = STUDENT_EXERCISE_REDIS_KEY_PREFIXX + studentExercise.getExerciseSource() + "_" +
                    studentExercise.getResourceId() + "_" + studentExercise.getStudentId() + "_*";
            redisUtil.del(commonKey, questionKey, studentKey);
        }
    }


    public static String getYear(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        if (null == date) {
            date = new Date();
        }
        // Date date = new Date();
        return sdf.format(date);
    }


    public static void sortByParentSubQues(List<StudentExercise> allSes, QuestionService questionService) {
        List<Long> parentQuesIds = getParentQuesIds(allSes);
        if (CollectionUtils.isNotEmpty(parentQuesIds)) {
            FindLeafQuesIdsByParentIdsRequest req = new FindLeafQuesIdsByParentIdsRequest();
            req.setParentQuesIds(parentQuesIds);
            FindParentIdLeafQuesIdsMapResponse parentIdLeafQuesIdsMapResponse = questionService
                    .findParentIdLeafQuesIdsMap(req);
            if (parentIdLeafQuesIdsMapResponse.success()) {
                Map<Long, List<Long>> parentIdLeafQuesIdsMap = parentIdLeafQuesIdsMapResponse
                        .getParentIdLeafQuesIdsMap();
                if (parentIdLeafQuesIdsMap.size() > 0) {
                    sortByParentSubQues(parentQuesIds, parentIdLeafQuesIdsMap, allSes);
                }
            }
        }
    }

    public static void sortByParentSubQuesFlowTurn(List<FlowTurnList> allSes, QuestionService questionService) {
        List<Long> parentQuesIds = getParentQuesIdsFlowTurn(allSes);
        if (CollectionUtils.isNotEmpty(parentQuesIds)) {
            FindLeafQuesIdsByParentIdsRequest req = new FindLeafQuesIdsByParentIdsRequest();
            req.setParentQuesIds(parentQuesIds);
            FindParentIdLeafQuesIdsMapResponse parentIdLeafQuesIdsMapResponse = questionService
                    .findParentIdLeafQuesIdsMap(req);
            if (parentIdLeafQuesIdsMapResponse.success()) {
                Map<Long, List<Long>> parentIdLeafQuesIdsMap = parentIdLeafQuesIdsMapResponse
                        .getParentIdLeafQuesIdsMap();
                if (parentIdLeafQuesIdsMap.size() > 0) {
                    sortByParentSubQuesFlowTurn(parentQuesIds, parentIdLeafQuesIdsMap, allSes);
                }
            }
        }
    }

    private static List<Long> getParentQuesIds(List<StudentExercise> allSes) {
        List<Long> ids = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(allSes)) {
            for (StudentExercise se : allSes) {
                if (se.getParentQuestionId() != null) {
                    ids.add(se.getParentQuestionId());
                }
            }
        }
        return ids;
    }

    private static List<Long> getParentQuesIdsFlowTurn(List<FlowTurnList> allSes) {
        List<Long> ids = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(allSes)) {
            for (FlowTurnList se : allSes) {
                if (se.getParentQuestionId() != null) {
                    ids.add(se.getParentQuestionId());
                }
            }
        }
        return ids;
    }

    private static void sortByParentSubQues(List<Long> parentQuesIds, Map<Long, List<Long>> parentIdLeafQuesIdsMap,
                                            List<StudentExercise> allSes) {
        Collections.sort(allSes, new ParentSubQuesIdComparator(parentQuesIds, parentIdLeafQuesIdsMap));
    }

    private static void sortByParentSubQuesFlowTurn(List<Long> parentQuesIds, Map<Long, List<Long>> parentIdLeafQuesIdsMap,
                                            List<FlowTurnList> allSes) {
        Collections.sort(allSes, new ParentSubQuesIdComparatorFlowTurn(parentQuesIds, parentIdLeafQuesIdsMap));
    }

   private static class ParentSubQuesIdComparator implements Comparator<StudentExercise> {
        private List<Long> parentQuesIds;
        Map<Long, List<Long>> parentIdLeafQuesIdsMap;

        public ParentSubQuesIdComparator(List<Long> parentQuesIds, Map<Long, List<Long>> parentIdLeafQuesIdsMap) {
            this.parentQuesIds = parentQuesIds;
            this.parentIdLeafQuesIdsMap = parentIdLeafQuesIdsMap;
        }

        @Override
        public int compare(StudentExercise se1, StudentExercise se2) {
            if (se1 != null && se2 != null) {
                Long parentQuestionId1 = se1.getParentQuestionId();
                Long parentQuestionId2 = se2.getParentQuestionId();

                int p1 = parentQuesIds.indexOf(parentQuestionId1);
                int p2 = parentQuesIds.indexOf(parentQuestionId2);
                if (p1 < p2) {
                    return -1;
                } else if (p1 > p2) {
                    return 1;
                } else {
                    List<Long> sonList = parentIdLeafQuesIdsMap.get(parentQuestionId1);
                    if (sonList != null) {
                        return getCompareInt(sonList, se1.getQuestionId(), se2.getQuestionId());
                    }
                    return 0;
                }
            }
            return 0;
        }
    }

   private static class ParentSubQuesIdComparatorFlowTurn implements Comparator<FlowTurnList> {
        private List<Long> parentQuesIds;
        Map<Long, List<Long>> parentIdLeafQuesIdsMap;

        public ParentSubQuesIdComparatorFlowTurn(List<Long> parentQuesIds, Map<Long, List<Long>> parentIdLeafQuesIdsMap) {
            this.parentQuesIds = parentQuesIds;
            this.parentIdLeafQuesIdsMap = parentIdLeafQuesIdsMap;
        }

        @Override
        public int compare(FlowTurnList se1, FlowTurnList se2) {
            if (se1 != null && se2 != null) {
                Long parentQuestionId1 = se1.getParentQuestionId();
                Long parentQuestionId2 = se2.getParentQuestionId();

                int p1 = parentQuesIds.indexOf(parentQuestionId1);
                int p2 = parentQuesIds.indexOf(parentQuestionId2);
                if (p1 < p2) {
                    return -1;
                } else if (p1 > p2) {
                    return 1;
                } else {
                    List<Long> sonList = parentIdLeafQuesIdsMap.get(parentQuestionId1);
                    if (sonList != null) {
                        return getCompareInt(sonList, se1.getQuestionId(), se2.getQuestionId());
                    }
                    return 0;
                }
            }
            return 0;
        }
    }

    private static int getCompareInt(List<Long> sonList, Long questionId1, Long questionId2) {

        int i1 = sonList.indexOf(questionId1);
        int i2 = sonList.indexOf(questionId2);
        if (i1 < i2) {
            return -1;
        } else if (i1 > i2) {
            return 1;
        } else {
            return 0;
        }
    }

    private static void flowTurnCorrect(List<Long> parentQuesIds, Map<Long, List<Long>> parentIdLeafQuesIdsMap,
                                            List<StudentExercise> allSes) {
        Collections.sort(allSes, new ParentSubQuesIdComparator(parentQuesIds, parentIdLeafQuesIdsMap));
    }

    public static void updateFlowTurnCorrectState(StudentExercise se) {
        String correctorRole = se.getCorrectorRole();
        if(correctorRole != null && !"".equals(correctorRole)) {
            if("teacher".equals(correctorRole) || "ai".equals(correctorRole) || correctorRole.contains("teacher") || correctorRole.contains("ai")) {
                se.setExtraFlag(1);
            }else {
                se.setExtraFlag(0);
            }
        }
    }


    public static String getRedisKeyOfFlowTurnList(FlowTurnCorrectRequest in) {

        String systemId = String.valueOf(in.getSystemId());

        Long catalogGroupId = in.getCatalogGroupId();
        String catalogGroupIdStr = String.valueOf(catalogGroupId);
        if (catalogGroupId == null) {
            catalogGroupIdStr = "*";
        }

        Integer isCorrected = in.getIs_corrected();
        String isCorrectedStr = String.valueOf(isCorrected);
        if (isCorrected == null) {
            isCorrectedStr = "*";
        }

        Long catalogIdFirst = in.getCatalogIdFirst();
        String catalogIdFirstStr = String.valueOf(catalogIdFirst);
        if (catalogIdFirst == null) {
            catalogIdFirstStr = "*";
        }

        Long catalogIdSecond = in.getCatalogIdSecond();
        String catalogIdSecondStr = String.valueOf(catalogIdSecond);
        if (catalogIdSecond == null) {
            catalogIdSecondStr = "*";
        }

        Long catalogId = in.getCatalogId();
        String catalogIdStr = String.valueOf(catalogId);
        if (catalogId == null) {
            catalogIdStr = "*";
        }

        String pageStr;
        PageBounds pageBounds = in.getPageBounds();
        if(pageBounds == null) {
            pageStr = "*";
        }else {
            Integer page = pageBounds.getPage();
            pageStr = String.valueOf(page);
            if (page == null) {
                pageStr = "*";
            }
        }

        return FLOW_TURN_REDIS_KEY_PREFIXX + systemId + FLOW_TURN_REDIS_KEY_SPLIT + catalogGroupIdStr + FLOW_TURN_REDIS_KEY_SPLIT + isCorrectedStr + FLOW_TURN_REDIS_KEY_SPLIT + pageStr
                + FLOW_TURN_REDIS_KEY_SPLIT + catalogIdStr + FLOW_TURN_REDIS_KEY_SPLIT
                + catalogIdSecondStr + FLOW_TURN_REDIS_KEY_SPLIT + catalogIdFirstStr;
    }

    public static String getRedisKeyOfFindFlowTurnStu(FlowTurn in) {

        String systemId = String.valueOf(in.getSystemId());
        Long questionId = in.getQuestionId();

        return FLOW_TURN_STU_REDIS_KEY_PREFIXX + systemId + FLOW_TURN_REDIS_KEY_SPLIT + questionId;
    }
}