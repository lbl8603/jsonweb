package com.top.annotation.ioc;

import java.lang.annotation.*;

/**
 * 初始化完成后调用存在改注解的方法
 *
 * @author lubeilin
 * @date 2021/1/13
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PostConstruct {
}
