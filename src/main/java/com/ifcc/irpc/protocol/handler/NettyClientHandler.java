package com.ifcc.irpc.protocol.handler;

import com.ifcc.irpc.common.AsyncResponse;
import com.ifcc.irpc.common.IrpcRequest;
import com.ifcc.irpc.common.IrpcResponse;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author chenghaifeng
 * @date 2020-07-28
 * @description
 */
public class NettyClientHandler extends ChannelDuplexHandler {

    private Map<String, AsyncResponse> futureMap = new ConcurrentHashMap<>();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof IrpcResponse) {
            IrpcResponse response = (IrpcResponse)msg;
            AsyncResponse result = futureMap.get(response.getRequestId());
            result.getResponseFuture().complete(response);
            updateFutureMap(result, response.getRequestId());
        }
        super.channelRead(ctx, msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof IrpcRequest) {
            IrpcRequest request = (IrpcRequest) msg;
            futureMap.putIfAbsent(request.getRequestId(), AsyncResponse.newDefaultAsyncResponse());
        }
        super.write(ctx, msg, promise);
    }

    @Override
    public boolean isSharable() {
        return true;
    }

    public AsyncResponse getResponse(String requestId) {
        AsyncResponse response = futureMap.get(requestId);
        updateFutureMap(response, requestId);
        return response;
    }

    private void updateFutureMap(AsyncResponse response, String requestId) {
        if (response.getFutureFlag().get()) {
            futureMap.remove(requestId);
        } else {
            if (!response.getFutureFlag().compareAndSet(false , true)) {
                futureMap.remove(requestId);
            }
        }
    }
}
