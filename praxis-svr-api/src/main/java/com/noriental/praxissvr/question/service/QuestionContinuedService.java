package com.noriental.praxissvr.question.service;

import com.noriental.praxissvr.question.request.*;
import com.noriental.praxissvr.question.response.*;
import com.noriental.validate.exception.BizLayerException;

/**
 * Created by hushuang on 2017/3/6.
 *  题目操作的服务接口
 */
public interface QuestionContinuedService {
    /**
     * 题目透传服务
     */
    CommonResult createContinuedQuestion(ContinuedRequest request) throws BizLayerException;


    /**
     * 透题更新服务
     * @param req
     * @return
     * @throws BizLayerException
     */
    CommonResult updateContinuedQuestion(ContinuedRequest req) throws BizLayerException;


    /**
     * 关闭题目
     * @param request
     * @throws BizLayerException
     */
    void batchCloseQuestion(StateQuestionRequest request) throws BizLayerException;

    /**
     * 开启题目
     * @param request
     * @throws BizLayerException
     */
    void batchStartQuestion(StateQuestionRequest request) throws BizLayerException;
    /**
     * 通过题目ID查询题目答案
     * @param requst
     * @return
     * @throws BizLayerException
     */
    FindBlankAndAnswerResponse findBlankAndAnswer(FindBlankAndAnswerRequst requst) throws BizLayerException;

    /**
     * 根据章节ID查询单词列表
     * @param request
     * @return
     */
    FindWordByChapterResponse findWordsByChapterId(FindWordByChapterRequest request);

    /**
     * 通过单词列表和题型列表快速查询单词题
     * @param request
     * @return
     */
    FindQuestionsByTypesAndWordsResponse findQuestionsByTypesAndWords(FindQuestionsByTypesAndWordsRequest request);

    /**
     * 通过单词列表和题型列表快速查询单词题
     * @param request
     * @return
     */
    FindWordsByQuestionIdsAndChapterIdResponse findWordsByQuestionIdsAndChapterId(FindWordsByQuestionIdsAndChapterIdRequest request);

}
