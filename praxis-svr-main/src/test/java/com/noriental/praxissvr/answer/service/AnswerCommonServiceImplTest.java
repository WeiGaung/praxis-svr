package com.noriental.praxissvr.answer.service;

import com.noriental.BaseTest;
import com.noriental.praxissvr.LoaderRunnerUnit;
import com.noriental.praxissvr.answer.request.*;
import com.noriental.praxissvr.answer.response.FindHistoryDataResponse;
import com.noriental.praxissvr.answer.util.RedisUtilExtend;
import com.noriental.praxissvr.brush.bean.StudentWorkAnswer;
import com.noriental.praxissvr.brush.service.StuBrushService;
import com.noriental.praxissvr.common.StuAnswerConstant;
import com.noriental.praxissvr.wrong.bean.SimpleKnowVo;
import com.noriental.utils.json.JsonUtil;
import com.noriental.validate.exception.BizLayerException;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import static com.noriental.praxissvr.answer.service.impl.AnswerCommonServiceImpl.INTELL_WHITE;

public class AnswerCommonServiceImplTest extends BaseTest {

    @Autowired
    AnswerCommonService answerCommonService;
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private Long studentId = 81951051147L;
    private Long resourceId = 275608L;
    private Long questionIdjdt1 = 2530339L;
    private Long questionIdjdt2 = 1392067L;
    private Long questionIdjdt3 = 1304645L;
    private String postil = "[\"a.jpg\"]";
    private String answer = "[\"a.jpg\"]";
    private String exerciseSource = StuAnswerConstant.ExerciseSource.WORK;

