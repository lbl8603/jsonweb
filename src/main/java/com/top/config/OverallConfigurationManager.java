package com.top.config;

import com.top.annotation.ioc.Configuration;
import com.top.config.resource.ResourceLoader;
import com.top.config.resource.property.PropertiesResourceLoader;
import com.top.config.resource.yaml.YamlResourceLoader;
import com.top.exception.InitException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author lubeilin
 * @date 2021/1/7
 */
@Slf4j
@Configuration
public class OverallConfigurationManager implements LoadConfiguration {
    /**
     * 默认配置文件
     */
    public static final String DEFAULT_CONFIG = "application";
    public static final String DEFAULT_CONFIG_ACTIVE = "application-";
    /**
     * 支持的配置文件后缀
     */
    public static final String PROPERTIES_FILE_EXTENSION = ".properties";
    public static final String YAML_FILE_EXTENSION = ".yaml";
    public static final String YML_FILE_EXTENSION = ".yml";
    public static final String[] FILE_EXTENSIONS = {PROPERTIES_FILE_EXTENSION, YAML_FILE_EXTENSION, YML_FILE_EXTENSION};
    /**
     * 动态配置指定配置文件
     */
    public static final String PROFILES_ACTIVE = "web.profiles.active";
    private OverallConfiguration overallConfiguration;

    public OverallConfigurationManager(OverallConfiguration overallConfiguration) {
        this.overallConfiguration = overallConfiguration;
    }

    @Override
    public void loadResources(ClassLoader classLoader) {
        loadResources(classLoader, OverallConfigurationManager.DEFAULT_CONFIG);
        String profilesActive = overallConfiguration.getString(OverallConfigurationManager.PROFILES_ACTIVE);
        if (profilesActive != null) {
            loadResources(classLoader, OverallConfigurationManager.DEFAULT_CONFIG_ACTIVE + profilesActive);
        }
    }

    private void loadResources(ClassLoader classLoader, String path) {
        List<Path> filePaths = new ArrayList<>();
        for (String ext : OverallConfigurationManager.FILE_EXTENSIONS) {
            URL url = classLoader.getResource(path + ext);
            if (!Objects.isNull(url)) {
                try {
                    filePaths.add(Paths.get(url.toURI()));
                } catch (URISyntaxException ignored) {
                }
            }
        }
        loadResources(filePaths);
    }

    @Override
    public void loadResources(List<Path> resourcePaths) {
        try {
            for (Path resourcePath : resourcePaths) {
                String fileName = resourcePath.getFileName().toString();
                if (fileName.endsWith(PROPERTIES_FILE_EXTENSION)) {
                    ResourceLoader resourceLoader = new PropertiesResourceLoader();
                    overallConfiguration.putAll(resourceLoader.loadResource(resourcePath));
                } else if (fileName.endsWith(YML_FILE_EXTENSION) || fileName.endsWith(YAML_FILE_EXTENSION)) {
                    ResourceLoader resourceLoader = new YamlResourceLoader();
                    overallConfiguration.putAll(resourceLoader.loadResource(resourcePath));
                }
            }
        } catch (IOException ex) {
            throw new InitException(ex.getMessage(), ex);
        }
    }
}
