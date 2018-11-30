package com.noriental.praxissvr.question.service;

import com.noriental.BaseTest;
import com.noriental.praxissvr.question.bean.LinkQuestionTopic;
import com.noriental.praxissvr.question.mapper.LinkQuestionTopicMapper;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hushuang on 2016/12/2.
 */
public class LinkQuestionTopicMapperTest extends BaseTest {

    @Resource
    private LinkQuestionTopicMapper mapper;


    @Test
    public void testBatchInsertInto(){
        List<Map<String,Object>> list = new ArrayList<Map<String, Object>>();
        Map<String,Object> map1 = new HashMap<String,Object>();
        map1.put("questionId",3032805);
        map1.put("topicId",13894);
        list.add(map1);
        Map<String,Object> map2 = new HashMap<String,Object>();
        map2.put("questionId",3032805);
        map2.put("topicId",13894);
        list.add(map2);
        Map<String,Object> map3 = new HashMap<String,Object>();
        map3.put("questionId",3032805);
        map3.put("topicId",13894);
        list.add(map3);
        int i = mapper.batchQuestionTopicLink(list);
        System.out.println("============《》《》《》《》《》《》《》"+i);

    }


    @Test
    public void testFindLinkQuestionTopicByQuestionId(){


        List<LinkQuestionTopic> topics =
                mapper.findLinkQuestionTopicByQuestionId(3533L);


        for (LinkQuestionTopic topic : topics) {
            System.out.println("kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk:"+topic);
        }

    }


}
