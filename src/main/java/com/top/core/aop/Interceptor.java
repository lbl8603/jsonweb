package com.top.core.aop;

/**
 * 拦截器
 *
 * @author lubeilin
 * @date 2021/1/11
 */
public interface Interceptor {

    /**
     * 判断是否拦截
     *
     * @param bean 原始bean
     * @return 是否拦截
     */
    boolean supports(Object bean);

    /**
     * 方法拦截
     *
     * @param joinPoint 执行内容
     * @return 执行结果
     * @throws Exception 处理异常
     */
    Object intercept(JoinPoint joinPoint) throws Exception;
}
