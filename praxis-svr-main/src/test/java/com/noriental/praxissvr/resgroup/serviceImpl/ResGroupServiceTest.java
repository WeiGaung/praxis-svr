package com.noriental.praxissvr.resgroup.serviceImpl;

import com.noriental.BaseTest;
import com.noriental.praxissvr.resourcegroup.request.*;
import com.noriental.praxissvr.resourcegroup.service.ResourceGroupService;
import com.noriental.utils.json.JsonUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.invoke.MethodHandles;
import java.util.Date;

/**
 * Created by kate on 2016/11/21.
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class ResGroupServiceTest extends BaseTest {
    @Autowired
    private ResourceGroupService resourceGroupService;
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @Test //ok
    public void testResGroupCreate() throws Exception{
        ResGroupCreateRequest resGroupCreateRequest=new ResGroupCreateRequest();
        resGroupCreateRequest.setName("kate test6"+new Date());
      /*  resGroupCreateRequest.setSecret("hello");
        resGroupCreateRequest.setTimestamp("");*/
        resGroupCreateRequest.setSystemId(6131418L);
        logger.info(JsonUtil.obj2Json(resourceGroupService.createGroup(resGroupCreateRequest)));

    }

    @Test //ok
    public void testResGroupDelete()throws Exception{
        ResGroupDeleteRequest resGroupDeleteRequest=new ResGroupDeleteRequest();
        resGroupDeleteRequest.setId(1001148L);
        resGroupDeleteRequest.setSystemId(6106330L);
        logger.info(JsonUtil.obj2Json(resourceGroupService.deleteGroup(resGroupDeleteRequest)));


    }

    @Test  //ok
    public void testResGroupTransfer() throws Exception{
        ResGroupTransferRequest resGroupTransferRequest=new ResGroupTransferRequest();
        resGroupTransferRequest.setGroupId(1001153L);
        resGroupTransferRequest.setQuestionId(4000781L);
        resGroupTransferRequest.setSystemId(6106330L);
        logger.info(JsonUtil.obj2Json(resourceGroupService.transferGroup(resGroupTransferRequest)));
    }

    @Test //ok
    public void testGroupUpdate() throws Exception{
        ResGroupUpdateRequest resGroupUpdateRequest=new ResGroupUpdateRequest();
        resGroupUpdateRequest.setId(1008497L);
        resGroupUpdateRequest.setName("哈利路亚"+new Date());
        resGroupUpdateRequest.setSystemId(6131418L);
        logger.info(JsonUtil.obj2Json(resourceGroupService.updateGroup(resGroupUpdateRequest)));

    }

    @Test //ok
    public void testResGroupGetList()throws Exception{
      //  for (int i=0;i<100;i++) {
            ResGroupGetListRequest resGroupGetListRequest = new ResGroupGetListRequest();
            resGroupGetListRequest.setSecret("e6f53871d1a96ac392588eb3e8647cc7aafa4f06");
            resGroupGetListRequest.setSystemId(6106335L);
            resGroupGetListRequest.setTimestamp("1482484782549");
            logger.info(JsonUtil.obj2Json(resourceGroupService.getGroupList(resGroupGetListRequest)));
       // }

    }

}
