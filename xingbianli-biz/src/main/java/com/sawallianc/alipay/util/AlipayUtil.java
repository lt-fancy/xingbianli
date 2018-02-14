package com.sawallianc.alipay.util;

import com.google.common.collect.Maps;

import java.lang.reflect.Field;
import java.util.Map;

public class AlipayUtil {

    public static Map<String,String> obj2Map(Object object){
        if(null == object){
            return null;
        }
        Map<String, String> map = Maps.newHashMap();

        Field[] declaredFields = object.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            try {
                Object obj = field.get(object);
                String name = field.getName();
                if(null != obj){
//                    if("sign".equalsIgnoreCase(name) || "sign_type".equalsIgnoreCase(name)) {
//                        continue;
//                    }
                    map.put(field.getName(),String.valueOf(obj));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }
}
