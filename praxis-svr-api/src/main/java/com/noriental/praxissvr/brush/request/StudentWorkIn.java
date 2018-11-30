package com.noriental.praxissvr.brush.request;

import java.io.Serializable;
import java.util.List;

import com.noriental.praxissvr.answer.bean.StudentExercise;
import com.noriental.praxissvr.brush.bean.StudentWork;
import com.noriental.validate.bean.BaseRequest;

public class StudentWorkIn extends BaseRequest implements Serializable {
        /**
	 * 
	 */
	private static final long serialVersionUID = -5852457586263001991L;
		private StudentWork studentWork;

		public StudentWorkIn() {
			studentWork = new StudentWork();
		}

	    public void setStudentWork(StudentWork studentWork) {
			this.studentWork = studentWork;
		}
        public StudentWork getStudentWork() {
			return studentWork;
		}
	    public void setId(Long id){
			studentWork.setId(id);
		}
		public void setStudentId(Long studentId){
			studentWork.setStudentId(studentId);
		}
	    public void setStudentExercises(List<StudentExercise> studentExercises){
			studentWork.setStudentExercises(studentExercises);
		}
		public void setWorkStatus(Integer workStatus) {
			studentWork.setWorkStatus(workStatus) ;
		}
}
