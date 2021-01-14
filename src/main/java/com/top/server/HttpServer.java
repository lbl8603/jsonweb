package com.top.server;

import com.top.annotation.ioc.Component;
import com.top.annotation.ioc.PostConstruct;
import com.top.annotation.ioc.Resource;
import com.top.annotation.ioc.Value;
import com.top.exception.InitException;
import com.top.server.handler.HttpServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lubeilin
 * @date 2021/1/7
 */
@Component
@Slf4j
public class HttpServer {
    @Resource
    private HttpServerHandler httpServerHandler;
    @Value("web.port")
    private int port;

    @PostConstruct
    public void postConstruct() {
        start(port);
    }

    public void start(int port) {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline()
                                    .addLast("decoder", new HttpRequestDecoder())
                                    .addLast("encoder", new HttpResponseEncoder())
                                    .addLast("aggregator", new HttpObjectAggregator(512 * 1024))
                                    .addLast("handler", httpServerHandler);
                        }
                    });
            b.bind(port).sync();
        } catch (InterruptedException e) {
            throw new InitException(e.getMessage(), e);
        }
        log.info("启动：" + port);
    }

}
