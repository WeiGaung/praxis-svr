package com.noriental.praxissvr.wrongQuestion.serviceImpl;

import com.noriental.BaseTest;
import com.noriental.praxissvr.answer.bean.StudentExercise;
import com.noriental.praxissvr.common.QuestionSort;
import com.noriental.praxissvr.common.QuestionTypeEnum;
import com.noriental.praxissvr.question.bean.Question;
import com.noriental.praxissvr.question.request.FindQuestionsRequest;
import com.noriental.praxissvr.question.response.FindQuestionsResponse;
import com.noriental.praxissvr.questionSearch.service.QuestionSearchService;
import com.noriental.praxissvr.wrongQuestion.request.*;
import com.noriental.praxissvr.wrongQuestion.response.*;
import com.noriental.praxissvr.wrongQuestion.service.WrongQuestionService;
import com.noriental.utils.json.JsonUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by kate on 2016/11/21.
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class ServiceTest extends BaseTest {
    @Autowired
    private WrongQuestionService wrongQuestionService;
    @Autowired
    private QuestionSearchService questionSearchService;

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Test
    public void testFindWrongQuesSubjectStatis() throws Exception {
        WrongQuesSubjectStatisReq wrongQuesGroupReq = new WrongQuesSubjectStatisReq();
        wrongQuesGroupReq.setStudentId(81198700L);
        WrongQuesSubjectStatisResp wrongQuesSubjectStatisResp = wrongQuestionService.findWrongQuestionGroup
                (wrongQuesGroupReq);
        logger.info(JsonUtil.obj2Json(wrongQuesSubjectStatisResp));
    }


    @Test
    public void testFindWrongQuestionChapter() throws Exception {
        WrongQuesChapQueryReq wrongQuesChapQueryReq = new WrongQuesChapQueryReq();
        wrongQuesChapQueryReq.setStudentId(81148400L);
        wrongQuesChapQueryReq.setSubjectId(20L);
        wrongQuesChapQueryReq.setCurrentPage(1);
        wrongQuesChapQueryReq.setFromIndex(236625L);
        wrongQuesChapQueryReq.setPageSize(5);
       /* wrongQuesChapQueryReq.setDirectoryId(482L);*/
      /*  wrongQuesChapQueryReq.setChapterLevel(1);
        wrongQuesChapQueryReq.setChapterId(7141L);*/
        WrongQuesChapQueryResp wrongQuesChapQueryResp = wrongQuestionService.findWrongQuestionChapter
                (wrongQuesChapQueryReq);
        logger.info(JsonUtil.obj2Json(wrongQuesChapQueryResp));
    }

    @Test
    public void testFindWrongQuestionHis() throws Exception {
        // studentId ":81951087627," questionId ":6326660
        WrongQuestionHisReq wrongQuestionHisReq;
        String s = "{\"reqId\":null,\"studentId\":81951096610,\"questionId\":10791859,\"pageSize\":0,\"currentPage\":0}";
        wrongQuestionHisReq = JsonUtil.readValue(s, WrongQuestionHisReq.class);
        WrongQuestionHisResp wrongQuesChapQueryResp = wrongQuestionService.findWrongQuestionHis(wrongQuestionHisReq);
        logger.info(JsonUtil.obj2Json(wrongQuesChapQueryResp));
    }


    @Test
    public void testFindWrongQuestion() throws Exception {
        WrongQuesQueryReq wrongQuesChapQueryReq = new WrongQuesQueryReq();
        String s = "{\"reqId\":null,\"studentId\":82951185593,\"subjectId\":20,\"directoryId\":null,\"chapterId\":18827,\"chapterLevel\":2,\"pageSize\":5,\"fromIndex\":0,\"chapterOrKnowledge\":\"4\"}";
        wrongQuesChapQueryReq = JsonUtil.readValue(s, WrongQuesQueryReq.class);
        WrongQuesQueryResp wrongQuesChapQueryResp = wrongQuestionService.findWrongQuestion(wrongQuesChapQueryReq);
        logger.info(JsonUtil.obj2Json(wrongQuesChapQueryResp));
    }


    @Test
    public void testFindWrongQuestions() throws Exception {
        WrongQuesListReq wrongQuesListReq = new WrongQuesListReq();
        wrongQuesListReq.setStudentId(8136100L);
        //1知识点已做题目2知识点错题3章节已做题目4章节错题
        wrongQuesListReq.setDataType("4");
        wrongQuesListReq.setKnowledgeId(229L);
        wrongQuesListReq.setLevel(1);
        WrongQuesListResp wrongQuesListResp = wrongQuestionService.findWrongQuestions(wrongQuesListReq);
        logger.info(JsonUtil.obj2Json(wrongQuesListResp));

    }

    @Test
    public void testFindWrongQuestionsNum() throws Exception {
        WrongQuesNumQueryReq wrongQuesNumQueryReq = new WrongQuesNumQueryReq();
        wrongQuesNumQueryReq.setStudentId(8133149L);
        List<Long> questionIds = new ArrayList<>();
        questionIds.add(1204465L);
        questionIds.add(1394356L);
        wrongQuesNumQueryReq.setQuestionIds(questionIds);
        WrongQuesNumQueryResp wrongQuesListResp = wrongQuestionService.findWrongQuestionsNum(wrongQuesNumQueryReq);
        logger.info(JsonUtil.obj2Json(wrongQuesListResp));

    }

    @Test
    public void testfindStudentAnswer() throws Exception {
        StudentExercise se = new StudentExercise();
        se.setQuestionId(7561928L);
        se.setResourceId(767870L);
        se.setYear(2016);
        se.setClassId(30057L);
        List<StudentExercise> studentExercises = wrongQuestionService.findStudentAnswer(se);
        logger.info(JsonUtil.obj2Json(studentExercises));

    }

    @Test
    public void testfindStudentExerciseInfo() throws Exception {

        StudentExercise studentExercises = wrongQuestionService.findStudentExerciseInfo
                ("entity_student_exercise_2016_28", 527411L);
        logger.info(JsonUtil.obj2Json(studentExercises));

    }

    @Test
    public void testQuestionSearchService() throws Exception {
        List<Question> list = questionSearchService.getQuesListByIds(Collections.singletonList(7670179L));
        logger.info(JsonUtil.obj2Json(list));
    }


    @Test
    public void testQuestion() throws Exception {
        FindQuestionsRequest questionsRequest = new FindQuestionsRequest();
        questionsRequest.setPageNo(1);
        questionsRequest.setPageSize(3);
       /* questionsRequest.setQuestionType(QuestionType.DUO_XUAN);
        questionsRequest.setUnitIds(Collections.singletonList(617L));
        questionsRequest.setQuestionType(QuestionTypeEnum.ALL);
        questionsRequest.setNewFormat(true);
        questionsRequest.setQueryTrunk(true);
        questionsRequest.setSubjectId(1L);
        questionsRequest.setPageable(false);
        questionsRequest.setIds(Arrays.asList(201331L));
        questionsRequest.setUploadId(6161545L);
        questionsRequest.setSubjectId(1L);
        questionsRequest.setUploadId(6161745L);
        questionsRequest.setStates(Collections.singletonList(QuestionState.ENABLED));
        questionsRequest.setDifficulty(Difficulty.EASY);
        questionsRequest.setUploadTimeStart(new GregorianCalendar(2016, 2, 16, 0, 59, 34).getTime());
        questionsRequest.setUploadTimeEnd(new GregorianCalendar(2016, 2, 16, 0, 59, 36).getTime());
       QuesSearchPerm perm = new QuesSearchPerm();
        perm.setCurrentOrgId(82L);
        perm.setCurrentOrgType(2);
        questionsRequest.setQuesSearchPerm(perm);*/
        questionsRequest.setQuestionType(QuestionTypeEnum.ALL);
        questionsRequest.setBasic(true);
        questionsRequest.setSorts(Arrays.asList(QuestionSort.UPDATE_TIME_DESC));
        questionsRequest.setIds(Collections.singletonList(45953L));
        // questionsRequest = JsonUtil.readValue(s, FindQuestionsRequest.class);
        FindQuestionsResponse response = questionSearchService.findSolrQuestions(questionsRequest);
        logger.info(JsonUtil.obj2Json(response.getQuestionList()));
    }


    //{"reqId":null,"studentId":81951115719,"resourceId":776567,"exerciseSource":"8","redoSource":null,
    // "subQuesSort":false,"structIdList":null,"forPad":true}
    @Test
    public void findIntellStudentAnswer() throws Exception {
        StudentExercise studentExercises = new StudentExercise();
        String s = "{\"studentId\":81951117330,\"exerciseSource\":\"6\",\"exerciseSourceZh\":\"作业\",\"resourceId\":908072}";
        studentExercises = JsonUtil.readValue(s, StudentExercise.class);
        List<StudentExercise> list = wrongQuestionService.findIntellStudentAnswer(studentExercises);
        logger.info(JsonUtil.obj2Json(list));

    }


    @Test
    public void findErrorQuestionsNum() throws Exception {
        String s = "{\"reqId\":null,\"pointId\":14139,\"level\":3,\"days\":30,\"dataType\":2,\"sortByErrorNum\":null," +
                "\"studentId\":81951063991}";
        ErrorQuesChapQueryReq req;
        req = JsonUtil.readValue(s, ErrorQuesChapQueryReq.class);
        WrongQuesNumQueryResp resp = wrongQuestionService.findErrorQuestionsNum(req);
        logger.info(JsonUtil.obj2Json(resp.getDataList()));

    }

    //{"reqId":null,"studentId":81951063991,"questionId":10555209,"pageSize":0,"currentPage":0}

    @Test
    public void findWrongQuestions() throws Exception {
        String s = "{\"reqId\":null,\"studentId\":82951191043,\"level\":2,\"knowledgeId\":24978,\"dataType\":\"3\"}";
        WrongQuestionHisReq req;
        req = JsonUtil.readValue(s, WrongQuestionHisReq.class);
        WrongQuestionsResp resp = wrongQuestionService.findWrongQuestions(req);
        logger.info(JsonUtil.obj2Json(resp.getResultList()));

    }

}
