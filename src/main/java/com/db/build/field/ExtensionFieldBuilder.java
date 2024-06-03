package com.db.build.field;

import com.db.CommonModel;
import com.db.util.FieldUtils;
import com.db.util.ModelToTableUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yuanmengfan
 * @date 2022/7/24 14:51
 * @description 生成带有扩展字段的非List字段集合
 */
public class ExtensionFieldBuilder implements FieldBuilder{

    @Override
    public List<Field> getField(Class<?> clazz, boolean isSuper) {
        // 根据是否为主表生成所有字段
        List<Field> result = new ArrayList<>(Arrays.asList(isSuper ? CommonModel.class.getFields() : CommonModel.class.getDeclaredFields()));

        // 拿到clazz中的所有非List类型的字段
        List<Field> fields = FieldUtils.getBaseField(clazz);

        System.out.println(fields);

        // 根据clazz生成一个只有字段名的集合
        List<String> fieldNames = fields.stream()
                .map(ModelToTableUtils::getFieldName)
                .collect(Collectors.toList());


        // 判断下clazz中是否已有CommonModel同名的字段 如果有个话需要在result中替换掉CommonModel中的
        for (int i = 0; i < result.size(); i++) {
            Field field = result.get(i);
            if (fieldNames.contains(field.getName())) {
                try {
                    result.set(i, clazz.getDeclaredField(field.getName()));
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }
        }

        // 根据CommonModel生成一个只有字段名的集合
        List<String> commonFieldNames = result.stream()
                .map(ModelToTableUtils::getFieldName)
                .collect(Collectors.toList());

        // 把clazz添加进result中，且排除以及存在的字段
        result.addAll(fields.stream()
                .filter(field -> !commonFieldNames.contains(field.getName()))
                .collect(Collectors.toList()));

        return result;
    }
}
