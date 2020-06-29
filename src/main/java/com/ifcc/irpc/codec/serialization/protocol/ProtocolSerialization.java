package com.ifcc.irpc.codec.serialization.protocol;

import com.ifcc.irpc.codec.serialization.Serialization;

/**
 * @author chenghaifeng
 * @date 2020-06-11
 * @description
 */
public class ProtocolSerialization implements Serialization {
    @Override
    public byte[] marshal(Object o) {
        return new byte[0];
    }

    @Override
    public <T> T unMarshal(Class<T> clazz, byte[] data) {
        return null;
    }
}
