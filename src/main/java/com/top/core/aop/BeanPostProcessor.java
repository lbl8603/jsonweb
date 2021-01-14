package com.top.core.aop;

/**
 * @author lubeilin
 * @date 2021/1/11
 */
public interface BeanPostProcessor {
    /**
     * 包装bean
     *
     * @param bean bean实例
     * @return 包装后的实例
     */
    Object postProcess(Object bean);
}
