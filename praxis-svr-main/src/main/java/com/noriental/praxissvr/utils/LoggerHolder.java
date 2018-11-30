package com.noriental.praxissvr.utils;

import com.noriental.praxissvr.brush.bean.StudentWork;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by shengxian on 2016/12/19.
 */
public class LoggerHolder {
    private static  ThreadLocal<StudentWork> commonParam = new ThreadLocal<>();

    public static void set(StudentWork studentWork) {
        commonParam.set(studentWork);
    }

    public static StudentWork get() {
        return commonParam.get();
    }

    public static String getBrushLogger(String sex_) {
        if(StringUtils.isBlank(sex_)){
            return "";
        }else if(sex_.length()>3 && "se5".equals(sex_.substring(0,3))){
            String info = "";
            if(commonParam.get()!=null){
                StudentWork studentWork = commonParam.get();
                Integer resourceType = studentWork.getResourceType();
                Integer workLevel = studentWork.getWorkLevel();
                Long levelId = studentWork.getLevelId();
                info+=","+String.valueOf(resourceType)+"_"+String.valueOf(workLevel)+"_"+String.valueOf(levelId);
            }
            return info;
        }else{
            return "";
        }
    }

    public static void clear(){
        commonParam.remove();
    }
}
