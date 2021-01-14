package com.top.core.aop;

import com.top.annotation.aop.After;
import com.top.annotation.aop.Around;
import com.top.annotation.aop.Aspect;
import com.top.annotation.aop.Before;
import com.top.exception.InitException;
import com.top.utils.PatternMatchUtils;

import java.lang.reflect.Method;

/**
 * aop拦截处理
 *
 * @author lubeilin
 * @date 2021/1/11
 */
public class AspectInterceptor implements Interceptor {

    private Class<?> aClass;
    /**
     * 原始bean，aop类本身
     */
    private Object adviceBean;
    /**
     * 环绕拦截
     */
    private Method doAroundMethod;
    /**
     * 拦截的条件表达式
     */
    private String expression;
    /**
     * 前置方法
     */
    private Method beforeMethod;
    /**
     * 后置方法
     */
    private Method afterMethod;

    public AspectInterceptor(Class<?> adviceClass) {
        aClass = adviceClass;
        expression = adviceClass.getAnnotation(Aspect.class).value();

        for (Method method : adviceClass.getMethods()) {
            Around around = method.getAnnotation(Around.class);
            if (around != null) {
                if (doAroundMethod != null) {
                    throw new InitException(adviceClass + " 不能有多个@Around方法");
                }
                doAroundMethod = method;
            }
            Before before = method.getAnnotation(Before.class);
            if (before != null) {
                if (beforeMethod != null) {
                    throw new InitException(adviceClass + " 不能有多个@Before方法");
                }
                beforeMethod = method;
            }
            After after = method.getAnnotation(After.class);
            if (after != null) {
                if (afterMethod != null) {
                    throw new InitException(adviceClass + " 不能有多个@After方法");
                }
                afterMethod = method;
            }
            if (doAroundMethod == null && afterMethod == null && beforeMethod == null) {
                throw new InitException(adviceClass + " 不能没有拦截方法");
            }
        }
    }

    public void init(Object adviceBean) {
        this.adviceBean = adviceBean;
    }

    @Override
    public boolean supports(Object bean) {
        if (PatternMatchUtils.simpleMatch(expression, bean.getClass().getName())) {
            if (adviceBean == null) {
                throw new InitException(aClass + " 拦截器未初始化成功,无法包装目标:" + bean.getClass());
            }
            return true;
        }
        return false;
    }

    @Override
    public Object intercept(JoinPoint joinPoint) throws Exception {
        Object result;
        if (beforeMethod != null) {
            beforeMethod.invoke(adviceBean);
        }
        if (doAroundMethod != null) {
            result = doAroundMethod.invoke(adviceBean, joinPoint);
        } else {
            result = joinPoint.proceed();
        }
        if (afterMethod != null) {
            afterMethod.invoke(adviceBean);
        }
        return result;
    }
}
