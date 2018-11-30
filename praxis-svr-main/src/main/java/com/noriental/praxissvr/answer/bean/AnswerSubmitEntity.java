package com.noriental.praxissvr.answer.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @author kate
 * @create 2017-12-21 16:00
 * @desc 学生做答消息实体
 **/
public class AnswerSubmitEntity implements Serializable {
   private  String traceKey;
   private List<StudentExercise> list;
   private OperateType operateType;

    public AnswerSubmitEntity(String traceKey, List<StudentExercise> list,OperateType operateType) {
        this.traceKey = traceKey;
        this.list = list;
        this.operateType=operateType;
    }

    public String getTraceKey() {
        return traceKey;
    }

    public void setTraceKey(String traceKey) {
        this.traceKey = traceKey;
    }

    public List<StudentExercise> getList() {
        return list;
    }

    public void setList(List<StudentExercise> list) {
        this.list = list;
    }

    public OperateType getOperateType() {
        return operateType;
    }

    public void setOperateType(OperateType operateType) {
        this.operateType = operateType;
    }
}
