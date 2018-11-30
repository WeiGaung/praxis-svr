package com.noriental.praxissvr.answer.service.impl;

import com.noriental.praxissvr.answer.bean.StudentExercise;
import com.noriental.praxissvr.answer.bean.UpdateSubmitAnswerVo;
import com.noriental.praxissvr.answer.dao.StudentErrorExeDao;
import com.noriental.praxissvr.answer.dao.StudentExerciseDao;
import com.noriental.praxissvr.answer.service.StudentErrorExeService;
import com.noriental.praxissvr.answer.util.StuAnswerUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.*;
import java.util.Map.Entry;

/**
 * Created by bluesky on 2016/5/24.
 */
@Service(value = "studentErrorExeService")
public class StudentErrorExeServiceImpl implements StudentErrorExeService {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private StudentErrorExeDao studentErrorExeDao;
    @Autowired
    private StudentExerciseDao stuExeDao;

    @Override
    public void updateOne(StudentExercise studentExercise) {
        boolean update = studentErrorExeDao.update(studentExercise);
        //进入错题本和错题挑战的条件是否成立
        if (!update && StuAnswerUtil.isWrong(studentExercise)) {
            List<StudentExercise> allLeafAnswers = new ArrayList<>();
            //所属大题id    单题为空 复合题不为空
            if (studentExercise.getParentQuestionId() != null) {    //复合题
                StudentExercise se = new StudentExercise(studentExercise.getStudentId(), null, studentExercise
                        .getExerciseSource(), studentExercise.getResourceId(), studentExercise.getParentQuestionId(),
                        studentExercise.getRedoSource());
                se.setYear(studentExercise.getYear());
                se.setClassId(studentExercise.getClassId());
                allLeafAnswers.addAll(stuExeDao.findByStudentExercise(se));
            } else {    //单题
                allLeafAnswers.add(studentExercise);
            }
            if (CollectionUtils.isNotEmpty(allLeafAnswers)) {
                studentErrorExeDao.creates(allLeafAnswers);
            } else {
                logger.error("不存在答题记录！");
            }
        }
    }

    @Override
    public void updateBatch(StudentExercise se) {
        StudentExercise se1 = filterWrongQues(se);
        if (se1 != null) {
            se1.getUpdateSubmitAnswerVoList().addAll(getBrotherAnswers(se1.getUpdateSubmitAnswerVoList(), se
                    .getUpdateSubmitAnswerVoList()));

            List<StudentExercise> seList = StuAnswerUtil.initSeList(se1, se1.getUpdateSubmitAnswerVoList(),
                    se1.getUpdateSubmitAnswerVoList().get(0).getCorrectorRole(), se1.getUpdateSubmitAnswerVoList()
                            .get(0).getCorrectorId());
            studentErrorExeDao.creates(seList);
        }
    }

    /****
     * 学生错题记录入库
     * @param seList
     */
    @Override
    public void creates(List<StudentExercise> seList) {
        Map<Long, List<StudentExercise>> parentChildList = new HashMap<>();
        for (StudentExercise all : seList) {
            Long key = all.getQuestionId();
            if (all.getParentQuestionId() != null) {
                key = all.getParentQuestionId();
            }
            List<StudentExercise> list = parentChildList.get(key);
            if (list == null) {
                list = new ArrayList<>();
                parentChildList.put(key, list);
            }
            list.add(all);
        }
        List<StudentExercise> seList1 = new ArrayList<>();
        for (Entry<Long, List<StudentExercise>> entry : parentChildList.entrySet()) {
            List<StudentExercise> list = entry.getValue();
            for (StudentExercise se : list) {
                if (StuAnswerUtil.isWrong(se)) {
                    seList1.addAll(list);
                    break;
                }
            }
        }
        if (CollectionUtils.isNotEmpty(seList1)) {
            studentErrorExeDao.creates(seList1);
        }
    }

    @Override
    public List<StudentExercise> findByQuesIds(StudentExercise se) {
        List<StudentExercise> list = studentErrorExeDao.findByQuesIds(se);
        return list;
    }


    private StudentExercise filterWrongQues(StudentExercise se) {
        StudentExercise se1 = new StudentExercise();
        BeanUtils.copyProperties(se, se1);

        List<UpdateSubmitAnswerVo> updateSubmitAnswerVoList2 = new ArrayList<>();
        List<UpdateSubmitAnswerVo> updateSubmitAnswerVoList1 = se1.getUpdateSubmitAnswerVoList();
        for (UpdateSubmitAnswerVo vo : updateSubmitAnswerVoList1) {
            if (StuAnswerUtil.isWrong(vo.getStructId(), vo.getResult())) {
                updateSubmitAnswerVoList2.add(vo);
            }
        }

        if (CollectionUtils.isNotEmpty(updateSubmitAnswerVoList2)) {
            se1.setUpdateSubmitAnswerVoList(updateSubmitAnswerVoList2);
            return se1;
        } else {
            return null;
        }
    }


    private Set<UpdateSubmitAnswerVo> getBrotherAnswers(List<UpdateSubmitAnswerVo> wrongAnswers,
                                                        List<UpdateSubmitAnswerVo> allAnswers) {
        Set<UpdateSubmitAnswerVo> brotherAnswers = new HashSet<>();
        for (UpdateSubmitAnswerVo wrong : wrongAnswers) {
            for (UpdateSubmitAnswerVo all : allAnswers) {
                if (wrong.getParentQuestionId() != null && all.getParentQuestionId() != null && wrong
                        .getParentQuestionId().equals(all.getParentQuestionId()) && !wrong.getQuestionId().equals(all
                        .getQuestionId()) && !StuAnswerUtil.isWrong(all.getStructId(), all.getResult())) {
                    brotherAnswers.add(all);
                }
            }
        }
        return brotherAnswers;
    }

}
