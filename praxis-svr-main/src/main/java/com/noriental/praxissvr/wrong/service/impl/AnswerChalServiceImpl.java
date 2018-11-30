package com.noriental.praxissvr.wrong.service.impl;

import com.noriental.praxissvr.answer.bean.CorrectOperateType;
import com.noriental.praxissvr.answer.bean.StudentExercise;
import com.noriental.praxissvr.answer.dao.StudentExerciseDao;
import com.noriental.praxissvr.answer.service.StudentExerciseService;
import com.noriental.praxissvr.answer.util.StuAnswerUtil;
import com.noriental.praxissvr.common.StuAnswerConstant;
import com.noriental.praxissvr.exception.PraxisErrorCode;
import com.noriental.praxissvr.question.request.FindLeafQuesIdsByParentIdsRequest;
import com.noriental.praxissvr.question.response.FindLeafQuesIdsByParentIdsResponse;
import com.noriental.praxissvr.question.service.QuestionService;
import com.noriental.praxissvr.utils.PraxisUtilForOther;
import com.noriental.praxissvr.wrong.bean.AnswerChal;
import com.noriental.praxissvr.wrong.dao.AnswerChalDao;
import com.noriental.praxissvr.wrong.service.AnswerChalService;
import com.noriental.praxissvr.wrong.service.StuRedoTempService;
import com.noriental.praxissvr.wrong.util.WrongUtil;
import com.noriental.praxissvr.wrong.vo.GetsonBatchIdParam;
import com.noriental.praxissvr.wrong.vo.GetsonBatchIdsParam;
import com.noriental.praxissvr.wrong.vo.OperateType;
import com.noriental.utils.json.JsonUtil;
import com.noriental.validate.exception.BizLayerException;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by bluesky on 2016/7/5.
 */
@Service
@Deprecated
public class AnswerChalServiceImpl implements AnswerChalService {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @Autowired
    AnswerChalDao answerChalDao;
    @Autowired
    StuRedoTempService stuRedoTempService;
    @Autowired
    StudentExerciseService stuExerService;
    @Autowired
    private StudentExerciseService stuExeService;
    @Autowired
    private StudentExerciseDao studentExerciseDao;
    @Autowired
    private QuestionService questionService_v2;

    /**
     * 上课作业测评生成错题消灭。会生成错题消灭记录，和答题记录
     *
     * @param seList
     * @param operateType
     */
    private void createAnswerChalsFirst(List<StudentExercise> seList, OperateType operateType) {
        StudentExercise se = seList.get(0);
        Long prarentBatchId = se.getResourceId();
        List<StudentExercise> seList2;
        Long resourceId = null;
        if (CollectionUtils.isEmpty(seList2 = getWrongList(seList))) {
            resourceId = corectWright(se, operateType, seList);
            return;
        }
        List<Long> quesIds = WrongUtil.getQuesIdListExeptLeaf(seList2);
        logger.debug("wrong ques ids:" + quesIds);
        AnswerChal answerChal = getAnswerChal(se, quesIds);
        answerChal.setExerciseSource(se.getExerciseSource());
        answerChal.setFlag("1");
        List<AnswerChal> existChals = answerChalDao.findByAnswerChal(answerChal);
        List<Long> existIds = WrongUtil.getQuesIdsByList(existChals);
        List<Long> notExistIds = new ArrayList<>(CollectionUtils.removeAll(quesIds, existIds));
        logger.debug("notExist ques ids in master table:" + notExistIds);
        if (CollectionUtils.isNotEmpty(notExistIds)) {
            List<AnswerChal> answerChals = convertAnswerChals(se, notExistIds);
            setExerciseSource(se.getExerciseSource(), answerChals);
            answerChalDao.creates(answerChals);
            List<StudentExercise> seList1 = new ArrayList<>();
            WrongUtil.copySeList(seList, seList1);
            if (operateType == OperateType.ANSWER) {
                stuRedoTempService.createRedoStuExes(seList1);
            } else if (operateType == OperateType.CORRECT) {
                stuRedoTempService.createRedoStuExesCorrect(seList1.get(0), this, prarentBatchId);
            }
        } else {//对已经是错题重复批改状态的修改
            if (operateType == OperateType.CORRECT) {
                StudentExercise se1 = new StudentExercise();
                se1.setQuestionId(se.getQuestionId());
                se1.setResourceId(resourceId);
                se1.setStudentId(se.getStudentId());
                se1.setExerciseSource(StuAnswerConstant.ExerciseSource.WRONGBOOK);
                String redoStatus = PraxisUtilForOther.getQuesResultWrongBook(se.getStructId(), se.getResult());
                se1.setRedoStatus(redoStatus);
                se1.setClassId(se.getClassId());
                se1.setYear(se.getYear());
                logger.info("createRedoStuExesCorrect 重复批改为错的数据请求参数:" + JsonUtil.obj2Json(se1));
                studentExerciseDao.updateRepeatCorrect(se1);
            }

        }
    }


