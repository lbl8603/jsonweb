package com.top.aop;

import com.top.annotation.aop.After;
import com.top.annotation.aop.Around;
import com.top.annotation.aop.Aspect;
import com.top.annotation.aop.Before;
import com.top.annotation.ioc.Value;
import com.top.core.aop.JoinPoint;

import java.lang.reflect.InvocationTargetException;

/**
 * @author lubeilin
 * @date 2021/1/12
 */
@Aspect("com.top.service.*")
public class AspectTest {
    @Value("key")
    private String key;
//    @Resource //会造成循环依赖
//    private TestService testService;

    private static final ThreadLocal<Long> LONG_THREAD_LOCAL = new ThreadLocal<>();

    @Before
    public void before() {
        LONG_THREAD_LOCAL.set(System.currentTimeMillis());
        System.out.println("AspectTest before:" + key);
    }

    @Around
    public Object doAround(JoinPoint joinPoint) throws InvocationTargetException, IllegalAccessException {
        Object object = joinPoint.proceed();
        System.out.println("AspectTest 环绕触发：" + object);
        return object;
    }

    @After
    public void after() {
        System.out.println("AspectTest after:" + (System.currentTimeMillis() - LONG_THREAD_LOCAL.get()) + "(ms)");
    }
}
