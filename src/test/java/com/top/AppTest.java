package com.top;

import com.top.config.TestConfig;
import com.top.core.ApplicationContext;
import com.top.core.ioc.BeanManager;
import com.top.service.TestService;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

/**
 * Unit test for simple App.
 */
public class AppTest {

    /**
     * Rigorous Test :-)
     */
//    @Test
    public void shouldAnswerWithTrue() {
        assertTrue( true );
        ApplicationContext.run(AppTest.class);
        TestService testService = BeanManager.getBean(TestService.class);
        System.out.println("1+1=" + testService.sum(1, 1));
        System.out.println("class " + testService.getClass());

        TestConfig testConfig = BeanManager.getBean(TestConfig.class.getName());
        System.out.println(testConfig);
    }

    public static void main(String[] args) {
        ApplicationContext.run(AppTest.class);
    }
}
