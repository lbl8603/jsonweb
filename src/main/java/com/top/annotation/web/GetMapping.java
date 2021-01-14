package com.top.annotation.web;

import java.lang.annotation.*;

/**
 * get请求
 *
 * @author lubeilin
 * @date 2021/1/12
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GetMapping {
    String value() default "";
}
