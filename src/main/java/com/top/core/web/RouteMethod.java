package com.top.core.web;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author lubeilin
 * @date 2021/1/13
 */
@Data
@AllArgsConstructor
public class RouteMethod {
    private String url;
    private Method method;
    private Object bean;
    private String[] params;
    private int body;

    public Object invoke(Object[] args) throws InvocationTargetException, IllegalAccessException {
        return method.invoke(bean, args);
    }

    public Object invoke() throws InvocationTargetException, IllegalAccessException {
        return method.invoke(bean);
    }
}
