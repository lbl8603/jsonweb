package com.top.utils;

import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * 反射处理
 *
 * @author lubeilin
 * @date 2021/1/7
 */
public class ReflectionScan {
    /**
     * 获取包路径下存在对应注解的所有类
     *
     * @param packageNames 包路径
     * @param annotation   注解
     * @return 类集合
     */
    public static Set<Class<?>> annotationClass(Object[] packageNames, Class<? extends Annotation> annotation) {
        Reflections reflections = new Reflections(packageNames, new TypeAnnotationsScanner());
        return reflections.getTypesAnnotatedWith(annotation, true);
    }

    /**
     * 获取包路径下所有实现了对应接口的类
     *
     * @param packageNames   包路径
     * @param interfaceClass 接口
     * @param <T>            接口类型
     * @return 类集合
     */
    public static <T> Set<Class<? extends T>> getSubClass(Object[] packageNames, Class<T> interfaceClass) {
        Reflections reflections = new Reflections(packageNames);
        return reflections.getSubTypesOf(interfaceClass);

    }
}
