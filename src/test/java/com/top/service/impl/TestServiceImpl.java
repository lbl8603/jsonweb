package com.top.service.impl;

import com.top.annotation.ioc.Component;
import com.top.annotation.ioc.Resource;
import com.top.config.TestConfig;
import com.top.service.TestService;

/**
 * @author lubeilin
 * @date 2021/1/12
 */
@Component
public class TestServiceImpl implements TestService {
    @Resource
    private TestConfig testConfig;
    @Override
    public int sum(int num1, int num2) {
        return num1 + num2;
    }

    @Override
    public TestConfig getConfig() {
        return testConfig;
    }
}
