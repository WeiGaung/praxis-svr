package com.noriental.praxissvr.question.service;

import com.hyd.ssdb.util.Str;
import com.noriental.BaseTest;
import com.noriental.praxissvr.question.request.ConvertMp3CallBackRequest;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hushuang on 2016/11/21.
 */

public class QiniuServiceTest extends BaseTest{

    private static final Logger logger = LoggerFactory.getLogger(QiniuServiceTest.class);

    @Resource
    private QiniuCallBackService qiniuCallBackService;



    @Test
    public void testCallBack() throws Exception {
        ConvertMp3CallBackRequest request = new ConvertMp3CallBackRequest();
        request.setEntityId("11111");
        String json = "{\"id\": \"16864pauo1vc9nhp12\",\"code\": 0,\"desc\": \"The fop was completed successfully\",\"inputKey\": \"sample.mp4\",\"inputBucket\": \"dutest\",\"items\": [{\"cmd\": \"avthumb/mp4/r/30/vb/256k/vcodec/libx264/ar/22061/ab/64k/acodec/libmp3lame\",\"code\": 0,\"desc\": \"The fop was completed successfully\",\"error\": \"\",\"hash\": \"FrPNF2qz66Bt14JMdgU8Ya7axZx-\",\"key\": \"v-PtT-DzpyCcqv6xNU25neTMkcc=/FjgJQXuH7OresQL4zgRqYG5bZ64x\",\"returnOld\": 0}],\"pipeline\": \"0.default\",\"reqid\": \"ffmpeg.3hMAAH3p5Gupb6oT\"}";
        request.setJsonStatusDescription(json);
        //qiniuCallBackService.convertMp3CallBack(request);


    }

    @Test
    public void testqiniuUrl(){

        String requestUrl="entityId="+100000+"&"+"token="+"dasdsadasdas";
        String entityId="";
        if(StringUtils.isNotEmpty(requestUrl)){
            //将截取到的url内的参数放在Map中保存
            Map<String,String> prameMap = new HashMap<>();
            //截取url内的参数
            boolean isHas = requestUrl.contains("&");
            if(isHas){
                String[] strings = requestUrl.split("&");
                for (String string : strings) {
                    String[] split = string.split("=");
                    prameMap.put(split[0],split[1]);
                }

            }
            entityId=prameMap.get("entityId");
            logger.info("======="+entityId);
            logger.info("======="+prameMap.get("token"));
        }


    }





}
