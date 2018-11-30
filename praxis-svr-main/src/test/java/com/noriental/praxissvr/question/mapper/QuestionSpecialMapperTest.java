package com.noriental.praxissvr.question.mapper;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.noriental.BaseTest;
import com.noriental.praxissvr.question.bean.EntityQuestionSpecial;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liujiang on 2018/4/26.
 */
public class QuestionSpecialMapperTest extends BaseTest {
    private static final Logger logger = LoggerFactory.getLogger(LinkCustomQuestionResourceMapperTest.class);
    @Resource
    private QuestionSpecialMapper mapper;

    @Test
    public void testQueryQuestionSpecialList(){
        PageList<EntityQuestionSpecial> SpecialList= mapper.queryQuestionSpecialList(8401474L,new PageBounds(1,10));
        for (EntityQuestionSpecial ids:SpecialList){
            System.out.println("专题id为"+ids.getExamSitesId()+" 名称为："+ids.getName());
        }
    }

    @Test
    public void testCreateQuestionSpecial(){
        EntityQuestionSpecial entityQuestionSpecial=new EntityQuestionSpecial();
        entityQuestionSpecial.setQuestionId(111111111L);
        entityQuestionSpecial.setExamSitesId(222222222L);
        System.out.println(mapper.createQuestionSpecial(entityQuestionSpecial));
    }

    @Test
    public void testDeleteQuestionSpecialById(){
        System.out.println(mapper.deleteQuestionSpecialById(111111111L));
    }

    @Test
    public void testBatchInsertQuestionSpecial(){
        EntityQuestionSpecial entityQuestionSpecial=new EntityQuestionSpecial();
        entityQuestionSpecial.setQuestionId(111111111L);
        entityQuestionSpecial.setExamSitesId(222222222L);
        EntityQuestionSpecial entityQuestionSpecial1=new EntityQuestionSpecial();
        entityQuestionSpecial1.setQuestionId(211111111L);
        entityQuestionSpecial1.setExamSitesId(122222222L);
        List<EntityQuestionSpecial> entityList=new ArrayList<>();
        entityList.add(entityQuestionSpecial);
        entityList.add(entityQuestionSpecial1);

        System.out.println( mapper.batchInsertQuestionSpecial(entityList));
    }
}
