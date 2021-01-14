package com.top.core.web.handler;

import com.top.annotation.ioc.Component;
import com.top.annotation.ioc.Resource;
import com.top.core.web.RouteMethod;
import com.top.core.web.RouteMethodMapper;
import com.top.utils.ObjectUtil;
import com.top.utils.ResponseUtil;
import com.top.utils.UrlUtil;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;

import java.util.Map;

/**
 * @author lubeilin
 * @date 2021/1/13
 */
@Component
public class HttpGetRequestHandler implements HttpRequestHandler {

    @Resource
    private RouteMethodMapper routeMethodMapper;

    @Override
    public FullHttpResponse handle(FullHttpRequest request) {
        try {
            String requestUri = request.uri();
            Map<String, String> queryParameterMappings = UrlUtil.getQueryParams(requestUri);
            String requestPath = UrlUtil.getRequestPath(requestUri);
            RouteMethod routeMethod = routeMethodMapper.getRoute(HttpMethod.GET, requestPath);
            if (routeMethod == null) {
                return ResponseUtil.notFound();
            }
            String[] params = routeMethod.getParams();
            Object[] args = new Object[params.length];
            Class<?>[] paramsType = routeMethod.getMethod().getParameterTypes();
            for (int index = 0; index < args.length; index++) {
                args[index] = ObjectUtil.convert(paramsType[index], queryParameterMappings.get(params[index]));
            }
            return ResponseUtil.success(routeMethod.invoke(args));
        } catch (Throwable e) {
            e.printStackTrace();
            return ResponseUtil.error();
        }
    }


}
