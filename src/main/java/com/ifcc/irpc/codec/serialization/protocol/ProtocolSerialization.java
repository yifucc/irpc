package com.ifcc.irpc.codec.serialization.protocol;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.ifcc.irpc.codec.serialization.Serialization;
import com.ifcc.irpc.common.IrpcRequest;
import com.ifcc.irpc.common.IrpcResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;

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
