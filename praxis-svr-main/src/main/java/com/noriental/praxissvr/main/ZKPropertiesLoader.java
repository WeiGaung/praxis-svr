package com.noriental.praxissvr.main;

import com.dangdang.config.service.zookeeper.ZookeeperConfigGroup;
import com.dangdang.config.service.zookeeper.ZookeeperConfigProfile;
import com.noriental.praxissvr.exception.PraxisErrorCode;
import com.noriental.validate.exception.BizLayerException;

import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

/**
 * Created by dell on 2015/12/14.
 */
public class ZKPropertiesLoader {

    /***
     * 系统 提前 加载配置文件 到系统变量里面
     * @param rootNode
     */
    public static void load(String rootNode){
        System.setProperty("projectName","praxis-svr");

        Properties properties = new Properties();
        ZookeeperConfigGroup group1 = null;
        ZookeeperConfigGroup group2 = null;
        try {
            Object e = ZKPropertiesLoader.class.getClassLoader().getResourceAsStream("zk.properties");
            properties.load((InputStream)e);
            String zkAddress = properties.getProperty("zk.address");
            String zkVersion = properties.getProperty("zk.version");
            ZookeeperConfigProfile zookeeperConfigProfile = new ZookeeperConfigProfile(zkAddress, rootNode, zkVersion);
            group1 = new ZookeeperConfigGroup(zookeeperConfigProfile, "change");
            group2 = new ZookeeperConfigGroup(zookeeperConfigProfile, "unchange");
            for(Map.Entry<String,String> entry:group1.exportProperties().entrySet())
            {
                System.setProperty(entry.getKey(),entry.getValue());
            }
            for(Map.Entry<String,String> entry:group2.exportProperties().entrySet())
            {
                System.setProperty(entry.getKey(),entry.getValue());
            }
        } catch (Exception var2) {
            System.out.println("zkCLient: 未找到zk.properties配置文件");
            var2.printStackTrace();
            throw new BizLayerException("", PraxisErrorCode.INNER_ERROR);
        } finally {
            if (group1 != null) {
                group1.close();
            }
            if (group2 != null) {
                group2.close();
            }
        }

    }

}
