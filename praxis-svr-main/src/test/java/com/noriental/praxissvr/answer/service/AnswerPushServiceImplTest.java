package com.noriental.praxissvr.answer.service;

import com.noriental.BaseTest;
import com.noriental.log.TraceKeyHolder;
import com.noriental.praxissvr.answer.bean.ExerciseTypeEnum;
import com.noriental.praxissvr.answer.bean.StudentExercise;
import com.noriental.praxissvr.answer.request.UpdateFinshCorrectRequest;
import com.noriental.utils.json.JsonUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.invoke.MethodHandles;
import java.util.Collections;

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = {"classpath:testApplicationContext.xml"})
public class AnswerPushServiceImplTest extends BaseTest {

    @Autowired
    AnswerPushService answerPushService;
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @Test
    public void updateSubmitAnswer() {
        UpdateFinshCorrectRequest req = new UpdateFinshCorrectRequest(ExerciseTypeEnum.YUXI,2461097L,81951130035L);
        req.setTotalCount(4);
        req.setRightCount(4);
        req.setContain(true);
        req.setTeacher(false);
        TraceKeyHolder.setTraceKey("2847484905");
        answerPushService.updateFinshCorrect(req);

    }
    @Autowired
    StuAnswerService stuAnswerService;
    @Test
    public void notifyTrail() throws Exception{
        String s="{\"id\":861831,\"studentId\":81110319,\"questionId\":5968936,\"exerciseSource\":\"1\"," +
                "\"exerciseSourceZh\":\"上课\",\"createTime\":1494398768000," +
                "\"submitAnswer\":\"[\\\"group1\\\\/M00\\\\/9A\\\\/34\\\\/CgoEc1kSty6AFRoiAAABJ-c7Ohg322.svg\\\"," +
                "\\\"group1\\\\/M00\\\\/87\\\\/53\\\\/CgoEclkSty6ADkswAAABJ8eR6WI932.svg\\\"]\",\"result\":\"1\"," +
                "\"submitTime\":1494398768000,\"classId\":1974,\"correctorId\":61110317," +
                "\"correctorRole\":\"teacher\",\"correctorTime\":1494470593492,\"lastUpdateTime\":1494470593492," +
                "\"structId\":3,\"resourceId\":590139,\"redoSourceZh\":\"\",\"year\":2016,\"isNew\":0}";

        StudentExercise se=new StudentExercise();
         se=JsonUtil.readValue(s,StudentExercise.class);
        StudentExercise exercise=new StudentExercise();
        exercise.setStudentExerciseList(Collections.singletonList(se));
        int operaType=1;
        stuAnswerService.notifyTrail(exercise,operaType,null);

    }





}