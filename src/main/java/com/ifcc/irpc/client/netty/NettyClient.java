package com.ifcc.irpc.client.netty;

import com.ifcc.irpc.client.Client;
import com.ifcc.irpc.codec.serialization.Serialization;
import com.ifcc.irpc.common.AsyncResponse;
import com.ifcc.irpc.common.Invocation;
import com.ifcc.irpc.common.IrpcRequest;
import com.ifcc.irpc.common.IrpcResponse;
import com.ifcc.irpc.common.URL;
import com.ifcc.irpc.protocol.handler.IrpcDecoder;
import com.ifcc.irpc.protocol.handler.IrpcEncoder;
import com.ifcc.irpc.protocol.handler.NettyClientHandler;
import com.ifcc.irpc.spi.annotation.Inject;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author chenghaifeng
 * @date 2020-07-09
 * @description
 */
@Slf4j
public class NettyClient implements Client {

    private EventLoopGroup eventLoopGroup;
    private Channel channel;
    private static final int MAX_RETRY = 5;

    @Inject
    private Serialization serialization;

    private NettyClientHandler handler;

    public NettyClient() {
        this.handler = new NettyClientHandler();
    }

    @Override
    public void connect(URL url) {
        eventLoopGroup = new NioEventLoopGroup(1);
        //启动类
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                //指定传输使用的Channel
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        //添加编码器
                        pipeline.addLast(new IrpcEncoder(IrpcRequest.class, serialization));
                        //添加解码器
                        pipeline.addLast(new IrpcDecoder(IrpcResponse.class, serialization));
                        pipeline.addLast(handler);
                    }
                });
        connectInner(bootstrap, url, MAX_RETRY);
    }

    private void connectInner(Bootstrap bootstrap, URL url, int retry) {
        ChannelFuture channelFuture = bootstrap.connect(url.getHost(), url.getPort()).addListener(future -> {
            if (future.isSuccess()) {
                log.info("Connect server successfully");
            } else if (retry == 0) {
                log.error("重试次数已用完，放弃连接");
            } else {
                //第几次重连：
                int order = (MAX_RETRY - retry) + 1;
                //本次重连的间隔
                int delay = 1 << order;
                log.error("{} : 连接失败，第 {} 重连...", new Date(), order);
                bootstrap.config().group().schedule(() -> {
                    try {
                        connectInner(bootstrap, url, retry - 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, delay, TimeUnit.SECONDS);
            }
        });
        try {
            channel = channelFuture.sync().channel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public AsyncResponse send(final Invocation invocation) {
        try {
            String requestId = RandomStringUtils.randomAlphanumeric(10);
            channel.writeAndFlush(invocation).await();
            return handler.getResponse(requestId);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

}
