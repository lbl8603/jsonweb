package com.top.config;

import com.top.annotation.ioc.Configuration;
import com.top.annotation.ioc.Value;
import lombok.Data;

/**
 * @author lubeilin
 * @date 2021/1/12
 */
@Configuration
@Data
public class TestConfig {
    @Value("key")
    private String key;
    @Value("dddd:1")
    private int integer;
    @Value("array:1,2")
    private String[] array;
    @Value("intayy:1,2")
    private Integer[] intayy;
    @Value("dd:1,2")
    private int[] dd;
    @Value("def:true")
    private Boolean def;
}
