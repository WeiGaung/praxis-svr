package com.noriental.praxissvr.brush.response;

import java.io.Serializable;
import java.util.List;

import com.noriental.praxissvr.brush.bean.StudentWork;
import com.noriental.validate.bean.CommonDes;

public class StudentWorkPageOutput extends CommonDes implements Serializable {

    private static final long serialVersionUID = 1321709436293806587L;
    //当前页
    private Integer currentPage;
    //总页数
    private Integer totalPageCount;
    //作业历史数据
    private List<StudentWork> studentWorks;


    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getTotalPageCount() {
        return totalPageCount;
    }

    public void setTotalPageCount(Integer totalPageCount) {
        this.totalPageCount = totalPageCount;
    }

    public List<StudentWork> getStudentWorks() {
        return studentWorks;
    }

    public void setStudentWorks(List<StudentWork> studentWorks) {
        this.studentWorks = studentWorks;
    }


}
