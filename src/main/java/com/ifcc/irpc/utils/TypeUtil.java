package com.ifcc.irpc.utils;

import java.lang.reflect.Constructor;

/**
 * @author chenghaifeng
 * @date 2020-08-04
 * @description
 */
public class TypeUtil {

    public static Object getValue(String val, Class clazz) {
        try {
            Constructor constructor = clazz.getConstructor(String.class);
            constructor.setAccessible(true);
            return constructor.newInstance(val);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
