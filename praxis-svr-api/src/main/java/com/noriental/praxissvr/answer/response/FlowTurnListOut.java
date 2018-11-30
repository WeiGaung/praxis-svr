package com.noriental.praxissvr.answer.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.noriental.praxissvr.answer.bean.FlowTurnList;
import com.noriental.praxissvr.answer.bean.StudentExercise;
import com.noriental.validate.bean.CommonDes;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 求助批改 - 批改 返回列表
 * @author sheng.xiao
 * 2018年7月5日
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FlowTurnListOut extends CommonDes implements Serializable {
     /**
	 * 
	 */
	private static final long serialVersionUID = -4201410995205730537L;
	private List<FlowTurnList> flowTurnList;
	//每页最大记录数
	private Integer pageSize;
	//当前页
	private Integer currentPage;
	//总记录数
	private Integer totalCount;
	//总页数：totalPage
	private Integer totalPage;

	//该题的总人数
	private Integer totalNum;
	//该题未批改人数
	private Integer num;
	//截止时间
	private Date deadline;


	public List<FlowTurnList> getFlowTurnList() {
		return flowTurnList;
	}

	public void setFlowTurnList(List<FlowTurnList> flowTurnList) {
		this.flowTurnList = flowTurnList;
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

	public Integer getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(Integer totalPage) {
		this.totalPage = totalPage;
	}

	public void setTotalPage(Integer totalCount,Integer pageSize) {
		totalPage = totalCount % pageSize == 0 ? (totalCount / pageSize) : (totalCount/pageSize+1);
	}

	public Integer getTotalNum() {
		return totalNum;
	}

	public void setTotalNum(Integer totalNum) {
		this.totalNum = totalNum;
	}

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

	public Date getDeadline() {
		return deadline;
	}

	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}
}
