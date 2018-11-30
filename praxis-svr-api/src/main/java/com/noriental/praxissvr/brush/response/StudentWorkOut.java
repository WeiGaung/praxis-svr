package com.noriental.praxissvr.brush.response;

import java.io.Serializable;

import com.noriental.praxissvr.brush.bean.StudentWork;
import com.noriental.validate.bean.CommonDes;

public class StudentWorkOut extends CommonDes implements Serializable {
		/**
	 * 
	 */
	private static final long serialVersionUID = -4429025279773293912L;
		private StudentWork studentWork;
        public void setStudentWork(StudentWork studentWork) {
			this.studentWork = studentWork;
		}
        public StudentWork getStudentWork() {
			return studentWork;
		}
        
}
