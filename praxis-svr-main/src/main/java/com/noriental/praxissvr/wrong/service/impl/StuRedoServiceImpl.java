package com.noriental.praxissvr.wrong.service.impl;

import com.noriental.praxissvr.answer.bean.OperateType;
import com.noriental.praxissvr.answer.bean.StudentExercise;
import com.noriental.praxissvr.answer.service.StudentExerciseService;
import com.noriental.praxissvr.answer.util.StuAnswerUtil;
import com.noriental.praxissvr.common.StuAnswerConstant;
import com.noriental.praxissvr.questionSearch.service.QuestionSearchService;
import com.noriental.praxissvr.wrong.request.UpdateRedoStatusRequest;
import com.noriental.praxissvr.wrong.service.StuRedoService;
import com.noriental.validate.bean.CommonDes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;

@Service(value = "stuRedoService")
@Deprecated
public class StuRedoServiceImpl implements StuRedoService {
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	@Autowired
	private StudentExerciseService stuExeService;
	/*@Autowired
	StuAnswerMoreService stuAnswerMoreService;*/
    @Autowired
    private QuestionSearchService questionSearchService;

	


	@Override
	public CommonDes updateRedoStatus(UpdateRedoStatusRequest in) {
		StudentExercise  se = new StudentExercise();
		se.setQuestionId(in.getQuestionId());
		se.setStudentId(in.getStudentId());
		se.setRedoSource(in.getRedoSource());
		se.setResourceId(in.getResourceId());
		se.setExerciseSource(StuAnswerConstant.ExerciseSource.WRONGBOOK);
		se.setRedoStatus(StuAnswerConstant.ExerciseResult.MASTERED);
		initParentQuestionId(se);
		stuExeService.updateRecord(se, OperateType.NONE);
		return new CommonDes();
	}
	


	private void initParentQuestionId(StudentExercise se) {
        se.setParentQuestionId(StuAnswerUtil.getParentQuesId(questionSearchService,se.getQuestionId()));
	}
}
