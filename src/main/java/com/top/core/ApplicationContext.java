package com.top.core;

import com.top.annotation.ioc.ComponentScan;
import com.top.core.ioc.BeanFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @author lubeilin
 * @date 2021/1/7
 */
@Slf4j
public class ApplicationContext {

    public static void run(Class<?> applicationClass) {
        try {
            BeanFactory beanFactory = new BeanFactory(getPackageNames(applicationClass));
            beanFactory.loadSystemBeans(applicationClass.getClassLoader());
            beanFactory.loadBeans();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }

    }

    private static String[] getPackageNames(Class<?> applicationClass) {
        ComponentScan componentScan = applicationClass.getAnnotation(ComponentScan.class);
        String[] packageNames = !Objects.isNull(componentScan) ? componentScan.value()
                : new String[]{applicationClass.getPackage().getName()};
        List<String> packageNameList = new LinkedList<>();
        packageNameList.add(ApplicationContext.class.getPackage().getName());
        packageNameList.addAll(Arrays.asList(packageNames));
        return packageNameList.toArray(new String[0]);
    }
}
