package com.top.config;

import java.util.Map;

/**
 * @author lubeilin
 * @date 2021/1/7
 */
public interface OverallConfiguration {

    /**
     * 数组
     *
     * @param key
     * @return
     */
    String[] getArray(String key);

    /**
     * 单精度浮点型
     *
     * @param key
     * @return
     */
    Float getFloat(String key);

    /**
     * 双精度浮点型
     *
     * @param key
     * @return
     */
    Double getDouble(String key);

    /**
     * 字节
     *
     * @param key
     * @return
     */
    Byte getByte(String key);

    /**
     * 字符
     *
     * @param key
     * @return
     */
    Character getCharacter(String key);

    /**
     * 短整型
     *
     * @param key
     * @return
     */
    Short getShort(String key);

    /**
     * 获取整数
     *
     * @param key key
     * @return 配置信息
     */
    Integer getInt(String key);

    /**
     * 获取长整数
     *
     * @param key key
     * @return 配置信息
     */
    Long getLong(String key);

    /**
     * 获取字符串
     *
     * @param key key
     * @return 配置信息
     */
    String getString(String key);

    /**
     * 获取布尔值
     *
     * @param key key
     * @return 配置信息
     */
    Boolean getBoolean(String key);


    /**
     * 修改配置
     *
     * @param key     key
     * @param content 配置内容
     */
    default void put(String key, String content) {
    }

    /**
     * 批量修改
     *
     * @param map map
     */
    default void putAll(Map<String, String> map) {
    }
}
