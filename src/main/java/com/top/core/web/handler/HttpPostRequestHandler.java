package com.top.core.web.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author lubeilin
 * @date 2021/1/13
 */
@Component
public class HttpPostRequestHandler implements HttpRequestHandler {
    @Resource
    private RouteMethodMapper routeMethodMapper;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public FullHttpResponse handle(FullHttpRequest request) {
        try {
            String requestUri = request.uri();
            Map<String, String> queryParameterMappings = UrlUtil.getQueryParams(requestUri);
            String requestPath = UrlUtil.getRequestPath(requestUri);
            RouteMethod routeMethod = routeMethodMapper.getRoute(HttpMethod.POST, requestPath);
            if (routeMethod == null) {
                return ResponseUtil.notFound();
            }
            Object rs;
            if (routeMethod.getMethod().getParameterCount() == 0) {
                rs = routeMethod.invoke();
            } else {
                String[] params = routeMethod.getParams();
                Object[] args = new Object[params.length];
                Class<?>[] paramsType = routeMethod.getMethod().getParameterTypes();
                for (int index = 0; index < args.length; index++) {
                    if (index == routeMethod.getBody()) {
                        String json = request.content().toString(StandardCharsets.UTF_8);
                        if(paramsType[index]==String.class){
                            args[index] = json;
                        }else {
                            args[index] = objectMapper.readValue(json, paramsType[index]);
                        }
                    } else {
                        args[index] = ObjectUtil.convert(paramsType[index], queryParameterMappings.get(params[index]));
                    }
                }
                rs = routeMethod.invoke(args);
            }
            return ResponseUtil.success(rs);
        } catch (Throwable e) {
            e.printStackTrace();
            return ResponseUtil.error();
        }

    }
}
