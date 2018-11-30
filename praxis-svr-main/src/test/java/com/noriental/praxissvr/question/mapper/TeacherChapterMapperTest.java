package com.noriental.praxissvr.question.mapper;

import com.noriental.BaseTest;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Created by hushuang on 2017/7/24.
 */
public class TeacherChapterMapperTest extends BaseTest {


    @Resource
    private TeacherChapterMapper mapper;

    @Test
    public void testFindVersionByDirectoryId(){
        Map<String, Object> objectMap = mapper.findVersionByDirectoryId(1L);
        System.out.println(objectMap);


    }

}
