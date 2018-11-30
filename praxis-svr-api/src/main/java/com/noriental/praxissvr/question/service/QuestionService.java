package com.noriental.praxissvr.question.service;

import com.noriental.praxissvr.answer.request.FlowTurnCorrectRequest;
import com.noriental.praxissvr.question.request.*;
import com.noriental.praxissvr.question.response.*;
import com.noriental.validate.bean.CommonDes;
import com.noriental.validate.bean.CommonResponse;
import com.noriental.validate.bean.LongRequest;
import com.noriental.validate.exception.BizLayerException;

import java.util.List;

/**
 * @author chenlihua
 * @date 2015/12/22
 * @time 11:25
 */
public interface QuestionService {


    /**
     * 更新题目的自定义目录
     * @param request
     * @return
     */
    CommonResult updateCustomerDiretory(UpCusDirRequest request) throws BizLayerException;

    /*
        收藏题目
     */
    CommonResult collectionQuestion(CollectionQuestionRequest request) throws BizLayerException;

    /**
     * 批量收藏题目
     * @param request
     * @return
     * @throws BizLayerException
     */
    CommonResult collectionQuestions(CollectionQuestionsRequest request) throws BizLayerException;

    /**
     * 根据条件查询习题列表
     *
     * @param request req
     * @return resp
     * teacher-web1.8后将废弃次方法
     */

    FindQuestionsResponse findQuestions(FindQuestionsRequest request) throws BizLayerException;

    /**
     * 根据试卷id获取试卷信息，包含试题下的试题
     *
     * @param request req
     * @return FindPaperByPaperIdRsp
     */
    @Deprecated
    FindPaperByPaperIdResponse findPaperByPaperId(FindPaperByPaperIdRequest request) throws BizLayerException;

    /**
     * 根据试题id获取试题基本信息
     *
     * @param request request
     * @return FindQuestionsByIdsRsp
     */
    @Deprecated
    FindQuestionsByIdsResponse findQuestionsByIds(FindQuestionsByIdsRequest request) throws BizLayerException;

    /**
     * 输入复合题或者单题，返回题目信息（recursive=true 复合题包含子题）
     *
     * @param request
     * @return
     * @throws BizLayerException
     */
    FindQuestionsByIdsResponse findQuestionsByIdsFast(FindQuestionsByIdsRequest request) throws BizLayerException;

    /**
     * 根据试题id获取试题基本信息
     *
     * @param request request
     * @return FindQuestionByIdRsp
     */
    FindQuestionByIdResponse findQuestionById(FindQuestionByIdRequest request) throws BizLayerException;

    /**
     * 通过章节或知识点、科目、题型、难度 批量查询题目数量
     * 使用solr的统计进行查询
     * 数据检索的接口已经迁移到了付鹏那，此接口作废！！！
     *
     * @param request request
     * @return resp
     */
    @Deprecated
    FindFacetQuestionCountResponse findFacetQuestionCount1(FindFacetQuestionCount1Request request) throws BizLayerException;

    /**
     * 查询题集的子题ID
     *
     * @param request req
     * @return resp
     */
    FindLeafQuesIdsByParentIdsResponse findLeafQuesIdsByParentIds(FindLeafQuesIdsByParentIdsRequest request) throws BizLayerException;

    FindParentIdLeafQuesIdsMapResponse findParentIdLeafQuesIdsMap(FindLeafQuesIdsByParentIdsRequest request) throws BizLayerException;

    /**
     * 找出题目ids中的主题&&新题的题目ids
     *
     * @param request req
     * @return resp
     */
    CheckoutNewFormatAndTrunkResponse checkoutNewFormatAndTrunk(CheckoutNewFormatAndTrunkRequest request) throws BizLayerException;

    /**
     * 根据试题ids的list集合获取返回给pad的试题
     *
     * @param request request
     */
    FindPadQuestionListByQuestionIdsResponse findPadQuestionListByQuestionIds(FindPadQuestionListByQuestionIdsRequest request) throws BizLayerException;

    /**
     * 查询题目的知识点
     *
     * @param request req
     * @return resp
     */
    FindQuestionKnowledgeByIdsResponse findQuestionKnowledgeByIds(FindQuestionKnowledgeByIdsRequest request) throws BizLayerException;


