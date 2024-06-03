package com.model;

/**
 * @author yuanmengfan
 * @date 2021/10/28 11:59 上午
 * @description
 */
public enum Charsets {
    //常用字符集
    UTF_8("UTF-8"),
    UTF_16("UTF-16"),
    UTF_32("UTF-32"),
    GBK("GBK"),
    GB2312("GB2312"),
    ISO8859_1("ISO8859-1"),
    UNICODE("unicode");

    public String charset;
    Charsets(String charset) {
        this.charset = charset;
    }

    public String getCharset() {
        return charset;
    }
}
