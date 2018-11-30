package com.noriental.praxissvr.statis.service.impl;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.noriental.praxissvr.exception.PraxisErrorCode;
import com.noriental.praxissvr.statis.bean.ClassWrongQuesCount;
import com.noriental.praxissvr.statis.bean.DataWrongQuesVo;
import com.noriental.praxissvr.statis.bean.KnowledgeVo;
import com.noriental.praxissvr.statis.request.FindClassWrongQuesCountRequest;
import com.noriental.praxissvr.statis.request.FindKnowledgeListRequest;
import com.noriental.praxissvr.statis.service.DataWrongQuesService;
import com.noriental.praxissvr.statis.statismapper.TeacherWebWrongQuesMapper;
import com.noriental.usersvr.bean.group.domain.Klass;
import com.noriental.usersvr.bean.request.KlassRequest;
import com.noriental.usersvr.bean.request.UserBaseMapRequest;
import com.noriental.usersvr.bean.response.StudentQueryResponse;
import com.noriental.usersvr.bean.user.domain.Student;
import com.noriental.usersvr.service.okuser.KlassService;
import com.noriental.usersvr.service.okuser.UserService;
import com.noriental.validate.bean.CommonResponse;
import com.noriental.validate.exception.BizLayerException;
import com.sumory.mybatis.pagination.result.PageResult;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service(value="dataWrongQuesService")
public class DataWrongQuesServiceImpl implements DataWrongQuesService {
    @Resource
    private TeacherWebWrongQuesMapper teacherWebWrongQuesMapper;
    @Resource
    private UserService userService;
    @Resource
    private KlassService klassService;
    private static final Logger logger = LoggerFactory.getLogger(DataWrongQuesServiceImpl.class);
    @Override
    public CommonResponse<PageResult<ClassWrongQuesCount>> findClassWrongQuesCount(FindClassWrongQuesCountRequest
                                                                                               request) {
        DataWrongQuesVo vo=getDataWrongQuesVo(request);
        // 知识点id 知识点级别 知识点名称 知识点父id     entity_data_wrong_ques_knom 班级学科错题知识点展示表
        PageList<ClassWrongQuesCount> classWrongQuesCount = teacherWebWrongQuesMapper.findClassWrongQuesCount(vo, new
                PageBounds(request.getPage(), request.getLimit()));
        return CommonResponse.success(classWrongQuesCount.toPageResult());
    }

    @Override
    public CommonResponse<List<KnowledgeVo>> findKnowledgeList(FindKnowledgeListRequest request) {
        // 查询班级科目下错误知识点信息   entity_data_ques_know 班级科目错题知识点展示表
        return CommonResponse.success(teacherWebWrongQuesMapper.findKnowledgeList(request.getClassIdList(), request
                .getSubjectId(), request.getKnowledgeLevel(), request.getParKnowledgeId()));
    }

    @Override
    public CommonResponse<List<String>> findErrorQuesStudentNames(FindClassWrongQuesCountRequest request) {
        // 教师空间错题查询     查询某题的错题的所有学生名字
        if(null==request.getQuestionId()){
            throw new BizLayerException("",PraxisErrorCode.ANSWER_PARAMETER_NULL);
        }
        DataWrongQuesVo vo=getDataWrongQuesVo(request);
        vo.setQuestion_id(request.getQuestionId());
        // link_data_wrong_knowledge 题目知识点关联表
        List<Long> studentIds=teacherWebWrongQuesMapper.findStudentIds(vo);
        Map paraMap=new HashMap(1);
        paraMap.put("systemId",studentIds);
        UserBaseMapRequest userBaseMapRequest=new UserBaseMapRequest();
        userBaseMapRequest.setParams(paraMap);
        CommonResponse<StudentQueryResponse> response= userService.findStudent(userBaseMapRequest);
        StudentQueryResponse responseData=  response.getData();
        List<String> studentNames=new ArrayList<>();
        if (null!=responseData){
            List<Student> studentList=responseData.getList();
           for (Student entity:studentList){
               studentNames.add(entity.getName());
           }
        }
        CommonResponse<List<String>> responseResult=new CommonResponse<>();
        responseResult.setData(studentNames);
        return responseResult;
    }


    private DataWrongQuesVo getDataWrongQuesVo(FindClassWrongQuesCountRequest request) {

        List<Long> schoolIds=new ArrayList<>();
        KlassRequest klassRequest=new KlassRequest();
        List<Klass> klassList=new ArrayList<>();
        for (Long id:request.getClassIdList()){
            Klass klass=new Klass();
            klass.setId(id);
            klassList.add(klass);
        }
        klassRequest.setClassList(klassList);
        CommonResponse<List<Klass>> res=klassService.findFromRedisWithClassInfo(klassRequest);
        for (Klass klass:res.getData()){
            if (null!=klass){
                if (!schoolIds.contains(klass.getOrgId())){
                    schoolIds.add(klass.getOrgId());
                }
            }
        }
        if (CollectionUtils.isEmpty(schoolIds)) {
            logger.error("教师空间错题本根据classID:{}获取学校ID失败。");
            throw new BizLayerException("", PraxisErrorCode.QUERY_SCHOOLD_ID_ERROR);
        }
        Integer knowledgeLevel = request.getKnowledgeLevel() == null ? 3 : request.getKnowledgeLevel();
        List<Long> knowIds = request.getKnowledgeId() != null ? Collections.singletonList(request.getKnowledgeId()) :
                null;
        DataWrongQuesVo vo = new DataWrongQuesVo(knowIds, request.getClassIdList(), knowledgeLevel, request
                .getWrongQuesSortType().getSort(), request.getQuesTypeId(), request.getDifficulty(), request
                .getSubjectId(), schoolIds);
        vo.setSources(request.getWrongQuesSource() == FindClassWrongQuesCountRequest.WrongQuesSourceEnum.ALL ? null :
                request.getWrongQuesSource().getSource());//来源
        if (request.getWrongQuesSource() == FindClassWrongQuesCountRequest.WrongQuesSourceEnum.OWN) {
            vo.setTeacherId(request.getTeacherId());
        }
        return vo;
    }


}
