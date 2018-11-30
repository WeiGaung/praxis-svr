package com.noriental.praxissvr.question.service.impl;

import com.noriental.praxissvr.question.bean.CreateQuestionFeedback;
import com.noriental.praxissvr.question.bean.QuestionFeedback;
import com.noriental.praxissvr.question.mapper.QuestionFeedbackMapper;
import com.noriental.praxissvr.question.request.CreateQuestionFeedbackRequest;
import com.noriental.praxissvr.question.request.FindQuestionByIdRequest;
import com.noriental.praxissvr.question.request.FindQuestionFeedbacksRequest;
import com.noriental.praxissvr.question.response.FindQuestionByIdResponse;
import com.noriental.praxissvr.question.response.FindQuestionFeedBackResp;
import com.noriental.praxissvr.question.service.QuestionFeedbackService;
import com.noriental.praxissvr.question.service.QuestionService;
import com.noriental.praxissvr.question.utils.JsonUtils;
import com.noriental.publicsvr.bean.CommonLongIdRequest;
import com.noriental.publicsvr.bean.Subject;
import com.noriental.publicsvr.service.SubjectService;
import com.noriental.resourcesvr.clip.service.ClipService;
import com.noriental.resourcesvr.clip.vo.ClipVo;
import com.noriental.resourcesvr.common.request.IdRequest;
import com.noriental.securitysvr.bean.Admin;
import com.noriental.securitysvr.service.AdminService;
import com.noriental.usersvr.bean.request.UserBaseRequest;
import com.noriental.usersvr.bean.user.domain.User;
import com.noriental.usersvr.service.okuser.UserService;
import com.noriental.validate.bean.CommonDes;
import com.noriental.validate.bean.CommonResponse;
import com.noriental.validate.exception.BizLayerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.invoke.MethodHandles;
import java.util.List;

import static com.noriental.praxissvr.exception.PraxisErrorCode.PRAXIS_QUESTION_NOT_FOUND;

@Service("questionFeedbackService")
public class QuestionFeedbackServiceImpl implements QuestionFeedbackService {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Resource
    private QuestionFeedbackMapper questionFeedbackMapper;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private SubjectService subjectService;
    @Autowired
    private UserService userService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private ClipService clipService;

