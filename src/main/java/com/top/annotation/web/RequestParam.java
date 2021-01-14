package com.top.annotation.web;

import java.lang.annotation.*;

/**
 * url参数解析
 *
 * @author lubeilin
 * @date 2021/1/12
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestParam {
    String value();

    boolean require() default false;

    String defaultValue() default "";
}
