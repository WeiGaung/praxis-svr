package com.noriental.praxissvr.answer.util;

import com.noriental.utils.json.JsonUtil;
import com.noriental.utils.redis.RedisUtil;
import com.noriental.validate.error.BasicErrorCode;
import com.noriental.validate.exception.BizLayerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.io.*;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;

/**
 * @author kate
 * @create 2018-01-04 10:46
 * @desc redis工具类的扩展
 **/
@Component
public class RedisUtilExtend extends RedisUtil {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public void viewmSet(int dbIndex, Map<String, Object> map, Map<String, Integer> expireMap) {
        if (map == null || map.isEmpty()) {
            throw new BizLayerException("参数错误", BasicErrorCode.BASIC_INPUT_PARAM_ERROR);
        }
        try (Jedis jedis = jedisPool.getResource()) {
            if (dbIndex > 0) {
                jedis.select(dbIndex);
            }
            Pipeline pipelined = jedis.pipelined();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                byte[] key = getKey(entry.getKey());
                Object value = entry.getValue();
                Map<String, Object> tinyMap = JsonUtil.obj2Map(value);
                Map<byte[], byte[]> newMap = new HashMap<>();
                for (Map.Entry<String, Object> tinyEntry : tinyMap.entrySet()) {
                    if (tinyEntry.getValue() != null) {
                        byte[] key1 = tinyEntry.getKey().getBytes();
                        byte[] val1 = object2Bytes(tinyEntry.getValue());
                        newMap.put(key1, val1);
                    }
                }
                pipelined.hmset(key, newMap);
                int expire = expireMap.get(entry.getKey());
                pipelined.expire(key, expire);
            }
            pipelined.sync();
        } catch (Exception e) {
            throw e;
        }
    }


    private byte[] getKey(String key) {
        return key.getBytes();
    }


    /**
     * 对象转化为字节
     *
     * @param value
     * @return
     */
    private byte[] object2Bytes(Object value) {
        if (value == null) {
            return null;
        }
        try (ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream(); ObjectOutputStream outputStream =
                new ObjectOutputStream(arrayOutputStream);) {
            outputStream.writeObject(value);
            outputStream.flush();
            return arrayOutputStream.toByteArray();
        } catch (IOException e) {
            logger.error("", e);
        }
        return null;
    }

    /**
     * redis加锁
     * @param dbIndex
     * @param key
     * @param expireSeconds
     * @return
     */

    public  boolean simpleLock(int dbIndex, String key, int expireSeconds) {
        try ( Jedis jedis = jedisPool.getResource()) {
            if(dbIndex > 0) {
                jedis.select(dbIndex);
            }
            String result = jedis.set(key, "lock", "nx", "ex", expireSeconds);
            return "OK".equals(result);

        } catch (Exception e) {
            throw e;
        }
    }
    /**
     * 字节转化为对象
     *
     * @param bytes
     * @return
     */
    private Object bytes2Object(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        try (ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(bytes)); ){
            Object obj = inputStream.readObject();
            return obj;
        } catch (IOException e) {
            logger.error("", e);
        } catch (ClassNotFoundException e) {
            logger.error("", e);
        }
        return null;
    }

    /***
     *
     * @param keys
     * @param fields
     * @param values
     */
    public  void redisHset(byte[] keys,byte[] fields,byte[] values){
        if (values == null || values.length == 0) {
            logger.error("values is null");
            return;
        }
        try (Jedis jedis = jedisPool.getResource()) {
             jedis.hset(keys,fields,values);
        }

    }
}
