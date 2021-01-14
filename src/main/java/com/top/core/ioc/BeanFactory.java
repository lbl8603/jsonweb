package com.top.core.ioc;

import com.top.annotation.ioc.*;
import com.top.annotation.web.RestController;
import com.top.config.OverallConfiguration;
import com.top.config.OverallConfigurationManager;
import com.top.core.aop.AopProxyFactory;
import com.top.core.aop.AspectFactory;
import com.top.core.web.RouteMethodMapper;
import com.top.exception.InitException;
import com.top.utils.FieldUtils;
import com.top.utils.MethodVariableNameUtil;
import com.top.utils.ReflectionScan;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

/**
 * bean工厂
 *
 * @author lubeilin
 * @date 2021/1/7
 */
@Slf4j
public class BeanFactory {
    /**
     * 最终生成的bean
     */
    private Map<String, Object> singletonObjects = new HashMap<>(128);
    /**
     * 二级缓存，存储中间状态的bean实例，解决循环依赖问题
     */
    private Map<String, Object> earlySingletonObjects = new HashMap<>(16);

    /**
     * 存储bean名称到具体信息的映射
     */
    private Map<String, BeanInfo> beanInfoMap = new HashMap<>(16);
    /**
     * 缓存正在创建中的实例
     */
    private Set<String> localCreation = new HashSet<>();
    /**
     * 需要执行@PostConstruct方法的集合
     */
    private List<Object> objectList = new LinkedList<>();
    /**
     * 需要扫描的包路径
     */
    private String[] packageNames;
    /**
     * 配置信息类
     */
    private OverallConfiguration configuration;
    private AopProxyFactory aopProxyFactory;

    public BeanFactory(String[] packageNames) {
        BeanManager.init(this);
        this.packageNames = packageNames;
    }

    /**
     * 加载系统默认实例
     *
     * @throws Exception 加载异常
     */
    public void loadSystemBeans(ClassLoader classLoader) throws Exception {
        aopProxyFactory = loadBean(AopProxyFactory.class);
        loadBean(OverallConfigurationManager.class).loadResources(classLoader);
        loadBean(AspectFactory.class, null, false).loadInterceptor(packageNames);
    }

    /**
     * 加载使用@Bean注解的信息
     *
     * @param aClass 配置类
     */
    private void loadBeanClassForMethod(Class<?> aClass) {
        BeanInfo configBeanInfo = getBeanInfo(aClass);
        saveBeanInfo(configBeanInfo);
        for (Method method : aClass.getMethods()) {
            if (method.isAnnotationPresent(Bean.class)) {
                Bean bean = method.getAnnotation(Bean.class);
                BeanInfo beanInfo = new BeanInfo();
                beanInfo.setConfigType(aClass);
                Class<?> returnType = method.getReturnType();
                beanInfo.setBeanType(returnType);
                if ("".equals(bean.value())) {
                    beanInfo.setBeanName(method.getName());
                } else {
                    beanInfo.setBeanName(bean.value());
                }
                beanInfo.setOtherName(returnType.getName());
                saveBeanInfo(beanInfo);
            }
        }
    }

    /**
     * 加载使用@Bean注入，但是没被引用的bean
     */
    private void loadBeansForMethod() {
        beanInfoMap.forEach((name, beanInfo) -> {
            try {
                loadBean(beanInfo.getBeanType(), name);
            } catch (InitException e) {
                throw e;
            } catch (Exception e) {
                throw new InitException(beanInfo.getBeanType() + " " + e.getMessage(), e);
            }
        });
    }

    /**
     * 加载所有外部依赖
     */
    public void loadBeans() {
        ClassManager classManager = new ClassManager();
        classManager.loadClass(packageNames);
        //先注入有Configuration注解的
        classManager.getClassSet(Configuration.class).forEach(this::loadBeanClassForMethod);
        classManager.getClassSet(Configuration.class).forEach(aClass -> {
            try {
                loadBean(aClass);
            } catch (InitException e) {
                throw e;
            } catch (Exception e) {
                throw new InitException(aClass.getName() + " " + e.getMessage(), e);
            }
        });
        loadBeansForMethod();
        classManager.getClassSet(Component.class).forEach(aClass -> {
            try {
                loadBean(aClass);
            } catch (InitException e) {
                throw e;
            } catch (Exception e) {
                throw new InitException(aClass.getName() + " " + e.getMessage(), e);
            }
        });
        RouteMethodMapper methodMapper = getBean(RouteMethodMapper.class);
        classManager.getClassSet(RestController.class).forEach(aClass -> {
            try {
                methodMapper.loadRoutes(aClass, loadBean(aClass));
            } catch (InitException e) {
                throw e;
            } catch (Exception e) {
                throw new InitException(aClass.getName() + " " + e.getMessage(), e);
            }
        });
        //执行初始化方法
        singletonObjects.values().forEach(this::invokePostConstruct);
    }

