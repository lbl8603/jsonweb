package com.top.annotation.aop;

import java.lang.annotation.*;

/**
 * 环绕触发，此注解的方法会代替目标方法执行
 *
 * @author lubeilin
 * @date 2021/1/11
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Around {
}
