package com.top.annotation.ioc;

import java.lang.annotation.*;

/**
 * 注入ioc容器
 *
 * @author lubeilin
 * @date 2021/1/7
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Component {
    String value() default "";
}
