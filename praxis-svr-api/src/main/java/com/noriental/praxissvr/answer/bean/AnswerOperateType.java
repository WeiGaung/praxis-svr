package com.noriental.praxissvr.answer.bean;

/**
 * Created by gaobin on 2018/8/27.
 */
public enum AnswerOperateType {
    query,      //get   查询
    correct,     //post     批改
    query_result,   //get(透传数据)
    postil,         //post      批注

    submit_answer,   //学生提交做答
    machine_correct,     //老师学生批改
    human_correct,      //智能批改结果自动生效
    post_postil,        //老师批注


}
