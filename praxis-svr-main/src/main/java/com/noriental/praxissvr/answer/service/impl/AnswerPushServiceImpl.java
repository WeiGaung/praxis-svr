package com.noriental.praxissvr.answer.service.impl;

import com.noriental.imsvr.bean.request.RequestSendMessageByType;
import com.noriental.imsvr.service.ImXiaoMiService;
import com.noriental.lessonsvr.entity.request.LinkResourceWithPublishInfo;
import com.noriental.lessonsvr.entity.request.LongRequest;
import com.noriental.lessonsvr.entity.request.ResourceParam;
import com.noriental.lessonsvr.entity.request.StudentLessonRequest;
import com.noriental.lessonsvr.entity.vo.LinkPublishResource;
import com.noriental.lessonsvr.entity.vo.LinkResourceStudentVo;
import com.noriental.lessonsvr.rservice.LessonService;
import com.noriental.praxissvr.answer.bean.ExerciseTypeEnum;
import com.noriental.praxissvr.answer.request.UpdateFinshCorrectRequest;
import com.noriental.praxissvr.answer.service.AnswerPushService;
import com.noriental.praxissvr.common.StuAnswerConstant;
import com.noriental.publicsvr.bean.CommonLongIdRequest;
import com.noriental.publicsvr.bean.Subject;
import com.noriental.publicsvr.service.SubjectService;
import com.noriental.usersvr.bean.group.domain.Klass;
import com.noriental.usersvr.bean.request.KlassInfoRequest;
import com.noriental.usersvr.service.okuser.KlassService;
import com.noriental.utils.json.JsonUtil;
import com.noriental.utils.redis.RedisUtil;
import com.noriental.validate.bean.CommonDes;
import com.noriental.validate.bean.CommonResponse;
import com.noriental.validate.exception.BizLayerException;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.invoke.MethodHandles;
import java.util.*;

import static com.noriental.praxissvr.exception.PraxisErrorCode.ANSWER_PUBLISH_RESOURCE_ID_NOT_EXIST;

@SuppressWarnings("SpringJavaAutowiringInspection")
@Service(value = "answerPushService")
public class AnswerPushServiceImpl implements AnswerPushService {
    @Resource
    private LessonService lessonService;
    @Resource
    private KlassService klassService;
    @Resource
    private ImXiaoMiService imXiaoMiService;
    @Resource
    private RedisUtil redisUtil;
    @Autowired
    private SubjectService subjectService;
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
   /* @Value("${push.key.rom}")
    String push_key_rom;*/
    private final static String UPDATE_CORRECT_MULIT_PUBLISH_VIEW = "UPDATE_CORRECT_MULIT_PUBLISH_VIEW";
    private final static String UPDATE_CORRECT_MULIT_PUBLISH_RESOURCE_VIEW =
            "UPDATE_CORRECT_MULIT_PUBLISH_RESOURCE_VIEW";

    @Override
    public CommonDes updateFinshCorrect(UpdateFinshCorrectRequest req) {
        if (req.getExerciseTypeEnum() == ExerciseTypeEnum.YUXI || req.getExerciseTypeEnum() == ExerciseTypeEnum
                .ZUO_YE) {
            String sourceName = StuAnswerConstant.ExerciseSource.getExerciseSourceNameByCode(String.valueOf(req
                    .getExerciseTypeEnum().getCode()));
            String appName = req.getExerciseTypeEnum() == ExerciseTypeEnum.YUXI ? "com.okay.preview" : "com.okay.hw";
            //多题集
            //查询题集个数
            //存储批改状态，资源包批改状态，每个题集批改状态
            //如果多个题集都被批改，批改数量达到题集总数量，且至少包含一个主观题，就发送通知
            //获取题集发布信息
            LinkResourceWithPublishInfo linkResourceWithPublishInfo = getLinkResourceWithPublishInfo(req.getLinkId());
            Long publishId = linkResourceWithPublishInfo.getResPackagePublish().getId();
            String packageDesc = linkResourceWithPublishInfo.getResPackagePublish().getPackageDesc();
            Long subjectId = linkResourceWithPublishInfo.getLinkPublishResource().getSubjectId();
            int exerciseCount = getExerciseCount(publishId);
            List<UpdateFinshCorrectRequest> updateFinshCorrectRequests = getCorrectStatus(publishId, req);
            boolean isNotify = isNotify(updateFinshCorrectRequests, exerciseCount, publishId);
            if (isNotify) {
                //完成状态
                int tab = 1;
                StudentLessonRequest req1 = new StudentLessonRequest();
                req1.setPublishId(publishId);
                req1.setStudentId(req.getStudentId());
                CommonResponse<LinkResourceStudentVo> linkResourceStudentVoCommonResponse = lessonService
                        .fetchStudentLessonDetail(req1);
                if (linkResourceStudentVoCommonResponse.success()) {
                    LinkResourceStudentVo data = linkResourceStudentVoCommonResponse.getData();
                    if (data != null && CollectionUtils.isNotEmpty(data.getStudents())) {
                        boolean complete = data.getStudents().get(0).isComplete();
                        if (complete) tab = 2;
                    }
                    KlassInfoRequest klassInfoRequest = new KlassInfoRequest();
                    klassInfoRequest.setClassId(data.getClassId());
                    CommonResponse<Klass> result = klassService.findFromRedis(klassInfoRequest);
                    logger.info("updateFinshCorrect 根据班级ID:{}查询学生当前学年json数据:{}", data.getClassId(), JsonUtil.obj2Json
                            (result.getData()));
                    //如果学生跨学年，上学年的任务全部进入已完成列表
                    if (null != result && null != result.getData() && CollectionUtils.isNotEmpty(data
                            .getLinkResResourceVos()) && !(data.getLinkResResourceVos().get(0).getYear() == result
                            .getData().getYear())) {
                        tab = 2;
                    }
                } else {
                    logger.warn("has no  data :" + JsonUtil.obj2Json(req1));
                }
                CommonLongIdRequest subjectReq = new CommonLongIdRequest();
                subjectReq.setLongTypeId(subjectId);
                CommonResponse<Subject> subject = subjectService.findById(subjectReq);
                String title = sourceName;
                String content = "你的" + subject.getData().getName() + sourceName + "【" + packageDesc + "】已被老师批改，快来看看吧~";
                Map<String, String> extras = new HashMap<>(1);
                extras.put("on", "1");
                extras.put("a", "2002");
                extras.put("c", "1");
                extras.put("p", appName);
                extras.put("t", "1");
                extras.put("type", "2");
                extras.put("sid", String.valueOf(subjectId));
                extras.put("pid", String.valueOf(publishId));
                extras.put("name", packageDesc);
                extras.put("tab", String.valueOf(tab));
                return push(req.getStudentId(), extras, title, content);
            }
        }
        return new CommonDes();
    }