    @Autowired
    private StuAnswerService stuAnswerService;
    @Test
    public void test(){
        String s="{\"reqId\":null,\"resourceId\":1235996,\"exerciseSource\":\"7\",\"studentId\":81951087385," +
                "\"updateSubmitAnswerList\":[{\"questionId\":5972216,\"result\":\"[{\\\"index\\\":1," +
                "\\\"result\\\":\\\"6\\\"},{\\\"index\\\":2,\\\"result\\\":\\\"6\\\"},{\\\"index\\\":3," +
                "\\\"result\\\":\\\"6\\\"}]\",\"submitAnswer\":\"[{\\\"index\\\":1," +
                "\\\"indexanswer\\\":\\\"group1\\\\/M00\\\\/A3\\\\/71\\\\/CgoHylpF4c2AE97QAAADbndldpg252.svg\\\"," +
                "\\\"indexanswer_small\\\":\\\"group1\\\\/M00\\\\/A3\\\\/71\\\\/CgoHylpF4c2AE97QAAADbndldpg252.svg" +
                "\\\"},{\\\"index\\\":2," +
                "\\\"indexanswer\\\":\\\"group1\\\\/M01\\\\/A3\\\\/71\\\\/CgoHy1pF4c6ALpB0AAAJqvx78IA733.svg\\\"," +
                "\\\"indexanswer_small\\\":\\\"group1\\\\/M01\\\\/A3\\\\/71\\\\/CgoHy1pF4c6ALpB0AAAJqvx78IA733.svg" +
                "\\\"},{\\\"index\\\":3," +
                "\\\"indexanswer\\\":\\\"group1\\\\/M01\\\\/A3\\\\/71\\\\/CgoHyVpF4dKAdZ2hAAAKLpq8jd0766.svg\\\"," +
                "\\\"indexanswer_small\\\":\\\"group1\\\\/M01\\\\/A3\\\\/71\\\\/CgoHyVpF4dKAdZ2hAAAKLpq8jd0766.svg" +
                "\\\"}]\",\"audioResult\":null},{\"questionId\":5972218,\"result\":\"[{\\\"index\\\":1," +
                "\\\"result\\\":\\\"6\\\"},{\\\"index\\\":2,\\\"result\\\":\\\"6\\\"}]\"," +
                "\"submitAnswer\":\"[{\\\"index\\\":1," +
                "\\\"indexanswer\\\":\\\"group1\\\\/M01\\\\/A3\\\\/71\\\\/CgoHylpF4daAXDvlAAAEm0DOIOk221.svg\\\"," +
                "\\\"indexanswer_small\\\":\\\"group1\\\\/M01\\\\/A3\\\\/71\\\\/CgoHylpF4daAXDvlAAAEm0DOIOk221.svg" +
                "\\\"},{\\\"index\\\":2," +
                "\\\"indexanswer\\\":\\\"group1\\\\/M01\\\\/A3\\\\/71\\\\/CgoHylpF4dqASRmaAAAKIjskBQI112.svg\\\"," +
                "\\\"indexanswer_small\\\":\\\"group1\\\\/M01\\\\/A3\\\\/71\\\\/CgoHylpF4dqASRmaAAAKIjskBQI112.svg" +
                "\\\"}]\",\"audioResult\":null},{\"questionId\":5972224,\"result\":\"[{\\\"index\\\":1," +
                "\\\"result\\\":\\\"6\\\"},{\\\"index\\\":2,\\\"result\\\":\\\"6\\\"}]\"," +
                "\"submitAnswer\":\"[{\\\"index\\\":1," +
                "\\\"indexanswer\\\":\\\"group1\\\\/M00\\\\/A3\\\\/72\\\\/CgoHyVpF4eGAM0jpAAAl7E4Tvuw389.svg\\\"," +
                "\\\"indexanswer_small\\\":\\\"group1\\\\/M00\\\\/A3\\\\/72\\\\/CgoHyVpF4eGAM0jpAAAl7E4Tvuw389.svg" +
                "\\\"},{\\\"index\\\":2," +
                "\\\"indexanswer\\\":\\\"group1\\\\/M00\\\\/A3\\\\/72\\\\/CgoHylpF4eeAHjxFAAAxG7IDn_U111.svg\\\"," +
                "\\\"indexanswer_small\\\":\\\"group1\\\\/M00\\\\/A3\\\\/72\\\\/CgoHylpF4eeAHjxFAAAxG7IDn_U111.svg" +
                "\\\"}]\",\"audioResult\":null},{\"questionId\":5972236,\"result\":\"[{\\\"index\\\":1," +
                "\\\"result\\\":\\\"6\\\"},{\\\"index\\\":2,\\\"result\\\":\\\"6\\\"},{\\\"index\\\":3," +
                "\\\"result\\\":\\\"6\\\"}]\",\"submitAnswer\":\"[{\\\"index\\\":1," +
                "\\\"indexanswer\\\":\\\"group1\\\\/M00\\\\/A3\\\\/72\\\\/CgoHylpF4eyAPH8oAAAG_WMe0Ck261.svg\\\"," +
                "\\\"indexanswer_small\\\":\\\"group1\\\\/M00\\\\/A3\\\\/72\\\\/CgoHylpF4eyAPH8oAAAG_WMe0Ck261.svg" +
                "\\\"},{\\\"index\\\":2,\\\"indexanswer\\\":\\\"group1\\\\/M00\\\\/A3\\\\/72\\\\/CgoHyVpF4e2AOd4" +
                "-AAAJvKxPCfM042.svg\\\"," +
                "\\\"indexanswer_small\\\":\\\"group1\\\\/M00\\\\/A3\\\\/72\\\\/CgoHyVpF4e2AOd4-AAAJvKxPCfM042.svg" +
                "\\\"},{\\\"index\\\":3," +
                "\\\"indexanswer\\\":\\\"group1\\\\/M00\\\\/A3\\\\/72\\\\/CgoHy1pF4fGALv4ZAAAM9AM_x8o278.svg\\\"," +
                "\\\"indexanswer_small\\\":\\\"group1\\\\/M00\\\\/A3\\\\/72\\\\/CgoHy1pF4fGALv4ZAAAM9AM_x8o278.svg" +
                "\\\"}]\",\"audioResult\":null},{\"questionId\":5972239,\"result\":\"[{\\\"index\\\":1," +
                "\\\"result\\\":\\\"7\\\"},{\\\"index\\\":2,\\\"result\\\":\\\"7\\\"},{\\\"index\\\":3," +
                "\\\"result\\\":\\\"7\\\"},{\\\"index\\\":4,\\\"result\\\":\\\"7\\\"},{\\\"index\\\":5," +
                "\\\"result\\\":\\\"7\\\"}]\",\"submitAnswer\":\"[{\\\"index\\\":1,\\\"indexanswer\\\":\\\"\\\"," +
                "\\\"indexanswer_small\\\":\\\"\\\"},{\\\"index\\\":2,\\\"indexanswer\\\":\\\"\\\"," +
                "\\\"indexanswer_small\\\":\\\"\\\"},{\\\"index\\\":3,\\\"indexanswer\\\":\\\"\\\"," +
                "\\\"indexanswer_small\\\":\\\"\\\"},{\\\"index\\\":4,\\\"indexanswer\\\":\\\"\\\"," +
                "\\\"indexanswer_small\\\":\\\"\\\"},{\\\"index\\\":5,\\\"indexanswer\\\":\\\"\\\"," +
                "\\\"indexanswer_small\\\":\\\"\\\"}]\",\"audioResult\":null},{\"questionId\":5972248," +
                "\"result\":\"6\",\"submitAnswer\":\"[\\\"group1\\\\/M01\\\\/A3\\\\/72" +
                "\\\\/CgoHy1pF4fiAcS7SAAAIWlWPua4011.svg\\\"]\",\"audioResult\":null},{\"questionId\":5972315," +
                "\"result\":\"6\",\"submitAnswer\":\"[\\\"group1\\\\/M00\\\\/A3\\\\/72" +
                "\\\\/CgoHylpF4f2AWzQpAAAR4PEiW60314.svg\\\"]\",\"audioResult\":null},{\"questionId\":5972257," +
                "\"result\":\"2\",\"submitAnswer\":\"B\",\"audioResult\":null},{\"questionId\":5972259," +
                "\"result\":\"2\",\"submitAnswer\":\"C\",\"audioResult\":null},{\"questionId\":5972262," +
                "\"result\":\"2\",\"submitAnswer\":\"B\",\"audioResult\":null},{\"questionId\":5972285," +
                "\"result\":\"2\",\"submitAnswer\":\"B\",\"audioResult\":null},{\"questionId\":5972295," +
                "\"result\":\"2\",\"submitAnswer\":\"B\",\"audioResult\":null},{\"questionId\":5972301," +
                "\"result\":\"2\",\"submitAnswer\":\"B\",\"audioResult\":null}],\"correctorRole\":\"student\"," +
                "\"correctorId\":81951087385,\"redoSource\":null,\"subExerciseSource\":0," +
                "\"uniqueKey\":\"df79a23339dd4d6da317137a2194ac90\"}";
        StudentWorkAnswer studentWorkAnswerKnowSub=JsonUtil.readValue(s,StudentWorkAnswer.class);
        String ss="{\"topicId\":14139,\"unitId\":2254,\"moduleId\":220,\"subjectId\":5,\"directoryId\":null}";
        SimpleKnowVo simpleKnowVo=JsonUtil.readValue(ss,SimpleKnowVo.class);
        Map<Long, SimpleKnowVo> topicKnowMap=new HashMap<>();
        topicKnowMap.put(14139L,simpleKnowVo);
        stuAnswerService.statisCorrect(9927296L, studentWorkAnswerKnowSub, topicKnowMap);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void updateSubmitAnswer() {
        UpdateSubmitAnswerRequest request ;
        String s = "{\"reqId\":null,\"resourceId\":2460604,\"exerciseSource\":\"1\",\"studentId\":81951096610,\"updateSubmitAnswerList\":[{\"questionId\":21332822,\"result\":\"6\",\"submitAnswer\":\"[\\\"group1\\\\/M00\\\\/27\\\\/72\\\\/CjwAKFuu2mOAGPkYAADJkIIuAAU989.svg\\\",\\\"group1\\\\/M00\\\\/27\\\\/72\\\\/CjwAKFuu2oGAM_KhAABT0kqZzGI846.svg\\\"]\",\"audioResult\":null,\"totalScore\":null,\"parentQuestionId\":null,\"structId\":3,\"subjectId\":5,\"questionTypeId\":50,\"matrix\":\"[{\\\"confirmed_data\\\":\\\"Mid-autumn festival is one of the most important day for Chinese people. The families will get together and enjoy the reunion. Mooncake is the typical food. People will have it while appreciate the full moon. At this time, the old generation like to tell the story of Hou Yi and his wife, which is the coming of this festival. Hou Yi is a man with great strength. He shot down nine suns and left only one. While his wife was so curious to taste the pill which Hou Yi told her to keep it for him. Then his wife flied to the moon and left Hou Yi forever. It is said that the wife lives in the moon. So when we look at moon, she is watching us.  \\\",\\\"index\\\":0,\\\"recognize\\\":\\\"Mid-autumn festival is one of the most important day for Chinese people. The families will get together and enjoy the reunion. Mooncake is the typical food. People will have it while appreciate the full moon. At this time, the old generation like to tell the story of Hou Yi and his wife, which is the coming of this festival. Hou Yi is a man with great strength. He shot down nine suns and left only one. While his wife was so curious to taste the pill which Hou Yi told her to keep it for him. Then his wife flied to the moon and left Hou Yi forever. It is said that the wife lives in the moon. So when we look at moon, she is watching us.  \\\"},{\\\"confirmed_data\\\":\\\"Mid-autumn festival is one of the most important day for Chinese people. The families will get together and enjoy the reunion. Mooncake is the typical food. People will have it while appreciate the full moon. At this time, the old generation like to tell the story of Hou Yi and his wife, which is the coming of this festival. Hou Yi is a man with great strength. He shot down nine suns and left only one. While his wife was so curious to taste the pill which Hou Yi told her to keep it for him. Then his wife flied to the moon and left Hou Yi forever. It is said that the wife lives in the moon. So when we look at moon, she is watching us.  \\\",\\\"index\\\":1,\\\"recognize\\\":\\\"Mid-autumn festival is one of the most important day for Chinese people. The families will get together and enjoy the reunion. Mooncake is the typical food. People will have it while appreciate the full moon. At this time, the old generation like to tell the story of Hou Yi and his wife, which is the coming of this festival. Hou Yi is a man with great strength. He shot down nine suns and left only one. While his wife was so curious to taste the pill which Hou Yi told her to keep it for him. Then his wife flied to the moon and left Hou Yi forever. It is said that the wife lives in the moon. So when we look at moon, she is watching us.  \\\"}]\",\"intelligent\":null}],\"correctorRole\":\"student\",\"correctorId\":82951063997,\"redoSource\":null,\"subExerciseSource\":null,\"uniqueKey\":\"ab6b3b9a8bd94ebaa3897d8b84851c23\"}";

        request = JsonUtil.readValue(s, UpdateSubmitAnswerRequest.class);
        try {
            //logger.info(JsonUtil.obj2Json(answerCommonService.updateSubmitAnswer(request)));
            boolean falg = false;
            logger.info(JsonUtil.obj2Json(answerCommonService.updateSubmitAnswer(request)));
        } catch (Exception e) {
            if (e instanceof BizLayerException) {
                logger.error(JsonUtil.obj2Json(e));
            } else {
                logger.error("eee", e);
            }
        }
        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Autowired
    StuBrushService stuBrushService;


    @Test
    public void updateCorrect() {

        UpdateCorrectRequest studentExercise;
        String s = "{\"reqId\":null,\"resourceId\":1235050,\"exerciseSource\":\"8\",\"studentId\":81951085079," +
                "\"questionId\":11346800,\"correctorRole\":\"teacher\",\"correctorId\":61951107816," +
                "\"result\":\"[{\\\"result\\\":1,\\\"index\\\":\\\"1\\\"}]\",\"redoSource\":null}";

        s="{\"reqId\":null,\"resourceId\":16060703,\"exerciseSource\":\"5\",\"studentId\":81951128338," +
                "\"questionId\":5967777,\"correctorRole\":\"teacher\",\"correctorId\":160," +
                "\"result\":\"[{\\\"index\\\":1,\\\"result\\\":\\\"2\\\"}]\",\"redoSource\":null}";
        studentExercise = JsonUtil.readValue(s, UpdateCorrectRequest.class);
        try

        {
            logger.info(JsonUtil.obj2Json(answerCommonService.updateCorrect(studentExercise)));
            Thread.sleep(10000);
        } catch (Exception e)

        {
            if (e instanceof BizLayerException) {
                logger.error(JsonUtil.obj2Json(e));
            } else {
                logger.error("eee", e);
            }
        }

    }


    @Test
    public void updatePostil() {
        UpdatePostilRequest studentExercise = new UpdatePostilRequest();
        String s = "{\"reqId\":\"031379461338\",\"resourceId\":1245830,\"exerciseSource\":\"8\"," +
                "\"studentId\":81951066375,\"questionId\":11347246," +
                "\"postilTeacher\":\"[\\\"group1/M00/26/BD/CjwAKFkZcweAPDmjAAAULAuginQ810.svg\\\"," +
                "\\\"group1/M00/26/BD/CjwAKFkZcweAQcNkAAAl3i_WVWk827.svg\\\"," +
                "\\\"group1/M00/26/C3/CjwAKFkewEuAZu0jAACL2PH7WRI450.svg\\\"]\"}";
        studentExercise = JsonUtil.readValue(s, UpdatePostilRequest.class);

        logger.info(JsonUtil.obj2Json(answerCommonService.updatePostil(studentExercise)));
    }

    @Test
    public void updateCorrectPostilCon() {
        final ExecutorService executorService = Executors.newFixedThreadPool(2);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                //                setRequestid();
                UpdatePostilRequest p = new UpdatePostilRequest();
                p.setResourceId(resourceId);
                p.setStudentId(studentId);
                p.setExerciseSource(exerciseSource);
                p.setQuestionId(questionIdjdt2);
                p.setPostilTeacher(postil);
                logger.info(JsonUtil.obj2Json(answerCommonService.updatePostil(p)));
            }
        };


        Runnable runnable1 = new Runnable() {
            @Override
            public void run() {
                //                setRequestid();
                UpdateCorrectRequest studentExercise = new UpdateCorrectRequest();
                studentExercise.setCorrectorId(1L);
                studentExercise.setCorrectorRole(StuAnswerConstant.CorrectorRole.TEACHER);
                studentExercise.setStudentId(studentId);
                studentExercise.setExerciseSource(exerciseSource);
                studentExercise.setQuestionId(questionIdjdt2);
                studentExercise.setResourceId(resourceId);
                studentExercise.setResult("2");
                logger.info(JsonUtil.obj2Json(answerCommonService.updateCorrect(studentExercise)));
            }
        };
        executorService.execute(runnable);
        executorService.execute(runnable1);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executorService.shutdown();
    }


