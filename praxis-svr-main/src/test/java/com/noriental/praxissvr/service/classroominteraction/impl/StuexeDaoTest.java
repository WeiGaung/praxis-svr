package com.noriental.praxissvr.service.classroominteraction.impl;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.invoke.MethodHandles;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testApplicationContext.xml"})
@Ignore
public class StuexeDaoTest {
	     
	 private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
//	     @Autowired
//	    private StudentExerciseDao StudentExerciseDao;
//	    @Test
//	    public void insert() throws Exception {
//			StudentExercise se = new StudentExercise();
//			se.setClassId(13L);
//			se.setCreateTime(new Date());
//			StudentExerciseDao.insert(se);
//	    }

}
