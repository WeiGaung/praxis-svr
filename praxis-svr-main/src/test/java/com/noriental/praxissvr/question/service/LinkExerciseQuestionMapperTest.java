package com.noriental.praxissvr.question.service;

import com.noriental.BaseTest;
import com.noriental.praxissvr.question.bean.LinkExerciseQuestion;
import com.noriental.praxissvr.question.mapper.LinkExerciseQuestionMapper;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by hushuang on 2016/12/2.
 */
public class LinkExerciseQuestionMapperTest extends BaseTest{

    @Resource
    private LinkExerciseQuestionMapper linkExerciseQuestionMapper;

    @Test
    public void testfindLinkExerciseQuestionById(){

        List<LinkExerciseQuestion> linkExerciseQuestionById =
                linkExerciseQuestionMapper.findLinkExerciseQuestionById(1414338L);

        System.out.println("<><><><><><><><><><><<><>><>><><><><><><<>>"+linkExerciseQuestionById);


    }

}
