package com.noriental.praxissvr.service.stuanswer.impl;

import com.noriental.praxissvr.answer.bean.StudentExercise;
import com.noriental.praxissvr.answer.service.StudentErrorExeService;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/application*.xml" })
@Ignore
public class StudentErrorExeServiceImplTest {
//    @Autowired
//    private StudentWorkService studentWorkService;
    @Autowired
    private StudentErrorExeService studentErrorExeService;
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @Test
    public void main() throws Exception {
		List<StudentExercise> seList = new ArrayList<>();
		StudentExercise se  = new StudentExercise();
		se.setStudentId(100L);
		se.setQuestionId(2L);
		se.setExerciseSource("1");
		se.setResourceId(1L);
		se.setYear(1);
		seList.add(se);
		studentErrorExeService.creates(seList);
    	
    }

}
