package com.noriental.praxissvr.statis.util;

/**
 * @author kate
 * @create 2017-11-13 17:19
 * @desc 教师空间错题数据表根据学校ID路由
 **/
public class DataWrongTableRoute {

    /**
     * 根据学校ID获取要路由的表名
     * @param schoolId
     * @return
     */
    public static String getTableName(Long schoolId){
        String tableName;
        String prefixName="entity_data_wrong_ques_";
        Long afterId=schoolId % 100;
        tableName=prefixName+afterId;
        return tableName;
    }

}
