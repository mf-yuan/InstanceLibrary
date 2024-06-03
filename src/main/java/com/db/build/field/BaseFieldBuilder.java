package com.db.build.field;

import com.db.util.FieldUtils;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author yuanmengfan
 * @date 2022/7/23 15:49
 * @description 生成所以非List字段的集合 isSuper这个无效果
 */
public class BaseFieldBuilder implements FieldBuilder {
    @Override
    public List<Field> getField(Class<?> clazz, boolean isSuper) {
        return FieldUtils.getBaseField(clazz);
    }
}
