package com.db.util;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author yuanmengfan
 * @date 2022/7/24 14:56
 * @description
 */
public class FieldUtils {
    public static List<Field> getListTypeFields(Class<?> clazz) {
        return getBaseOrListFields(clazz,false);
    }

    public static List<Field> getBaseField(Class<?> clazz) {
        return getBaseOrListFields(clazz,true);
    }

    private static List<Field> getBaseOrListFields(Class<?> clazz,boolean isBase){
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(isBase ? filterFieldPredicate() : Predicate.not(filterFieldPredicate()))
                .collect(Collectors.toList());
    }
    /**
     * 生成一个函数式接口 过滤字段类型为List的字段
     *
     * @return java.util.function.Predicate<java.lang.reflect.Field>
     * @title filterFieldPredicate
     * @author yuanmengfan
     * @date 2022/7/20 14:00
     */
    public static Predicate<Field> filterFieldPredicate() {
        return field -> !field.getType().getName().endsWith("List");
    }
}

