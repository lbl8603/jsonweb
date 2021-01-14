package com.top.core.aop;

import com.top.annotation.aop.Aspect;
import com.top.annotation.ioc.Component;
import com.top.core.ioc.BeanManager;
import com.top.utils.ReflectionScan;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * aop切面工厂
 *
 * @author lubeilin
 * @date 2021/1/11
 */
@Component
public class AspectFactory {
    private List<Interceptor> interceptors = new LinkedList<>();

    public void loadInterceptor(String[] packageNames) {
        Set<Class<?>> aspects = ReflectionScan.annotationClass(packageNames, Aspect.class);
        aspects.forEach(aClass -> {
            AspectInterceptor interceptor = new AspectInterceptor(aClass);
            interceptors.add(interceptor);
            interceptor.init(BeanManager.getBean(aClass));
        });
    }

    public List<Interceptor> getInterceptors() {
        return interceptors;
    }
}
