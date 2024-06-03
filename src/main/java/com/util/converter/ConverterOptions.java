package com.util.converter;

import lombok.Builder;
import lombok.Data;

/**
 * @author yuanmengfan
 * @date 2023/6/23 15:12
 * @description 转换可选项
 */
@Data
@Builder
public class ConverterOptions {
    /**
     * 忽略大小写
     */
    private boolean ignoreCase = false;
    /**
     * 忽略常量
     */
    private boolean ignoreConstants = false;
    /**
     * 忽略静态变量
     */
    private boolean ignoreStatic = false;
}
