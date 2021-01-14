package com.top.core.ioc;

import lombok.Data;

import java.util.Objects;

/**
 * @author lubeilin
 * @date 2021/1/8
 */
@Data
public class BeanInfo {
    private String beanName;
    private String otherName;
    private Class<?> beanType;
    private Class<?> configType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BeanInfo)) return false;
        BeanInfo beanInfo = (BeanInfo) o;
        return Objects.equals(getBeanName(), beanInfo.getBeanName()) &&
                Objects.equals(getOtherName(), beanInfo.getOtherName()) &&
                Objects.equals(getBeanType(), beanInfo.getBeanType()) &&
                Objects.equals(getConfigType(), beanInfo.getConfigType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBeanName(), getOtherName(), getBeanType(), getConfigType());
    }
}
