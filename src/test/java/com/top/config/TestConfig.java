package com.top.config;

import com.top.annotation.ioc.Bean;
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
    @Value("integer:1")
    private int integer;
    @Value("strings:1,2")
    private String[] strings;
    @Value("integers:1,2")
    private Integer[] integers;
    @Value("ints:1,2")
    private int[] ints;
    @Value("def:true")
    private Boolean def;

    @Bean
    public String testBean(){
        return "testBean";
    }
}
