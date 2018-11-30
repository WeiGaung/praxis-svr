package com.noriental.praxissvr.question.mapper;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.noriental.BaseTest;
import com.noriental.praxissvr.question.bean.BaseWordTranslation;
import com.noriental.praxissvr.question.bean.EntityCounterResources;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by hushuang on 2017/7/10.
 */
public class BaseWordTranslationMapperTest extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(BaseWordTranslationMapperTest.class);

    @Resource
    private BaseWordTranslationMapper baseWordTranslationMapper;

    @Resource
    private EntityCounterResMapper entityCounterResMapper;


    @Test
    public void find(){
        List<EntityCounterResources> longs = entityCounterResMapper.find(Arrays.asList(4751894L,2451543L,4644859L));
        System.out.println(longs.toString());
    }
    @Test
    public void updateCounter(){
        int i = entityCounterResMapper.updateCounter(7l,4751894l);
        if(i>0){
            System.out.println(i);
        }
    }

    @Test
    public void findWordTranslationsByChpterIdTest() {
        PageList<BaseWordTranslation> wordTranslations = baseWordTranslationMapper.findWordTranslationsByChpterId(100L,new PageBounds(1,100));
        logger.info("\n <<<<<<<<<<<<<<<<<<{}", wordTranslations);
    }

    @Test
    public void findWordsByIdsTest() {
        List<Long> list = new ArrayList<>();
        list.add(100L);
        PageList<BaseWordTranslation> wordTranslations = baseWordTranslationMapper.findWordsByIds(list,new PageBounds(1,100));
        logger.info("\n <<<<<<<<<<<<<<<<<<{}", wordTranslations);
    }

    @Test
    public void findWordTypeByTypesAndWordsTest(){

//        List<Integer> typeIds = new ArrayList<>();
//        typeIds.add(55);
//        //typeIds.add(56);
//        //typeIds.add(57);
//
//        List<Long> wordIds = new ArrayList<>();
//        wordIds.add(743L);
//        wordIds.add(474L);
//        wordIds.add(475L);
//        wordIds.add(476L);
//        wordIds.add(477L);
//        Long chapterId=null;
//        Long versionId=null;
//        Long directoryId=1001541L;
//
//        PageList<LinkWordType> linkWordTypes = baseWordTranslationMapper.findWordTypeByTypesAndWords(directoryId,chapterId,versionId,typeIds, wordIds, new PageBounds(1, 100));
//        logger.info("\n <<<<<<<<<<<<<<<<<<{}", linkWordTypes);
//        logger.info("\n <<<<<<<<<<<<<<<<<<{}", linkWordTypes.size());
    }

}
