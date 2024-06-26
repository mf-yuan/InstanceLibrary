package com.db;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.db.ds.DSFactory;
import cn.hutool.setting.Setting;
import com.constants.DefaultValueConstants;
import com.db.annotation.ColumnExtension;
import com.db.annotation.TableExtension;
import lombok.Getter;
import lombok.Setter;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

/**
 * @author yuanmengfan
 * @date 2022/7/9 17:26
 * @description
 */

@Getter
@Setter
@TableExtension(name =  "t_testModel",remark = "测试表")
public class TestModel<T> {
    @ColumnExtension(isId = true,length = 255)
    private String id;
    @ColumnExtension(columnName = "userName",defaultValue = "name1", remark = "姓名")
    private String name;
    @ColumnExtension(remark = "年龄", length = 10)
    private int age;
    @ColumnExtension(defaultValue = DefaultValueConstants.NULL, remark = "工资")
    private Double salary;
    @ColumnExtension(remark = "生日")
    private Date birth;
    @ColumnExtension(defaultValue=DefaultValueConstants.TRUE,remark = "是否禁用")
    private boolean disabled;

    private LocalDate localDate;

    private LocalDateTime localDateTime;

    private LocalTime localTime;

//    private List<YFSYQKZXJCZFXXList> list;

    public static void main(String[] args) throws SQLException {
        String db_setting = "db/db.setting";
        Setting setting = new Setting(db_setting, CharsetUtil.CHARSET_UTF_8, true);
        DataSource group_test = DSFactory.create(setting).getDataSource("group_test");
//        MySQLSqlBuild mySQLBaseSqlBuild = new MySQLSqlBuild(new ExtensionFieldBuilder());
//        String tableSql = mySQLBaseSqlBuild.buildTableSql(TestModel.class);
//        System.out.println(tableSql);
//        Db.use(group_test).execute(tableSql,null);
//
//        TestModel testModel = new TestModel();
//        testModel.setAge(1);
//        testModel.setId(UUID.randomUUID().toString());
//        testModel.setBirth(new Date());
//        testModel.setSalary(123124124123D);
//        testModel.setDisabled(false);
//        testModel.setLocalDate(LocalDate.now());
//        testModel.setLocalDateTime(LocalDateTime.now());
//        testModel.setLocalTime(LocalTime.now());
//
//        YFSYQKZXJCZFXXList yfsyqkzxjczfxxList = new YFSYQKZXJCZFXXList();
//        yfsyqkzxjczfxxList.setYFGPMC("11111");
//        YFSYQKZXJCZFXXListSYRList yfsyqkzxjczfxxListSYRList = new YFSYQKZXJCZFXXListSYRList();
//        yfsyqkzxjczfxxListSYRList.setSYRRSBH("11111");
//        yfsyqkzxjczfxxList.setYfsyqkzxjczfxxListSYRList(Arrays.asList(yfsyqkzxjczfxxListSYRList, yfsyqkzxjczfxxListSYRList));
//        testModel.setList(Arrays.asList(yfsyqkzxjczfxxList, yfsyqkzxjczfxxList, yfsyqkzxjczfxxList, yfsyqkzxjczfxxList));
//
//        System.out.println(dbContext.addExtensionList(testModel));
//        System.out.println(dbContext.getTableMeta("test", "test1"));
        Double a = null;
    }
}