    @Test
    public void findQuesAnswsOnBatch() {
        FindQuesAnswsOnBatchRequest in;

        String s = "\"reqId\":null,\"questionId\":9906966,\"resourceId\":2460587,\"exerciseSource\":\"8\",\"structIdList\":null,\"sortType\":\"TIME\",\"forPad\":false}";
        try {
            in = JsonUtil.readValue(s, FindQuesAnswsOnBatchRequest.class);
            logger.info(JsonUtil.obj2Json(answerCommonService.findQuesAnswsOnBatch(in)));
        } catch (Exception e) {
            if (e instanceof BizLayerException) {
                logger.error(JsonUtil.obj2Json(e));
            } else {
                logger.error("eee", e);
            }
        }

    }

    @Test
    public void findStuListQuesAnsw() {
        /*FindSomeStuQuesAnswOnBatchRequest in;
        String s = "{\"reqId\":null,\"studentIdList\":[{81951063991,81951063992,81951096609}],\"questionId\":11331197,\"resourceId\":2460557,\"exerciseSource\":\"6\"}";*/

        FindSomeStuQuesAnswOnBatchRequest in = new FindSomeStuQuesAnswOnBatchRequest();
        in.setExerciseSource("6");
        in.setQuestionId(11331197L);
        in.setResourceId(2460557L);
        List<Long> longs = new ArrayList<>();
        longs.add(81951063991L);
        longs.add(81951063992L);
        longs.add(81951096609L);
        in.setStudentIdList(longs);
        try {
            //in = JsonUtil.readValue(s, FindSomeStuQuesAnswOnBatchRequest.class);
            logger.info(JsonUtil.obj2Json(answerCommonService.findStuListQuesAnsw(in)));
        } catch (Exception e) {
            if (e instanceof BizLayerException) {
                logger.error(JsonUtil.obj2Json(e));
            } else {
                logger.error("eee", e);
            }
        }

    }

