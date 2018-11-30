package com.noriental.praxissvr.utils;

import com.noriental.praxissvr.common.StuAnswerConstant;
import com.noriental.praxissvr.question.mapper.AuditsedSchoolMapper;
import com.noriental.praxissvr.question.utils.QuestionServiceUtil;
import com.noriental.utils.redis.RedisUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by shengxian on 2017/1/11.
 */
public class PraxisUtilForOtherTest {


    @Test
    public void getResultArray() throws Exception {

            System.out.println(
                    Arrays.toString(PraxisUtilForOther.getResultArray(StuAnswerConstant.StructType.STRUCT_7x5,"[1,2,4,\"0\"]"))
            );
    }
    @Test
    public void getCollectionEmpty(){
        List<String> strList=new ArrayList<>();
        if(CollectionUtils.isNotEmpty(strList)){
            System.out.println("ok");
        }

    }



}