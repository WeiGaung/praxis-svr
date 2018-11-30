package com.noriental.praxissvr.answer.request;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.noriental.praxissvr.answer.bean.StudentExercise;
import com.noriental.validate.bean.BaseRequest;

/**
 * 答题记录返回列表
 * @author sheng.xiao
 * 2015年12月21日
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Deprecated
public class StudentExerciseListIn extends BaseRequest implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2276734862517682057L;
	private List<StudentExercise> studentExerciseList;
	public List<StudentExercise> getStudentExerciseList() {
		return studentExerciseList;
	}
	public void setStudentExerciseList(List<StudentExercise> studentExerciseList) {
		this.studentExerciseList = studentExerciseList;
	}
}
