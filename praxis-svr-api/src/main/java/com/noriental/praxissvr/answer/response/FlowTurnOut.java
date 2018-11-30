package com.noriental.praxissvr.answer.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.noriental.praxissvr.answer.bean.FlowTurn;
import com.noriental.praxissvr.answer.bean.FlowTurnList;
import com.noriental.validate.bean.CommonDes;

import java.io.Serializable;
import java.util.List;

/**
 * 求助怕批改返回列表
 * @author
 * 2018年7月2日
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FlowTurnOut extends CommonDes implements Serializable {
     /**
	 * 
	 */
	private static final long serialVersionUID = -4201410995205730537L;
	private List<FlowTurn> flowTurn;

	//每页最大记录数
	private Integer pageSize;
	//当前页
	private Integer currentPage;
	//总记录数
	private Integer totalCount;
	//总页数：totalPage
	private Integer totalPage;

	public List<FlowTurn> getFlowTurn() {
		return flowTurn;
	}

	public void setFlowTurn(List<FlowTurn> flowTurn) {
		this.flowTurn = flowTurn;
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
		return  totalPage;
	}

	public void setTotalPage(Integer totalPage) {
		this.totalPage = totalPage;
	}

	public void setTotalPage(Integer totalCount,Integer pageSize) {
		totalPage = totalCount % pageSize == 0 ? (totalCount / pageSize) : (totalCount/pageSize+1);
	}
}