    public <T> T getBean(Class<T> aClass) {
        BeanInfo beanInfo = getBeanInfo(getTargetBeanType(aClass, null));
        Object beanObject = singletonObjects.get(beanInfo.getBeanName());
        if (beanObject != null) {
            return (T) beanObject;
        }
        try {
            return loadBean(aClass, null, false);
        } catch (InitException e) {
            throw e;
        } catch (Exception e) {
            throw new InitException(e.getMessage(), e);
        }
    }

    public <T> T getBean(String beanName) {
        BeanInfo beanInfo = getBeanInfo(beanName);
        if (beanInfo != null) {
            return (T) singletonObjects.get(beanInfo.getBeanName());
        }
        throw new InitException(beanName + "不存在");
    }

    /**
     * 执行@Bean注解的方法
     *
     * @param beanInfo bean信息
     * @return 实例
     * @throws Exception 执行异常
     */
    private Object invokeBeanMethod(BeanInfo beanInfo) throws Exception {
        Object parentBean = loadBean(beanInfo.getConfigType(), null, false);
        for (Method method : beanInfo.getConfigType().getMethods()) {
            if (method.isAnnotationPresent(Bean.class)) {
                Bean bean = method.getAnnotation(Bean.class);
                if (bean.value().equals(beanInfo.getBeanName()) ||
                        method.getName().equals(beanInfo.getBeanName()) ||
                        method.getReturnType().getName().equals(beanInfo.getOtherName())) {

                    if (method.getParameterCount() > 0) {
                        String[] parameterNames = MethodVariableNameUtil.getMethodParameterNames(beanInfo.getConfigType(), method);
                        Class<?>[] types = method.getParameterTypes();
                        Object[] args = new Object[parameterNames.length];
                        for (int index = 0; index < parameterNames.length; index++) {
                            args[index] = loadBean(getTargetBeanType(types[index], parameterNames[index]), parameterNames[index]);
                        }
                        return method.invoke(parentBean, args);
                    } else {
                        return method.invoke(parentBean);
                    }
                }
            }
        }
        throw new InitException("在配置类" + beanInfo.getConfigType() + "中未找到符合" + beanInfo.getBeanName() + "的方法");
    }


    private <T> T loadBean(Class<T> aClass) throws Exception {
        return loadBean(aClass, null, true);
    }

    private <T> T loadBean(Class<T> aClass, String name) throws Exception {
        return loadBean(aClass, name, true);
    }

    /**
     * 加载对应bean
     *
     * @param aClass 类对象
     * @param name   bean名称
     * @param early  是否允许加载早期实例
     * @param <T>    类型
     * @return 生成的实例对象
     * @throws Exception 执行异常
     */
    private <T> T loadBean(Class<T> aClass, String name, boolean early) throws Exception {
        BeanInfo beanInfo = getBeanInfo(name);
        if (beanInfo == null || !beanInfo.getBeanType().isAssignableFrom(aClass)) {
            beanInfo = getBeanInfo(aClass);
        }
        String beanName = beanInfo.getBeanName();
        Object beanObject = getSingleton(beanName, early);
        if (beanObject == null) {
            if (localCreation.contains(beanName)) {
                throw new InitException((beanInfo.getConfigType() == null ? "" : (beanInfo.getConfigType().getName() + " @"))
                        + beanInfo.getBeanType() + " @" + beanInfo.getBeanName() + " 循环依赖");
            }
            localCreation.add(beanName);
            if (beanInfo.getConfigType() != null) {
                beanObject = aopProxyFactory.getBeanPostProcessor(beanInfo.getBeanType()).postProcess(invokeBeanMethod(beanInfo));
            } else {
                saveBeanInfo(beanInfo);
                Class<?> beanClass = beanInfo.getBeanType();
                Constructor<?>[] constructors = beanClass.getConstructors();
                if (constructors.length > 1) {
                    throw new InitException(aClass.getName() + " 不能有多个构造方法");
                }
                Constructor<?> constructor = constructors[0];
                if (constructor.getParameterCount() == 0) {
                    beanObject = beanClass.newInstance();
                } else {
                    Parameter[] parameters = constructor.getParameters();
                    Class<?>[] parameterTypes = constructor.getParameterTypes();
                    Object[] args = new Object[constructor.getParameterCount()];
                    String[][] names = MethodVariableNameUtil.getConstructorParameterNames(aClass);
                    for (int index = 0; index < parameters.length; index++) {
                        args[index] = loadBean(getTargetBeanType(parameterTypes[index], names[0][index]), names[0][index]);
                    }
                    beanObject = constructor.newInstance(args);

                }
                Object targetBean = beanObject;
                if (aopProxyFactory != null) {
                    beanObject = aopProxyFactory.getBeanPostProcessor(beanClass).postProcess(targetBean);
                }
                earlySingletonObjects.put(beanName, beanObject);
                //注入属性
                initBean(targetBean, beanClass);
                localCreation.remove(beanName);
                earlySingletonObjects.remove(beanName);
            }
            singletonObjects.put(beanName, beanObject);
        }
        return (T) beanObject;
    }

