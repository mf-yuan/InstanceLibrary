package com.dao;

import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.db.Session;
import cn.hutool.db.handler.EntityListHandler;
import com.util.DBHelp;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

/**
 * @author yuanmengfan
 * @date 2021/10/13 5:59 下午
 * @description
 */
public class TestDao extends DBHelp {

    public List<Entity>  queryEntityListNoParam(String sql) throws SQLException {
        return db.query(sql,new EntityListHandler(),new Object[0]);
    }

    public int executeNotQuery(String sql,Object... params) throws SQLException {
        return db.execute(sql,params);
    }


}
