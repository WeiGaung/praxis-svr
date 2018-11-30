package com.noriental.praxissvr.resgroup.serviceImpl;

import com.noriental.praxissvr.exception.PraxisErrorCode;
import com.noriental.praxissvr.question.request.UpdateAudioInfoQuestionRequest;
import com.noriental.praxissvr.question.utils.JsonUtils;
import com.noriental.utils.json.JsonUtil;
import com.noriental.validate.exception.BizLayerException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kate on 2016/11/23.
 */
public class JsonUtilTest {

    public static void main(String[] args) throws Exception {

        String jsonData2="{\"question_id\":4002027,\"content\":{\"translation\":\"<p>666666666666</p>\",\"audio\":{\"url\":\"http://ra.okjiaoyu.cn/ra_LHDNIWoc2Q.amr\",\"name\":\"AMR-20170112.amr\",\"size\":\"1.7 M\"},\"map\":{\"url\":\"\",\"name\":\"\",\"size\":\"\"},\"questions\":[{\"type\":{\"id\":1,\"name\":\"选择题\"},\"body\":\"<p>666666666666666666666666</p>\",\"options\":[\"<p>666666666666</p>\",\"<p>666666666666</p>\",\"<p>666666666666</p>\",\"<p>666666666666</p>\"],\"answer\":[\"D\"],\"analysis\":\"<p>666666666666</p>\",\"difficulty\":\"2\"}],\"material\":\"<p>666666666666</p>\"}}";


        UpdateAudioInfoQuestionRequest updateAudioInfoQuestionRequest=new UpdateAudioInfoQuestionRequest();
        updateAudioInfoQuestionRequest.setSize("10.9 M");
        updateAudioInfoQuestionRequest.setName("See You Again.mp3");
        updateAudioInfoQuestionRequest.setUrl("http://live.ttmv.cn/ra_KoluN3F1xS.mp3");
       System.out.println(replaceAudioInfo(jsonData2,updateAudioInfoQuestionRequest));



    }


    public static String replaceAudioInfo(String jsonData, UpdateAudioInfoQuestionRequest request) {
        jsonData = JsonUtils.replaceMark(jsonData);
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonData);
            JSONObject htmlObject = jsonObject.getJSONObject("content");
            String name = htmlObject.getJSONObject("audio").getString("name");
            htmlObject.remove("audio");
            Map<String, String> audioMap = new HashMap<>();
            audioMap.put("url", request.getUrl());
            audioMap.put("name", name);
            audioMap.put("size", request.getSize());
           // Object audioJson = JsonUtils.getObjectToJson(audioMap);
            htmlObject.put("audio", audioMap);
            System.out.println("上传习题SSDB替换后的json数据:" + jsonObject.toString());
        } catch (JSONException e) {
            throw new BizLayerException("json处理异常", PraxisErrorCode.INNER_ERROR);
        }
        return jsonObject.toString();
    }

    /***
     * @param jsonData
     * @param jsonKey
     * @param dataType
     * @return
     * @throws Exception
     */
    public static Object getJsonData(String jsonData, String jsonKey, String dataType) throws Exception {
        JSONObject jsonObject = new JSONObject(jsonData);
        if (jsonObject.has(jsonKey)) {
            if ("JSONArray".equals(dataType)) {
                JSONArray jsonArray = jsonObject.getJSONArray(jsonKey);
                return jsonArray;
            } else {
                return jsonObject.get(jsonKey);
            }
        } else {
            return null;
        }
    }
}
