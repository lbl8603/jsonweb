package com.top.annotation.ioc;

import java.lang.annotation.*;

/**
 * @author lubeilin
 * @date 2021/1/9
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Configuration {
    String value() default "";
}