    @Test
    //    @Ignore
    public void findStuAnswsOnBatch() {

        //String s = "{\"reqId\":null,\"studentId\":81951143071,\"resourceId\":2460639,\"exerciseSource\":\"7\",\"redoSource\":null,\"subQuesSort\":false,\"structIdList\":null,\"forPad\":true}";

        //String s1="{\"reqId\":null,\"studentId\":81104098,\"resourceId\":2460445,\"exerciseSource\":\"6\",\"redoSource\":null,\"subQuesSort\":false,\"structIdList\":null,\"forPad\":true}";
        String s1="{\"reqId\":null,\"studentId\":81951190261,\"resourceId\":2618030,\"exerciseSource\":\"8\",\"redoSource\":null,\"subQuesSort\":true,\"structIdList\":null,\"forPad\":true}";
        FindStuAnswsOnBatchRequest i = JsonUtil.readValue(s1, FindStuAnswsOnBatchRequest.class);

        //for (int a=0;a<1000;a++) {

        logger.info(JsonUtil.obj2Json(answerCommonService.findStuAnswsOnBatch(i)));
        //}


    }

    @Test
    public void findStuQuesAnswOnBatch() {
        FindStuQuesAnswOnBatchRequest in = new FindStuQuesAnswOnBatchRequest();
        in.setExerciseSource(StuAnswerConstant.ExerciseSource.LESSON);
        String s = "{\"reqId\":null,\"studentId\":81951096609,\"questionId\":11331197,\"resourceId\":2460557,\"exerciseSource\":\"6\"}";
        in = JsonUtil.readValue(s, FindStuQuesAnswOnBatchRequest.class);
        logger.info(JsonUtil.obj2Json(answerCommonService.findStuQuesAnswOnBatch(in)));
    }

