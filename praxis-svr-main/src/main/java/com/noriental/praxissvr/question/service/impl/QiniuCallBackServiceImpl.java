package com.noriental.praxissvr.question.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noriental.extresources.request.GetFileInfoRequest;
import com.noriental.extresources.service.QiniuVoiceService;
import com.noriental.extresources.vo.QiniuFileInfoVo;
import com.noriental.praxissvr.exception.PraxisErrorCode;
import com.noriental.praxissvr.question.bean.ConvertCallBack;
import com.noriental.praxissvr.question.request.UpdateAudioInfoQuestionRequest;
import com.noriental.praxissvr.question.service.QiniuCallBackService;
import com.noriental.praxissvr.question.service.QuestionService;
import com.noriental.utils.json.JsonUtil;
import com.noriental.validate.bean.CommonDes;
import com.noriental.validate.bean.CommonResponse;
import com.noriental.validate.exception.BizLayerException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by luozukai on 2016/11/25.
 */
@Service("qiniuCallBackService")
public class QiniuCallBackServiceImpl implements QiniuCallBackService {
    private static final Logger logger = LoggerFactory.getLogger(QiniuCallBackServiceImpl.class);
    @Autowired
    private QuestionService questionService;

    @Resource
    private QiniuVoiceService qiniuVoiceService;

    // 七牛文件外链地址
    private static final String httpUrl = "http://ra.okjiaoyu.cn/";

    /**
     * 七牛音频转换回调服务
     * (目前只支持转一个)
     * @param
     * @return
     * @throws BizLayerException
     */
    @Override
    public CommonDes convertMp3CallBack(String requestUrl,String json) throws BizLayerException {
        logger.info("七牛回调,入参requestUrl：{}，json:{}",requestUrl,json);
        long entityId = 0;
        /**
         * entityId获取方式
         */
        if(StringUtils.isNotEmpty(requestUrl)){
            //将截取到的url内的参数放在Map中保存
            Map<String,Object> prameMap = new HashMap<>();
            //截取url内的参数
            boolean isHas = requestUrl.contains("&");
            if(isHas){
                String[] strings = requestUrl.split("&");
                for (String string : strings) {
                    String[] split = string.split("=");
                    prameMap.put(split[0],split[1]);
                }

            }

            //entityId=(long)prameMap.get("entityId");
            logger.info("=======entityId={}",prameMap.get("entityId"));
            entityId=Long.parseLong(prameMap.get("entityId")+"");
        }



        String jsonStatusDescription = json;

        logger.info("截取的请求实体entityId={}",entityId);
        ConvertCallBack convertCallBack;
        try {
            convertCallBack = new ObjectMapper().readValue(jsonStatusDescription, ConvertCallBack.class);
        } catch (IOException e) {
            logger.error("json解析异常",e);
            throw new BizLayerException("", PraxisErrorCode.JSON_CONVERT_FAIL);
        }

        logger.info("json对象转换结果：{}",JsonUtil.obj2Json(convertCallBack));

        if(convertCallBack.getCode() == 0){
            String fileName = convertCallBack.getItems().get(0).getKey();//(目前只支持转一个)

            // 调用七牛服务，查询文件大小
            GetFileInfoRequest fileRequest = new GetFileInfoRequest();
            fileRequest.setFileName(fileName);
            CommonResponse<QiniuFileInfoVo> cr = qiniuVoiceService.getFileInfo(fileRequest);

            logger.info("七牛回调,查询文件返回：{}",cr);
            logger.info("七牛回调,查询文件返回文件名：{}，查询文件大小：{}",fileName,cr.getData().getSize());

            // 修改数据库audioUrl和ssdb
            UpdateAudioInfoQuestionRequest updateRequest = new UpdateAudioInfoQuestionRequest();
            updateRequest.setId(entityId);
            updateRequest.setName(fileName);
            updateRequest.setUrl(httpUrl + fileName);
            updateRequest.setSize(cr.getData().getSize());

            questionService.updateAudioInfoQuestion(updateRequest);
        }else{
            logger.error("七牛转码失败,"+convertCallBack.getDesc());
        }
        return new CommonDes();
    }

    public static void main(String[] args) {
//        String url = "http://111.203.233.66:2012/adapter/covertMp3Callback?entityId=4001096";
        String param = "{\"id\":\"z0.5866319d45a2650cfd9b067f\",\"pipeline\":\"1380380457.transcoding\",\"code\":0,\"desc\":\"The fop was completed successfully\",\"reqid\":\"HQQAAMBzlPypAJUU\",\"inputBucket\":\"fs-ra\",\"inputKey\":\"ACC-105.aac\",\"items\":[{\"cmd\":\"avthumb/mp3|saveas/ZnMtcmE6QUNDLTEwNS5tcDM=\",\"code\":0,\"desc\":\"The fop was completed successfully\",\"hash\":\"lgq8uC5k0h9-mX4834zBav9grqg5\",\"key\":\"ACC-105.mp3\",\"returnOld\":0}]}";
        //System.out.println("=========="+sendPost(url,param));
        try {
            ConvertCallBack  convertCallBack = new ObjectMapper().readValue(param, ConvertCallBack.class);
            logger.info(""+JsonUtil.obj2Json(convertCallBack));
        } catch (IOException e) {
            logger.error("json解析异常",e);
            throw new BizLayerException("", PraxisErrorCode.JSON_CONVERT_FAIL);
        }
    }


    public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        StringBuilder result = new StringBuilder();
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append( line);
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result.toString();
    }
}
