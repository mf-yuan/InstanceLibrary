package com.util;

import com.util.converter.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.map.CaseInsensitiveMap;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * @author yuanmengfan
 * @date 2023/5/28 15:29
 * @description Java Object To Map
 * Map To Java Object
 */
@Slf4j
public class BeanUtils implements Serializable {

    /**
     * 对象转换为Map
     * @param obj
     * @return
     */
    public static Map<String, Object> toMap(Object obj) {
       return  toMap( obj, ConverterOptions.builder().build());
    }

    /**
     * 对象转换为Map
     * @param obj
     * @param converterOptions 可选地设置项
     * @return
     */
    public static Map<String, Object> toMap(Object obj, ConverterOptions converterOptions) {
        if (Objects.isNull(obj)) {
            return null;
        }

        Field[] fields = obj.getClass().getDeclaredFields();
        Map<String, Object> result = new HashMap<>(fields.length);
        if (converterOptions != null) {
            fields = Arrays.stream(fields).filter(field ->{
                // 是否开启忽略常量
                if(converterOptions.isIgnoreConstants() && Modifier.isFinal(field.getModifiers())){
                    return false;
                }

                // 是否开启忽略静态变量
                if(converterOptions.isIgnoreStatic() && Modifier.isStatic(field.getModifiers())){
                    return false;
                }
                return true;
            }).toArray(Field[]::new);

            // 如果忽略大小写则使用CaseInsensitiveMap 来替换
            if (converterOptions.isIgnoreCase()) {
                result = new CaseInsensitiveMap<>(fields.length);
            }
        }

        for (Field field : fields) {
            // 强制设置为可访问
            field.setAccessible(true);
            String name = field.getName();
            try {
                Object value = field.get(obj);
                result.put(name, value);
            } catch (IllegalAccessException e) {
                log.warn("Get "+ name +" Field Exception occurred", e);
            }
        }
        return result;
    }

    /**
     * map 转换为 bean
     * @param map
     * @param targetClass 目标对象类型
     * @param ignoreCase 是否忽略大小写
     * @return
     */
    public static <T> T toBean(Map<String, Object> map, Class<T> targetClass,boolean ignoreCase) {
        T result = null;
        try {
            result = targetClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            // 可能因为某种情况创建失败
            log.warn("Not Constructors " +targetClass.getTypeName(),e);
            return null;
        }

        if (map == null || map.isEmpty() ) {
            return result;
        }

        // 如果忽略大写些则使用CaseInsensitiveMap
        if(ignoreCase){
            map = new CaseInsensitiveMap<>(map);
        }

        Field[] fields = targetClass.getDeclaredFields();
        for (Field field : fields) {
            String name = field.getName();
            if (!map.containsKey(name)) {
                continue;
            }
            // 获取转换后的值
            Object value = convert(map.get(name), field.getType());
            field.setAccessible(true);
            try {
                field.set(result, value);
            } catch (IllegalAccessException e) {
                log.warn("Set "+ name +" Field Exception occurred",e);
            }
        }
        return result;
    }
    /**
     * 不忽略大小写 map 转换为 bean
     * @param map
     * @param targetClass 目标对象类型
     * @return
     */
    public static <T> T toBean(Map<String, Object> map, Class<T> targetClass) {
        return toBean(map,targetClass,false);
    }


    /**
     * 将obj类的属性copy值target中
     * @param obj
     * @param target 目标对象
     */
    public static void copyProperties(Object obj, Object target) {
        Map<String, PropertyDescriptor> targetPropertyDescriptorMap = getPropertyDescriptorMap(target.getClass());

        Map<String, PropertyDescriptor> readPropertyDescriptorMap = getPropertyDescriptorMap(obj.getClass());

        targetPropertyDescriptorMap.values()
                .stream()
                // 过滤掉不可写的字段
                .filter(propertyDescriptor -> !Objects.isNull(propertyDescriptor.getWriteMethod()))
                // 过滤掉不存在与obj中的字段 并且过滤掉不可读的字段
                .filter(propertyDescriptor -> {
                    if (!readPropertyDescriptorMap.containsKey(propertyDescriptor.getName())) {
                        return false;
                    }
                    PropertyDescriptor readPropertyDescriptor = readPropertyDescriptorMap.get(propertyDescriptor.getName());
                    return readPropertyDescriptor != null && readPropertyDescriptor.getReadMethod() != null;
                }).forEach(propertyDescriptor -> {
                    PropertyDescriptor readPropertyDescriptor = readPropertyDescriptorMap.get(propertyDescriptor.getName());
                    try {
                        Object readValue = readPropertyDescriptor.getReadMethod().invoke(obj);
                        Object value = convert(readValue, propertyDescriptor.getPropertyType());
                        readPropertyDescriptor.getReadMethod().invoke(obj);
                        propertyDescriptor.getWriteMethod().invoke(target, value);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        log.warn("Copy "+ propertyDescriptor.getName() +" Field Exception occurred",e);
                    }
                });
    }


    /**
     * 排除掉class与serialVersionUID属性的所有属性
     *
     * @param targetClass
     * @return
     */
    public static PropertyDescriptor[] getPropertyDescriptors(Class<?> targetClass) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(targetClass);
            return Arrays.stream(beanInfo.getPropertyDescriptors())
                    .filter(property -> !"class".equals(property.getName()) && !"serialVersionUID".equals(property.getName()))
                    .toArray(PropertyDescriptor[]::new);
        } catch (IntrospectionException e) {
            log.warn("Get Property Fail",e);
        }
        return null;
    }


    /**
     * 排除掉class与serialVersionUID属性的所有属性并转换为Map  key为属性名 value为{@link java.beans.PropertyDescriptor}
     *
     * @param targetClass
     * @return
     */
    public static Map<String, PropertyDescriptor> getPropertyDescriptorMap(Class<?> targetClass) {
        PropertyDescriptor[] propertyDescriptors = getPropertyDescriptors(targetClass);
        if (propertyDescriptors == null || propertyDescriptors.length <= 0) {
            return Collections.emptyMap();
        }
        Map<String, PropertyDescriptor> result = new HashMap<>(propertyDescriptors.length);
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            result.put(propertyDescriptor.getName(), propertyDescriptor);
        }
        return result;
    }

    /**
     * 获取转换后的值
     * @param obj
     * @param targetClass
     * @param <T>
     * @return
     */
    private static <T> T convert(Object obj, Class<T> targetClass) {
        Converter<T> converter = (Converter<T>) getAppropriateConvert(targetClass);
        return converter.convert(obj);
    }

    /**
     * 根据目标类型获取到合适的转换器
     * @param targetClass 目标类型
     * @return
     */
    private static Converter<?> getAppropriateConvert(Class<?> targetClass) {
        if (NumberUtils.isNumberType(targetClass)) {
            return new NumberConverter(targetClass);
        } else if (targetClass == String.class) {
            return new StringConverter();
        }
        return new DefaultConverter();
    }
}
