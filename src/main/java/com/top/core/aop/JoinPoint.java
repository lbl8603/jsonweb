package com.top.core.aop;

import lombok.Data;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author lubeilin
 * @date 2021/1/11
 */
@Data
public class JoinPoint {
    private final Object targetObject;
    private final Method targetMethod;
    private final Object[] args;

    public Object proceed() throws InvocationTargetException, IllegalAccessException {
        return targetMethod.invoke(targetObject, args);
    }
}
