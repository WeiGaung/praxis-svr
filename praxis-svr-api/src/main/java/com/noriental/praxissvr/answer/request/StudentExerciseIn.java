package com.noriental.praxissvr.answer.request;

import java.io.Serializable;

import com.noriental.praxissvr.answer.bean.StudentExercise;
import com.noriental.validate.bean.BaseRequest;
@Deprecated
public class StudentExerciseIn extends BaseRequest implements Serializable {
   
	/**
	 * 
	 */
	private static final long serialVersionUID = 3516060526980896903L;
	private StudentExercise studentExercise;
	private Boolean padInvoke;
	private String requestId;
	public StudentExerciseIn() {
		studentExercise = new StudentExercise();
	}
   public void setStudentExercise(StudentExercise studentExercise) {
	this.studentExercise = studentExercise;
   }
   public StudentExercise getStudentExercise() {
	return studentExercise;
   }
   public void setId(Long id){
	   studentExercise.setId(id);
   }
	public void setQuestionId(Long questionId){
		studentExercise.setQuestionId(questionId);
	}
	public void setStudentId (Long studentId){
		studentExercise.setStudentId(studentId);
	}
	public void setResult(String result){
		studentExercise.setResult(result);
	}
	public void setParentQuestionId(Long parentQuestionId){
		studentExercise.setParentQuestionId(parentQuestionId);
	}

	public void setResourceId(long resourceId) {
		studentExercise.setResourceId(resourceId);
	}
	public void setRedoStatus(String redoStatus) {
		studentExercise.setRedoStatus(redoStatus);
	}
	public void setRedoSource(String redoSource) {
		studentExercise.setRedoSource(redoSource);
	}

	public void setPadInvoke(Boolean padInvoke) {
		this.padInvoke = padInvoke;
	}
	public Boolean getPadInvoke() {
		return padInvoke;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
}