    public void invokePostConstruct(Object object) {
        for (Method method : object.getClass().getMethods()) {
            if (method.isAnnotationPresent(PostConstruct.class)) {
                try {
                    method.invoke(object);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new InitException(e.getMessage(), e);
                }
            }
        }
    }

    /**
     * 注入属性
     *
     * @param beanObject bean实例
     * @param beanClass  bean类对象
     * @throws Exception 属性注入异常
     */
    private void initBean(Object beanObject, Class<?> beanClass) throws Exception {
        List<Field> beanFields = FieldUtils.getAllFieldsList(beanClass);
        for (Field field : beanFields) {
            if (field.isAnnotationPresent(Resource.class)) {
                Resource resource = field.getAnnotation(Resource.class);
                String fieldName = "".equals(resource.value()) ? field.getName() : resource.value();
                Object beanFieldInstance = loadBean(getTargetBeanType(field.getType(), fieldName), fieldName);
                boolean accessible = field.isAccessible();
                field.setAccessible(true);
                field.set(beanObject, beanFieldInstance);
                field.setAccessible(accessible);
            } else if (field.isAnnotationPresent(Value.class)) {
                String value = field.getAnnotation(Value.class).value();
                if ("".equals(value)) {
                    throw new InitException(beanClass.getName() + field.getName() + " @value值为空");
                }
                int indexSplit = value.indexOf(":");
                String key;
                String defaultV = null;
                if (indexSplit == 0) {
                    throw new InitException(beanClass.getName() + field.getName() + " @value ':' 位置错误");
                }
                if (indexSplit > 0) {
                    key = value.substring(0, indexSplit);
                    defaultV = value.substring(indexSplit + 1);
                } else {
                    key = value;
                }
                Object fieldValue = getValueBase(field.getType(), key, defaultV);

                if (fieldValue != null) {
                    boolean accessible = field.isAccessible();
                    field.setAccessible(true);
                    field.set(beanObject, fieldValue);
                    field.setAccessible(accessible);
                }
            }
        }
    }

    private Object getValue(Class<?> type, String value, String defaultValue) {
        Object fieldValue = null;
        if (type == Integer.class || type == int.class) {
            if (value != null) {
                fieldValue = Integer.parseInt(value);
            } else if (defaultValue != null) {
                fieldValue = Integer.parseInt(defaultValue);
            }
        } else if (type == String.class) {
            if (value != null) {
                fieldValue = value;
            } else if (defaultValue != null) {
                fieldValue = defaultValue;
            }
        } else if (type == Boolean.class || type == boolean.class) {
            if (value != null) {
                fieldValue = Boolean.parseBoolean(value);
            } else if (defaultValue != null) {
                fieldValue = Boolean.parseBoolean(defaultValue);
            }
        } else if (type == Long.class || type == long.class) {
            if (value != null) {
                fieldValue = Long.parseLong(value);
            } else if (defaultValue != null) {
                fieldValue = Long.parseLong(defaultValue);
            }
        } else if (type == Character.class || type == char.class) {
            if (value != null) {
                fieldValue = value.charAt(0);
            } else if (defaultValue != null) {
                fieldValue = defaultValue.charAt(0);
            }
        } else if (type == Short.class || type == short.class) {
            if (value != null) {
                fieldValue = Short.parseShort(value);
            } else if (defaultValue != null) {
                fieldValue = Short.parseShort(defaultValue);
            }
        } else if (type == Double.class || type == double.class) {
            if (value != null) {
                fieldValue = Double.parseDouble(value);
            } else if (defaultValue != null) {
                fieldValue = Double.parseDouble(defaultValue);
            }
        } else if (type == Float.class || type == float.class) {
            if (value != null) {
                fieldValue = Float.parseFloat(value);
            } else if (defaultValue != null) {
                fieldValue = Float.parseFloat(defaultValue);
            }
        } else if (type == Byte.class || type == byte.class) {
            if (value != null) {
                fieldValue = Byte.parseByte(value);
            } else if (defaultValue != null) {
                fieldValue = Byte.parseByte(defaultValue);
            }
        } else if (type.isArray()) {
            String[] temp = null;
            if (value != null) {
                temp = value.split(",");
            } else if (defaultValue != null) {
                temp = defaultValue.split(",");
            }
            if (temp != null) {
                fieldValue = Array.newInstance(type.getComponentType(), temp.length);
                for (int index = 0; index < temp.length; index++) {
                    Array.set(fieldValue, index, getValue(type.getComponentType(), temp[index], temp[index]));
                }
            }
        } else {
            throw new InitException(" @value 不支持的类型");
        }
        return fieldValue;
    }

