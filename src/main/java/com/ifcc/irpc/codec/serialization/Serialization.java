package com.ifcc.irpc.codec.serialization;

import com.ifcc.irpc.spi.annotation.SPI;

/**
 * @author chenghaifeng
 * @date 2020-06-11
 * @description
 */
@SPI("protocol")
public interface Serialization {
    byte[] marshal(Object o);
    <T> T unMarshal(Class<T> clazz, byte[] data);
}
