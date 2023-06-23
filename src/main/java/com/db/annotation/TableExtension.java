package com.db.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author yuanmengfan
 * @date 2022/7/21 23:12
 * @description
 * 字段的扩展属性
 * /@Retention 的用法：指示注释类型的注释要保留多久。如果注释类型声明中不存在 Retention 注释，则保留策略默认为 RetentionPolicy.CLASS。
 * RetentionPolicy.CLASS 编译器将把注释记录在类文件中，但在运行时 VM 不需要保留注释
 * RetentionPolicy.RUNTIME 编译器将把注释记录在类文件中，在运行时 VM 将保留注释，因此可以反射性地读取
 * RetentionPolicy.SOURCE 编译器要丢弃的注释
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TableExtension {

    // 自定义的表名
    String name() default "";

    // 表注释
    String remark() default "";


    // 字符集
    String charset() default "";

}
