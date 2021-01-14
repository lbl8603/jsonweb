package com.top.core.web;

import com.top.annotation.ioc.Component;
import com.top.annotation.web.*;
import com.top.exception.InitException;
import com.top.utils.MethodVariableNameUtil;
import io.netty.handler.codec.http.HttpMethod;
import javassist.NotFoundException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lubeilin
 * @date 2021/1/13
 */
@Component
public class RouteMethodMapper {
    private final Map<HttpMethod, Map<String, RouteMethod>> requestMethodMap = new HashMap<>(4);

    public void loadRoutes(Class<?> aClass, Object bean) throws NotFoundException {
        RestController restController = aClass.getAnnotation(RestController.class);
        Method[] methods = aClass.getDeclaredMethods();
        String baseUrl = restController.value();
        for (Method method : methods) {
            if (method.isAnnotationPresent(GetMapping.class)) {
                GetMapping getMapping = method.getAnnotation(GetMapping.class);
                if (getMapping != null) {
                    mapUrlToMethod(HttpMethod.GET, baseUrl + getMapping.value(), method, bean);
                }
            }
            if (method.isAnnotationPresent(PostMapping.class)) {
                PostMapping postMapping = method.getAnnotation(PostMapping.class);
                if (postMapping != null) {
                    mapUrlToMethod(HttpMethod.POST, baseUrl + postMapping.value(), method, bean);
                }
            }
        }
    }

    public RouteMethod getRoute(HttpMethod httpMethod, String url) {
        Map<String, RouteMethod> urlToMethodMap = requestMethodMap.get(httpMethod);
        return urlToMethodMap == null ? null : urlToMethodMap.get(url);
    }

    private void mapUrlToMethod(HttpMethod httpMethod, String url, Method method, Object bean) throws NotFoundException {
        Map<String, RouteMethod> urlToMethodMap = requestMethodMap.computeIfAbsent(httpMethod, e -> new HashMap<>(32));
        if (urlToMethodMap.containsKey(url)) {
            throw new InitException(String.format("url重复: %s", url));
        }
        int i = 0;
        int bodyNum = -1;
        String[] params = MethodVariableNameUtil.getMethodParameterNames(bean.getClass(), method);
        for (Annotation[] annotations : method.getParameterAnnotations()) {
            for (Annotation annotation : annotations) {
                if (annotation.annotationType() == RequestParam.class) {
                    params[i] = ((RequestParam) annotation).value();
                } else if (annotation.annotationType() == RequestBody.class) {
                    if (bodyNum != -1) {
                        throw new InitException(bean.getClass() + " method " + method.getName() + "只能有一个@RequestBody注解的参数");
                    }
                    bodyNum = i;
                }
            }
            i++;
        }
        RouteMethod routeMethod = new RouteMethod(url, method, bean, params, bodyNum);
        urlToMethodMap.put(url, routeMethod);
    }

}
