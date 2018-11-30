package com.noriental.praxissvr.questionSearch.serviceImpl;

import com.noriental.praxissvr.exception.PraxisErrorCode;
import com.noriental.praxissvr.question.bean.Question;
import com.noriental.praxissvr.question.request.FindAllQuestionTypeRequest;
import com.noriental.praxissvr.question.request.FindQuestionsRequest;
import com.noriental.praxissvr.question.response.FindAllQuestionTypeResponse;
import com.noriental.praxissvr.question.response.FindQuestionsResponse;
import com.noriental.praxissvr.question.service.QuestionTypeService;
import com.noriental.praxissvr.questionSearch.mapper.QuestionSearchMapper;
import com.noriental.praxissvr.questionSearch.service.QuestionSearchService;
import com.noriental.praxissvr.questionSearch.util.SolrSearchUtil;
import com.noriental.solr.bean.doc.QuestionDocument;
import com.noriental.solr.common.search.SolrPage;
import com.noriental.solr.common.search.SolrQueryPageReq;
import com.noriental.solr.common.search.SolrQueryPageRsp;
import com.noriental.solr.service.search.QuestionSolrSearchService;
import com.noriental.utils.entity.EntityUtils;
import com.noriental.validate.exception.BizLayerException;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.*;

/**
 * Created by kate on 2017/8/8.
 * 使用通用的方法性能太差，单独出来只查询需要的信息
 */
@Service
public class QuestionSearchServiceImpl implements QuestionSearchService {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @Autowired
    private QuestionSearchMapper questionSearchMapper;
    @Autowired
    private QuestionSolrSearchService questionSolrSearchService;
    @Autowired
    private QuestionTypeService questionTypeService;

    @Override
    public List<Question> getQuesListByIds(List<Long> ids) {
        return questionSearchMapper.getQuesListByIds(ids);
    }

    /***
     * 根据大题ID查询题目信息
     * @param request
     * @return
     * @throws BizLayerException
     */
    @Override
    public FindQuestionsResponse findSolrQuestions(FindQuestionsRequest request) throws BizLayerException {
        long querySolrStart = System.currentTimeMillis();
        FindQuestionsResponse resp = new FindQuestionsResponse();
        Map<String, Object> queryConf = SolrSearchUtil.buildSolrQueryMap(request);

        SolrQueryPageRsp<QuestionDocument> search = questionSolrSearchService.search(new SolrQueryPageReq(queryConf));
        if (!search.success()) {
            throw new BizLayerException("数据查询异常|", PraxisErrorCode.PRAXIS_INVOKE_SOLR);
        }
        SolrPage<QuestionDocument> solrPages = search.getPage();

        // 所有题
        List<QuestionDocument> allQuestionDocuments = solrPages.getList();
        long totalPage = solrPages.getTotalPage();
        long totalCount = solrPages.getTotalCount();
        long currentPage = solrPages.getCurrentPage();
        List<Question> returnList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(allQuestionDocuments)) {
            Set<Long> topicIds = new HashSet<>();
            // 遍历试题
            List<Long> resultQuestionIds = new ArrayList<>();
            FindAllQuestionTypeResponse allQuestionTypes = questionTypeService.findAllQuestionType(new
                    FindAllQuestionTypeRequest());
            for (QuestionDocument questDoc : allQuestionDocuments) {
                // solr中试题信息
                try {
                    Question question = EntityUtils.copyValueDeep2Object(questDoc, 1, Question.class, 1);
                    question.setSingle(questDoc.getIsSingle() != null && questDoc.getIsSingle() == 1);
                    //处理历史数据，并更新类型详情字段
                    SolrSearchUtil.analyzeQuestionType(question);
                    // 题型结构
                    SolrSearchUtil.analyzeQuestionStruct(question, allQuestionTypes);
                    returnList.add(question);
                    resultQuestionIds.add(question.getId());
                } catch (InstantiationException | IllegalAccessException | IllegalArgumentException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        resp.setCurrentPage(currentPage);
        resp.setTotalPageCount(totalPage);
        resp.setTotalCount(totalCount);
        resp.setQuestionList(returnList);
        long querySolrEnd = System.currentTimeMillis();
        logger.info("findSolrQuestions query solr cost:{}ms", (querySolrEnd - querySolrStart));
        return resp;

    }



}
