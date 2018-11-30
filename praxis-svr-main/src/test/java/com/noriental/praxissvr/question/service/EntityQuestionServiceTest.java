package com.noriental.praxissvr.question.service;

import com.alibaba.fastjson.JSONObject;
import com.noriental.BaseTest;
import com.noriental.adminsvr.bean.knowledge.TopicWithParent;
import com.noriental.adminsvr.bean.teaching.Chapter;
import com.noriental.adminsvr.request.RequestEntity;
import com.noriental.adminsvr.response.ResponseEntity;
import com.noriental.adminsvr.service.knowledge.TopicService;
import com.noriental.adminsvr.service.teaching.ChapterService;
import com.noriental.praxissvr.common.QuestionState;
import com.noriental.praxissvr.common.QuestionTypeEnum;
import com.noriental.praxissvr.common.QuestionVisibleEnum;
import com.noriental.praxissvr.question.bean.CollectionQuestion;
import com.noriental.praxissvr.question.bean.QuestionData;
import com.noriental.praxissvr.question.bean.queueBean.QuestionQuality;
import com.noriental.praxissvr.question.request.*;
import com.noriental.praxissvr.question.response.*;
import com.noriental.solr.bean.doc.LqResourceDocument;
import com.noriental.solr.bean.doc.QuestionDocument;
import com.noriental.solr.bean.doc.UserDocument;
import com.noriental.solr.common.msg.SolrIndexReqMsg;
import com.noriental.solr.common.search.QueryMap;
import com.noriental.solr.common.search.SolrPage;
import com.noriental.solr.common.search.SolrQueryPageReq;
import com.noriental.solr.common.search.SolrQueryPageRsp;
import com.noriental.solr.common.utils.UUIDUtil;
import com.noriental.solr.service.search.UserSolrSearchService;
import com.noriental.utils.json.JsonUtil;
import com.noriental.validate.bean.CommonDes;
import com.noriental.validate.bean.LongRequest;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created by hushuang on 2016/11/21.
 */

