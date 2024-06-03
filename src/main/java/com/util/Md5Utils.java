package com.util;

import cn.hutool.crypto.SecureUtil;

/**
 * @author yuanmengfan
 * @date 2021/10/19 2:29 下午
 * @description
 * MD5 加密后的位数有两种：16 位与 32 位。16 位实际上是从 32 位字符串中取中间的第 9 位到第 24 位的部分
 * MD5 加密后的字符串又分为大写与小写两种，也就是其中的字母是大写还是小写。
 *
 */
public class Md5Utils {

    /**
     * MD5加密 16位大写
     * @param data
     * @return
     */
    public static String md5_Upper_16(String data){
        return md5_Upper_32(data).substring(8,24);
    }

    /**
     * MD5加密 32位大写
     * @param data
     * @return
     */
    public static String md5_Upper_32(String data){
        return SecureUtil.md5(data).toUpperCase();
    }

    /**
     * MD5加密 16位小写
     * @param data
     * @return
     */
    public static String md5_Lower_16(String data){
        return md5_Lower_32(data).substring(8,24);
    }

    /**
     * MD5加密 32位小写
     * @param data
     * @return
     */
    public static String md5_Lower_32(String data){
        return SecureUtil.md5(data);
    }
}
