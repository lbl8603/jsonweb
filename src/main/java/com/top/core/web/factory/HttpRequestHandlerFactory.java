package com.top.core.web.factory;

import com.top.annotation.ioc.Component;
import com.top.annotation.ioc.Resource;
import com.top.core.web.handler.HttpGetRequestHandler;
import com.top.core.web.handler.HttpPostRequestHandler;
import com.top.core.web.handler.HttpRequestHandler;
import com.top.exception.InitException;
import io.netty.handler.codec.http.HttpMethod;

/**
 * @author lubeilin
 * @date 2021/1/13
 */
@Component
public class HttpRequestHandlerFactory {
    @Resource
    private HttpGetRequestHandler httpGetRequestHandler;
    @Resource
    private HttpPostRequestHandler httpPostRequestHandler;

    public HttpRequestHandler getHandler(HttpMethod httpMethod) {
        if (HttpMethod.GET.equals(httpMethod)) {
            return httpGetRequestHandler;
        }
        if
        (HttpMethod.POST.equals(httpMethod)) {
            return httpPostRequestHandler;
        }
        throw new InitException("不支持的请求类型：" + httpMethod);
    }
}
