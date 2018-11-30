package com.noriental.praxissvr.wrong.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.noriental.BaseTest;
import com.noriental.BaseTestClient;
import com.noriental.praxissvr.wrong.request.UpdateRedoStatusRequest;
import com.noriental.praxissvr.wrong.service.StuRedoService;
import com.noriental.validate.bean.CommonDes;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * Created by chenlihua on 2016/8/19.
 * praxis-svr
 */
public class StuRedoServiceImplTest extends BaseTestClient {
    @Autowired
    private StuRedoService stuRedoService;
    private Long studentId=82951058012L;
    private Long resourceId = 650L;
    private Long questionIdjdt1 = 1392068L;
    private Long questionIdjdt2 = 1392067L;
    private Long questionIdjdt3 = 1304645L;
    private String postil="[\"a.jpg\"]";
    private String answer="[\"a.jpg\"]";
    @Test
    public void updateRedoStatus() throws Exception {
        UpdateRedoStatusRequest request = JSONObject.parseObject("{\"resourceId\":650,\"studentId\":82951058012,\"questionId\":1392068,\"redoSource\":\"6\"}", UpdateRedoStatusRequest.class);
        CommonDes commonDes = stuRedoService.updateRedoStatus(request);
        System.out.println(commonDes);
    }

}