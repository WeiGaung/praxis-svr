package com.noriental.praxissvr.resourcegroup.serviceImpl;

import com.noriental.log.TraceKeyHolder;
import com.noriental.praxissvr.exception.PraxisErrorCode;
import com.noriental.praxissvr.question.bean.EntityQuestion;
import com.noriental.praxissvr.question.mapper.EntityQuestionMapper;
import com.noriental.praxissvr.question.utils.Constants;
import com.noriental.praxissvr.resourcegroup.bean.ResourceGroup;
import com.noriental.praxissvr.resourcegroup.common.Sha1Util;
import com.noriental.praxissvr.resourcegroup.mapper.ResourceGroupMapper;
import com.noriental.praxissvr.resourcegroup.request.*;
import com.noriental.praxissvr.resourcegroup.response.ResGroupCreateResponse;
import com.noriental.praxissvr.resourcegroup.response.ResGroupGetListResponse;
import com.noriental.praxissvr.resourcegroup.service.ResourceGroupService;
import com.noriental.solr.bean.doc.QuestionDocument;
import com.noriental.solr.common.msg.SolrIndexReqMsg;
import com.noriental.solr.common.search.QueryMap;
import com.noriental.validate.bean.CommonDes;
import com.noriental.validate.exception.BizLayerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kate on 2016/11/18.
 * 资源组创建、删除、更新、获取列表、转移业务处理
 */
@Service("resourceGroupService")
@Deprecated
public class ResourceGroupServiceImpl implements ResourceGroupService {

    private static final Logger logger = LoggerFactory.getLogger(ResourceGroupServiceImpl.class);
    @Resource
    private ResourceGroupMapper resourceGroupMapper;
    @Resource
    private RabbitTemplate solrUploadQuestionRabbitTemplate;
    @Resource
    private EntityQuestionMapper entityQuestionMapper;

    /***
     * 根据组名和用户ID查询组是否已创建
     *
     * @param request
     * @return
     * @throws BizLayerException
     */
    public int isGroupExit(ResGroupCreateRequest request) throws BizLayerException {
        int num = resourceGroupMapper.isGroupExit(request);
        return num;
    }

    /***
     * 在创建前先查看组名是否已存在
     *
     * @param request
     * @return
     * @throws BizLayerException
     */
    @Override
    public ResGroupCreateResponse createGroup(ResGroupCreateRequest request) throws BizLayerException {
        int num = isGroupExit(request);
        if (num != 0) {
            throw new BizLayerException("", PraxisErrorCode.RESOURCE_GROUP_NAME_EXIT);
        }
        resourceGroupMapper.createResGroup(request);
        Long id = request.getId();
        String name = request.getName();
        ResGroupCreateResponse resGroupCreateResponse = new ResGroupCreateResponse();
        resGroupCreateResponse.setId(id);
        resGroupCreateResponse.setName(name);
        return resGroupCreateResponse;
    }

    /***
     * 做sha1验证，验证成功继续操作下面的查询步骤
     *
     * @param request
     * @return
     * @throws BizLayerException
     */
    @Override
    public ResGroupGetListResponse getGroupList(ResGroupGetListRequest request) throws BizLayerException {
        boolean flag = validateSha1(request.getTimestamp(), request.getSecret());
        if (flag) {
            List<ResourceGroup> ls = resourceGroupMapper.getGroupList(request);
            ResGroupGetListResponse resGroupGetListResponse = new ResGroupGetListResponse();
            resGroupGetListResponse.setGroup_list(ls);
            List<ResourceGroup> defaultList = resourceGroupMapper.getDefaultGroupList(request);
            resGroupGetListResponse.setDefault_num(defaultList.size() > 0 ? defaultList.get(0).getNum() : 0);
            return resGroupGetListResponse;
        } else {
            throw new BizLayerException("", PraxisErrorCode.SHA1_EQUAL_EXCEPTION);
        }
    }

    @Override
    public CommonDes updateGroup(ResGroupUpdateRequest request) throws BizLayerException {
        sendUpdateDataToSolr(request);
        ResGroupCreateRequest param = new ResGroupCreateRequest();
        param.setName(request.getName());
        param.setSystemId(request.getSystemId());
        int num = isGroupExit(param);
        if (num != 0) {
            throw new BizLayerException("", PraxisErrorCode.RESOURCE_GROUP_NAME_EXIT);
        }
        resourceGroupMapper.updateResGroup(request);
        return new CommonDes();
    }

