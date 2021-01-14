package com.top.annotation.ioc;

import java.lang.annotation.*;

/**
 * @author lubeilin
 * @date 2021/1/8
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Value {
    String value();
}
