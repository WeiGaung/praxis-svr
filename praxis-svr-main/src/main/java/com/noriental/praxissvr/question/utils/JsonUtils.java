package com.noriental.praxissvr.question.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.noriental.validate.exception.BizLayerException;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.noriental.praxissvr.exception.PraxisErrorCode.JSON_CONVERT_FAIL;

/**
 * Created by hushuang on 2016/11/24.
 */
public class JsonUtils {


    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    private static ObjectMapper mapper = new ObjectMapper();

    public static <T> T fromJson(String jsonAsString,Class<T> clazz) throws IOException {
        return mapper.readValue(jsonAsString,clazz);
    }


    /**
     * 获取ssdb内容
     * @param ssdb_content
     * @return
     */
    public static String getSsdbContent(String ssdb_content) {
        if(StringUtils.isNotEmpty(ssdb_content)){
            JSONObject jsonObject=null;
            try {
                jsonObject = new JSONObject(ssdb_content);
            } catch (JSONException e) {
                logger.error(e.getMessage(),e);
            }

            try {
                Object o = jsonObject==null?null:jsonObject.get("content");
                return o==null?null:o.toString();
            } catch (JSONException e) {
                logger.error(e.getMessage(),e);
            }
        }
        return null;
    }

    /**
     * 获取指定节点信息
     * @param ssdb_content
     * @param index
     * @return
     */
    public static String getIndexContent(String ssdb_content,String index) {
        if(StringUtils.isNotEmpty(ssdb_content)){
            JSONObject jsonObject=null;
            try {
                jsonObject = new JSONObject(ssdb_content);
            } catch (JSONException e) {
                logger.error(e.getMessage(),e);
            }

            try {
                Object o = jsonObject==null?null:jsonObject.get(index);
                return o==null?null:o.toString();
            } catch (JSONException e) {
                logger.error(e.getMessage(),e);
            }
        }
        return null;
    }


    /**
     * 判断json对象里面是否有key
     * @param content
     * @param key
     * @return
     */
    public static boolean is_key(String content,String key) throws BizLayerException{
        JSONObject json;

        try {
            json = new JSONObject(content);
        } catch (JSONException e) {
            logger.error("判断是否是单题异常JSONObject={}",e.getMessage()+"html=="+content);
            throw new BizLayerException("",JSON_CONVERT_FAIL);
        }
        return json.has(key);
    }



    /**
     * 获取json中key的数组长度
     * @param content
     * @param key
     * @return
     * @throws JSONException
     */
    public static int getQuestionsLength(String content,String key) throws BizLayerException{
        JSONObject json;
        try {
            json = new JSONObject(content);
        } catch (JSONException e) {
            logger.error("判断是否是单题异常JSONObject={}",e.getMessage()+"html=="+content);
            throw new BizLayerException("",JSON_CONVERT_FAIL);
        }
        boolean has = json.has(key);
        if(!has){
            throw new BizLayerException("key不存在",JSON_CONVERT_FAIL);
        }
        JSONArray array;
        try {
            array = json.getJSONArray(key);
        } catch (JSONException e) {
            logger.error("判断是否是单题异常getJSONArray={}",e.getMessage()+"html=="+content);
            throw new BizLayerException("",JSON_CONVERT_FAIL);
        }
        return array.length();
    }


    public static Object getJsonData(String jsonData, String key, String dataType) throws JSONException {
        JSONObject jsonObject=new JSONObject(jsonData);
        if(jsonObject.has(key)){
            if("JSONArray".equals(dataType)){
                JSONArray jsonArray = jsonObject.getJSONArray(key);
                return jsonArray;
            }
            return jsonObject.get(key);
        }else{
            return null;
        }
    }

    /**
     * 替换html中的双引号
     * (查找格式为 ="开头，并以此为开始查找它的下一个闭合")
     * @param htmlJson
     * @return
     */
    public static String replaceMark(String htmlJson){

        StringBuilder stringBuilder = new StringBuilder();
        if(null!=htmlJson && !"".equals(htmlJson)){
            stringBuilder.append(htmlJson);
            Pattern p = Pattern.compile("=\"");
            Matcher m = p.matcher(htmlJson);

            while (m.find()) {
                int start = m.start();
                int end = m.end();
                stringBuilder.replace(start,end,"='");

                int markStart = htmlJson.indexOf("\"",end);
                stringBuilder = stringBuilder.replace(markStart,markStart+1,"'");
            }
        }

        return stringBuilder.toString();
    }



    /**
     * 将Object对象转换成Json格式的数据
     * @param obj
     * @return
     * @throws Exception
     *
     *  注意：慎用这个ObjectMapper对象来进行json转换，会将new Date() 日期格式 转换成 long类型  ----大坑
     */
    public static String getObjectToJson(Object obj) throws BizLayerException{
        String resultJson  = null;
        ObjectMapper jsonMapper = new ObjectMapper();
        try {
            resultJson = jsonMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            logger.error("将Object对象转换成Json格式的数据失败",e);
            throw new BizLayerException("",JSON_CONVERT_FAIL);
        }
        if(resultJson != null){
            return resultJson;
        }
        return null;
    }

    /**
     * 将Json字符串转换成相应的
     * @param json
     * @return
     * @throws Exception
     *
     * 注意：慎用这个ObjectMapper对象来进行json转换，会将new Date() 日期格式 转换成 long类型  ----大坑
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Object getObjectFromJson(String json,Class objClass) throws BizLayerException{
        ObjectMapper jsonMapper = new ObjectMapper();
        Object obj = null;
        try {
            obj = jsonMapper.readValue(json, objClass);
        } catch (IOException e) {
            logger.error("将Json字符串转换成相应的对象失败",e);
            throw new BizLayerException("",JSON_CONVERT_FAIL);
        }
        if(obj != null){
            return obj;
        }
        return null;
    }
}
