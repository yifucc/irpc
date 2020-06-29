package com.ifcc.irpc.codec.serialization;

/**
 * @author chenghaifeng
 * @date 2020-06-11
 * @description
 */
public interface Serialization {
    byte[] marshal(Object o);
    <T> T unMarshal(Class<T> clazz, byte[] data);
}