    /**
     * 重复批改为对的情况数据移出操作
     *
     * @param se
     * @param operateType
     * @param seList
     * @return
     */
    public Long corectWright(StudentExercise se, OperateType operateType, List<StudentExercise> seList) {
        //批改重复批改错题挑战里面的错题删除
        if (OperateType.CORRECT.equals(operateType)) {
            List<Long> quesIds = WrongUtil.getQuesIdListExeptLeaf(seList);
            AnswerChal answerChal = getAnswerChal(se, quesIds);
            answerChal.setExerciseSource(se.getExerciseSource());
            answerChal.setFlag("1");
            List<AnswerChal> existChals = answerChalDao.findByAnswerChal(answerChal);
            if (CollectionUtils.isNotEmpty(existChals)) {
                //先做一次查询
                List<Long> subQuesIds = new ArrayList<>();
                StudentExercise se1 = new StudentExercise();
                if (StuAnswerUtil.isComplexQues(se.getParentQuestionId())) {
                    FindLeafQuesIdsByParentIdsRequest req = new FindLeafQuesIdsByParentIdsRequest();
                    req.setParentQuesIds(Collections.singletonList(se.getParentQuestionId()));
                    FindLeafQuesIdsByParentIdsResponse response = questionService_v2.findLeafQuesIdsByParentIds(req);
                    subQuesIds.addAll(response.getLeafQuesIds());
                    se1.setQuestionIdList(subQuesIds);
                    se1.setQuestionId(null);
                } else {
                    se1.setQuestionId(se.getQuestionId());
                }
                se1.setParentQuestionId(se.getParentQuestionId());
                se1.setStudentId(se.getStudentId());
                se1.setStructId(se.getStructId());
                se1.setResourceId(existChals.get(0).getId());
                se1.setClassId(se.getClassId());
                se1.setExerciseSource(StuAnswerConstant.ExerciseSource.WRONGBOOK);
                WrongUtil.setRedoSouce(se1, se);
                se1.setQuestionType(se.getQuestionType());
                se1.setCreateTime(new Date());
                se1.setYear(se.getYear());
                //先做一次查询
                List<StudentExercise> ls = studentExerciseDao.findByStudentExercise(se1);
                int num = 0;
                int rightNum = 0;
                for (StudentExercise exercise : ls) {
                    if (exercise.getRedoStatus().equals(StuAnswerConstant.ExerciseResult.ERROR) || exercise
                            .getRedoStatus().equals(StuAnswerConstant.ExerciseResult.HALFRIGHT) || exercise
                            .getRedoStatus().equals(StuAnswerConstant.ExerciseResult.NOANSWER) || exercise
                            .getRedoStatus().equals(StuAnswerConstant.ExerciseResult.SUBMITED)) {
                        num++;
                        if (exercise.getQuestionId().equals(se.getQuestionId())) {
                            rightNum++;
                        }
                    }
                }
                if ((rightNum == 1) && (num == 1)) {
                    logger.info("createAnswerChalsFirst 删除重复批改为对的数据请求参数:" + JsonUtil.obj2Json(se1));
                    stuExeService.deleteErrorQuestionChallenge(se1);
                    answerChalDao.deleteAnswerChal(answerChal);
                } else {
                    if (!StuAnswerUtil.isWrong(se)) {
                        se1.setRedoStatus("1");
                        se1.setQuestionId(se.getQuestionId());
                        studentExerciseDao.update(se1);
                    }
                }
                return existChals.get(0).getId();
            }
        }
        return null;
    }


    private void setExerciseSource(String source, List<AnswerChal> answerChals) {
        for (AnswerChal ac : answerChals) {
            ac.setExerciseSource(source);
        }
    }

    /**
     * 消灭错题做错再次生成消灭错题(把原来的数据库状态置为失效，生成新的数据)。会生成错题消灭记录，和答题记录
     *
     * @param seList
     * @param operateType
     */
    private void createAnswerChalsAgain(List<StudentExercise> seList, OperateType operateType) {
        if (!WrongUtil.isNeedCreatChalAgain(seList)) {
            return;
        }
        StudentExercise se = seList.get(0);
        Long prarentBatchId = se.getResourceId();
        List<Long> quesIds = WrongUtil.getQuesIdListExeptLeaf(seList);

        AnswerChal answerChal = getAnswerChal(se, quesIds);
        answerChal.setExerciseSource(se.getRedoSource());
        answerChal.setFlag("1");
        answerChal.setFlagNew("0");
        answerChalDao.update(answerChal);

        List<AnswerChal> answerChals = convertAnswerChals(se, quesIds);
        setExerciseSource(se.getRedoSource(), answerChals);
        answerChalDao.creates(answerChals);

        List<StudentExercise> seList1 = new ArrayList<>();
        WrongUtil.copySeList(seList, seList1);
        if (operateType == OperateType.ANSWER) {
            stuRedoTempService.createRedoStuExes(seList1);
        } else if (operateType == OperateType.CORRECT) {
            stuRedoTempService.createRedoStuExesCorrect(seList1.get(0), this, prarentBatchId);
        }
    }

