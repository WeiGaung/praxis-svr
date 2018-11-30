package com.noriental.praxissvr.question.service;

import com.noriental.praxissvr.question.request.FindGroupByIdRequest;
import com.noriental.praxissvr.question.request.FindGroupBySystemIdRequest;
import com.noriental.praxissvr.question.response.FindGroupByIdResponse;
import com.noriental.praxissvr.question.response.FindGroupBySystemIdResponse;
import com.noriental.validate.exception.BizLayerException;

/**
 * @author chenlihua
 * @date 2015/12/23
 * @time 11:12
 */
public interface GroupService {
    /**
     * 通过systemId 查询 用户的分组列表
     * @param request req
     * @return resp
     */
    FindGroupBySystemIdResponse findGroupBySystemId(FindGroupBySystemIdRequest request) throws BizLayerException;

    /**
     * 通过ID查询分组
     * @param request req
     * @return resp
     */
    FindGroupByIdResponse findGroupById(FindGroupByIdRequest request) throws BizLayerException;
}