public class EntityQuestionServiceTest extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(EntityQuestionServiceTest.class);

    @Resource
    private ChapterService chapterService;
    @Resource
    private QuestionService questionService;
    @Resource
    private QuestionContinuedService questionContinuedService;
    @Resource
    private TopicService topicService;
    @Resource
    private RabbitTemplate solrUploadQuestionRabbitTemplate;
    @Resource
    private UserSolrSearchService userSolrSearchService;
    @Resource
    private RabbitTemplate createQuestionRecommendTemplate;



    @Test
    public void testCreateQuestionToRecommendQueue(){
        List<QuestionQuality> questionQualityList=new ArrayList<>();
        QuestionQuality questionQuality=new QuestionQuality();
        questionQuality.setQuestionId(11111111L);
        questionQuality.setDifficulty(1);
        questionQuality.setUploadId(1111112222222L);

        QuestionQuality questionQuality1=new QuestionQuality();
        questionQuality1.setQuestionId(222222222L);
        questionQuality1.setDifficulty(2);
        questionQuality1.setUploadId(2222222111111L);
        questionQualityList.add(questionQuality);
        questionQualityList.add(questionQuality1);
        //System.out.println(JsonUtil.obj2Json(questionQualityList));
        createQuestionRecommendTemplate.convertAndSend(JsonUtil.obj2Json(questionQualityList));
    }

    @Test
    public void updateLinkCustomDirectoryBySysIdAndCusDirIdTest(){

        UpdateCusDirRequest request = new UpdateCusDirRequest();
        request.setCusDirId(291L);
        request.setOldCusDirId(291L);
        request.setSystemId(6160381L);
        CommonDes commonDes = questionService.updateLinkCustomDirectoryBySysIdAndCusDirId(request);


    }

    @Test
    public void deleteCustomDiritoryTest(){
        DeleteCusDirRequest request = new DeleteCusDirRequest();
        request.setSystemId(6160381L);
        request.setCusDirId(4189L);
        CommonDes commonDes = questionService.deleteCustomDiritory(request);
    }


    @Test
    public void testCollectionQuestion(){
        CollectionQuestionRequest reques = new  CollectionQuestionRequest();
        reques.setQuestionId(4056853L);
        reques.setUploadId(6160381L);
        reques.setCustomerDirectoryId(286L);
        reques.setGroupId(4396L);
        CommonResult commonResult = questionService.collectionQuestion(reques);
    }

    @Test
    public void testCollectionQuestions(){
        CollectionQuestionsRequest reques = new  CollectionQuestionsRequest();
        List<CollectionQuestion> collections=new ArrayList<>();
        CollectionQuestion collectionQuestion=new CollectionQuestion();
        collectionQuestion.setQuestionId(4056853L);
        collectionQuestion.setUploadId(6160381L);
        collectionQuestion.setCustomerDirectoryId(992868888L);
        collectionQuestion.setGroupId(4396L);

        CollectionQuestion collectionQuestion1=new CollectionQuestion();
        collectionQuestion1.setQuestionId(4056854L);
        collectionQuestion1.setUploadId(6160381L);
        collectionQuestion1.setCustomerDirectoryId(287L);
        collectionQuestion1.setGroupId(4396L);

        collections.add(collectionQuestion);
        collections.add(collectionQuestion1);

        reques.setCollectionQuestions(collections);
        CommonResult commonResult = questionService.collectionQuestions(reques);
    }


    @Test
    public void testFindQuestionById(){

        LongRequest request = new LongRequest();
        request.setId(8401474L);
        GetQuestionByIdResponse response = questionService.findQuestionById(request);
        QuestionData questionData = response.getQuestionData();
        logger.info("\n 数据为：{}",questionData);
    }



    @Test
    public  void testFindByIds(){

        long chapterId=27249L;
        ResponseEntity<List<Chapter>> ids = chapterService.findByIds(new RequestEntity<>(Collections.singletonList(chapterId)));
        List<Chapter> entity = ids.getEntity();
        for (Chapter chapter : entity) {
            logger.info("=============="+chapter.getId());
        }




    }



    @Test
    public void testbatchQueryQuestionsByIds(){

        BatchQueryQuestionsRequest request = new BatchQueryQuestionsRequest();
        List<Long> ids = new ArrayList<>();
        ids.add(4009216L);
        ids.add(4009217L);
        ids.add(4009218L);
        request.setIds(ids);
        BatchQueryQuestionsResponse response = questionService.batchQueryQuestionsByIds(request);
        System.out.println("======"+response.getQuestionBeanList());

    }


    /**
     * 测试智能批改，更新solr智能批改状态
     */
    @Test
    public void testIntelligent(){

        Map<String, Object> bodyMap = new HashMap<>();

        bodyMap.put("id",4047213);
        bodyMap.put("difficulty",1);
        bodyMap.put(QueryMap._DOC_CLASS_NAME, QuestionDocument.class.getName());

        SolrIndexReqMsg msg = new SolrIndexReqMsg(bodyMap);
        msg.setRequestId(UUIDUtil.getUUID());

        System.out.println("requestid : "+msg.getRequestId()+"===="+"body:"+msg.getBody());

        solrUploadQuestionRabbitTemplate.convertAndSend(msg);

    }

    @Test
    public void testCreateLinkQuestionCustom(){
        Map<String, Object> bodyMap = new HashMap<>();

        bodyMap.put("id",4047213);
        bodyMap.put("difficulty",1);
        bodyMap.put(QueryMap._DOC_CLASS_NAME, LqResourceDocument.class.getName());

        SolrIndexReqMsg msg = new SolrIndexReqMsg(bodyMap);
        msg.setRequestId(UUIDUtil.getUUID());

        System.out.println("requestid : "+msg.getRequestId()+"===="+"body:"+msg.getBody());

        solrUploadQuestionRabbitTemplate.convertAndSend(msg);
    }

    @Test
    public void testCreateSolr(){

        QuestionDocument doc= new QuestionDocument();
        doc.setDifficulty(2);
        doc.setId(99999999L);
        doc.setParentQuestionId(7582884L);
        SolrIndexReqMsg solrIndexReqMsg = new SolrIndexReqMsg(doc);
        solrIndexReqMsg.setRequestId(1111111+"");
        solrUploadQuestionRabbitTemplate.convertAndSend(solrIndexReqMsg);

    }




    @Test
    public void findPadQuestionListByQuestionIds(){


        FindPadQuestionListByQuestionIdsRequest request = new FindPadQuestionListByQuestionIdsRequest();
        String reqStr="{\"reqId\":null,\"questionIds\":[11503928,11637698,4640674,11339041,10799833,10805091,11346849,5972409,11305637,11474452,5813350,4741567,11305631,10805093,11334003,10309036,11346872,11430293,10782133,11346875,10768318,10309084,10309057,6432166],\"exerciseId\":608067}";
        FindPadQuestionListByQuestionIdsRequest req = JsonUtil.readValue(reqStr, FindPadQuestionListByQuestionIdsRequest.class);
        List<Long> questionIds = new ArrayList<>();
        //测试线上http图片链接没有被适配好的问题---解决：solr中的相关题目数据重新同步后解决。线上数据导入开发环境库检查不出问题。
//        questionIds.add(11L);
//        questionIds.add(12L);
//        questionIds.add(13L);
//        questionIds.add(14L);
//        questionIds.add(15L);
//        questionIds.add(16L);
//        questionIds.add(17L);
//        questionIds.add(18L);
//        questionIds.add(19L);
//        questionIds.add(20L);
//        questionIds.add(21L);
//        questionIds.add(22L);
//        questionIds.add(23L);
//        questionIds.add(24L);
//        questionIds.add(25L);
//        questionIds.add(26L);
//        questionIds.add(27L);
//        questionIds.add(28L);

        //测试作图题的https，通过
//        questionIds.add(10321314L);
//        questionIds.add(10321313L);

        //questionIds.add(10818111L);
        //questionIds.add(8448689L);
        //questionIds.add(7194795L);
        //测试返回单个章节
//        questionIds.add(6329656L);
//        questionIds.add(10795008L);

        //stuBrushService.getWork和questionService.findPadQuestionListByQuestionIds返回测试dev环境题目数量不一致问题
//        questionIds.add(2513561L);
//        questionIds.add(6205029L);
//        questionIds.add(6211831L);
//        questionIds.add(6432740L);
//        questionIds.add(6765699L);
//        //htmldata为空的题目
//        questionIds.add(6301373L);


//
//        questionIds.add(8448689L);
//        questionIds.add(7194795L);
//        questionIds.add(10645194L);
//        questionIds.add(10795148L);
//        questionIds.add(10795145L);
//        questionIds.add(10795091L);
//        questionIds.add(10795089L);
//        questionIds.add(10795085L);
//        questionIds.add(10794962L);
//        questionIds.add(10794960L);
//        questionIds.add(10794954L);
//        questionIds.add(10794904L);
//        questionIds.add(10794901L);
//        questionIds.add(10794898L);
//        questionIds.add(10794895L);
//        questionIds.add(10794892L);
//        questionIds.add(10794889L);
//        questionIds.add(10793639L);
//        questionIds.add(10793634L);
//        questionIds.add(10793586L);
//        questionIds.add(10614881L);
//        questionIds.add(10614848L);
//        questionIds.add(10614825L);
//        questionIds.add(10614819L);
//        questionIds.add(10614800L);
//        questionIds.add(10614138L);
//        questionIds.add(10614119L);
//        questionIds.add(10613989L);
//        questionIds.add(10613896L);
//        questionIds.add(10613877L);
//        questionIds.add(10613848L);
//        questionIds.add(10613836L);
//        questionIds.add(10613832L);
//        questionIds.add(10613794L);
//        questionIds.add(10613265L);
//        questionIds.add(10613100L);
//        questionIds.add(10613096L);
//        questionIds.add(10613092L);
//        questionIds.add(10613091L);
//        questionIds.add(10613088L);
//        questionIds.add(10607279L);
//        questionIds.add(10606922L);
//        questionIds.add(10606743L);
//        questionIds.add(10606726L);
//        questionIds.add(10606700L);
//        questionIds.add(10606680L);
//        questionIds.add(10606658L);
//        questionIds.add(10606381L);
//        questionIds.add(10606313L);
//        questionIds.add(10606161L);

        //作图题
        //questionIds.add(10795008L);
        //questionIds.add(10818111L);
        //questionIds.add(8448689L);
        //questionIds.add(7194795L);
        //questionIds.add(10645194L);
        //10645194,10645202
        //questionIds.add(10645202L);
//        questionIds.add(7669741L);
//        questionIds.add(7669740L);
//        questionIds.add(7669739L);
//        questionIds.add(5963001L);
        //questionIds.add(3L);
        request.setQuestionIds(questionIds);
        request.setExerciseId(1000000L);
        FindPadQuestionListByQuestionIdsResponse response = questionService.findPadQuestionListByQuestionIds(req);

        System.out.println("=========="+response.getQuestionList());

    }



    @Test
    public void batchCloseQuestion(){
        StateQuestionRequest request = new StateQuestionRequest();
        List<Long> requestIds = new ArrayList<>();
        requestIds.add(4003698L);
        request.setRequestIds(requestIds);
        questionContinuedService.batchCloseQuestion(request);

    }
    @Test
    public void batchStartQuestion(){
        StateQuestionRequest request = new StateQuestionRequest();
        List<Long> requestIds = new ArrayList<>();
        requestIds.add(4003698L);
        request.setRequestIds(requestIds);
        questionContinuedService.batchStartQuestion(request);

    }

    @Test
    public void createContinuedQuestion(){
        ContinuedRequest request = new ContinuedRequest();
        String str = "{\"typeId\": 1, \"src\": 0, \"group\": 0, \"uploadSrc\": 120, " +
                "\"level\": 1, \"topicIds\": [15748], \"subjectId\": 1, \"typeName\": \"\\u9009\\u62e9\\u9898\", " +
                "\"state\": \"ENABLED\", \"html\": \"{\"body\": \"<p>&#x4E0B;&#x5217;&#x5404;&#x9879;&#x4E2D;&#x5212;" +
                "&#x7EBF;&#x5B57;&#x6CE8;&#x97F3;&#x5168;&#x5BF9;&#x7684;&#x4E00;&#x9879;&#x662F;</p><br " +
                "/><p>[&#x00A0;&#x00A0;&#x00A0;&#x00A0; ]</p>\",  \"analysis\": \"<p></p><br /><p><img src=\\\"http://qdimg.okjiaoyu" +
                ".cn/qdimg_62edfc3dd1916095d4771775d64fdab5.png\\\" width=\\\"21px\\\" height=\\\"51px\\\"></p>\", " +
                "\"questions\": [], \"answer\": [\"C\"], \"audio\": {\"url\": \"\", \"name\": \"\", \"size\": \"\"}, " +
                "\"options\": [\"<p><u>&#x7EA4;</u>&#xFF08;xi&#x0101;n&#xFF09;&#x7EC6;&#x915D;<u>&#x917F;</u>&#xFF08;" +
                "ni&#x00E0;ng&#xFF09;&#x6839;&#x6DF1;<u>&#x8482;</u>&#xFF08;t&#x00EC;&#xFF09;&#x56FA;<u>&#x6096;" +
                "</u>&#xFF08;b&#x00E8;i&#xFF09;&#x8C2C;</p>\", \"<p><u>&#x7EA4;</u>&#xFF08;xi&#x0101;n&#xFF09;" +
                "&#x7EC6;&#x915D;<u>&#x917F;</u>&#xFF08;ni&#x00E0;ng&#xFF09;&#x6839;&#x6DF1;<u>&#x8482;</u>&#xFF08;" +
                "t&#x00EC;&#xFF09;&#x56FA;<u>&#x6096;</u>&#xFF08;b&#x00E8;i&#xFF09;&#x8C2C;</p>\", \"<p><u>&#x7EA4;" +
                "</u>&#xFF08;xi&#x0101;n&#xFF09;&#x7EC6;&#x915D;<u>&#x917F;</u>&#xFF08;ni&#x00E0;ng&#xFF09;&#x6839;" +
                "&#x6DF1;<u>&#x8482;</u>&#xFF08;t&#x00EC;&#xFF09;&#x56FA;<u>&#x6096;</u>&#xFF08;b&#x00E8;i&#xFF09;" +
                "&#x8C2C;</p>\", \"<p><u>&#x7EA4;</u>&#xFF08;xi&#x0101;n&#xFF09;&#x7EC6;&#x915D;<u>&#x917F;" +
                "</u>&#xFF08;ni&#x00E0;ng&#xFF09;&#x6839;&#x6DF1;<u>&#x8482;</u>&#xFF08;t&#x00EC;&#xFF09;&#x56FA;" +
                "<u>&#x6096;</u>&#xFF08;b&#x00E8;i&#xFF09;&#x8C2C;</p>\", \"<p>&#x5D99;<u>&#x5CCB;</u>&#xFF08;" +
                "x&#x00FA;n&#xFF09;&#x72E1;<u>&#x9EE0;</u>&#xFF08;xi&#x00E9;&#xFF09;<u>&#x60F4;</u>&#xFF08;" +
                "zhu&#x00EC;&#xFF09;&#x60F4;&#x4E0D;&#x5B89;&#x79C0;<u>&#x9880;</u>&#xFF08;q&#x00ED;&#xFF09;</p>\", " +
                "\"<p>&#x5D99;<u>&#x5CCB;</u>&#xFF08;x&#x00FA;n&#xFF09;&#x72E1;<u>&#x9EE0;</u>&#xFF08;xi&#x00E9;" +
                "&#xFF09;<u>&#x60F4;</u>&#xFF08;zhu&#x00EC;&#xFF09;&#x60F4;&#x4E0D;&#x5B89;&#x79C0;<u>&#x9880;" +
                "</u>&#xFF08;q&#x00ED;&#xFF09;</p>\", \"<p>&#x5D99;<u>&#x5CCB;</u>&#xFF08;x&#x00FA;n&#xFF09;&#x72E1;" +
                "<u>&#x9EE0;</u>&#xFF08;xi&#x00E9;&#xFF09;<u>&#x60F4;</u>&#xFF08;zhu&#x00EC;&#xFF09;&#x60F4;&#x4E0D;" +
                "&#x5B89;&#x79C0;<u>&#x9880;</u>&#xFF08;q&#x00ED;&#xFF09;</p>\", \"<p>&#x5D99;<u>&#x5CCB;</u>&#xFF08;" +
                "x&#x00FA;n&#xFF09;&#x72E1;<u>&#x9EE0;</u>&#xFF08;xi&#x00E9;&#xFF09;<u>&#x60F4;</u>&#xFF08;" +
                "zhu&#x00EC;&#xFF09;&#x60F4;&#x4E0D;&#x5B89;&#x79C0;<u>&#x9880;</u>&#xFF08;q&#x00ED;&#xFF09;</p>\", " +
                "\"<p><u>&#x7701;</u>&#xFF08;x&#x01D0;ng&#xFF09;&#x609F;<u>&#x707C;</u>&#xFF08;zhu&#x00F3;&#xFF09;" +
                "&#x4F24;&#x5766;&#x8361;&#x5982;<u>&#x7825;</u>&#xFF08;d&#x01D0;&#xFF09;&#x65E0;<u>&#x57A0;" +
                "</u>&#xFF08;y&#x00ED;n&#xFF09;</p>\", \"<p><u>&#x7701;</u>&#xFF08;x&#x01D0;ng&#xFF09;&#x609F;" +
                "<u>&#x707C;</u>&#xFF08;zhu&#x00F3;&#xFF09;&#x4F24;&#x5766;&#x8361;&#x5982;<u>&#x7825;</u>&#xFF08;" +
                "d&#x01D0;&#xFF09;&#x65E0;<u>&#x57A0;</u>&#xFF08;y&#x00ED;n&#xFF09;</p>\", \"<p><u>&#x7701;" +
                "</u>&#xFF08;x&#x01D0;ng&#xFF09;&#x609F;<u>&#x707C;</u>&#xFF08;zhu&#x00F3;&#xFF09;&#x4F24;&#x5766;" +
                "&#x8361;&#x5982;<u>&#x7825;</u>&#xFF08;d&#x01D0;&#xFF09;&#x65E0;<u>&#x57A0;</u>&#xFF08;y&#x00ED;" +
                "n&#xFF09;</p>\", \"<p><u>&#x7701;</u>&#xFF08;x&#x01D0;ng&#xFF09;&#x609F;<u>&#x707C;</u>&#xFF08;" +
                "zhu&#x00F3;&#xFF09;&#x4F24;&#x5766;&#x8361;&#x5982;<u>&#x7825;</u>&#xFF08;d&#x01D0;&#xFF09;&#x65E0;" +
                "<u>&#x57A0;</u>&#xFF08;y&#x00ED;n&#xFF09;</p>\", \"<p>&#x606B;<u>&#x5413;</u>&#xFF08;xi&#x00E0;" +
                "&#xFF09;<u>&#x7317;</u>&#xFF08;y&#x012B;&#xFF09;&#x90C1;&#x6B7B;&#x8005;&#x76F8;<u>&#x85C9;" +
                "</u>&#xFF08;ji&#x00E8;&#xFF09;&#x719F;<u>&#x7A14;</u>&#xFF08;r&#x011B;n&#xFF09;</p>\", \"<p>&#x606B;" +
                "<u>&#x5413;</u>&#xFF08;xi&#x00E0;&#xFF09;<u>&#x7317;</u>&#xFF08;y&#x012B;&#xFF09;&#x90C1;&#x6B7B;" +
                "&#x8005;&#x76F8;<u>&#x85C9;</u>&#xFF08;ji&#x00E8;&#xFF09;&#x719F;<u>&#x7A14;</u>&#xFF08;r&#x011B;" +
                "n&#xFF09;</p>\", \"<p>&#x606B;<u>&#x5413;</u>&#xFF08;xi&#x00E0;&#xFF09;<u>&#x7317;</u>&#xFF08;" +
                "y&#x012B;&#xFF09;&#x90C1;&#x6B7B;&#x8005;&#x76F8;<u>&#x85C9;</u>&#xFF08;ji&#x00E8;&#xFF09;&#x719F;" +
                "<u>&#x7A14;</u>&#xFF08;r&#x011B;n&#xFF09;</p>\", \"<p>&#x606B;<u>&#x5413;</u>&#xFF08;xi&#x00E0;" +
                "&#xFF09;<u>&#x7317;</u>&#xFF08;y&#x012B;&#xFF09;&#x90C1;&#x6B7B;&#x8005;&#x76F8;<u>&#x85C9;" +
                "</u>&#xFF08;ji&#x00E8;&#xFF09;&#x719F;<u>&#x7A14;</u>&#xFF08;r&#x011B;n&#xFF09;</p>\"]}\", " +
                "\"uploadId\": 62951096208, \"source\": \"content_group\", \"chapterId\": 0, \"orgId\": 1, " +
                "\"orgType\": 4, \"new_format\": 1}";
        String tingkouStr="{\"typeId\": 59, \"src\": 0, \"group\": 0, \"uploadSrc\": 115, \"level\": 1, \"topicIds\": [17638], \"subjectId\": 6, \"typeName\": \"看词义点击单词\", \"state\": \"ENABLED\", \"html\": \"{'body': '<p>打字机;</p>', 'map':{'url': '', 'name': '', 'size': ''}, 'analysis': '<p>考核词汇的词义</p>', 'answer': ['B'], 'audio': {'url': '', 'name': '', 'size': ''}, 'options': ['<p>official</p>', '<p>typewriter</p>', '<p>schedule</p>', '<p>oxygen</p>']}\", \"uploadId\": 62951096197, \"source\": \"content_group\", \"chapterId\": 0, \"orgId\": 1, \"intelligent\": 0, \"orgType\": 4, \"new_format\": 1}";
        request.setObject(tingkouStr);
        CommonResult result = questionContinuedService.createContinuedQuestion(request);
        System.out.println(result.getId());
    }




    @Test
    public void getTopicList(){

        List<Long> topicIds =  new ArrayList<>();
        topicIds.add(34L);

        ResponseEntity<List<TopicWithParent>> entity =
                topicService.findTopicsWithParent(new RequestEntity<>(topicIds));

        List<TopicWithParent> entity1 = entity.getEntity();
        System.out.println(entity1.get(0).getId()+"=="+entity1.get(0).getUnit().getId()+"=="+entity1.get(0).getModule().getId());
    }


    @Test
    public void testGetById(){

        long l = System.currentTimeMillis();
        LongRequest request = new LongRequest();
        //request.setId(1401111L);
        request.setId(4139531L);
        //request.setId(3032904L);

        GetQuestionByIdResponse response = questionService.findQuestionById(request);
        long l1 = System.currentTimeMillis();

        System.out.print("=================================================\n");
        System.out.println(response.getQuestionData());

        System.out.print("=================================================\n");



        logger.error("++++++++++++++++++++++消耗时间:"+(l1-l)/1000f+"s");

    }



    /**
     * 上传习题测试
     */
    @Test
    public void testUploadQuestion() {


        UploadQuestionRequest request = new UploadQuestionRequest();
        //System.out.println(QuestionServiceUtil.htmlStyleRegex);
        //白名单和乱码html测试
        request.setHtml("{\"body\":\"<p>8选择题</p><style=\\\"width: 58px; height: 46px;\\\"><style=\\\"text-decoration: underline;\\\">\",\"options\":[\"<p>撒的撒<br/></p>\",\"<p>滴滴顺风车</p>\",\"<p>发顺丰撒</p>\",\"<p>范德萨发的</p>\"],\"audio\":{\"url\":\"\",\"name\":\"\",\"size\":\"\"},\"answer\":[\"A\",\"C\"],\"analysis\":\"<p>发大水发大水</p>\",\"questions\":[]}");
        //request.setHtml("{\"body\":\"<p>百度新<span style=\\\"text-decoration: line-through;\\\">闻\\n\\n百度一下\\t帮助高级搜索设置\\n新闻全文新闻标题\\n首页\\n百家号\\n国内\\n国际\\n军事\\n社会\\n财经\\n娱乐\\n体育\\n互联网\\n科技\\n游戏\\n时尚\\n女人\\n汽车\\n房产\\n个性推荐\\n热</span>点要闻个性推荐\\n十九大后，习近平对中国经济给出8大论断\\n2017年中国经济回眸：民生改善，实打实的获得感\\n习近平主席视察王杰生前所在连<span style=\\\"text-decoration: underline;\\\">在全军引</span>起强烈反响\\n带您感知十九大报告的民生温度 &nbsp;领航新征程\\n开放透明 同中国合作大有可为 新时代新气象新作为\\n蓝天与温暖，一个也不能少 &nbsp;暖新闻</p><p><br/></p><ul><li><p>全国20余省份试水聘任制公务员 专家：比例不应超0.5%</p></li><p></p><li><p>经济风向标向好：前11个月全社会用电量同比增长6.5%</p></li><li><p>甘肃省市联合工作组彻查兰州危废污染</p></li><li><p>港珠澳大桥澳门口岸整体亮灯 具备通车条件</p></li><li><p>上海浦东一商务楼深夜起火 现场火光冲天</p></li><li><p>台抗议被日地图标注为中国一省 外交部：难道不是吗</p></li><li><p>辽宁鞍山海城市发生4.4级地震，震源深度10千米</p></li><li><p>那所勇敢捍卫国歌尊严的香港学校，背景太不简单！</p></li><p><br/></p><p><br/></p></ul>\",\"options\":[\"<ul><li><p>全国20余省份试水聘任制公务员 专家：比例不应超0.5%</p></li><p></p><li><p>经济风向标向好：前11个月全社会用电量同比增长6.5%</p></li><li><p>甘肃省市联合工作组彻查兰州危废污染</p></li><li><p>港珠澳大桥澳门口岸整体亮灯 具备通车条件</p></li><li><p>上海浦东一商务楼深夜起火 现场火光冲天</p></li><li><p>台抗议被日地图标注为中国一省 外交部：难道不是吗</p></li><li><p>辽宁鞍山海城市发生4.4级地震，震源深度10千米</p></li><li><p>那所勇敢捍卫国歌尊严的香港学校，背景太不简单！</p></li><p><br/></p></ul>\",\"<ul><li><p>全国20余省份试水聘任制公务员 专家：比例不应超0.5%</p></li><p></p><li><p>经济风向标向好：前11个月全社会用电量同比增长6.5%</p></li><li><p>甘肃省市联合工作组彻查兰州危废污染</p></li><li><p>港珠澳大桥澳门口岸整体亮灯 具备通车条件</p></li><li><p>上海浦东一商务楼深夜起火 现场火光冲天</p></li><li><p>台抗议被日地图标注为中国一省 外交部：难道不是吗</p></li><li><p>辽宁鞍山海城市发生4.4级地震，震源深度10千米</p></li><li><p>那所勇敢捍卫国歌尊严的香港学校，背景太不简单！</p></li><p><br/></p></ul>\",\"<ul><li><p>全国20余省份试水聘任制公务员 专家：比例不应超0.5%</p></li><p></p><li><p>经济风向标向好：前11个月全社会用电量同比增长6.5%</p></li><li><p>甘肃省市联合工作组彻查兰州危废污染</p></li><li><p>港珠澳大桥澳门口岸整体亮灯 具备通车条件</p></li><li><p>上海浦东一商务楼深夜起火 现场火光冲天</p></li><li><p>台抗议被日地图标注为中国一省 外交部：难道不是吗</p></li><li><p>辽宁鞍山海城市发生4.4级地震，震源深度10千米</p></li><li><p>那所勇敢捍卫国歌尊严的香港学校，背景太不简单！</p></li><p><br/></p></ul>\",\"<p>asdf&nbsp;<img src=\\\"http://rc.okjiaoyu.cn/o_1c1m8gv0sjsp87g7db5tlsrk10.png\\\" width=\\\"568\\\" height=\\\"404\\\"/>qqq<img class=\\\"kfformula\\\" width=\\\"70\\\" height=\\\"53\\\" src=\\\"http://rc.okjiaoyu.cn/rc_UNAV9coDWU.png\\\" data-latex=\\\"\\\\frac {dy} {dx}\\\"/></p>\"],\"analysis\":\"<p>asdsa</p>\",\"answer\":[\"B\"],\"questions\":[]}");
        //常规单题
        //request.setHtml("{\"body\":\"<p>选择题</p><style=\\\"width: 58px; height: 46px;\\\"><style=\\\"text-decoration: underline; sfas: 46px;\\\">\",\"options\":[\"<p>撒的撒<br/></p>\",\"<p>滴滴顺风车</p>\",\"<p>发顺丰撒</p>\",\"<p>范德萨发的</p>\"],\"audio\":{\"url\":\"\",\"name\":\"\",\"size\":\"\"},\"answer\":[\"A\",\"C\"],\"analysis\":\"<p>发大水发大水</p>\",\"questions\":[]}");
        //request.setHtml("{\"body\": \"<p>??<input type=\\\"text\\\" readonly=\\\"true\\\" class=\\\"questions-blank\\\" value=\\\"1\\\" contenteditable=\\\"true\\\"/>&nbsp;</p>\",\"analysis\": \"<p>??</p>\",\"answer\": [\"<p>??</p>\"],\"questions\": []}");
        //request.setHtml("{\"body\":\"<p>fsfadsfsda</p>\",\"audio\":{\"url\":\"\",\"name\":\"\",\"size\":\"\"},\"answer\":\"1\",\"analysis\":\"<p>fdsfdsafasd</p>\",\"questions\":[]}");
        //复合题
        //request.setHtml("{\"audio\":{\"url\":\"\",\"name\":\"\",\"size\":\"\"},\"questions\":[{\"type\":{\"id\":1,\"name\":\"选择题\"},\"body\":\"<p>选择题</p>\",\"options\":[\"<p>撒范德萨a<br/></p>\",\"<p>&nbsp;阿斯顿发多少</p>\",\"<p>&nbsp;发的所发生的</p>\",\"<p>&nbsp;阿斯顿发多少</p>\"],\"answer\":[\"A\",\"B\"],\"analysis\":\"<p>发第三方</p>\",\"difficulty\":\"1\"},{\"type\":{\"id\":1,\"name\":\"选择题\"},\"body\":\"<p>选择题2</p>\",\"options\":[\"<p>法第三方</p>\",\"<p>&nbsp;啥地方都是</p>\",\"<p>&nbsp;大发 的范德萨</p>\",\"<p>&nbsp;都是法第三方都是</p>\"],\"answer\":[\"A\",\"C\",\"D\"],\"analysis\":\"<p>&nbsp;阿萨德十大</p>\",\"difficulty\":\"2\"},{\"type\":{\"id\":2,\"name\":\"填空题\"},\"body\":\"<p>&nbsp;发多少发多少<input type=\\\"text\\\" readonly=\\\"true\\\" class=\\\"questions-blank\\\" value=\\\"1\\\" contenteditable=\\\"true\\\"/>&nbsp;</p>\",\"options\":[],\"answer\":[\"<p>&nbsp;十大范德萨方法发</p>\"],\"analysis\":\"<p>&nbsp;水电费都是发多少</p>\",\"difficulty\":\"3\"},{\"type\":{\"id\":3,\"name\":\"判断题\"},\"body\":\"<p>判断题</p>\",\"options\":[],\"answer\":[\"0\"],\"analysis\":\"<p>水电费大</p>\",\"difficulty\":\"4\"},{\"type\":{\"id\":4,\"name\":\"简答题\"},\"body\":\"<p>简答题</p>\",\"options\":[],\"answer\":[\"<p>发发大烦死哒</p>\"],\"analysis\":\"<p>&nbsp;撒地方第三方发</p>\",\"difficulty\":\"4\"},{\"type\":{\"id\":4,\"name\":\"简答题\"},\"body\":\"<p>简答题</p>\",\"options\":[],\"answer\":[\"<p>&nbsp;都是发多少发</p>\"],\"analysis\":\"<p>撒地方爱迪生</p>\",\"difficulty\":\"1\"}],\"material\":\"<p>文言文阅读</p>\"}");

        List<Long> tops = new ArrayList<Long>();
//        tops.add(13894L);
//
//        request.setChapterId(7772L);
//        request.setLevel(2);
//        request.setType(QuestionTypeEnum.DAN_XUAN);
//        //request.setGroup(0);
//        request.setRef(0);
//        request.setSubjectId(1L);
//        request.setUploadId(1L);
//        request.setUploadSrc(2);
        //request.setNewFormat(0);

        tops.add(328L);
        request.setChapterId(0L);
        request.setLevel(2);
        //request.setType(QuestionTypeEnum.DUO_XUAN);
        request.setType(1);
        request.setGroupId(36344L);
        request.setCustomerDirectory(2158725L);
        request.setRef(0);
        request.setSubjectId(13L);
        request.setUploadId(62951051116L);
        request.setUploadSrc(3);
        request.setSource("test1");
        request.setOrgId(567);
        request.setOrgType(4);

        request.setTopicIds(tops);
        List<Long> specials=new ArrayList<>();
        specials.add(20180427111L);
        specials.add(20180427222L);
        request.setSpecials(specials);

        long l = System.currentTimeMillis();
        CommonDes commonDes = questionService.createQuestion(request);
        long l1 = System.currentTimeMillis();
        logger.info("{}", commonDes.getMessage());
        logger.info("l1-l=====<><><<><><><>><<>{}", (l1-l)/1000f+"S" +"=======");

    }

    @Test
    public void testFindQuestions(){

        FindQuestionsRequest request = new FindQuestionsRequest();
        request.setPageSize(20);
        request.setPageNo(1);
        request.setPageable(false);
        request.setSameFilter(false);
        request.setUploadId(null);
        request.setSubjectId(null);
        request.setNewFormat(false);
        request.setVisible(QuestionVisibleEnum.ALL);
        request.setQuestionType(QuestionTypeEnum.ALL);
        request.setQuestionTypeId(null);
        request.setDifficulty(null);
        request.setQuestionGroup(null);
        request.setModuleIds(null);
        request.setUnitIds(null);
        request.setTopicIds(null);
        request.setSeriesIds(null);
        List<Long> ids = new ArrayList<>();
//        ids.add(11345862L);
//        ids.add(4L);
//        ids.add(10330082L);
        //问题题目，测试html为空时的处理。
        ids.add(6301373L);

        ids.add(2872618L);
        ids.add(11014908L);
        ids.add(2577775L);
        //request.setIds(Arrays.asList(5908865L,5908866L,5908867L,5908868L,5908869L,5908880L,5908882L,5908883L,5908884L,5908886L,5908897L,5908903L,5908907L,5965080L,6704195L,5965017L,6440695L,6440489L));
        request.setIds(Arrays.asList(569L));
        request.setStates(Arrays.asList(QuestionState.ALL));
        request.setPaperId(null);
        request.setPaperTypeId(null);
        request.setGradeId(null);
        request.setPaperRegion(null);
        request.setBasic(false);
        request.setSubStates(Arrays.asList(QuestionState.ALL));
        request.setOrgId(0);


        FindQuestionsResponse questions = questionService.findQuestions(request);
        logger.info("\n====>{}",questions.getQuestionList());

    }

    @Test
    public void testUpdateCustomerDiretory(){
        UpCusDirRequest request = new UpCusDirRequest();
        request.setSystemId(100L);
        request.setCusDirId(100L);
        request.setQuestionId(100L);
        questionService.updateCustomerDiretory(request);
    }


    @Test
    public void findMine() throws Exception {

        String reqStr="{\"reqId\":null,\"questionType\":0,\"pageNo\":11,\"pageSize\":10,\"pageable\":true,\"systemId\":6160381,\"difficulty\":null,\"cusDirId1\":42054,\"cusDirId2\":null,\"cusDirId3\":null,\"questionGroupId\":null,\"moduleIds\":null,\"unitIds\":null,\"topicIds\":null,\"isOffset\":1}";
        String reqStr1="{systemId=6160381, subjectId=5, questionType=0, difficulty=null, cusDirId1=42095, cusDirId2=null, cusDirId3=null, questionGroupId=null, moduleIds=null, unitIds=null, topicIds=null,\"pageNo\":1,\"pageSize\":10}";
        FindMyQuestionRequest req = JsonUtil.readValue(reqStr, FindMyQuestionRequest.class);
        FindMyQuestionRequest request = new FindMyQuestionRequest();
//        request.setCusDirId3(24795L);
//        request.setSystemId(6106335L);

        //request.setCusDirId1(674530L);
        //request.setCusDirId2(3266487L);
        request.setSystemId(61951243814L);
        request.setPageSize(10);
        request.setPageNo(1);
        request.setQuestionType(-1);
        request.setSubjectId(12L);
        //request.setDifficulty(Difficulty.NORMAL);

        FindQuestionsResponse resp = questionService.findMine(req);
        System.out.println(JSONObject.toJSONString(resp, true));
        System.out.println("===============>>>>>>>>>>"+resp.getQuestionList().toString());
    }

    @Test
    public void testFindQuestionsAllFiled() {
        FindQuestionsRequest request = new FindQuestionsRequest();
        request.setPageSize(10);
        request.setPageNo(1);
        request.setPageable(false);
        request.setSameFilter(false);
        request.setUploadId(null);
        request.setSubjectId(null);
        request.setNewFormat(false);
        request.setVisible(QuestionVisibleEnum.ALL);
        request.setQuestionType(QuestionTypeEnum.ALL);
        request.setQuestionTypeId(null);
        request.setDifficulty(null);
        request.setQuestionGroup(null);
        request.setModuleIds(null);
        request.setUnitIds(null);
        request.setTopicIds(null);
        request.setSeriesIds(null);
        List<Long> ids = new ArrayList<>();
        //ids.add(11345888L);
        ids.add(11014822L);
        request.setIds(ids);
        request.setStates(Arrays.asList(QuestionState.ALL));
        request.setPaperId(null);
        request.setPaperTypeId(null);
        request.setGradeId(null);
        request.setPaperRegion(null);
        request.setBasic(false);
        request.setSubStates(Arrays.asList(QuestionState.ALL));
        request.setOrgId(0);


        FindQuestionsResponse questions = questionService.findQuestionsAllFiled(request);
        logger.info("\n====>{}",questions.getQuestionList());
    }

    @Test
    public void testUserSolrSearch(){
        List<Long> uploadIds=new ArrayList<>();
        uploadIds.add(62951096200L);
        uploadIds.add(6257423L);
        Map<String, Object> qMap = new HashMap<>();
        Map<String, Object> userQueryMap = new HashMap<>();
        qMap.put("systemId", uploadIds);
        userQueryMap.put("q",qMap);
        userQueryMap.put(QueryMap.KEY_START,1);
        userQueryMap.put(QueryMap.KEY_ROWS,50);
        SolrQueryPageRsp<UserDocument> userDocumentSolrQueryPageRsp= userSolrSearchService.search(new SolrQueryPageReq
                (userQueryMap));
        SolrPage<UserDocument> userList= userDocumentSolrQueryPageRsp.getPage();

        logger.info(userList.getList().get(0).getName().toString());
    }
}
