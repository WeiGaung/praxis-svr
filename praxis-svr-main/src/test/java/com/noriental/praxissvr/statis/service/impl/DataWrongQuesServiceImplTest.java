package com.noriental.praxissvr.statis.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.noriental.BaseTest;
import com.noriental.praxissvr.answer.bean.StudentExercise;
import com.noriental.praxissvr.answer.util.RedisUtilExtend;
import com.noriental.praxissvr.statis.bean.ClassWrongQuesCount;
import com.noriental.praxissvr.statis.bean.WrongQuesSortType;
import com.noriental.praxissvr.statis.request.FindClassWrongQuesCountRequest;
import com.noriental.praxissvr.statis.service.DataWrongQuesService;
import com.noriental.utils.json.JsonUtil;
import com.noriental.utils.redis.RedisUtil;
import com.noriental.validate.bean.CommonResponse;
import com.sumory.mybatis.pagination.result.PageResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.List;

/**
 * Created by chenlihua on 2016/8/11.
 * praxis-svr
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class DataWrongQuesServiceImplTest extends BaseTest {

    @Autowired
    private DataWrongQuesService dataWrongQuesService;
    @Resource
    private RedisUtilExtend redisUtilExtend;

    @Test
    public void findClassWrongQuesCount() throws Exception {
        FindClassWrongQuesCountRequest request;
        //线上QA的测试用例
        String s = "{\"reqId\":null,\"gradeId\":null,\"classIdList\":[12059,12722,31483,34502,34505,34506,34507," +
                "34508," + "36122,36144,36145,36366,36367,36368,36370],\"subjectId\":13,\"quesTypeId\":null," +
                "\"difficulty\":null," + "\"knowledgeLevel\":null,\"knowledgeId\":null,\"questionId\":null," +
                "\"wrongQuesSortType\":\"TIME_DESC\",\"page\":1,\"limit\":10,\"teacherId\":62951051116," +
                "\"wrongQuesSource\":\"ALL\"}";

        s = "{\"reqId\":null,\"gradeId\":null,\"classIdList\":[2059,12893,35409,35433,35435,36609],\"subjectId\":3,\"quesTypeId\":null,\"difficulty\":null,\"knowledgeLevel\":null,\"knowledgeId\":null,\"questionId\":null,\"wrongQuesSortType\":\"TIME_DESC\",\"page\":1,\"limit\":10,\"teacherId\":61168445,\"wrongQuesSource\":\"ALL\"}";
        request = JsonUtil.readValue(s, FindClassWrongQuesCountRequest.class);
        CommonResponse<PageResult<ClassWrongQuesCount>> resp = dataWrongQuesService.findClassWrongQuesCount(request);
        System.out.println(JSONObject.toJSONString(resp, true));
    }

    @Test
    public void findErrorQuesStudentNames() {
        FindClassWrongQuesCountRequest request;
        String s = "{\"reqId\":null,\"gradeId\":null,\"classIdList\":[2059,12893],\"subjectId\":3," +
                "\"quesTypeId\":null," + "\"difficulty\":null,\"knowledgeLevel\":null,\"knowledgeId\":null," +
                "\"questionId\":2998908," + "\"wrongQuesSortType\":\"WRONG_COUNT_DESC\",\"page\":1,\"limit\":10," +
                "\"teacherId\":61168445," + "\"wrongQuesSource\":\"ALL\"}";
        request = JsonUtil.readValue(s, FindClassWrongQuesCountRequest.class);
        CommonResponse<List<String>> resp = dataWrongQuesService.findErrorQuesStudentNames(request);
        System.out.println(JSONObject.toJSONString(resp.getData(), true));
    }


    @Test
    public void findKnowledgeList() throws Exception {
        FindClassWrongQuesCountRequest request = new FindClassWrongQuesCountRequest();
        request.setPage(1);
        request.setLimit(10);
        request.setQuesTypeId(-1);
        request.setSubjectId(20L);
        request.setClassIdList(Arrays.asList(1986L));
        request.setWrongQuesSortType(WrongQuesSortType.TIME_DESC);
        request.setTeacherId(61119536L);
        request.setQuestionId(10727939L);
        request.setWrongQuesSource(FindClassWrongQuesCountRequest.WrongQuesSourceEnum.ALL);
        CommonResponse<List<String>> resp = dataWrongQuesService.findErrorQuesStudentNames(request);
        System.out.println(JSONObject.toJSONString(resp.getData(), true));
    }

    @Test
    public void testRedis() throws Exception {
        String s = "[{\"index\":1,\"result\":\"1\"}]";
        StudentExercise exercise = new StudentExercise();
        exercise.setResult(s);
       /* redisUtilExtend.redisHset("stuexe_6_1149513_81951164776_6060249".getBytes(), "result".getBytes(),
                object2Bytes(exercise.getResult()));*/


    }

    private byte[] object2Bytes(Object value) {
        if (value == null) {
            return null;
        }
        try (ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream(); ObjectOutputStream outputStream =
                new ObjectOutputStream(arrayOutputStream);) {
            outputStream.writeObject(value);
            outputStream.flush();
            return arrayOutputStream.toByteArray();
        } catch (IOException e) {
            //   logger.error("", e);
        }
        return null;
    }


}