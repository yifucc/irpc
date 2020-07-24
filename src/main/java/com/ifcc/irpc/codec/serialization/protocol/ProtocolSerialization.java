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
        if (o == null) {
            return new byte[0];
        }
        ByteArrayOutputStream out = null;
        ObjectOutputStream outputStream = null;
        try {
            out = new ByteArrayOutputStream();
            outputStream = new ObjectOutputStream(out);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (o instanceof IrpcRequest) {
            IrpcRequest request = (IrpcRequest) o;
            try {
                outputStream.writeObject(request.getPayload());
                byte[] payload = out.toByteArray();
                IrpcProtocol.Request.Builder builder = IrpcProtocol.Request.newBuilder();
                builder.setPayload(ByteString.copyFrom(payload));
                builder.setServicePath(request.getService());
                if (request.getMetaData() != null) {
                    for (Map.Entry entry : request.getMetaData().entrySet()) {
                        builder.putMetadata(entry.getKey().toString(), ByteString.copyFromUtf8(entry.getValue().toString()));
                    }
                }
                return builder.build().toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (o instanceof IrpcResponse){
            IrpcResponse response = (IrpcResponse) o;
            try {
                outputStream.writeObject(response.getPayload());
                byte[] payload = out.toByteArray();
                IrpcProtocol.Response.Builder builder = IrpcProtocol.Response.newBuilder();
                builder.setPayload(ByteString.copyFrom(payload));
                builder.setCode(response.getCode());
                builder.setMsg(response.getMessage());
                if (response.getMetaData() != null) {
                    for (Map.Entry entry : response.getMetaData().entrySet()) {
                        builder.putMetadata(entry.getKey().toString(), ByteString.copyFromUtf8(entry.getValue().toString()));
                    }
                }
                return builder.build().toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new byte[0];
    }

    @Override
    public <T> T unMarshal(Class<T> clazz, byte[] data) {
        if (data == null || data.length == 0 || clazz == null) {
            return null;
        }
        if (IrpcRequest.class.equals(clazz)) {
            try {
                IrpcRequest irpcRequest = new IrpcRequest();
                IrpcProtocol.Request request = IrpcProtocol.Request.parseFrom(data);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        } else if (IrpcResponse.class.equals(clazz)) {

        }
        return null;
    }
}
