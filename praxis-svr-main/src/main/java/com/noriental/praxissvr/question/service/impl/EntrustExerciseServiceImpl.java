package com.noriental.praxissvr.question.service.impl;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.noriental.praxissvr.common.PageBaseResponse;
import com.noriental.praxissvr.question.bean.EntrustUpload;
import com.noriental.praxissvr.question.bean.EntrustUploadFile;
import com.noriental.praxissvr.question.constaints.EntrustStatusEnum;
import com.noriental.praxissvr.question.mapper.EntrustUploadFileMapper;
import com.noriental.praxissvr.question.mapper.EntrustUploadMapper;
import com.noriental.praxissvr.question.request.CreateEntrustExerciseRequest;
import com.noriental.praxissvr.question.request.EntrustUploadFileVo;
import com.noriental.praxissvr.question.request.FindEntrustExercisesRequest;
import com.noriental.praxissvr.question.service.EntrustExerciseService;
import com.noriental.resourcesvr.customlist.request.CanUseCustomRequest;
import com.noriental.resourcesvr.customlist.service.CustomListService;
import com.noriental.validate.bean.CommonDes;
import com.noriental.validate.bean.CommonResponse;
import com.noriental.validate.exception.BizLayerException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.noriental.praxissvr.exception.PraxisErrorCode.CUS_DIR_INVALID;

/**
 * @author chenlihua
 * @date 2015/12/23
 * @time 11:14
 */
@Service("entrustExerciseService")
public class EntrustExerciseServiceImpl implements EntrustExerciseService {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Resource
    private EntrustUploadMapper entrustUploadMapper;
    @Resource
    private EntrustUploadFileMapper entrustUploadFileMapper;
    @Resource
    private CustomListService customListService;

//    @Override
//    public CommonDes updateEntrustExercise(UpdateEntrustExerciseRequest request) throws BizLayerException {
//        if (entrustUploadMapper.updateEntrustExercise(request)) return new CommonDes();
//        throw new BizLayerException("", PraxisErrorCode.PRAXIS_ENTRUST_NO_FOUND);
//    }

    @Override
    public PageBaseResponse<EntrustUpload> findEntrustExercises(FindEntrustExercisesRequest request) throws BizLayerException {
        EntrustStatusEnum status = request.getStatus();
        PageList<EntrustUpload> pageList = entrustUploadMapper.findEntrustExercises(request.getTeacherId(), status != null ? status.getCode() : null, new PageBounds(request.getPageNo(), request.getPageSize()));
        return new PageBaseResponse<>(pageList);
    }

    @Override
    public CommonDes createEntrustExercise(CreateEntrustExerciseRequest request) throws BizLayerException {

        logger.info("委托传题数据为:{}",request.toString());
        /*
            1.判断自定义目录是否存在
         */
        CanUseCustomRequest canUseCustomRequest = new CanUseCustomRequest();
        canUseCustomRequest.setGroupId(request.getGroupId());
        canUseCustomRequest.setCatalogId(request.getCustomerDirectoryId());
        canUseCustomRequest.setSystemId(request.getTeacherId());
        CommonResponse<Boolean> useCustom = customListService.canUseCustom(canUseCustomRequest);
        /*
            可用的是true
            不可用是false
         */
        if(!useCustom.getData()){
            logger.info("委托传题失败自定义目录不存在数据为:{}",request.toString());
            throw new BizLayerException("",CUS_DIR_INVALID);
        }

        EntrustUpload record = new EntrustUpload();
        String description = request.getDescription();
        request.setDescription(description.length() > 205 ? description.substring(0, 205) : description);
        BeanUtils.copyProperties(request, record);
        record.setLastUpdateTime(new Date());
        record.setTime(new Date());
        record.setStatus("1");
        List<Long> topicId = request.getTopicId();
        if (CollectionUtils.isNotEmpty(topicId)) {
            record.setTopicIds(StringUtils.join(topicId.toArray(), ","));
        }
        if (request.getChapterId() == null) {
            record.setChapterId(0);
        }
        if(request.getCustomerDirectoryId()!=null){
            record.setCustomListId(request.getCustomerDirectoryId());
        }
        if(request.getGroupId()!=null){
            record.setGroupId(request.getGroupId());
        }
        entrustUploadMapper.insert(record);
        List<EntrustUploadFileVo> filesIn = request.getFiles();
        List<EntrustUploadFile> files = new ArrayList<>();
        if (filesIn != null) {
            for (EntrustUploadFileVo vo : filesIn) {
                EntrustUploadFile f = new EntrustUploadFile();
                f.setEntrustId(record.getId());
                BeanUtils.copyProperties(vo, f);
                files.add(f);
            }
            if (files.size() > 0) {
                entrustUploadFileMapper.inserts(files);
            }
        }

        return new CommonDes();

    }
}
