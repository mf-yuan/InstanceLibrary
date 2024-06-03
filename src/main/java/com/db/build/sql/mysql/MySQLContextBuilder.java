package com.db.build.sql.mysql;

import cn.hutool.core.util.StrUtil;
import com.constants.DefaultValueConstants;
import com.db.DbType;
import com.db.FieldToColumnType;
import com.db.annotation.ColumnExtension;
import com.db.build.sql.ContextBuilder;
import com.db.util.ModelToTableUtils;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author yuanmengfan
 * @date 2022/7/23 16:08
 * @description
 */
public class MySQLContextBuilder implements ContextBuilder {

    @Override
    public String context(List<Field> fields) {
        StringBuffer context = new StringBuffer();
        fields.forEach(field -> {
            // 根据对应的字段类型与数据库类型获取匹配的FieldToColumnType
            FieldToColumnType columnTypeByField = FieldToColumnType.getColumnTypeByField(field.getType().getName(), DbType.MySQL);
            // 字段名
            String fieldName = field.getName();
            // 数据库列类型
            String columnType = columnTypeByField.getColumnType();
            // 字段类型长度
            String columnLength = StrUtil.isBlank(columnTypeByField.getColumnLength()) ?
                    "" : String.format("(%s)", columnTypeByField.getColumnLength());
            // 字段是否为主键
            boolean idPresent = false;
            // 字段是否不能为NULL
            boolean isNotNull = false;
            // 字段默认值
            String defaultValue = "";
            // 数据库字段注释
            String columnRemark = "";

            // 判断该字段是否有扩展属性的这个注解 如果有个话 针对每个字段进行特殊处理
            ColumnExtension extension = field.getAnnotation(ColumnExtension.class);
            if (extension != null) {
                // 扩展属性的 isNotNull 为true 时defaultValue不能为 DefaultValues.NULL
                if (extension.isNotNull() && extension.defaultValue().equals(DefaultValueConstants.NULL))
                    throw new RuntimeException("Field " + fieldName +
                            "! When Extension IsNotNull are true, DefaultValue cannot be DefaultValues.NULL.");
                fieldName = ModelToTableUtils.getFieldName(field);
                columnLength = extension.length() == Integer.MIN_VALUE ?
                        columnLength : String.format("(%s)", extension.length());
                idPresent = extension.isId();
                isNotNull = extension.isNotNull();
                // 可自定义默认值
                defaultValue = ModelToTableUtils.getDefaultValue(extension.defaultValue());
                columnRemark = extension.remark();
            }
            context.append("\t")
                    .append(" `").append(fieldName).append("` ")
                    .append(columnType)
                    .append(columnLength)
                    // 该字段是主键的话添加 PRIMARY KEY这个关键字
                    .append(idPresent ? " PRIMARY KEY" : "")
                    // 该字段如果不为NULL添加 NOT NULL这个关键字
                    .append(isNotNull ? " NOT NULL" : "")
                    .append(StrUtil.isBlank(defaultValue) ? "" : (" DEFAULT " + defaultValue))
                    .append(" COMMENT '").append(columnRemark).append("'")
                    // 判断是否为最后一个字段 做最后一个逗号的处理
                    .append(field == fields.get(fields.size() - 1) ? "" : ",")
                    .append("\n");
        });
        return context.toString();
    }
}
