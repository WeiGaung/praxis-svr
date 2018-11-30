package com.noriental.praxissvr.wrong.util;

import com.noriental.praxissvr.answer.bean.StudentExercise;
import com.noriental.praxissvr.answer.util.StuAnswerUtil;
import com.noriental.praxissvr.common.StuAnswerConstant;
import com.noriental.praxissvr.exception.PraxisErrorCode;
import com.noriental.praxissvr.utils.PraxisUtilForOther;
import com.noriental.praxissvr.wrong.bean.AnswerChal;
import com.noriental.praxissvr.wrong.service.AnswerChalService;
import com.noriental.praxissvr.wrong.vo.GetsonBatchIdParam;
import com.noriental.praxissvr.wrong.vo.GetsonBatchIdsParam;
import com.noriental.validate.exception.BizLayerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by bluesky on 2016/7/5.
 */
public class WrongUtil {
    public  static List<Long> getQuesIdsByList(List<AnswerChal> answerChalList){
        List<Long> ids = new ArrayList<>();
        for(AnswerChal ac : answerChalList){
            ids.add(ac.getQuestionId());
        }
        return  ids;
    }

    public static List<Long> getIdsByChals(List<AnswerChal> answerChalList) {
        List<Long> ids = new ArrayList<>();
        for(AnswerChal ac : answerChalList){
            ids.add(ac.getId());
        }
        return  ids;
    }

//    public static void convertSonBatchs(AnswerChalService answerChalService,StudentExercise se){
//        String exerciseSource = se.getExerciseSource();
//        if(StuAnswerConstant.ExerciseSource.WRONGBOOK.equals(exerciseSource)){
//            GetsonBatchIdsParam param = new GetsonBatchIdsParam();
//            param.setResourceId(se.getResourceId());
//            param.setStudentId(se.getStudentId());
//            param.setExerciseSource(se.getRedoSource());
//            List<Long> sonBatchIds = answerChalService.getSonBatchIds(param);
//            if(CollectionUtils.isEmpty(sonBatchIds)){
//                  throw new BizLayerException("", PraxisErrorCode.ANSWER_RECORD_SET_NOT_FOUND);
//            }
//            se.setResourceId(null);
//            se.setResourceIdList(sonBatchIds);
//        }
//    }




    public static boolean isChal(String exerciseSource) {
        return StuAnswerConstant.ExerciseSource.WRONGBOOK.equals(exerciseSource);
    }

    public static boolean isNeedCreatChalAgain(List<StudentExercise> seList){
        if (isNeedCreateAgain(seList)) {
            return true;
        }
       return  false;
    }
    private static boolean isNeedCreateAgain(List<StudentExercise> seList) {
        StudentExercise se = seList.get(0);
        if(se.getParentQuestionId()!=null){
            return hasWrongHasNotToCorrect(seList);
        }else{
            return StuAnswerUtil.isWrong(se);
        }
    }

    private static boolean hasWrongHasNotToCorrect(List<StudentExercise> seList) {
        boolean hasWrong = false;
        boolean hasToCorrect = false;
        for(StudentExercise se : seList){
            if(StuAnswerUtil.isWrong(se)){
               hasWrong = true;
            }
            if(isNotCorrect(se)){
                hasToCorrect = true;
            }
        }
        if(hasWrong&&!hasToCorrect){
            return true;
        }else{
            return false;
        }
    }
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static boolean isNotCorrect(StudentExercise se) {
        Integer structId = se.getStructId();
        String result = se.getResult();
        if(StuAnswerConstant.ExerciseResult.SUBMITED.equals(PraxisUtilForOther.getQuesResultTrail(structId,result))){
            return true;
        }
        return false;
    }

