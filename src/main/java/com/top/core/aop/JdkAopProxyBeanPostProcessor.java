package com.top.core.aop;

import com.top.annotation.ioc.Component;

import java.lang.reflect.Proxy;

/**
 * @author lubeilin
 * @date 2021/1/11
 */
@Component
public class JdkAopProxyBeanPostProcessor extends AbstractAopProxyBeanPostProcessor {

    @Override
    public Object wrapBean(Object bean, Interceptor interceptor) {
        return Proxy.newProxyInstance(bean.getClass().getClassLoader(), bean.getClass().getInterfaces(),
                (proxy, method, args) -> interceptor.intercept(new JoinPoint(bean, method, args)));
    }
}
