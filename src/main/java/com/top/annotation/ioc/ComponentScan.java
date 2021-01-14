package com.top.annotation.ioc;

import java.lang.annotation.*;

/**
 * 扫描包路径
 *
 * @author lubeilin
 * @date 2021/1/7
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ComponentScan {
    String[] value();
}
