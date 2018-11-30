package com.noriental.praxissvr.question.mapper;

import com.noriental.BaseTest;
import com.noriental.praxissvr.question.bean.CustomQuestionResource;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by hushuang on 2017/7/11.
 */
public class LinkCustomQuestionResourceMapperTest extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(LinkCustomQuestionResourceMapperTest.class);
    @Resource
    private LinkCustomQuestionResourceMapper mapper;



    @Test
    public void batchUpdateCustomeQuestionResourceTest(){

        List<CustomQuestionResource> customQuestionResourceList = new ArrayList<>();
        CustomQuestionResource questionResource = new CustomQuestionResource();
        questionResource.setCreateTime(new Date());
        questionResource.setSystemId(200L);
        questionResource.setQuestionId(100L);
        questionResource.setCustomListId(500L);
        questionResource.setGroupId(500L);
        questionResource.setIsFav(0);
        questionResource.setResourceStatus(1);
        customQuestionResourceList.add(questionResource);

        CustomQuestionResource questionResource1 = new CustomQuestionResource();
        questionResource1.setCreateTime(new Date());
        questionResource1.setSystemId(100L);
        questionResource1.setQuestionId(100L);
        questionResource1.setCustomListId(60000L);
        questionResource1.setGroupId(60000L);
        questionResource1.setIsFav(0);
        questionResource1.setResourceStatus(1);
        customQuestionResourceList.add(questionResource1);


        int i = mapper.batchUpdateLinkCustomQuestionResource(customQuestionResourceList);
        logger.info("\n<<<<<<<<<<<<<<<：{}",i);


    }


    @Test
    public void batchInsertLinkCustomQuestionResourceTest() {

        List<CustomQuestionResource> customQuestionResourceList = new ArrayList<>();
        CustomQuestionResource questionResource = new CustomQuestionResource();
        questionResource.setCreateTime(new Date());
        questionResource.setSystemId(101L);
        questionResource.setQuestionId(100L);
        questionResource.setCustomListId(100L);
        questionResource.setGroupId(100L);
        questionResource.setIsFav(1);
        questionResource.setResourceStatus(1);
        customQuestionResourceList.add(questionResource);

        CustomQuestionResource questionResource1 = new CustomQuestionResource();
        questionResource1.setCreateTime(new Date());
        questionResource1.setSystemId(100L);
        questionResource1.setQuestionId(100L);
        questionResource1.setCustomListId(100L);
        questionResource1.setGroupId(100L);
        questionResource1.setIsFav(1);
        questionResource1.setResourceStatus(1);
        customQuestionResourceList.add(questionResource1);


        int i = mapper.batchInsertLinkCustomQuestionResource(customQuestionResourceList);
        logger.info("\n<<<<<<<<<<<<<<<：{}",i);
    }

    @Test
    public void insertLinkCustomQuestionResourceTest() {
        CustomQuestionResource questionResource = new CustomQuestionResource();
        questionResource.setCreateTime(new Date());
        questionResource.setSystemId(100L);
        questionResource.setQuestionId(100L);
        questionResource.setCustomListId(100L);
        questionResource.setGroupId(100L);
        questionResource.setIsFav(1);
        questionResource.setResourceStatus(1);

        int i = mapper.insertLinkCustomQuestionResource(questionResource);
        logger.info("\n<<<<<<<<<<<<<<<：{}",questionResource.getId());
    }

    @Test
    public void queryCustomQuestionResourceByQuestionIdTest(){
        CustomQuestionResource resource = mapper.queryCustomQuestionResourceByQuesIdAndSysId(4170525L,6160381L);
        logger.info("\n<<<<<<<<数据为:{}",resource);
    }


    @Test
    public void updateLinkCustomQuestionResourceTest(){
        CustomQuestionResource customQuestionResource = new CustomQuestionResource();
        customQuestionResource.setSystemId(6160381L);
        customQuestionResource.setQuestionId(7669026L);
        customQuestionResource.setGroupId(101L);
        customQuestionResource.setCustomListId(101L);
        customQuestionResource.setIsFav(0);
        customQuestionResource.setResourceStatus(1);
        customQuestionResource.setUpdateTime(new Date());
        int i = mapper.updateLinkCustomQuestionResource(customQuestionResource);
        logger.info("\n<<<<<<<<<<<<<<<影响行数：{}",i);
    }

    @Test
    public void queryCustomQuestionResourceByCusDirIdTest(){

        List<CustomQuestionResource> resources = mapper.queryCustomQuestionResourceByCusDirId(6160381L, 291L);
        logger.info("\n<<<<<<<<<<<<<<<resources：{}",resources);

    }


    @Test
    public void updateLinkCustomDirectoryBySysIdAndCusDirIdTest(){

        int i = mapper.updateLinkCustomDirectoryBySysIdAndCusDirId(286L, 287L, 6160381L, 0L);
        logger.info("\n<<<<<<<<<<<<<<<resources：{}",i);
    }

    @Test
    public void deleteCustomDiritorydTest(){

        int i = mapper.deleteCustomDiritory(4056854L,6160381L);
        logger.info("\n<<<<<<<<<<<<<<<resources：{}",i);
    }

    @Test
    public void queryCustomQuestionBySysIdAndCusDirIdAndQuestIdTest(){
        CustomQuestionResource questionResource = mapper.queryCustomQuestionBySysIdAndCusDirIdAndQuestId(1133546L, 6160381L, 19985L);
        logger.info("\n<<<<<<<<<<<<<<<questionResource：{}",questionResource);
    }

}
