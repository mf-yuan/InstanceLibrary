package com.model;

public enum DateFormats{

    //时间格式
    DEFAULT_STANDARD_DATE_PATTERN("yyyy-MM-dd HH:mm:ss"),
    DEFAULT_STANDARD_DATE_PATTERN2("yyyy-MM-dd HH:mm"),
    DEFAULT_SHORT_DATE_PATTERN("yyyy-MM-dd"),
    DEFAULT_STRING_DATE_PATTERN("yyyyMMdd"),
    DEFAULT_STRING_DATETIME_PATTERN("HH:mm:ss"),
    DEFAULT_TIME_PATTERN("HH:mm"),
    DEFAULT_YEAR_PATTERN("yyyy-MM"),
    NYR_PATTERN("yyyy年MM月dd日"),
    NY_PATTERN("yyyy年MM月"),
    YR_PATTERN("MM月dd日");

    public String format;
    DateFormats(String format) {
        this.format = format;
    }

    public String getFormat() {
        return format;
    }
}