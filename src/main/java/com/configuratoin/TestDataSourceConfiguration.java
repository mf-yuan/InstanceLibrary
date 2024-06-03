package com.configuratoin;

import cn.hutool.db.Db;
import cn.hutool.db.Session;
import com.db.DbContext;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

/**
 * @author yuanmengfan
 * @date 2021/12/27 11:07 下午
 * @description
 */
@Repository
public class TestDataSourceConfiguration extends DbContext {

    public TestDataSourceConfiguration(DataSource testDataSource){
        this.db = Db.use(testDataSource);
        this.session = Session.create(testDataSource);
    }
}
