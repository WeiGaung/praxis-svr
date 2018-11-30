package com.noriental.praxissvr.wrongQuestion.serviceImpl;

import com.noriental.praxissvr.Person;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by kate on 2017/2/9.
 */
public class ReflexTest {


    public static void main(String[] args) throws Exception{

        /*Field field_name=Person.class.getDeclaredField("name");
        System.out.println(field_name.getName());
        Field[] field = Person.class.getClass().getDeclaredFields(); // 获取实体类的所有属性，返回Field数组
        try {
            for (int j = 0; j < field.length; j++) { // 遍历所有属性
                String name = field[j].getName(); // 获取属性的名字
                name = name.substring(0, 1).toUpperCase() + name.substring(1); // 将属性的首字符大写，方便构造get，set方法
                String type = field[j].getGenericType().toString(); // 获取属性的类型
                if (type.equals("class java.lang.String")) { // 如果type是类类型，则前面包含"class "，后面跟类名
                    Method m = Person.class.getClass().getMethod("get" + name);
                    String value = (String) m.invoke(Person.class); // 调用getter方法获取属性值
                    if (value == null) {
                        m = Person.class.getClass().getMethod("set"+name,String.class);
                        m.invoke(Person.class, "");
                    }
                }
                if (type.equals("class java.lang.Integer")) {
                    Method m = Person.class.getClass().getMethod("get" + name);
                    Integer value = (Integer) m.invoke(Person.class);
                    if (value == null) {
                        m = Person.class.getClass().getMethod("set"+name,Integer.class);
                        m.invoke(Person.class, 0);
                    }
                }
                if (type.equals("class java.lang.Boolean")) {
                    Method m = Person.class.getClass().getMethod("get" + name);
                    Boolean value = (Boolean) m.invoke(Person.class);
                    if (value == null) {
                        m = Person.class.getClass().getMethod("set"+name,Boolean.class);
                        m.invoke(Person.class, false);
                    }
                }
                if (type.equals("class java.util.Date")) {
                    Method m = Person.class.getClass().getMethod("get" + name);
                    Date value = (Date) m.invoke(Person.class);
                    if (value == null) {
                        m = Person.class.getClass().getMethod("set"+name,Date.class);
                        m.invoke(Person.class, new Date());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        System.out.println(getdate(-30));

    }

    public static Date getdate(int i) // //获取前后日期 i为正数 向后推迟i天，负数时向前提前i天
    {
        Date dat = null;
        Calendar cd = Calendar.getInstance();
        cd.add(Calendar.DATE, i);
        dat = cd.getTime();
        SimpleDateFormat dformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Timestamp date = Timestamp.valueOf(dformat.format(dat));
        return date;
    }

}