    private void addRecords(List<UpdateSubmitAnswer> updateSubmitAnswerList) {
        UpdateSubmitAnswer u1 = new UpdateSubmitAnswer();

        //
        UpdateSubmitAnswer u3 = new UpdateSubmitAnswer();
        u3.setQuestionId(questionIdjdt1);
        u3.setSubmitAnswer("A");
        u3.setResult("[{\"index\":1,\"result\":2}]");
        updateSubmitAnswerList.add(u3);


    }

    private void addRecords7x5(List<UpdateSubmitAnswer> updateSubmitAnswerList) {
        UpdateSubmitAnswer u1 = new UpdateSubmitAnswer();
        u1.setQuestionId(4001082L);
        u1.setSubmitAnswer("[A,B,C,D,E]");
        u1.setResult("[1,2,7,2,2]");
        updateSubmitAnswerList.add(u1);
    }

    @Test
    @Ignore
    public void updateSubmitAnswerStress() {

        final AtomicLong al = new AtomicLong(studentId + 351 + 105);
        Runnable run = new Runnable() {
            @Override
            public void run() {
                //                setRequestid();
                UpdateSubmitAnswerRequest request = new UpdateSubmitAnswerRequest();

                request.setExerciseSource("8");
                request.setCorrectorId(1L);
                request.setCorrectorRole(StuAnswerConstant.CorrectorRole.STUDENT);
                request.setResourceId(resourceId);
                request.setStudentId(al.incrementAndGet());
                logger.info("studentid:" + al);

                List<UpdateSubmitAnswer> updateSubmitAnswerList = new ArrayList<>();
                request.setUpdateSubmitAnswerList(updateSubmitAnswerList);
                addRecords(updateSubmitAnswerList);
                long st = System.currentTimeMillis();
                logger.info(JsonUtil.obj2Json(answerCommonService.updateSubmitAnswer(request)) + " cost:" + (System
                        .currentTimeMillis() - st));

            }
        };
        LoaderRunnerUnit.run(run, 50, 1);


    }

    @Test
    public void intelligenceCorrectAnswerTest() {
        IntelligenceAnswerRequest in = new IntelligenceAnswerRequest();
        String s="{\"reqId\":null,\"resourceId\":2460662,\"exerciseSource\":\"8\",\"studentId\":81951063991,\"intelligenceAnswerList\":[{\"questionId\":11015016,\"result\":\"[{\\\"index\\\":1,\\\"result\\\":\\\"1\\\"},{\\\"index\\\":2,\\\"result\\\":\\\"1\\\"},{\\\"index\\\":3,\\\"result\\\":\\\"1\\\"},{\\\"index\\\":4,\\\"result\\\":\\\"1\\\"},{\\\"index\\\":5,\\\"result\\\":\\\"1\\\"}]\",\"answerSource\":\"[{\\\"index\\\":1,\\\"recogniseResult\\\":\\\"is\\\",\\\"matrix\\\":\\\"\\\"},{\\\"index\\\":2,\\\"recogniseResult\\\":\\\"are\\\",\\\"matrix\\\":\\\"\\\"},{\\\"index\\\":3,\\\"recogniseResult\\\":\\\"are\\\",\\\"matrix\\\":\\\"\\\"},{\\\"index\\\":4,\\\"recogniseResult\\\":\\\"is\\\",\\\"matrix\\\":\\\"\\\"},{\\\"index\\\":5,\\\"recogniseResult\\\":\\\"Are\\\",\\\"matrix\\\":\\\"\\\"}]\",\"correctorRole\":\"[{\\\"index\\\":1,\\\"corrector_role\\\":\\\"ai\\\"},{\\\"index\\\":2,\\\"corrector_role\\\":\\\"ai\\\"},{\\\"index\\\":3,\\\"corrector_role\\\":\\\"ai\\\"},{\\\"index\\\":4,\\\"corrector_role\\\":\\\"ai\\\"},{\\\"index\\\":5,\\\"corrector_role\\\":\\\"ai\\\"}]\",\"matrix\":null,\"totalScore\":null}],\"correctorId\":1,\"timeStamp\":1534387838066}";
        in=JsonUtil.readValue(s,IntelligenceAnswerRequest.class);
        answerCommonService.intelligenceCorrectAnswer(in);
    }

