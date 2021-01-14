package com.top.controller;

import com.top.annotation.ioc.Resource;
import com.top.annotation.ioc.Value;
import com.top.annotation.web.*;
import com.top.config.TestConfig;
import com.top.service.TestService;

import java.util.Map;

/**
 * @author lubeilin
 * @date 2021/1/12
 */
@RestController("/url")
public class TestController {
    @Value("key")
    private String key;
    @Resource
    private TestService testService;

    @GetMapping("/get")
    public String test(@RequestParam("a") int a, int b) {
        return key + testService.sum(a, b);
    }

    @PostMapping("/get")
    public String test(@RequestParam("a") int a, int b, @RequestBody Map<String, Object> msg) {
        return msg.toString() + testService.sum(a, b);
    }

    @PostMapping("/post")
    public TestConfig post(@RequestParam("a") int a, int b, @RequestBody Map<String, Object> msg) {
        return testService.getConfig();
    }
}
