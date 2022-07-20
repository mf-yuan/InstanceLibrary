package com.db;

import cn.hutool.db.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author yuanmengfan
 * @date 2022/7/13 13:24
 * @description 生成与数据库操作的对象
 */
public class SqlUtils {

    private static final Logger logger = LoggerFactory.getLogger(SqlUtils.class);

    /**
     * 生成Entity对象
     * @title CreateInsert
     * @param obj 需要生成的对象
     * @param isSuper 是否为主类
     * @param pid 主类ID
     * @return cn.hutool.db.Entity
     * @author yuanmengfan
     * @date 2022/7/20 14:05
     */
    public Entity CreateInsert(Object obj,boolean isSuper,String pid) {
        LocalDateTime now = LocalDateTime.now();
        if (obj != null) {
            // 根据obj生成一个具有对应表名的Entity对象
            Class<?> modelClass = obj.getClass();
            Entity entity = Entity.create(TableBuilder.getTableName(modelClass));

            // 拿到所需的字段
            List<Field> fields = TableBuilder.getFields(obj.getClass(), true,isSuper);

            fields.forEach(field -> {
                //判断该字段是否来自公共类 如果来着公共类由这里生成对应的值
                if (field.getDeclaringClass() == CommonModel.class) {
                    switch (field.getName()) {
                        case "id":
                            entity.set(field.getName(), UUID.randomUUID().toString());
                            break;
                        case "pid":
                            entity.set(field.getName(),pid);
                            break;
                        case "createDate":
                        case "updateDate":
                            entity.set(field.getName(),now);
                            break;
                    }
                } else {
                    try {
                        // 开放访问
                        field.setAccessible(true);
                        // 获取字段名
                        String fieldName = TableBuilder.getFieldName(field);
                        // 获取该字段的值
                        Object value = field.get(obj);
                        // 值为空的字段不添加
                        if (value != null) {
                            entity.set(fieldName, value);
                        }
                    } catch (IllegalAccessException e) {
                        logger.error("赋值错误", e);
                    }
                }
            });
            return entity;
        }
        return null;
    }

    /**
     * 连同子表一起生成Entity对象的集合
     * @title createInsertList
     * @param obj 需要生成的对象
     * @param pid 主类ID
     * @param isSuper 是否为主类
     * @return java.util.List<cn.hutool.db.Entity>
     * @author yuanmengfan
     * @date 2022/7/20 14:11
     */
    public List<Entity> createInsertList(Object obj,String pid,boolean isSuper) {
        if (obj != null) {

            // 生成主表Entity
            List<Entity> result = new ArrayList<>();
            Entity entity = CreateInsert(obj,isSuper,pid);
            logger.info(entity.toString());
            result.add(entity);

            // 拿到是List类型的字段 生成子表Entity
            TableBuilder.getFields(obj.getClass(), false,false).forEach(field -> {
                // 开放访问
                field.setAccessible(true);
                try {
                    // 利用迭代完成可重复判断子类
                    List<?> values = (List<?>) field.get(obj);
                    // 子类都带上父类的id
                    values.forEach(value -> result.addAll(createInsertList(value,entity.getStr("id"),false)));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            });
            return result;
        }
        return null;
    }
}
