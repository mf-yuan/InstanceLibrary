package com.util;

import java.io.File;

/**
 * @author yuanmengfan
 * @date 2022/7/12 22:47
 * @description
 */
public class PathUtil {

    /**
     * 获取项目的绝对路径
     *
     * @return java.lang.String
     * @title getAbsolutePath
     * @author yuanmengfan
     * @date 2022/7/12 22:51
     */
    public static String getAbsolutePath() {
        // 发布到linux服务器中的路径会很奇怪
        // System.out.println(System.getProperty("user.dir"));

        return new File("").getAbsolutePath();
    }

    public static String getClassPath() {
        return Thread.currentThread().getContextClassLoader().getResource("").getPath();
    }


}
