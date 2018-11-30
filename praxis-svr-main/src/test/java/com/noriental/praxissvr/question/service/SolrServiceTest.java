package com.noriental.praxissvr.question.service;

import com.noriental.BaseTest;
import com.noriental.log.TraceKeyHolder;
import com.noriental.praxissvr.common.QuestionSort;
import com.noriental.praxissvr.question.utils.Constants;
import com.noriental.praxissvr.question.utils.SolrUtils;
import com.noriental.solr.bean.doc.LqResourceDocument;
import com.noriental.solr.bean.doc.QuestionDocument;
import com.noriental.solr.common.msg.SolrIndexReqMsg;
import com.noriental.solr.common.search.QueryMap;
import com.noriental.solr.common.search.SolrPage;
import com.noriental.solr.common.search.SolrQueryPageReq;
import com.noriental.solr.common.search.SolrQueryPageRsp;
import com.noriental.solr.common.utils.UUIDUtil;
import com.noriental.solr.service.search.LqResourceSolrSearchService;
import com.noriental.solr.service.search.QuestionSolrSearchService;
import com.noriental.validate.exception.BizLayerException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.noriental.praxissvr.exception.PraxisErrorCode.UPDATE_SOLR_FAIL;

/**
 * Created by hushuang on 2017/8/9.
 */
public class SolrServiceTest extends BaseTest {


    private static final Logger logger = LoggerFactory.getLogger(SolrServiceTest.class);
    @Resource
    private LqResourceSolrSearchService lqResourceSolrSearchService;
    @Resource
    private QuestionSolrSearchService questionSolrSearchService;
    @Resource
    private RabbitTemplate solrUploadQuestionRabbitTemplate;



    @Test
    public void lqResourceSolrSearchTest(){

        Map<String,Object> queryConf = new HashMap<>();

        queryConf.put(QueryMap.KEY_START, 0);
        queryConf.put(QueryMap.KEY_ROWS, 10);
        queryConf.put("sort", QuestionSort.UPDATE_TIME_DESC.getSort());

        Map<String, Object> qMap = new HashMap<>();
        qMap.put("systemId", 6106335);
        qMap.put("customListId3",24795);
        queryConf.put("q",qMap);

        SolrQueryPageRsp<LqResourceDocument> search = lqResourceSolrSearchService.search(new SolrQueryPageReq(queryConf));
        SolrPage<LqResourceDocument> page = search.getPage();
        List<LqResourceDocument> pageList = page.getList();
        List<Long> ids = new ArrayList<>();
        for (LqResourceDocument lqResourceDocument : pageList) {
            ids.add(lqResourceDocument.getQuestionId());
        }
        logger.info("=={}",ids);

    }


    @Test
    public void fBatchDeleteLqResourceIndexTest(){

        List<Long> ids = new ArrayList<>();
        ids.add(5L);
        SolrUtils.fBatchDeleteLqResourceIndex(ids,solrUploadQuestionRabbitTemplate);

    }


    @Test
    public void testDeleteSolrLink(){


        //更新solr
        Map<String, Object> mapBody = new HashMap<>();
        mapBody.put("id", 22554);
        mapBody.put("resourceStatus", 0);

        try {
            mapBody.put(Constants._DOC_CLASS_NAME, LqResourceDocument.class.getName());
            SolrIndexReqMsg msg = new SolrIndexReqMsg(mapBody);
            msg.setRequestId(TraceKeyHolder.getTraceKey());
            solrUploadQuestionRabbitTemplate.convertAndSend(msg);
        } catch (Exception e) {
            logger.error("删除习题更新solr失败：{}", e.getMessage());
            throw new BizLayerException("删除习题更新solr失败", UPDATE_SOLR_FAIL);
        }

    }


    @Test
    public void testlqResourceSolr(){

        Map<String, Object>  queryConf = new HashMap<String,Object>();
        Map<String,Object> qMap = new HashMap<>();
        queryConf.put(QueryMap.KEY_START, (1 - 1) * 10);
        queryConf.put(QueryMap.KEY_ROWS, 24);
        List<Long> ids=new ArrayList<>();
        ids.add(7671657L);
        //qMap.put("questionId",ids);
        qMap.put("systemId",6106335);
        qMap.put("resourceStatus",1);

        qMap.put("customListId1",24817);

        queryConf.put("q",qMap);
        SolrQueryPageReq solrQueryPageReq = new SolrQueryPageReq(queryConf);
        logger.info("\n<><><><><><><><><><><>{}",solrQueryPageReq.getReqId());
        SolrQueryPageRsp<LqResourceDocument> search = lqResourceSolrSearchService.search(solrQueryPageReq);
        logger.info("====={}",search.getPage().getList());

    }

    @Test
    public void questionSolrSearchServiceTest(){

        Map<String, Object>  queryConf = new HashMap<String,Object>();
        Map<String,Object> qMap = new HashMap<>();
        queryConf.put(QueryMap.KEY_START, 0);
        queryConf.put(QueryMap.KEY_ROWS, 10);
        List<Long> ids = new ArrayList<>();
        ids.add(4170369L);
        ids.add(4172849L);

        qMap.put("id",ids);

        HashMap<String,Object> raped = new HashMap<>();
        raped.put("sortedIds",ids);
        queryConf.put("raped",raped);

        //queryConf.put("sort", QuestionSort.UPDATE_TIME_DESC.getSort());
        queryConf.put("q",qMap);
        SolrQueryPageReq solrQueryPageReq = new SolrQueryPageReq(queryConf);
        SolrQueryPageRsp<QuestionDocument> search = questionSolrSearchService.search(solrQueryPageReq);
        SolrPage<QuestionDocument> page = search.getPage();
        logger.info("\n==============={}",page);
    }


    @Test
    public void updateLqResourceQuestion(){

        Map<String, Object> mapBody = new HashMap<>();
        mapBody.put("id", 22535);
        mapBody.put("resourceStatus", 0);

        mapBody.put(Constants._DOC_CLASS_NAME, LqResourceDocument.class.getName());

        SolrIndexReqMsg msg = new SolrIndexReqMsg(mapBody);
        msg.setRequestId(UUIDUtil.getUUID());
        logger.info("\n===================<><><><><><>{}",msg.getRequestId());
        solrUploadQuestionRabbitTemplate.convertAndSend(msg);

    }



}
