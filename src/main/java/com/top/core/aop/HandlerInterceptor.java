package com.top.core.aop;

/**
 * @author lubeilin
 * @date 2021/1/11
 */
public interface HandlerInterceptor {
    default boolean preHandle() {
        return true;
    }
}