    /***
     * 此处操作涉及到事务，1、根据ID和systemId字段信息删除entity_group表对应的信息
     * 2、更新entity_question对应的字段question_group信息
     *
     * @param request
     * @return
     * @throws BizLayerException
     */
    @Override
    public CommonDes deleteGroup(ResGroupDeleteRequest request) throws BizLayerException {
        sendGroupDataListToSolr(request);
        resourceGroupMapper.updateEntityQuestion(request);
        resourceGroupMapper.deleteResGroup(request);
        return new CommonDes();
    }

    @Override
    public CommonDes transferGroup(ResGroupTransferRequest request) throws BizLayerException {
        //转移组后添加solr更新
        sendGroupDataToSolr(request);
        resourceGroupMapper.updateResGroupId(request);
        return new CommonDes();
    }

    /***
     * 更新组名后，修改solr对应的组信息
     * @param request
     * @throws BizLayerException
     */
    private void sendUpdateDataToSolr(ResGroupUpdateRequest request)throws BizLayerException {
        List<EntityQuestion> resultList=entityQuestionMapper.findQuestinByUploadIdAndGroupId(request.getSystemId(),request.getId());
        if (null!=resultList&&resultList.size()>0){
            Map<String, Object> bodyMap = new HashMap<>();
            List<Map> docList = new ArrayList<>();
            for (EntityQuestion data:resultList) {
                Map<String, Object> currentMap = new HashMap<>();
                currentMap.put("id",data.getId());
                currentMap.put("questionGroup",request.getId());
                currentMap.put("questionGroupName",request.getName());
                docList.add(currentMap);
            }
            bodyMap.put(QueryMap.KEY_DOC_LIST, docList);
            bodyMap.put(QueryMap._DOC_CLASS_NAME, QuestionDocument.class.getName());
            SolrIndexReqMsg msg = new SolrIndexReqMsg(bodyMap);
            msg.setRequestId(TraceKeyHolder.getTraceKey());
            solrUploadQuestionRabbitTemplate.convertAndSend(msg);
        }

    }



    /***
     * 单条习题组转移更新solr组信息
     * @param request
     * @throws BizLayerException
     */
    private void sendGroupDataToSolr(ResGroupTransferRequest request) throws BizLayerException {
        //根据新的组ID查询组名称
        ResourceGroup entity= resourceGroupMapper.findGroupEntity(request);
        if (null!=entity){
            Map<String, Object> bodyMap = new HashMap<>();
            bodyMap.put("id",request.getQuestionId());
            bodyMap.put("questionGroup",request.getGroupId());
            bodyMap.put("questionGroupName",entity.getName());
            bodyMap.put(QueryMap._DOC_CLASS_NAME, QuestionDocument.class.getName());
            SolrIndexReqMsg msg = new SolrIndexReqMsg(bodyMap);
            msg.setRequestId(TraceKeyHolder.getTraceKey());
            solrUploadQuestionRabbitTemplate.convertAndSend(msg);
        }


    }

    /***
     * 组删除，组下面所有的习题更新solr组信息
     * @param request
     * @throws BizLayerException
     */
    private void sendGroupDataListToSolr(ResGroupDeleteRequest request)throws BizLayerException{
        List<EntityQuestion> resultList=entityQuestionMapper.findQuestinByUploadIdAndGroupId(request.getSystemId(),request.getId());
        if (null!=resultList&&resultList.size()>0){
            Map<String, Object> bodyMap = new HashMap<>();
            List<Map> docList = new ArrayList<Map>();
            for (EntityQuestion data:resultList) {
                Map<String, Object> currentMap = new HashMap<>();
                currentMap.put("id",data.getId());
                currentMap.put("questionGroup",Constants.GROUP_ID);
                currentMap.put("questionGroupName",Constants.GROUP_NAME);
                docList.add(currentMap);
            }
            bodyMap.put(QueryMap.KEY_DOC_LIST, docList);
            bodyMap.put(QueryMap._DOC_CLASS_NAME, QuestionDocument.class.getName());
            SolrIndexReqMsg msg = new SolrIndexReqMsg(bodyMap);
            msg.setRequestId(TraceKeyHolder.getTraceKey());
            solrUploadQuestionRabbitTemplate.convertAndSend(msg);
        }

    }


    private boolean validateSha1(String timeStamp, String secret) throws BizLayerException {
        String sha1key = "";
        try {
            sha1key = Sha1Util.SHA1(timeStamp);
        } catch (Exception e) {
            logger.error("习题组sha1加密失败", e);
            throw new BizLayerException("", PraxisErrorCode.SHA1_CODE_EXCEPTION);
        }
        if (secret.equals(sha1key)) {
            return true;
        }
        return false;
    }
}
