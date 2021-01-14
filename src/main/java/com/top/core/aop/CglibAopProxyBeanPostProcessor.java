package com.top.core.aop;

import com.top.annotation.ioc.Component;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

/**
 * @author lubeilin
 * @date 2021/1/11
 */
@Component
public class CglibAopProxyBeanPostProcessor extends AbstractAopProxyBeanPostProcessor {

    @Override
    public Object wrapBean(Object bean, Interceptor interceptor) {
        Class<?> rootClass = bean.getClass();
        Class<?> proxySuperClass = rootClass;
        // cglib 多级代理处理
        if (bean.getClass().getName().contains("$$")) {
            proxySuperClass = rootClass.getSuperclass();
        }
        Enhancer enhancer = new Enhancer();
        enhancer.setClassLoader(bean.getClass().getClassLoader());
        enhancer.setSuperclass(proxySuperClass);
        enhancer.setCallback((MethodInterceptor) (o, method, objects, methodProxy) -> interceptor.intercept(new JoinPoint(bean, method, objects)));
        return enhancer.create();
    }
}
