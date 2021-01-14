package com.top.core.aop;

import com.top.annotation.ioc.Resource;

/**
 * @author lubeilin
 * @date 2021/1/11
 */
public abstract class AbstractAopProxyBeanPostProcessor implements BeanPostProcessor {
    @Resource
    private AspectFactory aspectFactory;

    @Override
    public Object postProcess(Object bean) {
        Object wrapperProxyBean = bean;
        //链式包装目标类
        for (Interceptor interceptor : aspectFactory.getInterceptors()) {
            if (interceptor.supports(bean)) {
                wrapperProxyBean = wrapBean(wrapperProxyBean, interceptor);
            }
        }
        return wrapperProxyBean;
    }

    /**
     * 包装bean
     *
     * @param bean        bean实例
     * @param interceptor 拦截器
     * @return 包装后的实例
     */
    public abstract Object wrapBean(Object bean, Interceptor interceptor);
}
