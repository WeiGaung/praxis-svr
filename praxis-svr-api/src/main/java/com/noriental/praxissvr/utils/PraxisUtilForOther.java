package com.noriental.praxissvr.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.noriental.praxissvr.common.StuAnswerConstant;
import com.noriental.utils.json.JsonUtil;
import com.noriental.validate.exception.BizLayerException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.noriental.praxissvr.exception.PraxisErrorCode.ANSWER_PARAMETER_INVALID_RESULT_FORMAT;

/**
 * Created by bluesky on 2016/3/21.
 */
public class PraxisUtilForOther {
    public static final String BLANK_RESULT = "result";
    public static final String BLANK_INDEX = "index";
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * 统计判断单题批改状态：优先判断未批改
     * trail-svr调用
     *
     * @param structId
     * @param result
     * @return
     */
    public static String getQuesResultTrail(Integer structId, String result) {
        if (StuAnswerConstant.StructType.STRUCT_7x5.equals(structId)) {
            return getQuesResultOf7x5(result);
        } else if (StuAnswerConstant.StructType.STRUCT_TKT.equals(structId)) {
            return getQuesResultTrailTkt(result);
        } else {
            return result;
        }
    }

    /**
     * 统计使用的填空题批改状态规则：优先判断未批改
     * 一个空未批改，整道题为未批改；全部为对，为对；其它，为错。
     *
     * @param blankResult 填空题批改状态
     * @return 1正确 2错误 6未批改 7未作答
     */
    private static String getQuesResultTrailTkt(String blankResult) {

        if (StringUtils.isBlank(blankResult)) {
            return null;
        }
        List<Map<String, Object>> resultList;
        try {
            resultList = JsonUtil.readValue(blankResult, new TypeReference<List<Map<String, Object>>>() {
            });
        } catch (Exception e) {
            logger.error("转换异常", e);
            return null;
        }

        if (resultList == null || resultList.size() == 0) {
            return null;
        }

        boolean isRight = true;
        for (Map<String, Object> map : resultList) {
            Object subBlankResult = map.get(BLANK_RESULT);
            if (subBlankResult != null) {
                String subBlankResultStr = subBlankResult.toString();

                if (subBlankResultStr.equals(StuAnswerConstant.ExerciseResult.SUBMITED)) {
                    return StuAnswerConstant.ExerciseResult.SUBMITED;
                }

                if (!subBlankResultStr.equals(StuAnswerConstant.ExerciseResult.RIGHT)) {
                    isRight = false;
                }
            }
        }
        return isRight ? StuAnswerConstant.ExerciseResult.RIGHT : StuAnswerConstant.ExerciseResult.ERROR;


    }


    /**
     * 统计判断单题批改状态:优先判断错
     *
     * @param structId
     * @param result
     * @return
     */
    public static String getQuesResultWrongBook(Integer structId, String result) {
        if (StuAnswerConstant.StructType.STRUCT_7x5.equals(structId)) {
            return getQuesResultOf7x5(result);
        } else if (StuAnswerConstant.StructType.STRUCT_TKT.equals(structId)) {
            return getQuesResultWrongBookTkt(result);
        } else {
            return result;
        }
    }

    /**
     * 根据填空题每个空的result获得整个题目的result：优先判断错
     * 进入错题本的判断条件 / 错题挑战排序redoStatus也用到了该函数 / 统计错题挑战也用到了该函数
     * 一个填空是错或者一个空为作答，整个题目都是错；全部空正确，整个题目是正确；其余情况提交未批改
     *
     * @return 1正确 2错误
     */
    private static String getQuesResultWrongBookTkt(String blankResult) {

        if (StringUtils.isBlank(blankResult)) {
            return null;
        }

        List<Map<String, Object>> resultList = null;
        try {
            resultList = JsonUtil.readValue(blankResult, new TypeReference<List<Map<String, Object>>>() {
            });
        } catch (Exception e) {
            logger.error("转换异常", e);
        }

        if (resultList == null || resultList.size() == 0) {
            return null;
        }
        //1正确
        boolean isRight = true;
        //2错误5半错7未作答--按错误
        for (Map<String, Object> map : resultList) {
            Object subBlankResult = map.get(BLANK_RESULT);
            if (subBlankResult != null) {
                String subBlankResultStr = subBlankResult.toString();
                if (isWrong(subBlankResultStr)) {
                    return StuAnswerConstant.ExerciseResult.ERROR;
                }
                if (!subBlankResultStr.equals(StuAnswerConstant.ExerciseResult.RIGHT)) {
                    isRight = false;
                }
            }
        }

        if (isRight) {
            return StuAnswerConstant.ExerciseResult.RIGHT;
        }
        //        其余情况：  6提交未批改
        return StuAnswerConstant.ExerciseResult.SUBMITED;
    }

