package com.top.annotation.aop;

import java.lang.annotation.*;

/**
 * 拦截方法调用后，会调用存在此注解的方法
 *
 * @author lubeilin
 * @date 2021/1/11
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface After {
}
