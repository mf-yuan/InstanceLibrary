package com.db.build.sql;

/**
 * @author yuanmengfan
 * @date 2022/7/23 14:05
 * @description SQL生成器
 */
public interface SqlBuilder {
    /**
     * 根据model对象生成建表语句 可连同生成子表
     * @title buildTableSql
     * @param clazz
     * @return java.lang.String
     * @author yuanmengfan
     * @date 2022/7/24 15:43
     */
    String buildTableSql(Class<?> clazz);
}
