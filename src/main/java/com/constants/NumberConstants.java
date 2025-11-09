package com.constants;

import java.util.List;

/**
 * @author yuanmengfan
 * @date 2023/5/28 22:20
 * @description 数字常用常量
 */
public interface NumberConstants {
    /**
     * 数字类型
     */
    List<Class<?>> NUMBER_TYPE_LIST = List.of(
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
            Double.class
    );
}
