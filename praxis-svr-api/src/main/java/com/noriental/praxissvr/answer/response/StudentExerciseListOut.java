package com.noriental.praxissvr.answer.response;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.noriental.praxissvr.answer.bean.StudentExercise;
import com.noriental.validate.bean.CommonDes;
/**
 * 答题记录返回列表
 * @author sheng.xiao
 * 2015年12月21日
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudentExerciseListOut extends CommonDes implements Serializable {
     /**
	 * 
	 */
	private static final long serialVersionUID = -4201410995205730537L;
	private List<StudentExercise> studentExerciseList;
	private Integer isOldData = 1;  //1是新数据 0是老数据
	private Integer pageCount;
	private Integer pageSize;
	private Integer currentPage;
	private Integer totalCount;
	public List<StudentExercise> getStudentExerciseList() {
		return studentExerciseList;
	}
	public void setStudentExerciseList(List<StudentExercise> studentExerciseList) {
		this.studentExerciseList = studentExerciseList;
	}

	public Integer getIsOldData() {
		return isOldData;
	}

	public void setIsOldData(Integer isOldData) {
		this.isOldData = isOldData;
	}

	public Integer getPageCount() {
		return pageCount;
	}
	public void setPageCount(Integer pageCount) {
		this.pageCount = pageCount;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	public Integer getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}
	public Integer getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}
	
}
