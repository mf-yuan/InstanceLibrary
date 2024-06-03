package com.util;

import com.constants.NumberConstants;
import com.constants.RegexConstants;

import java.util.regex.Pattern;

/**
 * @author yuanmengfan
 * @date 2023/5/28 21:44
 * @description 数字工具类
 */
public class NumberUtils {
    /**
     * 是否为数字字符串
     * @param str
     * @return
     */
    public static boolean isNumber(String str){
        return Pattern.matches(RegexConstants.NUMBER_REGEX,str);
    }


    /**
     * 是否为数字类型
     * @param objClass
     * @return
     */
    public static boolean isNumberType(Class<?> objClass){
        return NumberConstants.NUMBER_TYPE_LIST.contains(objClass);
    }

    /**
     * 是否为数字类型
     * @param obj
     * @return
     */
    public static boolean isNumberType(Object obj){
        return isNumberType(obj.getClass());
    }


}
