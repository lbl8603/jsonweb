package com.top.core.ioc;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * @author lubeilin
 * @date 2021/1/7
 */
public interface ClassScan {
    /**
     * 加载类
     *
     * @param packageNames 包路径
     */
    void loadClass(String[] packageNames);

    /**
     * 获取对应注解的类集合
     *
     * @param annotation 注解
     * @return 类集合
     */
    Set<Class<?>> getClassSet(Class<? extends Annotation> annotation);
}
