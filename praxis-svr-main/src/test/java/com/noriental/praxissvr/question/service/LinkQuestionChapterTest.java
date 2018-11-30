package com.noriental.praxissvr.question.service;

import com.noriental.BaseTest;
import com.noriental.praxissvr.question.bean.LinkQuestionChapter;
import com.noriental.praxissvr.question.mapper.LinkQuestionChapterMapper;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created by hushuang on 2016/12/2.
 */
public class LinkQuestionChapterTest extends BaseTest {

    @Resource
    private LinkQuestionChapterMapper mapper;

    @Test
    public void testBatchLinkQuestionChapter(){



        List<Map<String,Object>> list = new ArrayList<Map<String, Object>>();

        Map<String,Object> map1 = new HashMap<String,Object>();
        map1.put("questionId",4000034);
        map1.put("chapterId",8072);
        list.add(map1);

        Map<String,Object> map2 = new HashMap<String,Object>();
        map2.put("questionId",4000033);
        map2.put("chapterId",8072);
        list.add(map2);


        try{
            int i = mapper.batchLinkQuestionChapter(list);
            System.out.println("<><><><><<><><><><><><><><><><?<?<><><?<<><><"+i);
        }catch (Exception e){
            e.printStackTrace();
            System.err.println(e.getStackTrace());
            //抛出错误不需要触发构建测试错误邮件通知
        }


    }


    @Test
    public void testFindLinkQuestionChapterById(){
        List<LinkQuestionChapter> chapterById = mapper.findLinkQuestionChapterById(4002098L);
        System.out.println(chapterById);
    }

}
