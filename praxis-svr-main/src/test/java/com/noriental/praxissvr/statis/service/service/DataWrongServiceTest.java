package com.noriental.praxissvr.statis.service.service;

import com.alibaba.fastjson.JSONObject;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.noriental.BaseTest;
import com.noriental.praxissvr.answer.request.FindStuAnswsOnBatchRequest;
import com.noriental.praxissvr.answer.request.UpdateCorrectRequest;
import com.noriental.praxissvr.answer.request.UpdateSubmitAnswer;
import com.noriental.praxissvr.answer.request.UpdateSubmitAnswerRequest;
import com.noriental.praxissvr.answer.service.AnswerCommonService;
import com.noriental.praxissvr.common.StuAnswerConstant;
import com.noriental.praxissvr.statis.bean.ClassWrongQuesCount;
import com.noriental.praxissvr.statis.bean.WrongQuesSortType;
import com.noriental.praxissvr.statis.request.FindClassWrongQuesCountRequest;
import com.noriental.praxissvr.statis.request.FindKnowledgeListRequest;
import com.noriental.praxissvr.statis.service.DataWrongQuesService;
import com.noriental.utils.json.JsonUtil;
import com.noriental.validate.bean.CommonResponse;
import com.sumory.mybatis.pagination.result.PageResult;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import static  org.junit.Assert.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.validation.constraints.AssertTrue;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

/**
 * 错题记录测试用例
 */
public class DataWrongServiceTest extends BaseTest{
@Autowired
    private DataWrongQuesService dataWrongQuesService;
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Test
    public  void find(){
        Long gradeId = 1L;
        List<Long> classIdList = new ArrayList<>();
        classIdList.add(1575L);
        //多个班级
        classIdList.add(1577L);

//        Integer quesTypeId =1;
//        Integer difficulty =1;

        //题目属性为空
        Integer quesTypeId =null;
        Integer difficulty =null;

        //知识点为空
        Long knowledgeId =null;

        String updateTimeStart = "2018-05-10";
        String updateTimeEnd = "2018-05-20";

        //知识点不为空
//        Long knowledgeId =1L;

        //第N页
        int page = 1;
        //页大小
        int limit = 4;

        WrongQuesSortType sort = WrongQuesSortType.TIME_DESC;

        FindClassWrongQuesCountRequest req = new FindClassWrongQuesCountRequest(10L,gradeId,classIdList,quesTypeId,difficulty,knowledgeId,sort,page,limit);
        CommonResponse<PageResult<ClassWrongQuesCount>> classWrongQuesCount = dataWrongQuesService.findClassWrongQuesCount(req);
        logger.info(JSONObject.toJSONString(classWrongQuesCount, true));
        logger.info(classWrongQuesCount.getData().getPager().getTotalCount()+"");
//        assertTrue("没有数据",classWrongQuesCount.getData().getPager().getTotalCount()>0);
    }

    @Test
    public  void findKnow() {
        List<Long> classIdList = new ArrayList<>();
        classIdList.add(1L);
        Long subjectId = 1L;
        Integer levle = 2;
        Long parknowid=1L;
        FindKnowledgeListRequest re = new FindKnowledgeListRequest(classIdList,subjectId,levle,parknowid);
        logger.info(JSONObject.toJSONString(dataWrongQuesService.findKnowledgeList(re),true));
    }

}
