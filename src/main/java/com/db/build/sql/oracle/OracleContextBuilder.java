package com.db.build.sql.oracle;

import cn.hutool.core.util.StrUtil;
import com.db.DbType;
import com.db.DefaultValues;
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
public class OracleContextBuilder implements ContextBuilder {

    @Override
    public String context(List<Field> fields) {
        StringBuffer context = new StringBuffer();

        fields.forEach(field -> {
            FieldToColumnType columnTypeByField = FieldToColumnType.getColumnTypeByField(field.getType().getName(), DbType.Oracle);
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
            ColumnExtension extension = field.getAnnotation(ColumnExtension.class);
            if (extension != null) {
                if (extension.isNotNull() && extension.defaultValue().equals(DefaultValues.NULL))
                    throw new RuntimeException(" Field " + fieldName +
                            "! When Extension IsNotNull are true, DefaultValue cannot be DefaultValues.NULL.");

                fieldName = ModelToTableUtils.getFieldName(field);
                columnLength = extension.length() == Integer.MIN_VALUE ?
                        columnLength : String.format("(%s)", extension.length());
                idPresent = extension.isId();
                isNotNull = extension.isNotNull();
                defaultValue = ModelToTableUtils.getDefaultValue(extension.defaultValue());
            }
            context.append("\t")
                    .append(" ").append(fieldName.toUpperCase()).append(" ")
                    .append(columnType)
                    .append(columnLength)
                    .append(idPresent ? " PRIMARY KEY" : "")
                    .append(StrUtil.isBlank(defaultValue) ? "" : (" DEFAULT " + defaultValue))
                    .append(isNotNull ? " NOT NULL" : "")
                    // 判断是否为最后一个字段 做最后一个逗号的处理
                    .append(field == fields.get(fields.size() - 1) ? "" : ",")
                    .append("\n");
        });
        return context.toString();
    }



}
