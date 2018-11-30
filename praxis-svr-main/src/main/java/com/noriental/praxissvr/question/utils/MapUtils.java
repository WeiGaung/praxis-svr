package com.noriental.praxissvr.question.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by hushuang on 2017/6/16.
 */
public class MapUtils {

    public static Map<String, Object> mapRemoveWithNullByRecursion(Map<String, Object> map){

        Set<Map.Entry<String, Object>> set = map.entrySet();

        Iterator<Map.Entry<String, Object>> it = set.iterator();

        Map map2 = new HashMap();

        while(it.hasNext()){
            Map.Entry<String, Object> en = it.next();
            if(!(en.getValue() instanceof Map)){
                if(null == en.getValue() || "".equals(en.getValue())){
                    it.remove();
                }
            }else{
                map2 = (Map) en.getValue();
                mapRemoveWithNullByRecursion(map2);
            }
        }

        return map;
    }


    public static void main(String[] args){


        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> map2 = new HashMap<String, Object>();
        Map<String, Object> map3 = new HashMap<String, Object>();
        Map<Integer, Object> map4 = new HashMap<Integer, Object>();
        String str = null ;
        String str2 = "str2";
        map.put("str", str);
        map.put("str2", str2);
        map.put("b", "map1");
        map.put("a", null);
        map.put("aa", null);

        map.put("c", map2);
        map2.put("11", "map2");
        map2.put("12", null);

        map2.put("d", map3);
        map3.put("111", "map3");
        map3.put("121", null);
        map3.put("", null);
        map3.put(null, null);

        map4.put(1234, "map2value");
        map4.put(12345, null);
        map4.put(5432, "");
        map3.put("map4", map4);
        System.out.println(map);

        System.out.println(mapRemoveWithNullByRecursion(map));





    }


}
