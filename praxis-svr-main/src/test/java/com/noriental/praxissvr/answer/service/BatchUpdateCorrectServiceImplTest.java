package com.noriental.praxissvr.answer.service;

import com.noriental.BaseTest;
import com.noriental.praxissvr.answer.bean.BrushDataEntity;
import com.noriental.praxissvr.answer.bean.StudentExercise;
import com.noriental.praxissvr.answer.mappers.AnswerCorrectMapper;
import com.noriental.praxissvr.answer.request.BatchIntellUpdateRequest;
import com.noriental.praxissvr.answer.request.BatchUpdateRequest;
import com.noriental.utils.json.JsonUtil;
import com.noriental.validate.bean.CommonDes;
import com.noriental.validate.bean.CommonResponse;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

/**
 * @author kate
 * @create 2017-12-29 16:50
 * @desc 一键批改测试用例
 **/
public class BatchUpdateCorrectServiceImplTest extends BaseTest {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @Resource
    BatchUpdateCorrectService batchUpdateCorrectService;
    @Resource
    AnswerCorrectMapper answerCorrectMapper;

    @Test  //ok
    public void findStudentAnswerList() {
        StudentExercise exercise = new StudentExercise();
        exercise.setResourceId(1149317L);
        exercise.setExerciseSource("7");
        CommonResponse response = batchUpdateCorrectService.findStudentAnswerList(exercise);
        logger.info(JsonUtil.obj2Json(response.getData()));

    }
    //{"reqId":"1515407991804","resourceId":1149281,"exerciseSource":"7","correctorId":61951064006,"correctorRole":"teacher","result":"1","studentIds":[81951064015],"questionId":null,"busType":1}
    @Test
    public void batchUpdateCorrect() {
        BatchUpdateRequest request;
        /*String s="{\"reqId\":null,\"resourceId\":960928,\"exerciseSource\":\"6\",\"correctorId\":61951145793," +
                "\"correctorRole\":\"teacher\",\"result\":\"1\",\"studentIds\":[81951196299],\"questionId\":null," +
                "\"busType\":1}";*/
        String s="{\"reqId\":null,\"resourceId\":1824320,\"exerciseSource\":\"6\",\"correctorId\":61148744,\"correctorRole\":\"teacher\",\"result\":\"1\",\"studentIds\":[81192395],\"questionId\":2424976,\"busType\":2,\"totalScoreMap\":{}}";
       /* s="{\"reqId\":\"1515466711706\",\"resourceId\":1149281,\"exerciseSource\":\"7\",\"correctorId\":61951064006," +
                "\"correctorRole\":\"teacher\",\"result\":\"1\",\"studentIds\":[81951068962],\"questionId\":null," +
                "\"busType\":1}";*/
        request=JsonUtil.readValue(s,BatchUpdateRequest.class);
        CommonDes commonDes=batchUpdateCorrectService.batchUpdateCorrect(request);
        logger.info(JsonUtil.obj2Json(commonDes));

    }


    @Test
    public void batchUpdateIntellCorrect(){
        BatchIntellUpdateRequest request;
        String s="{\"reqId\":\"030690332044\",\"resourceId\":2298517,\"exerciseSource\":\"6\",\"correctorId\":61951185794,\"correctorRole\":\"teacher\",\"studentIds\":[81951185845,81951185847],\"questionId\":4640701}";
        request=JsonUtil.readValue(s,BatchIntellUpdateRequest.class);
        CommonDes commonDes=batchUpdateCorrectService.batchUpdateIntellCorrect(request);
        logger.info(JsonUtil.obj2Json(commonDes));

    }

    @Test
    public void inserData2Cms(){
        BrushDataEntity brushDataEntity=new BrushDataEntity();
        brushDataEntity.setResourceId(1149295L);
        brushDataEntity.setExerciseSource("5");
        brushDataEntity.setStudentId(81951063994L);
        brushDataEntity.setBusFlag(brushDataEntity.getBusFlag());
        answerCorrectMapper.insertDataToCms(brushDataEntity);
    }

    @Test
    public void batchUpdateCorrectList(){
        List<BatchUpdateRequest> requestList = new ArrayList<>();
        BatchUpdateRequest request = new BatchUpdateRequest();
        request.setResourceId(1824477L);
        request.setExerciseSource("6");
        request.setCorrectorId(6160381L);
        request.setCorrectorRole("teacher");
        request.setResult("5");
        List<Long> list = new ArrayList<>();
        list.add(81951184789L);
        request.setStudentIds(list);
        request.setQuestionId(11330349L);
        request.setBusType(2);
        requestList.add(request);

        /*BatchUpdateRequest request1 = new BatchUpdateRequest();
        request1.setResourceId(1824502L);
        request1.setExerciseSource("6");
        request1.setCorrectorId(6160381L);
        request1.setCorrectorRole("teacher");
        request1.setResult("1");
        List<Long> list1 = new ArrayList<>();
        list1.add(81951184789L);
        request1.setStudentIds(list1);
        request1.setQuestionId(81951206570L);
        request1.setBusType(2);
        requestList.add(request1);

        BatchUpdateRequest request2 = new BatchUpdateRequest();
        request2.setResourceId(1824502L);
        request2.setExerciseSource("6");
        request2.setCorrectorId(6160381L);
        request2.setCorrectorRole("teacher");
        request2.setResult("1");
        List<Long> list2 = new ArrayList<>();
        list1.add(81951184789L);
        request2.setStudentIds(list2);
        request2.setQuestionId(21332114L);
        request2.setBusType(2);
        requestList.add(request2);

        BatchUpdateRequest request3 = new BatchUpdateRequest();
        request3.setResourceId(1824502L);
        request3.setExerciseSource("6");
        request3.setCorrectorId(6160381L);
        request3.setCorrectorRole("teacher");
        request3.setResult("2");
        List<Long> list3 = new ArrayList<>();
        list3.add(81951184789L);
        request3.setStudentIds(list3);
        request3.setQuestionId(21332114L);
        request3.setBusType(2);
        requestList.add(request3);*/
        batchUpdateCorrectService.batchUpdateCorrectList(requestList);
    }


}
