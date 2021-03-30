package com.douyuehan.doubao.utils;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class BeanUtil {

    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) throws Exception {
    }


    public static <E> Object reflectObj(Class<E> c, Map<String, Object> map) throws Exception {

        Class classType = Class.forName(c.getName());
        Method[] methodArray = classType.getDeclaredMethods();
        E e = c.newInstance();
        int j = 0;
        for (int i = 0; i < methodArray.length; i++) {
            if ((methodArray[i].getName().substring(0, 3).equalsIgnoreCase("set"))) {
                Class[] type = methodArray[i].getParameterTypes();
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    if (methodArray[i].getName().equals(changeFieldToSetter(entry.getKey()))) {
                        System.out.println(type[0].getName());
                        if (type[0].getName().equals("java.lang.String")) {
                            methodArray[i].invoke(e, entry.getValue());
                        }
                        if (type[0].getName().equals("int")) {
                            methodArray[i].invoke(e, Integer.parseInt(entry.getValue().toString()));
                        }
                        if (type[0].getName().equals("java.lang.Integer")) {
                            methodArray[i].invoke(e, Integer.parseInt(entry.getValue().toString()));
                        }
                        if (type[0].getName().equals("java.util.Date")) {
                            methodArray[i].invoke(e, new Date(entry.getValue().toString()));
                        }
                        break;
                    }
                }
            }
        }
        return e;
    }


    /**
     * @Param:field字段名
     * @Param:newObj 类
     * @Param:entityClass 类类型
     * @Description:
     */
    public static String getValueByField(String fieldName, Object newObj, Class<?> entityClass) {
        String methodName = changeFieldToGetter(fieldName);
        try {
            Method method = entityClass.getMethod(methodName);
            if (method == null) {
                methodName = changeFieldToIs(fieldName);
                method = entityClass.getMethod(methodName);
            }
            if (method == null) {
                return null;
            }
            Object obj = method.invoke(newObj);
            if (obj == null) {
                return null;
            }

            if (obj instanceof Date) {
                return sdf.format((Date) obj);
            }
            return obj.toString();

        } catch (Exception e) {
            log.error("\n getValueByField error methodName:{}", methodName);
            e.printStackTrace();
        }


        return null;
    }

    /**
     * @param field
     * @Description: 根据字段获取get方法
     */
    private static String changeFieldToGetter(String field) {
        return "get" + field.substring(0, 1).toUpperCase() + field.substring(1);
    }

    private static String changeFieldToSetter(String field) {
        return "set" + field.substring(0, 1).toUpperCase() + field.substring(1);
    }

    /**
     * @param field
     * @Description: 根据字段获取is方法
     */
    private static String changeFieldToIs(String field) {
        return "is" + field.substring(0, 1).toUpperCase() + field.substring(1);
    }
}