    //{"reqId":null,"exerciseSource":"7","resourceId":584966,"questionId":6326415,"studentId":81951087646,
    // "correctorId":6160381,"structId":4}]
    @Test
    public void submitIntellCorrectAnswer() {

        IntellCorrectStuQueRequest in = new IntellCorrectStuQueRequest();
        String s = "{\"reqId\":null,\"exerciseSource\":\"8\",\"resourceId\":775212,\"questionId\":7669071," +
                "\"studentId\":81951085079,\"correctorId\":61951107816,\"structId\":4,\"postilUrl\":null}";
        in = JsonUtil.readValue(s, IntellCorrectStuQueRequest.class);
        answerCommonService.submitIntellCorrectAnswer(in);

    }

   /* {
        "reqId":"031237615135", "exerciseSource":"8", "resourceId":775513, "questionId":2481727, "studentId":
        81951115716, "correctorId":61951115715, "structId":3, "postilUrl":
        "[\"group1/M00/14/30/CgoHylmFbwOAbs83AAFoQSKphDY205.svg\"]"
    }*/

    @Test
    public void submitIntell() {

        IntellCorrectStuQueRequest in = new IntellCorrectStuQueRequest();
        String s = "{\"reqId\":null,\"exerciseSource\":\"6\",\"resourceId\":777643,\"questionId\":6060455," +
                "\"studentId\":81951086159,\"correctorId\":61951087552,\"structId\":3," +
                "\"postilUrl\":\"[\\\"group1/M01/15/EB/CgoHylmNQrWAEnOvAAB8I2Kf2fc718.svg\\\"]\"}";
        in = JsonUtil.readValue(s, IntellCorrectStuQueRequest.class);
        answerCommonService.submitIntellInfo(in);

    }

    @Test
    public void intellAnswer() {
        IntelligenceAnswerRequest in = new IntelligenceAnswerRequest();
        String s = "{\"reqId\":null,\"resourceId\":1233673,\"exerciseSource\":\"8\",\"studentId\":81104098," +
                "\"intelligenceAnswerList\":[{\"questionId\":11264835,\"result\":\"[{\\\"index\\\":1," +
                "\\\"result\\\":\\\"2\\\"}]\",\"answerSource\":\"[{\\\"index\\\":1,\\\"recogniseResult\\\":\\\"r\\\"," +
                "\\\"matrix\\\":\\\"group1/M00/3B/EF/CgoHylove0WAIm5eAAABItHjgyA637.txt\\\"}]\"," +
                "\"correctorRole\":\"[{\\\"index\\\":1,\\\"corrector_role\\\":\\\"ai\\\"}]\",\"matrix\":null}]," +
                "\"correctorId\":1,\"timeStamp\":1513061191792}";
        String s1="{\"reqId\":null,\"resourceId\":2460057,\"exerciseSource\":\"7\",\"studentId\":82951258463,\"intelligenceAnswerList\":[{\"questionId\":2630105,\"result\":\"[{\\\"index\\\":1,\\\"result\\\":\\\"2\\\"}]\",\"answerSource\":\"[{\\\"index\\\":1,\\\"recogniseResult\\\":\\\"多\\\",\\\"matrix\\\":\\\"\\\"}]\",\"correctorRole\":\"[{\\\"index\\\":1,\\\"corrector_role\\\":\\\"ai\\\"}]\",\"matrix\":null,\"totalScore\":null},{\"questionId\":2630106,\"result\":\"[{\\\"index\\\":1,\\\"result\\\":\\\"2\\\"}]\",\"answerSource\":\"[{\\\"index\\\":1,\\\"recogniseResult\\\":\\\"n\\\",\\\"matrix\\\":\\\"\\\"}]\",\"correctorRole\":\"[{\\\"index\\\":1,\\\"corrector_role\\\":\\\"ai\\\"}]\",\"matrix\":null,\"totalScore\":null},{\"questionId\":2630107,\"result\":\"[{\\\"index\\\":1,\\\"result\\\":\\\"2\\\"},{\\\"index\\\":2,\\\"result\\\":\\\"2\\\"}]\",\"answerSource\":\"[{\\\"index\\\":1,\\\"recogniseResult\\\":\\\"徐然\\\",\\\"matrix\\\":\\\"\\\"},{\\\"index\\\":2,\\\"recogniseResult\\\":\\\"是\\\",\\\"matrix\\\":\\\"\\\"}]\",\"correctorRole\":\"[{\\\"index\\\":1,\\\"corrector_role\\\":\\\"ai\\\"},{\\\"index\\\":2,\\\"corrector_role\\\":\\\"ai\\\"}]\",\"matrix\":null,\"totalScore\":null}],\"correctorId\":1,\"timeStamp\":1533723811149}";
        in = JsonUtil.readValue(s1, IntelligenceAnswerRequest.class);
        answerCommonService.intelligenceCorrectAnswer(in);

    }

