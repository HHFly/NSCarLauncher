package com.example.dell.nscarlauncher.common.util;

import android.util.ArrayMap;

import com.example.dell.nscarlauncher.base.model.BaseModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

/**
 * 功能：map工具类
 */

public class MapUtils {
    /**
     * 将实体类转化为map
     *
     * @param o
     * @return
     */
    public static Map<String, String> getMap(Object o) {
        Map<String, String> map = new ArrayMap<>();
        try {
            Field[] fields = o.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                String proName = field.getName();
                Object proValue = field.get(o);
                if (proValue != null) {
                    if (proValue instanceof BaseModel) {
                        Gson gson = new GsonBuilder().create();
                        String json = gson.toJson(proValue);
                        map.put(proName, json);
                    } else {
                        map.put(proName, proValue + "");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 将map转换为实体类
     *
     * @param map
     * @param o
     * @return
     * @throws Exception
     */
    public static Object getModel(Map<String, Object> map, Object o) throws Exception {
        if (!map.isEmpty()) {
            for (String k : map.keySet()) {
                Object v = "";
                if (!k.isEmpty()) {
                    v = map.get(k);
                }
                Field[] fields = o.getClass().getDeclaredFields();
                for (Field field : fields) {
                    int mod = field.getModifiers();
                    if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                        continue;
                    }
                    if (field.getName().toUpperCase().equals(k)) {
                        field.setAccessible(true);
                        field.set(o, v);
                    }

                }
            }
        }
        return o;
    }
}
