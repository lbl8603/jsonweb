package com.top.core.aop;

import com.top.annotation.ioc.Component;
import com.top.annotation.ioc.Resource;

/**
 * @author lubeilin
 * @date 2021/1/11
 */
@Component
public class AopProxyFactory {
    @Resource
    private JdkAopProxyBeanPostProcessor jdkAopProxyBeanPostProcessor;
    @Resource
    private CglibAopProxyBeanPostProcessor cglibAopProxyBeanPostProcessor;

    public BeanPostProcessor getBeanPostProcessor(Class<?> beanClass) {
        if (beanClass.isInterface()) {
            return jdkAopProxyBeanPostProcessor;
        } else {
            return cglibAopProxyBeanPostProcessor;
        }
    }
}
