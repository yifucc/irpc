package com.ifcc.irpc.protocol.handler;

import com.ifcc.irpc.codec.serialization.Serialization;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author chenghaifeng
 * @date 2020-07-22
 * @description
 */
public class IrpcDecoder extends ByteToMessageDecoder {

    private Serialization serialization;

    private Class<?> type;

    public IrpcDecoder(Class<?> type, Serialization serialization) {
        this.type = type;
        this.serialization = serialization;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf.readableBytes() < 9) {
            return;
        }
        byteBuf.markReaderIndex();
        // 魔数
        byte magic = byteBuf.readByte();
        // 版本
        byte version = byteBuf.readByte();
        // 消息类型
        byte msgType = byteBuf.readByte();
        // 请求类型
        byte reqType = byteBuf.readByte();
        // 压缩类型
        byte compressType = byteBuf.readByte();
        // 请求id
        long requestId = byteBuf.readLong();
        // 长度
        int length = byteBuf.readInt();
        // 保留字段
        int reserved = byteBuf.readInt();
        System.out.println(magic);
        System.out.println(version);
        System.out.println(msgType);
        System.out.println(reqType);
        System.out.println(compressType);
        System.out.println(requestId);
        System.out.println(length);
        System.out.println(reserved);
    }
}
