package com.top.annotation.web;

import java.lang.annotation.*;

/**
 * @author lubeilin
 * @date 2021/1/13
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestBody {
}
