package com.util.converter;


import cn.hutool.core.util.StrUtil;
import com.util.NumberUtils;
import com.util.TemporalDateTimeUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.List;

/**
 * @author yuanmengfan
 * @date 2023/5/28 21:04
 * @description
 */
public class NumberConverter implements Converter<Number>{

    /**
     * 转换后的类型
     */
    private final Class<?> targetClass;

    public NumberConverter(Class<?> targetClass){
        this.targetClass = targetClass;
    }

    /**
     * 支持转换的列表
     */
    private final static List<Class<?>> BLACK_CONVERT_LIST = List.of(
            byte.class,
            short.class,
            int.class,
            long.class,
            float.class,
            double.class,
            Byte.class,
            Short.class,
            Integer.class,
            Long.class,
            Float.class,
            Double.class,
            String.class,
            Date.class,
            LocalDate.class,
            LocalDateTime.class,
            LocalTime.class,
            TemporalAccessor.class
    );

    @Override
    public Number convert(Object obj) {
        if (!BLACK_CONVERT_LIST.contains(obj.getClass())) {
            return null;
        }
        Number value = null;
        if (obj instanceof String) {
            String str = (String) obj;
            if (StrUtil.isBlank(str)) {
                return null;
            }
            if (!NumberUtils.isNumber(str)) {
                return null;
            }
            value = Double.parseDouble(str);
        }else if (obj instanceof Date){
            value = ((Date)obj).getTime();
        }else if (obj instanceof TemporalAccessor){
            value = TemporalDateTimeUtils.getEpochMilli((TemporalAccessor) obj);
        }else {
            value = (Number) obj;
        }
        if (targetClass == Byte.class || targetClass == byte.class) {
            return value.byteValue();
        } else if (targetClass == Short.class || targetClass == short.class) {
            return value.intValue();
        } else if (targetClass == Integer.class || targetClass == int.class) {
            return value.intValue();
        } else if (targetClass == Long.class || targetClass == long.class) {
            return value.longValue();
        } else if (targetClass == Float.class || targetClass == float.class) {
            return value.floatValue();
        } else if (targetClass == Double.class || targetClass == double.class) {
            return value.doubleValue();
        }
        return null;
    }
}