    private Object getValueBase(Class<?> type, String key, String defaultValue) {
        if (configuration == null) {
            configuration = getBean(OverallConfiguration.class);
        }
        return getValue(type, configuration.getString(key), defaultValue);
    }

    private boolean isComponent(Class<?> aClass) {
        if (aClass.isAnnotationPresent(Component.class)) {
            return true;
        }
        for (Annotation annotation : aClass.getAnnotations()) {
            if (isComponent(annotation.annotationType())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取真实的class对象
     *
     * @param aClass class对象
     * @param name   bean名称
     * @return 真实类
     */
    private Class<?> getTargetBeanType(Class<?> aClass, String name) {
        BeanInfo beanInfo = name == null ? null : beanInfoMap.get(name);
        if (beanInfo != null && beanInfo.getBeanType().isAssignableFrom(aClass)) {
            return beanInfo.getBeanType();
        }
        beanInfo = beanInfoMap.get(aClass.getName());
        if (beanInfo != null && beanInfo.getBeanType().isAssignableFrom(aClass)) {
            return beanInfo.getBeanType();
        }
        if (isComponent(aClass)) {
            return aClass;
        }
        Set<Class<?>> components = ReflectionScan.getSubClass(packageNames, (Class<Object>) aClass);
        Class<?> target = null;
        boolean error = false;
        for (Class<?> item : components) {
            if (item.isAnnotationPresent(Component.class)) {
                Component component = item.getAnnotation(Component.class);
                if (component.value().equals(name)) {
                    target = item;
                    error = false;
                    break;
                }
                if (target != null) {
                    error = true;
                }
                target = item;
            } else if (item.isAnnotationPresent(Configuration.class)) {
                Configuration component = item.getAnnotation(Configuration.class);
                if (component.value().equals(name)) {
                    target = item;
                    error = false;
                    break;
                }
                if (target != null) {
                    error = true;
                }
                target = item;
            }
        }
        if (error) {
            throw new InitException(aClass.getName() + ",beanName:" + name + ",匹配到多个目标对象");
        }
        if (target == null) {
            throw new InitException(aClass.getName() + ",beanName:" + name + ",未找到目标对象");
        }
        return target;
    }

    private Object getSingleton(String beanName, boolean early) {
        Object object = singletonObjects.get(beanName);
        if (object == null && early) {
            object = earlySingletonObjects.get(beanName);
        }
        return object;
    }

    private void saveBeanInfo(BeanInfo beanInfo) {
        //bean名称不能重复
        BeanInfo old = beanInfoMap.get(beanInfo.getBeanName());
        if (old != null && !beanInfo.equals(old)) {
            throw new InitException(beanInfo.getBeanName() + ",多个同名bean");
        }
        beanInfoMap.put(beanInfo.getBeanName(), beanInfo);
        if (beanInfo.getOtherName() != null) {
            if ((old = beanInfoMap.get(beanInfo.getOtherName())) != null) {
                old.setOtherName(null);
                beanInfo.setOtherName(null);
            } else {
                beanInfoMap.put(beanInfo.getOtherName(), beanInfo);
            }
        }
    }

    private BeanInfo getBeanInfo(String name) {
        if (name == null) {
            return null;
        }
        BeanInfo beanInfo = beanInfoMap.get(name);
        if (beanInfo != null) {
            if (name.equals(beanInfo.getBeanName()) || name.equals(beanInfo.getOtherName())) {
                return beanInfo;
            }
        }
        return null;
    }

    /**
     * 获取bean信息
     *
     * @param aClass class对象
     * @return bean信息
     */
    private BeanInfo getBeanInfo(Class<?> aClass) {
        BeanInfo beanInfo = beanInfoMap.get(aClass.getName());
        if (beanInfo != null) {
            return beanInfo;
        }
        beanInfo = new BeanInfo();
        beanInfo.setBeanType(aClass);
        String value = "";
        if (aClass.isAnnotationPresent(Component.class)) {
            Component component = aClass.getAnnotation(Component.class);
            value = component.value();
        } else if (aClass.isAnnotationPresent(Configuration.class)) {
            Configuration component = aClass.getAnnotation(Configuration.class);
            value = component.value();
        }
        if (!"".equals(value)) {
            beanInfo.setBeanName(value);
            beanInfo.setOtherName(aClass.getName());
        } else {
            beanInfo.setBeanName(aClass.getName());
        }
        return beanInfo;
    }
}
