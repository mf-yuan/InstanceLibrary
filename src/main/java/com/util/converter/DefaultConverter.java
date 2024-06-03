package com.util.converter;


/**
 * @author yuanmengfan
 * @date 2023/5/28 20:59
 * @description
 */
public class DefaultConverter implements Converter<Object>{

    /**
     * 将obj数据转换为 T
     * @param obj
     * @return
     */
    @Override
    public Object convert(Object obj){
        return  obj;
    };
}
