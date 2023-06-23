package com.util.converter;

import com.util.TemporalDateTimeUtils;

import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.List;

/**
 * @author yuanmengfan
 * @date 2023/5/28 22:54
 * @description
 */
public class StringConverter implements Converter<String> {

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
            TemporalAccessor.class
    );



    @Override
    public String convert(Object obj) {
        // 不在可以转换的白名单类
        if (!BLACK_CONVERT_LIST.contains(obj.getClass())) {
            return null;
        }

        if(obj instanceof TemporalAccessor){
            return TemporalDateTimeUtils.format((TemporalAccessor)obj);
        }else if(obj instanceof Date){
            return TemporalDateTimeUtils.format((Date) obj);
        }
        return obj.toString();
    }
}