    private List<StudentExercise> getAllSeList(List<StudentExercise> seList, OperateType flag) {
        if (isLeafQuesCorrect(flag, seList)) {
            StudentExercise se = seList.get(0);
            StudentExercise s1 = new StudentExercise();
            s1.setClassId(se.getClassId());
            s1.setParentQuestionId(se.getParentQuestionId());
            s1.setResourceId(se.getResourceId());
            s1.setExerciseSource(se.getExerciseSource());
            s1.setRedoSource(se.getRedoSource());
            s1.setStudentId(se.getStudentId());
            List<StudentExercise> allLeafSeList = stuExerService.getDbRecords(s1);
            return allLeafSeList;
        }
        return seList;
    }

    private boolean isLeafQuesCorrect(OperateType flag, List<StudentExercise> seList) {
        return flag == OperateType.CORRECT && seList.get(0).getParentQuestionId() != null;
    }


    @Override
    public Long getSonBatchId(GetsonBatchIdParam param) {
        AnswerChal answerChal = new AnswerChal();
        answerChal.setExerciseSource(param.getExerciseSource());
        answerChal.setStudentId(param.getStudentId());
        answerChal.setResourceId(param.getResourceId());
        answerChal.setQuestionId(param.getQuestionId());
        answerChal.setFlag("1");
        List<AnswerChal> list = answerChalDao.findByAnswerChal(answerChal);
        if (CollectionUtils.size(list) == 1) {
            return list.get(0).getId();
        }
        throw new BizLayerException("", PraxisErrorCode.ANSWER_CHAL_SET_NOT_EXIST);
    }

    @Override
    public List<Long> getSonBatchIds(GetsonBatchIdsParam param) {
        AnswerChal answerChal = new AnswerChal();
        answerChal.setExerciseSource(param.getExerciseSource());
        answerChal.setStudentId(param.getStudentId());
        answerChal.setResourceId(param.getResourceId());
        answerChal.setFlag("1");
        List<AnswerChal> list = answerChalDao.findByAnswerChal(answerChal);
        List<Long> ids = WrongUtil.getIdsByChals(list);
        //        if(CollectionUtils.isEmpty(ids)){
        //            ids.add(0L);
        //        }
        return ids;
    }


    //    @Override
    //    public GetParentBatchIdReturn getParentBatchId(Long sonBatchId) {
    //        AnswerChal answerChal = new AnswerChal();
    //        answerChal.setId(sonBatchId);
    //        List<AnswerChal> list = answerChalDao.findByAnswerChal(answerChal);
    //        if(CollectionUtils.isNotEmpty(list)){
    //            GetParentBatchIdReturn return1 =new GetParentBatchIdReturn();
    //            AnswerChal ar = list.get(0);
    //            return1.setResourceId(ar.getResourceId());
    //            return1 .setClassId(ar.getClassId());
    //            return1.setCourseId(ar.getCourseId());
    //            return return1;
    //        }
    //        return null;
    //    }


    private List<StudentExercise> getWrongList(List<StudentExercise> seList) {
        return StuAnswerUtil.getWrongList(seList);
    }


    private AnswerChal getAnswerChal(StudentExercise se, List<Long> quesIdList) {
        AnswerChal answerChal = new AnswerChal();
        answerChal.setStudentId(se.getStudentId());
        answerChal.setResourceId(se.getResourceId());
        answerChal.setQuesIdList(quesIdList);
        return answerChal;
    }


    private List<AnswerChal> convertAnswerChals(StudentExercise se, List<Long> quesIdList) {
        List<AnswerChal> acList = new ArrayList<>();
        for (Long quesId : quesIdList) {
            AnswerChal ac = new AnswerChal();
            ac.setResourceId(se.getResourceId());
            ac.setStudentId(se.getStudentId());
            ac.setQuestionId(quesId);
            acList.add(ac);
        }
        return acList;
    }

    @Override
    public void createAnswerChals(List<StudentExercise> seList, OperateType operateType) {
        try {
            String exerciseSource = seList.get(0).getExerciseSource();
            //答题场景为上课、作业、测评、预习
            if (WrongUtil.isNeedCreateChal(exerciseSource)) {
                createAnswerChalsFirst(seList, operateType);
                createAnswerChalsFirstRevert(seList, operateType);
            }
            //答题场景为错题消灭
            if (WrongUtil.isChal(exerciseSource)) {
                seList = getAllSeList(seList, operateType);
                createAnswerChalsAgain(seList, operateType);
            }
        } catch (Exception e) {
            logger.error("批改后置业务-创建错题消灭失败", e);
        }

    }

    private void createAnswerChalsFirstRevert(List<StudentExercise> seList, OperateType operateType) {
        StudentExercise se = seList.get(0);
        CorrectOperateType correctOperateType = se.getCorrectOperateType();
        //如果操作场景为老师批改并且错题批改为正确
        if (OperateType.CORRECT.equals(operateType) && CorrectOperateType.WrongToRigth.equals(correctOperateType)) {
            List<Long> quesIds = WrongUtil.getQuesIdListExeptLeaf(seList);

            AnswerChal answerChal = getAnswerChal(se, quesIds);
            answerChal.setExerciseSource(se.getRedoSource());
            answerChal.setFlag("1");
            answerChal.setFlagNew("0");
            answerChalDao.update(answerChal);
        }
    }

}
