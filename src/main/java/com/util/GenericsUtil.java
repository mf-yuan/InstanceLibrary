package com.util;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yuanmengfan
 * @date 2022/7/14 00:06
 * @description 泛型工具类
 */
public class GenericsUtil {

    /**
     * 拿到下标为index的泛型类型
     * @title getGenericsTypeByFiledAndIndex
     * @param field 需要拿到泛型的字段
     * @param index 拿到第几个从0开始
     * @return java.lang.String
     * @author yuanmengfan
     * @date 2022/7/20 14:18
     */
    public static Class<?> getGenericsTypeByFiledAndIndex(Field field, int index) throws ClassNotFoundException {
        return Class.forName(getGenericsTypeNameByFiledAndIndex(field,index));
    }

    /**
     * 拿到所有该field的所有泛型集合
     * @title getGenericsTypeNameByFiledAll
     * @param field
     * @return java.util.List<java.lang.String>
     * @author yuanmengfan
     * @date 2022/7/20 14:19
     */
    public static List<Class<?>> getGenericsTypeByFiledAll(Field field){
        return getGenericsTypeNameByFiledAll(field).stream().map(fieldName -> {
            try {
                return Class.forName(fieldName);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }).collect(Collectors.toList());
    }


    /**
     * 拿到下标为index的泛型名
     * @title getGenericsTypeNameByFiledAndIndex
     * @param field 需要拿到泛型的字段
     * @param index 拿到第几个从0开始
     * @return java.lang.String
     * @author yuanmengfan
     * @date 2022/7/20 14:18
     */
    public static String getGenericsTypeNameByFiledAndIndex(Field field,int index){
        ParameterizedType genericType = (ParameterizedType) field.getGenericType();
        return genericType.getActualTypeArguments()[index].getTypeName();
    }

    /**
     * 拿到所有该field的所有泛型名集合
     * @title getGenericsTypeNameByFiledAll
     * @param field
     * @return java.util.List<java.lang.String>
     * @author yuanmengfan
     * @date 2022/7/20 14:19
     */
    public static List<String> getGenericsTypeNameByFiledAll(Field field){
        ParameterizedType genericType = (ParameterizedType) field.getGenericType();
        return Arrays.stream(genericType.getActualTypeArguments()).map(Type::getTypeName).collect(Collectors.toList());
    }

}
