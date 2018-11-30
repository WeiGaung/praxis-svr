package com.noriental.praxissvr.service.quiz2.impl;

import com.alibaba.fastjson.JSONObject;
import com.noriental.BaseTestClient;
import com.noriental.log.TraceKeyHolder;
import com.noriental.praxissvr.question.request.FindAllQuestionTypeRequest;
import com.noriental.praxissvr.question.request.FindQuestionTypesBySubjectIdRequest;
import com.noriental.praxissvr.question.request.FindQuestionTypesZhBySubjectIdRequest;
import com.noriental.praxissvr.question.response.FindAllQuestionTypeResponse;
import com.noriental.praxissvr.question.response.FindQuestionTypesBySubjectIdResponse;
import com.noriental.praxissvr.question.response.FindQuestionTypesZhBySubjectIdResponse;
import com.noriental.praxissvr.question.service.QuestionTypeService;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.lang.invoke.MethodHandles;
import java.util.Date;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author chenlihua
 * @date 2015/12/22
 * @time 16:01
 */
public class QuestionTypeServiceImplTest extends BaseTestClient {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Resource
    private QuestionTypeService questionTypeService;

    @Test
    public void testFindQuestionTypeBySubjectId() throws Exception {
        FindQuestionTypesBySubjectIdRequest request = new FindQuestionTypesBySubjectIdRequest();
        request.setSubjectId(5);
        FindQuestionTypesBySubjectIdResponse resp = questionTypeService.findQuestionTypeBySubjectId(request);
        System.out.println(JSONObject.toJSONString(resp, true));
        assertNotNull(resp);
        assertTrue(CollectionUtils.isNotEmpty(resp.getList()));
    }

    @Test
    public void testFindQuestionTypesZhBySubjectId() throws Exception {
        FindQuestionTypesZhBySubjectIdRequest request = new FindQuestionTypesZhBySubjectIdRequest();
        request.setSubjectId(1);
        FindQuestionTypesZhBySubjectIdResponse resp = questionTypeService.findQuestionTypesZhBySubjectId(request);
        assertNotNull(resp);
        assertTrue(CollectionUtils.isNotEmpty(resp.getList()));
        System.out.println(JSONObject.toJSONString(resp, true));
    }

    /**
     * 测试所有学段学科的题型
     */
    /*@Test
    public void testAllSubjectQuestionType() {
        List<Subject> subjectList = subjectService.findAll();
        Map<String, List<Subject>> stageSubjectsMap = new HashMap<>();
        for (Subject subject : subjectList) {
            Subject s = subjectService.findById(subject.getId());
            if (s == null) {
                continue;
            }
            Stage stage = s.getStage();
            long stageId = stage.getId();
            String stageName = stage.getName();
            String key = stageId + ":" + stageName;
            List<Subject> subjects = stageSubjectsMap.get(key);
            if (subjects == null) {
                subjects = new ArrayList<>();
                stageSubjectsMap.put(key, subjects);
            }
            subjects.add(s);
        }

        for (String s : stageSubjectsMap.keySet()) {
            List<Subject> subjects = stageSubjectsMap.get(s);
            for (Subject subject : subjects) {
                System.out.println("------------------------------"+ subject.getFullName() +"-------------------------------");
                FindQuestionTypesZhBySubjectIdRequest request = new FindQuestionTypesZhBySubjectIdRequest();
                request.setSubjectId(subject.getId());
                FindQuestionTypesZhBySubjectIdResponse resp = questionTypeService.findQuestionTypesZhBySubjectId(request);
                List<String> list = resp.getList();
                for (String typeName : list) {
                    System.out.println(String.format("%s:[%s]%s:%s", s,subject.getId(),subject.getName(), typeName));
                }
            }
        }

    }*/
    @Before
    public void setUp() throws Exception {
        String requestId = new Date().getTime() + "";
        System.out.println("requestId:" + requestId);
        TraceKeyHolder.setTraceKey(requestId);
    }

    @Test
    public void testFindAllQuestionType() throws Exception {
        FindAllQuestionTypeResponse resp = questionTypeService.findAllQuestionType(new FindAllQuestionTypeRequest());
        assertTrue(resp.success());
        System.out.println(JSONObject.toJSONString(resp, true));
    }

}