package com.db.build.sql.mysql;


import com.db.TestModel;
import com.db.annotation.TableExtension;
import com.db.build.field.BaseFieldBuilder;
import com.db.build.field.ExtensionFieldBuilder;
import com.db.build.field.FieldBuilder;
import com.db.build.sql.ContextBuilder;
import com.db.build.sql.SqlBuilder;
import com.db.util.FieldUtils;
import com.db.util.ModelToTableUtils;
import com.util.GenericsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * @author yuanmengfan
 * @date 2022/7/23 15:00
 * @description
 */
public class MySQLSqlBuild implements SqlBuilder {
    private static final Logger logger = LoggerFactory.getLogger(MySQLSqlBuild.class);

    private ContextBuilder contextBuilder = new MySQLContextBuilder();
    private FieldBuilder fieldBuilder;

    public MySQLSqlBuild(FieldBuilder fieldBuilder) {
        this.fieldBuilder = fieldBuilder;
    }

    public MySQLSqlBuild() {
        this.fieldBuilder = new BaseFieldBuilder();
    }

    @Override
    public String buildTableSql(Class<?> clazz) {
        return buildTableSql(clazz, true);
    }

    public String buildTableSql(Class<?> clazz, boolean isSuper) {
        // model不能为空
        Objects.requireNonNull(clazz, "MODEL MUST NOT NULL");

        StringBuffer sql = new StringBuffer();

        TableExtension extension = clazz.getAnnotation(TableExtension.class);

        // 表注释
        String columnRemark = extension == null ? "" : extension.remark();
        // 表名
        String tableName = ModelToTableUtils.getTableName(clazz);
        // 主体内容
        String context = contextBuilder.context(fieldBuilder.getField(clazz, isSuper));

        // 生成建表的声明语句
        sql.append("-- START CREATE TABLE ").append(tableName).append("\n");

        // 前置处理表存在的导致表创建不了的Sql
        sql.append("DROP TABLE IF EXISTS `").append(tableName).append("`;\n");

        // 构建CREATE TABLE 语句
        sql.append("CREATE TABLE ").append("`").append(tableName).append("`").append("(\n")
                .append(context)
                .append(") ").append(" COMMENT '").append(columnRemark).append("';").append("\n");

        // 生成建表的声明语句
        sql.append("-- END CREATE TABLE ").append(tableName).append("\n\n");

        // 拿到List类型的字段利用递归，生成子表
        FieldUtils.getListTypeFields(clazz).forEach(field -> {
            // 拿到这些字段的类型的第一个泛型类型 根据这个泛型的类型生成对应的子表
            try {
                sql.append(buildTableSql(Class.forName(GenericsUtil.getGenericsTypeNameByFiledAndIndex(field, 0)), false));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
        return sql.toString();
    }


    public static void main(String[] args) {
        System.out.println(new MySQLSqlBuild(new ExtensionFieldBuilder()).buildTableSql(TestModel.class));
    }
}
