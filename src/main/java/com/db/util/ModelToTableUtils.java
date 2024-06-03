package com.db.util;

import cn.hutool.core.util.StrUtil;
import com.constants.DefaultValueConstants;
import com.db.annotation.ColumnExtension;
import com.db.annotation.TableExtension;

import java.lang.reflect.Field;

/**
 * @author yuanmengfan
 * @date 2022/7/23 13:52
 * @description
 */
public class ModelToTableUtils {

    /**
     * 处理特殊的默认值
     *
     * @param defaultValue
     * @return java.lang.String
     * @title getDefaultValue
     * @author yuanmengfan
     * @date 2022/7/12 22:20
     */
    public static String getDefaultValue(String defaultValue) {
        if (StrUtil.isBlank(defaultValue)) return "";
        switch (defaultValue) {
            case DefaultValueConstants.NULL:
            case DefaultValueConstants.EMPTY_STRING:
            case DefaultValueConstants.NULL_STRING:
            case DefaultValueConstants.TRUE:
            case DefaultValueConstants.FALSE:
                return defaultValue;
            default:
                return "'" + defaultValue + "'";
        }
    }

    /**
     * 根据Model类获取生成的表名
     *
     * @param model Model对象
     * @return java.lang.String
     * @title getTableName
     * @author yuanmengfan
     * @date 2022/7/12 21:59
     */
    public static String getTableName(Class<?> model) {
        String tableName = "";
        // 当model 有 Table 这个注解时 且 Table注解的name值不是空时 直接取tableName为 生成表名
        // 否则 类名 当表名
        if (model.isAnnotationPresent(TableExtension.class) && StrUtil.isNotBlank(model.getAnnotation(TableExtension.class).name())) {
            tableName = model.getAnnotation(TableExtension.class).name();
        } else {
            tableName = model.getSimpleName();
        }
        return tableName;
    }


    /**
     * 根据field生成字段名
     *
     * @return java.lang.String
     * @title getTableName
     * @author yuanmengfan
     * @date 2022/7/12 21:59
     */
    public static String getFieldName(Field field) {
        String fieldName = field.getName();
        ColumnExtension extension = field.getAnnotation(ColumnExtension.class);
        if (extension != null) {
            fieldName = (StrUtil.isNotBlank(extension.columnName()) ? extension.columnName() : fieldName);
        }
        return fieldName;
    }
}
