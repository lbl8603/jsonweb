package com.top.utils;

import javassist.*;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

import java.lang.reflect.Method;

/**
 * 获取方法参数名称的工具类
 *
 * @author lubeilin
 * @date 2021/1/9
 */
public class MethodVariableNameUtil {
    public static String[] getMethodParameterNames(Class<?> aClass, Method method) throws NotFoundException {
        ClassPool pool = ClassPool.getDefault();
        CtClass cc = pool.get(aClass.getName());
        String[] typeClassNames = new String[method.getParameterCount()];
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (int index = 0; index < typeClassNames.length; index++) {
            typeClassNames[index] = parameterTypes[index].getName();
        }
        CtMethod ctMethod = cc.getDeclaredMethod(method.getName(), pool.get(typeClassNames));
        MethodInfo methodInfo = ctMethod.getMethodInfo();
        CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
        String[] paramNames = new String[ctMethod.getParameterTypes().length];
        LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
        if (attr != null) {
            int pos = Modifier.isStatic(ctMethod.getModifiers()) ? 0 : 1;
            for (int i = 0; i < paramNames.length; i++) {
                paramNames[i] = attr.variableName(i + pos);
            }
        }
        return paramNames;
    }

    public static String[][] getConstructorParameterNames(Class<?> aClass) throws NotFoundException {
        ClassPool pool = ClassPool.getDefault();
        CtClass cc = pool.get(aClass.getName());
        CtConstructor[] ctConstructors = cc.getConstructors();
        String[][] parameterNames = new String[ctConstructors.length][];
        for (int cIndex = 0; cIndex < parameterNames.length; cIndex++) {
            CtConstructor ctConstructor = ctConstructors[cIndex];
            MethodInfo methodInfo = ctConstructor.getMethodInfo();
            CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
            String[] paramNames = new String[ctConstructor.getParameterTypes().length];
            LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
            if (attr != null) {
                int pos = Modifier.isStatic(ctConstructor.getModifiers()) ? 0 : 1;
                for (int i = 0; i < paramNames.length; i++) {
                    paramNames[i] = attr.variableName(i + pos);
                }
                parameterNames[cIndex] = paramNames;
            }
        }
        return parameterNames;
    }
}
