package com.top.core.web.handler;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * 请求事件处理
 *
 * @author lubeilin
 * @date 2021/1/13
 */
public interface HttpRequestHandler {
    /**
     * 请求事件
     *
     * @param request 请求
     * @return 响应
     */
    FullHttpResponse handle(FullHttpRequest request);
}
