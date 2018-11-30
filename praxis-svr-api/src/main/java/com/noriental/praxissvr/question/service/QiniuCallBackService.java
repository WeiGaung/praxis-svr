package com.noriental.praxissvr.question.service;

import com.noriental.validate.bean.CommonDes;
import com.noriental.validate.exception.BizLayerException;
import java.util.Map;


/**
 * 七牛音频服务
 * Created by luozukai on 2016/11/18.
 */
public interface QiniuCallBackService {

    /**
     * 音频转码回调服务
     * @param
     * @return
     * @throws BizLayerException
     */
    CommonDes convertMp3CallBack(String requestUrl,String json)throws BizLayerException;
}