    /***
     * 缓存每次调用push的发布信息和学生信息
     * @param publishId
     * @param req
     * @return
     */
    private List<UpdateFinshCorrectRequest> getCorrectStatus(Long publishId, UpdateFinshCorrectRequest req) {
        String keyP = UPDATE_CORRECT_MULIT_PUBLISH_VIEW + publishId + "_" + req.getStudentId();
        String keyPR = UPDATE_CORRECT_MULIT_PUBLISH_RESOURCE_VIEW + req.getLinkId() + "_" + req.getStudentId();
        redisUtil.viewSet(keyPR, req, 120 * 24 * 3600);
        redisUtil.sadd(keyP, keyPR);
        redisUtil.expire(keyP, 120 * 24 * 3600);
        Set<String> smembers = redisUtil.smembers(keyP);
        List<UpdateFinshCorrectRequest> updateFinshCorrectRequests = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(smembers)) {
            Set resultSet = redisUtil.viewGetSet(smembers, UpdateFinshCorrectRequest.class);
            if (CollectionUtils.isNotEmpty(resultSet)) {
                updateFinshCorrectRequests = new ArrayList<>(resultSet);
            }
        }
        logger.info("saved updateFinshCorrectRequests:" + JsonUtil.obj2Json(updateFinshCorrectRequests));
        return updateFinshCorrectRequests;
    }

    private int getExerciseCount(Long publishId) {
        CommonResponse<List<LinkPublishResource>> listCommonResponse = lessonService.fetchResourceByPublishId(new
                LongRequest(publishId));
        int exerciseCount = 0;
        if (listCommonResponse.success() && CollectionUtils.isNotEmpty(listCommonResponse.getData())) {
            for (LinkPublishResource linkPublishResource : listCommonResponse.getData()) {
                if (linkPublishResource.getResourceType().equals(2)) {
                    exerciseCount++;
                }
            }
        }
        logger.info("exerciseCount:" + exerciseCount);
        return exerciseCount;
    }

    private LinkResourceWithPublishInfo getLinkResourceWithPublishInfo(Long resourcePublishId) {
        ResourceParam req1 = new ResourceParam(resourcePublishId);
        CommonResponse<LinkResourceWithPublishInfo> response = lessonService.fetchResourcePublicByLinkId(req1);
        if (response.success() && response.getData() != null) {
            return response.getData();
        } else {
            throw new BizLayerException("lesson:" + response.getCode() + "," + response.getMessage() + ";",
                    ANSWER_PUBLISH_RESOURCE_ID_NOT_EXIST);
        }
    }

    private CommonDes push(Long studentId, Map<String, String> extras, String title, String content) {
        RequestSendMessageByType request=new RequestSendMessageByType();
        //PushRequest request = new PushRequest();
        request.setIds(Collections.singletonList(studentId+""));
        request.setExtras(extras);
        request.setTitle(title);
        request.setContent(content);
        //request.setApps(Collections.singletonList(push_key_rom));
        //request.setType(SendType.MESSAGE);
        request.setPublishType("批改");
        logger.info("updateFinshCorrect push msg:" + JsonUtil.obj2Json(request));
        return imXiaoMiService.sendImMessageByType(request);
    }

    public boolean isNotify(List<UpdateFinshCorrectRequest> updateFinshCorrectRequests, int exerciseCount, Long
            publishId) {
        boolean isNotify = false;
        if (CollectionUtils.isNotEmpty(updateFinshCorrectRequests) && updateFinshCorrectRequests.size() ==
                exerciseCount) {
            for (UpdateFinshCorrectRequest request : updateFinshCorrectRequests) {
                if (request.isContain() && request.isTeacher()) {
                    isNotify = true;
                    cleanRedisData(request,publishId);
                }
            }
        }
        logger.info("isNotify:" + isNotify);
        return isNotify;
    }


    /***
     * 添加redis缓存清除操作，防止无用的缓存占用时间过长
     * @param request
     * @param publishId
     */
    private void cleanRedisData(UpdateFinshCorrectRequest request, Long publishId) {
        String keyP = UPDATE_CORRECT_MULIT_PUBLISH_VIEW + publishId + "_" + request.getStudentId();
        String keyPR = UPDATE_CORRECT_MULIT_PUBLISH_RESOURCE_VIEW + request.getLinkId() + "_" + request.getStudentId();
        redisUtil.del(keyP);
        redisUtil.del(keyPR);
    }
}
