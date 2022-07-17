package com.db;

import java.util.Date;

/**
 * @author yuanmengfan
 * @date 2022/7/17 16:26
 * @description
 */
public class CommonModel {
    @TableExtension(isId = true)
    private String id;

    private String pid;

    private Date createDate;

    private Date updateDate;
}
