package com.top.annotation.ioc;

import java.lang.annotation.*;

/**
 * 配合@Configuration注解使用，会调用存在此注解的方法，并将返回值注入ioc容器
 *
 * @author lubeilin
 * @date 2021/1/9
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Bean {
    String value() default "";
}