    /**
     * 7选5的题目的result
     * <p>
     * 7选5分值
     *
     * @param result,everyIndexScore
     * @return
     */

    public static Double getScoreQuesResultOf7x5(String result,Double totalScore) {
        Double score = new Double(0);
        Map<String,Double> result7x5Map = new HashedMap<>();
        if (StringUtils.isBlank(result)) {
            return null;
        }

        List<Object> resultList = null;
        try {
            resultList = JsonUtil.readValue(result, new TypeReference<List<Object>>() {
            });
            if (resultList == null) {
                logger.warn("转换异常:" + result);
                return null;
            }
        } catch (Exception e) {
            logger.error("转换异常", e);
            return null;
        }
        for(int i = 0; i < resultList.size(); i++) {
            Double everyIndexScore = totalScore / resultList.size();
            if (resultList.get(i).equals(StuAnswerConstant.ExerciseResult.RIGHT)) {
                score += everyIndexScore;
            }
        }
        return score;
    }
    /**
     * 7选5的题目的result
     * <p>
     * 7选5每小题的分值
     *
     * @param result,everyIndexScore
     * @return
     */

    public static Map<String, Double> getItemQuesResultOf7x5(String result,Double totalScore) {
        Map<String,Double> result7x5Map = new HashedMap<>();
        if (StringUtils.isBlank(result)) {
            return null;
        }

        List<Object> resultList = null;
        try {
            resultList = JsonUtil.readValue(result, new TypeReference<List<Object>>() {
            });
            if (resultList == null) {
                logger.warn("转换异常:" + result);
                return null;
            }
        } catch (Exception e) {
            logger.error("转换异常", e);
            return null;
        }
        for(int i = 0; i < resultList.size(); i++) {
            Double everyIndexScore = totalScore / resultList.size();
            if (!resultList.get(i).equals(StuAnswerConstant.ExerciseResult.RIGHT)) {
                result7x5Map.put(i+1 + "",new Double(0));
            }else {
                result7x5Map.put(i+1 + "",everyIndexScore);
            }
        }
        return result7x5Map;
    }

    /**
     * 7选5的题目的result
     * <p>
     * 全部为对，整个题目为对，否则为错。
     *
     * @param result
     * @return
     */
    public static String getQuesResultOf7x5(String result) {
        if (StringUtils.isBlank(result)) {
            return null;
        }

        List<Object> resultList = null;
        try {
            resultList = JsonUtil.readValue(result, new TypeReference<List<Object>>() {
            });
            if (resultList == null) {
                logger.warn("转换异常:" + result);
                return null;
            }
        } catch (Exception e) {
            logger.error("转换异常", e);
            return null;
        }
        boolean isRight = true;
        for (Object res : resultList) {

            if (!String.valueOf(res).equals(StuAnswerConstant.ExerciseResult.RIGHT)) {
                isRight = false;
            }
        }
        if (isRight) {
            return StuAnswerConstant.ExerciseResult.RIGHT;
        } else {
            return StuAnswerConstant.ExerciseResult.ERROR;
        }
    }

    public static String getQuesResultOf7x5In(String result) {
        return getQuesResultOf7x5(result);
    }

    public static final String CORRECTOR_ROLE = "corrector_role";

    public static String tktCorrectRoleTo12(String blankCorrectRole) {

        if (StringUtils.isBlank(blankCorrectRole)) {
            return blankCorrectRole;
        }

        List<Map<String, Object>> roleList = JsonUtil.readValue(blankCorrectRole, new TypeReference<List<Map<String,
                Object>>>() {
        });
        for (Map<String, Object> map : roleList) {
            Object subBlankRole = map.get(CORRECTOR_ROLE);
            if (subBlankRole != null) {
                if (StringUtils.equals(subBlankRole.toString(), StuAnswerConstant.CorrectorRole.STUDENT)) {
                    map.put(CORRECTOR_ROLE, StuAnswerConstant.CorrectorRole.STUDENT_NUM);
                } else if (StringUtils.equals(subBlankRole.toString(), StuAnswerConstant.CorrectorRole.TEACHER)) {
                    map.put(CORRECTOR_ROLE, StuAnswerConstant.CorrectorRole.TEACHER_NUM);
                }
            }
        }
        return JsonUtil.obj2Json(roleList);
    }

    public static String normalCorrectRoleTo12(String correctRole) {
        if (StringUtils.equals(correctRole, StuAnswerConstant.CorrectorRole.STUDENT)) {
            return StuAnswerConstant.CorrectorRole.STUDENT_NUM;
        } else if (StringUtils.equals(correctRole, StuAnswerConstant.CorrectorRole.TEACHER)) {
            return StuAnswerConstant.CorrectorRole.TEACHER_NUM;
        } else {
            return correctRole;
        }
    }

