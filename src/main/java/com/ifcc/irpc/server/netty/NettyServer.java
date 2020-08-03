package com.ifcc.irpc.server.netty;

import com.ifcc.irpc.codec.serialization.Serialization;
import com.ifcc.irpc.common.IrpcRequest;
import com.ifcc.irpc.common.IrpcResponse;
import com.ifcc.irpc.common.URL;
import com.ifcc.irpc.protocol.handler.IrpcDecoder;
import com.ifcc.irpc.protocol.handler.IrpcEncoder;
import com.ifcc.irpc.protocol.handler.NettyServerHandler;
import com.ifcc.irpc.server.Server;
import com.ifcc.irpc.spi.annotation.Inject;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author chenghaifeng
 * @date 2020-07-09
 * @description
 */
@Slf4j
public class NettyServer implements Server {
    private EventLoopGroup boss;
    private EventLoopGroup worker;

    @Inject
    private Serialization serialization;

    @Override
    public void open(URL url) {
        boss = new NioEventLoopGroup(1);
        worker = new NioEventLoopGroup(10);
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        //添加遍码器
                        pipeline.addLast(new IrpcEncoder(IrpcResponse.class, serialization));
                        //添加解码器
                        pipeline.addLast(new IrpcDecoder(IrpcRequest.class, serialization));
                        pipeline.addLast(new NettyServerHandler());
                    }
                });
        bind(serverBootstrap, url.getPort());
    }

    private void bind(final ServerBootstrap serverBootstrap,int port) {
        serverBootstrap.bind(port).addListener(future -> {
            if (future.isSuccess()) {
                log.info("Port[ {} ] binding successfully",port);
            } else {
                log.error("Port[ {} ] binding failed", port);
                bind(serverBootstrap, port + 1);
            }
        });
    }
}
