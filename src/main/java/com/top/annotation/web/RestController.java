package com.top.annotation.web;

import com.top.annotation.ioc.Component;

import java.lang.annotation.*;

/**
 * @author lubeilin
 * @date 2021/1/12
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface RestController {
    String value() default "";
}