    public static boolean isWrong(String result) {
        return StuAnswerConstant.ExerciseResult.NOANSWER.equals(result) || StuAnswerConstant.ExerciseResult.ERROR
                .equals(result) || StuAnswerConstant.ExerciseResult.HALFRIGHT.equals(result);
    }


    public static boolean isAbsolutelyWrong(String result) {
        return StuAnswerConstant.ExerciseResult.NOANSWER.equals(result) || StuAnswerConstant.ExerciseResult.ERROR
                .equals(result) ;
    }

    /**
     * 按照数组获取批改状态
     *
     * @param structId
     * @param result
     * @return
     */
    public static String[] getResultArray(Integer structId, String result) {
        if (StuAnswerConstant.StructType.STRUCT_7x5.equals(structId)) {
            return getResults7x5(result);
        } else if (StuAnswerConstant.StructType.STRUCT_TKT.equals(structId)) {
            return getBlankResults(result);
        } else {
            return new String[0];
        }

    }

    /**
     * 填空题按照数组获取批改状态
     *
     * @param blankResult
     * @return null or eg:[1,2,6,7]
     */
    public static String[] getBlankResults(String blankResult) {

        if (StringUtils.isBlank(blankResult)) {
            return null;
        }
        List<Map<String, Object>> resultList = null;
        try {
            resultList = JsonUtil.readValue(blankResult, new TypeReference<List<Map<String, Object>>>() {
            });
        } catch (Exception e) {
            logger.error("转换异常", e);
            return null;
        }

        if (resultList == null || resultList.size() == 0) {
            return null;
        }

        int length = resultList.size();
        String[] results = new String[length];
        for (int j = 0; j < resultList.size(); j++) {
            Map<String, Object> map = resultList.get(j);
            Object subBlankResult = map.get(BLANK_RESULT);
            Object subBlankIndex = map.get(BLANK_INDEX);

            String subBlankResultStr = StuAnswerConstant.ExerciseResult.NOANSWER;
            if (subBlankResult != null) {
                subBlankResultStr = subBlankResult.toString();
            }

            Integer subBlankIndexInteger = 1;
            try {
                subBlankIndexInteger = Integer.valueOf(subBlankIndex.toString());
            } catch (Exception e) {
                logger.error("转换异常", e);
            }
            if ((subBlankIndexInteger - 1) >= 0) {
                results[subBlankIndexInteger - 1] = subBlankResultStr;
            } else {
                results[0] = subBlankResultStr;
            }
        }
        return results;
    }

    /**
     * 7x5按照数组获取批改状态
     *
     * @return null or eg:[1,2,6,7]
     */
    private static String[] getResults7x5(String result) {
        List<String> resultsOf7x5Exe = getResultsOf7x5Exe(result);
        return resultsOf7x5Exe.toArray(new String[resultsOf7x5Exe.size()]);
    }


    private static List<String> getBlankResultsExe(String result) {

        if (StringUtils.isBlank(result)) {
            throw new BizLayerException("result:" + result, ANSWER_PARAMETER_INVALID_RESULT_FORMAT);
        }

        List<Map> resultList = convertListExe(result, Map.class);

        List<String> results = new ArrayList<>();
        for (int j = 0; j < resultList.size(); j++) {
            Map<String, Object> map = resultList.get(j);
            Object subBlankResult = map.get(BLANK_RESULT);
            if (subBlankResult == null) {
                throw new BizLayerException("result:" + result, ANSWER_PARAMETER_INVALID_RESULT_FORMAT);
            }
            results.add(String.valueOf(subBlankResult));
        }
        return results;
    }

    private static List<String> getResultsOf7x5Exe(String result) {
        List<String> results = new ArrayList<>();

        List<Object> resultList = convertListExe(result, Object.class);

        for (Object res : resultList) {
            results.add(String.valueOf(res));
        }

        return results;
    }

    private static <T> List<T> convertListExe(String result, Class<T> t) {
        List<T> resultList = null;
        try {
            resultList = JsonUtil.readValue(result, new TypeReference<List<T>>() {
            });
            if (CollectionUtils.isEmpty(resultList)) {
                throw new BizLayerException("result:" + result, ANSWER_PARAMETER_INVALID_RESULT_FORMAT);
            }
        } catch (Exception e) {
            logger.error("转换异常", e);
            throw new BizLayerException("result:" + result, ANSWER_PARAMETER_INVALID_RESULT_FORMAT);
        }
        return resultList;
    }

    public static List<String> getResultsThr(Integer structId, String result) {
        if (StuAnswerConstant.StructType.STRUCT_7x5.equals(structId)) {
            List<String> resultsOf7x5 = getResultsOf7x5Exe(result);
            return resultsOf7x5;
        } else if (StuAnswerConstant.StructType.STRUCT_TKT.equals(structId)) {
            List<String> results = getBlankResultsExe(result);
            return results;
        } else {
            return Arrays.asList(result);
        }
    }
}
