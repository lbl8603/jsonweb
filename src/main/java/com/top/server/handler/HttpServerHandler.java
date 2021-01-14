package com.top.server.handler;

import com.top.annotation.ioc.Component;
import com.top.annotation.ioc.Resource;
import com.top.core.web.handler.HttpRequestHandler;
import com.top.core.web.factory.HttpRequestHandlerFactory;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * @author lubeilin
 * @date 2021/1/12
 */
@Component
@ChannelHandler.Sharable
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private static final String FAVICON_ICO = "/favicon.ico";
    @Resource
    private HttpRequestHandlerFactory httpRequestHandlerFactory;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        String uri = msg.uri();
        if (uri.equals(FAVICON_ICO)) {
            return;
        }
        HttpRequestHandler httpRequestHandler = httpRequestHandlerFactory.getHandler(msg.method());
        ctx.writeAndFlush(httpRequestHandler.handle(msg)).addListener(ChannelFutureListener.CLOSE);
    }
}
