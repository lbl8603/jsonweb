# 一个简易的web框架
参照springboot的功能使用java实现如下操作
### 功能示例

#### 1. 配置文件
    ```
    #支持yml和properties两种格式
    web:
      profiles:
        active: local #指定读取配置文件
      port: 8080      #web服务器端口
    ```

#### 2. 读取配置，支持字符串注入，支持基础数据类型和其包装类以及数组

```
    @Value("string")
    private String string;
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
```

#### 3. 实例化到容器和获取引用
```
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

@Configuration
public class TestConfig {
    @Bean
    public String testBean(){
        return "testBean";
    }
}
```
#### 4. aop切面
```
//设置需要拦截的类路径
@Aspect("com.top.service.*") 
public class AspectTest {
    @Value("key")
    private String key;

    private static final ThreadLocal<Long> LONG_THREAD_LOCAL = new ThreadLocal<>();

    @Before
    public void before() {
        LONG_THREAD_LOCAL.set(System.currentTimeMillis());
        System.out.println("AspectTest before:" + key);
    }

    @Around
    public Object doAround(JoinPoint joinPoint) throws InvocationTargetException, IllegalAccessException {
        Object object = joinPoint.proceed();
        System.out.println("AspectTest 环绕触发：" + object);
        return object;
    }

    @After
    public void after() {
        System.out.println("AspectTest after:" + (System.currentTimeMillis() - LONG_THREAD_LOCAL.get()) + "(ms)");
    }
}

```
#### 5. 控制器
```
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
```