    public static  void  setRedoSouce(StudentExercise se1,StudentExercise se){
        String answerSource = se.getExerciseSource();
        if(StuAnswerConstant.ExerciseSource.WRONGBOOK.equals(answerSource)){
            se1.setRedoSource(se.getRedoSource());
        }else {
            se1.setRedoSource(se.getExerciseSource());
        }
    }
    public static  void  setExerciseSource(StudentExercise se1,StudentExercise se){
        String answerSource = se.getExerciseSource();
        if(StuAnswerConstant.ExerciseSource.WRONGBOOK.equals(answerSource)){
            se1.setExerciseSource(se.getRedoSource());
        }else {
            se1.setExerciseSource(se.getExerciseSource());
        }
    }

//    public static  Long  setSonBatchIds(AnswerChalService answerChalService,StudentExercise se){
//        String exerciseSource = se.getExerciseSource();
//        Long parentResourceId = se.getResourceId();
//        Long quesId = se.getParentQuestionId()==null?se.getQuestionId():se.getParentQuestionId();
//        if(isChal(exerciseSource)){
//            if(quesId!=null){
//                GetsonBatchIdParam param1 = new GetsonBatchIdParam();
//                param1.setQuestionId(quesId);
//                param1.setExerciseSource(se.getRedoSource());
//                param1.setResourceId(se.getResourceId());
//                param1.setStudentId(se.getStudentId());
//                Long sonBatchId = answerChalService.getSonBatchId(param1);
//                if(sonBatchId!=null){
//                    se.setResourceId(sonBatchId);
//                }
//            }else{
//                GetsonBatchIdsParam param = new GetsonBatchIdsParam();
//                param.setExerciseSource(se.getRedoSource());
//                param.setClassId(se.getClassId());
//                param.setStudentId(se.getStudentId());
//                param.setResourceId(se.getResourceId());
//                List<Long> sonBatchIds = answerChalService.getSonBatchIds(param);
//                if(CollectionUtils.isNotEmpty(sonBatchIds)){
//                    se.setResourceId(null);
//                    se.setResourceIdList(sonBatchIds);
//                }
//            }
//        }
//        return parentResourceId;
//    }

    public static  Long  setSonBatchId(AnswerChalService answerChalService,StudentExercise se){
        String exerciseSource = se.getExerciseSource();
        Long parentResourceId = se.getResourceId();
        if(isChal(exerciseSource)){
            GetsonBatchIdParam param = new GetsonBatchIdParam();
            param.setExerciseSource(se.getRedoSource());
            param.setStudentId(se.getStudentId());
            param.setResourceId(se.getResourceId());
            param.setQuestionId(se.getParentQuestionId()==null?se.getQuestionId():se.getParentQuestionId());
            Long sonBatchId = answerChalService.getSonBatchId(param);
            if(sonBatchId!=null){
                se.setResourceId(sonBatchId);
            }else{
                throw new BizLayerException("",PraxisErrorCode.ANSWER_CHAL_SET_NOT_EXIST);
            }
        }
        return parentResourceId;
    }

    public static List<Long> getQuesIdListExeptLeaf(List<StudentExercise> seList){
        List<Long> questionIdList = new ArrayList<>();
        for (StudentExercise answer : seList) {
            if (answer.getParentQuestionId() != null && answer.getParentQuestionId() > 0) {
                questionIdList.add(answer.getParentQuestionId());
            }else {
                questionIdList.add(answer.getQuestionId());
            }
        }
        questionIdList =  new ArrayList<>(new HashSet<>(questionIdList));
        return questionIdList;
    }

    public static void setParentBatchId(List<StudentExercise> studentExerciseList, Long parentResourceId) {
        for(StudentExercise se : studentExerciseList){
            se.setResourceId(parentResourceId);
        }
    }

    public static boolean isNeedCreateChal(String source){
        return StuAnswerUtil.isWrongQuesChalRecorded(source);
    }

    public static void setExerciseSource(GetsonBatchIdsParam param, StudentExercise se) {
        String exerciseSource = se.getExerciseSource();
        if(WrongUtil.isNeedCreateChal(exerciseSource)){
            param.setExerciseSource(se.getExerciseSource());
        }else if(WrongUtil.isChal(exerciseSource)){
            param.setExerciseSource(se.getRedoSource());
        }
    }

    public static void copySeList(List<StudentExercise> seList, List<StudentExercise> seList1) {
        for(StudentExercise se:seList){
            StudentExercise se1 = new StudentExercise();
            BeanUtils.copyProperties(se,se1);
            seList1.add(se1);
        }
    }

    public static void setOhterSeSonBatchId(StudentExercise studentExercise, List<StudentExercise> seList) {
        Long resourceId = studentExercise.getResourceId();
        for(StudentExercise se : seList){
            se.setResourceId(resourceId);
        }
    }
}
