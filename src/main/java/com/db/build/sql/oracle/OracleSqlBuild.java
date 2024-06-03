package com.db.build.sql.oracle;

import cn.hutool.core.util.StrUtil;
import com.db.TestModel;
import com.db.annotation.ColumnExtension;
import com.db.annotation.TableExtension;
import com.db.build.field.BaseFieldBuilder;
import com.db.build.field.ExtensionFieldBuilder;
import com.db.build.field.FieldBuilder;
import com.db.build.sql.ContextBuilder;
import com.db.build.sql.SqlBuilder;
import com.db.util.FieldUtils;
import com.db.util.ModelToTableUtils;
import com.util.GenericsUtil;

import java.util.Objects;

/**
 * @author yuanmengfan
 * @date 2022/7/24 15:14
 * @description
 */
public class OracleSqlBuild implements SqlBuilder {

    private ContextBuilder contextBuilder = new OracleContextBuilder();
    private FieldBuilder fieldBuilder;

    public OracleSqlBuild(FieldBuilder fieldBuilder) {
        this.fieldBuilder = fieldBuilder;
    }

    public OracleSqlBuild() {
        this.fieldBuilder = new BaseFieldBuilder();
    }

    @Override
    public String buildTableSql(Class<?> clazz) {
        return buildTableSql(clazz,true);
    }

    public String buildTableSql(Class<?> clazz, boolean isSuper) {
        // model不能为空
        Objects.requireNonNull(clazz, "MODEL MUST NOT NULL");
        StringBuffer sql = new StringBuffer();
        // 表名
        String tableName = ModelToTableUtils.getTableName(clazz).toUpperCase();
        // 主体内容
        String context = contextBuilder.context(fieldBuilder.getField(clazz, isSuper));
        // 注释内容
        String fieldNodes = getOracleFieldNotes(clazz, tableName, isSuper);

        // 生成建表的声明语句
        sql.append("-- START CREATE TABLE ").append(tableName).append("\n");

        // 表存在则删除表的的前置语句
        sql.append(String.format("DECLARE \n" +
                "      NUM   NUMBER; \n" +
                "BEGIN \n" +
                "      SELECT COUNT(1) INTO NUM FROM ALL_TABLES WHERE TABLE_NAME = '%1$s'; \n" +
                "      IF   NUM = 1   THEN \n" +
                "          EXECUTE IMMEDIATE 'DROP TABLE %1$s'; \n" +
                "      END IF; \n" +
                "END;\n", tableName));

        // 处理begin 后不能执行其他语句的问题
        sql.append("/\n");

        sql.append("CREATE TABLE ").append(tableName).append("(\n")
                .append(context)
                .append(") ;");
        sql.append("\n").append(fieldNodes);

        // 生成建表的声明语句
        sql.append("-- END CREATE TABLE ").append(tableName).append("\n\n");

        // 拿到List类型的字段利用递归，生成子表
        FieldUtils.getListTypeFields(clazz).forEach(field -> {
            try {
                // 拿到这些字段的类型的第一个泛型类型 根据这个泛型的类型生成对应的子表
                sql.append(buildTableSql(Class.forName(GenericsUtil.getGenericsTypeNameByFiledAndIndex(field, 0)), false));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
        return sql.toString();
    }

    /**
     * 生成添加Oracle注释的语句
     *
     * @param clazz     需要生成建表语句的对象
     * @param tableName 表名
     * @param isSuper   是否为主表
     * @return java.lang.String
     * @title getOracleFieldNotes
     * @author yuanmengfan
     * @date 2022/7/12 22:21
     */
    private String getOracleFieldNotes(Class<?> clazz, String tableName, boolean isSuper) {
        StringBuffer fieldNotes = new StringBuffer();
        TableExtension extension = clazz.getAnnotation(TableExtension.class);

        // 生成表注释
        if (extension != null && StrUtil.isNotBlank(extension.remark())) {
            fieldNotes.append(String.format("comment on table %s is '%s' ;\n", tableName, extension.remark()));
        }
        // 只为有remark值的字段添加注释
        fieldBuilder.getField(clazz, isSuper)
                .stream()
                .filter(field -> {
                    ColumnExtension fieldAnnotation = field.getAnnotation(ColumnExtension.class);
                    return fieldAnnotation != null && StrUtil.isNotBlank(fieldAnnotation.remark());
                }).forEach(field -> {
                    ColumnExtension fieldAnnotation = field.getAnnotation(ColumnExtension.class);
                    String fieldName = ModelToTableUtils.getFieldName(field).toUpperCase();
                    fieldNotes.append(String.format("comment on column %s.%s is '%s';\n"
                            , tableName, fieldName, fieldAnnotation.remark()));
                });
        return fieldNotes.toString();
    }


    public static void main(String[] args) {
        System.out.println(new OracleSqlBuild(new ExtensionFieldBuilder()).buildTableSql(TestModel.class));
    }
}