    @Test
    public void getHistoryDataList() {

        FindHistoryDataRequest request = new FindHistoryDataRequest();
        request.setExerciseSource("6");
        request.setResourceId(2460557L);
        FindHistoryDataResponse response = answerCommonService.findHistoryDataList(request);
        logger.info(JsonUtil.obj2Json(response.getStudentExerciseList()));

    }

    //{"reqId":null,"resourceId":1148721,"exerciseSource":"1","studentId":81104100,"questionId":10791898,
    // "submitAnswer":"http://ap.okjiaoyu.cn/ap_TLesHk4zUA.mp3"}
    @Test
    public void updateAudioAnswer() {

        AudioUrlUpdateRequest request ;
        String s = "{\"reqId\":null,\"resourceId\":1148721,\"exerciseSource\":\"1\",\"studentId\":81104100," +
                "\"questionId\":10791899,\"submitAnswer\":\"http://ap.okjiaoyu.cn/ap_TLesHk4zUA.mp3\"}";
        request = JsonUtil.readValue(s, AudioUrlUpdateRequest.class);
        answerCommonService.updateAudioAnswer(request);
    }


    @Resource
    private RedisUtilExtend redisUtilExtend;
    @Test
    public void testa() {

       /* Jedis j = new Jedis("10.60.0.125",29100);
        j.lpush("OKAY_INTELL_WHITE","1");
        redisUtilExtend.addToListLeft("OKAY_INTELL_WHITE","6160381");*/
        List<Object> listAll = redisUtilExtend.getListAll(INTELL_WHITE);
        System.out.println(listAll.toString());

        redisUtilExtend.del("OKAY_INTELL_WHITE");
       /* Jedis jedis = new Jedis("10.10.6.7",6484);
        jedis.del("OKAY_INTELL_WHITE");*/
        String s = "6106327,6106328,6106329,6106330,6106331,6106335,6106357,6120424,6120425,6120426,6120427,6120428,6120429,6120430,6120431,6120432,6120433,6120434,6120435,6120436,6120437,6120438,6120439,6120440,6156516,6156517,6160381,6193442,6196880,61104096,61104097,61108890,61110317,61110423,61110438,61141005,61148517,61148744,61151099,61164689,61168443,61168445,61170368,61183975,61183978,61195355,61951051107,61951051108,61951051109,61951051111,61951051115,61951051153,61951052223,61951052279,61951052280,61951052287,61951052601,61951052753,61951052919,61951053084,61951053138,61951053266,61951053267,61951053268,61951053316,61951053505,61951053506,61951053555,61951053784,61951053785,61951053786,61951054086,61951054087,61951054176,61951054245,61951054451,61951054453,61951054489,61951054651,61951054988,61951054989,61951054990,61951054991,61951055556,61951055557,61951055613,61951055627,61951055784,61951056327,61951056328,61951056335,61951056336,61951056724,61951056732,61951056736,61951056737,61951057065,61951057397,61951057548,61951058103,61951058252,61951058265,61951058341,61951059705,61951060257,61951060355,61951060356,61951060357,61951060358,61951060772,61951060773,61951062371,61951062665,61951064002,61951064004,61951064006,61951064013,61951064017,61951064023,61951064050,61951064126,61951064127,61951064128,61951064129,61951064130,61951064132,61951064133,61951064134,61951064135,61951064136,61951064137,61951064138,61951064166,61951064168,61951064182,61951064183,61951067095,61951068926,61951068927,61951068928,61951068929,61951068930,61951068931,61951068932,61951068933,61951068934,61951068935,61951068936,61951068937,61951069175,61951069176,61951069177,61951069178,61951069179,61951069180,61951069181,61951069182,61951069183,61951069184,61951069185,61951069186,61951069187,61951072062,61951073801,61951074431,61951075249,61951079581,61951080865,61951080893,61951080897,61951080899,61951080900,61951080910,61951080913,61951080919,61951081041,61951081062,61951081595,61951081656,61951081657,61951081658,61951081659,61951081660,61951081661,61951081662,61951081663,61951081664,61951081665,61951081666,61951081667,61951081668,61951081669,61951081670,61951081671,61951081672,61951081673,61951081674,61951081675,61951081676,61951081677,61951081678,61951081679,61951081680,61951081681,61951081711,61951081722,61951081741,61951081742,61951081961,61951081991,61951082036,61951082038,61951082039,61951082040,61951082041,61951082042,61951082043,61951082044,61951082045,61951082401,61951082560,61951084621,61951084622,61951084954,61951084955,61951084961,61951085068,61951085077,61951085078,61951085097,61951085098,61951085162,61951085174,61951085178,61951085180,61951085195,61951085357,61951085484,61951085488,61951085489,61951085989,61951086071,61951086121,61951086158,61951086876,61951086877,61951087376,61951087377,61951087378,61951087379,61951087465,61951087468,61951087550,61951087551,61951087552,61951087965,61951087966,61951087967,61951087968,61951087969,61951087970,61951087971,61951087972,61951087973,61951087974,61951087989,61951087990,61951087998,61951088013,61951088014,61951088015,61951088016,61951088021,61951088197,61951088198,61951088200,61951088201,61951088202,61951088203,61951088204,61951088205,61951088206,61951088650,61951091125,61951091127,61951091128,61951091232,61951091233,61951094517,61951096607,61951096612,61951096613,61951096616,61951100722,61951100980,61951102946,61951104499,61951104500,61951104501,61951104508,61951104902,61951106340,61951107403,61951107522,61951107816,61951107825,61951108498,61951109078,61951109079,61951110008,61951110009,61951110224,61951111075,61951111076,61951111077,61951111078,61951111079,61951111080,61951111081,61951111082,61951111083,61951111084,61951111085,61951111086,61951111087,61951111088,61951111089,61951111090,61951113989,61951113990,61951114911,61951114912,61951115752,61951116287,61951116288,61951116293,61951116294,61951116295,61951116299,61951116301,61951116302,61951116303,61951116308,61951116472,61951116473,61951116493,61951117928,61951117970,61951118086,61951118087,61951119009,61951119010,61951119162,61951119375,61951119376,61951119377,61951119378,61951119379,61951119380,61951119381,61951119382,61951119383,61951119384,61951121868,61951121869,61951121870,61951121871,61951121872,61951121873,61951121874,61951121875,61951121876,61951121877,61951122297,61951122339,61951122759,61951123046,61951123047,61951123048,61951123049,61951124642,61951125240,61951127793,61951128112,61951128255,61951128269,61951128309,61951128450,61951129283,61951129291,61951129304,61951129846,61951129869,61951129997,61951130046,61951130047,61951130054,61951130087,61951139557,61951141969,61951142479,61951142512,61951143008,61951143013,61951143014,61951143015,61951143016,61951143017,61951143019,61951144909,61951152576,61951153838,61951154906,61951158773,61951158844,61951158845,61951162296,61951163512,61951163514,61951163559,61951163682,61951163984,61951164582,61951164587,61951164652,61951164725,61951165947,61951166255,61951168646,61951168812,61951168813,61951168814,61951168815,61951168816,61951168817,61951170213,61951174570,61951178488,61951179438,61951179587,61951179644,61951179645,61951179646,61951179655,61951179666,61951179672,61951181166,61951181167,61951181169,61951181171,61951181173,61951181427,61951181668,61951182067,61951182831,61951183724,61951183749,61951185794,61951185939,61951185940,61951186426,61951186427,61951190021,61951190346,61951192566,61951192711,61951192712,61951192756,61951193507,61951193509,61951194271,61951195028,61951195512,61951195513,61951195514,61951195515,61951195516,61951195517,61951195518,61951195519,61951195520,61951195521,61951196356,61951196357,61951196401,61951198639,61951198821,61951199661,61951210567,61951210570,61951210596,61951210604,61951210732,61951216338,61951216534,61951218342,61951218366,61951221180,61951221925,61951225390,61951225455,61951225741,61951231689,61951240190,61951240746,61951243764,61951243768,61951244036,61951244818,61951244819,61951244851,61951249054,61951249286,61951250158,61951250159,61951250160,61951250161,61951250716,61951250959,61951250963,61951250974,61951251276,61951251506,61951251953,61951251957,61951252932,61951252933,61951252934,61951256342,61951256557,61951257196,61951257197,61951257198,61951257199,61951257200,61951257201,61951257202,61951257203,61951257204,61951257205,61951257206,61951257207,61951257208,61951257209,61951257210,61951257211,61951257212,61951257213,61951257216,61951257217,61951257397,61951223368,61951223369,61951223370,61951223371,61951223372,61951223374,61951223376,61951258113,61951258121,61951258183,61951258188,61951258206,61951258214,61951258218,61951258223,61951258227,61951258230,61951258234,61951258311,61951258343,61951258484,61951258496,61951258508,61951258519,61951258520,61951258521,61951258522,61951258524,61951258526,61951258529,61951258530,61951258549,61951258611,61951258657,61951258696,61951258721,61951258850";
        String[] split = s.split(",");
        for (String s1 : split) {
            //jedis.lpush("OKAY_INTELL_WHITE",s1);
            redisUtilExtend.addToListLeft("OKAY_INTELL_WHITE",s1);
        }
        System.out.println("成功");

       /* List<String> okay_intell_white = jedis.lrange("OKAY_INTELL_WHITE", 0, -1);*/
        List<Object> listAll1 = redisUtilExtend.getListAll(INTELL_WHITE);
        System.out.println(listAll1.toString());
    }


    /*@Test
    public void test2() {
        String str = getvalue("hello world",x -> x.toUpperCase());
        System.out.println("++++++++++++++++++++++++++++++++++++++++");
        System.out.println(str);
    }

    public String getvalue(String str, MyFunction1 my) {
        return my.getvalue(str);
    }

    @FunctionalInterface
    public interface MyFunction1 {
        public String getvalue(String str);
    }*/

}