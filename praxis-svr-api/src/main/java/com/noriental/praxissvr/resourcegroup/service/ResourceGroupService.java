package com.noriental.praxissvr.resourcegroup.service;

import com.noriental.praxissvr.resourcegroup.request.*;
import com.noriental.praxissvr.resourcegroup.response.ResGroupCreateResponse;
import com.noriental.praxissvr.resourcegroup.response.ResGroupGetListResponse;
import com.noriental.validate.bean.CommonDes;
import com.noriental.validate.bean.CommonResponse;
import com.noriental.validate.exception.BizLayerException;

/**
 * Created by kate on 2016/11/18.
 */
@Deprecated
public interface ResourceGroupService {


    /***
     * 创建组
     * @param request
     * @return
     * @throws BizLayerException
     */
    ResGroupCreateResponse createGroup(ResGroupCreateRequest request )throws BizLayerException;


    /***
     * 获取组列表
     * @param request
     * @return
     * @throws BizLayerException
     */
    ResGroupGetListResponse getGroupList(ResGroupGetListRequest request )throws BizLayerException;

    /***
     * 更新组
     * @param request
     * @return
     * @throws BizLayerException
     */
    CommonDes updateGroup(ResGroupUpdateRequest request )throws BizLayerException;

    /***
     * 删除组
     * @param request
     * @return
     * @throws BizLayerException
     */
    CommonDes deleteGroup(ResGroupDeleteRequest request )throws BizLayerException;
    /***
     * 转移组
     * @param request
     * @return
     * @throws BizLayerException
     */
    CommonDes transferGroup(ResGroupTransferRequest request )throws BizLayerException;



}
