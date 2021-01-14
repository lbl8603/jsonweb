package com.top.config;

import java.nio.file.Path;
import java.util.List;

/**
 * @author lubeilin
 * @date 2021/1/11
 */
public interface LoadConfiguration {
    /**
     * 加载配置
     *
     * @param resourcePaths 文件路径
     */
    default void loadResources(List<Path> resourcePaths) {
    }

    /**
     * 加载配置
     *
     * @param classLoader 类加载器
     */
    default void loadResources(ClassLoader classLoader) {
    }
}
