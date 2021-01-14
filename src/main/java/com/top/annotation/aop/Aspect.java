package com.top.annotation.aop;

import com.top.annotation.ioc.Component;

import java.lang.annotation.*;

/**
 * aop注解，会拦截value匹配的类
 *
 * @author lubeilin
 * @date 2021/1/11
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Aspect {
    String value();
}