    /**
     * 创建题目
     * @param request req
     * @return resp
     * @throws BizLayerException e
     * @since teacher-web-1.8.0
     */
    CommonResult createQuestion(UploadQuestionRequest request) throws BizLayerException;

    /**
     * 根据ID查询习题
     *
     * @param request req
     * @return resp
     * @throws BizLayerException e
     * @since teacher-web-1.8.0
     */
    GetQuestionByIdResponse findQuestionById(LongRequest request) throws BizLayerException;

    /**
     * 删除习题
     *
     * @param request req
     * @return resp
     * @throws BizLayerException e
     * @since teacher-web-1.8.0
     */
    CommonDes deleteQuestion(DeleteQuestionRequest request) throws BizLayerException;

    /**
     * 更新题目用于草稿
     * @param request req
     * @return
     * @throws BizLayerException
     */
    CommonResult updateQuestion_sketch(UpdateQuestionRequest request) throws BizLayerException;


    /**
     * 更新习题
     *
     * @param request req
     * @return resp
     * @throws BizLayerException e
     * @since teacher-web-1.8.0
     */
    CommonResult updateQuestion(UpdateQuestionRequest request) throws BizLayerException;

    /**
     * 教师空间查询我的题目
     *该接口入参 分页参数需要注意，是取的偏移量，不是取第几页，多少条。
     *
     * @param request req
     * @return resp
     * @throws BizLayerException e
     * @since teacher-web-1.8.0
     */
    FindQuestionsResponse findMine(FindMyQuestionRequest request) throws BizLayerException;

    /***
     * 音频转换完成后提供回写音频信息的API接口
     * @since teacher-web-1.8.0
     * @param request req
     * @return resp
     * @throws BizLayerException e
     */
    CommonDes updateAudioInfoQuestion(UpdateAudioInfoQuestionRequest request) throws BizLayerException;


    /**
     * 智慧学习补全的题目
     *
     * @param request req
     * @return resp
     */
    CommonResponse<List<Long>> findComplement(FindComplementQuestionRequest request) throws BizLayerException;


    /**
     * 批量查询题目的基本信息
     */
    BatchQueryQuestionsResponse batchQueryQuestionsByIds(BatchQueryQuestionsRequest request) throws BizLayerException;


    /**
     * 批量更新题目的状态
     * @param request req
     * @return resp
     * @throws BizLayerException e
     */
    CommonDes updateQuestionState(UpdateQuestionStateRequest request) throws BizLayerException;

    /**
     * 根据用户ID和自定义目录ID查询自定义目录下面是否存在资源
     * @return
     * @throws BizLayerException
     */
    QueryCusDirResponse queryCustomQuestionResourceByCusDirId(QueryCusDirRequest request) throws BizLayerException;

    /**
     * 更新自定义目录
     * @param request
     * @return
     * @throws BizLayerException
     */
    CommonDes updateLinkCustomDirectoryBySysIdAndCusDirId(UpdateCusDirRequest request) throws BizLayerException;

    /**
     * 删除自定义目录
     * @param request
     * @return
     * @throws BizLayerException
     */
    CommonDes deleteCustomDiritory(DeleteCusDirRequest request) throws BizLayerException;

    /**
     * 插入自定义目录
     * @param request re
     * @return
     * @throws BizLayerException
     */
    CommonDes insertLinkCusQuesResou(InsertCusDirRequest request) throws BizLayerException;


    CommonResult isMyCollection(FindMyCollectionRequest request) throws BizLayerException;

    /**
     * 更新题目中的图片
     * @return
     * @throws BizLayerException
     */
     CommonDes updateQuestionHtmlImg(UpdateQuestionHtmlImgRequest request) throws BizLayerException;

    /**
     * 查询题目信息，针对复合题下的子题答案问题。--重要：目前只支持单题查询
     * @param request
     * @return
     */
     FindQuestionsResponse findQuestionsAllFiled(FindQuestionsRequest request);

    /**
     * 查询自定义目录题目关系表
     * @param request
     * @return
     */
    List<Long> findLinkCustomQuestionResource(FlowTurnCorrectRequest request);

}
