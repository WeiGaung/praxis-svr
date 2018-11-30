package com.noriental.praxissvr.question.service.impl;

import com.noriental.BaseTest;
import com.noriental.praxissvr.question.constaints.EntrustStatusEnum;
import com.noriental.praxissvr.question.request.CreateEntrustExerciseRequest;
import com.noriental.praxissvr.question.request.EntrustUploadFileVo;
import com.noriental.praxissvr.question.request.FindEntrustExercisesRequest;
import com.noriental.praxissvr.question.request.UpdateEntrustExerciseRequest;
import com.noriental.praxissvr.question.service.EntrustExerciseService;
import com.noriental.utils.json.JsonUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

//@Transactional
public class EntrustExerciseServiceImplTest extends BaseTest {


    @Resource
    private EntrustExerciseService entrustExerciseService;
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Test
    public void findEntrustExercises() throws Exception {
        FindEntrustExercisesRequest req = new FindEntrustExercisesRequest();
        req.setPageNo(1);
        req.setPageSize(10);
        req.setTeacherId(1L);
//        req.setStatus(EntrustStatusEnum.FAIL);
        logger.info("result-" + JsonUtil.obj2Json(entrustExerciseService.findEntrustExercises(req)));
    }

    @Test
    public void updateEntrustExercise() throws Exception {
        UpdateEntrustExerciseRequest req = new UpdateEntrustExerciseRequest(1L, 6106331L, "name1", EntrustStatusEnum.SUCCESS, "desc1");
//        logger.info(JsonUtil.obj2Json(entrustExerciseService.updateEntrustExercise(req)));
    }
    @Test
    public void createEntrustExercise() throws Exception {
        CreateEntrustExerciseRequest reqq = new CreateEntrustExerciseRequest();
        reqq.setArticle("a");
        reqq.setChapter("c");
        reqq.setChapterId(1);
        reqq.setDescription("de");
        List<EntrustUploadFileVo> files = new ArrayList<>();
        EntrustUploadFileVo vo = new EntrustUploadFileVo();
        vo.setQiniuUrl("qiniu1");
        vo.setFileName("fn1");
        files.add(vo);
        EntrustUploadFileVo vo1 = new EntrustUploadFileVo();
        vo1.setQiniuUrl("qiniu2");
        vo1.setFileName("fn2");
        files.add(vo1);

//        reqq.setFiles(files);
//        reqq.setPhoneNumber("11");
//        reqq.setQuestionsName("qn");
//        reqq.setSchoolName("scn");
//        reqq.setSection("s");
//        reqq.setStageName("sn");
        reqq.setSubjectName("sbn");
        reqq.setTeacherId(1L);
        reqq.setTeacherName("tn");
        reqq.setQiniuUrl("qiniuurl");
        reqq.setFileName("filename");
        reqq =  JsonUtil.readValue("{\"reqId\":null,\"phoneNumber\":\"15666666666\",\"files\":[],\"description\":\"sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多sd的点点滴滴多多多多多多多多多多多多多多多多\",\"teacherName\":\"王小岩初英\",\"schoolName\":\"Okay学校\",\"subjectName\":\"英语\",\"stageName\":\"初中\",\"teacherId\":62951079122,\"chapter\":\"\",\"section\":\"\",\"article\":\"\",\"questionsName\":\"111\",\"chapterId\":7760,\"fileName\":\"新建 Microsoft Word 文档.docx\",\"qiniuUrl\":\"http://rc.okjiaoyu.cn/rc_KA9biQsolG.docx\"}",reqq.getClass());
        valid(reqq);
        entrustExerciseService.createEntrustExercise(reqq);
    }
}