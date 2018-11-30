package com.noriental.praxissvr.question.utils;

import com.noriental.praxissvr.exception.PraxisErrorCode;
import com.noriental.validate.exception.BizLayerException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Created by kate on 2016/11/30.
 * 解析html数据，获取题干信息和选择题选项信息
 */
public class ParseData2SolrUtil {


    //判断html json数据1、判断数据options长度是否大于0，如果大于零说明数据类型为选择题 ，解析options数据在里面的数据
    //2、判断questions数组长度是否大于0，如果大于0，解析数组每个信息，获取options信息

    @Deprecated
    public static boolean isListByKey(JSONObject jsonObject, String key)  {
        if (jsonObject.has(key)) {
            JSONArray jsonArray = null;
            try {
                jsonArray = jsonObject.getJSONArray(key);
            } catch (JSONException e) {
                e.printStackTrace();
                throw new BizLayerException("转换异常", PraxisErrorCode.INNER_ERROR);
            }
            if (jsonArray.length() > 0) {
                return true;
            }
        }

        return false;
    }

    /***
     * 获取题干、选择题选项信息
     * @param jsonObject
     * @return
     * @throws Exception
     */
    @Deprecated
    public static String getDataByKey(JSONObject jsonObject, String key, String dataType)  {
        try{

        if (jsonObject.has(key)) {
            if ("JSONObject".equals(dataType)) {
                String body = jsonObject.getString(key);
                Document document = Jsoup.parse(body);
                return document.tagName("p").text();
            }
            StringBuilder content = new StringBuilder();
            if ("JSONArray".equals(dataType)) {
                JSONArray jsonArray = jsonObject.getJSONArray(key);
                for (int i = 0; i < jsonArray.length(); i++) {
                    String currentData = jsonArray.get(i).toString();
                    Document document = Jsoup.parse(currentData);
                    content.append( document.tagName("p").text());
                }
                return content.toString();
            }
        }
    } catch (JSONException e) {
        e.printStackTrace();
        throw new BizLayerException("转换异常", PraxisErrorCode.INNER_ERROR);
    }
        return null;
    }


   /* public static void main(String[] args) throws Exception {
        String html = "{\"translation\":\"<p>选择完形填空</p>\",\"material\":\"<p>选择完形填空</p>\",\"questions\":[{\"answer\":[\"D\"],\"difficulty\":\"2\",\"type\":{\"id\":1,\"name\":\"选择题\"},\"options\":[\"<p>选择完形填空</p>\",\"<p>选择完形填空</p>\",\"<p>选择完形填空</p>\",\"<p>选择完形填空</p>\"],\"analysis\":\"<p>选择完形填空</p>\"}],\"audio\":{\"url\":\"\",\"name\":\"\",\"size\":\"\"}}";
        String html2 = "{\"body\":\"<p>测试</p>\",\"answer\":[\"A\"],\"analysis\":\"<p>测试</p>\",\"options\":[\"<p>2</p>\",\"<p>2</p>\",\"<p>2</p>\",\"<p>2</p>\"],\"questions\":[]}";
        String resultData = getSolrData(html2);
        System.out.println(resultData);
    }*/


}
