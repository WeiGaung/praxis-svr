package com.noriental.praxissvr.statis.service.impl;

import com.noriental.global.dict.AppType;
import com.noriental.praxissvr.brush.bean.StudentWork;
import com.noriental.praxissvr.exception.PraxisErrorCode;
import com.noriental.praxissvr.statis.bean.AnswAndResultSatis;
import com.noriental.praxissvr.statis.bean.StuWorkStatis;
import com.noriental.praxissvr.statis.dao.StuWorkStatisDao;
import com.noriental.praxissvr.statis.request.FindStuWorkSatisRequest;
import com.noriental.praxissvr.statis.request.StatisLevelVo;
import com.noriental.praxissvr.statis.request.StatisLevelsRequest;
import com.noriental.praxissvr.statis.response.FindStuWorkSatisResponse;
import com.noriental.praxissvr.statis.service.StuWorkStatisService;
import com.noriental.praxissvr.utils.ValidateUtil;
import com.noriental.utils.redis.RedisUtil;
import com.noriental.validate.bean.CommonResponse;
import com.noriental.validate.exception.BizLayerException;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@Service(value = "stuWorkStatisService")
public class StuWorkStatisServiceImpl implements StuWorkStatisService {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @Autowired
    StuWorkStatisDao stuWorkStatisDao;
    @Autowired
    RedisUtil redisUtil;

    @SuppressWarnings("unchecked")
    @Override
    @Deprecated
    public List<StuWorkStatis> findStuWorkSatis(Integer stuType, Long stuId, Long subjectId) {
        throw new BizLayerException("", PraxisErrorCode.PRAXIS_METHOD_DEPRECATED);
    }

    @Override
    public CommonResponse<List<StuWorkStatis>> findStuWorkSatisByIds(StatisLevelsRequest request) {
        List<StatisLevelVo> statisLevels = request.getStatisLevels();

        for(StatisLevelVo vo :statisLevels){
            ValidateUtil.validateThrow(vo);
        }
        return  CommonResponse.success(stuWorkStatisDao.findstuworkstatisByIds(request));
    }

    @Override
    public FindStuWorkSatisResponse findStuWorkSatis(FindStuWorkSatisRequest request) {
        Long stuId = request.getStudentId();
        Long subjectId = request.getSubjectId();
        List<StuWorkStatis> stuWorkStatises = findStuWorkStatis(stuId,subjectId);//todo +subjectId
        List<StuWorkStatis> stuWorkStatisesSubject = getStuWorkStatisBySubject(subjectId, stuWorkStatises);
        FindStuWorkSatisResponse response = new FindStuWorkSatisResponse();
        response.setStuWorkStatisList(stuWorkStatisesSubject);
        return response;
    }

    private List<StuWorkStatis> getStuWorkStatisBySubject(Long subjectId, List<StuWorkStatis> stuWorkStatises) {
        List<StuWorkStatis> stuWorkSubjectStatises = new ArrayList<>();
        for (StuWorkStatis stuWorkStatis : stuWorkStatises) {
            Long subId = stuWorkStatis.getSubjectId();
            if (subjectId.equals(subId)) {
                stuWorkSubjectStatises.add(stuWorkStatis);
            }
        }
        return stuWorkSubjectStatises;
    }

    private List<StuWorkStatis> findStuWorkStatis(long stuId,long subjectId) {
       /* Object obj = redisUtil.get(getWorkStaisRedisKey(stuId));
        if (obj == null || CollectionUtils.isEmpty( (List<StuWorkStatis>)obj ) ) {*/
            List<StuWorkStatis> stuWorkStatis = stuWorkStatisDao.getStuWorkStatis(stuId,subjectId);
           // setWorkStatisCache(stuId, stuWorkStatis);
            return stuWorkStatis;
      /*  }else{
            return  (List<StuWorkStatis>)obj ;
        }*/
    }

    private void setWorkStatisCache(long stuId,List<StuWorkStatis> stuWorkStatis ){
        if(CollectionUtils.isNotEmpty(stuWorkStatis)){
            redisUtil.set(getWorkStaisRedisKey(stuId), stuWorkStatis, AppType.RedisPrefixStudentWork.REDIS_EXPIRE_SECONDS);
        }
    }





    @Override
    public void updateStuWorkSatisList(List<AnswAndResultSatis> countSatises) {
        List<StuWorkStatis> stuWorkStatises = convertWorkStatis(countSatises);
        List<StuWorkStatis> listByEntity = stuWorkStatisDao.findListByEntityList(stuWorkStatises);

        List<StuWorkStatis> listCreate = new ArrayList<>();
        List<StuWorkStatis> listUpdate = new ArrayList<>();
        for(StuWorkStatis s : stuWorkStatises){
            StuWorkStatis s1;
            if((s1= listContainOne(listByEntity,s))!=null){
                s1.setRightNumber(s1.getRightNumber() + s.getRightNumber());
                s1.setAnswerNumber(s1.getAnswerNumber() + s.getAnswerNumber());
                listUpdate.add(s1);
            }else{
                listCreate.add(s);
            }
        }
        if(CollectionUtils.isNotEmpty(listCreate)){
            stuWorkStatisDao.creates(listCreate);
        }
        if(CollectionUtils.isNotEmpty(listUpdate)){
            stuWorkStatisDao.updateByEntityList(listUpdate);
        }
        /*List<StuWorkStatis> stuWorkStatis = stuWorkStatisDao.getStuWorkStatis(countSatises.get(0).getStudentId());
        setWorkStatisCache(countSatises.get(0).getStudentId(), stuWorkStatis);*/
    }

    private StuWorkStatis listContainOne(List<StuWorkStatis> listByEntity, StuWorkStatis s) {
        Integer level = s.getLevel();
        for(StuWorkStatis l : listByEntity){
           if (l.getLevel() != null && level != null && l.getLevel().equals(level)) {
               if (level.equals(StudentWork.WorkLevel.TOPIC) && s.getTopicId().equals(l.getTopicId())) {
                   return l;
               } else if (level.equals(StudentWork.WorkLevel.UNIT)  && s.getUnitId().equals(l.getUnitId())) {
                   return l;
               } else if (level.equals(StudentWork.WorkLevel.MODULE)  && s.getModuleId().equals(l.getModuleId())) {
                   return l;
               }
           }
       }
        return null;
    }

    private List<StuWorkStatis> convertWorkStatis(List<AnswAndResultSatis> countSatises) {
        List<StuWorkStatis> stuWorkStatises = new ArrayList<>();
        for(AnswAndResultSatis a : countSatises){
            StuWorkStatis s = new StuWorkStatis();
            s.setStudentId(a.getStudentId());
            s.setSubjectId(a.getSubjectId());
            s.setModuleId(a.getModuleId());
            s.setUnitId(a.getUnitId());
            s.setTopicId(a.getTopicId());
            s.setLevel(a.getLevel());
            s.setRightNumber(a.getRightNumber());
            s.setAnswerNumber(a.getAnswerNumber());
            stuWorkStatises.add(s);
        }
        return  stuWorkStatises;
    }

    private String getWorkStaisRedisKey(Long studentId) {
        return AppType.RedisPrefixStudentWork.REDIS_KEY_PROFIX_STU_WORK_STATIS + "_" + String.valueOf(studentId);
    }
}
