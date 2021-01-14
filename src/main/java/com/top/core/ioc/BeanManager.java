package com.top.core.ioc;

/**
 * @author lubeilin
 * @date 2021/1/11
 */
public class BeanManager {
    private static BeanFactory beanFactory;

    public static void init(BeanFactory beanFactory) {
        BeanManager.beanFactory = beanFactory;
    }

    public static <T> T getBean(Class<T> tClass) {
        return beanFactory.getBean(tClass);
    }

    public static <T> T getBean(String beanName) {
        return beanFactory.getBean(beanName);
    }
}
