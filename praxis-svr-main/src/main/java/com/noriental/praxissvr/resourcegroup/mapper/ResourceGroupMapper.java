package com.noriental.praxissvr.resourcegroup.mapper;

import com.noriental.praxissvr.resourcegroup.bean.ResourceGroup;
import com.noriental.praxissvr.resourcegroup.request.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by kate on 2016/11/18.
 */
@Deprecated
@Repository
public interface ResourceGroupMapper {

    int isGroupExit(ResGroupCreateRequest resGroupCreateRequest);

    /**
     * 创建组
     * @param resGroupCreateRequest
     * @return
     */
    Long createResGroup(ResGroupCreateRequest resGroupCreateRequest);

    /**
     * 删除组
     * @param resGroupUpdateRequest
     * @return
     */

    boolean updateResGroup(ResGroupUpdateRequest resGroupUpdateRequest);

    /**
     * 更新组
     * @param resGroupDeleteRequest
     * @return
     */

    boolean deleteResGroup(ResGroupDeleteRequest resGroupDeleteRequest);

    /***
     * 转移组
     * @param resGroupTransferRequest
     * @return
     */

    boolean updateResGroupId(ResGroupTransferRequest resGroupTransferRequest);

    /***
     * 获取组列表
     * @param resGroupGetListRequest
     * @return
     */

    List<ResourceGroup> getGroupList(ResGroupGetListRequest resGroupGetListRequest);

    /***
     * 获取默认组数据
     * @return
     */
    List<ResourceGroup> getDefaultGroupList(ResGroupGetListRequest resGroupGetListRequest);


    /***
     * 更新entity_question表字段信息
     * @param resGroupDeleteRequest
     * @return
     */
    boolean updateEntityQuestion(ResGroupDeleteRequest resGroupDeleteRequest);



    ResourceGroup  findGroupEntity(ResGroupTransferRequest request);


}
