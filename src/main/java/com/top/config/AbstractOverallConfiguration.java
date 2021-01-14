package com.top.config;

import com.top.annotation.ioc.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lubeilin
 * @date 2021/1/7
 */
@Configuration()
public class AbstractOverallConfiguration implements OverallConfiguration {
    private Map<String, String> configurationCache = new ConcurrentHashMap<>(64);

    @Override
    public String[] getArray(String key) {
        String result = getString(key);
        return result == null ? null : result.split(",");
    }

    @Override
    public Float getFloat(String key) {
        String result = getString(key);
        return result == null ? null : Float.parseFloat(result);
    }

    @Override
    public Double getDouble(String key) {
        String result = getString(key);
        return result == null ? null : Double.parseDouble(result);
    }

    @Override
    public Byte getByte(String key) {
        String result = getString(key);
        return result == null ? null : Byte.parseByte(result);
    }

    @Override
    public Character getCharacter(String key) {
        String result = getString(key);
        return result == null || "".equals(result) ? null : result.charAt(0);
    }

    @Override
    public Short getShort(String key) {
        String result = getString(key);
        return result == null ? null : Short.parseShort(result);
    }

    @Override
    public Integer getInt(String key) {
        String result = getString(key);
        return result == null ? null : Integer.parseInt(result);
    }

    @Override
    public Long getLong(String key) {
        String result = getString(key);
        return result == null ? null : Long.parseLong(result);
    }

    @Override
    public String getString(String key) {
        return configurationCache.get(key);
    }

    @Override
    public Boolean getBoolean(String key) {
        String result = getString(key);
        return result == null ? null : Boolean.parseBoolean(result);
    }

    @Override
    public void put(String key, String content) {
        configurationCache.put(key, content);
    }

    @Override
    public void putAll(Map<String, String> map) {
        configurationCache.putAll(map);
    }
}
