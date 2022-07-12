package com.db;

import com.db.annotation.ColumnExtension;
import com.db.annotation.TableExtension;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
/**
 * @author yuanmengfan
 * @date 2022/7/9 17:26
 * @description
 */

@Getter
@Setter
@TableExtension(name =  "t_testModel",remark = "测试表")
public class TestModel {
    @ColumnExtension(isId = true,length = 255)
    private String id;
    @ColumnExtension(columnName = "userName",defaultValue = "name1", remark = "姓名")
    private String name;
    @ColumnExtension(remark = "年龄", length = 10)
    private int age;
    @ColumnExtension(defaultValue = DefaultValues.NULL, remark = "工资")
    private Double salary;
    @ColumnExtension(remark = "生日")
    private Date birth;
    @ColumnExtension(defaultValue=DefaultValues.TRUE,remark = "是否禁用")
    private boolean disabled;

    private LocalDate localDate;

    private LocalDateTime localDateTime;

    private LocalTime localTime;

    private List<Date> list;
}
