package com.top.service;

import com.top.config.TestConfig;

import java.util.Map;

/**
 * @author lubeilin
 * @date 2021/1/12
 */
public interface TestService {
    int sum(int num1, int num2);
    TestConfig getConfig();
}
