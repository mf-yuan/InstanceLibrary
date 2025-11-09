package com.util;

import com.alibaba.fastjson.JSONObject;
import com.model.Charsets;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author yuanmengfan
 * @date 2021/10/28 11:59 上午
 * @description
 */
public class UrlUtils {

    /**
     * 判断是否为url的正则表达式
     */
    private static final String pattern = "[a-zA-z]+://[^\\s]*";

    /**
     * 判断是否为正确的url
     *
     * @param url
     * @return
     */
    public static boolean isUrl(String url) {
        return url.toLowerCase().matches(pattern);
    }

    /**
     * 判断url是否没有参数
     *
     * @param url
     * @return
     */
    public static boolean isExistParam(String url) {
        if (isUrl(url)) {
            if (url.split("\\?", 2).length == 2) {
                return true;
            }
            return false;
        }
        throw new RuntimeException("url不正确");
    }

    /**
     * 获取uri协议
     *
     * @param url
     * @return
     */
    public static String getNetworkProtocol(String url) {

        if (isUrl(url)) {
            String[] split = url.split("://");
            return split[0];
        }
        throw new RuntimeException("url不正确");
    }

    /**
     * 获取url主体内容  http://www.baidu.com?JSESSIONID=*       http://www.baidu.com
     *
     * @param url
     * @return
     */
    public static String getUrlSubject(String url) {

        if (isUrl(url)) {
            String[] split = url.split("\\?");
            return split[0];
        }
        throw new RuntimeException("url不正确");
    }

    /**
     * 获取url参数内容  http://www.baidu.com?JSESSIONID=*       JSESSIONID=*
     *
     * @param url
     * @return
     */
    public static String getUrlParam(String url) {
        if (isExistParam(url)) {
            //如果满足正则表达式 split只会把字符串分为两个 按第一个满足的去分割
            String[] split = url.split("\\?", 2);
            if (split.length == 2) {
                return split[1];
            }
        }
        throw new RuntimeException("url没有参数");
    }

    /**
     * 获取url的参数列表
     *
     * @param url              地址
     * @param isNullParamValue 是否获取为null的参数值
     * @return
     */
    private static Map<String, Object> getUrlParamToMap(String url, boolean isNullParamValue) {
        String urlParam = getUrlParam(url);
        System.out.println(urlParam);
        Map<String, Object> params = new HashMap<>(8);
        for (String entity : urlParam.split("&")) {
            String[] param = entity.split("=", 2);
            if (param.length != 2) {
                if (isNullParamValue) {
                    params.put(param[0], "");
                }
                continue;
            }
            params.put(param[0], param[1]);
        }
        return params;
    }

    /**
     * 获取url的参数列表  包括参数中没有值的
     *
     * @param url 地址
     * @return
     */
    public static Map<String, Object> getUrlParamToMap(String url) {
        return getUrlParamToMap(url, true);
    }

    /**
     * 获取url的参数列表 只拿到有值的参数列表
     *
     * @param url 地址
     * @return
     */
    public static Map<String, Object> getUrlParamNotNullToMap(String url) {
        return getUrlParamToMap(url, false);
    }

    /**
     * URL加密
     *
     * @param url      信息
     * @param charsets 字符集
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String encode(String url, Charsets charsets) throws UnsupportedEncodingException {
        return URLEncoder.encode(url, charsets.getCharset());
    }

    /**
     * 使用UTF-8的加密方式
     *
     * @param url
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String encode(String url) throws UnsupportedEncodingException {
        return encode(url, Charsets.UTF_8);
    }

    /**
     * URL解密
     *
     * @param url      信息
     * @param charsets 字符集
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String decode(String url, Charsets charsets) throws UnsupportedEncodingException {
        return URLDecoder.decode(url, charsets.getCharset());
    }

    /**
     * 使用UTF-8的解密方式
     *
     * @param url
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String decode(String url) throws UnsupportedEncodingException {
        return decode(url, Charsets.UTF_8);
    }

    /**
     * Json 转 key=value&key1=value1的格式
     *
     * @param json
     * @param isOrder 是否需要排序 默认按JSON的key排序
     * @return java.lang.String
     * @title jsonToParamString
     * @author yuanmengfan
     * @date 2022/9/19 13:09
     */
    public static String jsonToParamString(JSONObject json, boolean isOrder) {
        if (json != null) {
            StringBuffer result = new StringBuffer();
            Set<Map.Entry<String, Object>> entries = json.entrySet();
            (isOrder ? entries.stream().sorted(Map.Entry.comparingByKey()) : entries.stream())
                    .forEach((entry) -> result.append(entry.getKey()).append("=").append(entry.getValue()).append("&"));
            if (result.length() > 0) result.deleteCharAt(result.length() - 1);
            return result.toString();
        }
        return null;
    }

    public static String jsonToParamString(JSONObject json) {
        return jsonToParamString(json, false);
    }


}
