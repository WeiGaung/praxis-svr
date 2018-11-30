package com.noriental.praxissvr.answer.util;

import org.apache.commons.lang3.StringUtils;

/**
 * @author liuhuapeng
 * @date 2016年5月28日16:56:56
 */
public class AppException extends Throwable {
    private AppReturnCode error;

    public AppException(String message, AppReturnCode error) {
        super(message);
        if (error == null) {
            throw new IllegalArgumentException("OkHttpErrorCodes is null.");
        }
        this.error = error;
    }
    
    public AppReturnCode getError() {
        return error;
    }

    public String getMsg() {
        if (StringUtils.isBlank(this.getMessage())) {
            return error.getComment();
        }
        return String.format("%s: %s", error.getComment(), this.getMessage());
    }
    public String getAppReturnMsg(){
        return error.getComment();
    }
}
