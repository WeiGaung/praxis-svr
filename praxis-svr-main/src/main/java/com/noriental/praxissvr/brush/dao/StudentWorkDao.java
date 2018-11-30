package com.noriental.praxissvr.brush.dao;

import com.noriental.dao.BaseDao;
import com.noriental.praxissvr.brush.bean.StudentWork;
import com.sumory.mybatis.pagination.result.PageResult;

import java.util.List;
import java.util.Map;

public interface StudentWorkDao extends BaseDao<StudentWork, Long> {
    /**
     * 分页获得作业记录
     *
     * @param params
     * @return
     */
    PageResult<StudentWork> findWorksPage(Map<String, Object> params);

    /**
     * workid and studentid 是否存在
     *
     * @param studentWork
     * @return
     */
    int findExist(StudentWork studentWork);

    List<StudentWork> findForExist(StudentWork studentWork);
    /**
     * 获取单个 复习（学生作业记录表对象）
     */
    StudentWork findById(long id);

}
