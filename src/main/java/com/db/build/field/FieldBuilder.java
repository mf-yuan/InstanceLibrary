package com.db.build.field;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author yuanmengfan
 * @date 2022/7/23 15:47
 * @description 字段生成器
 */
public interface FieldBuilder {
    /**
     * 根据不同的规则生成不同的字段集合
     * @title getField
     * @param clazz model对象的class
     * @param isSuper 是否为主类
     * @return java.util.List<java.lang.reflect.Field>
     * @author yuanmengfan
     * @date 2022/7/24 15:32
     */
    List<Field> getField(Class<?> clazz,boolean isSuper);
}