    /**
     * 创建问题反馈(结果)
     *
     * @param request
     * @return
     * @throws BizLayerException
     */
    @Override
    public CommonDes createQuestionFeedback(CreateQuestionFeedbackRequest request) throws BizLayerException {
        CreateQuestionFeedback createQuestionFeedback = new CreateQuestionFeedback();
        BeanUtils.copyProperties(request,createQuestionFeedback);
        createQuestionFeedback.setError_types(getError_types(request.getError_type()));

        UserBaseRequest userReq = new UserBaseRequest();
        userReq.setSystemId(request.getSubmit_by());
        //获取到pad登陆人用户信息
        CommonResponse<User> userBySystemId = userService.findUserBySystemId(userReq);
        if(userBySystemId.success()){
            createQuestionFeedback.setSubmit_name(userBySystemId.getData().getName());
        }


        FindQuestionByIdRequest request1 = new FindQuestionByIdRequest();
        request1.setQuestionId(request.getQuestion_id());
        Long systemId = new Long(0);
        //判断资源类型 1,题目;2,微课;3,音频;4,视频;5,课件;6,教案;7,导学案;8,文档;9,图片;10,拓展资源;
        if(isSubject(request.getQues_resource_type())){
            //1,题目:获取问题信息
            FindQuestionByIdResponse questionById = questionService.findQuestionById(request1);
            if(questionById.success()){
                //题目科目
                Long subjectId = questionById.getQuestion().getSubjectId();
                createQuestionFeedback.setSubject_id(subjectId);
                if(subjectId!=null){
                    CommonLongIdRequest req = new CommonLongIdRequest();
                    req.setLongTypeId(subjectId);
                    CommonResponse<Subject> byId = subjectService.findById(req);
                    if(byId.success()){
                        long stageId = byId.getData().getStageId();
                        createQuestionFeedback.setStage_id(stageId);
                    }
                }
                Long uploadId = questionById.getQuestion().getUploadId();
                systemId = uploadId;
                Integer uploadSrc = questionById.getQuestion().getUploadSrc();
                if(/*uploadSrc!=null && */uploadId!=null){
                    if (uploadSrc!=null && uploadSrc.equals(1)){
                        Admin byId = adminService.findById(uploadId);
                        if(byId!=null){
                            createQuestionFeedback.setQues_upload_name(byId.getName());
                            createQuestionFeedback.setQues_upload_id(byId.getId());
                        }
                    }else {
                        UserBaseRequest request2 = new UserBaseRequest();
                        request2.setSystemId(uploadId);
                        CommonResponse<User> userBySystemId1 = userService.findUserBySystemId(request2);
                        if(userBySystemId1.success()){
                            createQuestionFeedback.setQues_upload_name(userBySystemId1.getData().getName());
                            createQuestionFeedback.setQues_upload_id(userBySystemId1.getData().getId());
                        }
                    }
                }
            }
        }else {
            //获取 音视频,微课信息
            CommonResponse<ClipVo> commonResponse = clipService.findById(new IdRequest(request.getQuestion_id()));
            if(null == commonResponse.getData()){
                throw new BizLayerException(String.format("[questionId:%s] not exist.", request.getQuestion_id()),
                        PRAXIS_QUESTION_NOT_FOUND);
            }
            createQuestionFeedback.setSubject_id(commonResponse.getData().getSubjectId());
            createQuestionFeedback.setStage_id(commonResponse.getData().getStageId());
            createQuestionFeedback.setQues_upload_name(commonResponse.getData().getUserName());

            systemId = commonResponse.getData().getCreatedBy();
            //这里的createBy是systemId 要获取userId
            UserBaseRequest userBaseRequest = new UserBaseRequest();
            userBaseRequest.setSystemId(commonResponse.getData().getCreatedBy());
            CommonResponse<User> userResp = userService.findUserBySystemId(userBaseRequest);
            if(userResp.success()){
                createQuestionFeedback.setQues_upload_id(userResp.getData().getId());
            }else {
                logger.error("获取音视频信息上传者失败! systemId="+systemId);
                createQuestionFeedback.setQues_upload_id(0L);
            }
        }

        if(!systemId.toString().equals("0")){
            //获取上传人所属学校
            String quesUploadSchool = questionFeedbackMapper.getQuesUploadSchool(systemId);
            createQuestionFeedback.setQues_upload_school(quesUploadSchool);
        }

        questionFeedbackMapper.createQuestionFeedback(handleParam(createQuestionFeedback));
        return new CommonDes();
    }

    @Override
    public FindQuestionFeedBackResp findQuestionFeedbacks(FindQuestionFeedbacksRequest request) throws BizLayerException {
        FindQuestionFeedBackResp resp=new FindQuestionFeedBackResp();
        List<QuestionFeedback> dataList=questionFeedbackMapper.findQuestionFeedbacks(request);
        resp.setDataList(dataList);
        return resp;
    }

    private String getError_types(List<String> errors) {

        StringBuilder s= new StringBuilder();
        for(int i=0;i<errors.size();i++){
            if(i==errors.size()-1){
                s.append(errors.get(i));
            }else {
                s.append(errors.get(i)+",");
            }
        }
        return s.toString();
    }


    /**
     * 判断题目
     * @return
     */
    private Boolean isSubject(Integer type){
        //判断资源类型 1,题目;2,微课;3,音频;4,视频;5,课件;6,教案;7,导学案;8,文档;9,图片;10,拓展资源;
        if(null == type){
            throw new BizLayerException(String.format("[ques_resource_type:%s] is null.", type),
                    PRAXIS_QUESTION_NOT_FOUND);
        }
        if(type.equals(1)){
            return true;
        }else{
            return false;
        }
    }

    private static CreateQuestionFeedback handleParam(CreateQuestionFeedback createQuestionFeedback){
        if(null == createQuestionFeedback.getQues_upload_school()){
            createQuestionFeedback.setQues_upload_school("");
        }
        if(null == createQuestionFeedback.getQues_upload_id()){
            createQuestionFeedback.setQues_upload_id(0L);
        }
        if(null == createQuestionFeedback.getQues_upload_name()){
            createQuestionFeedback.setQues_upload_name("");
        }
        return createQuestionFeedback;
    }


}
