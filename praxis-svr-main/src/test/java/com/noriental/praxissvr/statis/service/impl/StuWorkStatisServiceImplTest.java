package com.noriental.praxissvr.statis.service.impl;

import com.noriental.BaseTest;
import com.noriental.praxissvr.statis.request.FindStuWorkSatisRequest;
import com.noriental.praxissvr.statis.response.FindStuWorkSatisResponse;
import com.noriental.praxissvr.statis.service.StuWorkStatisService;
import com.noriental.utils.json.JsonUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * Created by kate on 2017/10/19.
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class StuWorkStatisServiceImplTest extends BaseTest {

    @Resource
    private StuWorkStatisService statisService;


    @Test
    public void testFindStuWorkSatis(){
        FindStuWorkSatisRequest request=new FindStuWorkSatisRequest();
        request.setStudentId(8230162L);
        request.setSubjectId(1L);
        FindStuWorkSatisResponse response= statisService.findStuWorkSatis(request);
        System.out.println(JsonUtil.obj2Json(response.getStuWorkStatisList()));


    }



}
