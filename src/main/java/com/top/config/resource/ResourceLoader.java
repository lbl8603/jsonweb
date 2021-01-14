package com.top.config.resource;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

/**
 * @author lubeilin
 * @date 2021/1/7
 */
public interface ResourceLoader {
    /**
     * 加载配置
     *
     * @param path 文件路径
     * @return 配置信息
     * @throws IOException i/o异常
     */
    Map<String, String> loadResource(Path path) throws IOException;
}
