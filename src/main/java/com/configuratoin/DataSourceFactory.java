package com.configuratoin;

import com.util.DaoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author yuanmengfan
 * @date 2023/6/17 16:13
 * @description 数据源工厂
 */
@Configuration
public class DataSourceFactory {

    @Autowired
    private DaoUtil daoUtil;

    @Bean
    public DataSource testDataSource(){
        return daoUtil.getDao("group_test");
    }
}
