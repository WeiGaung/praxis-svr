package com.noriental.praxissvr.wrongQuestion.util;

import com.noriental.praxissvr.exception.PraxisErrorCode;
import com.noriental.validate.exception.BizLayerException;

import java.text.SimpleDateFormat;

/**
 * Created by kate on 2016/12/29.
 */
public class DateUtil {
    public static Long  getLongDate(String date) throws BizLayerException{
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (null != date) {
            try {
                long timeStart = sdf.parse(date).getTime();
                return timeStart;
            } catch (Exception e) {
                e.printStackTrace();
                throw new BizLayerException("", PraxisErrorCode.GET_DATE_FAIL);

            }
        }
        return null;
    }
}
