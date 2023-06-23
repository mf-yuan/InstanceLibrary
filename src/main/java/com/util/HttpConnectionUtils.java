package com.util;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.log.Log;
import org.springframework.util.StopWatch;

import java.util.Map;

/**
 * @author yuanmengfan
 * @date 2021/10/19 2:50 下午
 * @description  http 与 https 连接工具
 */
public class HttpConnectionUtils {

    private static final Log log = Log.get(HttpConnectionUtils.class);

    /**
     * 不带请求头与请求参数的GET请求
     * @param url 请求地址
     * @return
     */
    public static String doGet(String url){
        return getHttpRequest(HttpUtil.createGet(url));
    }

    /**
     * 带请求参数的GET请求
     * @param url 请求地址
     * @param params 请求参数
     * @return
     */
    public static String doGet(String url, Map<String,Object> params){
        return getHttpRequest(HttpUtil.createGet(url).form(params));
    }

    /**
     * 带请求头与请求参数的GET请求
     * @param url 请求地址
     * @param headers 请求头
     * @param params 请求参数
     * @return
     */
    public static String doGet(String url, Map<String,String> headers, Map<String,Object> params){
        return getHttpRequest(HttpUtil.createGet(url).addHeaders(headers).form(params));
    }

    public static String doGet(String url,String  body){
        return getHttpRequest(HttpUtil.createGet(url).body(body));
    }

    /**
     * 不带请求头与请求参数的POST请求
     * @param url 请求地址
     * @return
     */
    public static String doPost(String url){
        return getHttpRequest(HttpUtil.createPost(url));
    }

    /**
     * 带请求参数的POST请求
     * @param url 请求地址
     * @param params 请求参数
     * @return
     */
    public static String doPost(String url, Map<String,Object> params){
        return getHttpRequest(HttpUtil.createPost(url).form(params));
    }

    /**
     * 带请求参数与请求体的POST请求
     * @param url 请求地址
     * @param body 请求体
     *     1. 标准参数，例如 a=1&b=2 这种格式
     * 	   2. Rest模式，此时body需要传入一个JSON或者XML字符串，Hutool会自动绑定其对应的Content-Type
     * @return
     */
    public static String doPost(String url,String  body){
        return getHttpRequest(HttpUtil.createPost(url).body(body));
    }

    /**
     * 带请求头与请求参数的POST请求
     * @param url 请求地址
     * @param headers 请求头
     * @param params 请求参数
     * @return
     */
    public static String doPost(String url, Map<String,String> headers, Map<String,Object> params){
        return getHttpRequest(HttpUtil.createPost(url).addHeaders(headers).form(params));
    }

    private static String getHttpRequest(HttpRequest request){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        log.info("请求URL： "+ request.getUrl());
        log.info("请求头： "+ request.headers());
        log.info("请求参数： "+ request.form());
        String body = request.execute().body();
        stopWatch.stop();
        log.info("请求结果： "+ body );
        log.info("耗时： "+ stopWatch.getLastTaskTimeMillis() +"毫秒" );
        return body;
    }
}
