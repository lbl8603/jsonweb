package com.top.core.ioc;

import com.top.annotation.ioc.Component;
import com.top.annotation.ioc.Configuration;
import com.top.annotation.web.RestController;
import com.top.utils.ReflectionScan;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 扫描出存在@Component注解的类
 *
 * @author lubeilin
 * @date 2021/1/7
 */
public class ClassManager implements ClassScan {
    private Map<Class<? extends Annotation>, Set<Class<?>>> classMap = new HashMap<>();

    @Override
    public void loadClass(String[] packageNames) {
        loadClass(packageNames, Component.class, Component.class);
    }

    private void loadClass(String[] packageNames, Class<? extends Annotation> aClass, Class<? extends Annotation> present) {
        Set<Class<?>> components = ReflectionScan.annotationClass(packageNames, aClass);
        components.forEach(c -> {
            if (c.isAnnotation()) {
                loadClass(packageNames, (Class<? extends Annotation>) c, getPresent(c));
            } else {
                classMap.computeIfAbsent(present, e -> new HashSet<>()).add(c);
            }
        });
    }

    private Class<? extends Annotation> getPresent(Class<?> aClass) {
        if (Configuration.class == aClass || aClass.isAnnotationPresent(Configuration.class)) {
            return Configuration.class;
        }
        if (RestController.class == aClass || aClass.isAnnotationPresent(RestController.class)) {
            return RestController.class;
        }
        return Component.class;
    }


    @Override
    public Set<Class<?>> getClassSet(Class<? extends Annotation> annotation) {
        return classMap.get(annotation);
    }
}
