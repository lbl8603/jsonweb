package com.top.annotation.ioc;

import java.lang.annotation.*;

/**
 * 注入属性时使用
 *
 * @author lubeilin
 * @date 2021/1/8
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Resource {
    String value() default "";
}
