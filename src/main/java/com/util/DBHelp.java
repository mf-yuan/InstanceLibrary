package com.util;

import cn.hutool.db.Db;
import cn.hutool.db.Session;

import java.sql.SQLException;

/**
 * @author yuanmengfan
 * @date 2021/10/13 6:27 下午
 * @description
 */
public class DBHelp {
    public Db db;
    public Session session;

    public void beginTransaction() throws SQLException {
        session.beginTransaction();
    }

    public void close(){
        session.close();
    }
    public void commit() throws SQLException {
        session.commit();
    }

    public void rollback()  {
        try {
            session.rollback();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
