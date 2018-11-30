package com.noriental.praxissvr.brush.dao.impl;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.noriental.dao.BaseDaoImpl;
import com.noriental.praxissvr.brush.bean.StudentWork;
import com.noriental.praxissvr.brush.dao.StudentWorkDao;
import com.sumory.mybatis.pagination.result.PageResult;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class StudentWorkDaoImpl extends BaseDaoImpl<StudentWork, Long> implements StudentWorkDao {
    private final String namespace = StudentWork.class.getName();

    @Override
    public PageResult<StudentWork> findWorksPage(Map<String, Object> params) {
        PageBounds pageBounds = (PageBounds) params.get("pager");
        return this.findPageResult(this.namespace + ".getWorks", params, pageBounds);
    }

    @Override
    public int findExist(StudentWork studentWork) {
        return this.count(this.namespace + ".findExist", studentWork);
    }

    @Override
    public List<StudentWork> findForExist(StudentWork studentWork) {
        return this.findList(this.namespace + ".findForExist", studentWork);
    }
    /**
     * 获取单个 复习（学生作业记录表对象）
     * @param id
     */
    @Override
    public StudentWork findById(long id) {
        return this.findById(StudentWork.class, id);
    }

}
