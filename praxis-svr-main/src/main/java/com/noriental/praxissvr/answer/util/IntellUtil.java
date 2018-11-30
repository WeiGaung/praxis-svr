package com.noriental.praxissvr.answer.util;

import com.noriental.log.TraceKeyHolder;
import com.noriental.praxissvr.answer.bean.IntellMqEntity;
import com.noriental.utils.json.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.amqp.core.Message;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.Map;

/**
 * Created by kate on 2017/8/4.
 */
public class IntellUtil {
    private static Logger logger = LoggerFactory.getLogger(IntellUtil.class);

    public static Object getFromMessage(Message message) {
        byte[] bytes = message.getBody();
        Object object = null;
        try {
            ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
            ObjectInputStream oi = new ObjectInputStream(bi);
            object = oi.readObject();
            bi.close();
            oi.close();
        } catch (Exception e) {
            logger.error("Exception occurs", e);
        }
        return object;
    }


    public static IntellMqEntity getPhpFromMessage(Message message) {
        byte[] bytes = message.getBody();
        IntellMqEntity object = null;
        String reqString;
        try {
            reqString = new String(bytes,"UTF-8");
            Map<String,Object> map= JsonUtil.readValue(reqString,Map.class);
            String reqDate =map.get("data").toString();
            object = JsonUtil.readValue(reqDate,IntellMqEntity.class);
        } catch (Exception e) {
            logger.error("Exception occurs", e);
        }
        return object;
    }


    /**
     * logger中使用的id
     */
    public static void initLogRequestId(String requestId) {
        if (StringUtils.isBlank(requestId)) {
            logger.warn("requestId is null!");
        } else {
            MDC.put("id", requestId);
            TraceKeyHolder.setTraceKey(requestId);
        }
    }



}
