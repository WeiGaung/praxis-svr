package com.noriental;

import com.dangdang.config.service.GeneralConfigGroup;
import com.dangdang.config.service.zookeeper.ZookeeperConfigGroup;
import com.dangdang.config.service.zookeeper.ZookeeperConfigProfile;

/**
 * @author chenlihua
 * @date 2015/12/14
 * @time 13:11
 */
public class ToolKitTest {
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    public static void main(String[] args) {
       /* ZookeeperConfigProfile configProfile = new ZookeeperConfigProfile("10.60.0.63:2181", "/xdfapp/praxis-svr", "v1.0");
        GeneralConfigGroup propertyGroup1 = new ZookeeperConfigGroup(configProfile, "unchange");

        String stringProperty = propertyGroup1.get("ds.druid.url");
        System.out.println("stringProperty = " + stringProperty);*/
       try{
           int a=7/0;
       }finally {
           System.out.println("finally已经执行");
       }



    }
}
