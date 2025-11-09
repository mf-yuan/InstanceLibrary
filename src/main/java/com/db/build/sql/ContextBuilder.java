package com.db.build.sql;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author yuanmengfan
 * @date 2022/7/23 16:07
 * @description 生成建表语句中()中的内容
 */
public interface ContextBuilder {
    String context(List<Field> field);
}
