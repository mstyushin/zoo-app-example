package com.mstyushin.example.application;

import com.mstyushin.example.netty.ServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RESTService implements Runnable {
    private final Integer appId;
    private final Integer httpPort;

    public RESTService(Integer appId, Integer httpPort) {

        this.appId = appId;
        this.httpPort = httpPort;
    }

    @Override
    public void run() {
        log.info("Application[" + appId + "][RESTService] - starting");
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.option(ChannelOption.SO_BACKLOG, 1024);
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ServerInitializer());

            Channel ch = b.bind(httpPort).sync().channel();
            log.info("Application[" + appId + "][RESTService] - started");

            ch.closeFuture().sync();
        } catch (InterruptedException e) {
            log.warn("Application[" + appId + "][RESTService] - interrupted");
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            log.info("Application[" + appId + "][RESTService] - stopped");
        }
    }
}
