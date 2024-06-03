package com.util.converter;


/**
 * @author yuanmengfan
 * @date 2023/5/28 20:59
 * @description
 */
public interface Converter<T> {

    /**
     * 将obj数据转换为 T
     * @param obj
     * @return
     */
    T convert(Object obj);
}
