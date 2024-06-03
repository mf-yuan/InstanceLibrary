package com;

import cn.hutool.http.HttpGlobalConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author yuanmengfan
 * @date 2021/11/18 4:38 下午
 * @description
 */
@SpringBootApplication
// 添加starter后必须需要一个DateSource的bean或者就会报错
@EnableTransactionManagement
public class Application {
    public static void main(String[] args) {
        HttpGlobalConfig.setTimeout(1000 * 60 * 5);
        SpringApplication.run(Application.class, args);
    }
}
