package com.noriental.praxissvr.question.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.noriental.BaseTest;
import com.noriental.log.TraceKeyHolder;
import com.noriental.praxissvr.common.QuestionState;
import com.noriental.praxissvr.question.bean.BlankAndAnswer;
import com.noriental.praxissvr.question.bean.WordAndChapter;
import com.noriental.praxissvr.question.bean.html.ComplexQuestionV1;
import com.noriental.praxissvr.question.request.*;
import com.noriental.praxissvr.question.response.*;
import com.noriental.praxissvr.question.service.QuestionContinuedService;
import com.noriental.praxissvr.question.service.QuestionService;
import com.noriental.praxissvr.question.utils.JsonUtils;
import com.noriental.test.util.Method;
import com.noriental.test.util.ThreadUtil;
import com.noriental.utils.json.JsonUtil;
import com.noriental.utils.ssdb.SSDBUtil;
import com.noriental.validate.bean.CommonDes;
import com.noriental.validate.bean.CommonResponse;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.annotation.Resource;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static com.noriental.praxissvr.common.QuestionTypeEnum.ALL;
import static org.junit.Assert.assertTrue;

/**
 * @author chenlihua
 * @date 2015/12/22
 * @time 13:59
 */
public class QuestionServiceImplTest extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Resource
    private QuestionService questionService;
    @Resource
    private QuestionContinuedService questionContinuedService;


    @Test
    public void testFindQuestionsByTypesAndWords() {

        FindQuestionsByTypesAndWordsRequest request = new FindQuestionsByTypesAndWordsRequest();


        List<Integer> typeIds = new ArrayList<>();
        //typeIds.add(55);
        //typeIds.add(56);
        typeIds.add(57);
        typeIds.add(58);
        List<WordAndChapter> wordAndChapters=new ArrayList<>();
        WordAndChapter w1=new WordAndChapter();
        w1.setWordId(319L);
        w1.setCharpterId(7763L);
        WordAndChapter w2=new WordAndChapter();
        w2.setWordId(324L);
        w2.setCharpterId(7763L);
        WordAndChapter w3=new WordAndChapter();
        w3.setWordId(395L);
        w3.setCharpterId(7765L);

        wordAndChapters.add(w1);
        wordAndChapters.add(w2);
        wordAndChapters.add(w3);
        List<Long> wordIds = new ArrayList<>();
        wordIds.add(1L);
        wordIds.add(2L);
//        wordIds.add(475L);
//        wordIds.add(476L);
//        wordIds.add(477L);
        wordIds.add(1424L);
        Long chapterId = null;
        Long versionId = null;
//        Long directoryId = 1001541L;
        Long directoryId = 280L;
        //request.setDirectoryId(directoryId);
        request.setQuestionTypeIds(typeIds);
        request.setWordIds(wordIds);
        request.setWordAndChapters(wordAndChapters);
        FindQuestionsByTypesAndWordsResponse response = questionContinuedService.findQuestionsByTypesAndWords(request);
        logger.info("========>{}", response.getQuestionDataList());
    }

    @Test
    public void testfindWordsByQuestionIdsAndChapterId(){
        FindWordsByQuestionIdsAndChapterIdRequest request=new FindWordsByQuestionIdsAndChapterIdRequest();
        List<Long> questionIds = new ArrayList<>();
        questionIds.add(10617043L);
        questionIds.add(10617042L);
        questionIds.add(10791930L);

        request.setQuestionIds(questionIds);
        //request.setChapterId(1041190L);
        FindWordsByQuestionIdsAndChapterIdResponse res= questionContinuedService.findWordsByQuestionIdsAndChapterId(request);
        logger.info("====>"+res.toString());
    }

    @Test
    public void testFindBlankAndAnswer(){
        FindBlankAndAnswerRequst requst=new FindBlankAndAnswerRequst();
        List<BlankAndAnswer> list=new ArrayList<BlankAndAnswer>();
        BlankAndAnswer b1=new BlankAndAnswer();
//        b1.setQuestionId(7964131L);
//        b1.setParentQuestionId(0L);
//        b1.setIndex(1);


        b1.setQuestionId(2547216L);
        b1.setParentQuestionId(2547215L);
        b1.setIndex(0);
        BlankAndAnswer b2=new BlankAndAnswer();
        b2.setQuestionId(2547217L);
        b2.setParentQuestionId(2547215L);
        b2.setIndex(1);
//        BlankAndAnswer b3=new BlankAndAnswer();
//        b3.setQuestionId(2547218L);
//        b3.setParentQuestionId(2547215L);
//        b3.setIndex(2);
//        BlankAndAnswer b4=new BlankAndAnswer();
//        b4.setQuestionId(7067821L);
//        b4.setParentQuestionId(0L);
//        b4.setIndex(0);
//        BlankAndAnswer b5=new BlankAndAnswer();
//        b5.setQuestionId(851911L);
//        b5.setParentQuestionId(0L);
//        b5.setIndex(0);
//        BlankAndAnswer b6=new BlankAndAnswer();
//        b6.setQuestionId(7102119L);
//        b6.setParentQuestionId(2547215L);
//        b6.setIndex(0);
//
//        BlankAndAnswer b3=new BlankAndAnswer();
//        b3.setQuestionId(4003728L);
//        b3.setParentQuestionId(4003726L);
//        b3.setIndex(1);
        list.add(b1);
        list.add(b2);
//        list.add(b3);
//        list.add(b4);
//        list.add(b5);
//        list.add(b6);

        requst.setBlankAndAnswers(list);

        questionContinuedService.findBlankAndAnswer(requst);
    }
    @Test
    public void testJsonConvetQuestionV1(){
        String htmlData="{\"questions\":[{\"body\":\"<p>甲图表示的是<input type=\\\"text\\\" readonly=\\\"true\\\" class=\\\"questions-blank\\\" value=\\\"1\\\" contenteditable=\\\"true\\\"/>&nbsp;季的风向，乙图表示的是<input type=\\\"text\\\" readonly=\\\"true\\\" class=\\\"questions-blank\\\" value=\\\"2\\\" contenteditable=\\\"true\\\"/>&nbsp;季的风向。<\\/p>\",\"answer\":[\"<p>冬&nbsp; &nbsp;&nbsp;<br/><\\/p>\",\"<p>夏<\\/p>\"],\"difficulty\":\"2\",\"analysis\":\"<p>无<\\/p>\",\"type\":{\"id\":2,\"name\":\"填空题\"},\"options\":[]},{\"body\":\"<p>亚洲东部和南部均受到季风影响，但冬夏季风的风向有所差别，冬季风风向有什么不同？<\\/p>\",\"answer\":\"<p>东部夏季是东南季风，冬季是西北季风，而南部夏季是西南季风，冬季是东北季风。<\\/p>\",\"difficulty\":\"2\",\"analysis\":\"<p>无<\\/p>\",\"type\":{\"id\":4,\"name\":\"简答题\"},\"options\":[]},{\"body\":\"<p>亚洲东部的季风气候特别典型，其原因是什么？<\\/p>\",\"answer\":\"<p>亚洲东部地处世界上最大的大陆亚欧大陆东侧， 最大的大洋太平洋西侧，海陆热力性质差异 最显著，所以季风气候最典型。<\\/p>\",\"difficulty\":\"2\",\"analysis\":\"<p>无<br/><\\/p>\",\"type\":{\"id\":4,\"name\":\"简答题\"},\"options\":[]},{\"body\":\"<p>亚洲东部的季风气候可以分为<input type=\\\"text\\\" readonly=\\\"true\\\" class=\\\"questions-blank\\\" value=\\\"1\\\" contenteditable=\\\"true\\\"/>&nbsp;季风气候和<input type=\\\"text\\\" readonly=\\\"true\\\" class=\\\"questions-blank\\\" value=\\\"2\\\" contenteditable=\\\"true\\\"/>&nbsp;其中，1月平均气温在0°C以下，雨季较短的是<input type=\\\"text\\\" readonly=\\\"true\\\" class=\\\"questions-blank\\\" value=\\\"3\\\" contenteditable=\\\"true\\\"/>&nbsp;气候；而1月平均气温在0°C以上，雨季较长的是<input type=\\\"text\\\" readonly=\\\"true\\\" class=\\\"questions-blank\\\" value=\\\"4\\\" contenteditable=\\\"true\\\"/>&nbsp;气候。<\\/p>\",\"answer\":[\"<p>亚热带&nbsp;&nbsp;&nbsp;&nbsp;<\\/p>\",\"<p>温带<\\/p>\",\"<p>温带季风<\\/p>\",\"<p>亚热带季风<\\/p>\"],\"difficulty\":\"2\",\"analysis\":\"<p>无<\\/p>\",\"type\":{\"id\":2,\"name\":\"填空题\"},\"options\":[]},{\"body\":\"<p>想一想，亚洲东部地区的乎风气候对农业生产发展提供的有利条件是( &nbsp; &nbsp;)<\\/p><p>①年降水量分配均匀②雨热同期③冬季普遍高温④年降水量较丰沛<\\/p><p><br/><\\/p>\",\"answer\":[\"D\"],\"difficulty\":\"2\",\"analysis\":\"<p>无<br/><\\/p>\",\"type\":{\"id\":1,\"name\":\"选择题\"},\"options\":[\"<p>①②<\\/p>\",\"<p>②③<\\/p>\",\"<p>③④<\\/p>\",\"<p>②④<\\/p>\"]}],\"material\":\"<p>发表于《地球物理学研究快报》上的一份最新研究表明，受全球变暖影响，下个世纪夏季风到来的时间可能会推迟5〜15天，因此南亚大部分地区的降水将大幅减少。读亚洲东部、南部冬季风图，回答下列问题。<\\/p><p><img width=\\\"544\\\" height=\\\"293\\\" src=\\\"http://rc.okjiaoyu.cn/rc_EmRlqhjzLq.png\\\"/><\\/p>\"}";
        ComplexQuestionV1 question = null;
        try {
            question = JsonUtils.fromJson(htmlData, ComplexQuestionV1.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(question);
    }

    @Test
    public void testFindQuestions() throws Exception {
        /**
         * {"reqId":null,"pageNo":1,"pageSize":10,"pageable":false,"sameFilter":false,"uploadId":null,
         * "subjectId":null,"newFormat":false,"visible":"ALL","questionType":"ALL","questionTypeId":null,
         * "difficulty":null,"questionGroup":null,"moduleIds":null,"unitIds":null,"topicIds":null,"seriesIds":null,
         * "ids":[7675166,7675165,5975069,5973108],"paperId":null,"paperTypeId":null,"gradeId":null,
         * "paperRegion":null,"paperProv":null,"paperCity":null,"paperCounty":null,"mastery":null,"highQual":null,
         * "sorts":null,"chapterId":null,"queryTrunk":false,"states":["ALL"],"uploadTimeStart":null,
         * "uploadTimeEnd":null,"paperKeyword":null,"basic":false,"subStates":["ALL"],"orgId":0,
         * "quesSearchPerm":null,"audio":null}
         */
        FindQuestionsRequest request = new FindQuestionsRequest();
        request.setPageNo(1);
        request.setPageSize(10);
        //        request.setQuestionType(QuestionType.DUO_XUAN);
        //        request.setUnitIds(Collections.singletonList(617L));
        request.setQuestionType(ALL);
        request.setNewFormat(true);
        request.setQueryTrunk(true);
        List ids = new ArrayList();
        ids.add(7675166L);
        ids.add(7675165L);
        ids.add(5975069L);
        ids.add(5973108L);
        //  ids.add(7674667L);
        request.setIds(ids);
        // request.setStates(ALL);
        //        request.setSubjectId(1L);
        //        request.setPageable(false);
        //        request.setIds(Arrays.asList(201331L));
        //        request.setUploadId(6161545L);
        //        request.setSubjectId(1L);
        //        request.setUploadId(6161745L);
        //        request.setStates(Collections.singletonList(QuestionState.ENABLED));
        //        request.setDifficulty(Difficulty.EASY);
        //        request.setUploadTimeStart(new GregorianCalendar(2016,2, 16, 0, 59, 34).getTime());
        //        request.setUploadTimeEnd(new GregorianCalendar(2016,2, 16, 0, 59, 36).getTime());
      /*  QuesSearchPerm perm = new QuesSearchPerm();
        perm.setCurrentOrgId(82L);
        perm.setCurrentOrgType(2);
        request.setQuesSearchPerm(perm);
        request.setBasic(false);
        request.setSorts(Arrays.asList(QuestionSort.UPDATE_TIME_DESC));*/

        FindQuestionsResponse resp = questionService.findQuestions(request);
        System.out.println(JSON.toJSONString(resp, true));
        assertTrue(resp.success());
    }

    @Test
    public void testFindQuestions1() throws Exception {
        FindQuestionsRequest request = new FindQuestionsRequest();
        request.setPageable(true);
        request.setPageNo(1);
        request.setPageSize(10);
        //        request.setIds(Arrays.asList(2434784L));
        request.setQuestionType(ALL);
        //        request.setPaperKeyword("61148744");
        request.setStates(Collections.singletonList(QuestionState.ALL));
        //        request.setIds(Arrays.asList(2492824L));
        //        request = JsonUtil.readValue("{\"pageNo\":1,\"pageSize\":10,\"pageable\":true,\"sameFilter\":false,
        // \"uploadId\":6160381,\"subjectId\":5,\"newFormat\":true,\"visible\":\"VISIBLE\",\"questionType\":\"ALL\",
        // \"questionTypeId\":null,\"difficulty\":null,\"questionGroup\":null,\"moduleIds\":null,\"unitIds\":null,
        // \"topicIds\":null,\"seriesIds\":null,\"ids\":null,\"paperId\":null,\"paperTypeId\":null,\"gradeId\":null,
        // \"paperRegion\":null,\"paperProv\":null,\"paperCity\":null,\"paperCounty\":null,\"mastery\":null,
        // \"highQual\":null,\"sorts\":null,\"chapterId\":null,\"queryTrunk\":true,\"states\":[\"ENABLED\",
        // \"BANNED\",\"PREVIEWED\"],\"uploadTimeStart\":null,\"uploadTimeEnd\":null,\"paperKeyword\":null,
        // \"basic\":false,\"subStates\":null,\"orgId\":0,\"audio\":null}",FindQuestionsRequest.class);
        long s = System.currentTimeMillis();

        FindQuestionsResponse resp = questionService.findQuestions(request);
        logger.info(JSONObject.toJSONString(resp, true));
        assert resp != null;
        long e = System.currentTimeMillis();
        //        System.out.println(JSON.toJSONString(resp, true));
        long cost = e - s;
        System.out.println("testFindQuestions1 cost " + cost + "ms");
        if (cost > 100) {
            logger.error("接口效率低[COST][{}ms]", cost);
        }
        Thread.sleep(10000L);
    }

    @Test
    @Ignore
    public void testFindQuestionsConcurrent() throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 1000; i++) {
            Thread command = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        testFindQuestions1();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            pool.execute(command);
        }
        pool.awaitTermination(1, TimeUnit.HOURS);
        pool.shutdown();
        logger.info("主线程结束");
    }

    /**
     * 根据上传用户查询
     *
     * @throws Exception
     */
    @Test
    public void testFindQuestions2() throws Exception {
        FindQuestionsRequest request = new FindQuestionsRequest();
        request.setPageNo(1);
        request.setPageSize(10);
        request.setIds(Collections.singletonList(2432089L));
        request.setStates(Collections.singletonList(QuestionState.ALL));
        request.setQuestionType(ALL);
        //        request.setQueryTrunk(true);
        //        request.setSubjectId(5L);
        long s = System.currentTimeMillis();
        FindQuestionsResponse resp = questionService.findQuestions(request);
        assert resp != null;
        long e = System.currentTimeMillis();
        System.out.println("findPaperByPaperId used " + (e - s) + "ms");
        String json = JSON.toJSONString(resp, true);
        System.out.println(json);
    }

    /**
     * 按照ID查询
     *
     * @throws Exception
     */
    @Test
    public void testFindQuestions3() throws Exception {
        FindQuestionsRequest request = new FindQuestionsRequest();
        //        request.setPageNo(1);
        //        request.setPageSize(1);
        //        request.setIds((Collections.singletonList(27040L)));
        request.setPageable(false);
        request.setIds(Arrays.asList(3009584L/*, 2430573L*//*, 2430400L, 2430373L, 2430351L, 2430330L, 2427679L,
        153282L, 152159L, 151741L, 151730L, 143954L, 143712L, 143089L, 142351L, 142255L, 142090L, 142044L, 140079L,
        138673L, 138531L, 127772L, 127291L, 126219L, 125129L, 124862L, 124339L, 123527L, 123451L, 103536L, 102846L,
        102190L, 100615L, 92252L, 31762L, 31689L, 31499L, 27404L, 27150L*/));

        request.setQuestionType(ALL);
        request.setStates(Collections.singletonList(QuestionState.ALL));

        FindQuestionsResponse resp = questionService.findQuestions(request);
        assert resp != null;
        String json = JSON.toJSONString(resp, true);
        System.out.println(json);
    }

    @Test
    public void testFindPaperByPaperId() throws Exception {
        FindPaperByPaperIdRequest req = new FindPaperByPaperIdRequest();
        req.setPaperId(7704);
        long s = System.currentTimeMillis();
        FindPaperByPaperIdResponse resp = questionService.findPaperByPaperId(req);
        assert resp != null;
        long e = System.currentTimeMillis();
        System.out.println("findPaperByPaperId used " + (e - s) + "ms");
        String json = JSON.toJSONString(resp, true);
        System.out.println(json);
    }

    @Test
    public void testFindQuestionById() throws Exception {
        FindQuestionByIdRequest req = new FindQuestionByIdRequest();
        req.setQuestionId(8448689);
        //req.setQuestionId(7194795);
        req.setRecursive(true);
        req.setSubStates(Collections.singletonList(QuestionState.ALL));
        long s = System.currentTimeMillis();
        FindQuestionByIdResponse resp = questionService.findQuestionById(req);
        assert resp != null;
        long e = System.currentTimeMillis();
        System.out.println("findPaperByPaperId used " + (e - s) + "ms");
        String json = JSON.toJSONString(resp, true);
        //System.out.println(json);
        logger.info("findQuestionById接口结果集是：{}",resp.getQuestion());
    }

    @Test
    public void testFindQuestionsByIds() throws Exception {
        String jsonStr="{\"reqId\":null,\"questionIds\":[11504275,10905100,10905112,4640674,10799835,10805091,4741567,6663505,4640701,11305631,11346849,10802270,10768318,6699476],\"recursive\":false}";

        FindQuestionsByIdsRequest request = JsonUtil.readValue(jsonStr, FindQuestionsByIdsRequest.class);
        FindQuestionsByIdsRequest req = new FindQuestionsByIdsRequest();
        req.setRecursive(true);
        List<Long> ids = new ArrayList<>();
//        ids.add(11345862L);
//        ids.add(11014822L);
        ids.add(8448689L);
        ids.add(7194795L);
        req.setQuestionIds(ids);
        long s = System.currentTimeMillis();
        FindQuestionsByIdsResponse resp = questionService.findQuestionsByIds(request);
        assert resp != null;
        long e = System.currentTimeMillis();
        System.out.println("testFindQuestionsByIds used " + (e - s) + "ms");
        String json = JSON.toJSONString(resp, true);
        //System.out.println(json);'
        logger.info("testFindQuestionsByIds接口结果集是：{}",resp.getQuestionList());
    }

    @Test
    public void findQuestionsByIdsFast() throws Exception {
        FindQuestionsByIdsRequest req = new FindQuestionsByIdsRequest();
        req.setRecursive(true);
        req.setQuestionIds(Arrays.asList(8448689L, 7194795L));
        //req.setQuestionIds(Arrays.asList(10328515L));

        long s = System.currentTimeMillis();
        FindQuestionsByIdsResponse resp = questionService.findQuestionsByIdsFast(req);
        assert resp != null;
        long e = System.currentTimeMillis();
        System.out.println("testFindQuestionsByIds used " + (e - s) + "ms");
        String json = JSON.toJSONString(resp, true);
        //System.out.println(json);
        logger.info("findQuestionsByIdsFast接口结果集是：{}",resp.getQuestionList());
    }

    @Test
    public void testFindFacetQuestionCount1() throws Exception {
        FindFacetQuestionCount1Request request = new FindFacetQuestionCount1Request();
        request.setChapterIds(Arrays.asList(10615L, 10616L, 10617L, 10618L));
        request.setModuleIds(Collections.singletonList(242L));
        request.setUnitIds(Collections.singletonList(617L));
        request.setTopicIds(Collections.singletonList(3407L));
        request.setSubjectId(1);
        //        request.setQuesSearchPerm(new QuesSearchPerm(82L,2));
        FindFacetQuestionCountResponse resp = questionService.findFacetQuestionCount1(request);
        System.out.println(JSONObject.toJSONString(resp, true));
        assertTrue(resp.success());
    }

    @Test
    public void checkoutNewFormatAndTrunk() throws Exception {
        CheckoutNewFormatAndTrunkRequest req = new CheckoutNewFormatAndTrunkRequest();
        req.setIds(Arrays.asList(1L, 2L, 3L, 571L, 569L));
        CheckoutNewFormatAndTrunkResponse resp = questionService.checkoutNewFormatAndTrunk(req);
        System.out.println(JSONObject.toJSONString(resp, true));
    }

    /**
     * 测试过滤相似的题目
     */
    @Test
    public void testSameFilterTest() {
        FindQuestionsRequest req = new FindQuestionsRequest();
        req.setIds(Arrays.asList(579L, 96789L, 466557L));
        req.setSameFilter(true);
        req.setSubjectId(10L);
        req.setNewFormat(true);
        req.setQuestionType(ALL);
        req.setPageNo(1);
        req.setPageSize(10);
        req.setStates(Collections.singletonList(QuestionState.ENABLED));
        FindQuestionsResponse resp = questionService.findQuestions(req);
        System.out.println(JSONObject.toJSONString(resp, true));
    }

    @Test
    public void testFindPadQuestionListByQuestionIds() {
        String jsonStr="{\"reqId\":null,\"questionIds\":[4640674,10805091,10905100,11346849,6699476,11305631,6663505,4741567,10802270,10768318,4640701,11504275,10905112,10799835],\"exerciseId\":608810}";
        FindPadQuestionListByQuestionIdsRequest req = JsonUtil.readValue(jsonStr, FindPadQuestionListByQuestionIdsRequest.class);
        FindPadQuestionListByQuestionIdsRequest request = new FindPadQuestionListByQuestionIdsRequest();
        request.setQuestionIds(Arrays.asList(5876748L, 5876720L, 5876645L, 5876709L, 5876676L, 5876740L, 5876695L));
        FindPadQuestionListByQuestionIdsResponse resp = questionService.findPadQuestionListByQuestionIds(req);
        System.out.println(JSONObject.toJSONString(resp, false));
    }

    @Test
    @Ignore
    public void testFindPadQuestionListByQuestionIdsConcurrent() {
        ExecutorService executor = Executors.newFixedThreadPool(40);
        for (int i = 0; i < 40; i++) {
            Runnable t = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < 1000; j++) {
                        //                        TraceKeyHolder.setTraceKey("TEST_CON_" + RandomStringUtils.random
                        // (8, false, true));
                        FindPadQuestionListByQuestionIdsRequest request = new FindPadQuestionListByQuestionIdsRequest();
                        request.setQuestionIds(Arrays.asList(4002094L, 4002098L, 4002093L, 4002087L, 4001725L));
                        FindPadQuestionListByQuestionIdsResponse resp = questionService
                                .findPadQuestionListByQuestionIds(request);
                        System.out.println(JSONObject.toJSONString(resp, false));
                    }
                }
            });
            executor.execute(t);
        }
        try {
            executor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Ignore
    public void conTestFindQuestions() {
        ThreadUtil.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    testFindQuestions1();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, 200, 300);
    }

    @Test
    public void findLeafQuesIdsByParentIds() {
        FindLeafQuesIdsByParentIdsRequest request = new FindLeafQuesIdsByParentIdsRequest();
        request.setParentQuesIds(Collections.singletonList(2536413L));
        FindLeafQuesIdsByParentIdsResponse resp = questionService.findLeafQuesIdsByParentIds(request);
        System.out.println(JSONObject.toJSONString(resp, false));
    }

    @Test
    public void findQuestionKnowledgeByIds() throws Exception {
        FindQuestionKnowledgeByIdsRequest req = new FindQuestionKnowledgeByIdsRequest();
        req.setQuestionIds(Arrays.asList(2598621L, 3009593L, 3009590L, 3009588L, 3009587L, 3009585L, 3009584L,
                3009583L, 3009581L, 3009579L, 2778908L, 2620400L, 2620399L, 2620395L, 2620394L, 2620393L, 2620392L,
                2620391L, 2620388L, 2620387L, 2620386L));
        //        req.setQuestionIds(Arrays.asList(2625810L, 2455626L, 2455622L, 2455617L, 2455612L, 2455608L,
        // 2455603L, 2455602L, 2455601L, 2455595L, 2778908L,
        //                2620400L, 2620399L, 2620395L, 2620394L, 2620393L, 2620392L,2620391L, 2620388L, 2620387L,
        // 2620386L));
        FindQuestionKnowledgeByIdsResponse resp = questionService.findQuestionKnowledgeByIds(req);
        System.out.println(JSONObject.toJSONString(resp));
    }

    @Test
    @Ignore
    public void testFindQuestionKnowledgeByIdsConcurrent() {
        final ExecutorService executor = Executors.newFixedThreadPool(100);
        for (int i = 0; i < 100; i++) {
            final String name = "Thread-" + i + "-" + System.currentTimeMillis();
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            long l = System.currentTimeMillis();
                            TraceKeyHolder.setTraceKey(name);
                            findQuestionKnowledgeByIds();

                            long o1 = System.currentTimeMillis() - l;
                            if (o1 > 2000) {
                                logger.info("[{}]cost:{}", name, o1);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            executor.execute(t);
        }
        try {
            executor.awaitTermination(5, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void findComplement() {
        FindComplementQuestionRequest request = new FindComplementQuestionRequest();
        request.setOrgType(4);
        request.setOrgId(1);
        Set<Long> s = new HashSet<>();
        s.add(2L);
        request.setSubjectIds(s);
        request.setMaxCount(50);
        CommonResponse<List<Long>> resp = questionService.findComplement(request);
        System.out.println(JSONObject.toJSONString(resp, true));
    }

    @Before
    public void setUp() throws Exception {
        long time = new Date().getTime();
        String key = String.valueOf(time);
        System.out.println("RequestId:" + key);
        TraceKeyHolder.setTraceKey(key);
        MDC.put("id", key);
    }

    @Test
    @Ignore
    public void findQuestionsStress() {


        final AtomicLong m1 = new AtomicLong();
        final AtomicInteger m2 = new AtomicInteger();
        Method runnable = new Method() {
            public void run(int a, int b) {
                long ss = System.currentTimeMillis();
                //                setRequestid();
                //                String s = "{\"reqId\":null,\"pageNo\":1,\"pageSize\":10,\"pageable\":false,
                // \"sameFilter\":false,\"uploadId\":null,\"subjectId\":null,\"newFormat\":false,\"visible\":\"ALL\",
                // \"questionType\":\"ALL\",\"questionTypeId\":null,\"difficulty\":null,\"questionGroup\":null,
                // \"moduleIds\":null,\"unitIds\":null,\"topicIds\":null,\"seriesIds\":null,\"ids\":[1282713,1290705,
                // 1057620,1268886,1049251,1129506,2453185,1406146,2453516,1352830,5962863,5962851,4755526,5831905,
                // 16243,16244,16245,16246,16247,16248,16249,16250,16253,16254,16255,16256,16258,4755523,4755516],
                // \"paperId\":null,\"paperTypeId\":null,\"gradeId\":null,\"paperRegion\":null,\"paperProv\":null,
                // \"paperCity\":null,\"paperCounty\":null,\"mastery\":null,\"highQual\":null,\"sorts\":null,
                // \"chapterId\":null,\"queryTrunk\":false,\"states\":[\"ALL\"],\"uploadTimeStart\":null,
                // \"uploadTimeEnd\":null,\"paperKeyword\":null,\"basic\":false,\"subStates\":[\"ALL\"],\"orgId\":0,
                // \"quesSearchPerm\":null,\"audio\":null}";
                //                FindQuestionsRequest request = JsonUtil.readValue(s,FindQuestionsRequest.class);
                //                FindQuestionsResponse resp = questionService.findQuestions(request);
                //                System.out.println(JSON.toJSONString(resp, true));
                FindPadQuestionListByQuestionIdsRequest request = new FindPadQuestionListByQuestionIdsRequest();

                //                4002094L, 4002098L, 4002093L, 4002087L, 4001725L
                //                l1,l2,l3,l4,l5
                //                Long l1 = Long.valueOf(String.valueOf(Math.abs(new Random().nextInt())));
                //                Long l2 = Long.valueOf(String.valueOf(Math.abs(new Random().nextInt())));
                //                Long l3 = Long.valueOf(String.valueOf(Math.abs(new Random().nextInt())));
                //                Long l4 = Long.valueOf(String.valueOf(Math.abs(new Random().nextInt())));
                //                Long l5 = Long.valueOf(String.valueOf(Math.abs(new Random().nextInt())));
                request.setQuestionIds(Arrays.asList(4002094L, 4002098L, 4002093L, 4002087L, 4001725L));
                FindPadQuestionListByQuestionIdsResponse resp = questionService.findPadQuestionListByQuestionIds
                        (request);
                System.out.println(JSONObject.toJSONString(resp, false));
                long l = System.currentTimeMillis() - ss;
                logger.info("cost " + l);
                m1.addAndGet(l);
                m2.addAndGet(1);
            }
        };
        //        LoaderRunnerUnit.run(runnable,100,25);
        //        ThreadUtil.executeBySeconds(runnable,1,1);
        System.out.println("total time:" + m1 + ", total" + m2 + " , avg:" + m1.longValue() / m2.longValue());
    }

    @Resource
    private SSDBUtil ssdbUtil;

    @Test
    @Ignore
    public void testSSDB() {

        ThreadUtil.execute(new Runnable() {
            @Override
            public void run() {
                long l = System.currentTimeMillis();
                List<String> keySet = Arrays.asList("ssdb_question_html_4002141", "ssdb_question_html_4002142",
                        "ssdb_question_html_4002137", "ssdb_question_html_4002136", "ssdb_question_html_4002139",
                        "ssdb_question_html_4002149", "ssdb_question_html_4002148", "ssdb_question_html_4002135",
                        "ssdb_question_html_4002150", "ssdb_question_html_4002147");
                //                for (String s : keySet) {
                //                    String s1 = ssdbUtil.get(s);
                //                }
                Map<String, String> mget = ssdbUtil.mget(keySet);
                System.out.println("mget = " + mget);
                System.out.println("=====================ssdb cost :" + (System.currentTimeMillis() - l));

            }
        }, 200, 20);
    }


    @Test
    public void findMine() {

        String s = "{systemId=61951120271, subjectId=6, questionType=ALL, difficulty=null, cusDirId1=8832514, " +
                "cusDirId2=null, cusDirId3=null, questionGroupId=null, moduleIds=null, unitIds=null, topicIds=null}";
        FindMyQuestionRequest findMyQuestionRequest = new FindMyQuestionRequest();
        findMyQuestionRequest.setSystemId(61951120271L);
        findMyQuestionRequest.setSubjectId(6L);
        //findMyQuestionRequest.setQuestionType(ALL);
        findMyQuestionRequest.setQuestionType(0);
        findMyQuestionRequest.setCusDirId1(8832514L);
        findMyQuestionRequest.setPageNo(2);
        FindQuestionsResponse response = questionService.findMine(findMyQuestionRequest);
        logger.info(JsonUtil.obj2Json(response));

    }


    /***
     * {
     id=1411520,
     html='{"body":"<p>拼写 修改后</p><p><span style="line-height: 1em;">xǐ chū wàng wài<input type="text" readonly="true"
     class="questions-blank" value="1" contenteditable="true"/>&nbsp;</span></p>","analysis":"<p>无</p>",
     "answer":["<p>喜出望外</p>"],"questions":[]}',
     topicIds=null,
     chapterId=9146,
     level=1,
     type=TIAN_KONG,
     groupId=204665,
     customerDirectory=8815026,
     ref=0,
     subjectId=1,
     uploadId=61951119025,
     uploadSrc=2,
     newFormat=1,
     source='',
     orgId=80,
     orgType=2
     }
     */
    @Test
    public void testUpdateQuestion() {
        String s = "{id=1411520, html='{\"body\":\"<p>拼写 修改后</p><p><span style=\\\"line-height: 1em;\\\">xǐ chū wàng " +
                "" + "" + "" + "" + "wài<input type=\\\"text\\\" readonly=\\\"true\\\" class=\\\"questions-blank\\\" " +
                "" + "value=\\\"1\\\"" + " " + "contenteditable=\\\"true\\\"/>&nbsp;</span></p>\"," +
                "\"analysis\":\"<p>无</p>\"," + "\"answer\":[\"<p>喜出望外</p>\"],\"questions\":[]}', topicIds=null, " +
                "chapterId=9146, level=1, " + "type=TIAN_KONG, groupId=204665, customerDirectory=8815026, ref=0, " +
                "subjectId=1, uploadId=61951119025," + "" + " uploadSrc=2, newFormat=1, source='', orgId=80, " +
                "orgType=2}";

        UpdateQuestionRequest updateQuestionRequest = new UpdateQuestionRequest();
        updateQuestionRequest.setId(11330234L);
        String ss = "{\"body\": \"<p>根据句意，用括号内所给单词的正确形式填空</p><p>1、My brother doesn’t have a good lifestyle. He seldom eats&nbsp;<input type=\\\"text\\\" readonly=\\\"true\\\" class=\\\"questions-blank\\\" value=\\\"1\\\" contenteditable=\\\"true\\\"/>&nbsp; (bean) or other vegetables.</p><p><br/></p><p>2、<input type=\\\"text\\\" readonly=\\\"true\\\" class=\\\"questions-blank\\\" value=\\\"2\\\" contenteditable=\\\"true\\\"/>&nbsp;(plan) a day out for their family is a lot of fun for the twin sisters.</p><p><br/></p><p>3、Monday is the&nbsp;<input type=\\\"text\\\" readonly=\\\"true\\\" class=\\\"questions-blank\\\" value=\\\"3\\\" contenteditable=\\\"true\\\"/>&nbsp; (two) day of a week.</p><p><br/></p><p>4、Frank, I think you should spend twenty<input type=\\\"text\\\" readonly=\\\"true\\\" class=\\\"questions-blank\\\" value=\\\"4\\\" contenteditable=\\\"true\\\"/>&nbsp;(much) minutes on sports every day.</p><p><br/></p><p>5、It’s important for us to keep<input type=\\\"text\\\" readonly=\\\"true\\\" class=\\\"questions-blank\\\" value=\\\"5\\\" contenteditable=\\\"true\\\"/>&nbsp; (health).</p>\",\"answer\": [\"<p>beans</p>\",\"<p>Planning</p>\",\"<p>second</p>\",\"<p>more</p>\",\"<p>healthy</p>\"],\"analysis\": \"\",\"questions\": []}";
        updateQuestionRequest.setHtml(ss);
        updateQuestionRequest.setChapterId(9146L);
        updateQuestionRequest.setLevel(1);
        //updateQuestionRequest.setType(TIAN_KONG);
        updateQuestionRequest.setType(2);
        updateQuestionRequest.setGroupId(36344L);
        updateQuestionRequest.setCustomerDirectory(2158725L);
        updateQuestionRequest.setRef(0);
        updateQuestionRequest.setSubjectId(1);
        updateQuestionRequest.setUploadId(62951051116L);
        updateQuestionRequest.setUploadSrc(2);
        updateQuestionRequest.setNewFormat(1);
        updateQuestionRequest.setOrgId(80);
        updateQuestionRequest.setOrgType(2);

        List<Long> specials=new ArrayList<>();
        specials.add(20180427333L);
        specials.add(20180427444L);
        updateQuestionRequest.setSpecials(specials);

       /* updateQuestionRequest=JsonUtil.readValue(s,UpdateQuestionRequest.class);*/
        CommonResult result = questionService.updateQuestion(updateQuestionRequest);

        logger.info(JsonUtil.obj2Json(result));

    }


    @Test
    public void testUpdateContinuedQuestion(){
        String s="{\"reqId\":null,\"object\":\"{\\\"html\\\":\\\"{\\\\\\\"body\\\\\\\":\\\\\\\"<p>\\\\u5355\\\\u9009" +
                "\\\\u591a\\\\u9009<\\\\\\\\\\\\/p>\\\\\\\"," +
                "\\\\\\\"options\\\\\\\":[\\\\\\\"<p>\\\\u5355\\\\u9009\\\\u591a\\\\u9009<\\\\\\\\\\\\/p>\\\\\\\"," +
                "\\\\\\\"<p>\\\\u5355\\\\u9009\\\\u591a\\\\u9009<\\\\\\\\\\\\/p>\\\\\\\"," +
                "\\\\\\\"<p>\\\\u5355\\\\u9009\\\\u591a\\\\u9009<\\\\\\\\\\\\/p>\\\\\\\"," +
                "\\\\\\\"<p>\\\\u5355\\\\u9009\\\\u591a\\\\u9009<\\\\\\\\\\\\/p>\\\\\\\"]," +
                "\\\\\\\"audio\\\\\\\":{\\\\\\\"url\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"name\\\\\\\":\\\\\\\"\\\\\\\"," +
                "\\\\\\\"size\\\\\\\":\\\\\\\"\\\\\\\"},\\\\\\\"map\\\\\\\":{\\\\\\\"url\\\\\\\":\\\\\\\"\\\\\\\"," +
                "\\\\\\\"name\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"size\\\\\\\":\\\\\\\"\\\\\\\"}," +
                "\\\\\\\"answer\\\\\\\":[\\\\\\\"A\\\\\\\",\\\\\\\"B\\\\\\\"]," +
                "\\\\\\\"analysis\\\\\\\":\\\\\\\"<p>\\\\u5355\\\\u9009\\\\u591a\\\\u9009<\\\\\\\\\\\\/p>\\\\\\\"," +
                "\\\\\\\"questions\\\\\\\":[]}\\\",\\\"level\\\":1,\\\"source\\\":\\\"CMS\\\\u5957\\\\u5377\\\\u4e0a\\\\u4f20\\\"," +
                "\\\"id\\\":\\\"4174150\\\",\\\"type\\\":\\\"1\\\",\\\"state\\\":\\\"DISABLED\\\"," +
                "\\\"uploadId\\\":\\\"6160381\\\",\\\"subjectId\\\":\\\"5\\\"}\"}";

        String ss="{\"reqId\":null,\"object\":\"{\\\"html\\\":\\\"{\\\\\\\"material\\\\\\\":\\\\\\\"<p>1111111111111111<\\\\/p>\\\\\\\",\\\\\\\"questions\\\\\\\":[{\\\\\\\"body\\\\\\\":\\\\\\\"<p>1111111<br\\\\/><\\\\/p>\\\\\\\",\\\\\\\"options\\\\\\\":[\\\\\\\"<p>11111111111<\\\\/p>\\\\\\\",\\\\\\\"<p>1111111111<\\\\/p>\\\\\\\",\\\\\\\"<p>1111<\\\\/p>\\\\\\\",\\\\\\\"<p>11111111<\\\\/p>\\\\\\\"],\\\\\\\"analysis\\\\\\\":\\\\\\\"<p>11111111111<\\\\/p>\\\\\\\",\\\\\\\"difficulty\\\\\\\":\\\\\\\"3\\\\\\\",\\\\\\\"type\\\\\\\":{\\\\\\\"id\\\\\\\":1,\\\\\\\"name\\\\\\\":\\\\\\\"\\\\u9009\\\\u62e9\\\\u9898\\\\\\\"},\\\\\\\"answer\\\\\\\":[\\\\\\\"C\\\\\\\"]},{\\\\\\\"body\\\\\\\":\\\\\\\"<p>22222222222<input type=\\\\\\\\\\\\\\\"text\\\\\\\\\\\\\\\" readonly=\\\\\\\\\\\\\\\"true\\\\\\\\\\\\\\\" class=\\\\\\\\\\\\\\\"questions-blank\\\\\\\\\\\\\\\" value=\\\\\\\\\\\\\\\"1\\\\\\\\\\\\\\\" contenteditable=\\\\\\\\\\\\\\\"true\\\\\\\\\\\\\\\"\\\\/>&nbsp;22222222<\\\\/p>\\\\\\\",\\\\\\\"analysis\\\\\\\":\\\\\\\"<p>2222222222222<\\\\/p>\\\\\\\",\\\\\\\"difficulty\\\\\\\":\\\\\\\"2\\\\\\\",\\\\\\\"type\\\\\\\":{\\\\\\\"id\\\\\\\":2,\\\\\\\"name\\\\\\\":\\\\\\\"\\\\u586b\\\\u7a7a\\\\u9898\\\\\\\"},\\\\\\\"answer\\\\\\\":[\\\\\\\"<p>22222222222<\\\\/p>\\\\\\\"]},{\\\\\\\"body\\\\\\\":\\\\\\\"<p>33333333333333333333333333<\\\\/p>\\\\\\\",\\\\\\\"answer\\\\\\\":[\\\\\\\"0\\\\\\\"],\\\\\\\"analysis\\\\\\\":\\\\\\\"<p>33333333333333333333<\\\\/p>\\\\\\\",\\\\\\\"difficulty\\\\\\\":\\\\\\\"3\\\\\\\",\\\\\\\"type\\\\\\\":{\\\\\\\"id\\\\\\\":3,\\\\\\\"name\\\\\\\":\\\\\\\"\\\\u5224\\\\u65ad\\\\u9898\\\\\\\"}},{\\\\\\\"body\\\\\\\":\\\\\\\"<p>4444444444<\\\\/p>\\\\\\\",\\\\\\\"answer\\\\\\\":[\\\\\\\"<p>4444444444<\\\\/p>\\\\\\\"],\\\\\\\"analysis\\\\\\\":\\\\\\\"<p>4444444444<\\\\/p>\\\\\\\",\\\\\\\"difficulty\\\\\\\":\\\\\\\"4\\\\\\\",\\\\\\\"type\\\\\\\":{\\\\\\\"id\\\\\\\":4,\\\\\\\"name\\\\\\\":\\\\\\\"\\\\u7b80\\\\u7b54\\\\u9898\\\\\\\"}},{\\\\\\\"body\\\\\\\":\\\\\\\"<p>5555555555<\\\\/p>\\\\\\\",\\\\\\\"map\\\\\\\":{\\\\\\\"name\\\\\\\":\\\\\\\"20141119_174417.jpg\\\\\\\",\\\\\\\"size\\\\\\\":3153767,\\\\\\\"url\\\\\\\":\\\\\\\"http:\\\\/\\\\/rc.okjiaoyu.cn\\\\/FvzHdIIZeB6mIWcRAo7Y2rmzbC8Y\\\\\\\"},\\\\\\\"answer\\\\\\\":[\\\\\\\"<p>555555555<\\\\/p>\\\\\\\"],\\\\\\\"analysis\\\\\\\":\\\\\\\"<p>5555555555555555555<\\\\/p>\\\\\\\",\\\\\\\"difficulty\\\\\\\":\\\\\\\"4\\\\\\\",\\\\\\\"type\\\\\\\":{\\\\\\\"id\\\\\\\":51,\\\\\\\"name\\\\\\\":\\\\\\\"\\\\u4f5c\\\\u56fe\\\\u9898\\\\\\\"}}]}\\\",\\\"level\\\":\\\"2\\\",\\\"source\\\":\\\"\\\",\\\"id\\\":\\\"2462214\\\",\\\"type\\\":\\\"21\\\",\\\"state\\\":\\\"PREVIEWED\\\",\\\"uploadId\\\":\\\"6257479 \\\",\\\"subjectId\\\":\\\"15\\\"}\"}";

        ContinuedRequest request=JsonUtil.readValue(ss,ContinuedRequest.class);
        CommonResult result=questionContinuedService.updateContinuedQuestion(request);
        logger.info(JsonUtil.obj2Json(result));
    }

    @Test
    public void testupdateQuestionHtmlImg(){
        String htmldata="{\"body\": \"<p>&#x9752;&#x6CE5;&#x4F55;&#x76D8;&#x76D8;&#xFF0C;<input type=\\\"text\\\" readonly=\\\"true\\\" class=\\\"questions-blank\\\" value=\\\"1\\\" contenteditable=\\\"true\\\"> &#x3002;&#xFF08;&#x674E;&#x767D;&#x300A;&#x8700;&#x9053;&#x96BE;&#x300B;&#xFF09;<br></p>\", \"answer\": [\"<p>&#x767E;&#x6B65;&#x4E5D;&#x6298;&#x8426;&#x5CA9;&#x5CE6;</p>\"], \"options\": [], \"analysis\": \"<p>&#x7565;<br></p>\"}";
        UpdateQuestionHtmlImgRequest req=new UpdateQuestionHtmlImgRequest();
        req.setId(23088L);
        req.setHtml(htmldata);
        CommonDes commonDes= questionService.updateQuestionHtmlImg(req);
        logger.info(JsonUtil.obj2Json(commonDes));
    }
   /* @Test
    public void testFindBlankAndAnswer(){
        FindBlankAndAnswerRequst req=new FindBlankAndAnswerRequst();
        List<BlankAndAnswer> blankAndAnswerList=new ArrayList<>();
        BlankAndAnswer b1=new BlankAndAnswer();
        b1.setIndex(0);
        b1.setQuestionId(4003727L);
        b1.setParentQuestionId(4003726);
        BlankAndAnswer b2=new BlankAndAnswer();
        b2.setIndex(1);
        b2.setQuestionId(4003728L);
        b2.setParentQuestionId(4003726);
        BlankAndAnswer b3=new BlankAndAnswer();
        b3.setIndex(0);
        b3.setQuestionId(4003651L);
        b3.setParentQuestionId(0);
        BlankAndAnswer b4=new BlankAndAnswer();
        b4.setIndex(0);
        b4.setQuestionId(2466791L);
        b4.setParentQuestionId(0);

        blankAndAnswerList.add(b1);
        blankAndAnswerList.add(b2);
        blankAndAnswerList.add(b3);
        blankAndAnswerList.add(b4);
        req.setBlankAndAnswers(blankAndAnswerList);
        questionContinuedService.findBlankAndAnswer(req);
        logger.info("ok");
    }*/
    @Test
    public void testDeleteQuestion(){
        DeleteQuestionRequest request=new DeleteQuestionRequest();
        request.setId(4644859L);
        request.setSystemId(62951129931L);
        questionService.deleteQuestion(request);
    }
}