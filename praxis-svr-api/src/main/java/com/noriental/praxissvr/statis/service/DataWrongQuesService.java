package com.noriental.praxissvr.statis.service;

import com.noriental.praxissvr.statis.bean.ClassWrongQuesCount;
import com.noriental.praxissvr.statis.bean.KnowledgeVo;
import com.noriental.praxissvr.statis.request.FindClassWrongQuesCountRequest;
import com.noriental.praxissvr.statis.request.FindKnowledgeListRequest;
import com.noriental.validate.bean.CommonResponse;
import com.sumory.mybatis.pagination.result.PageResult;

import java.util.List;

public interface DataWrongQuesService {
    /**
     * 查询指定班级指定科目下题目错误次数。
     * @param request
     * @return
     */
    CommonResponse<PageResult<ClassWrongQuesCount>> findClassWrongQuesCount(FindClassWrongQuesCountRequest request);

    /**
     * 查询班级可科目下错误的知识点。
     * 返回结果作为查询条件。
     * @param request
     * @return
     */
    CommonResponse<List<KnowledgeVo>> findKnowledgeList(FindKnowledgeListRequest request);

    /***
     * 教师空间错题查询某题的错题所有学生名字
     * @param request
     * @return
     */
    CommonResponse<List<String>> findErrorQuesStudentNames(FindClassWrongQuesCountRequest request);

}
