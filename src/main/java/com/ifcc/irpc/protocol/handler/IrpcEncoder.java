package com.ifcc.irpc.protocol.handler;

import com.ifcc.irpc.codec.serialization.Serialization;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author chenghaifeng
 * @date 2020-07-22
 * @description
 */
public class IrpcEncoder extends MessageToByteEncoder {

    private Class<?> type;
    private Serialization serialization;

    public IrpcEncoder(Class<?> type, Serialization serialization) {
        this.type = type;
        this.serialization = serialization;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        if(type != null && type.isInstance(o)) {
            byte[] bytes = serialization.marshal(o);
            byteBuf.writeInt(bytes.length);
            byteBuf.writeInt(0);
            byteBuf.writeBytes(bytes);
        }
    }
}
